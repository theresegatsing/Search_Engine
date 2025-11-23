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

}
