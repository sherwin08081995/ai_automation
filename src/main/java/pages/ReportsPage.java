package pages;


import base.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import utils.ReusableCommonMethods;
import utils.ScenarioContext;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sherwin
 * @since 16-10-2025
 */

public class ReportsPage extends BasePage {

    public ReportsPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//span[normalize-space()='Reports']")
    private WebElement reportsTab;

    @FindBy(xpath = "//h1[normalize-space()='Reports']")
    private WebElement reportsTitle;

    // Category headers
    @FindBy(xpath = "//div[@class='flex flex-col gap-[0.8rem]']//p[normalize-space()='Frequently Used']")
    private WebElement frequentlyUsedHeader;

    @FindBy(xpath = "//div[@class='flex flex-col gap-[0.8rem]']//p[normalize-space()='By Risk']")
    private WebElement byRiskHeader;

    @FindBy(xpath = "//div[@class='flex flex-col gap-[0.8rem]']//p[normalize-space()='By Stage']")
    private WebElement byStageHeader;

    @FindBy(xpath = "//div[@class='flex flex-col gap-[0.8rem]']//p[normalize-space()='By Organization']")
    private WebElement byOrganizationHeader;

    @FindBy(xpath = "//div[@class='flex flex-col gap-[0.8rem]']//p[normalize-space()='By Department']")
    private WebElement byDepartmentHeader;

    @FindBy(xpath = "//div[contains(@class,'flex') and contains(@class,'flex-col') and contains(@class,'gap-[0.8rem]')]//p[" + "normalize-space()='Frequently Used' or " + "normalize-space()='By Risk' or " + "normalize-space()='By Stage' or " + "normalize-space()='By Organization' or " + "normalize-space()='By Department']")
    private List<WebElement> allCategoryHeaders;


    // ================= Frequently Used → details page =================

    // ===== Frequently Used =====
    @FindBy(xpath = "//p[normalize-space()='Frequently Used']" + "/following-sibling::div[contains(@class,'bg-white') and contains(@class,'rounded-xl')]")
    private WebElement frequentlyUsedContainer;

    @FindBy(xpath = "//p[normalize-space()='Frequently Used']" + "/following-sibling::div[contains(@class,'bg-white') and contains(@class,'rounded-xl')]" + "//div[contains(@class,'cursor-pointer')]")
    private List<WebElement> frequentlyUsedItems;

    @FindBy(xpath = "//p[normalize-space()='Based on' or normalize-space()='Based on ']/parent::*")
    private WebElement basedOnBlock;

    // Fallback (rare): single <p> that already has the full text
    @FindBy(xpath = "(//p[contains(normalize-space(),'Based on')])[1]")
    private WebElement basedOnAnyLine;

    @FindBy(xpath = "//p[contains(normalize-space(),'Based on')]/following-sibling::p[1]")
    private WebElement basedOnValueLine; // may not always be present/visible

    private final By basedOnValueFlexible = By.xpath("//p[contains(normalize-space(),'Based on')]/following::*[self::p or self::div][1]");

    @FindBy(xpath = "(//p[contains(normalize-space(),'Based on')])[1]")
    private WebElement basedOnP;

    @FindBy(xpath = "(//p[contains(normalize-space(),'Based on')])[1]/following-sibling::*[1]")
    private WebElement basedOnNextSibling; // p or span

    @FindBy(xpath = "(//p[contains(normalize-space(),'Based on')])[1]//span[1]")
    private WebElement basedOnChildSpan;

    private By frequentlyUsedItemsBy = By.xpath("//p[normalize-space()='Frequently Used']" + "/following-sibling::div[contains(@class,'bg-white') and contains(@class,'rounded-xl')]" + "//div[contains(@class,'cursor-pointer')]");

    @FindBy(xpath = "//*[self::a or self::p or self::span][normalize-space()='Go back' or normalize-space()='Go Back']")
    private WebElement goBackButton;

    /* ================= By Risk → landing ================= */

    @FindBy(xpath = "//p[normalize-space()='By Risk']")
    private WebElement byRiskHeaderBox;

    @FindBy(xpath = "//p[normalize-space()='By Risk']/following-sibling::div[contains(@class,'bg-white') and contains(@class,'rounded-xl')]")
    private WebElement byRiskContainer;

    @FindBy(xpath = "//p[normalize-space()='By Risk']/following-sibling::div[contains(@class,'bg-white') and contains(@class,'rounded-xl')]//div[contains(@class,'cursor-pointer')]")
    private List<WebElement> byRiskItems;

    @FindBy(xpath = "//p[normalize-space()='By Department']")
    private WebElement byDepartmentHeaderBox;

    @FindBy(xpath = "//p[normalize-space()='By Department']/following-sibling::div[contains(@class,'bg-white') and contains(@class,'rounded-xl')]")
    private WebElement byDepartmentContainer;

    @FindBy(xpath = "//p[normalize-space()='By Department']/following-sibling::div[contains(@class,'bg-white') and contains(@class,'rounded-xl')]//div[contains(@class,'cursor-pointer')]")
    private List<WebElement> byDepartmentItems;

    private final By byDepartmentItemsBy = By.xpath("//p[normalize-space()='By Department']" + "/following-sibling::div[contains(@class,'bg-white') and contains(@class,'rounded-xl')]" + "//div[contains(@class,'cursor-pointer')]");

    @FindBy(xpath = "//p[normalize-space()='By Organization']")
    private WebElement byOrganizationHeaderBox;

    @FindBy(xpath = "//p[normalize-space()='By Organization']/following-sibling::div[contains(@class,'bg-white') and contains(@class,'rounded-xl')]")
    private WebElement byOrganizationContainer;

    @FindBy(xpath = "//p[normalize-space()='By Organization']/following-sibling::div[contains(@class,'bg-white') and contains(@class,'rounded-xl')]//div[contains(@class,'cursor-pointer')]")
    private List<WebElement> byOrganizationItems;

    private final By byOrganizationItemsBy = By.xpath("//p[normalize-space()='By Organization']" + "/following-sibling::div[contains(@class,'bg-white') and contains(@class,'rounded-xl')]" + "//div[contains(@class,'cursor-pointer')]");


    private final By byRiskItemsBy = By.xpath("//p[normalize-space()='By Risk']" + "/following-sibling::div[contains(@class,'bg-white') and contains(@class,'rounded-xl')]" + "//div[contains(@class,'cursor-pointer')]");

    private static final String EXPECTED_URL_CONTAINS = "/grc/reports";
    private static final String EXPECTED_HEADER_TEXT = "Reports";

