# AI-Generated Automation Scripts

## Project
Healthcare Patient Registration – AI-Assisted QA Portfolio

## Purpose
This folder documents how AI can assist with creating Java + Playwright + TestNG automation scripts, and how a QA engineer reviews those scripts before using them.

## Technology
- Java
- Playwright
- TestNG
- Maven
- Page Object Model (POM)

## Files
- `PatientRegistration_AI_Draft.java` – illustrative first AI-generated draft.
- `PatientRegistration_Reviewed.java` – reviewed reference version showing the intended test structure.
- `AI_Script_Review.md` – review findings, corrections, and acceptance checklist.

## Important
These Java files are portfolio examples, not drop-in replacements for the working project classes. Keep the production tests in `src/test/java/tests/RegistrationTest.java` and page interactions in `src/main/java/pages/RegistrationPage.java` (or their existing locations). Do not copy these reference classes into the Maven test source folder without reconciling method names and existing framework code.

## Review workflow
1. Provide the approved test cases and actual Page Object methods to the AI.
2. Ask AI to generate a test using the existing framework.
3. Verify every locator and method against the application and Page Object.
4. Review assertions, test data, cleanup, and negative-case behavior.
5. Execute the test.
6. Keep the reviewed implementation in the framework and document changes here.
