package pages;


import base.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import utils.TestDataGenerator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Sherwin
 * @since 24-08-2025
 */

public class ReportAnIssueSection extends BasePage {


    public ReportAnIssueSection(WebDriver driver) {
        super(driver);
    }


    @FindBy(xpath = "//p[normalize-space()='Report an issue']")
    private WebElement reportAnIssueSection;

    @FindBy(xpath = "//h2[normalize-space()='Help']")
    private WebElement reportAnIssueSectionText;

    // Dropdown clickable box
    @FindBy(xpath = "//img[@alt='dropdown']")
    private WebElement modulesDropdown;

    // Currently selected value
    @FindBy(xpath = "//div[contains(@class,'css-1tdk00-singleValue')]")
    private WebElement selectedModule;

    // Options (appear when dropdown is opened)
    @FindBy(xpath = "//div[contains(@class,'option')]")
    private List<WebElement> modulesOptions;

    @FindBy(css = "div[class*='-control']")
    private WebElement dropdownControl;   // the clickable control


    @FindBy(css = "div[class*='-menu']")
    private WebElement optionsMenu;

    @FindBy(css = "div[class*='-singleValue']")
    private WebElement singleValue;

    @FindBy(xpath = "//textarea[@id='query']")
    private WebElement feedbackTextarea;

    @FindBy(css = "button.styles_sendBtn__Croyp")
    private WebElement sendButton;

    @FindBy(css = "div.styles_report__1Kc21 p")
    private WebElement confirmationMessage;

    @FindBy(xpath = "//button[normalize-space()='Got it']")
    private WebElement gotItButton;

    @FindBy(xpath = "//a[normalize-space()='support@vakilsearch.com' and starts-with(@href,'mailto:')]")
    private WebElement supportEmailLink;

    private static final String[] EXPECTED_HEADERS = {"Help", "Report an Issue"};

    private static final String[] URL_CONTAINS_ANY = {"/grc/profile/support", "/profile/support", "/support", "/report-issue"};


