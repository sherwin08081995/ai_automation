package pages;

import base.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * LoginPage.java
 * <p>
 * Purpose:
 * Page Object Model (POM) for the Login Page of the application.
 * This class handles:
 * <p>
 * ✅ Entering username/email/mobile into the login input
 * ✅ OTP-based login workflow: triggering OTP and entering values into OTP fields
 * ✅ Verification of successful login via confirmation logo
 * ✅ UI validations: subtitle check, field visibility, button status
 * ✅ Support for automation-friendly interaction via waits and retries
 * <p>
 * Related Utilities:
 * - BasePage.java (parent class providing wait and helper utilities)
 * - WaitUtils.java (explicit wait handling)
 * <p>
 * Author:
 *
 * @author Sherwin
 * @since 09-06-2025
 */


public class LoginPage extends BasePage {

    @FindBy(xpath = "//input[@id='login-id']")
    private WebElement userName;

    @FindBy(xpath = "//p[normalize-space()='Login with OTP']")
    private WebElement otpBtn;

    @FindBy(xpath = "//p[normalize-space()='Get OTP']")
    private WebElement getOtpBtn;

    @FindBy(xpath = "//p[text()='Enter OTP']/following-sibling::div//input[@inputmode='numeric' and @maxlength='1']")
    private List<WebElement> otpInputs;

    @FindBy(xpath = "//p[text()='Enter OTP']/following-sibling::div//input[@inputmode='numeric' and @maxlength='1']")
    private WebElement otpInputBox;

    @FindBy(xpath = "//h1[normalize-space()='Log into your account']")
    private WebElement loginSubtitleText;

    @FindBy(xpath = "//img[@alt='Vakilsearch']")
    private WebElement loginSuccessfulConfirmation;

    @FindBy(xpath = "//p[normalize-space()='Choose an Email to Log In']")
    private WebElement chooserHeadingContent;

    @FindBy(xpath = "//div[contains(@class,'enterpriseScrollbar') or contains(@class,'overflow-auto')]")
    private WebElement emailChooserContainer;

    @FindBy(css = "div.cursor-pointer p")
    private List<WebElement> emailOptions;


    // In LoginPage
    @FindBy(xpath = "//h2|//h3|//p[contains(.,'Choose') or contains(.,'Select')]")
    private WebElement chooserHeading;

    @FindBy(css = "svg.cursor-pointer[class*='top-[2rem]'][class*='right-[2rem]']")
    private WebElement gstrCloseButton;

    @FindBy(css = "div[role='dialog'], div[role='alertdialog']")
    private WebElement anyDialog;

    @FindBy(css = "img[alt*='vakil' i]")
    private WebElement vakilLogo;

    @FindBy(css = "img[alt*='zolvit' i]")
    private WebElement zolvitLogo;

    @FindBy(xpath = "//h1[contains(.,'Compliance') or contains(.,'Dashboard') or contains(.,'Hello')]")
    private WebElement dashboardHeader;

    @FindBy(css = "nav, [class*='sidebar'], [role='navigation']")
    private WebElement sideNav;

    // Presence marker
    @FindBy(xpath = "//p[normalize-space()='Enjoy exclusive festive savings on services today']")
    private WebElement festiveSaleBannerText;       // list -> safe "is present?" check

    // Explore Service Hub button inside the festive popup
    @FindBy(xpath = "//button[.//p[normalize-space()='Explore Service Hub']]")
    private WebElement exploreServiceHubBtn;

    // Service Hub heading text
    @FindBy(xpath = "//h1[normalize-space()='Service Hub']")
    private WebElement serviceHubText;

    // --- Page Object fields (no list) ---
    @FindBy(xpath = "//p[normalize-space()='Action Required: Profile Incomplete']")
    private WebElement profileIncompleteBannerText;


    private final By serviceHubH1By = By.xpath("//h1[normalize-space()='Service Hub']");


