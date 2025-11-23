package searchengine;

import java.util.*;

/**
 * High-level search engine:
 *   - holds documents
 *   - builds an inverted index
 *   - takes user queries, uses BooleanQueryProcessor
 *   - scores and sorts results
 */

public class SimpleSearchEngine {
	
	private final List<Document> documents;
    private final InvertedIndex index;
    
    
    public SimpleSearchEngine(List<Document> documents) {
        this.documents = new ArrayList<>(documents);
        this.index = new InvertedIndex();

        // Build the index once
        for (Document doc : this.documents) {
            index.addDocument(doc);
        }
    }
    
    
    /**
     * Basic search using full "advanced" query syntax.
     * Relevance only.
     */
    public List<SearchResult> search(String query) {
        return search(query, false);
    }
    
    
    /**
     * Search with option to sort by date secondary.
     *
     * @param query      user query (supports AND/OR/NOT, phrases)
     * @param sortByDate if true, results with same score are ordered by date (newest first)
     */
    public List<SearchResult> search(String query, boolean sortByDate) {
        // 1) Use BooleanQueryProcessor to get matching documents
        Set<Document> matchedDocs = BooleanQueryProcessor.evaluate(query, documents, index);

        // 2) Compute scores for each matched document
        List<SearchResult> results = new ArrayList<>();

        for (Document doc : matchedDocs) {
            double score = computeScore(doc, query);
            String snippetQuery = extractSnippetQueryTerm(query);
            String snippet = doc.createSnippet(snippetQuery, 120);
            results.add(new SearchResult(doc, score, snippet));
        }

        // 3) Sort by score (and date optionally)
        if (sortByDate) {
            results.sort((r1, r2) -> {
                int cmp = Double.compare(r2.getScore(), r1.getScore());
                if (cmp != 0) {
                    return cmp;
                }
                // If scores equal, compare by date (newer first)
                return r2.getDocument().getDate().compareTo(r1.getDocument().getDate());
            });
        } else {
            Collections.sort(results); // uses compareTo (by score)
        }

        return results;
    }
    
    
    /**
     * Compute a very simple relevance score:
     *   - Count how many query terms appear in the document.
     *   - If document matches any phrase exactly, add a small bonus.
     *
     * This is NOT real TF-IDF, just a simple scoring for practice.
     */
    private double computeScore(Document doc, String rawQuery) {
        String lowerQuery = rawQuery.toLowerCase();

        // Extract terms (ignore operators AND/OR/NOT, ignore quotes)
        Set<String> terms = new HashSet<>();
        String[] parts = lowerQuery.replace("\"", "").split("[^a-z0-9]+");
        for (String p : parts) {
            if (p.isEmpty()) {
                continue;
            }
            if (p.equals("and") || p.equals("or") || p.equals("not")) {
                continue;
            }
            terms.add(p);
        }

        double score = 0.0;

        for (String term : terms) {
            if (doc.containsTerm(term)) {
                score += 1.0;
            }
        }

        // Bonus if the document contains any phrase from the query
        List<String> phrases = extractPhrases(rawQuery);
        for (String phrase : phrases) {
            if (doc.containsPhrase(phrase.toLowerCase())) {
                score += 2.0; // phrase match bonus
            }
        }

        return score;
    }

    
    /**
     * Extract phrases that were inside quotes.
     * Example: java "search engine" NOT python -> ["search engine"]
     */
    private List<String> extractPhrases(String rawQuery) {
        List<String> phrases = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < rawQuery.length(); i++) {
            char c = rawQuery.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                if (!inQuotes) {
                    // Phrase ended
                    if (current.length() > 0) {
                        phrases.add(current.toString());
                        current.setLength(0);
                    }
                }
            } else if (inQuotes) {
                current.append(c);
            }
        }

        return phrases;
    }

    
    /**
     * Extract something decent to use as the snippet search term:
     *   - If there's a phrase, use the first phrase
     *   - Else, use the first non-operator term
     */
    private String extractSnippetQueryTerm(String rawQuery) {
        List<String> phrases = extractPhrases(rawQuery);
        if (!phrases.isEmpty()) {
            return phrases.get(0).toLowerCase();
        }

        String[] parts = rawQuery.toLowerCase().replace("\"", "").split("[^a-z0-9]+");
        for (String p : parts) {
            if (p.isEmpty()) {
                continue;
            }
            if (p.equals("and") || p.equals("or") || p.equals("not")) {
                continue;
            }
            return p;
        }

        // fallback
        return rawQuery.toLowerCase().trim();
    }
    
    public List<Document> getDocuments() {
        return Collections.unmodifiableList(documents);
    }

}
