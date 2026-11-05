# QA Prompt Library – Healthcare Patient Registration

Use these prompts with ChatGPT or Claude. Replace bracketed placeholders with the approved requirement, test case, or actual project code. Never provide real patient data, passwords, tokens, or confidential production information.

---

## Prompt 1 – Requirement analysis

Act as a Senior Healthcare QA Engineer.

Analyze the requirement below and identify:
1. Functional requirements
2. Business rules
3. Mandatory fields
4. Positive scenarios
5. Negative scenarios
6. Boundary and edge cases
7. Missing or ambiguous requirements

Do not invent behavior. Mark anything not explicitly stated as "Clarification required".

Requirement:
[PASTE APPROVED REQUIREMENT]

Output a clear, numbered analysis.

---

## Prompt 2 – Generate test cases

Act as a Senior Healthcare QA Engineer.

Create detailed manual test cases for the following approved requirement.

For each test case include:
- Test Case ID
- Scenario
- Preconditions
- Test data (dummy data only)
- Steps
- Expected result
- Priority
- Positive / Negative classification

Cover positive, negative, mandatory-field, boundary, validation, and relevant integration scenarios.

Rules:
- Do not invent business rules.
- Use only the supplied requirement.
- Mark assumptions clearly.
- Avoid real patient information.

Requirement:
[PASTE APPROVED REQUIREMENT]

---

## Prompt 3 – Review AI-generated test cases

Act as a QA Test Lead.

Review the test cases below against the approved requirement.

Check:
1. Requirement coverage
2. Missing scenarios
3. Duplicate scenarios
4. Incorrect expected results
5. Unsupported assumptions
6. Invalid or unrealistic test data
7. Clear and executable steps

Return a table with:
Test Case ID | Finding | Severity | Recommended correction | Keep / Modify / Reject

Do not silently rewrite requirements. Identify unclear items for clarification.

Approved requirement:
[PASTE REQUIREMENT]

AI-generated test cases:
[PASTE TEST CASES]

---

## Prompt 4 – Generate Playwright Java TestNG code using an existing framework

Act as a Senior SDET specializing in Java, Playwright, TestNG, Maven, and Page Object Model.

Convert the approved test case into automation code using my existing framework.

Requirements:
- Reuse the existing BaseTest.
- Reuse the existing Page Object and test-data classes.
- Keep UI locators and interactions inside Page Objects.
- Use TestNG Assert assertions.
- Follow the project's current package structure and naming conventions.
- Do not invent locators, methods, fields, endpoints, or business rules.
- Do not rewrite working framework code unnecessarily.
- If a required method or locator is missing, ask me for the relevant source code or DOM details before generating it.
- Provide only the exact new or modified code and state where it belongs.

Approved test case:
[PASTE APPROVED TEST CASE]

Existing BaseTest.java:
[PASTE RELEVANT CODE]

Existing Page Object:
[PASTE RELEVANT METHODS]

Existing test-data class:
[PASTE RELEVANT CONSTANTS]

---

## Prompt 5 – Debug a failed Playwright test

Act as a Senior SDET and debug this failure systematically.

I will provide:
1. Test method
2. Page Object method
3. Full exception and stack trace
4. Screenshot or relevant DOM, if available

Your tasks:
- Identify the exact failing line.
- Separate locator, synchronization, application validation, test-data, and API issues.
- Explain the most likely root cause using evidence.
- Suggest the smallest safe code change.
- Preserve tests that already pass.
- Do not guess selectors or claim a fix without a rerun.

Failure output:
[PASTE FAILURE]

Relevant code:
[PASTE CODE]

---

## Prompt 6 – Assess AI output quality

Evaluate the AI-generated QA artifact using these criteria:
- Correctness against supplied requirements
- Completeness of scenario coverage
- Traceability to requirements
- No invented business rules
- Clear expected results
- Maintainable automation design
- Reuse of existing framework components
- Privacy-safe dummy data

Give each criterion a score from 1 to 5, explain the score, list critical defects, and provide a final decision: Accept, Accept with changes, or Reject.

Artifact:
[PASTE AI OUTPUT]

Source requirement:
[PASTE APPROVED REQUIREMENT]

---

## Prompt 7 – Create a traceability matrix

Create a requirement traceability matrix from the supplied requirements and test cases.

Columns:
Requirement ID | Requirement | Test Case IDs | Coverage status | Gap / clarification

Do not create requirement IDs or rules that are not supplied. Mark missing links as "Not mapped".

Requirements:
[PASTE REQUIREMENTS]

Test cases:
[PASTE TEST CASES]