    public void clickReportAnIssueTab() {
        try {
            wait.waitForElementToBeClickable(reportAnIssueSection);
            logger.info("📂 Clicking Report an Issue tab to navigate...");

            try {
                commonMethods.safeClick(driver, reportAnIssueSection, "Report an Issue Tab", 10);
            } catch (Exception e) {
                logger.warn("⚠️ Standard click failed, retry with JS: {}", e.getMessage());
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", reportAnIssueSection);
            }

        } catch (StaleElementReferenceException stale) {
            logger.warn("♻️ Element went stale. Rebinding & retrying click...");
            wait.waitForElementToBeClickable(reportAnIssueSection);
            reportAnIssueSection.click();

        } catch (Exception e) {
            logger.error("❌ Failed to click Report an Issue tab: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 2) Wait until the page is ready: header visible AND URL contains any of the allowed fragments.
     */
    public boolean waitForReportAnIssueLoaded(Duration timeout) {
        try {
            wait.waitForVisibility(reportAnIssueSectionText);

            String headerText = safeGetText(reportAnIssueSectionText);
            boolean headerOk = headerMatches(headerText);

            // (b) URL contains expected snippet
            boolean urlOk = wait.until(d -> {
                try {
                    String u = d.getCurrentUrl();
                    return urlMatches(u);
                } catch (Exception ignored) {
                    return false;
                }
            });

            boolean displayed = isDisplayedSafe(reportAnIssueSectionText);

            boolean allOk = displayed && headerOk && urlOk;
            if (allOk) {
                logger.info("✅ Report an Issue / Support page loaded. Header='{}', URL='{}'", headerText, driver.getCurrentUrl());
            } else {
                if (!displayed) logger.warn("⚠️ Header element not visible.");
                if (!headerOk)
                    logger.warn("⚠️ Header check failed. Actual='{}' (expected one of {})", headerText, Arrays.toString(EXPECTED_HEADERS));
                if (!urlOk)
                    logger.warn("⚠️ URL check failed. Current='{}' (expected to contain one of {})", driver.getCurrentUrl(), Arrays.toString(URL_CONTAINS_ANY));
            }
            return allOk;

        } catch (TimeoutException te) {
            logger.error("❌ Report an Issue did not load within {} ms.", timeout.toMillis());
            return false;
        } catch (Exception e) {
            logger.error("❌ Error while waiting for Report an Issue: {}", e.getMessage());
            return false;
        }
    }

    private boolean urlMatches(String currentUrl) {
        if (currentUrl == null) return false;
        for (String frag : URL_CONTAINS_ANY) {
            if (currentUrl.contains(frag)) return true;
        }
        return false;
    }

    private boolean headerMatches(String headerText) {
        if (headerText == null) return false;
        for (String expected : EXPECTED_HEADERS) {
            if (headerText.equalsIgnoreCase(expected) || headerText.contains(expected)) return true;
        }
        return false;
    }

    private String safeGetText(WebElement el) {
        try {
            return el.getText() == null ? "" : el.getText().trim();
        } catch (StaleElementReferenceException se) {
            try {
                // use refreshed helper (5s micro-timeout or whatever you prefer)
                wait.waitForVisibilityRefreshed(el, Duration.ofSeconds(5));
                return el.getText() == null ? "" : el.getText().trim();
            } catch (Exception ignored) {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    private boolean isDisplayedSafe(WebElement el) {
        try {
            return el.isDisplayed();
        } catch (StaleElementReferenceException se) {
            try {
                return wait.waitUntilDisplayedRefreshed(el, Duration.ofSeconds(5));
            } catch (Exception ignored) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }


    public List<String> getModulesOptionTexts() {
        List<String> texts = new ArrayList<>();

        try {
            if (modulesOptions == null) {
                logger.error("Modules options list is null. Locator may be incorrect.");
                return texts;
            }

            if (modulesOptions.isEmpty()) {
                logger.warn("Modules options list is empty. Dropdown may not be expanded or no options available.");
                return texts;
            }

            for (WebElement option : modulesOptions) {
                try {
                    String text = option.getText().trim();
                    if (text.isEmpty()) {
                        logger.warn("Encountered a module option with empty text. Skipping.");
                        continue;
                    }
                    texts.add(text);
                    logger.info("Module option found: {}", text);
                } catch (StaleElementReferenceException stale) {
                    logger.error("StaleElementReferenceException while reading option text. Skipping element.", stale);
                } catch (Exception e) {
                    logger.error("Unexpected exception while reading module option text.", e);
                }
            }

        } catch (Exception e) {
            logger.error("Error while fetching module option texts.", e);
        }

        if (texts.isEmpty()) {
            logger.warn("No valid module option texts were retrieved.");
        } else {
            logger.info("Total module options collected: {}", texts.size());
        }

        return texts;
    }


    private final By optionByText(String text) {                                          // specific option
        return By.xpath("//div[contains(@class,'-menu')]//div[contains(@class,'-option') and normalize-space()=" + escapeXpath(text) + "]");
    }

    private static String escapeXpath(String s) {
        if (!s.contains("'")) return "'" + s + "'";
        if (!s.contains("\"")) return "\"" + s + "\"";
        return "concat('" + s.replace("'", "',\"'\",'") + "')";
    }


    public void openModulesDropdown() {

        try {
            logger.info("Attempting to open the modules dropdown...");
            WebElement control = wait.waitForElementToBeClickable(dropdownControl);
            if (control == null) {
                logger.error("Dropdown control was not found or not clickable.");
                return;
            }
            control.click();
            logger.info("Clicked on dropdown control.");

            // Ensure menu is visible
            wait.waitForVisibility(optionsMenu);
            logger.info("Modules dropdown menu is now visible.");

        } catch (TimeoutException te) {
            logger.error("Timeout while waiting for dropdown control or menu to be visible.", te);
        } catch (NoSuchElementException ne) {
            logger.error("Dropdown element not found on the page.", ne);
        } catch (ElementClickInterceptedException eci) {
            logger.error("Dropdown control could not be clicked (possibly obscured).", eci);
        } catch (Exception e) {
            logger.error("Unexpected error while opening modules dropdown.", e);
        }
    }

//    public void selectModuleByName(String moduleName) {
//
//        if (moduleName == null || moduleName.trim().isEmpty()) {
//            logger.error("Module name is null or empty. Cannot proceed with selection.");
//            throw new IllegalArgumentException("Module name cannot be null or empty");
//        }
//
//        try {
//            logger.info("Attempting to select module: " + moduleName);
//            if (!isElementVisible(optionsMenu)) {
//                logger.info("Options menu is not visible. Opening dropdown control...");
//                dropdownControl.click();
//                wait.waitForVisibility(optionsMenu);
//                logger.info("Options menu opened successfully.");
//            } else {
//                logger.info("Options menu is already visible.");
//            }
//            if (driver.findElements(optionByText(moduleName)).isEmpty()) {
//                logger.error("Option '{}' not found in dropdown.", moduleName);
//                throw new NoSuchElementException("Module '" + moduleName + "' not found");
//            }
//            logger.info("Clicking on module option: " + moduleName);
//            wait.waitForElementToBeClickable(optionByText(moduleName)).click();
//            wait.waitForInvisibility(optionsMenu);
//            wait.waitForTextToBePresent(singleValue, moduleName);
//            String selected = singleValue.getText().trim();
//            if (!selected.equals(moduleName)) {
//                logger.error("Module selection failed. Expected: '{}', Found: '{}'", moduleName, selected);
//                throw new IllegalStateException("Module selection validation failed");
//            }
//
//            logger.info("Module '{}' selected successfully.", moduleName);
//        } catch (Exception e) {
//            logger.error("Error selecting module '{}': {}", moduleName, e.getMessage(), e);
//            throw e; // rethrow so test fails visibly
//        }
//    }

    public void selectModuleByName(String moduleName) {

        if (moduleName == null || moduleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Module name cannot be null or empty");
        }

        By menuBy = By.cssSelector("div[class*='-menu']");
        By optionBy = optionByText(moduleName);

        try {
            logger.info("Attempting to select module: {}", moduleName);

            // 1) Ensure dropdown is open
            if (driver.findElements(menuBy).isEmpty() || !driver.findElement(menuBy).isDisplayed()) {
                logger.info("Options menu is not visible. Opening dropdown control...");
                wait.waitForElementToBeClickable(dropdownControl).click();
                wait.waitForVisibility(optionsMenu);
                logger.info("Options menu opened successfully.");
            } else {
                logger.info("Options menu is already visible.");
            }

            // 2) Locate option
            WebElement optionEl = wait.waitForVisibility(optionBy);

            // 3) Scroll option into view (IMPORTANT for last items like "Other")
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", optionEl);

            // small stabilization
            Thread.sleep(150);

            // 4) Try normal click using Actions (best for React-select)
            try {
                new org.openqa.selenium.interactions.Actions(driver)
                        .moveToElement(optionEl)
                        .pause(Duration.ofMillis(100))
                        .click()
                        .perform();
            } catch (ElementClickInterceptedException e) {
                logger.warn("Click intercepted for '{}'. Retrying with JS click...", moduleName);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", optionEl);
            }

            // 5) Wait for dropdown to close + selection to reflect
            wait.waitForInvisibility(optionsMenu);
            wait.waitForTextToBePresent(singleValue, moduleName);

            String selected = singleValue.getText().trim();
            if (!selected.equals(moduleName)) {
                throw new IllegalStateException("Module selection validation failed. Expected=" + moduleName + " Actual=" + selected);
            }

            logger.info("Module '{}' selected successfully.", moduleName);

        } catch (Exception e) {
            logger.error("Error selecting module '{}': {}", moduleName, e.getMessage(), e);
            throw new RuntimeException("Failed selecting module: " + moduleName, e);
        }
    }


    /**
     * Utility method to safely check visibility of a WebElement.
     */
    private boolean isElementVisible(WebElement element) {
        try {
            return element != null && element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    private static String quote(String s) {
        if (!s.contains("'")) return "'" + s + "'";
        if (!s.contains("\"")) return "\"" + s + "\"";
        return "concat('" + s.replace("'", "',\"'\",'") + "')";
    }


    private void smallPause() {
        try {
            TimeUnit.MILLISECONDS.sleep(150);
        } catch (InterruptedException ignored) {
        }
    }


    public String getSelectedModule() {
        if (singleValue == null) {
            logger.error("singleValue WebElement is null (PageFactory not initialized?).");
            throw new IllegalStateException("singleValue WebElement is null");
        }

        final int maxAttempts = 2;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                WebElement valueEl = wait.waitForVisibility(singleValue); // needs WebElement overload
                String text = (valueEl.getText() == null) ? "" : valueEl.getText().trim();

                if (text.isEmpty()) {
                    logger.warn("Selected module text is empty.");
                    return "";
                }

                logger.info("Selected module: '{}'", text);
                return text;

            } catch (StaleElementReferenceException sere) {
                logger.warn("StaleElementReference on attempt {}/{} while reading selected module. Retrying…", attempt, maxAttempts, sere);
                smallPause();
            } catch (TimeoutException te) {
                logger.error("Timed out waiting for selected module element to be visible.", te);
                throw te;
            } catch (RuntimeException e) {
                logger.error("Unexpected error while reading selected module.", e);
                throw e;
            }
        }

        logger.error("Failed to read selected module after {} attempts.", maxAttempts);
        return "";
    }

    /**
     * Types random feedback into the feedback textarea and returns the text used.
     */
    public String enterRandomFeedback() {
        logger.info("📝 Preparing to enter random feedback");
        wait.waitForVisibility(feedbackTextarea);
        if (!feedbackTextarea.isDisplayed()) {
            throw new IllegalStateException("Feedback textarea is not visible");
        }
        if (!"textarea".equalsIgnoreCase(feedbackTextarea.getTagName())) {
            throw new IllegalStateException("Feedback field is not a <textarea>");
        }
        String feedback = TestDataGenerator.getRandomFeedbackMessage();
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", feedbackTextarea);
        } catch (Exception ignore) {
        }
        feedbackTextarea.clear();
        feedbackTextarea.sendKeys(feedback);
        String actualValue = feedbackTextarea.getAttribute("value");
        if (actualValue == null || actualValue.isBlank()) {
            logger.error("❌ Feedback textarea has no value after typing");
            throw new IllegalStateException("Failed to type feedback");
        }

        logger.info("✅ Random feedback entered: {}", feedback);
        return feedback;
    }


    /**
     * Validates that the Send button is enabled and visible.
     */

    public boolean verifySendButtonEnabled() {
        logger.info("🔎 Checking if Send button is enabled...");

        wait.waitForVisibility(sendButton);

        boolean isVisible = sendButton.isDisplayed();
        boolean isEnabled = sendButton.isEnabled();

        if (!isVisible) {
            logger.warn("⚠️ Send button is not visible on the page.");
        }
        if (!isEnabled) {
            logger.warn("⚠️ Send button is visible but disabled.");
        }

        boolean finalState = isVisible && isEnabled;
        logger.info("📋 Send button state => visible: {}, enabled: {}, result: {}", isVisible, isEnabled, finalState);

        return finalState;
    }


    /**
     * Clicks the Send button after verifying it's enabled.
     */

    public void clickSendButton() {
        logger.info("🖱️ Attempting to click the Send button...");

        try {
            wait.waitForVisibility(sendButton);

            if (!sendButton.isDisplayed()) {
                logger.error("❌ Send button not visible on the page.");
                throw new AssertionError("Send button not visible");
            }
            if (!sendButton.isEnabled()) {
                logger.error("❌ Send button is disabled, cannot click.");
                throw new AssertionError("Send button disabled");
            }

            commonMethods.safeClick(driver, sendButton, "Send Button", 10);

            logger.info("✅ Send button clicked successfully.");
        } catch (Throwable t) {
            logger.error("❌ Failed to click Send button", t);
            throw t;
        }
    }


    /**
     * Validates the confirmation message text.
     */

    public void verifyConfirmationMessage(String expectedMessage) {
        logger.info("🔎 Verifying confirmation message...");

        if (expectedMessage == null || expectedMessage.trim().isEmpty()) {
            logger.error("❌ Expected confirmation message is null or empty");
            throw new IllegalArgumentException("Expected confirmation message cannot be null or empty");
        }
        wait.waitForVisibility(confirmationMessage);

        String actualText = confirmationMessage.getText();
        if (actualText == null || actualText.trim().isEmpty()) {
            logger.error("❌ Confirmation message is empty in UI");
            throw new AssertionError("Confirmation message is empty in UI");
        }

        String expectedNorm = expectedMessage.trim();
        String actualNorm = actualText.trim();

        if (!expectedNorm.equals(actualNorm)) {
            logger.error("❌ Confirmation message mismatch. Expected='{}', Actual='{}'", expectedNorm, actualNorm);
            throw new AssertionError("Expected confirmation message: '" + expectedNorm + "' but found: '" + actualNorm + "'");
        }

        logger.info("✅ Confirmation message displayed correctly: {}", actualNorm);
    }


    /**
     * Closes the confirmation popup by clicking "Got it".
     */
    public void closeConfirmationPopup() {
        logger.info("🖱️ Attempting to close confirmation popup...");

        wait.waitForVisibility(gotItButton);
        if (!gotItButton.isDisplayed() || !gotItButton.isEnabled()) {
            logger.error("❌ 'Got it' button not ready for click.");
            throw new AssertionError("'Got it' button not clickable");
        }

        gotItButton.click();
        logger.info("✅ Confirmation popup closed successfully.");
    }


    public String getFeedbackFieldValue() {
        try {
            String value = feedbackTextarea.getAttribute("value");

            if (value == null) {
                logger.error("❌ Feedback field value is null");
                throw new AssertionError("Feedback field value returned null");
            }

            if (value.trim().isEmpty()) {
                logger.error("❌ Feedback field value is empty");
                throw new AssertionError("Feedback field is empty after entering feedback");
            }

            logger.info("✅ Retrieved feedback field value: {}", value);
            return value;

        } catch (Exception e) {
            logger.error("❌ Unable to retrieve feedback field value", e);
            throw e;
        }
    }


    /**
     * Returns the visible text of the support email link.
     */
    public String getSupportEmailLinkText() {
        wait.waitForVisibility(supportEmailLink);
        String txt = supportEmailLink.getText();
        return (txt == null) ? "" : txt.trim();
    }

    /**
     * Returns the href of the support email link.
     */
    public String getSupportEmailLinkHref() {
        wait.waitForVisibility(supportEmailLink);
        String href = supportEmailLink.getAttribute("href");
        return (href == null) ? "" : href.trim();
    }

    /**
     * Clean POM validation: no screenshots/Allure here (as you requested).
     */
    public void verifySupportEmailLink(String expectedEmail) {
        if (expectedEmail == null || expectedEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Expected email cannot be null or empty");
        }

        wait.waitForVisibility(supportEmailLink);

        if (!supportEmailLink.isDisplayed()) {
            throw new AssertionError("Support email hyperlink is not visible");
        }

        String actualText = getSupportEmailLinkText();
        if (!actualText.equals(expectedEmail.trim())) {
            throw new AssertionError("Link text mismatch. Expected='" + expectedEmail + "' Actual='" + actualText + "'");
        }

        String href = getSupportEmailLinkHref();
        String expectedHrefPrefix = "mailto:" + expectedEmail.trim();
        if (!href.toLowerCase().startsWith(expectedHrefPrefix.toLowerCase())) {
            throw new AssertionError("Link href mismatch. Expected href to start with '" + expectedHrefPrefix + "' but was '" + href + "'");
        }

        logger.info("✅ Support email link verified. text='{}', href='{}'", actualText, href);
    }


    /**
     * Clicks the support email link safely.
     */
    public void clickSupportEmailLink() {
        wait.waitForVisibility(supportEmailLink);
        if (!supportEmailLink.isDisplayed() || !supportEmailLink.isEnabled()) {
            throw new AssertionError("Support email link not visible or not clickable");
        }
        supportEmailLink.click();
        logger.info("✅ Clicked support email hyperlink.");
    }


}


