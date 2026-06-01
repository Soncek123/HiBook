package com.hibook.recommendationservice;

public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String genre;

    public Long getId() {
        return id;
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
}
