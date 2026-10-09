# ShelfUp

A role-based platform for sharing study resources, with a quiz game.

## Features
- Notes and question papers organized by course and subject
- Admin moderation, upvotes and a request board
- QuizArena: quizzes with server-side scoring
- JWT authentication with role-based access

## Tech Stack
Java, Spring Boot, Spring Security, JWT, Spring Data JPA, H2, HTML, CSS, JavaScript

## Run locally
mvn spring-boot:run

The app starts on http://localhost:8080 with a local H2 database stored in `data/`.
H2 console: http://localhost:8080/h2-console

## Configuration
Optional: set the `JWT_SECRET` environment variable to override the default JWT secret.