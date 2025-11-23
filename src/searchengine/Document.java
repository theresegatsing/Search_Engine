package searchengine;

import java.time.LocalDate;

/**
 * Represents a single document that can be searched.
 * Each document has:
 *   - an id (int)
 *   - a title
 *   - full text content
 *   - a date (for sorting by date)
 */

public class Document {
	
	 private final int id;
	 private final String title;
	 private final String content;
	 private final LocalDate date;

	 // Cached lowercase content for easier case-insensitive matching
	 private final String contentLower;
	 
	 
	 public Document(int id, String title, String content, LocalDate date) {
	        this.id = id;
	        this.title = title;
	        this.content = content;
	        this.date = date;
	        this.contentLower = content.toLowerCase();
	 }

	    public int getId() {
	        return id;
	    }

	    public String getTitle() {
	        return title;
	    }

	    public String getContent() {
	        return content;
	    }

	    public LocalDate getDate() {
	        return date;
	    }

	    public String getContentLower() {
	        return contentLower;
	    }

	    
	    /**
	     * Returns true if this document contains the given term (already lowercase).
	     * This is a very simple check: it does substring search, not full word-boundary matching.
	     */
	    public boolean containsTerm(String termLower) {
	        return contentLower.contains(termLower);
	    }
	    
	    /**
	     * Returns true if this document contains the given phrase (already lowercase).
	     */
	    public boolean containsPhrase(String phraseLower) {
	        return contentLower.contains(phraseLower);
	    }
	    
	    
	    /**
	     * Create a short snippet that shows where the query appears in the document.
	     * If the query is not found, returns the beginning of the document.
	     */
	    public String createSnippet(String queryLower, int maxLength) {
	        String text = content;
	        String lower = contentLower;

	        int index = lower.indexOf(queryLower);
	        if (index < 0) {
	            // If we can't find it, just return the beginning
	            if (text.length() <= maxLength) {
	                return text;
	            }
	            return text.substring(0, maxLength) + "...";
	        }

	        // Try to center the snippet around the match
	        int start = Math.max(0, index - 30);
	        int end = Math.min(text.length(), index + queryLower.length() + 70);

	        String snippet = text.substring(start, end);
	        if (start > 0) {
	            snippet = "..." + snippet;
	        }
	        if (end < text.length()) {
	            snippet = snippet + "...";
	        }
	        return snippet;
	    }

	    
	    @Override
	    public String toString() {
	        return "Document{id=" + id + ", title='" + title + "', date=" + date + "}";
	    }
	        
}