    private static final By CLOSE_ICON_BY = By.cssSelector("svg.absolute.top-\\[2rem\\].right-\\[2rem\\].cursor-pointer.stroke-\\[2\\]");


    public LoginPage(WebDriver driver) {
        super(driver);
    }


    /**
     * Enters the given email into the login input field.
     *
     * <p>Approach preserved:
     * <pre>wait.waitForElementToBeClickable(userName).sendKeys(emailInput)</pre>
     *
     * <p>Enhancements:
     * <ul>
     *   <li>Null / blank input guards</li>
     *   <li>Whitespace trimming (warn)</li>
     *   <li>Timing and detailed logs (info/warn/error)</li>
     * </ul>
     *
     * @param emailInput The email or mobile number to be entered.
     */
    public void enterEmail(String emailInput) {
        final String ctx = "Login email input";

        try {
            // --- basic validation (keeps same flow, just safer) ---
            if (emailInput == null) {
                logger.error("{}: provided input is null.", ctx);
                throw new IllegalArgumentException("emailInput must not be null");
            }
            final String original = emailInput;
            final String value = original.trim();
            if (!original.equals(value)) {
                logger.warn("{}: input had leading/trailing whitespace; trimming.", ctx);
            }
            if (value.isEmpty()) {
                logger.error("{}: provided input is empty after trimming.", ctx);
                throw new IllegalArgumentException("emailInput must not be blank");
            }

            logger.info("{}: typing value (masked): '{}'", ctx, maskForLogs(value));
            final long t0 = System.currentTimeMillis();

            // --- original approach preserved ---
            wait.waitForElementToBeClickable(userName).sendKeys(value);

            logger.info("{}: typed successfully in {} ms.", ctx, (System.currentTimeMillis() - t0));

        } catch (TimeoutException te) {
            logger.warn("{}: field not clickable within timeout: {}", ctx, te.toString());
            throw te;
        } catch (ElementNotInteractableException enie) {
            logger.error("{}: element not interactable: {}", ctx, enie.toString(), enie);
            throw enie;
        } catch (StaleElementReferenceException sre) {
            logger.warn("{}: element went stale during sendKeys: {}", ctx, sre.toString());
            throw sre;
        } catch (Exception e) {
            logger.error("{}: unexpected error while entering value: {}", ctx, e.toString(), e);
            throw e;
        }
    }

    /**
     * Masks emails/mobiles for logs: first 2 chars + **** + domain or last 2 digits.
     */
    private String maskForLogs(String s) {
        if (s == null) return "<null>";
        int at = s.indexOf('@');
        if (at > 0) {
            String prefix = s.substring(0, Math.min(2, at));
            String domain = s.substring(at); // includes '@'
            return prefix + "****" + domain;
        }
        if (s.length() <= 2) return "**";
        String tail = s.substring(Math.max(0, s.length() - 2));
        return "****" + tail;
    }


    /**
     * Clicks the "Login with OTP" button.
     */

    public void clickLoginWithOtpButton() {
        wait.waitForElementToBeClickable(otpBtn).click();
    }


