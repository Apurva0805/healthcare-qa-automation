package tests;

import base.BaseTest;
import org.testng.annotations.Test;
import pages.RegistrationPage;

public class RegistrationTest extends BaseTest {

    @Test
    public void verifyRegistrationPage() {
        loginToDashboard();

        RegistrationPage registrationPage = new RegistrationPage(page);
        registrationPage.openRegistration();

        registrationPage.fillPatientInformation();
        registrationPage.verifyNricAutoPopulated();
        registrationPage.fillCorrespondence();
        registrationPage.addNextOfKin();
        registrationPage.fillVisitInformation("1001");
        registrationPage.save();
    }
}