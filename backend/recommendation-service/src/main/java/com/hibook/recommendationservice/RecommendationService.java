package com.hibook.recommendationservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RecommendationService {
    private final RestClient restClient;
    private final String bookServiceUrl;
    private final String readingServiceUrl;

    public RecommendationService(
            RestClient.Builder builder,
            @Value("${book.service.url}") String bookServiceUrl,
            @Value("${reading.service.url}") String readingServiceUrl
    ) {
        this.restClient = builder.build();
        this.bookServiceUrl = bookServiceUrl;
        this.readingServiceUrl = readingServiceUrl;
    }

    public List<RecommendationDto> getRecommendations() {
        List<BookDto> books = fetchBooks();
        List<ReadingEntryDto> readingEntries = fetchReadingEntries();

        Set<Long> readingBookIds = new HashSet<>();
        Set<String> preferredGenres = new HashSet<>();

        for (ReadingEntryDto entry : readingEntries) {
            if (entry.getBookId() != null) {
                readingBookIds.add(entry.getBookId());
            }

            boolean likedBook = entry.getRating() != null && entry.getRating() >= 4;
            boolean finishedBook = "Read".equalsIgnoreCase(entry.getStatus());

            if (likedBook || finishedBook) {
                BookDto matchingBook = findBookById(books, entry.getBookId());
                if (matchingBook != null && matchingBook.getGenre() != null) {
                    preferredGenres.add(matchingBook.getGenre().toLowerCase());
                }
            }
        }

        List<RecommendationDto> recommendations = new ArrayList<>();

        for (BookDto book : books) {
            if (book.getId() == null || readingBookIds.contains(book.getId())) {
                continue;
            }

            String genre = book.getGenre() == null ? "" : book.getGenre().toLowerCase();

            if (!preferredGenres.isEmpty() && preferredGenres.contains(genre)) {
                recommendations.add(new RecommendationDto(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getGenre(),
                        "Recommended because you liked or finished books in the " + book.getGenre() + " genre."
                ));
            }
        }

        if (recommendations.isEmpty()) {
            for (BookDto book : books) {
                if (book.getId() != null && !readingBookIds.contains(book.getId())) {
                    recommendations.add(new RecommendationDto(
                            book.getId(),
                            book.getTitle(),
                            book.getAuthor(),
                            book.getGenre(),
                            "Trending recommendation from available books."
                    ));
                }

                if (recommendations.size() >= 5) {
                    break;
                }
            }
        }

        return recommendations;
    }

    private List<BookDto> fetchBooks() {
        try {
            return restClient.get()
                    .uri(bookServiceUrl + "/books")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<BookDto>>() {});
        } catch (Exception e) {
            System.out.println("Could not fetch books: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<ReadingEntryDto> fetchReadingEntries() {
        try {
            return restClient.get()
                    .uri(readingServiceUrl + "/reading")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ReadingEntryDto>>() {});
        } catch (Exception e) {
            System.out.println("Could not fetch reading entries: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private BookDto findBookById(List<BookDto> books, Long id) {
        if (id == null) {
            return null;
        }

        for (BookDto book : books) {
            if (id.equals(book.getId())) {
                return book;
            }
        }

        return null;
    }
}
