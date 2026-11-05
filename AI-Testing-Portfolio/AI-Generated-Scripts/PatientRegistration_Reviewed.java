/*
 * HUMAN-REVIEWED REFERENCE – PORTFOLIO EXAMPLE ONLY
 *
 * This version demonstrates safer TestNG assertions and clearer test intent.
 * It still depends on the exact methods/constants in your current project.
 * Keep the working implementation in your existing RegistrationTest.java.
 */

package ai.generated.reviewed;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.RegistrationPage;
import testdata.RegistrationTestData;

public class PatientRegistration_Reviewed extends BaseTest {

    @Test
    public void REG01_validPatientRegistration_shouldOpenVisitScreen() {
        loginToDashboard();

        RegistrationPage registration = new RegistrationPage(page);

        registration.openRegistration();
        registration.fillPatientInformation();

        Assert.assertTrue(
                registration.areDobAgeGenderValuesCorrect(),
                "DOB, age, and gender should be correctly derived from the identification number."
        );

        registration.fillCorrespondence();
        registration.addNextOfKin();
        registration.fillVisitInformation(RegistrationTestData.QUEUE_NUMBER);
        registration.save();

        Assert.assertTrue(
                registration.isRegistrationSuccessful(),
                "Patient registration should be saved successfully."
        );

        Assert.assertTrue(
                registration.isVisitCreated(),
                "The application should open the Visit screen after registration."
        );
    }
}
