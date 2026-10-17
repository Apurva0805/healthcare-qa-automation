package base;

import com.microsoft.playwright.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.LoginPage;

public class BaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext browserContext;
    protected Page page;

    @BeforeMethod
    public void setup() {

        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
        );

        browserContext = browser.newContext();
        page = browserContext.newPage();
    }

    // Login and open Dashboard
    protected void loginToDashboard() {

        page.navigate("https://thomson.sancyberhad.com/Account/Login?ReturnUrl=%2f");

        LoginPage loginPage = new LoginPage(page);

        loginPage.enterUsername("Apurva");
        loginPage.enterPassword("Tcare@123");
        loginPage.clickLogin();
        loginPage.handleExistingSessionPopup();
        loginPage.skipAccountSetup();

        page.waitForURL("**/PatientDashboard");

        System.out.println("Login successful. Dashboard displayed.");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        if (browserContext != null) {browserContext.close();}
        if (browser != null) {browser.close();}
        if (playwright != null) {playwright.close();}
    }
}
