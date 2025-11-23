package searchengine;

import java.util.*;

/**
 * Handles "advanced" queries:
 *
 * Supported syntax:
 *   - simple terms: java search engine
 *       => treated like: java AND search AND engine
 *
 *   - Boolean operators: AND, OR, NOT (case-insensitive)
 *       Example: java AND search NOT python
 *
 *   - Exact phrase: "java search"
 *       => matches documents whose text contains the phrase between quotes
 *
 * Precedence rules (simple version):
 *   - NOT has highest precedence (applies to the next term or phrase)
 *   - AND / OR are evaluated left-to-right
 */

public class BooleanQueryProcessor {
	
	/**
     * Evaluate a raw query string into a set of documents.
     *
     * @param rawQuery the user's query
     * @param allDocs  all documents in the collection
     * @param index    inverted index for fast term lookup
     */
    public static Set<Document> evaluate(String rawQuery,
                                         List<Document> allDocs,
                                         InvertedIndex index) {
        // 1) Parse into tokens where quoted phrases are kept together
        List<String> tokens = tokenizeQuery(rawQuery);

        if (tokens.isEmpty()) {
            return new HashSet<>();
        }

        // 2) Evaluate with simple boolean logic
        Set<Document> result = null;
        String pendingOp = null; // "AND" or "OR"
        boolean negateNext = false;

        for (String token : tokens) {
            String upper = token.toUpperCase();

            // Handle operators first
            if ("AND".equals(upper) || "OR".equals(upper)) {
                pendingOp = upper;
                continue;
            }
            if ("NOT".equals(upper)) {
                negateNext = true;
                continue;
            }

            // token is now either a term or a phrase
            Set<Document> termDocs = getDocsForToken(token, allDocs, index);

            // If we had a NOT before this term, take the complement
            if (negateNext) {
                Set<Document> complement = new HashSet<>(allDocs);
                complement.removeAll(termDocs);
                termDocs = complement;
                negateNext = false;
            }

            if (result == null) {
                // First operand
                result = new HashSet<>(termDocs);
            } else {
                // Combine with previous result using pending operator
                if ("OR".equals(pendingOp)) {
                    result.addAll(termDocs);
                } else {
                    // Default AND if no operator given
                    result.retainAll(termDocs);
                }
            }
        }

        if (result == null) {
            // If query was something weird like only operators, just return empty
            return new HashSet<>();
        }

        return result;
    }

    
    /**
     * Tokenize a query string, keeping phrases in quotes as a single token.
     * Example:
     *   raw:  java AND "search engine" NOT python
     *   tokens: [java, AND, search engine, NOT, python]
     */
    private static List<String> tokenizeQuery(String rawQuery) {
        List<String> tokens = new ArrayList<>();

        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < rawQuery.length(); i++) {
            char c = rawQuery.charAt(i);

            if (c == '"') {
                // Toggle quote mode
                inQuotes = !inQuotes;
                if (!inQuotes) {
                    // Just finished a phrase
                    if (current.length() > 0) {
                        tokens.add(current.toString());
                        current.setLength(0);
                    }
                }
                continue;
            }

            if (inQuotes) {
                // Inside quotes, keep everything (including spaces)
                current.append(c);
            } else {
                // Outside quotes, split on spaces
                if (Character.isWhitespace(c)) {
                    if (current.length() > 0) {
                        tokens.add(current.toString());
                        current.setLength(0);
                    }
                } else {
                    current.append(c);
                }
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens;
    }
    
    
    /**
     * Get all documents that match a single token.
     * If the token contains spaces, we treat it as a phrase.
     * Otherwise, we treat it as a single term and use the inverted index.
     */
    private static Set<Document> getDocsForToken(String token,
                                                 List<Document> allDocs,
                                                 InvertedIndex index) {
        Set<Document> result = new HashSet<>();

        if (token.contains(" ")) {
            // Phrase search: scan all documents and check if they contain the phrase
            String phraseLower = token.toLowerCase();
            for (Document doc : allDocs) {
                if (doc.containsPhrase(phraseLower)) {
                    result.add(doc);
                }
            }
        } else {
            // Single term: use inverted index
            result.addAll(index.getDocumentsForTerm(token));
        }

        return result;
    }

}
