package pages;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.Assert;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class RegistrationPage {

    private final Page page;
    private final Random random = new Random();
    private final String stamp =
            String.valueOf(System.currentTimeMillis()).substring(6);

    private final boolean male = random.nextBoolean();

    private final LocalDate dob = LocalDate.of(
            1970 + random.nextInt(30),
            1 + random.nextInt(12),
            1 + random.nextInt(28)
    );

    private final String patientName = "AUTOTEST PATIENT " + randomLetters(6);
    private final String patientNric = makeNric(dob, male);

    private final String kinNric = makeNric(
            LocalDate.of(
                    1965 + random.nextInt(30),
                    1 + random.nextInt(12),
                    1 + random.nextInt(28)
            ),
            !male
    );

    public RegistrationPage(Page page) {
        this.page = page;
    }

    // Malaysian NRIC: YYMMDD + place code + 4 digits.
    // Odd final digit = male; even final digit = female.
    private String makeNric(LocalDate birthDate, boolean isMale) {
        int lastDigit = isMale
                ? 1 + 2 * random.nextInt(5)
                : 2 * random.nextInt(5);

        return birthDate.format(DateTimeFormatter.ofPattern("yyMMdd"))
                + "14"
                + String.format("%03d", random.nextInt(1000))
                + lastDigit;
    }

    private String randomLetters(int length) {
        StringBuilder letters = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            letters.append((char) ('A' + random.nextInt(26)));
        }

        return letters.toString();
    }

    // ------------------------------------------------------------
    // Common helpers
    // ------------------------------------------------------------

    private Locator findField(String ngModel) {
        return page.locator(
                "[ng-model='" + ngModel + "']:visible"
        ).first();
    }

    private void fillFieldIfAvailable(String ngModel, String value) {
        Locator field = findField(ngModel);

        if (field.count() > 0 && field.isVisible() && field.isEnabled()) {
            field.fill(value);
        } else {
            System.out.println(
                    "Field not found or unavailable: " + ngModel
            );
        }
    }

    private void selectDropdownOption(String ngModel, String requestedText) {
        Locator dropdown = page.locator(
                "select[ng-model='" + ngModel + "']:visible"
        ).first();

        dropdown.waitFor();

        page.waitForCondition(
                () -> dropdown.locator("option").count() > 1
        );

        List<String> options =
                dropdown.locator("option").allInnerTexts();

        int selectedIndex = -1;

        if (requestedText != null && !requestedText.isBlank()) {

            // Exact match first.
            for (int i = 1; i < options.size(); i++) {
                if (options.get(i).trim()
                        .equalsIgnoreCase(requestedText.trim())) {
                    selectedIndex = i;
                    break;
                }
            }

            // Partial match if no exact match exists.
            if (selectedIndex == -1) {
                for (int i = 1; i < options.size(); i++) {
                    if (options.get(i).trim().toLowerCase()
                            .contains(requestedText.trim().toLowerCase())) {
                        selectedIndex = i;
                        break;
                    }
                }
            }
        }

        if (selectedIndex == -1) {
            throw new AssertionError(
                    "Option '" + requestedText + "' not found for "
                            + ngModel + ". Available options: " + options
            );
        }

        dropdown.selectOption(
                new SelectOption().setIndex(selectedIndex)
        );

        page.waitForTimeout(300);
    }

    private void selectFirstDropdownOption(String ngModel) {
        Locator dropdown = page.locator(
                "select[ng-model='" + ngModel + "']:visible"
        ).first();

        dropdown.waitFor();

        page.waitForCondition(
                () -> dropdown.locator("option").count() > 1
        );

        List<String> options =
                dropdown.locator("option").allInnerTexts();

        for (int i = 1; i < options.size(); i++) {
            String option = options.get(i).trim();

            if (!option.isEmpty()
                    && !option.startsWith("--")
                    && !option.equalsIgnoreCase("-Select-")) {

                dropdown.selectOption(
                        new SelectOption().setIndex(i)
                );

                page.waitForTimeout(300);

                return;
            }
        }

        throw new AssertionError(
                "No valid options found for " + ngModel
                        + ". Available options: " + options
        );
    }

    private String getSelectedOptionText(String ngModel) {
        Locator dropdown = page.locator(
                "select[ng-model='" + ngModel + "']:visible"
        ).first();

        return dropdown.locator("option:checked")
                .innerText()
                .trim();
    }

    private Locator findButton(String name) {
        return page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName(name)
                        .setExact(true)
        );
    }

    // ------------------------------------------------------------
    // 1. Open Registration
    // ------------------------------------------------------------

    public void openRegistration() {
        Locator opMenu = page.getByText(
                "OP",
                new Page.GetByTextOptions().setExact(true)
        ).first();

        assertThat(opMenu).isVisible();
        opMenu.click();

        page.getByText(
                "Registration",
                new Page.GetByTextOptions().setExact(true)
        ).first().click();

        // Verify that the Registration form has opened.
        page.locator("input[ng-model='Registration.FirstName']")
                .waitFor(new Locator.WaitForOptions().setTimeout(15000));

    }

    // ------------------------------------------------------------
    // 2. Patient information
    // ------------------------------------------------------------

    public void fillPatientInformation() {

        // Patient name and prefix.
        Locator patientNameField =
                page.locator("input[ng-model='Registration.FirstName']");
        assertThat(patientNameField).isVisible();
        patientNameField.fill(patientName);
        assertThat(patientNameField).hasValue(patientName);

        selectDropdownOption("Registration.PrefixID", male ? "Mr" : "Ms");

        // Identification type.
        selectDropdownOption("Registration.ICCardTypeID", "New IC");

        // Enter NRIC and trigger the application's change event.
        Locator nricField = findField("Registration.NationalId");

        nricField.fill(patientNric);
        nricField.press("Tab");

        String expectedDob = dob.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );

        page.waitForCondition(
                () -> expectedDob.equals(
                        page.locator("#txtDateOfBirth").inputValue().trim()
                )
        );

        String expectedGender = male ? "Male" : "Female";
        selectDropdownOption("Registration.GenderID", expectedGender);

        // Nationality and personal details.
        selectDropdownOption("Registration.NationalityID", "Malaysian");
        selectDropdownOption("Registration.ReligionID", "Islam");
        selectDropdownOption("Registration.RaceID", "Malay");
        selectDropdownOption("Registration.MaritalStatusID", "Single");

        // Blood group is optional. Select O or O+ only if available.
        Locator bloodGroup = page.locator(
                "select[ng-model='Registration.BloodGroupID']:visible"
        ).first();

        if (bloodGroup.count() > 0 && bloodGroup.isVisible()) {
            List<String> options =
                    bloodGroup.locator("option").allInnerTexts();

            for (int i = 1; i < options.size(); i++) {
                String option = options.get(i).trim();

                if (option.equalsIgnoreCase("O")
                        || option.equalsIgnoreCase("O+")) {
                    bloodGroup.selectOption(
                            new SelectOption().setIndex(i)
                    );
                    break;
                }
            }
        }
    }

    // ------------------------------------------------------------
    // 3. Verify NRIC, DOB, age and gender
    // ------------------------------------------------------------

    public void verifyNricAutoPopulated() {

        String expectedDob =
                dob.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        int expectedAge =
                Period.between(dob, LocalDate.now()).getYears();

        String expectedGender = male ? "Male" : "Female";

        assertThat(page.locator("#txtDateOfBirth"))
                .hasValue(expectedDob);

        String actualAge =
                page.locator("#txtYear").inputValue().trim();

        if (!actualAge.matches("^" + expectedAge + "\\b.*")) {
            throw new AssertionError(
                    "Expected age " + expectedAge
                            + " but actual age was '" + actualAge + "'"
            );
        }

        String actualGender =
                getSelectedOptionText("Registration.GenderID");

        if (!actualGender.equalsIgnoreCase(expectedGender)) {
            throw new AssertionError(
                    "Expected gender " + expectedGender
                            + " but actual gender was '" + actualGender
                            + "'. Verify whether the application "
                            + "automatically populates Gender from NRIC."
            );
        }
    }

    // ------------------------------------------------------------
    // 4. Correspondence and contact details
    // ------------------------------------------------------------

    public void fillCorrespondence() {

        fillFieldIfAvailable("Registration.ResAddress", "12 Jalan Automation");
        fillFieldIfAvailable("Registration.ResHouseNo", "12");
        fillFieldIfAvailable("Registration.ResStreet", "Taman Testing");
        fillFieldIfAvailable("Registration.ResPinCode", "47500");

        selectDropdownOption("Registration.ResStateID", "Selangor");

        // City is dependent on State.
        Locator city = page.locator(
                "select[ng-model='Registration.ResCityID']:visible"
        ).first();

        city.waitFor();

        page.waitForCondition(
                () -> city.locator("option").count() > 1
        );

        selectFirstDropdownOption("Registration.ResCityID");
        selectDropdownOption("Registration.ResCountryID", "Malaysia");

        fillFieldIfAvailable(
                "Registration.MobileNo",
                "0123456789"
        );

        fillFieldIfAvailable(
                "Registration.Email",
                "auto." + stamp + "@example.com"
        );
    }

    // ------------------------------------------------------------
    // 5. Next of Kin
    // ------------------------------------------------------------


    public void addNextOfKin() {

        String kinName = "AUTOTESTKIN";

        selectDropdownOption("Registration.KinTitleID", male ? "Ms" : "Mr");

        fillFieldIfAvailable("Registration.KinName", kinName);

        // Select identification type
        selectDropdownOption("Registration.NOKnationalid", "Malaysian");

        fillFieldIfAvailable(
                "Registration.KinNationalId",
                kinNric
        );

        selectDropdownOption("Registration.KinRelationID", "Guardian");

        fillFieldIfAvailable(
                "Registration.KinMobileNo",
                "0123456788"
        );

        fillFieldIfAvailable(
                "Registration.KinEmail",
                "kin." + stamp + "@example.com"
        );

        // Select same address as patient, if available
        Locator sameAddress = page.locator(
                "[ng-model='chkSameasPatAddr']:visible"
        ).first();

        if (sameAddress.count() == 0) {
            sameAddress = page.locator(
                    "#chkSameasPatAddr:visible"
            ).first();
        }

        if (sameAddress.count() > 0 && sameAddress.isVisible()) {
            sameAddress.check();
        }

        // Click Add
        Locator addButton = findButton("Add").first();

        assertThat(addButton).isVisible();
        addButton.click();

        // Verify the saved Next of Kin row
        Locator savedKin = page.locator("tr")
                .filter(
                        new Locator.FilterOptions()
                                .setHasText(kinName)
                )
                .first();

        assertThat(savedKin).isVisible(
                new LocatorAssertions.IsVisibleOptions()
                        .setTimeout(10000)
        );

    }


    // ------------------------------------------------------------
    // 6. Visit information
    // ------------------------------------------------------------

    public void fillVisitInformation(String queueNumber) {
        if (queueNumber == null || queueNumber.isBlank()) {
            throw new IllegalArgumentException("Queue number must not be blank");
        }

        Locator department =
                page.locator("select[ng-model='Visit.DepartmentID']:visible");
        if (!department.isVisible()) {
            Locator visitInformation = page.getByText(
                    "Visit Information",
                    new Page.GetByTextOptions().setExact(true)
            ).first();
            assertThat(visitInformation).isVisible();
            visitInformation.click();
        }
        assertThat(department).isVisible();
        department.selectOption(
                new SelectOption().setLabel("Cardiology")
        );
        if (!getSelectedOptionText("Visit.DepartmentID")
                .equalsIgnoreCase("Cardiology")) {
            throw new AssertionError(
                    "Cardiology department was not selected"
            );
        }

        Locator doctor =
                page.locator("select[ng-model='Visit.DoctorID']:visible")
                        .first();
        page.waitForCondition(
                () -> doctor.locator("option").count() > 1
        );

        doctor.selectOption(
                new SelectOption().setValue("number:3288")
        );
        assertThat(doctor).hasValue("number:3288");

        selectDropdownOption("Visit.VisitTypeID", "New");
        if (!getSelectedOptionText("Visit.VisitTypeID")
                .equalsIgnoreCase("New")) {
            throw new AssertionError("New visit type was not selected");
        }

        Locator referralSource =
                page.locator(
                        "select[ng-model='Visit.PatientSourceID']:visible"
                ).first();
        referralSource.selectOption(
                new SelectOption().setValue("number:8")
        );
        assertThat(referralSource).hasValue("number:8");

        Locator queueNo =
                page.locator("input[ng-model='Visit.TokenNo']:visible")
                        .first();
        queueNo.waitFor();
        assertThat(queueNo).isVisible();
        assertThat(queueNo).isEnabled();
        queueNo.fill(queueNumber);
        queueNo.press("Tab");
        assertThat(queueNo).hasValue(queueNumber);
    }

    // ------------------------------------------------------------
    // 7. Save registration
    // ------------------------------------------------------------

    public void save() {

        Locator saveButton = page.locator("#regbutton");

        assertThat(saveButton).isVisible();
        assertThat(saveButton).isEnabled();

        List<String> invalidFieldDescriptions =
                describeInvalidVisibleFields();
        if (!invalidFieldDescriptions.isEmpty()) {
            throw new AssertionError(
                    "Registration cannot be saved because these visible "
                            + "fields are invalid: "
                            + String.join(", ", invalidFieldDescriptions)
            );
        }

        Locator confirmationPrompt =
                page.getByText("Are you sure. Do you want to Save");
        Locator confirmSaveButton = page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Save")
                        .setExact(true)
        ).last();

        // A browser confirmation blocks the page until this handler responds.
        AtomicReference<Dialog> confirmationDialog = new AtomicReference<>();

        // Accept only the expected browser confirmation.
        Consumer<Dialog> dialogHandler = dialog -> {
            confirmationDialog.set(dialog);
            if ("confirm".equals(dialog.type())
                    && dialog.message().contains(
                    "Are you sure. Do you want to Save")) {
                dialog.accept();
            } else {
                dialog.dismiss();
            }
        };

        page.onDialog(dialogHandler);
        Response saveResponse;
        try {
            // Click Save and wait for the registration API request.
            saveResponse = page.waitForResponse(
                    response -> {
                        String method = response.request().method();
                        String resourceType =
                                response.request().resourceType();
                        return ("POST".equalsIgnoreCase(method)
                                || "PUT".equalsIgnoreCase(method)
                                || "PATCH".equalsIgnoreCase(method))
                                && ("xhr".equalsIgnoreCase(resourceType)
                                || "fetch".equalsIgnoreCase(resourceType))
                                && response.url().contains(
                                "/api/patientregistration/IUD"
                        );
                    },
                    new Page.WaitForResponseOptions()
                            .setTimeout(10000),
                    () -> {
                        saveButton.click();
                        Dialog dialog = confirmationDialog.get();
                        if (dialog != null) {
                            if (!"confirm".equals(dialog.type())
                                    || !dialog.message().contains(
                                    "Are you sure. Do you want to Save")) {
                                throw new AssertionError(
                                        "Unexpected browser confirmation: "
                                                + dialog.message()
                                );
                            }
                        } else {
                            try {
                                confirmationPrompt.waitFor(
                                        new Locator.WaitForOptions()
                                                .setTimeout(2000)
                                );
                                assertThat(confirmSaveButton).isVisible();
                                confirmSaveButton.click();
                            } catch (TimeoutError timeout) {
                                List<String> invalidFields =
                                        describeInvalidVisibleFields();
                                if (!invalidFields.isEmpty()) {
                                    throw new AssertionError(
                                            "Save was blocked by browser "
                                                    + "validation; invalid "
                                                    + "visible fields: "
                                                    + String.join(
                                                    ", ", invalidFields
                                            ),
                                            timeout
                                    );
                                }
                            }
                        }
                    }
            );
        } catch (TimeoutError timeout) {
            List<String> invalidFields = describeInvalidVisibleFields();
            String validationText = String.join(
                    " | ",
                    page.locator(
                            ".text-danger:visible, "
                                    + ".validation-error:visible, "
                                    + ".field-validation-error:visible, "
                                    + ".toast:visible, "
                                    + ".alert:visible, "
                                    + "[role='alert']:visible"
                    ).allInnerTexts()
            ).trim();
            throw new AssertionError(
                    "Timed out waiting for the patient-registration save "
                            + "request."
                            + (invalidFields.isEmpty()
                            ? ""
                            : " Invalid visible fields: "
                                    + String.join(", ", invalidFields) + ".")
                            + (validationText.isEmpty()
                            ? ""
                            : " Validation messages: " + validationText),
                    timeout
            );
        } finally {
            page.offDialog(dialogHandler);
        }

        if (confirmationDialog.get() == null) {
            confirmationPrompt.waitFor(
                    new Locator.WaitForOptions()
                            .setState(WaitForSelectorState.HIDDEN)
            );
        }

        if (!saveResponse.ok()) {
            throw new AssertionError(
                    "Registration save request failed with HTTP "
                            + saveResponse.status() + " at "
                            + saveResponse.url()
            );
        }

        String bodyText = page.locator("body").innerText();

        Locator validationMessages = page.locator(
                ".text-danger:visible, "
                        + ".validation-error:visible, "
                        + ".field-validation-error:visible, "
                        + ".toast:visible, "
                        + ".alert:visible, "
                        + "[role='alert']:visible"
        );

        List<String> validationText =
                validationMessages.allInnerTexts();

        if (bodyText.toLowerCase().contains(
                "please select referral source")) {
            throw new AssertionError(
                    "Registration was not saved. Referral Source is required. "
                            + "Check Visit.PatientSourceID."
            );
        }

        if (!validationText.isEmpty()) {
            String message = String.join(" | ", validationText).trim();

            if (!message.isEmpty()) {
                throw new AssertionError(
                        "Registration was not saved. Va" +
                                "lidation: " + message
                );
            }
        }
        // Wait for the application to navigate after saving
        page.waitForURL("**/#/VisitScreen");

