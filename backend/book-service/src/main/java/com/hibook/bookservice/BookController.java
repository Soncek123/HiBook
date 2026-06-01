package com.hibook.bookservice;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "*")
public class BookController {
    private final BookRepository repository;
    private final GoogleBooksService googleBooksService;

    public BookController(BookRepository repository, GoogleBooksService googleBooksService) {
        this.repository = repository;
        this.googleBooksService = googleBooksService;
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return repository.save(book);
    }

    @GetMapping("/{id}")
    public Book getBook(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @GetMapping("/search-google")
    public List<GoogleBookResult> searchGoogleBooks(@RequestParam String query) {
        return googleBooksService.searchBooks(query);
    }
}
