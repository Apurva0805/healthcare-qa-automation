package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.RegistrationPage;
import testdata.RegistrationTestData;

public class RegistrationTest extends BaseTest {

    // REG01 - Valid patient registration
    @Test(priority = 1, description = "REG01 - Valid patient registration")
    public void validPatientRegistration() {

        loginToDashboard();

        RegistrationPage registration = new RegistrationPage(page);

        registration.openRegistration();
        registration.fillPatientInformation();
        registration.verifyNricAutoPopulated();
        registration.fillCorrespondence();
        registration.addNextOfKin();

        registration.fillVisitInformation(
                RegistrationTestData.QUEUE_NUMBER
        );

        registration.save();

        Assert.assertTrue(
                registration.isRegistrationSuccessful(),
                "REG01 FAILED: Patient registration was not successful"
        );
    }

    // REG02 - Mandatory patient name validation
    @Test(priority = 2, description = "REG02 - Mandatory patient name validation")
    public void mandatoryPatientNameValidation() {

        loginToDashboard();

        RegistrationPage registration = new RegistrationPage(page);

        registration.openRegistration();
        registration.fillPatientInformation();
        registration.clearPatientName();
        registration.attemptSaveWithMissingPatientName();

        Assert.assertTrue(
                registration.isNameValidationDisplayed(),
                "REG02 FAILED: Patient name validation was not displayed"
        );

        Assert.assertFalse(
                registration.isRegistrationSuccessful(),
                "REG02 FAILED: Registration should not succeed without a patient name"
        );
    }

    // REG03 - Invalid identification number
    @Test(priority = 3, description = "REG03 - Invalid identification number")
    public void invalidIdentificationNumber() {

        loginToDashboard();

        RegistrationPage registration = new RegistrationPage(page);

        registration.openRegistration();
        registration.fillPatientInformation();

        registration.fillPatientInformationWithInvalidIC(
                RegistrationTestData.INVALID_IC
        );

        Assert.assertTrue(
                registration.isIdentificationValidationDisplayed(),
                "REG03 FAILED: Invalid identification number was not rejected"
        );

        // Attempt to save with invalid IC
        page.locator("#regbutton").click();

        Assert.assertTrue(
                registration.isIdentificationValidationDisplayed(),
                "REG03 FAILED: Invalid IC validation was not displayed after Save"
        );

        Assert.assertFalse(
                registration.isRegistrationSuccessful(),
                "REG03 FAILED: Registration should not succeed with invalid IC"
        );

        Assert.assertFalse(
                registration.isVisitCreated(),
                "REG03 FAILED: Visit should not be created with invalid IC"
        );
    }

    // REG04 - DOB, age and gender validation
    @Test(priority = 4, description = "REG04 - DOB, age and gender validation")
    public void dobAgeGenderValidation() {

        loginToDashboard();

        RegistrationPage registration = new RegistrationPage(page);

        registration.openRegistration();
        registration.fillPatientInformation();
        registration.verifyNricAutoPopulated();

        Assert.assertTrue(
                registration.areDobAgeGenderValuesCorrect(),
                "REG04 FAILED: DOB, age or gender does not match the NRIC"
        );
    }

    // REG05 - Visit information validation and creation
    @Test(priority = 5, description = "REG05 - Visit information validation and creation")
    public void visitInformationCreation() {

        loginToDashboard();

        RegistrationPage registration = new RegistrationPage(page);

        registration.openRegistration();
        registration.fillPatientInformation();
        registration.verifyNricAutoPopulated();
        registration.fillCorrespondence();
        registration.addNextOfKin();

        registration.fillVisitInformation(
                RegistrationTestData.QUEUE_NUMBER
        );

        Assert.assertEquals(
                registration.getDepartment(),
                "Cardiology",
                "REG05 FAILED: Incorrect department selected"
        );

        Assert.assertTrue(
                !registration.getDoctor().isBlank(),
                "REG05 FAILED: Doctor was not selected"
        );

        Assert.assertEquals(
                registration.getVisitType(),
                "New",
                "REG05 FAILED: Incorrect visit type"
        );

        Assert.assertTrue(
                !registration.getReferralSource().isBlank(),
                "REG05 FAILED: Referral source was not selected"
        );

        registration.save();

        Assert.assertTrue(
                registration.isVisitCreated(),
                "REG05 FAILED: Visit screen was not displayed"
        );
    }

    // REG07 - Invalid email validation
    @Test(priority = 7, description = "REG07 - Invalid email validation")
    public void invalidEmailValidation() {

        loginToDashboard();

        RegistrationPage registration = new RegistrationPage(page);

        registration.openRegistration();
        registration.fillPatientInformation();
        registration.fillCorrespondence();

        registration.fillInvalidEmail("invalid-email");

        Assert.assertTrue(
                registration.isEmailValidationDisplayed(),
                "REG07 FAILED: Invalid email was not rejected"
        );

        Assert.assertFalse(
                registration.isRegistrationSuccessful(),
                "REG07 FAILED: Registration should not succeed with invalid email"
        );
    }
}