// Verify that the visit screen is displayed
        Assert.assertTrue(
                page.url().contains("/#/VisitScreen"),
                "Registration failed: Visit screen was not displayed after saving."
        );

        System.out.println("PASS: Patient registration completed and Visit screen opened.");

    }

    private List<String> describeInvalidVisibleFields() {
        Locator invalidFields = page.locator(
                "input:invalid:visible, "
                        + "select:invalid:visible, "
                        + "textarea:invalid:visible"
        );
        List<String> descriptions = new ArrayList<>();

        for (Locator field : invalidFields.all()) {
            String description = field.getAttribute("ng-model");
            if (description == null || description.isBlank()) {
                description = field.getAttribute("name");
            }
            if (description == null || description.isBlank()) {
                description = field.getAttribute("id");
            }
            descriptions.add(
                    description == null || description.isBlank()
                            ? field.evaluate("element => element.tagName")
                                    .toString()
                            : description
            );
        }

        return descriptions;
    }


    public void clearPatientName() {
        Locator patientNameField =
                page.locator("input[ng-model='Registration.FirstName']:visible")
                        .first();

        patientNameField.waitFor();

        patientNameField.fill("");

        Assert.assertEquals(
                patientNameField.inputValue(),
                "",
                "Patient name should be blank for this negative test"
        );
    }

    public void fillPatientInformationWithInvalidIC(String invalidIC) {
        if (invalidIC == null || invalidIC.isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid identification value must not be blank"
            );
        }

        Locator nationalId = findField("Registration.NationalId");
        nationalId.fill(invalidIC);
        nationalId.press("Tab");
        assertThat(nationalId).hasValue(invalidIC);
    }

    public boolean isIdentificationValidationDisplayed() {
        Locator nationalId = findField("Registration.NationalId");
        boolean invalidField = (Boolean) nationalId.evaluate(
                "element => !element.checkValidity() "
                        + "|| element.classList.contains('ng-invalid')"
        );

        List<String> messages = page.locator(
                ".text-danger:visible, "
                        + ".validation-error:visible, "
                        + ".field-validation-error:visible, "
                        + "[role='alert']:visible"
        ).allInnerTexts();

        return invalidField || messages.stream().anyMatch(message ->
                message.toLowerCase().matches(
                        ".*(ic|nric|identification|national id|invalid).*"
                )
        );
    }

    public void attemptSaveWithMissingPatientName() {
        Locator saveButton = page.locator("#regbutton");

        Assert.assertTrue(
                saveButton.isVisible(),
                "Save button should be visible"
        );

        saveButton.click();

        // Allow client-side validation to respond.
        page.waitForTimeout(1500);

        Locator patientNameField =
                page.locator("input[ng-model='Registration.FirstName']:visible")
                        .first();

        Assert.assertEquals(
                patientNameField.inputValue(),
                "",
                "Patient name should remain blank"
        );

        // Check native browser validation.
        boolean browserValidationFailed =
                (Boolean) patientNameField.evaluate(
                        "(element) => !element.checkValidity()"
                );

        // Check common application validation elements.
        boolean applicationValidationVisible =
                page.locator(
                        ".text-danger:visible, " +
                                ".validation-error:visible, " +
                                ".field-validation-error:visible, " +
                                "[role='alert']:visible"
                ).count() > 0;

        // A successful save should not navigate to the Visit screen.
        Assert.assertFalse(
                page.url().contains("/#/VisitScreen"),
                "A patient must not be registered without a name"
        );

        Assert.assertTrue(
                browserValidationFailed || applicationValidationVisible,
                "Expected patient-name validation, but no validation was detected"
        );

        System.out.println(
                "PASS: Missing patient name was blocked by validation."
        );
    }

}
