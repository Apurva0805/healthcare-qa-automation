# AI Script Review – Patient Registration

## Review objective
Evaluate whether AI-generated Playwright + Java + TestNG code is accurate, maintainable, and compatible with the existing healthcare QA framework.

## Draft review

| Review area | Finding | Required action |
|---|---|---|
| Framework reuse | Draft extends `BaseTest` | Confirm the existing package and lifecycle setup |
| Page Object Model | UI actions are called through `RegistrationPage` | Do not put raw UI locators in the test class |
| Test data | Queue number comes from `RegistrationTestData` | Confirm the constant exists and is valid |
| Assertions | Draft uses a Java `assert` | Replace with TestNG `Assert` assertions |
| Locator reliability | No new locators are invented in the test | Verify locators inside the actual Page Object |
| Save behavior | Uses the existing `save()` workflow | Preserve API response and confirmation handling |
| Test isolation | Each test uses `BaseTest` lifecycle | Confirm setup and teardown run for each test |

## Human review decisions
1. Use TestNG `Assert` instead of Java's `assert`, because Java assertions may be disabled at runtime.
2. Reuse `BaseTest`, `RegistrationPage`, and `RegistrationTestData`.
3. Keep test steps in the test class and UI interactions in the Page Object.
4. Do not invent selectors or application behavior.
5. Do not replace the working registration test suite with generated code.
6. Verify that the methods referenced in the reference script exist in the current project before copying code.

## Acceptance checklist
- [ ] Code compiles with the project's Java/Maven configuration.
- [ ] Test uses the existing `BaseTest`.
- [ ] UI actions use the existing Page Object.
- [ ] All locators are verified against the application.
- [ ] Test data is valid and does not expose real patient information.
- [ ] Assertions use TestNG.
- [ ] Positive and negative scenarios are covered.
- [ ] Test passes when run independently.
- [ ] Full registration suite passes.
- [ ] Changes are committed to GitHub.

## Result
Status: Reference review completed. The reviewed file is an example, not yet certified as a drop-in replacement. Final acceptance requires comparison with the current local source files and a successful test run.
