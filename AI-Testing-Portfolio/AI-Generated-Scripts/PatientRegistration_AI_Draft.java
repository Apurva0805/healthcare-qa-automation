/*
 * AI-GENERATED DRAFT – PORTFOLIO EXAMPLE ONLY
 *
 * This is intentionally a draft to demonstrate AI output. It assumes
 * page-object methods and test-data constants that may need adjustment.
 * Do not add this file to src/test/java until it is reconciled with the
 * actual RegistrationPage.java and RegistrationTestData.java in your project.
 */

package ai.generated.draft;

import base.BaseTest;
import org.testng.annotations.Test;
import pages.RegistrationPage;
import testdata.RegistrationTestData;

public class PatientRegistration_AI_Draft extends BaseTest {

    @Test
    public void patientRegistration_shouldCreatePatient() {
        loginToDashboard();

        RegistrationPage registration = new RegistrationPage(page);

        registration.openRegistration();
        registration.fillPatientInformation();
        registration.verifyNricAutoPopulated();
        registration.fillCorrespondence();
        registration.addNextOfKin();
        registration.fillVisitInformation(RegistrationTestData.QUEUE_NUMBER);
        registration.save();

        // Draft assertion: confirm the application navigated to the Visit screen.
        assert page.url().contains("/#/VisitScreen");
    }
}
