# Healthcare QA Automation

[![Maven CI](https://github.com/Apurva0805/healthcare-qa-automation/actions/workflows/maven.yml/badge.svg)](https://github.com/Apurva0805/healthcare-qa-automation/actions/workflows/maven.yml)
![Java 17](https://img.shields.io/badge/Java-17-orange)
![Playwright](https://img.shields.io/badge/UI-Playwright-2EAD33)

A Java-based QA portfolio that brings together browser automation, API checks,
spreadsheet-driven test data, SQL exercises, AI-assisted test design, and CI.
The examples use synthetic test data and are intended to demonstrate practical
quality engineering—not to handle real patient information.

## What this project demonstrates

| Area | Evidence in this repository |
| --- | --- |
| Browser automation | Playwright and TestNG tests for login, patient registration, validation, and visit workflows |
| Maintainable UI tests | Page Object Model separates page interactions from test scenarios |
| API testing | REST Assured checks for successful, negative, create, update, and delete responses |
| Data-driven testing | Apache POI loads Excel cases into a TestNG data provider and validates required fields |
| Test analysis | Healthcare QA test-case workbook, SQL practice queries, and AI test-design prompts |
| AI-assisted QA | PromptFoo evaluation configuration, generated-script review, and documented human-review criteria |
| Continuous integration | GitHub Actions installs Chromium, runs Maven tests headlessly, and uploads screenshots as artifacts |

## Test coverage

The browser suite covers valid and invalid login, patient demographics derived
from an identification number, mandatory-field and format validation,
correspondence details, next of kin, and visit creation. API tests use the
configured JSONPlaceholder endpoint; they are examples of API automation and
do not connect to a healthcare production service.

## Framework at a glance

```text
src/test/java/
├── base/       Test lifecycle and shared browser setup
├── pages/      Login and registration page objects
├── testdata/   Reusable synthetic UI test data
├── tests/      UI, API, and Excel-driven TestNG tests
└── utils/      Configuration and Excel data-provider helpers

AI-QA-Prompts/                 Reusable QA prompt library
AI-Testing-Portfolio/          AI evaluation, test cases, and review material
sql-practice/                  Healthcare-oriented SQL exercises
test-cases/                    Excel test-case workbook
.github/workflows/maven.yml    GitHub Actions workflow
pom.xml                        Java dependencies and Maven test configuration
```

## Run locally

### Prerequisites

- JDK 17
- Maven
- Network access to the configured demo/UAT application for browser tests
- Authorized demo/UAT access; never use real patient information

Playwright's Chromium browser must be installed before running browser tests.
The CI workflow installs Chromium automatically. For a local installation,
run:

```bash
mvn -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium" -Dexec.classpathScope=test org.codehaus.mojo:exec-maven-plugin:3.6.4:java
```

Run all Maven tests:

```bash
mvn clean test
```

Run a specific test group:

```bash
mvn "-Dtest=RegistrationTest" test
mvn "-Dtest=RegistrationNegativeTest" test
mvn "-Dtest=ApiTest" test
```

The UI tests require access to the configured demo/UAT environment. API tests
use `api.base.url` from `src/test/resources/config.properties`.

## CI

On pushes and pull requests targeting `main`, GitHub Actions uses Java 17,
installs Chromium, and runs `mvn -B -Dplaywright.headless=true clean test`.
Playwright screenshots are uploaded as workflow artifacts, including when a
test run fails.

## Latest verified run

`mvn -Dtest=RegistrationTest test` — **6 tests passed, 0 failures** (25 Nov
2026). This result covers the registration test class, not the complete
repository test suite.

## Portfolio materials

- [AI-assisted QA documentation](AI-Testing-Portfolio/Documentation/README.md)
- [AI prompt library](AI-QA-Prompts/01-Requirement-Analysis.md)
- [SQL practice queries](sql-practice/healthcare_qa_queries.sql)
- [Excel test cases](test-cases/Healthcare_QA_Test_Cases.xlsx)

## Author

**Apurva Shivudkar** · [GitHub](https://github.com/Apurva0805)