    /**
     * Clicks the "Get OTP" button.
     *
     * <p>Approach preserved:
     * <pre>wait.waitForElementToBeClickable(getOtpBtn).click()</pre>
     *
     * <p>Enhancements:
     * <ul>
     *   <li>Null guard on the @FindBy element</li>
     *   <li>Timing + detailed logs</li>
     *   <li>Clear handling for Timeout / Interception / Stale / Unexpected errors</li>
     * </ul>
     */
    public void clickGetOtpButton() {
        final String ctx = "Get OTP button";

        try {
            if (getOtpBtn == null) {
                logger.error("{}: locator 'getOtpBtn' is null.", ctx);
                throw new IllegalStateException("getOtpBtn WebElement is null");
            }

            logger.info("{}: waiting to become clickable…", ctx);
            final long t0 = System.currentTimeMillis();

            // --- original approach preserved ---
            wait.waitForElementToBeClickable(getOtpBtn).click();

            logger.info("{}: clicked successfully in {} ms.", ctx, (System.currentTimeMillis() - t0));

        } catch (TimeoutException te) {
            logger.warn("{}: not clickable within wait timeout: {}", ctx, te.toString());
            throw te;

        } catch (ElementClickInterceptedException ecie) {
            logger.warn("{}: click intercepted (overlay/another element on top): {}", ctx, ecie.toString());
            throw ecie;

        } catch (StaleElementReferenceException sre) {
            logger.warn("{}: element went stale during click: {}", ctx, sre.toString());
            throw sre;

        } catch (ElementNotInteractableException enie) {
            logger.error("{}: element not interactable at click time: {}", ctx, enie.toString(), enie);
            throw enie;

        } catch (Exception e) {
            logger.error("{}: unexpected error while clicking: {}", ctx, e.toString(), e);
            throw e;
        }
    }


    /**
     * Enters the given OTP into the OTP input fields (starting from index 0).
     * <p>
     * Approach preserved:
     * <pre>for each digit: wait.waitForElementToBeClickable(box).clear(); box.sendKeys(digit)</pre>
     *
     * <p>Enhancements:
     * <ul>
     *   <li>Null/blank input checks, numeric validation</li>
     *   <li>otpInputs list null/size checks</li>
     *   <li>Length mismatch guard with clear error</li>
     *   <li>Per-digit timing + detailed logs (info/warn/error)</li>
     * </ul>
     *
     * @param otp A numeric string representing the OTP to be entered.
     */
    public void enterOtp(String otp) {
        final String ctx = "OTP entry";

        try {
            // -------- Input validations ----------
            if (otp == null) {
                logger.error("{}: provided OTP is null.", ctx);
                throw new IllegalArgumentException("OTP must not be null");
            }
            final String value = otp.trim();
            if (value.isEmpty()) {
                logger.error("{}: provided OTP is empty after trimming.", ctx);
                throw new IllegalArgumentException("OTP must not be blank");
            }
            if (!value.chars().allMatch(Character::isDigit)) {
                logger.error("{}: provided OTP is not numeric: length={}", ctx, value.length());
                throw new IllegalArgumentException("OTP must be a numeric string");
            }

            if (otpInputs == null) {
                logger.error("{}: locator list 'otpInputs' is null.", ctx);
                throw new IllegalStateException("otpInputs list is null");
            }
            if (otpInputs.isEmpty()) {
                logger.error("{}: no OTP input boxes found (otpInputs is empty).", ctx);
                throw new IllegalStateException("No OTP input boxes found");
            }

            if (otpInputs.size() != value.length()) {
                String msg = "Mismatch between OTP digits and input boxes. Found boxes: " + otpInputs.size() + ", OTP length: " + value.length();
                logger.error("{}: {}", ctx, msg);
                throw new RuntimeException(msg);
            }

            logger.info("{}: starting entry for {} digits (masked).", ctx, value.length());

            // -------- Type each digit ----------
            for (int i = 0; i < value.length(); i++) {
                final char digit = value.charAt(i);
                final long t0 = System.currentTimeMillis();

                try {
                    WebElement inputBox = otpInputs.get(i);
                    if (inputBox == null) {
                        logger.error("{}: input box at index {} is null.", ctx, i);
                        throw new IllegalStateException("OTP input box is null at index " + i);
                    }

                    // original approach preserved
                    WebElement clickable = wait.waitForElementToBeClickable(inputBox);
                    clickable.clear();
                    clickable.sendKeys(String.valueOf(digit));

                    long elapsed = System.currentTimeMillis() - t0;
                    logger.info("{}: typed digit index {} in {} ms.", ctx, i, elapsed);

                } catch (TimeoutException te) {
                    logger.warn("{}: timeout waiting for box {} to be clickable: {}", ctx, i, te.toString());
                    throw te;
                } catch (StaleElementReferenceException sre) {
                    logger.warn("{}: box {} went stale during entry: {}", ctx, i, sre.toString());
                    throw sre;
                } catch (ElementNotInteractableException enie) {
                    logger.error("{}: box {} not interactable: {}", ctx, i, enie.toString(), enie);
                    throw enie;
                } catch (Exception e) {
                    logger.error("{}: unexpected error at digit {}: {}", ctx, i, e.toString(), e);
                    throw e;
                }
            }

            logger.info("{}: OTP entry completed for {} digits.", ctx, value.length());

        } catch (RuntimeException re) {
            // keep behavior: propagate to caller after logging
            throw re;
        } catch (Exception e) {
            // wrap any checked exceptions just in case (shouldn’t happen with Selenium types)
            throw new RuntimeException("OTP entry failed: " + e.getMessage(), e);
        }
    }


