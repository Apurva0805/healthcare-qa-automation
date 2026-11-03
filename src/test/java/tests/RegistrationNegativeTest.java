
package tests;

import base.BaseTest;
import org.testng.annotations.Test;
import pages.RegistrationPage;
import utils.ExcelUtils;

public class RegistrationNegativeTest extends BaseTest {

    @Test
    public void verifyPatientNameIsMandatory() {

        // Read the negative test case from Excel.
        Object[] testCase =
                ExcelUtils.getTestCaseById("TC-REG-002");

        String testCaseId = String.valueOf(testCase[0]);
        String scenario = String.valueOf(testCase[3]);

        System.out.println("Executing: " + testCaseId);
        System.out.println("Scenario: " + scenario);

        // Open registration and prepare valid supporting data.
        loginToDashboard();

        RegistrationPage registrationPage =
                new RegistrationPage(page);

        registrationPage.openRegistration();
        registrationPage.fillPatientInformation();

        // Make the required patient name blank.
        registrationPage.clearPatientName();

        registrationPage.verifyNricAutoPopulated();
        registrationPage.fillCorrespondence();
        registrationPage.addNextOfKin();
        registrationPage.fillVisitInformation("1002");

        // Attempt to save without the patient name.
        registrationPage.attemptSaveWithMissingPatientName();
    }
}
