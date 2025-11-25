# Cinemas Booking System

Console-based prototype implementing the assessment requirements.

## Requirements
- Java 21
- Linux shell

## Build & Run (no external deps)
```bash
# compile
javac -d out $(find src/main/java -name "*.java")
# run
java -cp out app.GicCinemasApp
```

## Run tests (JUnit 5)
This project keeps tests self-contained without a build tool. To run tests:
```bash
# Download JUnit 5 (if not already on your machine)
# Linux example:
mkdir -p lib && cd lib
curl -LO https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar
cd ..

# Compile sources and tests
javac -cp lib/junit-platform-console-standalone-1.10.2.jar -d out $(find src/main/java -name "*.java") $(find src/test/java -name "*.java")

# Run tests
java -jar lib/junit-platform-console-standalone-1.10.2.jar -cp out --scan-classpath
```

## Build & Run with Gradle (preferred)
Requires Gradle **8.14.3** and Java 21.

```bash
./gradlew test
./gradlew run
./gradlew distZip
```

> Note: No external libraries are used to solve the problem. Tests rely on JUnit, which is permitted by the rules.

## Usage
- At startup, enter: `[Title] [Row] [SeatsPerRow]`, e.g., `Inception 8 10`.
- Menu:
  - `[1] Book tickets for <Title> (...)`
  - `[2] Check bookings`
  - `[3] Exit`

### Seat Markers
- `.` empty
- `#` seats in other confirmed bookings
- `o` seats in the **current** booking selection / or viewing a specific booking

## Assumptions
- Rows are labeled `A` (furthest from screen) up to the last row (closest).
- Default allocation:
  - Start at the **furthest** row, choose the **middle-most** column, then fill to the **right**.
  - Overflow continues row-by-row **closer to the screen** following the same rule.
- Custom allocation (e.g., `B03`):
  - Fill seats from that position **to the right** in the same row.
  - Overflow uses **default** rule from the next row (closer to the screen).
- Booking IDs: `GIC0001`, `GIC0002`, ...

## Project Layout
```
cinemas/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── app/
│   │           ├── model/
│   │           │   ├── Booking.java
│   │           │   └── Seat.java
│   │           ├── service/
│   │           │   ├── CinemaService.java
│   │           │   └── CinemaServiceImpl.java
│   │           ├── util/
│   │           │   └── SeatMapPrinter.java
│   │           └── GicCinemasApp.java
│   └── test/
│       └── java/
│           └── app/
│               └── service/
│                   └── CinemaServiceTest.java
├── build.gradle
├── README.md
└── settings.gradle
```

## Complexity (allocation)
- Per booking: O(R*C) in the worst case to scan availability; practical use scans in order and stops when N seats found.
- Memory: O(R*C) boolean seat map + per booking seat lists.
