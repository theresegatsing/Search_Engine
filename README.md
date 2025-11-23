# Simple Search Engine 

A small **console-based** search engine written in Java.  
It indexes a fixed set of documents and lets you search them using the following:

- Keywords  
- Exact phrases (`"search engine"`)  
- Boolean operators (`AND`, `OR`, `NOT`)  
- Optional sorting by **relevance** and then by **date**

---

## Features

- **Keyword search** (case-insensitive)
- **Advanced search**:
  - `"exact phrase"`
  - `AND`, `OR`, `NOT`
- **Ranking**:
  - Simple relevance score based on term and phrase matches
- **Optional date sorting** (newest first on ties)
- **Snippets**:
  - Shows a short piece of text around where the query matched

---

## Project Structure

Package: `searchengine`

- `Document.java`  
  Represents a document with:
  - `id`, `title`, `content`, `date`
  - Methods to:
    - Check if it contains a term or phrase
    - Build a short snippet around a match

- `SearchResult.java`  
  Holds:
  - A `Document`
  - A `score` (relevance)
  - A `snippet`  
  Implements `Comparable` to sort results by score (higher first).

- `InvertedIndex.java`  
  - Maps `term -> set of documents` that contain that term.  
  - Built from document titles + contents.  
  - Used for fast lookup of documents for each keyword.

- `BooleanQueryProcessor.java`  
  - Parses and evaluates **advanced queries**:  
    - Terms (e.g. `java`, `search`)  
    - Phrases in quotes (e.g. `"search engine"`)  
    - `AND`, `OR`, `NOT`  
  - Returns a `Set<Document>` matching the query.

- `SimpleSearchEngine.java`  
  - Owns the list of documents and the `InvertedIndex`.  
  - Uses `BooleanQueryProcessor` to find matching documents.  
  - Computes a simple relevance score:
    - +1 for each query term in the document
    - + bonus for phrase matches  
  - Sorts results by score and optionally by date.

- `SearchApp.java`  
  - Has the `main` method.  
  - Creates some sample documents.  
  - Builds `SimpleSearchEngine`.  
  - Reads queries from the console and prints:
    - Title, ID, date, score
    - Snippet

---

## Setup 

1. Create a new **Java Project**: `SimpleSearchEngine`.
2. Create package: `searchengine`.
3. Add the six classes above into that package, using the code provided.
4. Right-click `SearchApp.java` → **Run As → Java Application**.

---

## Using the Program

When it runs, type queries in the console, for example:

- `java`
- `search engine`
- `"search engine"`
- `java AND search`
- `"search engine" OR python`
- `search AND techniques NOT python`

Then answer:

- `Sort by date as well? (y/n)`

The program will print the list of matching documents with scores and snippets.

---

## Notes / Simplifications

- Matching is case-insensitive.
- Tokenization and scoring are **very simple** (good for learning, not production).
- Documents are hard-coded in `SearchApp.createSampleDocuments()`; you can replace them with your own.






