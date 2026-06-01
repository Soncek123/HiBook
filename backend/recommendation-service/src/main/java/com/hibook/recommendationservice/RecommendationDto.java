package com.hibook.recommendationservice;

public class RecommendationDto {
    private Long bookId;
    private String title;
    private String author;
    private String genre;
    private String reason;

    public RecommendationDto(Long bookId, String title, String author, String genre, String reason) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.reason = reason;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public String getReason() {
        return reason;
    }
}
