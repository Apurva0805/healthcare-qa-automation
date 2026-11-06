package pages;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.Assert;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class RegistrationPage {

    private final Page page;
    private final Random random = new Random();
    private boolean registrationSaved;

    private final String uniqueStamp =
            Long.toString(System.currentTimeMillis());

    private final boolean male = random.nextBoolean();

    private final LocalDate dob = LocalDate.of(
            1970 + random.nextInt(30),
            1 + random.nextInt(12),
            1 + random.nextInt(28)
    );

    private final String patientName =
            "AUTOTEST PATIENT " + randomLetters(6);

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

    // ============================================================
    // TEST DATA HELPERS
    // ============================================================

    private String randomLetters(int length) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            result.append((char) ('A' + random.nextInt(26)));
        }

        return result.toString();
    }

    private String makeNric(LocalDate birthDate, boolean isMale) {
        int finalDigit = isMale
                ? 1 + 2 * random.nextInt(5)
                : 2 * random.nextInt(5);

        return birthDate.format(DateTimeFormatter.ofPattern("yyMMdd"))
                + "14"
                + String.format("%03d", random.nextInt(1000))
                + finalDigit;
    }

    private String expectedDob() {
        return dob.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private int expectedAge() {
        return Period.between(dob, LocalDate.now()).getYears();
    }

    private String expectedGender() {
        return male ? "Male" : "Female";
    }

    // ============================================================
    // COMMON LOCATOR HELPERS
    // ============================================================

    private Locator findField(String ngModel) {
        return page.locator(
                "[ng-model='" + ngModel + "']:visible"
        ).first();
    }

    private Locator findDropdown(String ngModel) {
        return page.locator(
                "select[ng-model='" + ngModel + "']:visible"
        ).first();
    }

    private void fillField(String ngModel, String value) {
        Locator field = findField(ngModel);

        assertThat(field).isVisible();
        assertThat(field).isEnabled();

        field.fill(value);
    }

    private void fillFieldIfAvailable(String ngModel, String value) {
        Locator field = findField(ngModel);

        if (field.count() > 0 && field.isVisible() && field.isEnabled()) {
            field.fill(value);
        } else {
            System.out.println("Optional field unavailable: " + ngModel);
        }
    }

    private void selectDropdownOption(
            String ngModel,
            String requestedText
    ) {
        Locator dropdown = findDropdown(ngModel);

        dropdown.waitFor();

        page.waitForCondition(
                () -> dropdown.locator("option").count() > 1
        );

        List<String> options =
                dropdown.locator("option").allInnerTexts();

        int selectedIndex = -1;

        for (int i = 1; i < options.size(); i++) {
            if (options.get(i).trim()
                    .equalsIgnoreCase(requestedText.trim())) {
                selectedIndex = i;
                break;
            }
        }

        if (selectedIndex == -1) {
            for (int i = 1; i < options.size(); i++) {
                if (options.get(i).trim().toLowerCase()
                        .contains(requestedText.trim().toLowerCase())) {
                    selectedIndex = i;
                    break;
                }
            }
        }

        if (selectedIndex == -1) {
            throw new AssertionError(
                    "Option '" + requestedText
                            + "' not found for " + ngModel
                            + ". Available options: " + options
            );
        }

        dropdown.selectOption(
                new SelectOption().setIndex(selectedIndex)
        );
    }

    private void selectFirstDropdownOption(String ngModel) {
        Locator dropdown = findDropdown(ngModel);

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

                return;
            }
        }

        throw new AssertionError(
                "No selectable option found for " + ngModel
                        + ". Available options: " + options
        );
    }

    private String getSelectedOptionText(String ngModel) {
        Locator dropdown = findDropdown(ngModel);

        return dropdown.locator("option:checked")
                .innerText().trim();
    }

    private Locator button(String buttonName) {
        return page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName(buttonName)
                        .setExact(true)
        ).first();
    }

    // ============================================================
    // OPEN REGISTRATION
    // ============================================================

    public void openRegistration() {

        Locator opMenu = page.getByText(
                "OP",
                new Page.GetByTextOptions().setExact(true)
        ).first();

        assertThat(opMenu).isVisible();
        opMenu.click();

        Locator registrationMenu = page.getByText(
                "Registration",
                new Page.GetByTextOptions().setExact(true)
        ).first();

        assertThat(registrationMenu).isVisible();
        registrationMenu.click();

        Locator patientNameField =
                page.locator(
                        "input[ng-model='Registration.FirstName']"
                ).first();

        patientNameField.waitFor(
                new Locator.WaitForOptions().setTimeout(15000)
        );

        assertThat(patientNameField).isVisible();
    }

    // ============================================================
    // PATIENT INFORMATION
    // ============================================================

    public void fillPatientInformation() {

        Locator nameField =
                page.locator(
                        "input[ng-model='Registration.FirstName']:visible"
                ).first();

        assertThat(nameField).isVisible();

        nameField.fill(patientName);

        assertThat(nameField).hasValue(patientName);

        selectDropdownOption(
                "Registration.PrefixID",
                male ? "Mr" : "Ms"
        );

        selectDropdownOption(
                "Registration.ICCardTypeID",
                "New IC"
        );

        Locator nricField =
                findField("Registration.NationalId");

        nricField.fill(patientNric);
        nricField.press("Tab");

        page.waitForCondition(
                () -> expectedDob().equals(
                        page.locator("#txtDateOfBirth")
                                .inputValue().trim()
                )
        );

        selectDropdownOption(
                "Registration.GenderID",
                expectedGender()
        );

        selectDropdownOption(
                "Registration.NationalityID",
                "Malaysian"
        );

        selectDropdownOption(
                "Registration.ReligionID",
                "Islam"
        );

        selectDropdownOption(
                "Registration.RaceID",
                "Malay"
        );

        selectDropdownOption(
                "Registration.MaritalStatusID",
                "Single"
        );

        Locator bloodGroup =
                page.locator(
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

    public void verifyNricAutoPopulated() {

        assertThat(page.locator("#txtDateOfBirth"))
                .hasValue(expectedDob());

        String actualAge =
                page.locator("#txtYear").inputValue().trim();

        if (!actualAge.matches("^" + expectedAge() + "\\b.*")) {
            throw new AssertionError(
                    "Expected age " + expectedAge()
                            + " but actual age was '" + actualAge + "'"
            );
        }

        String actualGender =
                getSelectedOptionText("Registration.GenderID");

        Assert.assertEquals(
                actualGender.toLowerCase(),
                expectedGender().toLowerCase(),
                "Gender does not match the generated NRIC"
        );
    }

    // ============================================================
    // CORRESPONDENCE DETAILS
    // ============================================================

    public void fillCorrespondence() {

        fillFieldIfAvailable(
                "Registration.ResAddress",
                "12 Jalan Automation"
        );

        fillFieldIfAvailable(
                "Registration.ResHouseNo",
                "12"
        );

        fillFieldIfAvailable(
                "Registration.ResStreet",
                "Taman Testing"
        );

        fillFieldIfAvailable(
                "Registration.ResPinCode",
                "47500"
        );

        selectDropdownOption(
                "Registration.ResStateID",
                "Selangor"
        );

        Locator city =
                findDropdown("Registration.ResCityID");

        city.waitFor();

        page.waitForCondition(
                () -> city.locator("option").count() > 1
        );

        selectFirstDropdownOption(
                "Registration.ResCityID"
        );

        selectDropdownOption(
                "Registration.ResCountryID",
                "Malaysia"
        );

        fillFieldIfAvailable(
                "Registration.MobileNo",
                "0123456789"
        );

        fillFieldIfAvailable(
                "Registration.Email",
                "auto." + uniqueStamp + "@example.com"
        );
    }
    public void fillInvalidEmail(String email) {
        fillField("Registration.Email", email);
    }

    public boolean isEmailValidationDisplayed() {

        Locator email = findField("Registration.Email");

        return (Boolean) email.evaluate(
                "el => !el.checkValidity() " +
                        "|| el.classList.contains('ng-invalid')"
        );
    }
    public String getMobileFieldDetails() {

        Locator mobile = findField("Registration.MobileNo");

        return mobile.evaluate(
                "el => JSON.stringify({" +
                        "type: el.type," +
                        "pattern: el.pattern," +
                        "minlength: el.minLength," +
                        "maxlength: el.maxLength," +
                        "required: el.required" +
                        "})"
        ).toString();
    }

    // ============================================================
    // NEXT OF KIN
    // ============================================================

    public void addNextOfKin() {

        String kinName =
                "AUTOTESTKIN"
                        + uniqueStamp.substring(
                        Math.max(0, uniqueStamp.length() - 5)
                );

        selectDropdownOption(
                "Registration.KinTitleID",
                male ? "Ms" : "Mr"
        );

        fillFieldIfAvailable(
                "Registration.KinName",
                kinName
        );

        selectDropdownOption(
                "Registration.NOKnationalid",
                "Malaysian"
        );

        fillFieldIfAvailable(
                "Registration.KinNationalId",
                kinNric
        );

        selectDropdownOption(
                "Registration.KinRelationID",
                "Guardian"
        );

        fillFieldIfAvailable(
                "Registration.KinMobileNo",
                "0123456788"
        );

        fillFieldIfAvailable(
                "Registration.KinEmail",
                "kin." + uniqueStamp + "@example.com"
        );

        Locator sameAddress =
                page.locator(
                        "[ng-model='chkSameasPatAddr']:visible"
                ).first();

        if (sameAddress.count() == 0) {
            sameAddress =
                    page.locator("#chkSameasPatAddr:visible").first();
        }

        if (sameAddress.count() > 0 && sameAddress.isVisible()) {
            sameAddress.check();
        }

        Locator addButton = button("Add");

        assertThat(addButton).isVisible();

        addButton.click();

        Locator savedKinRow =
                page.locator("tr")
                        .filter(
                                new Locator.FilterOptions()
                                        .setHasText(kinName)
                        )
                        .first();

        assertThat(savedKinRow).isVisible();
    }

    // ============================================================
    // VISIT INFORMATION
    // ============================================================


    public void fillVisitInformation(String queueNumber) {

        if (queueNumber == null || queueNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Queue number must not be blank"
            );
        }

        Locator department = findDropdown("Visit.DepartmentID");

        if (!department.isVisible()) {
            Locator visitHeading = page.getByText(
                    "Visit Information",
                    new Page.GetByTextOptions().setExact(true)
            ).first();

            assertThat(visitHeading).isVisible();
            visitHeading.click();
        }

        assertThat(department).isVisible();

        department.selectOption(
                new SelectOption().setLabel("Cardiology")
        );

        Assert.assertEquals(
                getSelectedOptionText("Visit.DepartmentID"),
                "Cardiology",
                "Incorrect department selected"
        );

        Locator doctor = findDropdown("Visit.DoctorID");

        page.waitForCondition(
                () -> doctor.locator("option").count() > 1
        );

        doctor.selectOption(
                new SelectOption().setValue("number:3288")
        );

        assertThat(doctor).hasValue("number:3288");

        selectDropdownOption(
                "Visit.VisitTypeID",
                "New"
        );

        Assert.assertEquals(
                getSelectedOptionText("Visit.VisitTypeID"),
                "New",
                "Incorrect visit type selected"
        );

        Locator referralSource = findDropdown("Visit.PatientSourceID");

        referralSource.selectOption(
                new SelectOption().setValue("number:8")
        );

        assertThat(referralSource).hasValue("number:8");

        Locator queueField = findField("Visit.TokenNo");

        assertThat(queueField).isVisible();
        assertThat(queueField).isEnabled();

        queueField.fill(queueNumber);

        assertThat(queueField).hasValue(queueNumber);

        // Confirm the registration form has not disappeared.
        Locator firstNameField = page.locator(
                "input[ng-model='Registration.FirstName']"
        ).first();

        Assert.assertTrue(
                firstNameField.count() > 0,
                "First Name field disappeared after filling visit information"
        );

        System.out.println("Visit information filled successfully.");
    }


    // ============================================================
    // SAVE REGISTRATION
    // ============================================================

    public void save() {

        Locator saveButton =
                page.locator("#regbutton");

        assertThat(saveButton).isVisible();
        assertThat(saveButton).isEnabled();

        List<String> invalidFields =
                describeInvalidVisibleFields();

        if (!invalidFields.isEmpty()) {
            throw new AssertionError(
                    "Cannot save. Invalid visible fields: "
                            + String.join(", ", invalidFields)
            );
        }

        String expectedConfirmation =
                "Are you sure. Do you want to Save";

        AtomicReference<Dialog> handledDialog =
                new AtomicReference<>();

        Consumer<Dialog> dialogHandler = dialog -> {

            handledDialog.set(dialog);

            if ("confirm".equals(dialog.type())
                    && dialog.message().contains(expectedConfirmation)) {

                dialog.accept();

            } else {

                dialog.dismiss();
            }
        };

        page.onDialog(dialogHandler);
        StringBuilder observedApiTraffic = new StringBuilder();

        Consumer<com.microsoft.playwright.Request> requestLogger = request -> {
            if (request.url().contains("/api/")) {
                observedApiTraffic.append("\nREQUEST: ")
                        .append(request.method())
                        .append(" ")
                        .append(request.url());
            }
        };

        Consumer<Response> responseLogger = response -> {
            if (response.url().contains("/api/")) {
                observedApiTraffic.append("\nRESPONSE: HTTP ")
                        .append(response.status())
                        .append(" ")
                        .append(response.url());
            }
        };

        page.onRequest(requestLogger);
        page.onResponse(responseLogger);

        Response response;

        try {

            response = page.waitForResponse(
                    r -> {

                        String method = r.request().method();
                        String resourceType = r.request().resourceType();

                        return (
                                "POST".equalsIgnoreCase(method)
                                        || "PUT".equalsIgnoreCase(method)
                                        || "PATCH".equalsIgnoreCase(method)
                        )
                                && (
                                "xhr".equalsIgnoreCase(resourceType)
                                        || "fetch".equalsIgnoreCase(resourceType)
                        )
                                && r.url().contains(
                                "/api/patientregistration/IUD"
                        );
                    },

                    new Page.WaitForResponseOptions()
                            .setTimeout(15000),

                    () -> {

                        saveButton.click();

                        if (handledDialog.get() == null) {

                            Locator confirmationText =
                                    page.getByText(expectedConfirmation);

                            try {

                                confirmationText.waitFor(
                                        new Locator.WaitForOptions()
                                                .setTimeout(2500)
                                );

                                Locator confirmButton =
                                        page.getByRole(
                                                AriaRole.BUTTON,
                                                new Page.GetByRoleOptions()
                                                        .setName("Save")
                                                        .setExact(true)
                                        ).last();

                                assertThat(confirmButton).isVisible();

                                confirmButton.click();

                            } catch (TimeoutError ignored) {
                                // No HTML confirmation appeared.
                            }
                        }
                    }
            );

        } catch (TimeoutError timeout) {

            String validationMessages =
                    String.join(
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
                    "Timed out waiting for patient-registration API request."
                            + "\nVisible validation messages: "
                            + (validationMessages.isBlank()
                            ? "None detected"
                            : validationMessages)
                            + "\nObserved API traffic: "
                            + (observedApiTraffic.length() == 0
                            ? "No API requests detected"
                            : observedApiTraffic),
                    timeout
            );

        } finally {
            page.offDialog(dialogHandler);
            page.offRequest(requestLogger);
            page.offResponse(responseLogger);
        }

        if (!response.ok()) {
            throw new AssertionError(
                    "Registration API failed. HTTP "
                            + response.status()
                            + ", URL: " + response.url()
            );
        }

        String bodyText =
                page.locator("body").innerText();

        if (bodyText.toLowerCase().contains(
                "please select referral source")) {

            throw new AssertionError(
                    "Registration was not saved: Referral Source is required."
            );
        }

        page.waitForURL(
                "**/#/VisitScreen",
                new Page.WaitForURLOptions().setTimeout(15000)
        );

        Assert.assertTrue(
                page.url().contains("/#/VisitScreen"),
                "Visit screen did not open after saving registration"
        );

        registrationSaved = true;

        System.out.println(
                "PASS: Registration API succeeded and Visit screen opened."
        );
    }

    private List<String> describeInvalidVisibleFields() {

        Locator invalidFields =
                page.locator(
                        "input:invalid:visible, "
                                + "select:invalid:visible, "
                                + "textarea:invalid:visible"
                );

        List<String> descriptions =
                new java.util.ArrayList<>();

        for (Locator field : invalidFields.all()) {

            String description =
                    field.getAttribute("ng-model");

            if (description == null || description.isBlank()) {
                description = field.getAttribute("name");
            }

            if (description == null || description.isBlank()) {
                description = field.getAttribute("id");
            }

            if (description == null || description.isBlank()) {
                description = field.evaluate(
                        "element => element.tagName"
                ).toString();
            }

            descriptions.add(description);
        }

        return descriptions;
    }

    // ============================================================
    // NEGATIVE TEST SUPPORT
    // ============================================================

    public void clearPatientName() {

        Locator nameField = page.locator(
                "input[ng-model='Registration.FirstName']:visible"
        ).first();

        nameField.waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
        );

        nameField.fill("");

        Assert.assertEquals(
                nameField.inputValue(),
                "",
                "Patient name should be blank for the negative test"
        );
    }

    public void attemptSaveWithMissingPatientName() {

        Locator firstNameField = page.locator(
                "input[ng-model='Registration.FirstName']:visible"
        ).first();

        firstNameField.waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
        );

        Assert.assertEquals(
                firstNameField.inputValue(),
                "",
                "Patient name must be blank before attempting save"
        );

        Locator saveButton = page.locator("#regbutton");

        assertThat(saveButton).isVisible();
        saveButton.click();

        Locator nameValidation = page.getByText(
                "Please Enter First Name!",
                new Page.GetByTextOptions().setExact(true)
        );

        boolean browserValidation = (Boolean) firstNameField.evaluate(
                "element => !element.checkValidity()"
        );

        Assert.assertTrue(
                browserValidation || nameValidation.isVisible(),
                "No patient-name validation was detected"
        );

        Assert.assertFalse(
                registrationSaved,
                "Registration must not proceed without patient name"
        );
    }

    public void fillPatientInformationWithInvalidIC(String invalidIC) {

        if (invalidIC == null || invalidIC.isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid IC value must not be blank"
            );
        }

        Locator nationalId =
                findField("Registration.NationalId");

        nationalId.fill(invalidIC);
        nationalId.press("Tab");

        assertThat(nationalId).hasValue(invalidIC);
    }

    public boolean isIdentificationValidationDisplayed() {

        Locator nationalId =
                findField("Registration.NationalId");

        boolean invalidField =
                (Boolean) nationalId.evaluate(
                        "element => !element.checkValidity() "
                                + "|| element.classList.contains('ng-invalid')"
                );

        List<String> messages =
                page.locator(
                        ".text-danger:visible, "
                                + ".validation-error:visible, "
                                + ".field-validation-error:visible, "
                                + "[role='alert']:visible"
                ).allInnerTexts();

        boolean matchingMessage =
                messages.stream().anyMatch(
                        message -> message.toLowerCase().matches(
                                ".*(ic|nric|identification|national id|invalid).*"
                        )
                );

        return invalidField || matchingMessage;
    }

    public boolean isNameValidationDisplayed() {

        Locator nameField =
                page.locator(
                        "input[ng-model='Registration.FirstName']:visible"
                ).first();

        boolean browserValidation =
                (Boolean) nameField.evaluate(
                        "element => !element.checkValidity()"
                );

        boolean applicationValidation =
                page.locator(
                        ".text-danger:visible, "
                                + ".validation-error:visible, "
                                + ".field-validation-error:visible, "
                                + "[role='alert']:visible, "
                                + ".toast:visible, "
                                + ".alert:visible"
                ).allInnerTexts().stream().anyMatch(
                        message -> message.toLowerCase().contains("name")
                                || message.toLowerCase().contains("first")
                );

        return browserValidation || applicationValidation;
    }

    // ============================================================
    // REG01 - REG05 VERIFICATION METHODS
    // ============================================================

    public String getDateOfBirth() {
        return page.locator("#txtDateOfBirth")
                .inputValue().trim();
    }

    public String getAge() {
        return page.locator("#txtYear")
                .inputValue().trim();
    }

    public String getGender() {
        return getSelectedOptionText(
                "Registration.GenderID"
        );
    }

    public String getDepartment() {
        return getSelectedOptionText(
                "Visit.DepartmentID"
        );
    }

    public String getDoctor() {
        return getSelectedOptionText(
                "Visit.DoctorID"
        );
    }

    public String getVisitType() {
        return getSelectedOptionText(
                "Visit.VisitTypeID"
        );
    }

    public String getReferralSource() {
        return getSelectedOptionText(
                "Visit.PatientSourceID"
        );
    }

    public boolean areDobAgeGenderValuesCorrect() {

        boolean dobCorrect =
                getDateOfBirth().equals(expectedDob());

        boolean ageCorrect =
                getAge().matches(
                        "^" + expectedAge() + "\\b.*"
                );

        boolean genderCorrect =
                getGender().equalsIgnoreCase(expectedGender());

        return dobCorrect && ageCorrect && genderCorrect;
    }

    public boolean isRegistrationSuccessful() {
        return registrationSaved;
    }

    public boolean isVisitCreated() {
        return registrationSaved
                && page.url().contains("/#/VisitScreen");
    }

    // ============================================================
    // DEBUG GETTERS
    // ============================================================

    public String getGeneratedPatientName() {
        return patientName;
    }

    public String getGeneratedPatientNric() {
        return patientNric;
    }
}