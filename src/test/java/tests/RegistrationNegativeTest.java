
package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.RegistrationPage;
import utils.ExcelUtils;

public class RegistrationNegativeTest extends BaseTest {


    @Test
    public void verifyPatientNameIsMandatory() {

        Object[] testCase =
                ExcelUtils.getTestCaseById("TC-REG-002");

        String testCaseId = String.valueOf(testCase[0]);
        String scenario = String.valueOf(testCase[3]);

        System.out.println("Executing: " + testCaseId);
        System.out.println("Scenario: " + scenario);

        loginToDashboard();

        RegistrationPage registrationPage =
                new RegistrationPage(page);

        registrationPage.openRegistration();
        registrationPage.fillPatientInformation();
        registrationPage.fillCorrespondence();

        registrationPage.clearPatientName();
        registrationPage.attemptSaveWithMissingPatientName();

        Assert.assertTrue(
                registrationPage.isNameValidationDisplayed(),
                "FAIL: Mandatory patient-name validation was not displayed."
        );

        Assert.assertFalse(
                registrationPage.isRegistrationSuccessful(),
                "FAIL: Registration should not succeed with a blank patient name."
        );

        System.out.println(
                "PASS: Mandatory patient-name validation verified."
        );
    }


    @Test
    public void verifyInvalidIdentificationNumber() {

        Object[] testCase =
                ExcelUtils.getTestCaseById("TC-REG-003");

        String testCaseId = String.valueOf(testCase[0]);
        String scenario = String.valueOf(testCase[3]);

        System.out.println("Executing: " + testCaseId);
        System.out.println("Scenario: " + scenario);

        loginToDashboard();

        RegistrationPage registrationPage =
                new RegistrationPage(page);

        registrationPage.openRegistration();
        registrationPage.fillPatientInformation();

        // Replace the generated valid NRIC with invalid data.
        registrationPage.fillPatientInformationWithInvalidIC("123");

        Assert.assertTrue(
                registrationPage.isIdentificationValidationDisplayed(),
                "Invalid NRIC validation was not displayed."
        );

        System.out.println(
                "PASS: Invalid identification validation verified."
        );
    }


    @Test
    public void verifyInvalidEmailValidation() {

        Object[] testCase =
                ExcelUtils.getTestCaseById("TC-REG-004");

        String testCaseId = String.valueOf(testCase[0]);
        String scenario = String.valueOf(testCase[3]);

        System.out.println("Executing: " + testCaseId);
        System.out.println("Scenario: " + scenario);

        loginToDashboard();

        RegistrationPage registrationPage =
                new RegistrationPage(page);

        registrationPage.openRegistration();
        registrationPage.fillPatientInformation();
        registrationPage.fillCorrespondence();

        registrationPage.fillInvalidEmail("invalid-email");

        Assert.assertTrue(
                registrationPage.isEmailValidationDisplayed(),
                "Invalid email validation was not detected."
        );

        System.out.println(
                "PASS: Invalid email validation verified."
        );
    }


    @Test
    public void verifyMobileNumberMaximumLength() {

        loginToDashboard();

        RegistrationPage registrationPage =
                new RegistrationPage(page);

        registrationPage.openRegistration();
        registrationPage.fillPatientInformation();
        registrationPage.fillCorrespondence();

        registrationPage.enterMobileNumber("1234567890123456");

        int actualLength =
                registrationPage.getMobileNumberLength();

        Assert.assertTrue(
                actualLength <= 15,
                "Mobile number exceeds maximum length of 15. Actual: "
                        + actualLength
        );

        System.out.println(
                "PASS: Mobile number length = " + actualLength
        );
    }




}