    /**
     * Checks whether the login subtitle is correct and matches expectations.
     *
     * @return true if subtitle text matches, false otherwise.
     */

    public boolean isLoginSubtitleCorrect() {
        try {
            String expected = "Log into your account";
            Thread.sleep(2000);
            WebElement subtitleElement = wait.waitForVisibility(loginSubtitleText);
            String actual = subtitleElement.getText().trim();
            if (!expected.equals(actual)) {
                System.err.println("❌ Subtitle mismatch: Expected='" + expected + "' | Actual='" + actual + "'");
            }
            return expected.equals(actual);
        } catch (Exception e) {
            System.err.println("❌ Error verifying subtitle: " + e.getMessage());
            return false;
        }
    }


    /**
     * Checks if the email chooser dialog is currently visible.
     * <p>
     * Validations & behavior:
     * <ul>
     *   <li>Null guard on the locator field.</li>
     *   <li>Waits for visibility; logs elapsed time.</li>
     *   <li>Handles stale element once with a re-wait.</li>
     *   <li>Returns {@code true} only when displayed (and logs enabled state).</li>
     *   <li>Uses INFO for success, WARN for expected issues (timeout/missing), ERROR for unexpected failures.</li>
     * </ul>
     *
     * @return {@code true} if the chooser heading is visible; {@code false} otherwise.
     */
    public boolean isChooserOpen() {
        final String ctx = "Email chooser visibility";

        try {
            if (chooserHeadingContent == null) {
                logger.error("{}: locator 'chooserHeadingContent' is null.", ctx);
                return false;
            }

            logger.info("{}: waiting for chooser heading to become visible…", ctx);
            final long t0 = System.currentTimeMillis();

            // Primary wait
            wait.waitForVisibility(chooserHeadingContent);

            long elapsed = System.currentTimeMillis() - t0;

            boolean displayed;
            boolean enabled;
            try {
                displayed = chooserHeadingContent.isDisplayed();
                enabled = chooserHeadingContent.isEnabled();
            } catch (StaleElementReferenceException sre) {
                logger.warn("{}: heading went stale after initial wait; retrying visibility once.", ctx);
                // One retry on staleness
                wait.waitForVisibility(chooserHeadingContent);
                displayed = chooserHeadingContent.isDisplayed();
                enabled = chooserHeadingContent.isEnabled();
            }

            if (displayed) {
                logger.info("{}: visible ({} ms). Enabled={}", ctx, elapsed, enabled);
                return true;
            } else {
                logger.warn("{}: not displayed after {} ms (Enabled state unknown/false).", ctx, elapsed);
                return false;
            }

        } catch (TimeoutException te) {
            logger.warn("{}: not visible within wait timeout: {}", ctx, te.toString());
            return false;
        } catch (NoSuchElementException nse) {
            logger.warn("{}: chooser heading not present in DOM.", ctx);
            return false;
        } catch (Exception e) {
            logger.error("{}: unexpected error while checking visibility: {}", ctx, e.toString(), e);
            return false;
        }
    }


