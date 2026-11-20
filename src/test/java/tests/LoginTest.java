package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;

public class LoginTest extends BaseTest {

    @Test
    public void validLogin() {
        loginToDashboard();

        Assert.assertTrue(
                page.url().contains("/PatientDashboard"),
                "Dashboard was not displayed after login"
        );
    }

    @Test
    public void invalidLogin() {
        page.navigate("https://thomson.sancyberhad.com/Account/Login?ReturnUrl=%2f");

        LoginPage loginPage = new LoginPage(page);
        loginPage.enterUsername("Invalid");
        loginPage.enterPassword("wrongpassword");
        loginPage.clickLogin();

        if (loginPage.dismissExistingSessionPopupIfPresent()) {
            Assert.assertTrue(
                    page.locator("#Username").isVisible(),
                    "Login form should remain visible after declining session takeover"
            );
            Assert.assertFalse(
                    page.url().contains("/PatientDashboard"),
                    "Invalid login must not open the dashboard"
            );
            return;
        }

        var errorMessage =
                page.getByText("Incorrect username and/or password").first();
        errorMessage.waitFor();
        Assert.assertTrue(
                errorMessage.isVisible(),
                "Login error message was not displayed"
        );
    }
}
