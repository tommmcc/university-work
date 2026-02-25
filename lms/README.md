# Library Management System (Java Swing + SQLite)

A desktop GUI application for managing books, borrowers, and loans.

Originally developed during my Diploma studies (2023) and later refactored (2026) to modernize architecture, improve UI structure, and introduce SQLite persistence.

---

## Features

- Add / remove books
- Add / remove borrowers
- Checkout & return books
- Loan duration tracking
- Search & filter tables
- Book availability enforcement
- SQLite persistence (save & load)
- Standardized date format: **DD-MM-YYYY**

---

## UI Evolution (2023 → 2026 Refactor)

Below is a visual comparison of the original version and the refactored version.

### 🏠 Home Screen

| Before (2023) | After (2026 Refactor) |
|---|---|
| <img src="images/lmsv1.JPG" height="260" /> | <img src="images/lmsv2.JPG" height="260" /> |>

---

### 📚 Books Panel

| Before (2023) | After (2026 Refactor) |
|---|---|
| <img src="images/lmsbooksv1.JPG" height="260" /> | <img src="images/lmsbooksv2.JPG" height="260" /> |

---

### 👤 Borrowers Panel

| Before (2023) | After (2026 Refactor) |
|---|---|
| <img src="images/lmsborrowersv1.JPG" height="260" /> | <img src="images/lmsborrowersv2.JPG" height="260" /> |

---

### 🔄 Loans Panel

| Before (2023) | After (2026 Refactor) |
|---|---|
| <img src="images/lmsloansv1.JPG" height="260" /> | <img src="images/lmsloansv2.JPG" height="260" /> |

---

### 🛒 Checkout Panel

| Before (2023) | After (2026 Refactor) |
|---|---|
| <img src="images/lmscheckoutv1.JPG" height="260" /> | <img src="images/lmscheckoutv2.JPG" height="260" /> |

---

## Architecture

Layered design:

UI (Swing Panels)↓

Service Layer (Business Logic)↓

Persistence Layer (SQLite)


### Structure

- `model/` – Book, Borrower, Loan
- `ui/` – Swing panels & main window
- `LibraryService.java` – Core business logic
- `LibraryDB.java` – SQLite integration
- `Library.java` – Entry point

---

## Concepts Demonstrated

- Classes & Objects (OOP)
- Encapsulation (private fields + getters/setters)
- Separation of concerns
- Event-driven programming (Swing)
- JTable models & sorting
- JDBC + SQLite integration
- `LocalDate` + `DateTimeFormatter`
- Gradle build configuration

---

## How to Run

1. Open the `lms` folder.
2. Run:

```bash
gradlew.bat run
