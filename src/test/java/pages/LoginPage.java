package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class LoginPage {

    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public void enterUsername(String username) {
        page.locator("#username").fill(username);
    }

    public void enterPassword(String password) {
        page.locator("#password").fill(password);
    }

    public void clickLogin() {
        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Login")
        ).click();
    }

    public void handleExistingSessionPopup() {
        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("OK")
        ).click();
    }

    public void skipAccountSetup() {
        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Skip for now")
        ).click();
    }
}
