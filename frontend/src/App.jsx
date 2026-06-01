import { useEffect, useState } from "react";
import "./App.css";

function App() {
  const [page, setPage] = useState("books");
  const [books, setBooks] = useState([]);
  const [reading, setReading] = useState([]);
  const [progressInputs, setProgressInputs] = useState({});

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

    const inputs = {};
    data.forEach((entry) => {
      inputs[entry.id] = entry.progressPercent;
    });
    setProgressInputs(inputs);
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
        progressPercent: 0
      })
    });

    loadReading();
    setPage("reading");
  };

  const updateProgress = async (entry) => {
    const newPercent = Number(progressInputs[entry.id]);

    if (newPercent < 0 || newPercent > 100) {
      alert("Progress must be between 0 and 100.");
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
        progressPercent: newPercent
      })
    });

    loadReading();
  };

  return (
    <div className="app">
      <header>
        <h1>HiBook</h1>
        <p>Track books and reading progress</p>
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
                <li key={entry.id}>
                  <div>
                    <strong>{book ? book.title : `Book ID ${entry.bookId}`}</strong>
                    <br />
                    <span>{entry.status} · {entry.progressPercent}%</span>
                  </div>

                  <div className="progress-editor">
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
                    <button onClick={() => updateProgress(entry)}>
                      Update
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
