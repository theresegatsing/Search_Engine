package searchengine;


/**
 * Represents the result of a search:
 *   - which document matched
 *   - the score (relevance)
 *   - a snippet to show to the user
 *
 * Implements Comparable so we can sort by score descending.
 */

public class SearchResult implements Comparable<SearchResult> {
	
	  	private final Document document;
	    private final double score;
	    private final String snippet;

	    public SearchResult(Document document, double score, String snippet) {
	        this.document = document;
	        this.score = score;
	        this.snippet = snippet;
	    }
	    
	    public Document getDocument() {
	        return document;
	    }

	    public double getScore() {
	        return score;
	    }

	    public String getSnippet() {
	        return snippet;
	    }


}
