# AI Test Case Review – Patient Registration

## 1. Review purpose
Review AI-generated patient-registration test cases against the approved requirement and actual application behavior before accepting them for manual execution or automation.

## 2. Review rules
- The approved requirement is the source of truth.
- Do not accept assumptions as requirements.
- Mark unclear behavior as **Clarification required**.
- Check that each test case has clear preconditions, test data, steps, and expected results.
- Use dummy patient data only.
- Remove duplicate or out-of-scope scenarios.

## 3. Review checklist

| Review item | What to verify | Status |
|---|---|---|
| Requirement traceability | Test case maps to an approved requirement | Pending |
| Scenario coverage | Positive and negative paths are covered | Pending |
| Mandatory fields | Required-field checks match the requirement | Pending |
| Test data | Data is valid, invalid, or boundary-appropriate | Pending |
| Steps | Steps are clear and repeatable | Pending |
| Expected result | Result is observable and unambiguous | Pending |
| Duplicate coverage | No unnecessary duplicate test cases | Pending |
| Assumptions | Unsupported assumptions are identified | Pending |
| Automation feasibility | Existing Page Object methods and locators can support the test | Pending |
| Privacy | No real patient or production confidential data | Pending |

## 4. Scenario inventory

| Test ID | Scenario | Type | Review focus |
|---|---|---|---|
| REG01 | Valid patient registration | Positive | Patient and visit creation |
| REG02 | Missing mandatory patient name | Negative | Required-field validation |
| REG03 | Invalid identification number | Negative | Identification validation |
| REG04 | DOB, age, and gender validation | Positive / data validation | Values derived from identification |
| REG05 | Visit information creation | Positive | Department, doctor, visit type, referral source |
| REG07 | Invalid email | Negative | Email format validation |

These IDs reflect the current portfolio test suite; confirm the latest execution report before recording pass/fail status.

## 5. Findings log

| ID | Finding | Severity | Action | Decision |
|---|---|---|---|---|
| F-01 | Fill in after reviewing the generated workbook | To assess | Compare with approved requirement | Pending |

## 6. Review decision
**Overall status:** Pending final evidence review.

Choose one after completing the review:
- **Accepted** – all critical checks pass.
- **Accepted with changes** – corrections are required and recorded.
- **Rejected** – critical requirement or correctness issues remain.

## 7. Evidence to attach
- Approved requirement or sanitized requirement summary.
- AI-generated test-case workbook.
- Completed review checklist.
- Test execution output and screenshots where appropriate.
- Git commit or pull request containing the reviewed automation changes.

## 8. Reviewer notes
Record the actual defects, corrections, and clarification questions here. Do not claim a test case passed unless it was executed and the result was verified.
