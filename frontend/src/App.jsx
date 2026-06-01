import { useEffect, useState } from "react";
import "./App.css";

function App() {
  const [page, setPage] = useState("books");
  const [books, setBooks] = useState([]);
  const [reading, setReading] = useState([]);
  const [progressInputs, setProgressInputs] = useState({});
  const [ratingInputs, setRatingInputs] = useState({});
  const [reviewInputs, setReviewInputs] = useState({});

  const [newBook, setNewBook] = useState({
    title: "",
    author: "",
    genre: ""
  });

  const loadBooks = async () => {
    const response = await fetch("http://localhost:8081/books");
    const data = await response.json();
    setBooks(data);
  };

  const loadReading = async () => {
    const response = await fetch("http://localhost:8082/reading");
    const data = await response.json();
    setReading(data);

    const progress = {};
    const ratings = {};
    const reviews = {};

    data.forEach((entry) => {
      progress[entry.id] = entry.progressPercent ?? 0;
      ratings[entry.id] = entry.rating ?? 0;
      reviews[entry.id] = entry.review ?? "";
    });

    setProgressInputs(progress);
    setRatingInputs(ratings);
    setReviewInputs(reviews);
  };

  useEffect(() => {
    loadBooks();
    loadReading();
  }, []);

  const addBook = async () => {
    if (!newBook.title || !newBook.author || !newBook.genre) {
      alert("Please fill in title, author, and genre.");
      return;
    }

    await fetch("http://localhost:8081/books", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(newBook)
    });

    setNewBook({ title: "", author: "", genre: "" });
    loadBooks();
  };

  const addToReading = async (bookId) => {
    await fetch("http://localhost:8082/reading", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        bookId,
        status: "Want to Read",
        progressPercent: 0,
        rating: 0,
        review: ""
      })
    });

    loadReading();
    setPage("reading");
  };

  const updateReadingEntry = async (entry) => {
    const newPercent = Number(progressInputs[entry.id]);
    const newRating = Number(ratingInputs[entry.id]);
    const newReview = reviewInputs[entry.id] ?? "";

    if (newPercent < 0 || newPercent > 100) {
      alert("Progress must be between 0 and 100.");
      return;
    }

    if (newRating < 0 || newRating > 5) {
      alert("Rating must be between 0 and 5.");
      return;
    }

    const newStatus = newPercent === 100 ? "Read" : "Currently Reading";

    await fetch(`http://localhost:8082/reading/${entry.id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        bookId: entry.bookId,
        status: newStatus,
        progressPercent: newPercent,
        rating: newRating,
        review: newReview
      })
    });

    loadReading();
  };

  const renderStars = (rating) => {
    const fullStars = "★".repeat(rating || 0);
    const emptyStars = "☆".repeat(5 - (rating || 0));
    return fullStars + emptyStars;
  };

  return (
    <div className="app">
      <header>
        <h1>HiBook</h1>
        <p>Track books, reading progress, ratings, and reviews</p>
      </header>

      <nav>
        <button onClick={() => setPage("books")}>Books</button>
        <button onClick={() => setPage("reading")}>My Reading</button>
      </nav>

      {page === "books" && (
        <section className="card">
          <h2>Books</h2>

          <div className="form">
            <input
              placeholder="Title"
              value={newBook.title}
              onChange={(e) => setNewBook({ ...newBook, title: e.target.value })}
            />

            <input
              placeholder="Author"
              value={newBook.author}
              onChange={(e) => setNewBook({ ...newBook, author: e.target.value })}
            />

            <input
              placeholder="Genre"
              value={newBook.genre}
              onChange={(e) => setNewBook({ ...newBook, genre: e.target.value })}
            />

            <button onClick={addBook}>Add Book</button>
          </div>

          <ul>
            {books.map((book) => (
              <li key={book.id}>
                <div>
                  <strong>{book.title}</strong>
                  <br />
                  <span>{book.author} · {book.genre}</span>
                </div>

                <button onClick={() => addToReading(book.id)}>
                  Add to Reading
                </button>
              </li>
            ))}
          </ul>
        </section>
      )}

      {page === "reading" && (
        <section className="card">
          <h2>My Reading</h2>

          <ul>
            {reading.map((entry) => {
              const book = books.find((b) => b.id === entry.bookId);

              return (
                <li key={entry.id} className="reading-item">
                  <div className="reading-info">
                    <strong>{book ? book.title : `Book ID ${entry.bookId}`}</strong>
                    <br />
                    <span>{entry.status} · {entry.progressPercent}%</span>
                    <br />
                    <span className="stars">{renderStars(entry.rating)}</span>
                    {entry.review && (
                      <p className="review-text">"{entry.review}"</p>
                    )}
                  </div>

                  <div className="reading-editor">
                    <label>
                      Progress %
                      <input
                        type="number"
                        min="0"
                        max="100"
                        value={progressInputs[entry.id] ?? ""}
                        onChange={(e) =>
                          setProgressInputs({
                            ...progressInputs,
                            [entry.id]: e.target.value
                          })
                        }
                      />
                    </label>

                    <label>
                      Rating
                      <select
                        value={ratingInputs[entry.id] ?? 0}
                        onChange={(e) =>
                          setRatingInputs({
                            ...ratingInputs,
                            [entry.id]: e.target.value
                          })
                        }
                      >
                        <option value="0">No rating</option>
                        <option value="1">1 star</option>
                        <option value="2">2 stars</option>
                        <option value="3">3 stars</option>
                        <option value="4">4 stars</option>
                        <option value="5">5 stars</option>
                      </select>
                    </label>

                    <label>
                      Review
                      <textarea
                        placeholder="Write your thoughts..."
                        value={reviewInputs[entry.id] ?? ""}
                        onChange={(e) =>
                          setReviewInputs({
                            ...reviewInputs,
                            [entry.id]: e.target.value
                          })
                        }
                      />
                    </label>

                    <button onClick={() => updateReadingEntry(entry)}>
                      Save
                    </button>
                  </div>
                </li>
              );
            })}
          </ul>
        </section>
      )}
    </div>
  );
}

export default App;
