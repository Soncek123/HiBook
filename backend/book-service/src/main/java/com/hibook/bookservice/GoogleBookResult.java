package com.hibook.bookservice;

public class GoogleBookResult {
    private String title;
    private String author;
    private String genre;
    private String description;
    private String publishedDate;
    private String thumbnail;

    public GoogleBookResult(String title, String author, String genre, String description, String publishedDate, String thumbnail) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.publishedDate = publishedDate;
        this.thumbnail = thumbnail;
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

    public String getDescription() {
        return description;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
