package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.Assert;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

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

    private Locator model(String ngModel) {
        return page.locator(
                "[ng-model='" + ngModel + "']:visible"
        ).first();
    }

    private void fillIfPresent(String ngModel, String value) {
        Locator field = page.locator(
                "[ng-model='" + ngModel + "']:visible"
        ).first();

        if (field.count() > 0 && field.isVisible() && field.isEnabled()) {
            field.fill(value);
        } else {
            System.out.println(
                    "Field not found or unavailable: " + ngModel
            );
        }
    }

    private void choose(String ngModel, String requestedText) {
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

    private void chooseFirstAvailable(String ngModel) {
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

    private String selectedLabel(String ngModel) {
        Locator dropdown = page.locator(
                "select[ng-model='" + ngModel + "']:visible"
        ).first();

        return dropdown.locator("option:checked")
                .innerText()
                .trim();
    }

    private Locator button(String name) {
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

        choose("Registration.PrefixID", male ? "Mr" : "Ms");

        // Identification type.
        choose("Registration.ICCardTypeID", "New IC");

        // Enter NRIC and trigger the application's change event.
        Locator nricField = model("Registration.NationalId");

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
        choose("Registration.GenderID", expectedGender);

        // Nationality and personal details.
        choose("Registration.NationalityID", "Malaysian");
        choose("Registration.ReligionID", "Islam");
        choose("Registration.RaceID", "Malay");
        choose("Registration.MaritalStatusID", "Single");

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
                selectedLabel("Registration.GenderID");

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

        fillIfPresent("Registration.ResAddress", "12 Jalan Automation");
        fillIfPresent("Registration.ResHouseNo", "12");
        fillIfPresent("Registration.ResStreet", "Taman Testing");
        fillIfPresent("Registration.ResPinCode", "47500");

        choose("Registration.ResStateID", "Selangor");

        // City is dependent on State.
        Locator city = page.locator(
                "select[ng-model='Registration.ResCityID']:visible"
        ).first();

        city.waitFor();

        page.waitForCondition(
                () -> city.locator("option").count() > 1
        );

        chooseFirstAvailable("Registration.ResCityID");
        choose("Registration.ResCountryID", "Malaysia");

        fillIfPresent(
                "Registration.MobileNo",
                "0123456789"
        );

        fillIfPresent(
                "Registration.Email",
                "auto." + stamp + "@example.com"
        );
    }

    // ------------------------------------------------------------
    // 5. Next of Kin
    // ------------------------------------------------------------


    public void addNextOfKin() {

        String kinName = "AUTOTESTKIN";

        choose("Registration.KinTitleID", male ? "Ms" : "Mr");

        fillIfPresent("Registration.KinName", kinName);

        // Select identification type
        choose("Registration.NOKnationalid", "Malaysian");

        fillIfPresent(
                "Registration.KinNationalId",
                kinNric
        );

        choose("Registration.KinRelationID", "Guardian");

        fillIfPresent(
                "Registration.KinMobileNo",
                "0123456788"
        );

        fillIfPresent(
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
        Locator addButton = button("Add").first();

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

        Locator doctor = page.locator("select[ng-model='Visit.DoctorID']");
        page.waitForCondition(
                () -> doctor.locator("option").count() > 1
        );

        doctor.selectOption(
                new SelectOption().setValue("number:3288")
        );
        assertThat(doctor).hasValue("number:3288");

        choose("Visit.VisitTypeID", "New");
        if (!selectedLabel("Visit.VisitTypeID").equalsIgnoreCase("New")) {
            throw new AssertionError("New visit type was not selected");
        }

        Locator referralSource =
                page.locator("select[ng-model='Visit.PatientSourceID']");
        referralSource.selectOption(
                new SelectOption().setValue("number:8")
        );
        assertThat(referralSource).hasValue("number:8");

        Locator queueNo = page.locator("input[ng-model='Visit.TokenNo']");
        queueNo.waitFor();
        assertThat(queueNo).isVisible();
        assertThat(queueNo).isEnabled();
        queueNo.fill(queueNumber);
        // Move focus so the form processes the new queue number.
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

        saveButton.click();

        Locator confirmationPrompt =
                page.getByText("Are you sure. Do you want to Save");
        confirmationPrompt.waitFor();

        Locator confirmSaveButton = page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Save")
                        .setExact(true)
        ).last();
        assertThat(confirmSaveButton).isVisible();
        Response saveResponse = page.waitForResponse(
                response -> {
                    String method = response.request().method();
                    String resourceType = response.request().resourceType();
                    return ("POST".equalsIgnoreCase(method)
                            || "PUT".equalsIgnoreCase(method)
                            || "PATCH".equalsIgnoreCase(method))
                            && ("xhr".equalsIgnoreCase(resourceType)
                            || "fetch".equalsIgnoreCase(resourceType))
                            && response.url().contains(
                            "/api/patientregistration/IUD"
                    );
                },
                confirmSaveButton::click
        );
        confirmationPrompt.waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.HIDDEN)
        );

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
}
