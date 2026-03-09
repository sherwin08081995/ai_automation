package pages;


import base.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sherwin
 * @since 27-08-2025
 */

public class CustomerProfilePanel extends BasePage {

    public CustomerProfilePanel(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "(//header//button[.//img[@alt='avatar' or contains(@src,'/profile/Personal.svg')]])[1]")
    private WebElement profileIcon;

    @FindBy(xpath = "//p[normalize-space()='View Profile']")
    private WebElement viewProfileLink;

    @FindBy(xpath = "//h2[@class='font-semibold truncate']")
    private WebElement nameText;

    @FindBy(xpath = "(//div[@class='styles_emailPhoneSection__0nMhC'])[1]")
    private WebElement mobileText;

    @FindBy(xpath = "(//div[@class='styles_emailPhoneSection__0nMhC'])[2]")
    private WebElement emailText;

    @FindBy(xpath = "//section[contains(@class,'userManageable')]//section[contains(@class,'restProfileContainer')]//div[contains(@class,'restProfile__')]")
    private java.util.List<WebElement> menuItemsRows;


    @FindBy(xpath = "//p[normalize-space()='My Services']")
    private WebElement myServicesOption;

    @FindBy(xpath = "//p[normalize-space()='My Business']")
    private WebElement myBusinessOption;

    @FindBy(xpath = "//p[normalize-space()='Help']")
    private WebElement helpOption;

    @FindBy(xpath = "//p[normalize-space()='FAQs']")
    private WebElement faqsOption;

    @FindBy(xpath = "//p[normalize-space()='Log out']")
    private WebElement logoutOption;

    // ===== My Business anchors =====
    // Case-insensitive match for "Business that you are part of"
    @FindBy(xpath = "//*[contains(translate(normalize-space(.)," + " 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')," + " 'business that you are part of')]")
    private WebElement myBusinessSubheading;

    // ===== My Services anchors =====
    // Tabs row that contains all four labels
    @FindBy(xpath = "//*[" + ".//*[normalize-space()='Active'] and " + ".//*[normalize-space()='Pending'] and " + ".//*[normalize-space()='Completed'] and " + ".//*[normalize-space()='Closed']" + "]")
    private WebElement myServicesTabsRow;

    // Optional individual tabs if you need them later
    @FindBy(xpath = "//*[normalize-space()='Active']")
    private WebElement myServicesActiveTab;

    @FindBy(xpath = "//*[normalize-space()='Unpaid']")
    private WebElement myServicesUnpaidTab;

    @FindBy(xpath = "//*[normalize-space()='Completed']")
    private WebElement myServicesCompletedTab;

    @FindBy(xpath = "//*[normalize-space()='Closed']")
    private WebElement myServicesClosedTab;

    @FindBy(xpath = "//p[normalize-space()='My Quotations']")
    private WebElement myQuotationsOption;

    @FindBy(xpath = "//p[normalize-space()='My Quotations']")
    private WebElement myQuotationsHeading;

    @FindBy(xpath = "//p[normalize-space()='My Subscriptions']")
    private WebElement mySubscriptionOption;

    @FindBy(xpath = "//h1[normalize-space()='My Subscriptions']")
    private WebElement mySubscriptionHeading;



    /**
     * Checks whether the Profile icon is visible on the page.
     * <p>
     * This method attempts to call {@code isDisplayed()} on the Profile icon element.
     * If the element is found and visible, it logs the status and returns {@code true}.
     * If the element is not present in the DOM or becomes stale, it logs the exception type
     * and safely returns {@code false} instead of failing the test immediately.
     * </p>
     *
     * @return {@code true} if the Profile icon is displayed on the page,
     * {@code false} if the element is not found or not visible
     */
    public boolean isProfileIconVisible() {
        try {
            boolean visible = profileIcon.isDisplayed();
            logger.info("Profile icon visibility: {}", visible);
            return visible;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            logger.warn("Profile icon not visible: {}", e.getClass().getSimpleName());
            return false;
        }
    }


    public void clickProfileIcon() {
        final String name = "Profile icon";
        final int timeoutSec = 5; // tweak if you want

        logger.info("Clicking {} via safeClick ({}s timeout)", name, timeoutSec);
        commonMethods.safeClick(driver, profileIcon, name, timeoutSec);
        logger.info("{} click request completed", name);
    }


