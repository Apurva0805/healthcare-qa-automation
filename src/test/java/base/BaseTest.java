package base;

import com.microsoft.playwright.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.LoginPage;
import org.testng.ITestResult;
import java.nio.file.Paths;
import com.microsoft.playwright.Page;

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

    @AfterMethod
    public void tearDown(ITestResult result) {

        if (result.getStatus() == ITestResult.FAILURE && page != null) {
            try {
                String testName = result.getName();

                java.nio.file.Files.createDirectories(Paths.get("screenshots"));

                page.screenshot(
                        new Page.ScreenshotOptions()
                                .setPath(Paths.get("screenshots", testName + ".png"))
                                .setFullPage(true)
                );

                System.out.println(
                        "Failure screenshot saved for: " + testName
                );

            } catch (Exception e) {
                System.out.println(
                        "Could not capture screenshot: " + e.getMessage()
                );
            }
        }

        // Keep your existing browser cleanup code below this point.
        if (browser != null) {
            browser.close();
        }

        if (playwright != null) {
            playwright.close();
        }
    }
}
