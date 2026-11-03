
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
                page.url().contains("/#/VisitScreen"),
                "REG01 FAILED: Patient registration was not successful"
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
                "REG03 FAILED: Identification validation was not displayed"
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
    }

}
