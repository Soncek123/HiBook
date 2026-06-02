package com.hibook.bookservice;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GoogleBooksService {
    private final RestClient restClient;

    public GoogleBooksService(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public List<GoogleBookResult> searchBooks(String query) {
        try {
            URI uri = UriComponentsBuilder
                    .fromUriString("https://www.googleapis.com/books/v1/volumes")
                    .queryParam("q", query)
                    .queryParam("maxResults", 10)
                    .build()
                    .encode()
                    .toUri();

            Map response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(Map.class);

            List<GoogleBookResult> parsed = parseGoogleBooksResponse(response);

            if (parsed.isEmpty()) {
                return fallbackResults(query);
            }

            return parsed;

        } catch (RestClientResponseException e) {
            System.out.println("Google Books API HTTP error: " + e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
            return fallbackResults(query);
        } catch (Exception e) {
            System.out.println("Google Books API error: " + e.getMessage());
            return fallbackResults(query);
        }
    }

    private List<GoogleBookResult> parseGoogleBooksResponse(Map response) {
        List<GoogleBookResult> results = new ArrayList<>();

        if (response == null || response.get("items") == null) {
            return results;
        }

        List items = (List) response.get("items");

        for (Object itemObject : items) {
            Map item = (Map) itemObject;
            Map volumeInfo = (Map) item.get("volumeInfo");

            if (volumeInfo == null) {
                continue;
            }

            String title = getString(volumeInfo, "title");
            String description = getString(volumeInfo, "description");
            String publishedDate = getString(volumeInfo, "publishedDate");

            String author = "Unknown author";
            Object authorsObject = volumeInfo.get("authors");
            if (authorsObject instanceof List authors && !authors.isEmpty()) {
                author = String.valueOf(authors.get(0));
            }

            String genre = "Unknown genre";
            Object categoriesObject = volumeInfo.get("categories");
            if (categoriesObject instanceof List categories && !categories.isEmpty()) {
                genre = String.valueOf(categories.get(0));
            }

            String thumbnail = "";
            Object imageLinksObject = volumeInfo.get("imageLinks");
            if (imageLinksObject instanceof Map imageLinks && imageLinks.get("thumbnail") != null) {
                thumbnail = String.valueOf(imageLinks.get("thumbnail"));
            }

            results.add(new GoogleBookResult(
                    title,
                    author,
                    genre,
                    description,
                    publishedDate,
                    thumbnail
            ));
        }

        return results;
    }

    private List<GoogleBookResult> fallbackResults(String query) {
        List<GoogleBookResult> results = new ArrayList<>();
        String lowerQuery = query == null ? "" : query.toLowerCase();

        if (lowerQuery.contains("crime")) {
            results.add(new GoogleBookResult(
                    "Crime and Punishment",
                    "Fyodor Dostoevsky",
                    "Classic",
                    "Fallback result shown because the Google Books API request failed, quota was exceeded, or no results were returned.",
                    "1866",
                    ""
            ));
        } else if (lowerQuery.contains("perfume")) {
            results.add(new GoogleBookResult(
                    "Perfume: The Story of a Murderer",
                    "Patrick Süskind",
                    "Historical Fiction",
                    "Fallback result shown because the Google Books API request failed, quota was exceeded, or no results were returned.",
                    "1985",
                    ""
            ));
        } else if (lowerQuery.contains("harry")) {
            results.add(new GoogleBookResult(
                    "Harry Potter and the Philosopher's Stone",
                    "J.K. Rowling",
                    "Fantasy",
                    "Fallback result shown because the Google Books API request failed, quota was exceeded, or no results were returned.",
                    "1997",
                    ""
            ));
        } else if (lowerQuery.contains("dune")) {
            results.add(new GoogleBookResult(
                    "Dune",
                    "Frank Herbert",
                    "Science Fiction",
                    "Fallback result shown because the Google Books API request failed, quota was exceeded, or no results were returned.",
                    "1965",
                    ""
            ));
        } else if (lowerQuery.contains("hobbit")) {
            results.add(new GoogleBookResult(
                    "The Hobbit",
                    "J.R.R. Tolkien",
                    "Fantasy",
                    "Fallback result shown because the Google Books API request failed, quota was exceeded, or no results were returned.",
                    "1937",
                    ""
            ));
        } else {
            results.add(new GoogleBookResult(
                    query == null || query.isBlank() ? "Example Book Result" : query,
                    "Unknown author",
                    "Unknown genre",
                    "Fallback result shown because the Google Books API request failed, quota was exceeded, or no results were returned.",
                    "",
                    ""
            ));
        }

        return results;
    }

    private String getString(Map map, String key) {
        Object value = map.get(key);
        return value == null ? "" : String.valueOf(value);
    }
}