    /**
     * Selects the given email in the chooser, if the chooser is open.
     *
     * @param email the exact email text to select; must not be {@code null} or blank.
     * @return {@code true} if selection was attempted (chooser open) and post-wait ran; {@code false} if chooser was not open.
     * @throws IllegalArgumentException if {@code email} is {@code null} or blank.
     * @throws RuntimeException         if locating/clicking the email fails.
     */
    public boolean selectEmailInChooser(String email) throws InterruptedException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email to select must not be null/blank.");
        }

        if (!isChooserOpen()) {
            logger.warn("⚠️ Email chooser is not open. Skipping selection for '{}'.", email);
            return false;
        }

        final String trimmed = email.trim();
        final By emailExact = By.xpath("//p[normalize-space()='" + trimmed + "']");
        final By emailContains = By.xpath("//p[contains(normalize-space(),'" + trimmed + "')]");

        WebElement emailP;
        try {
            // Try exact first; if not present quickly, fall back to contains
            try {
                emailP = wait.waitForPresence(emailExact);
                Thread.sleep(5000);
            } catch (Exception ignored) {
                emailP = wait.waitForPresence(emailContains);
                Thread.sleep(5000);
            }

            // Click with your robust helper
            commonMethods.safeClick(driver, emailP, "Email Option: " + trimmed, 15);
            logger.info("✅ Selected email '{}' successfully from chooser.", trimmed);

            // Wait for chooser to disappear (prefer invisibility of the element we clicked)
            try {
                // If we matched by exact, wait on exact; else wait on contains
                if (driver.findElements(emailExact).isEmpty()) {
                    wait.waitForInvisibility(emailContains);
                } else {
                    wait.waitForInvisibility(emailExact);
                }
                logger.info("✅ Chooser closed after selecting email '{}'.", trimmed);
            } catch (Exception e) {
                // Not a test killer: the chooser might navigate away instead of fading
                logger.warn("⚠️ Chooser did not disappear after selecting email '{}': {}", trimmed, e.getMessage());
            }

            return true;

        } catch (Exception e) {
            logger.error("❌ Failed to select email '{}' from chooser: {}", trimmed, e.getMessage());
            throw e; // let the caller handle (screenshot/report/rethrow)
        }
    }


    /**
     * Returns true if the "Action Required: Profile Incomplete" popup banner is present & visible.
     * <p>
     * Validations & behavior:
     * <ul>
     *   <li>Null guard on the @FindBy field.</li>
     *   <li>Waits for visibility and logs elapsed time.</li>
     *   <li>Handles a single <i>stale</i> retry with a re-wait.</li>
     *   <li>INFO on success, WARN on expected issues (timeout/missing/not displayed), ERROR on unexpected failures.</li>
     * </ul>
     *
     * @return {@code true} if the banner is visible; {@code false} otherwise.
     */
    public boolean hasCloseIcon() {
        final String ctx = "Profile Incomplete popup";

        try {
            if (profileIncompleteBannerText == null) {
                logger.error("{}: locator 'profileIncompleteBannerText' is null.", ctx);
                return false;
            }

            logger.info("{}: waiting for banner to become visible…", ctx);
            final long t0 = System.currentTimeMillis();

            // Primary wait on the same @FindBy element
            wait.waitForVisibility(profileIncompleteBannerText);

            long elapsed = System.currentTimeMillis() - t0;

            boolean displayed;
            boolean enabled;
            try {
                displayed = profileIncompleteBannerText.isDisplayed();
                enabled = profileIncompleteBannerText.isEnabled();
            } catch (StaleElementReferenceException sre) {
                logger.warn("{}: banner went stale after initial wait; retrying visibility once.", ctx);
                // One retry on staleness — PageFactory proxy should re-resolve it
                wait.waitForVisibility(profileIncompleteBannerText);
                displayed = profileIncompleteBannerText.isDisplayed();
                enabled = profileIncompleteBannerText.isEnabled();
            }

            if (displayed) {
                logger.info("{}: present & visible ({} ms). Enabled={}", ctx, elapsed, enabled);
                return true;
            } else {
                logger.warn("{}: present but not displayed after {} ms. Enabled={}", ctx, elapsed, enabled);
                return false;
            }

        } catch (TimeoutException te) {
            logger.warn("{}: not visible within wait timeout: {}", ctx, te.toString());
            return false;
        } catch (NoSuchElementException nse) {
            logger.warn("{}: banner not present in DOM.", ctx);
            return false;
        } catch (Exception e) {
            logger.error("{}: unexpected error while checking visibility: {}", ctx, e.toString(), e);
            return false;
        }
    }


    /**
     * Verifies whether the login was successful by checking for the logo alt text.
     *
     * @param expectedAltText The expected logo alt text (not used, kept for future use).
     * @return true if login success logo is found, false otherwise.
     */


    public boolean isLoginSuccessful(String expectedAltText) {
        try {
            if (isChooserOpen()) return false;
            wait.waitForVisibility(loginSuccessfulConfirmation);
            String altText = loginSuccessfulConfirmation.getAttribute("alt");
            return altText != null && altText.equalsIgnoreCase(expectedAltText);
        } catch (Exception e) {
            System.err.println("❌ Login success check failed: " + e.getMessage());
            return false;
        }
    }


    /**
     * Checks whether the "Get OTP" button is visible and enabled.
     *
     * @return true if visible and enabled, false otherwise.
     */

    public boolean isSendOtpButtonVisibleAndEnabled() {
        try {
            WebElement sendOtpBtn = wait.waitForVisibility(otpBtn);
            return sendOtpBtn.isDisplayed() && sendOtpBtn.isEnabled();
        } catch (Exception e) {
            System.err.println("❌ OTP Button not visible/enabled: " + e.getMessage());
            return false;
        }
    }

    public List<WebElement> getOtpInputs() {
        return otpInputs;
    }


    /**
     * Checks whether the email/mobile input field is visible and enabled.
     *
     * @return true if visible and enabled, false otherwise.
     */

    public boolean isEmailFieldVisibleAndEnabled() {
        try {
            WebElement emailInput = wait.waitForVisibility(userName);
            return emailInput.isDisplayed() && emailInput.isEnabled();
        } catch (Exception e) {
            System.err.println("❌ Email input field not visible/enabled: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks whether the OTP field (7th box) is visible and enabled.
     *
     * @return true if visible and enabled, false otherwise.
     */

    public boolean isOtpFieldVisibleAndEnabled() {
        try {
            WebElement otpBox = wait.waitForVisibility(otpInputBox);
            return otpBox.isDisplayed() && otpBox.isEnabled();
        } catch (Exception e) {
            System.err.println("❌ OTP field not visible/enabled: " + e.getMessage());
            return false;
        }
    }


    /**
     * Attempts to close the optional top-right modal popup by clicking its close icon.
     *
     * <p>Safe to call at any time — if the icon is not present or not visible,
     * this method simply returns {@code false} and does nothing.
     *
     * @return {@code true} if a close action was performed (icon found & clicked),
     * {@code false} if no icon was present/visible.
     */
    public boolean closePopupIfPresent() {
        try {
            List<WebElement> icons = driver.findElements(CLOSE_ICON_BY);
            if (!icons.isEmpty()) {
                WebElement closeBtn = icons.get(0);
                if (closeBtn.isDisplayed()) {
                    // Try your robust click helper
                    commonMethods.safeClick(driver, closeBtn, "Popup Close Icon", 10);

                    // Best-effort: wait for the icon (or its host) to disappear
                    try {
                        wait.waitForInvisibility(closeBtn);
                    } catch (Exception ignored) {
                    }

                    logger.info("✅ Closed popup via top-right close icon successfully");
                    return true;
                }
            }
            logger.info("ℹ️ No close icon present.");
            return false;

        } catch (Exception e) {
            // Non-fatal by default (popup is optional). Flip to 'throw e;' if you want hard-fail here.
            logger.warn("⚠️ Failed to click close icon: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Returns true if the festive-sale popup heading is present and displayed.
     * <p>
     * Behavior:
     * <ul>
     *   <li>Null guard on the @FindBy element.</li>
     *   <li>Waits for visibility and logs elapsed time.</li>
     *   <li>Retries once on staleness.</li>
     *   <li>INFO on success, WARN on expected issues (timeout/missing/not displayed), ERROR on unexpected failures.</li>
     * </ul>
     *
     * @return {@code true} if the festive popup heading is visible; {@code false} otherwise.
     */
    public boolean isFestivePopupVisible() {
        final String ctx = "Festive Sale popup";

        try {
            if (festiveSaleBannerText == null) {
                logger.error("{}: locator 'festiveSaleBannerText' is null.", ctx);
                return false;
            }

            logger.info("{}: waiting for popup heading to become visible…", ctx);
            final long t0 = System.currentTimeMillis();

            // primary wait on the same @FindBy element
            wait.waitForVisibility(festiveSaleBannerText);

            final long elapsed = System.currentTimeMillis() - t0;

            boolean displayed;
            boolean enabled;
            try {
                displayed = festiveSaleBannerText.isDisplayed();
                enabled = festiveSaleBannerText.isEnabled();
            } catch (StaleElementReferenceException sre) {
                logger.warn("{}: heading went stale after initial wait; retrying visibility once.", ctx);
                // one retry on staleness; PageFactory proxy should re-resolve
                wait.waitForVisibility(festiveSaleBannerText);
                displayed = festiveSaleBannerText.isDisplayed();
                enabled = festiveSaleBannerText.isEnabled();
            }

            if (displayed) {
                logger.info("{}: present & visible ({} ms). Enabled={}", ctx, elapsed, enabled);
                return true;
            } else {
                logger.warn("{}: present but not displayed after {} ms. Enabled={}", ctx, elapsed, enabled);
                return false;
            }

        } catch (TimeoutException te) {
            logger.warn("{}: not visible within wait timeout: {}", ctx, te.toString());
            return false;
        } catch (NoSuchElementException nse) {
            logger.warn("{}: popup heading not present in DOM.", ctx);
            return false;
        } catch (Exception e) {
            logger.error("{}: unexpected error while checking visibility: {}", ctx, e.toString(), e);
            return false;
        }
    }


    /**
     * Clicks "Explore Service Hub" inside the festive popup and waits for the Service Hub page.
     * <p>
     * Behavior & validations:
     * <ul>
     *   <li>Guards against null/undisplayed button.</li>
     *   <li>Uses reusable {@code safeClick(...)} with multiple strategies and retries.</li>
     *   <li>Waits for Service Hub H1 and logs elapsed time.</li>
     *   <li>WARN on expected issues (timeout/stale), ERROR on unexpected failures.</li>
     * </ul>
     * Throws a RuntimeException if the click cannot be performed after retries (from safeClick).
     */
    public void clickExploreServiceHubFromPopup() {
        final String ctx = "Explore Service Hub (popup)";
        final int clickTimeoutSec = 5;

        try {
            if (exploreServiceHubBtn == null) {
                logger.error("{}: locator 'exploreServiceHubBtn' is null.", ctx);
                throw new IllegalStateException("exploreServiceHubBtn is null");
            }

            // Pre-flight visibility check (non-fatal; safeClick will still do its own waits)
            try {
                if (!exploreServiceHubBtn.isDisplayed()) {
                    logger.warn("{}: button not displayed yet; proceeding to safeClick anyway.", ctx);
                }
            } catch (StaleElementReferenceException sre) {
                logger.warn("{}: button went stale before clicking; safeClick will re-wait.", ctx);
            }

            logger.info("{}: attempting click with safeClick ({}s timeout)…", ctx, clickTimeoutSec);
            final long t0 = System.currentTimeMillis();

            // Use your reusable click helper (will retry with normal/Actions/JS)
            commonMethods.safeClick(driver, exploreServiceHubBtn, "Explore Service Hub", clickTimeoutSec);

            // Confirm navigation by waiting for the Service Hub H1
            logger.info("{}: click completed, waiting for Service Hub heading…", ctx);
            wait.waitForVisibility(serviceHubH1By);

            final long elapsed = System.currentTimeMillis() - t0;
            logger.info("{}: navigated to Service Hub ({} ms).", ctx, Long.valueOf(elapsed));

        } catch (TimeoutException te) {
            logger.warn("{}: Service Hub heading not visible within timeout: {}", ctx, te.toString());
            // Optional: dump element HTML to aid debugging
            try {
                String html = driver.findElement(serviceHubH1By).getAttribute("outerHTML");
                logger.warn("{}: Found candidate H1 outerHTML: {}", ctx, html);
            } catch (Exception ignore) { /* ignore */ }
            throw te; // rethrow so caller can assert/fail as needed

        } catch (StaleElementReferenceException sre) {
            logger.warn("{}: element went stale during click or confirmation: {}", ctx, sre.toString());
            throw sre;

        } catch (RuntimeException re) {
            // likely bubbled from safeClick after exhausting attempts
            logger.error("{}: click failed after retries: {}", ctx, re.toString(), re);
            throw re;

        } catch (Exception e) {
            logger.error("{}: unexpected error while clicking or confirming: {}", ctx, e.toString(), e);
            throw e;
        }
    }

    /**
     * Returns true if the Service Hub heading is present and visible.
     * <p>
     * Validations & behavior:
     * <ul>
     *   <li>Null guard on the @FindBy field.</li>
     *   <li>Waits for visibility and logs elapsed time.</li>
     *   <li>Retries once on staleness.</li>
     *   <li>INFO on success, WARN on expected issues (timeout/missing/not displayed), ERROR on unexpected failures.</li>
     * </ul>
     *
     * @return {@code true} if the Service Hub heading is visible; {@code false} otherwise.
     */
    public boolean isOnServiceHubPage() {
        final String ctx = "Service Hub heading";

        try {
            if (serviceHubText == null) {
                logger.error("{}: locator 'serviceHubText' is null.", ctx);
                return false;
            }

            logger.info("{}: waiting for heading to become visible…", ctx);
            final long t0 = System.currentTimeMillis();

            // Primary wait on the same @FindBy element
            wait.waitForVisibility(serviceHubText);

            long elapsed = System.currentTimeMillis() - t0;

            boolean displayed;
            boolean enabled;
            try {
                displayed = serviceHubText.isDisplayed();
                enabled = serviceHubText.isEnabled();
            } catch (StaleElementReferenceException sre) {
                logger.warn("{}: element went stale after initial wait; retrying visibility once.", ctx);
                // One retry on staleness
                wait.waitForVisibility(serviceHubText);
                displayed = serviceHubText.isDisplayed();
                enabled = serviceHubText.isEnabled();
            }

            if (displayed) {
                logger.info("{}: present & visible ({} ms). Enabled={}", ctx, elapsed, enabled);
                return true;
            } else {
                logger.warn("{}: present but not displayed after {} ms. Enabled={}", ctx, elapsed, enabled);
                return false;
            }

        } catch (TimeoutException te) {
            logger.warn("{}: not visible within wait timeout: {}", ctx, te.toString());
            return false;
        } catch (NoSuchElementException nse) {
            logger.warn("{}: heading not present in DOM.", ctx);
            return false;
        } catch (Exception e) {
            logger.error("{}: unexpected error while checking visibility: {}", ctx, e.toString(), e);
            return false;
        }
    }

}