# AI Testing Approach

## 1. Project overview
**Project:** Healthcare Patient Registration – AI-Assisted QA Portfolio

This portfolio demonstrates how generative AI can support healthcare QA activities while keeping the QA engineer responsible for validation, test design, and release decisions.

## 2. Objectives
- Analyze approved requirements with AI assistance.
- Generate positive, negative, boundary, and validation test scenarios.
- Review AI-generated test cases for correctness and coverage.
- Use approved test cases to assist Java + Playwright + TestNG automation development.
- Evaluate AI output quality and document human corrections.

## 3. Technology and tools
- ChatGPT / Claude – requirement analysis, test design, review, and coding assistance.
- Java – automation programming language.
- Playwright – browser automation.
- TestNG – test execution and assertions.
- Maven – dependency and build management.
- Apache POI / Microsoft Excel – test-case data and review.
- Git / GitHub – version control and portfolio hosting.
- PromptFoo – planned evaluation of AI prompts and outputs.

## 4. Scope
Patient-registration and visit scenarios covered by the current project include:
- Valid patient registration.
- Mandatory patient-name validation.
- Invalid identification number validation.
- DOB, age, and gender derived from identification information.
- Visit department, doctor, visit type, and referral source.
- Invalid email validation.

Only scenarios supported by approved requirements and actual application behavior should be treated as expected behavior.

## 5. AI-assisted workflow
1. Read and understand the approved requirement.
2. Ask AI to identify rules, ambiguities, and possible scenarios.
3. Generate draft test cases.
4. Compare every test case with the requirement.
5. Remove duplicates and unsupported assumptions.
6. Record review decisions and clarification questions.
7. Generate automation code using the existing BaseTest and Page Object Model.
8. Verify locators and methods against the actual application and existing source code.
9. Execute tests and investigate failures using logs and screenshots.
10. Commit reviewed and working changes to GitHub.

## 6. Human review and quality controls
AI output is a draft, not an authority. A QA engineer must verify:
- Requirement traceability.
- Correct preconditions, test data, steps, and expected results.
- Positive and negative coverage.
- No invented business rules or selectors.
- Reuse of existing framework components.
- Stable locators and meaningful assertions.
- Successful independent and full-suite execution.

## 7. Privacy and data handling
- Use dummy patient information only.
- Do not enter real patient identifiers, medical records, production credentials, tokens, or confidential information into AI tools.
- Follow organizational policies for handling application screenshots and test data.

## 8. Current status
The Java + Playwright + TestNG framework and patient-registration scenarios are part of the existing project. PromptFoo evaluation is a planned portfolio activity; record it as completed only after configuration and evaluation have actually run successfully.

## 9. Expected outcome
A traceable, human-reviewed set of healthcare QA test cases and maintainable automation assets demonstrating responsible AI-assisted testing.
