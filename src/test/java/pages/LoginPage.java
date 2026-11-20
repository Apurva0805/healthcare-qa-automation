package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.util.List;

public class LoginPage {

    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public void enterUsername(String username) {
        page.locator("#Username").fill(username);
    }

    public void enterPassword(String password) {
        page.locator("#Password").fill(password);
    }

    public void selectLoginContext() {
        selectFirstAvailableOption("#LocationID");
        selectFirstAvailableOption("#CashCounterID");
        selectFirstAvailableOption("#CostCenterID");
    }

    private void selectFirstAvailableOption(String selector) {
        Locator dropdown = page.locator(selector);
        Locator options = dropdown.locator("option:not([value=''])");
        options.first().waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.ATTACHED)
                        .setTimeout(10000)
        );

        List<Locator> availableOptions = options.all();
        for (Locator option : availableOptions) {
            String value = option.getAttribute("value");
            if (value != null && !value.isBlank() && option.isEnabled()) {
                dropdown.selectOption(value);
                dropdown.press("Tab");
                return;
            }
        }

        throw new IllegalStateException(
                "No selectable option found for login field: " + selector
        );
    }

    public void clickLogin() {
        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Login with MFA")
        ).click();
    }

    public void handleExistingSessionPopup() {
        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("OK")
        ).click();
    }

    public boolean dismissExistingSessionPopupIfPresent() {
        Locator cancelButton = page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("CANCEL")
        );
        try {
            cancelButton.waitFor(
                    new Locator.WaitForOptions().setTimeout(5000)
            );
        } catch (TimeoutError ignored) {
            return false;
        }

        cancelButton.click();
        return true;
    }

    public void skipAccountSetup() {
        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Skip for now")
        ).click();
    }
}
