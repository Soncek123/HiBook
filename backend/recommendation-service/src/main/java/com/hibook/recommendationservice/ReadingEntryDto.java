package com.hibook.recommendationservice;

public class ReadingEntryDto {
    private Long id;
    private Long bookId;
    private String status;
    private Integer progressPercent;
    private Integer rating;
    private String review;

    public Long getId() {
        return id;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getStatus() {
        return status;
    }

    public Integer getProgressPercent() {
        return progressPercent;
    }

    public Integer getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }
}
