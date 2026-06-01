package com.hibook.bookservice;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
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
        } catch (Exception e) {
            System.out.println("Google Books API error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private String getString(Map map, String key) {
        Object value = map.get(key);
        return value == null ? "" : String.valueOf(value);
    }
}
