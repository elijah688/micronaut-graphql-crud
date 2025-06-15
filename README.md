# Micronaut 4.8.2 GraphQL Java 21 Project with JWT Authentication and Hibernate ORM

This project is a modern Java 21 application built with **Micronaut 4.8.2** that exposes a **GraphQL API** for managing books and authors. It features:

- **JWT-based security** for protected endpoints
- **JPA with Hibernate ORM** for database persistence on PostgreSQL
- **Cursor-based pagination** in GraphQL queries (supporting `first`, `last`, `before`, `after`)
- Comprehensive **end-to-end integration tests** running against an embedded Micronaut server and a PostgreSQL test database spun up with Docker

---

## Key Highlights

- **GraphQL API** enables idiomatic queries and mutations to manage authors and books, including creation, update, and pagination.
- **JWT Authentication** integrates with Micronaut Security for secure access control.
- **Hibernate JPA** automatically manages schema migration and entity persistence.
- **Integration Testing** spins up a real PostgreSQL DB container via Docker for isolated, reproducible tests.
- **Makefile automation** simplifies common tasks like starting/stopping the DB container, running tests, and building the native Docker image.
- **Gradle build** uses Micronaut plugins for AOT compilation, GraalVM native image generation, and dependency management.

---

## Build & Run

### Prerequisites

- Java 21 JDK installed
- Docker installed and running
- Gradle wrapper (`./gradlew`) available

### Setup and Testing

Use the provided **Makefile** to handle database lifecycle and testing smoothly:

- **Start PostgreSQL test database:**

  ```bash
  make db_up
  ```

- **Run all tests with isolated DB:**

  ```bash
  make test
  ```

  This target starts the DB container, runs tests, then tears down the DB.

- **Stop the DB container (if needed):**

  ```bash
  make db_down
  ```

### Running Locally

To roll a local postgres DB instance:

```bash
docker compose up -d
```

To launch the Micronaut server locally:

```bash
make run
```

To sign up with a user:

```bash
./scripts/signup.sh
```

To login and upsert an author:

```bash
./scripts/upsert_author_auth.sh
```


### Building and Deploying Docker Native Image

To build a native Docker image using GraalVM and deploy with Docker Compose:

```bash
make docker
```

---

## Project Configuration Overview

- **Gradle (`build.gradle`)** manages dependencies including Micronaut core, GraphQL, security with JWT, JPA/Hibernate, PostgreSQL driver, and testing frameworks. It configures Java 21 compatibility, AOT optimizations, and native image build options.

- **Makefile** automates Docker DB container management and common Gradle commands, streamlining development and CI workflows.

---

## Testing

The project contains comprehensive integration tests for:

- Creating and updating authors and books through GraphQL mutations.
- Querying single entities and paginated lists via GraphQL queries.
- Validating cursor-based pagination with forward and backward navigation (`first`, `last`, `before`, `after`).
- Running all tests against a real PostgreSQL DB inside a Docker container for realistic and repeatable test runs.

---

## Useful Links

- [Micronaut Documentation](https://micronaut.io/documentation.html)
- [Micronaut GraphQL Guide](https://guides.micronaut.io/micronaut-graphql/latest/index.html)
- [Micronaut Security & JWT](https://micronaut-projects.github.io/micronaut-security/latest/guide/index.html)
- [Micronaut Data & Hibernate](https://micronaut-projects.github.io/micronaut-data/latest/guide/index.html)

---

## Summary

This project demonstrates a clean, idiomatic approach to building modern Java microservices with Micronaut. It combines GraphQL API flexibility, JWT authentication, robust data access with JPA/Hibernate, and reliable containerized testing — all with a streamlined developer experience powered by Gradle and Makefile automation.
