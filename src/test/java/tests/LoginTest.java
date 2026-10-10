package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;

public class LoginTest extends BaseTest {
 @Test
    public void ValidLogin(){
     page.navigate("https://thomson.sancyberhad.com/Account/Login?ReturnUrl=%2f");
        LoginPage loginPage = new LoginPage(page);
        loginPage.enterUsername("Apurva");
        loginPage.enterPassword("Tcare@123");
        loginPage.clickLogin();
        loginPage.handleExistingSessionPopup();
        loginPage.skipAccountSetup();
        page.waitForURL("https://thomson.sancyberhad.com/#/PatientDashboard");
        Assert.assertTrue(page.url().contains("/PatientDashboard"),
                "TC001 FAILED: Dashboard was not displayed");
        System.out.println("Login successful and dashboard displayed.");
    }

    @Test
   public void InValidLogin(){
       page.navigate("https://thomson.sancyberhad.com/Account/Login?ReturnUrl=%2f");
       LoginPage loginPage = new LoginPage(page);
       loginPage.enterUsername("Invalid");
       loginPage.enterPassword("wrongpassword");
       loginPage.clickLogin();
       page.getByText("Invalid username or password").first().waitFor();
       Assert.assertTrue(page.getByText("Invalid username or password").first().isVisible(),
               "Error message not displaying");
    }
}
