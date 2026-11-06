
# Patient Registration – PromptFoo AI Evaluation

## 1. Project Overview

This project evaluates AI-generated healthcare patient registration test cases using PromptFoo.

## 2. Objective

- Evaluate AI-generated test case quality.
- Check positive and negative scenarios.
- Verify expected result coverage.
- Identify missing validation scenarios.
- Improve AI-assisted QA review skills.

## 3. Technology

- PromptFoo 0.125.1
- Node.js 24.14.1
- npm 11.11.0
- OpenAI model provider
- YAML configuration
- AI prompt engineering

## 4. Project Files

- promptfooconfig.yaml
- patient_registration_prompt.txt
- README.md

## 5. Setup

Install dependencies:

```powershell
npm install
```

Configure the OpenAI API key in PowerShell:

```powershell
$env:OPENAI_API_KEY = "YOUR_OPENAI_API_KEY"
```

Do not save API keys in project files or GitHub.

## 6. Execute Evaluation

Run:

```powershell
npx promptfoo eval
```

View the report:

```powershell
npx promptfoo view
```

## 7. Evaluation Criteria

The evaluation checks whether AI-generated output contains:

- Test Case ID
- Expected Result
- Positive scenarios
- Negative scenarios
- Priority

## 8. QA Review

PromptFoo structural checks do not guarantee functional correctness.

A QA engineer must manually review:

- Requirement coverage
- Test data
- Expected results
- Missing scenarios
- Business rule accuracy

## 9. Security

- Use dummy patient data only.
- Never upload real patient information.
- Never commit API keys or credentials.

## 10. Evaluation Status

Status: Pending execution.

Pass rate and evaluation results will be documented only after an actual run.