    /**
     * Checks whether the Customer Profile panel is currently open.
     * <p>
     * Attempts to verify that {@code viewProfileLink} is displayed.
     * Logs the open state if successful. If the element is missing or stale,
     * logs the exception type and safely returns {@code false}.
     * </p>
     *
     * @return {@code true} if the Customer Profile panel is open,
     * {@code false} otherwise
     */
    public boolean isOpen() {
        try {
            boolean open = viewProfileLink.isDisplayed();
            logger.info("Customer Profile panel open state: {}", open);
            return open;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            logger.error("Customer Profile panel not open: {}", e.getClass().getSimpleName());
            return false;
        }
    }

    // --- small safe getters ---
    public String getDisplayedName() {
        try {
            return nameText.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getDisplayedMobile() {
        try {
            return mobileText.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getDisplayedEmail() {
        try {
            return emailText.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    // Optional visibility checks (used if you want explicit asserts)
    public boolean isNameVisible() {
        try {
            return nameText.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isMobileVisible() {
        try {
            return mobileText.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmailVisible() {
        try {
            return emailText.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Normalizes a phone number into a consistent digit-only format.
     * <ul>
     *   <li>Removes all non-digit characters.</li>
     *   <li>Trims leading zeros (keeps at least one digit).</li>
     *   <li>If longer than 10 digits (e.g., includes country code), keeps the last 10.</li>
     * </ul>
     * <p>
     * Examples:
     * <pre>
     * "+91 81484 38075" -> "8148438075"
     * "08148438075"     -> "8148438075"
     * "1234567890123"   -> "3456789012" (last 10 digits)
     * </pre>
     *
     * @param s the raw phone number string
     * @return normalized phone number with up to 10 digits, or empty string if input is null/blank
     */

    public String normalizePhone(String s) {
        if (s == null) return "";
        String digits = s.replaceAll("\\D", "");
        while (digits.startsWith("0") && digits.length() > 1) {
            digits = digits.substring(1);
        }
        if (digits.length() > 10) {
            digits = digits.substring(digits.length() - 10);
        }
        return digits;
    }

    /**
     * Normalizes a menu/item label for robust comparison.
     * <ul>
     *   <li>Trims leading/trailing whitespace.</li>
     *   <li>Collapses multiple spaces into one.</li>
     *   <li>Converts to lowercase.</li>
     *   <li>Removes arrow/chevron characters like '>' or '›'.</li>
     * </ul>
     * <p>
     * Examples:
     * <pre>
     * "  My   Services " -> "my services"
     * "FAQs ›"           -> "faqs"
     * </pre>
     *
     * @param s the raw label
     * @return normalized label string, or empty string if input is null
     */
    public String normalizeLabel(String s) {
        if (s == null) return "";
        String t = s.trim().replaceAll("\\s+", " ").toLowerCase();
        t = t.replace(">", "").replace("›", "").trim();
        return t;
    }


    /**
     * Masks a phone number for safe logging.
     * <ul>
     *   <li>If null or empty, returns "***".</li>
     *   <li>Replaces all but the last 3 digits with asterisks and a fixed "****" prefix.</li>
     * </ul>
     * <p>
     * Examples:
     * <pre>
     * "8148438075" -> "****075"
     * "12345"      -> "****345"
     * </pre>
     *
     * @param number the raw/normalized phone number
     * @return masked phone number safe for logging
     */
    public String maskPhone(String number) {
        if (number == null || number.isEmpty()) return "***";
        int keep = Math.min(3, number.length());
        return "****" + number.substring(number.length() - keep);
    }

    /**
     * Masks an email address for safe logging.
     * <ul>
     *   <li>If null or missing '@', returns "***".</li>
     *   <li>Keeps the first character of the local-part, replaces the rest with "***".</li>
     *   <li>Leaves the domain part intact.</li>
     * </ul>
     * <p>
     * Examples:
     * <pre>
     * "sherwinzolvit360@yopmail.com" -> "s***@yopmail.com"
     * "a@b.com"                      -> "a***@b.com"
     * </pre>
     *
     * @param email the raw email address
     * @return masked email safe for logging
     */
    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        String[] parts = email.split("@", 2);
        return parts[0].charAt(0) + "***@" + parts[1];
    }

    /**
     * Returns the visible left-menu items in DOM order.
     *
     * <p><b>Behavior & validation</b></p>
     * <ul>
     *   <li>Collects text from each {@code menuItemsRows} element, preserving order.</li>
     *   <li>Normalizes whitespace and skips blank/empty entries.</li>
     *   <li>Falls back to {@code innerText} if {@code getText()} is empty.</li>
     *   <li>Logs:
     *     <ul>
     *       <li>{@code INFO} – start/end summary with counts (and final list)</li>
     *       <li>{@code WARN} – empty/blank text for a row, stale elements</li>
     *       <li>{@code ERROR} – if no items could be collected</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * @return a list of menu item labels in DOM order (may be empty if none found)
     */
    public List<String> getMenuItemsInOrder() {
        List<String> items = new ArrayList<>();

        if (menuItemsRows == null) {
            logger.error("getMenuItemsInOrder: menuItemsRows is null; returning empty list.");
            return items;
        }

        logger.info("getMenuItemsInOrder: collecting menu items in DOM order (candidates: {}).", menuItemsRows.size());

        int index = -1;
        for (WebElement el : menuItemsRows) {
            index++;
            try {
                String text = el.getText();
                if (text == null || text.isBlank()) {
                    logger.warn("getMenuItemsInOrder: empty text at index {} via getText(); trying innerText.", index);
                    text = el.getAttribute("innerText");
                }

                if (text != null) {
                    text = text.trim().replaceAll("\\s+", " ");
                }

                if (text == null || text.isBlank()) {
                    logger.warn("getMenuItemsInOrder: skipping blank/empty item at index {}.", index);
                    continue;
                }

                items.add(text);
            } catch (StaleElementReferenceException sere) {
                logger.warn("getMenuItemsInOrder: stale element at index {}; skipping. Msg: {}", index, sere.getMessage());
            } catch (Exception e) {
                logger.error("getMenuItemsInOrder: failed to read item at index {}. Msg: {}", index, e.getMessage(), e);
            }
        }

        if (items.isEmpty()) {
            logger.error("getMenuItemsInOrder: collected 0 items from {} candidates.", menuItemsRows.size());
        } else {
            logger.info("getMenuItemsInOrder: collected {} item(s): {}", items.size(), items);
        }

        return items;
    }


    /**
     * Checks whether any element containing the given text is currently <em>visible</em> on the page.
     *
     * @param expectedText the text to match (exact after {@code normalize-space(.)}); must not be null/blank
     * @return {@code true} if a visible matching element is present; otherwise {@code false}
     * @since 04-09-2025
     */
    public boolean isHeadingOrTextVisible(String expectedText) {
        // 1) Basic input validation
        if (expectedText == null) {
            logger.warn("isHeadingOrTextVisible: expectedText is null");
            return false;
        }
        String trimmed = expectedText.trim();
        if (trimmed.isEmpty()) {
            logger.warn("isHeadingOrTextVisible: expectedText is blank");
            return false;
        }

        // 2) Exact (case-sensitive) match first
        String exactXpath = "//*[normalize-space(.)=" + toXPathLiteral(trimmed) + "]";

        try {
            List<WebElement> matches = driver.findElements(By.xpath(exactXpath));

            // 3) Case-insensitive exact fallback if no matches
            if (matches.isEmpty()) {
                String ciXpath = "//*[translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')=" + " translate(" + toXPathLiteral(trimmed) + ", 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')]";
                matches = driver.findElements(By.xpath(ciXpath));
                if (matches.isEmpty()) {
                    logger.info("isHeadingOrTextVisible: no elements found for '{}'", trimmed);
                    return false;
                }
            }

            // 4) Return true if any candidate is displayed (handle stale gracefully once)
            for (WebElement el : matches) {
                try {
                    if (el.isDisplayed()) return true;
                } catch (org.openqa.selenium.StaleElementReferenceException sere) {
                    logger.warn("isHeadingOrTextVisible: stale element encountered; refinding. Msg: {}", sere.getMessage());
                    // Re-find once using the same XPath (prefer exact; if none, try CI)
                    List<WebElement> retry = driver.findElements(By.xpath(exactXpath));
                    if (retry.isEmpty()) {
                        String ciXpath = "//*[translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')=" + " translate(" + toXPathLiteral(trimmed) + ", 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')]";
                        retry = driver.findElements(By.xpath(ciXpath));
                    }
                    for (WebElement el2 : retry) {
                        if (el2.isDisplayed()) return true;
                    }
                }
            }

            logger.info("isHeadingOrTextVisible: elements found for '{}' but none visible", trimmed);
            return false;

        } catch (Exception e) {
            logger.error("isHeadingOrTextVisible: unexpected error for '{}': {}", trimmed, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Converts a Java string into a safe XPath string literal, handling embedded quotes.
     *
     * <p>If the string contains only single quotes or only double quotes, we wrap with the other
     * quote type. If it contains both, we build a {@code concat(...)} expression.</p>
     *
     * @param s input string (not null)
     * @return XPath-safe string literal representing {@code s}
     * @since 04-09-2025
     */
    private String toXPathLiteral(String s) {
        if (!s.contains("'")) return "'" + s + "'";
        if (!s.contains("\"")) return "\"" + s + "\"";
        // contains both ' and " → use concat
        String[] parts = s.split("'");
        StringBuilder sb = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(", \"'\", ");
            sb.append("'").append(parts[i]).append("'");
        }
        sb.append(")");
        return sb.toString();
    }


    /**
     * Checks whether a login-related text is currently visible on the page.
     *
     * @param expectedText the text to match (exact after {@code normalize-space(.)})
     * @return {@code true} if a visible element with the given text exists; otherwise {@code false}
     * @since 04-09-2025
     */
    public boolean isLoginTextVisible(String expectedText) {
        // 1) Basic input validation
        if (expectedText == null) {
            logger.warn("isLoginTextVisible: expectedText is null");
            return false;
        }
        String trimmed = expectedText.trim();
        if (trimmed.isEmpty()) {
            logger.warn("isLoginTextVisible: expectedText is blank");
            return false;
        }

        // 2) Exact (case-sensitive) XPath
        String exactXpath = "//*[normalize-space(.)=" + toXPathLiteral(trimmed) + "]";

        try {
            List<WebElement> matches = driver.findElements(By.xpath(exactXpath));

            // 3) Case-insensitive exact fallback if none found
            if (matches.isEmpty()) {
                String ciXpath = "//*[translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')=" + " translate(" + toXPathLiteral(trimmed) + ", 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')]";
                matches = driver.findElements(By.xpath(ciXpath));
                if (matches.isEmpty()) {
                    logger.info("isLoginTextVisible: no elements found for '{}'", trimmed);
                    return false;
                }
            }

            // 4) Visible?
            for (WebElement el : matches) {
                try {
                    if (el.isDisplayed()) {
                        logger.debug("isLoginTextVisible: visible match found for '{}'", trimmed);
                        return true;
                    }
                } catch (org.openqa.selenium.StaleElementReferenceException sere) {
                    logger.warn("isLoginTextVisible: stale element encountered; refinding. Msg: {}", sere.getMessage());
                    // Re-find once (prefer exact; otherwise CI)
                    List<WebElement> retry = driver.findElements(By.xpath(exactXpath));
                    if (retry.isEmpty()) {
                        String ciXpath = "//*[translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')=" + " translate(" + toXPathLiteral(trimmed) + ", 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')]";
                        retry = driver.findElements(By.xpath(ciXpath));
                    }
                    for (WebElement el2 : retry) {
                        if (el2.isDisplayed()) return true;
                    }
                }
            }

            logger.info("isLoginTextVisible: elements found for '{}' but none visible", trimmed);
            return false;

        } catch (Exception e) {
            logger.error("isLoginTextVisible: unexpected error for '{}': {}", trimmed, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Waits for the Customer Profile panel to be open (visible).
     *
     * @throws TimeoutException if the panel does not become visible within the timeout
     * @since 04-09-2025
     */
    public void waitForOpen() {
        logger.info("waitForOpen: waiting for Customer Profile panel (default 10s)");
        waitForOpen(Duration.ofSeconds(10));
    }

    /**
     * Waits until the panel’s key element is visible; throws on failure.
     *
     * <p>Validates the provided timeout; if null/zero/negative, falls back to 10s and logs a warning.
     * Emits INFO logs on start and success (with elapsed time), and ERROR on timeout/other errors.</p>
     *
     * @param timeout maximum time to wait
     * @throws TimeoutException if the panel does not become visible within the timeout
     * @since 04-09-2025
     */
    public void waitForOpen(Duration timeout) {
        // 1) Validate timeout
        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            logger.warn("waitForOpen: invalid timeout '{}'; defaulting to 10s", timeout);
            timeout = Duration.ofSeconds(10);
        }

        logger.info("waitForOpen: waiting for Customer Profile panel to open (timeout={}s)", timeout.getSeconds());
        final long t0 = System.nanoTime();

        try {
            // 2) Delegate to your custom wait (must throw on failure)
            wait.waitForVisibilitywithacustomtimeout(viewProfileLink, timeout);

            // 3) Success logging with elapsed time
            final long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            logger.info("waitForOpen: panel visible in {} ms (~{} s)", elapsedMs, String.format("%.2f", elapsedMs / 1000.0));

        } catch (TimeoutException te) {
            final long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            logger.error("waitForOpen: panel did not become visible within {}s (elapsed {} s)", timeout.getSeconds(), String.format("%.2f", elapsedMs / 1000.0));
            throw te; // preserve semantics: caller decides what to do
        } catch (Exception e) {
            final long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            logger.error("waitForOpen: unexpected error after {} s: {}", String.format("%.2f", elapsedMs / 1000.0), e.getMessage(), e);
            throw e;
        }
    }


    /**
     * Clicks a menu by name with a custom timeout.
     *
     * <p>Strategy:</p>
     * <ol>
     *   <li>Resolve the element for {@code menuName}.</li>
     *   <li>Try a normal Selenium click when clickable.</li>
     *   <li>If that fails, scroll into view and retry click.</li>
     *   <li>If that still fails, fall back to JS click.</li>
     *   <li>Retries once on {@link StaleElementReferenceException} by re-resolving the element.</li>
     * </ol>
     *
     * <p>Logs INFO on start/success (with elapsed time), WARN when using fallbacks or stale retries,
     * and ERROR if all attempts fail.</p>
     *
     * @param menuName the visible label of the menu to click
     * @param timeout  maximum time to wait for clickability
     * @throws TimeoutException         if the element cannot be clicked within the timeout
     * @throws IllegalArgumentException if the menuName is null/blank or cannot be resolved
     * @since 04-09-2025
     */
    public void clickMenu(String menuName, Duration timeout) {
        // Validate inputs
        if (menuName == null || menuName.trim().isEmpty()) {
            logger.error("clickMenu: menuName is null/blank");
            throw new IllegalArgumentException("menuName must not be null/blank");
        }
        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            logger.warn("clickMenu: invalid timeout '{}'; defaulting to 20s", timeout);
            timeout = Duration.ofSeconds(20);
        }

        final String label = menuName.trim();
        final long t0 = System.nanoTime();

        // Resolve element (your existing resolver should throw for unknown labels)
        WebElement element = resolveMenuElement(label);
        logger.info("clickMenu: attempting to click '{}' (timeout={}s)", label, timeout.getSeconds());

        WebDriverWait w = new WebDriverWait(driver, timeout);

        // We'll allow one stale retry (re-resolve once)
        int attempts = 0;
        while (true) {
            try {
                // 1) Normal click
                w.until(ExpectedConditions.elementToBeClickable(element)).click();
                long ms = (System.nanoTime() - t0) / 1_000_000L;
                logger.info("clickMenu: clicked '{}' via normal click in {} ms (~{} s)", label, ms, String.format("%.2f", ms / 1000.0));
                return;

            } catch (StaleElementReferenceException sere) {
                // Re-resolve once
                if (++attempts > 1) {
                    long ms = (System.nanoTime() - t0) / 1_000_000L;
                    logger.error("clickMenu: stale element repeatedly for '{}' after {} ms: {}", label, ms, sere.getMessage(), sere);
                    throw sere;
                }
                logger.warn("clickMenu: stale element for '{}', re-resolving and retrying once...", label);
                element = resolveMenuElement(label);
                continue;

            } catch (ElementClickInterceptedException | TimeoutException | JavascriptException |
                     MoveTargetOutOfBoundsException e1) {
                logger.warn("clickMenu: normal click failed for '{}': {}. Trying scroll+click...", label, e1.getMessage());

                try {
                    // 2) Scroll + click
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", element);
                    w.until(ExpectedConditions.elementToBeClickable(element)).click();

                    long ms = (System.nanoTime() - t0) / 1_000_000L;
                    logger.info("clickMenu: clicked '{}' via scroll+click in {} ms (~{} s)", label, ms, String.format("%.2f", ms / 1000.0));
                    return;

                } catch (StaleElementReferenceException sere2) {
                    if (++attempts > 1) {
                        long ms = (System.nanoTime() - t0) / 1_000_000L;
                        logger.error("clickMenu: stale element after scroll for '{}' ({} ms): {}", label, ms, sere2.getMessage(), sere2);
                        throw sere2;
                    }
                    logger.warn("clickMenu: stale after scroll for '{}', re-resolving and retrying once...", label);
                    element = resolveMenuElement(label);
                    continue;

                } catch (Exception e2) {
                    logger.warn("clickMenu: scroll+click failed for '{}': {}. Falling back to JS click...", label, e2.getMessage());

                    try {
                        // 3) JS click fallback
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                        long ms = (System.nanoTime() - t0) / 1_000_000L;
                        logger.info("clickMenu: clicked '{}' via JS click in {} ms (~{} s)", label, ms, String.format("%.2f", ms / 1000.0));
                        return;

                    } catch (Exception e3) {
                        long ms = (System.nanoTime() - t0) / 1_000_000L;
                        logger.error("clickMenu: all click strategies failed for '{}' after {} ms: {}", label, ms, e3.getMessage(), e3);
                        // Re-throw the most recent exception as TimeoutException if it was a wait fail, else as-is
                        if (e3 instanceof TimeoutException) throw (TimeoutException) e3;
                        throw new TimeoutException("Unable to click menu '" + label + "' within " + timeout.getSeconds() + "s. Last error: " + e3.getMessage(), e3);
                    }
                }
            }
        }
    }


    /**
     * Resolves the left-panel WebElement for a given human-readable menu label.
     *
     * <p>Validates input and supports a few common aliases (e.g., "Logout" → "Log out").</p>
     *
     * @param menuName the visible label of the menu item (e.g., "My Services")
     * @return the corresponding {@link WebElement}
     * @throws IllegalArgumentException if {@code menuName} is null/blank or not recognized
     * @since 04-09-2025
     */
    private WebElement resolveMenuElement(String menuName) {
        if (menuName == null || menuName.trim().isEmpty()) {
            logger.error("resolveMenuElement: menuName is null/blank");
            throw new IllegalArgumentException("Unknown menu: <null/blank>");
        }

        final String raw = menuName.trim();
        final String key = raw.toLowerCase();
        logger.info("resolveMenuElement: resolving menu '{}'", raw);

        switch (key) {
            case "my services":
                return myServicesOption;
            case "my business":
                return myBusinessOption;
            case "my quotations":
                return myQuotationsOption;
            case "my subscriptions":
                return mySubscriptionOption;
            case "help":
                return helpOption;
            case "faqs":
                return faqsOption;
            case "view profile":
                return viewProfileLink;
            case "log out":
            case "logout": // common alias
                return logoutOption;
            default:
                logger.error("resolveMenuElement: unknown menu '{}'", raw);
                throw new IllegalArgumentException("Unknown menu: " + raw);
        }

    }

    /**
     * Waits until the destination view is ready for the provided label, preferring
     * page-specific anchors where available, else falling back to exact text presence.
     *
     * <p>This method is intended to be used with an explicit timeout and returns
     * {@code false} on timeout (does not throw), so callers can decide how to log/fail.</p>
     *
     * @param expectedText the destination label (e.g., "My Business", "My Services")
     * @param timeout      maximum time to wait
     * @return {@code true} if the destination becomes ready within the timeout; otherwise {@code false}
     * @since 04-09-2025
     */
    public boolean waitForDestinationReady(String expectedText, Duration timeout) {
        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            logger.warn("waitForDestinationReady: invalid timeout '{}'; defaulting to 20s", timeout);
            timeout = Duration.ofSeconds(20);
        }
        if (expectedText == null || expectedText.trim().isEmpty()) {
            logger.warn("waitForDestinationReady: expectedText is null/blank");
            return false;
        }

        final String label = expectedText.trim();
        final String key = safeLower(label);
        final long t0 = System.nanoTime();
        logger.info("waitForDestinationReady: waiting for '{}' (timeout={}s)", label, timeout.getSeconds());

        try {
            WebDriverWait w = new WebDriverWait(driver, timeout);

            switch (key) {
                case "my business":
                    // Wait for the subheading “Business that you are part of”
                    w.until(ExpectedConditions.visibilityOf(myBusinessSubheading));
                    long msBiz = (System.nanoTime() - t0) / 1_000_000L;
                    logger.info("waitForDestinationReady: '{}' ready via Business marker in {} ms (~{} s)", label, msBiz, String.format("%.2f", msBiz / 1000.0));
                    return true;

                case "my services":
                    // Wait for the tabs row (Active/Unpaid/Completed/Closed)
                    w.until(ExpectedConditions.visibilityOf(myServicesTabsRow));
                    long msSvc = (System.nanoTime() - t0) / 1_000_000L;
                    logger.info("waitForDestinationReady: '{}' ready via Services tabs in {} ms (~{} s)", label, msSvc, String.format("%.2f", msSvc / 1000.0));
                    return true;

                case "my quotations":
                    // Wait for the page header “My Quotations”
                    w.until(ExpectedConditions.visibilityOf(myQuotationsHeading));
                    long msQuote = (System.nanoTime() - t0) / 1_000_000L;
                    logger.info("waitForDestinationReady: '{}' ready via Quotations header in {} ms (~{} s)",
                            label, msQuote, String.format("%.2f", msQuote / 1000.0));
                    return true;

                case "my subscriptions":
                    // Wait for the page header “My Subscriptions”
                    w.until(ExpectedConditions.visibilityOf(mySubscriptionHeading));
                    long msSubs = (System.nanoTime() - t0) / 1_000_000L;
                    logger.info("waitForDestinationReady: '{}' ready via Subscriptions header in {} ms (~{} s)",
                            label, msSubs, String.format("%.2f", msSubs / 1000.0));
                    return true;

                default:
                    // Fallback: exact text visibility anywhere on the page
                    boolean ok = w.until(d -> isHeadingOrTextVisible(label) ? true : null);
                    long ms = (System.nanoTime() - t0) / 1_000_000L;
                    logger.info("waitForDestinationReady: '{}' ready via text check in {} ms (~{} s)", label, ms, String.format("%.2f", ms / 1000.0));
                    return ok;
            }
        } catch (TimeoutException te) {
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            logger.warn("waitForDestinationReady: timed out waiting for '{}' after {} ms (~{} s)", label, elapsedMs, String.format("%.2f", elapsedMs / 1000.0));
            return false;
        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            logger.error("waitForDestinationReady: unexpected error for '{}' after {} ms: {}", label, elapsedMs, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Returns whether the destination UI for the provided label is currently visible.
     *
     * @param expectedText the destination label to verify
     * @return {@code true} if the destination marker (or fallback text) is visible; otherwise {@code false}
     * @since 04-09-2025
     */
    public boolean isDestinationVisible(String expectedText) {
        if (expectedText == null || expectedText.trim().isEmpty()) {
            logger.warn("isDestinationVisible: expectedText is null/blank");
            return false;
        }

        final String label = expectedText.trim();
        final String key = safeLower(label);

        try {
            switch (key) {
                case "my business":
                    boolean biz = myBusinessSubheading != null && myBusinessSubheading.isDisplayed();
                    logger.info("isDestinationVisible: '{}' business marker visible = {}", label, biz);
                    return biz;

                case "my services":
                    boolean svc = myServicesTabsRow != null && myServicesTabsRow.isDisplayed();
                    logger.info("isDestinationVisible: '{}' services tabs visible = {}", label, svc);
                    return svc;

                case "my quotations":
                    boolean quot = myQuotationsHeading != null && myQuotationsHeading.isDisplayed();
                    logger.info("isDestinationVisible: '{}' quotations header visible = {}", label, quot);
                    return quot;

                case "my subscriptions":
                    boolean subs = mySubscriptionHeading != null && mySubscriptionHeading.isDisplayed();
                    logger.info("isDestinationVisible: '{}' subscriptions header visible = {}", label, subs);
                    return subs;

                default:
                    boolean text = isHeadingOrTextVisible(label);
                    logger.info("isDestinationVisible: '{}' fallback text visible = {}", label, text);
                    return text;
            }
        } catch (StaleElementReferenceException sere) {
            logger.warn("isDestinationVisible: stale element for '{}' marker: {}", label, sere.getMessage());
            // Fallback to text check on stale
            boolean text = isHeadingOrTextVisible(label);
            logger.info("isDestinationVisible: '{}' fallback text visible (after stale) = {}", label, text);
            return text;
        } catch (Exception e) {
            logger.error("isDestinationVisible: unexpected error for '{}': {}", label, e.getMessage(), e);
            return false;
        }
    }


    /**
     * Null-safe, trimmed lower-case helper.
     */
    private String safeLower(String s) {
        return (s == null) ? "" : s.trim().toLowerCase();
    }


}
