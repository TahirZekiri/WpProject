# TextClassifier

TextClassifier is a web application for annotating text samples with communicative metadata. You paste or type any text, then label it across three dimensions:

- **Type** — what the text *does*: Request, Proposal, Reminder, Question, Complaint, Feedback, or Other
- **Tone** — how it *sounds*: Friendly, Formal, Casual, Angry, Urgent, Sarcastic, or Neutral
- **Format** — what medium it belongs to: Email, SMS, Poetry, Formal Letter, Social Media, Chat, or Other

Every submission is stored in a database, gradually building a labeled dataset.

---

## Tech Stack

| Layer       | Technology                     |
|-------------|--------------------------------|
| Backend     | Spring Boot 4 (Java 17)        |
| Templating  | Thymeleaf + Bootstrap 5        |
| Database    | PostgreSQL via Docker / H2 dev |
| Build       | Maven (wrapper included)       |

---

## Getting Started

### Prerequisites

- **Java 17+**
- **Maven** — or use the included `./mvnw` wrapper (no installation needed)
- **Docker & Docker Compose** — only needed for the PostgreSQL profile

---

### Option A — Run with H2 (no Docker, dev mode)

The default profile uses an H2 in-memory database. Data is wiped on restart — perfect for local development and testing.

```bash
./mvnw spring-boot:run
```

Open **http://localhost:8080**

> H2 console (inspect the database in your browser):
> - URL: http://localhost:8080/h2-console
> - JDBC URL: `jdbc:h2:mem:textclassifier`
> - Username: `sa` — Password: *(leave empty)*

---

### Option B — Run with PostgreSQL (persistent data)

**Step 1 — Start the database container:**

```bash
docker compose up -d
```

This starts PostgreSQL 17 on port `5432` with:
- Database: `textclassifier`
- Username: `wp`
- Password: `wp`

**Step 2 — Run the app with the `postgres` profile:**

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

Open **http://localhost:8080**

To stop the database: `docker compose down`  
To also delete all stored data: `docker compose down -v`

---

## Project Structure

```
src/main/
├── java/mk/ukim/finki/wpproject/
│   ├── model/
│   │   ├── TextEntry.java               ← JPA entity (the core data model)
│   │   ├── enums/
│   │   │   ├── TextType.java            ← REQUEST, PROPOSAL, REMINDER, ...
│   │   │   ├── TextTone.java            ← FRIENDLY, FORMAL, ANGRY, ...
│   │   │   └── TextFormat.java          ← EMAIL, SMS, POETRY, ...
│   │   └── exceptions/
│   │       ├── TextEntryNotFoundException.java
│   │       └── InvalidTextEntryException.java
│   ├── repository/
│   │   └── TextEntryRepository.java     ← Spring Data JPA repository
│   ├── service/
│   │   ├── TextEntryService.java        ← Service interface
│   │   └── impl/
│   │       └── TextEntryServiceImpl.java
│   └── web/
│       ├── HomeController.java          ← GET /
│       └── TextEntryController.java     ← GET /entries, POST /entries/add, POST /entries/delete/{id}
└── resources/
    ├── application.properties           ← Common config (activates h2 profile by default)
    ├── application-h2.properties        ← H2 datasource settings
    ├── application-postgres.properties  ← PostgreSQL datasource settings
    └── templates/
        ├── home.html                    ← Main page (classifier form + recent entries)
        ├── entries.html                 ← Full entries list with delete
        └── fragments/
            ├── navigation.html          ← Shared navbar fragment
            └── footer.html              ← Shared footer fragment
```

---

## Adding New Labels

To add a new text type, tone, or format, simply add a constant to the corresponding enum in `model/enums/`. The form buttons are driven by the enum values — no other changes needed.

Example — adding a new tone:
```java
// TextTone.java
HUMOROUS("Humorous"),
```

---

## API Endpoints

| Method | Path                    | Description                    |
|--------|-------------------------|--------------------------------|
| GET    | `/`                     | Home page with classifier form |
| GET    | `/entries`              | Full list of all entries       |
| POST   | `/entries/add`          | Submit a new classified text   |
| POST   | `/entries/delete/{id}`  | Delete an entry by ID          |

---

## Contributing

1. Clone the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Follow the existing package structure: `model → repository → service → web`
4. Open a pull request with a clear description

---

## Notes for Future Development

Each `TextEntry` record stores the raw text alongside three structured labels. This dataset is designed to support a future AI/ML layer that learns from the accumulated annotations. When that time comes, extend the `/entries` endpoint to also serve JSON by adding a `@RestController` or a dedicated REST API endpoint alongside the existing Thymeleaf controllers.