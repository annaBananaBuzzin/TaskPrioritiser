# TaskPrioritiser

A Spring Boot application that helps prioritize tasks based on multiple scoring parameters including impact, urgency, and effort. The application uses intelligent deadline-based calculations to determine task priority scores.

## Project Overview

TaskPrioritiser is a REST API that manages tasks with the following features:
- Create, read, update, and delete tasks
- Calculate priority scores based on configurable scoring parameters
- Mark tasks as done/not done
- Filter tasks by status (outstanding, overdue, due today)
- Deadline-aware task prioritisation

## Technology Stack

- **Java 17**
- **Spring Boot 4.0.3**
- **Spring Data JPA** for data access
- **H2 Database** (in-memory for development)
- **MySQL** (for production)
- **Flyway** for database migrations
- **JUnit 5** for testing

## Project Structure

```
src/
├── main/
│   ├── java/com/example/taskprioritiser/
│   │   ├── api/                 # REST controllers and DTOs
│   │   ├── service/             # Business logic layer
│   │   ├── repository/          # Data persistence layer
│   │   ├── PrioritiserConfig.java
│   │   └── TaskPrioritiserApplication.java
│   └── resources/
│       ├── application.properties
│       └── db/migration/        # Flyway migration scripts
└── test/
    └── java/com/example/taskprioritiser/
        └── [Test files organized by layer]
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Running Locally

1. **Clone the repository** (if applicable)
   ```bash
   git clone <repository-url>
   cd TaskPrioritiser
   ```

2. **Build the project**
   ```bash
   ./mvnw clean install
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console`

### Configuration

Edit `src/main/resources/application.properties` to customize:

- **Scoring weights** (impact, urgency, effort)
- **Deadline constant** for priority calculation
- **Database settings** (switch from H2 to MySQL for production)

Example configuration:
```properties
prioritiser.day-deadline-constant=3
prioritiser.score-weight-map.impact=2
prioritiser.score-weight-map.urgency=3
prioritiser.score-weight-map.effort=3
```

## API Endpoints

### Task Management
- `POST /api/tasks` - Create a new task
- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get a specific task
- `PUT /api/tasks/{id}` - Update a task
- `DELETE /api/tasks/{id}` - Delete a task

### Task Filtering
- `GET /api/tasks/outstanding` - Get outstanding (not done) tasks
- `GET /api/tasks/outstanding/today` - Get tasks due today
- `GET /api/tasks/overdue` - Get overdue tasks

### Task Status
- `POST /api/tasks/{id}/done` - Mark task as done
- `DELETE /api/tasks/{id}/done` - Mark task as not done

## Running Tests

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=TaskControllerTest
```

## Still To Do / Future Enhancements

- [ ] Make score attributes dynamic
- [ ] Add missing unit tests 
- [ ] Add Prioritiser integration tests
- [ ] Add REST API documentation (Swagger/OpenAPI)

## Development Notes

### Database Migrations
Flyway migrations are located in `src/main/resources/db/migration/` and run automatically on application startup.

### Service Layer Architecture
- **PersistenceService**: Handles validation and orchestrates multiple repository operations
- **Repository**: Direct database access using Spring Data JPA
- **Service/UseCase**: Business logic and domain operations

### Scoring Algorithm
The priority score is calculated based on:
- **Impact**: Weighted importance of the task
- **Urgency**: Time-sensitive nature of the task
- **Effort**: Estimated work required
- **Deadline**: Days until deadline affects the final priority


