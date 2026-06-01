package com.hibook.readingservice;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reading")
@CrossOrigin(origins = "*")
public class ReadingController {
    private final ReadingRepository repository;

    public ReadingController(ReadingRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ReadingEntry> getAllReadingEntries() {
        return repository.findAll();
    }

    @PostMapping
    public ReadingEntry createReadingEntry(@RequestBody ReadingEntry entry) {
        if (entry.getRating() == null) {
            entry.setRating(0);
        }

        if (entry.getReview() == null) {
            entry.setReview("");
        }

        return repository.save(entry);
    }

    @PutMapping("/{id}")
    public ReadingEntry updateReadingEntry(@PathVariable Long id, @RequestBody ReadingEntry updated) {
        ReadingEntry entry = repository.findById(id).orElseThrow();

        entry.setBookId(updated.getBookId());
        entry.setStatus(updated.getStatus());
        entry.setProgressPercent(updated.getProgressPercent());
        entry.setRating(updated.getRating());
        entry.setReview(updated.getReview());

        return repository.save(entry);
    }

    @DeleteMapping("/{id}")
    public void deleteReadingEntry(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
