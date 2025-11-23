package searchengine;

import java.util.*;

/**
 * Inverted index maps terms -> documents that contain those terms.
 *
 * For example:
 *   "java" -> {doc1, doc3}
 *   "search" -> {doc2}
 */

public class InvertedIndex {
	
	// term (lowercase) -> set of documents that contain that term
    private final Map<String, Set<Document>> index = new HashMap<>();


}
