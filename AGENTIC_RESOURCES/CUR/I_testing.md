# Testing Framework Setup and Standards

The goal of this task is to establish a consistent testing framework for the bash-to-Java Swing transcoding project.

## Testing Philosophy

* Tests are mandatory for all implementation tasks in `checklist.md`
* Tests should be written alongside implementation, not after
* Prefer unit tests over integration tests where possible
* GUI tests should use Swing's built-in testing utilities where available

## Testing Framework Configuration

### Primary Framework: JUnit 5

* Use JUnit 5 (Jupiter) as the primary testing framework
* Add dependency to project build file:
  ```xml
  <!-- Maven -->
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
  </dependency>
  
  <!-- Gradle -->
  testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
  ```

### Assert Library

* Use AssertJ for fluent assertions (optional but recommended):
  ```xml
  <dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.25.0</version>
    <scope>test</scope>
  </dependency>
  ```

## Test Organization Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── project/
│               └── ...
└── test/
    └── java/
        └── com/
            └── project/
                └── ... (mirror package structure)
```

## Test Naming Conventions

* Test classes: `<ClassName>Test.java` or `<ClassName>Tests.java`
* Test methods: `should_<expectedBehavior>When_<condition>()`
  * Example: `shouldCalculateTotalCorrectlyWhenItemsProvided()`
* Integration tests: `<ClassName>IntegrationTest.java`
* Swing UI tests: `<ComponentName>SwingTest.java`

## Test Categories

### 1. Unit Tests

* Test individual methods in isolation
* Use mocks for dependencies (consider Mockito if needed)
* Should execute quickly (< 100ms per test)
* No external system dependencies

**Example:**
```java
@Test
void shouldReturnZeroWhenListIsEmpty() {
    Calculator calculator = new Calculator();
    List<Integer> emptyList = Collections.emptyList();
    
    int result = calculator.sum(emptyList);
    
    assertThat(result).isEqualTo(0);
}
```

### 2. Integration Tests

* Test interactions between components
* Use `@Tag("integration")` annotation for filtering
* May require more setup but should still be deterministic

**Example:**
```java
@Test
@Tag("integration")
void shouldProcessFileAndUpdateModel() {
    FileService fileService = new FileService();
    DataModel model = new DataModel();
    
    fileService.loadFile("test.txt", model);
    
    assertThat(model.getData()).isNotEmpty();
}
```

### 3. Swing Component Tests

* Use `SwingUtilities.invokeAndWait()` for thread safety
* Test component state changes, not visual appearance
* Consider using `Robolectric` or similar for UI testing if needed

**Example:**
```java
@Test
void shouldUpdateLabelWhenButtonClicked() throws Exception {
    final TestPanel panel = new TestPanel();
    final AtomicBoolean labelUpdated = new AtomicBoolean(false);
    
    SwingUtilities.invokeAndWait(() -> {
        panel.getUpdateButton().doClick();
        labelUpdated.set(panel.getLabel().getText().equals("Updated"));
    });
    
    assertThat(labelUpdated.get()).isTrue();
}
```

## Test Execution Commands

### Run All Tests
```bash
# Maven
mvn test

# Gradle
./gradlew test
```

### Run Specific Test Category
```bash
# Run only unit tests
mvn test -Dtest='*Test' -Dexclude-tags=integration

# Run only integration tests
mvn test -DfailIfNoTests=false -Dgroups=integration
```

## Checklist Integration

When creating tasks in `checklist.md`, each implementation item must have a corresponding test item:

```markdown
* [ ] implement <feature>
* [ ] write unit tests for <feature>
* [ ] verify all tests pass with `mvn test`
```

## Coverage Expectations

* Minimum 80% line coverage for new code
* Critical business logic should have 100% coverage
* Swing UI components: focus on logic, not visual rendering

## Troubleshooting Integration

* Failed tests should be documented in `troubleshooting.json` if they reveal environmental issues
* Flaky tests must be investigated and fixed, not ignored
* Add test-related problems to troubleshooting skills for future reference

## Committing Tests

* Tests should be committed with their corresponding implementation
* If a test reveals a bug, create a `[feature]_fix` branch as per `R_git.md`
* Include test output in commit message if tests revealed issues