    private final Duration defaultClickTimeout = Duration.ofSeconds(15);


    /**
     * Click the Reports tab using the common safeClick helper.
     * Clear logging is included for each attempt in safeClick.
     */
    public void clickReportsTab() {
        logger.info("📂 Preparing to click 'Reports' tab via safeClick...");
        commonMethods.safeClick(driver, reportsTab, "Reports tab", 10);
        logger.info("✅ Clicked 'Reports' tab (safeClick).");
    }

    /**
     * Wait until Reports page is fully loaded:
     * - Title visible
     * - Header text equals/contains "Reports"
     * - URL contains "/grc/reports"
     */
    public boolean waitForReportsLoaded(Duration timeout) {
        logger.info("⏳ Waiting for Reports page to load (timeout {} ms)...", Long.valueOf(timeout.toMillis()));
        try {
            // a) title visible
            wait.waitForVisibilityRefreshed(reportsTitle, timeout);
            boolean displayed = isDisplayedSafe(reportsTitle);
            if (!displayed) {
                logger.warn("⚠️ Reports title element is not displayed.");
            } else {
                logger.info("🔹 Reports title element is visible.");
            }

            // b) header text ok
            String headerText = safeGetText(reportsTitle);
            boolean headerOk = headerMatches(headerText);
            if (!headerOk) {
                logger.warn("⚠️ Header text mismatch. Actual='{}' (expected to equal/contain '{}')", headerText, EXPECTED_HEADER_TEXT);
            } else {
                logger.info("🔹 Header text OK: '{}'", headerText);
            }

            // c) url contains fragment
            wait.waitForUrlContains(EXPECTED_URL_CONTAINS);
            boolean urlOk = true;
            String curUrl = "";
            try {
                curUrl = driver.getCurrentUrl();
            } catch (Exception ignore) {
            }
            if (curUrl == null) curUrl = "";
            if (curUrl.indexOf(EXPECTED_URL_CONTAINS) < 0) {
                urlOk = false;
                logger.warn("⚠️ URL check failed. Current='{}' (expected to contain '{}')", curUrl, EXPECTED_URL_CONTAINS);
            } else {
                logger.info("🔹 URL OK: '{}'", curUrl);
            }

            boolean allOk = displayed && headerOk && urlOk;
            if (allOk) {
                logger.info("✅ Reports page loaded successfully.");
            } else {
                logger.warn("⚠️ Reports page readiness checks did not fully pass (displayed={}, headerOk={}, urlOk={}).", Boolean.valueOf(displayed), Boolean.valueOf(headerOk), Boolean.valueOf(urlOk));
            }
            return allOk;

        } catch (TimeoutException te) {
            logger.error("❌ Reports did not load within {} ms.", Long.valueOf(timeout.toMillis()));
            return false;
        } catch (Exception e) {
            logger.error("❌ Error while waiting for Reports: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Wait until all five category headers are visible.
     * Logs which header failed if not visible within the timeout.
     */
    public boolean waitForAllCategoryHeaders(Duration timeout) {
        logger.info("⏳ Waiting for all category headers to be visible (timeout {} ms)...", Long.valueOf(timeout.toMillis()));
        try {
            wait.waitForVisibilityRefreshed(frequentlyUsedHeader, timeout);
            wait.waitForVisibilityRefreshed(byRiskHeader, timeout);
            wait.waitForVisibilityRefreshed(byStageHeader, timeout);
            wait.waitForVisibilityRefreshed(byOrganizationHeader, timeout);
            wait.waitForVisibilityRefreshed(byDepartmentHeader, timeout);

            boolean allVisible = isDisplayedSafe(frequentlyUsedHeader) && isDisplayedSafe(byRiskHeader) && isDisplayedSafe(byStageHeader) && isDisplayedSafe(byOrganizationHeader) && isDisplayedSafe(byDepartmentHeader);

            if (allVisible) {
                logger.info("✅ All category headers are visible.");
            } else {
                // granular logs
                if (!isDisplayedSafe(frequentlyUsedHeader)) logger.warn("⚠️ 'Frequently Used' not visible.");
                if (!isDisplayedSafe(byRiskHeader)) logger.warn("⚠️ 'By Risk' not visible.");
                if (!isDisplayedSafe(byStageHeader)) logger.warn("⚠️ 'By Stage' not visible.");
                if (!isDisplayedSafe(byOrganizationHeader)) logger.warn("⚠️ 'By Organization' not visible.");
                if (!isDisplayedSafe(byDepartmentHeader)) logger.warn("⚠️ 'By Department' not visible.");
            }
            return allVisible;

        } catch (Exception e) {
            logger.warn("⚠️ Not all category headers became visible within {} ms. Error: {}", Long.valueOf(timeout.toMillis()), e.getMessage());
            return false;
        }
    }

    /**
     * Returns the normalized text of the five visible category headers.
     * No Java 8 streams; preserves compatibility.
     */
    public List<String> getVisibleCategoryHeaderTexts() {
        // ensure present (short wait)
        waitForAllCategoryHeaders(Duration.ofSeconds(10));

        List<String> texts = new ArrayList<String>();
        if (allCategoryHeaders != null) {
            for (int i = 0; i < allCategoryHeaders.size(); i++) {
                WebElement el = allCategoryHeaders.get(i);
                if (isDisplayedSafe(el)) {
                    String t = safeGetText(el);
                    if (t != null) {
                        t = t.trim();
                        if (t.length() > 0) {
                            texts.add(t);
                        }
                    }
                }
            }
        }
        logger.info("🔎 Found {} category headers: {}", Integer.valueOf(texts.size()), texts);
        return texts;
    }


    private String safeGetText(WebElement el) {
        try {
            String txt = el.getText();
            return (txt == null) ? "" : txt.trim();
        } catch (StaleElementReferenceException se) {
            try {
                wait.waitForVisibilityRefreshed(el, Duration.ofSeconds(5));
                String txt = el.getText();
                return (txt == null) ? "" : txt.trim();
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
            return wait.waitUntilDisplayedRefreshed(el, Duration.ofSeconds(5));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean headerMatches(String headerText) {
        if (headerText == null || headerText.length() == 0) return false;
        return headerText.equalsIgnoreCase(EXPECTED_HEADER_TEXT) || (headerText.indexOf(EXPECTED_HEADER_TEXT) >= 0);
    }


    private String readBasedOnHeader() {
        boolean baseVisible = false;
        try {
            // make sure the "Based on" <p> is visible
            wait.waitForVisibilityRefreshed(basedOnP, Duration.ofSeconds(8));
            baseVisible = isDisplayedSafe(basedOnP);
        } catch (Exception ignore) {
        }

        String base = safeGetText(basedOnP); // "Based on" (may be null)
        String value = "";

        try {
            if (isDisplayedSafe(basedOnChildSpan)) {
                value = safeGetText(basedOnChildSpan);
                logger.debug("readBasedOnHeader(): value from child <span>: '{}'", value);
            } else if (isDisplayedSafe(basedOnNextSibling)) {
                value = safeGetText(basedOnNextSibling);
                logger.debug("readBasedOnHeader(): value from next sibling: '{}'", value);
            } else {
                // Flexible fallback: first following p/div text (covers extra wrappers)
                WebElement flex = findBasedOnValueFlexible();
                if (flex != null && isDisplayedSafe(flex)) {
                    value = deepText(flex);
                    logger.debug("readBasedOnHeader(): value from flexible locator: '{}'", value);
                } else {
                    logger.debug("readBasedOnHeader(): no visible value element found near 'Based on' label.");
                }
            }
        } catch (Exception ignored) {
        }

        String full = (base == null ? "" : base.trim());
        if (!value.isEmpty()) {
            full = (full.isEmpty() ? value : (full + " " + value));
        }

        if (!baseVisible) {
            logger.warn("⚠️ 'Based on' label not confirmed visible before read. base='{}', value='{}'", base, value);
        }
        if (value.isEmpty()) {
            logger.warn("⚠️ BasedOn value appears empty. raw='{}'", full);
        }

        logger.info("🧾 BasedOn header (raw)='{}'", full);
        return full;
    }


    private String normalizeForContains(String s) {
        if (s == null) return "";
        s = s.replace('\n', ' ').replace('\r', ' ').trim();
        s = s.replaceAll("\\s*\\(\\d+\\)\\s*$", "");
        s = s.replaceAll("\\s+", " ");
        return s.toLowerCase();
    }


    public boolean waitForDetailsViewLoaded(Duration timeout) {
        final long deadline = System.currentTimeMillis() + timeout.toMillis();
        logger.info("⏳ Waiting for details page (redirect + header) up to {} ms...", timeout.toMillis());

        String startUrl = "";
        try {
            startUrl = driver.getCurrentUrl();
        } catch (Exception ignore) {
        }

        // a) wait for redirect to /grc/compliances/report
        boolean redirected = false;
        while (System.currentTimeMillis() < deadline) {
            try {
                String cur = driver.getCurrentUrl();
                if (!cur.contains("/grc/reports") && cur.contains("/grc/compliances/report")) {
                    logger.info("➡️ URL changed from '{}' → '{}'", startUrl, cur);
                    redirected = true;
                    break;
                }
            } catch (Exception ignore) {
            }
            try {
                Thread.sleep(120);
            } catch (InterruptedException ignored) {
            }
        }
        if (!redirected) {
            logger.warn("⚠️ No redirect to details page detected within {} ms (startUrl='{}')", timeout.toMillis(), startUrl);
            return false;
        }

        // b) wait until Based on ... header has a non-empty VALUE
        while (System.currentTimeMillis() < deadline) {
            String header = readBasedOnHeader();
            String norm = normalizeForContains(header);
            if (norm.startsWith("based on") && norm.length() > "based on".length() + 1) {
                logger.info("✅ Details view loaded. Header='{}'", header);
                return true;
            }
            long remaining = Math.max(0, deadline - System.currentTimeMillis());
            logger.debug("waitForDetailsViewLoaded(): header not ready yet; {} ms remaining", remaining);
            try {
                Thread.sleep(150);
            } catch (InterruptedException ignored) {
            }
        }

        logger.warn("⚠️ Details view did not fully render within timeout.");
        return false;
    }


    public boolean clickGoBackAndWaitReports(Duration timeout) {
        logger.info("🔙 Clicking 'Go back' to return to Reports landing...");
        try {
            commonMethods.safeClick(driver, goBackButton, "Go back", 8);
        } catch (Exception e) {
            logger.error("❌ Failed to click 'Go back': {}", e.getMessage());
            return false;
        }
        boolean ok = waitForReportsLoaded(timeout);
        if (!ok) {
            logger.error("❌ Reports page did not load after clicking 'Go back' within {} ms", timeout.toMillis());
        } else {
            logger.info("✅ Returned to Reports landing.");
        }
        return ok;
    }


    public String extractReportValueAfterFirstDash(String text) {
        if (text == null) return "";
        String s = text.replace('\n', ' ').replace('\r', ' ');
        s = s.replaceAll("\\s+", " ").trim(); // "Organization - Zolvit ... - Goa"
        int idx = s.indexOf('-');             // first '-' after label
        if (idx >= 0 && idx + 1 < s.length()) {
            String v = s.substring(idx + 1).trim();
            logger.debug("extractReportValueAfterFirstDash(): extracted value='{}' from '{}'", v, s);
            return v; // "Zolvit ... - Goa" OR "Indirect Tax" OR "High"
        }
        logger.debug("extractReportValueAfterFirstDash(): no '-' found; using full text='{}'", s);
        return s;
    }


    /**
     * Ensure the 'Frequently Used' section is visible with its container.
     */
    public boolean waitForFrequentlyUsedVisible(Duration timeout) {
        logger.info("⏳ Waiting for 'Frequently Used' section to be visible ({} ms)...", Long.valueOf(timeout.toMillis()));
        try {
            wait.waitForVisibilityRefreshed(frequentlyUsedHeader, timeout);
            wait.waitForVisibilityRefreshed(frequentlyUsedContainer, timeout);

            boolean headerOk = isDisplayedSafe(frequentlyUsedHeader);
            boolean boxOk = isDisplayedSafe(frequentlyUsedContainer);

            if (headerOk && boxOk) {
                logger.info("✅ 'Frequently Used' header & container are visible.");
                return true;
            }
            if (!headerOk) logger.warn("⚠️ 'Frequently Used' header not visible.");
            if (!boxOk) logger.warn("⚠️ 'Frequently Used' container not visible.");
            return false;

        } catch (Exception e) {
            logger.error("❌ Error while waiting for 'Frequently Used' section: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Return the number of visible items listed in Frequently Used.
     */
    public int getFrequentlyUsedItemCount() {
        int count = 0;
        if (frequentlyUsedItems != null) {
            for (int i = 0; i < frequentlyUsedItems.size(); i++) {
                WebElement el = frequentlyUsedItems.get(i);
                if (isDisplayedSafe(el)) count++;
            }
        }
        logger.info("🔎 Frequently Used items visible: {}", Integer.valueOf(count));
        return count;
    }

    /**
     * Return trimmed texts of visible Frequently Used items (pre-Java 8).
     */
    public List<String> getFrequentlyUsedItemTexts() {
        List<String> texts = new ArrayList<String>();
        if (frequentlyUsedItems != null) {
            for (int i = 0; i < frequentlyUsedItems.size(); i++) {
                WebElement el = frequentlyUsedItems.get(i);
                if (isDisplayedSafe(el)) {
                    String t = safeGetText(el);
                    if (t != null) {
                        t = t.trim();
                        if (t.length() > 0) texts.add(t);
                    }
                }
            }
        }
        logger.info("🗂️ Frequently Used item texts: {}", texts);
        return texts;
    }


    /**
     * Returns the first following p/div after the 'Based on' label (if present), else null.
     */
    private WebElement findBasedOnValueFlexible() {
        try {
            return driver.findElement(basedOnValueFlexible);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns visible text; if empty, uses innerText via JS.
     */
    private String deepText(WebElement el) {
        String t = safeGetText(el);
        if (t != null && t.trim().length() > 0) return t.trim();
        try {
            Object js = ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText;", el);
            return js == null ? "" : String.valueOf(js).trim();
        } catch (Exception ignore) {
            return "";
        }
    }


    public boolean basedOnHeaderContainsValue(String clickedValue) {
        String headerRaw = readBasedOnHeader();
        String headerNorm = normalizeForContains(headerRaw);
        String valueNorm = normalizeForContains(clickedValue);

        if (valueNorm.isEmpty()) {
            logger.error("❌ basedOnHeaderContainsValue(): expected value is empty. header='{}'", headerRaw);
            return false;
        }

        boolean ok = headerNorm.contains(valueNorm);
        logger.info("🔎 header(norm)='{}' | value(norm)='{}' → contains? {}", headerNorm, valueNorm, Boolean.valueOf(ok));
        if (!ok) {
            logger.error("❌ Details header does not contain expected value. header='{}', expected='{}'", headerRaw, clickedValue);
        }
        return ok;
    }


    public boolean validateAllFrequentlyUsedItems() {
        if (!waitForFrequentlyUsedVisible(Duration.ofSeconds(10))) {
            logger.error("❌ 'Frequently Used' section not visible.");
            return false;
        }

        int total;
        try {
            total = driver.findElements(frequentlyUsedItemsBy).size();
        } catch (Exception e) {
            logger.error("❌ Could not read Frequently Used items: {}", e.toString());
            return false;
        }
        if (total == 0) {
            logger.warn("⚠️ No items in Frequently Used section.");
            return false;
        }
        logger.info("🧮 Starting FU validation for {} items.", total);

        for (int i = 0; i < total; i++) {
            long t0 = System.currentTimeMillis();

            if (!waitForFrequentlyUsedVisible(Duration.ofSeconds(8))) {
                logger.error("❌ 'Frequently Used' not visible before clicking index {}", i);
                return false;
            }

            List<WebElement> items = driver.findElements(frequentlyUsedItemsBy);
            if (i >= items.size()) {
                logger.warn("⚠️ Items count changed ({} → {}); stopping at index {}", total, items.size(), i);
                return false;
            }
            WebElement item = items.get(i);
            if (!isDisplayedSafe(item)) {
                logger.warn("⚠️ Skipping invisible item index {}", i);
                continue;
            }

            String fullText = safeGetText(item);
            String value = extractReportValueAfterFirstDash(fullText);
            logger.info("🧩 [{} / {}] Validating FU item: '{}' (value='{}')", i + 1, total, fullText, value);

            commonMethods.safeClick(driver, item, "Frequently Used item #" + i, 10);

            if (!waitForDetailsViewLoaded(Duration.ofSeconds(20))) {
                logger.error("❌ Details did not load for '{}'", value);
                return false;
            }

            if (!basedOnHeaderContainsValue(value)) {
                logger.error("❌ Header did not contain value '{}'", value);
                return false;
            }
            logger.info("✅ Details page validated for '{}'", value);

            if (!clickGoBackAndWaitReports(Duration.ofSeconds(12))) {
                logger.error("❌ Could not navigate back to Reports after '{}'", value);
                return false;
            }

            long elapsed = System.currentTimeMillis() - t0;
            logger.info("↩️ Returned to Reports page for '{}' ({} ms)", value, elapsed);

            // tiny settle time helps the next findElements() be stable
            try {
                Thread.sleep(400);
            } catch (InterruptedException ignored) {
            }
        }

        logger.info("🎯 All Frequently Used items validated successfully!");
        return true;
    }


    // Thin helper so steps don’t touch locators:
    public boolean clickFUItemByIndex(int index) {
        try {
            List<WebElement> items = driver.findElements(frequentlyUsedItemsBy); // your existing By
            if (index < 0 || index >= items.size()) return false;
            WebElement el = items.get(index);
            if (!isDisplayedSafe(el)) return false;
            commonMethods.safeClick(driver, el, "Frequently Used item #" + index, 10);
            return true;
        } catch (Exception e) {
            logger.error("Click FU item #{} failed: {}", index, e.toString());
            return false;
        }
    }

    public String safeGetUrl() {
        try {
            return driver.getCurrentUrl();
        } catch (Exception e) {
            return "";
        }
    }

    public String fullToTag(String s) {
        if (s == null || s.trim().isEmpty()) return "NA";
        String t = s.replace('\n', ' ').replace('\r', ' ').trim().toLowerCase();
        t = t.replaceAll("[^a-z0-9]+", "_");
        if (t.length() > 40) t = t.substring(0, 40);
        return t.replaceAll("^_+|_+$", "");
    }


    /**
     * Wait until 'By Risk' section is visible (header + container).
     */
    public boolean waitForByRiskVisible(Duration timeout) {
        logger.info("⏳ Waiting for 'By Risk' section to be visible ({} ms)...", timeout.toMillis());
        try {
            wait.waitForVisibilityRefreshed(byRiskHeaderBox, timeout);
            wait.waitForVisibilityRefreshed(byRiskContainer, timeout);

            boolean headerOk = isDisplayedSafe(byRiskHeaderBox);
            boolean boxOk = isDisplayedSafe(byRiskContainer);

            if (headerOk && boxOk) {
                logger.info("✅ 'By Risk' header & container are visible.");
                return true;
            }
            if (!headerOk) logger.warn("⚠️ 'By Risk' header not visible.");
            if (!boxOk) logger.warn("⚠️ 'By Risk' container not visible.");
            return false;

        } catch (Exception e) {
            logger.error("❌ Error while waiting for 'By Risk' section: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Number of visible risk pills/cards.
     */
    public int getByRiskItemCount() {
        int count = 0;
        if (byRiskItems != null) {
            for (WebElement el : byRiskItems) {
                if (isDisplayedSafe(el)) count++;
            }
        }
        logger.info("🔎 By Risk items visible: {}", count);
        return count;
    }

    /**
     * Visible texts of risk options (High/Medium/Low).
     */
    public List<String> getByRiskItemTexts() {
        List<String> texts = new ArrayList<>();
        if (byRiskItems != null) {
            for (WebElement el : byRiskItems) {
                if (isDisplayedSafe(el)) {
                    String t = safeGetText(el);
                    if (t != null) {
                        t = t.trim();
                        if (!t.isEmpty()) texts.add(t);
                    }
                }
            }
        }
        logger.info("🗂️ By Risk item texts: {}", texts);
        return texts;
    }

    /**
     * Click a risk pill by exact/loose label ("High", "Medium", "Low").
     */
    public boolean clickByRiskByLabel(String label) {
        if (label == null || label.trim().isEmpty()) return false;
        String want = label.trim().toLowerCase();

        try {
            List<WebElement> items = driver.findElements(byRiskItemsBy);
            for (int i = 0; i < items.size(); i++) {
                WebElement el = items.get(i);
                if (!isDisplayedSafe(el)) continue;
                String t = safeGetText(el).toLowerCase();
                if (t.equals(want) || t.contains(want)) {
                    commonMethods.safeClick(driver, el, "By Risk item '" + label + "'", 10);
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("❌ clickByRiskByLabel('{}') failed: {}", label, e.toString());
        }
        return false;
    }

    /* ================= Details page → results ================= */

    /**
     * Returns true if any compliance “records” appear on the details screen.
     * Tries table rows, list cards, or generic result rows. Defensive & tolerant.
     */
    public boolean hasAnyComplianceResults() {
        try {
            // Try common patterns (tweak/selectors as your UI evolves)
            By[] candidates = new By[]{By.xpath("//table//tr[.//td or .//th][position()>1]"), By.xpath("//div[contains(@class,'table')]/descendant::div[contains(@class,'row')]"), By.xpath("//div[contains(@class,'results') or contains(@class,'list')]/descendant::*[contains(@class,'item') or contains(@class,'row')]"), By.xpath("//*[self::li or self::tr or self::div][contains(@class,'result') or contains(@class,'record') or contains(@class,'row')]")};

            for (By by : candidates) {
                List<WebElement> found = driver.findElements(by);
                int visible = 0;
                for (WebElement e : found) if (isDisplayedSafe(e)) visible++;
                if (visible > 0) {
                    logger.info("📄 Compliance results found via {} (visible={})", by, visible);
                    return true;
                }
            }
            logger.warn("⚠️ No compliance results matched the generic selectors.");
            return false;

        } catch (Exception e) {
            logger.error("❌ hasAnyComplianceResults() error: {}", e.getMessage());
            return false;
        }
    }











    /* ================= By Stage → landing ================= */

    @FindBy(xpath = "//p[normalize-space()='By Stage']")
    private WebElement byStageHeaderBox;

    @FindBy(xpath = "//p[normalize-space()='By Stage']/following-sibling::div[contains(@class,'bg-white') and contains(@class,'rounded-xl')]")
    private WebElement byStageContainer;

    @FindBy(xpath = "//p[normalize-space()='By Stage']/following-sibling::div[contains(@class,'bg-white') and contains(@class,'rounded-xl')]//div[contains(@class,'cursor-pointer')]")
    private List<WebElement> byStageItems;

    private final By byStageItemsBy = By.xpath("//p[normalize-space()='By Stage']" + "/following-sibling::div[contains(@class,'bg-white') and contains(@class,'rounded-xl')]" + "//div[contains(@class,'cursor-pointer')]");

    /**
     * Wait until 'By Stage' section is visible (header + container).
     */
    public boolean waitForByStageVisible(Duration timeout) {
        logger.info("⏳ Waiting for 'By Stage' section to be visible ({} ms)...", timeout.toMillis());
        try {
            wait.waitForVisibilityRefreshed(byStageHeaderBox, timeout);
            wait.waitForVisibilityRefreshed(byStageContainer, timeout);

            boolean headerOk = isDisplayedSafe(byStageHeaderBox);
            boolean boxOk = isDisplayedSafe(byStageContainer);

            if (headerOk && boxOk) {
                logger.info("✅ 'By Stage' header & container are visible.");
                return true;
            }
            if (!headerOk) logger.warn("⚠️ 'By Stage' header not visible.");
            if (!boxOk) logger.warn("⚠️ 'By Stage' container not visible.");
            return false;

        } catch (Exception e) {
            logger.error("❌ Error while waiting for 'By Stage' section: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Visible texts of stage options.
     */
    public List<String> getByStageItemTexts() {
        List<String> texts = new ArrayList<>();
        if (byStageItems != null) {
            for (WebElement el : byStageItems) {
                if (isDisplayedSafe(el)) {
                    String t = safeGetText(el);
                    if (t != null) {
                        t = t.trim();
                        if (!t.isEmpty()) texts.add(t);
                    }
                }
            }
        }
        logger.info("🗂️ By Stage item texts: {}", texts);
        return texts;
    }

    /**
     * Check if a stage element is visible & clickable (without navigating).
     */
    public boolean isStageClickableByLabel(String label) {
        if (label == null || label.trim().isEmpty()) return false;
        String want = label.trim().toLowerCase();
        try {
            List<WebElement> items = driver.findElements(byStageItemsBy);
            for (WebElement el : items) {
                if (!isDisplayedSafe(el)) continue;
                String t = safeGetText(el).toLowerCase();
                if (t.equals(want) || t.contains(want)) {
                    try {
                        // lightweight "clickable" check
                        return el.isDisplayed() && el.isEnabled();
                    } catch (Exception ignore) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("❌ isStageClickableByLabel('{}') failed: {}", label, e.toString());
        }
        return false;
    }

    /**
     * Click a stage pill/card by its label.
     */
    public boolean clickByStageByLabel(String label) {
        if (label == null || label.trim().isEmpty()) return false;
        String want = label.trim().toLowerCase();

        try {
            List<WebElement> items = driver.findElements(byStageItemsBy);
            for (WebElement el : items) {
                if (!isDisplayedSafe(el)) continue;
                String t = safeGetText(el).toLowerCase();
                if (t.equals(want) || t.contains(want)) {
                    commonMethods.safeClick(driver, el, "By Stage item '" + label + "'", 10);
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("❌ clickByStageByLabel('{}') failed: {}", label, e.toString());
        }
        return false;
    }


    /**
     * Wait until 'By Organization' section is visible (header + container).
     */
    public boolean waitForByOrganizationVisible(Duration timeout) {
        logger.info("⏳ Waiting for 'By Organization' section to be visible ({} ms)...", timeout.toMillis());
        try {
            wait.waitForVisibilityRefreshed(byOrganizationHeaderBox, timeout);
            wait.waitForVisibilityRefreshed(byOrganizationContainer, timeout);

            boolean headerOk = isDisplayedSafe(byOrganizationHeaderBox);
            boolean boxOk = isDisplayedSafe(byOrganizationContainer);

            if (headerOk && boxOk) {
                logger.info("✅ 'By Organization' header & container are visible.");
                return true;
            }
            if (!headerOk) logger.warn("⚠️ 'By Organization' header not visible.");
            if (!boxOk) logger.warn("⚠️ 'By Organization' container not visible.");
            return false;

        } catch (Exception e) {
            logger.error("❌ Error while waiting for 'By Organization' section: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Visible texts of organization options.
     */
    public List<String> getByOrganizationItemTexts() {
        List<String> texts = new ArrayList<>();
        if (byOrganizationItems != null) {
            for (WebElement el : byOrganizationItems) {
                if (isDisplayedSafe(el)) {
                    String t = safeGetText(el);
                    if (t != null) {
                        t = t.trim();
                        if (!t.isEmpty()) texts.add(t);
                    }
                }
            }
        }
        logger.info("🗂️ By Organization item texts: {}", texts);
        return texts;
    }

    /**
     * Check if an organization pill/card is visible & clickable (without navigating).
     */
    public boolean isOrganizationClickableByLabel(String label) {
        if (label == null || label.trim().isEmpty()) return false;
        String want = label.trim().toLowerCase();
        try {
            List<WebElement> items = driver.findElements(byOrganizationItemsBy);
            for (WebElement el : items) {
                if (!isDisplayedSafe(el)) continue;
                String t = safeGetText(el).toLowerCase();
                if (t.equals(want) || t.contains(want)) {
                    try {
                        return el.isDisplayed() && el.isEnabled();
                    } catch (Exception ignore) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("❌ isOrganizationClickableByLabel('{}') failed: {}", label, e.toString());
        }
        return false;
    }

    /**
     * Click an organization pill/card by its label.
     */
    public boolean clickByOrganizationByLabel(String label) {
        if (label == null || label.trim().isEmpty()) return false;
        String want = label.trim().toLowerCase();

        try {
            List<WebElement> items = driver.findElements(byOrganizationItemsBy);
            for (WebElement el : items) {
                if (!isDisplayedSafe(el)) continue;
                String t = safeGetText(el).toLowerCase();
                if (t.equals(want) || t.contains(want)) {
                    commonMethods.safeClick(driver, el, "By Organization item '" + label + "'", 10);
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("❌ clickByOrganizationByLabel('{}') failed: {}", label, e.toString());
        }
        return false;
    }


    /**
     * Wait until 'By Department' section is visible (header + container).
     */
    public boolean waitForByDepartmentVisible(Duration timeout) {
        logger.info("⏳ Waiting for 'By Department' section to be visible ({} ms)...", timeout.toMillis());
        try {
            wait.waitForVisibilityRefreshed(byDepartmentHeaderBox, timeout);
            wait.waitForVisibilityRefreshed(byDepartmentContainer, timeout);

            boolean headerOk = isDisplayedSafe(byDepartmentHeaderBox);
            boolean boxOk = isDisplayedSafe(byDepartmentContainer);

            if (headerOk && boxOk) {
                logger.info("✅ 'By Department' header & container are visible.");
                return true;
            }
            if (!headerOk) logger.warn("⚠️ 'By Department' header not visible.");
            if (!boxOk) logger.warn("⚠️ 'By Department' container not visible.");
            return false;

        } catch (Exception e) {
            logger.error("❌ Error while waiting for 'By Department' section: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Visible texts of department options.
     */
    public List<String> getByDepartmentItemTexts() {
        List<String> texts = new ArrayList<>();
        if (byDepartmentItems != null) {
            for (WebElement el : byDepartmentItems) {
                if (isDisplayedSafe(el)) {
                    String t = safeGetText(el);
                    if (t != null) {
                        t = t.trim();
                        if (!t.isEmpty()) texts.add(t);
                    }
                }
            }
        }
        logger.info("🗂️ By Department item texts: {}", texts);
        return texts;
    }

    /**
     * Check if a department pill/card is visible & clickable (without navigating).
     */
    public boolean isDepartmentClickableByLabel(String label) {
        if (label == null || label.trim().isEmpty()) return false;
        String want = label.trim().toLowerCase();
        try {
            List<WebElement> items = driver.findElements(byDepartmentItemsBy);
            for (WebElement el : items) {
                if (!isDisplayedSafe(el)) continue;
                String t = safeGetText(el).toLowerCase();
                if (t.equals(want) || t.contains(want)) {
                    try {
                        return el.isDisplayed() && el.isEnabled();
                    } catch (Exception ignore) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("❌ isDepartmentClickableByLabel('{}') failed: {}", label, e.toString());
        }
        return false;
    }

    /**
     * Click a department pill/card by its label.
     */
    public boolean clickByDepartmentByLabel(String label) {
        if (label == null || label.trim().isEmpty()) return false;
        String want = label.trim().toLowerCase();

        try {
            List<WebElement> items = driver.findElements(byDepartmentItemsBy);
            for (WebElement el : items) {
                if (!isDisplayedSafe(el)) continue;
                String t = safeGetText(el).toLowerCase();
                if (t.equals(want) || t.contains(want)) {
                    commonMethods.safeClick(driver, el, "By Department item '" + label + "'", 10);
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("❌ clickByDepartmentByLabel('{}') failed: {}", label, e.toString());
        }
        return false;
    }


    public void validateByStageAllAndReturn() {
        try {
            logger.info("🖱️ Validating each available stage → details → results → back (POM mode)");

            if (!waitForByStageVisible(Duration.ofSeconds(10))) {
                logger.error("'By Stage' section not visible.");
                throw new IllegalStateException("'By Stage' section not visible.");
            }

            @SuppressWarnings("unchecked") List<String> planned = (List<String>) ScenarioContext.get("byStage.available");
            if (planned == null || planned.isEmpty()) {
                planned = getByStageItemTexts();
            }

            if (planned == null || planned.isEmpty()) {
                logger.error("No stages available to click.");
                throw new IllegalStateException("No stages available to click.");
            }

            final long WARN_MS = ReusableCommonMethods.NAV_WARN_MS;
            final long FAIL_MS = ReusableCommonMethods.NAV_FAIL_MS;

            List<String> validated = new ArrayList<>();

            for (int idx = 0; idx < planned.size(); idx++) {
                if (!waitForByStageVisible(Duration.ofSeconds(10))) {
                    logger.error("'By Stage' section disappeared before clicking: {}", planned.get(idx));
                    throw new IllegalStateException("'By Stage' section disappeared before clicking: " + planned.get(idx));
                }

                String chosen = planned.get(idx) == null ? "" : planned.get(idx).trim();
                String tag = fullToTag(chosen);

                logger.info("🧩 [{}/{}] Stage: '{}'", idx + 1, planned.size(), chosen);

                Instant navStart = java.time.Instant.now();
                String urlBefore = safeGetUrl();

                boolean clickOk = clickByStageByLabel(chosen);
                if (!clickOk) {
                    logger.error("Failed to click stage option: {}", chosen);
                    throw new IllegalStateException("Failed to click stage option: " + chosen);
                }

                boolean loaded = waitForDetailsViewLoaded(Duration.ofSeconds(20));
                long loadMs = java.time.Duration.between(navStart, java.time.Instant.now()).toMillis();
                double loadSec = loadMs / 1000.0;

                if (!loaded) {
                    logger.error("❌ Stage Load Timeout: '{}' after ~{} ms", chosen, loadMs);
                    throw new IllegalStateException("Details view did not load for stage: " + chosen);
                }

                if (loadMs >= FAIL_MS) {
                    logger.error("❌ SLA Breach: '{}' loaded in {}s (≥ FAIL).", chosen, String.format("%.2f", loadSec));
                } else if (loadMs >= WARN_MS) {
                    logger.warn("⚠️ SLA Warning: '{}' loaded in {}s (≥ WARN).", chosen, String.format("%.2f", loadSec));
                } else {
                    logger.info("⏱️ '{}' loaded in {}s.", chosen, String.format("%.2f", loadSec));
                }

                boolean headerOk = basedOnHeaderContainsValue(chosen);
                if (!headerOk) {
                    logger.error("❌ Header Mismatch. Expected header to contain '{}'.", chosen);
                    throw new IllegalStateException("Header mismatch for stage: " + chosen);
                }

                boolean hasResults = hasAnyComplianceResults();
                if (!hasResults) {
                    logger.error("No compliance records found for stage: '{}'", chosen);
                    throw new IllegalStateException("No compliance records found for stage: " + chosen);
                }

                String urlAfter = safeGetUrl();
                logger.info("✅ Records visible for '{}'. URL: '{}' → '{}'", chosen, urlBefore, urlAfter);

                validated.add(chosen);

                // go back for next stage
                boolean backOk = clickGoBackAndWaitReports(Duration.ofSeconds(12));
                if (!backOk) {
                    logger.error("Could not navigate back to Reports after stage: '{}'", chosen);
                    throw new IllegalStateException("Could not navigate back to Reports after stage: " + chosen);
                }
                logger.info("↩️ Returned to Reports page for '{}'", chosen);

                try {
                    Thread.sleep(350);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.warn("Sleep interrupted while returning to Reports.", ie);
                }

                // keep last (legacy behavior)
                ScenarioContext.set("chosenStage", chosen);
            }

            ScenarioContext.set("byStage.validatedAll", Boolean.TRUE);
            ScenarioContext.set("byStage.validatedList", validated);
            logger.info("🎯 ByStage Combined Validation complete. Validated: {}", validated);

        } catch (Throwable t) {
            logger.error("Exception during 'ByStage combined step': {}", t.getMessage(), t);
            handleValidationException("ByStage combined step", t);
            throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
        }
    }


    public void validateByOrganizationAllAndReturn() {
        try {
            logger.info("🖱️ Validating each available organization → details → results → back (POM mode)");

            if (!waitForByOrganizationVisible(Duration.ofSeconds(10))) {
                logger.error("'By Organization' section not visible.");
                throw new IllegalStateException("'By Organization' section not visible.");
            }

            @SuppressWarnings("unchecked") List<String> planned = (List<String>) ScenarioContext.get("byOrg.available");
            if (planned == null || planned.isEmpty()) {
                planned = getByOrganizationItemTexts();
            }

            if (planned == null || planned.isEmpty()) {
                logger.error("No organizations available to click.");
                throw new IllegalStateException("No organizations available to click.");
            }

            final long WARN_MS = ReusableCommonMethods.NAV_WARN_MS;
            final long FAIL_MS = ReusableCommonMethods.NAV_FAIL_MS;

            List<String> validated = new ArrayList<>();

            for (int idx = 0; idx < planned.size(); idx++) {
                if (!waitForByOrganizationVisible(Duration.ofSeconds(10))) {
                    logger.error("'By Organization' section disappeared before clicking: {}", planned.get(idx));
                    throw new IllegalStateException("'By Organization' section disappeared before clicking: " + planned.get(idx));
                }

                String chosen = planned.get(idx) == null ? "" : planned.get(idx).trim();
                String tag = fullToTag(chosen); // kept if useful elsewhere

                logger.info("🧩 [{}/{}] Organization: '{}'", idx + 1, planned.size(), chosen);

                Instant navStart = java.time.Instant.now();
                String urlBefore = safeGetUrl();

                boolean clickOk = clickByOrganizationByLabel(chosen);
                if (!clickOk) {
                    logger.error("Failed to click organization: {}", chosen);
                    throw new IllegalStateException("Failed to click organization: " + chosen);
                }

                boolean loaded = waitForDetailsViewLoaded(Duration.ofSeconds(20));
                long loadMs = java.time.Duration.between(navStart, java.time.Instant.now()).toMillis();
                double loadSec = loadMs / 1000.0;

                if (!loaded) {
                    logger.error("❌ Organization Load Timeout: '{}' after ~{} ms", chosen, loadMs);
                    throw new IllegalStateException("Details view did not load for organization: " + chosen);
                }

                if (loadMs >= FAIL_MS) {
                    logger.error("❌ SLA Breach: '{}' loaded in {}s (≥ FAIL).", chosen, String.format("%.2f", loadSec));
                } else if (loadMs >= WARN_MS) {
                    logger.warn("⚠️ SLA Warning: '{}' loaded in {}s (≥ WARN).", chosen, String.format("%.2f", loadSec));
                } else {
                    logger.info("⏱️ '{}' loaded in {}s.", chosen, String.format("%.2f", loadSec));
                }

                boolean headerOk = basedOnHeaderContainsValue(chosen);
                if (!headerOk) {
                    logger.error("❌ Header Mismatch. Expected header to contain '{}'.", chosen);
                    throw new IllegalStateException("Header mismatch for organization: " + chosen);
                }

                boolean hasResults = hasAnyComplianceResults();
                if (!hasResults) {
                    logger.error("No compliance records found for organization: '{}'", chosen);
                    throw new IllegalStateException("No compliance records found for organization: " + chosen);
                }

                String urlAfter = safeGetUrl();
                logger.info("✅ Records visible for '{}'. URL: '{}' → '{}'", chosen, urlBefore, urlAfter);

                validated.add(chosen);

                // go back for next org
                boolean backOk = clickGoBackAndWaitReports(Duration.ofSeconds(12));
                if (!backOk) {
                    logger.error("Could not navigate back to Reports after organization: '{}'", chosen);
                    throw new IllegalStateException("Could not navigate back to Reports after organization: " + chosen);
                }
                logger.info("↩️ Returned to Reports page for '{}'", chosen);

                try {
                    Thread.sleep(350);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.warn("Sleep interrupted while returning to Reports.", ie);
                }

                // legacy single-step path: keep last selected
                ScenarioContext.set("chosenOrganization", chosen);
            }

            ScenarioContext.set("byOrg.validatedAll", Boolean.TRUE);
            ScenarioContext.set("byOrg.validatedList", validated);
            logger.info("🎯 ByOrganization Combined Validation complete. Validated: {}", validated);

        } catch (Throwable t) {
            logger.error("Exception during 'ByOrganization combined step': {}", t.getMessage(), t);
            handleValidationException("ByOrganization combined step", t);
            throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
        }
    }


    public void validateByDepartmentAllAndReturn() {
        try {
            logger.info("🖱️ Validating each available department → details → results → back (POM mode)");

            if (!waitForByDepartmentVisible(Duration.ofSeconds(10))) {
                logger.error("'By Department' section not visible.");
                throw new IllegalStateException("'By Department' section not visible.");
            }

            @SuppressWarnings("unchecked") List<String> planned = (List<String>) ScenarioContext.get("byDept.available");
            if (planned == null || planned.isEmpty()) {
                planned = getByDepartmentItemTexts();
            }

            if (planned == null || planned.isEmpty()) {
                logger.error("No departments available to click.");
                throw new IllegalStateException("No departments available to click.");
            }

            final long WARN_MS = ReusableCommonMethods.NAV_WARN_MS;
            final long FAIL_MS = ReusableCommonMethods.NAV_FAIL_MS;

            List<String> validated = new ArrayList<>();

            for (int idx = 0; idx < planned.size(); idx++) {
                if (!waitForByDepartmentVisible(Duration.ofSeconds(10))) {
                    logger.error("'By Department' section disappeared before clicking: {}", planned.get(idx));
                    throw new IllegalStateException("'By Department' section disappeared before clicking: " + planned.get(idx));
                }

                String chosen = planned.get(idx) == null ? "" : planned.get(idx).trim();
                String tag = fullToTag(chosen); // kept if useful elsewhere

                logger.info("🧩 [{}/{}] Department: '{}'", idx + 1, planned.size(), chosen);

                Instant navStart = java.time.Instant.now();
                String urlBefore = safeGetUrl();

                boolean clickOk = clickByDepartmentByLabel(chosen);
                if (!clickOk) {
                    logger.error("Failed to click department: {}", chosen);
                    throw new IllegalStateException("Failed to click department: " + chosen);
                }

                boolean loaded = waitForDetailsViewLoaded(Duration.ofSeconds(20));
                long loadMs = java.time.Duration.between(navStart, java.time.Instant.now()).toMillis();
                double loadSec = loadMs / 1000.0;

                if (!loaded) {
                    logger.error("❌ Department Load Timeout: '{}' after ~{} ms", chosen, loadMs);
                    throw new IllegalStateException("Details view did not load for department: " + chosen);
                }

                if (loadMs >= FAIL_MS) {
                    logger.error("❌ SLA Breach: '{}' loaded in {}s (≥ FAIL).", chosen, String.format("%.2f", loadSec));
                } else if (loadMs >= WARN_MS) {
                    logger.warn("⚠️ SLA Warning: '{}' loaded in {}s (≥ WARN).", chosen, String.format("%.2f", loadSec));
                } else {
                    logger.info("⏱️ '{}' loaded in {}s.", chosen, String.format("%.2f", loadSec));
                }

                boolean headerOk = basedOnHeaderContainsValue(chosen);
                if (!headerOk) {
                    logger.error("❌ Header Mismatch. Expected header to contain '{}'.", chosen);
                    throw new IllegalStateException("Header mismatch for department: " + chosen);
                }

                boolean hasResults = hasAnyComplianceResults();
                if (!hasResults) {
                    logger.error("No compliance records found for department: '{}'", chosen);
                    throw new IllegalStateException("No compliance records found for department: " + chosen);
                }

                String urlAfter = safeGetUrl();
                logger.info("✅ Records visible for '{}'. URL: '{}' → '{}'", chosen, urlBefore, urlAfter);

                validated.add(chosen);

                // go back for next department
                boolean backOk = clickGoBackAndWaitReports(Duration.ofSeconds(12));
                if (!backOk) {
                    logger.error("Could not navigate back to Reports after department: '{}'", chosen);
                    throw new IllegalStateException("Could not navigate back to Reports after department: " + chosen);
                }
                logger.info("↩️ Returned to Reports page for '{}'", chosen);

                try {
                    Thread.sleep(350);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.warn("Sleep interrupted while returning to Reports.", ie);
                }

                // legacy single-step path: keep last selected
                ScenarioContext.set("chosenDepartment", chosen);
            }

            ScenarioContext.set("byDept.validatedAll", Boolean.TRUE);
            ScenarioContext.set("byDept.validatedList", validated);
            logger.info("🎯 ByDepartment Combined Validation complete. Validated: {}", validated);

        } catch (Throwable t) {
            logger.error("Exception during 'ByDepartment combined step': {}", t.getMessage(), t);
            handleValidationException("ByDepartment combined step", t);
            throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
        }
    }


}

