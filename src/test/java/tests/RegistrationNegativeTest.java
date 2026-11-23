
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

}
