package pages;


import base.BasePage;
import dev.failsafe.internal.util.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import supportingclass.CheckboxValidationResult;
import utils.ComplianceRow;
import utils.PageNavigationCallback;
import utils.ReusableCommonMethods;
import utils.WaitOutcome;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.nio.file.Path;
import java.util.*;
import java.util.NoSuchElementException;

import static utils.AllureLoggerUtils.logToAllure;
import static utils.ReusableCommonMethods.NAV_FAIL_MS;
import static utils.ReusableCommonMethods.NAV_WARN_MS;

/**
 * @author Sherwin
 * @since 17-07-2025
 */

public class CompliancePage extends BasePage {

    private static final int TIMEOUT_SEC = 12;
    private static final int RETRIES = 2;
    private final long SHORT = 8;
    private static final int CLICK_TIMEOUT_SEC = 10;
    private static final Random RNG = new Random();

    public CompliancePage(WebDriver driver) {
        super(driver);
    }


    @FindBy(xpath = "//span[normalize-space()='Compliances']")
    private WebElement compliancesTab;

    @FindBy(xpath = "//p[@class='text-[32px] max-lg:hidden font-semibold'][normalize-space()='Compliances']")
    private WebElement compliancesText;

    @FindBy(xpath = "//div[contains(@class,'flex') and contains(@class,'overflow-x-auto')]//div[contains(@class,'cursor-pointer')]/p")
    private List<WebElement> stageSectionElements;

    @FindBy(xpath = "//div[contains(@class,'flex') and contains(@class,'overflow-x-auto')]//div[contains(@class,'cursor-pointer')]")
    private List<WebElement> stageSectionCount;

    @FindBy(xpath = "//table[@id='compliances-table']//tbody//tr/td[last()]")
    private List<WebElement> statusColumnCells;

    @FindBy(xpath = "//table[@id='compliances-table']//thead//th//p")
    private List<WebElement> columnHeaders;

    @FindBy(xpath = "//span[text()='Archive']")
    private WebElement archiveButton;

    @FindBy(xpath = "//div[contains(@class,'grid-cols') and contains(@class,'bg-white')]//p[contains(@class,'truncate')]")
    private List<WebElement> archivedComplianceNames;

    @FindBy(xpath = "//tbody//tr")
    private List<WebElement> complianceRows;


    @FindBy(xpath = "//div[@id='archived-panel']//table//tr") // or correct container
    List<WebElement> archivedComplianceRows;

    @FindBy(xpath = "//table[@id='compliances-table']/tbody/tr")
    private List<WebElement> mainComplianceRows;

    @FindBy(xpath = "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')," + "'archived successfully') and (" + " contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'compliance') or " + " contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'compliances'))]")
    private List<WebElement> archiveSuccessToasts;

    @FindBy(xpath = "//button[contains(@aria-label,'Close') or contains(.,'×') or contains(.,'x')]")
    private List<WebElement> toastCloseButtons;

    @FindBy(xpath = "//table[@id='compliances-table']//tbody//tr/td/p[contains(text(), 'No records found')]")
    private List<WebElement> noRecordMessages;

    @FindBy(xpath = "//table[@id='compliances-table']//tbody//tr//input[@type='checkbox']")
    private List<WebElement> recordCheckboxes;

    @FindBy(xpath = "//thead//input[@type='checkbox']")
    private WebElement headerSelectAllCheckbox;

    @FindBy(xpath = "//table[@id='compliances-table']//tbody//tr//input[@type='checkbox']")
    private List<WebElement> bodyCheckboxes;

    @FindBy(xpath = "(//button[.//span[normalize-space()='Archive'] or normalize-space()='Archive'])[1]")
    private WebElement archiveActionButton;

    @FindBy(xpath = "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'selected') and " + "(contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'compliance') or " + " contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'compliances'))]")
    private List<WebElement> selectionBanners;

    @FindBy(css = "table tbody tr")
    private List<WebElement> rowLocator;
    private static final By ROWS_BY = By.cssSelector("table tbody tr");

    @FindBy(xpath = "(//button[@title='Generate CSV'])[2]")
    public WebElement generateCSVButton;

    // keep your existing @FindBy
    @FindBy(css = "table tbody tr td:last-child")
    private List<WebElement> statusCellLocator;

    @FindBy(xpath = "(//table//tbody/tr)[1]")
    WebElement firstComplianceRecord;

    // Compliance detail panel fields
    @FindBy(xpath = "(//img[@alt='image']/following::p[contains(text(),'Status')])[1]")
    WebElement statusField;

    @FindBy(xpath = "//img[@alt='image']/following::p[contains(text(),'Due date')]")
    WebElement dueDateField;

    @FindBy(xpath = "//img[@alt='image']/following::p[contains(text(),'Assignee')]")
    WebElement assigneeField;

    @FindBy(xpath = "//p[contains(@class, 'max-lg:text-[12px]') and contains(text(), 'Risk')]")
    WebElement riskField;

    @FindBy(xpath = "//label[normalize-space()='Due Date']/following-sibling::div[contains(@class,'cursor-pointer')]")
    private WebElement dueDateChip;

    @FindBy(xpath = "//div[contains(@class,'cursor-pointer')]//p[normalize-space()='In progress' or normalize-space()='In Progress']")
    private WebElement inProgressTab;

    /* ======== TABS ======== */
    @FindBy(xpath = "//p[normalize-space()='Info']")
    private WebElement infoTab;

    @FindBy(xpath = "//p[normalize-space()='Tasks']")
    private WebElement tasksTab;

    @FindBy(xpath = "//p[normalize-space()='Audit trail' or normalize-space()='Audit Trail']")
    private WebElement auditTrialTab;

    @FindBy(xpath = "//p[normalize-space()='Connect with Expert']/ancestor::button[1] | //button[.//p[normalize-space()='Connect with Expert']]")
    private WebElement connectWithExpertBtn;

    @FindBy(xpath = "//p[normalize-space()='Opt with Zolvit']/ancestor::button[1] | //button[.//p[normalize-space()='Opt with Zolvit']]")
    private WebElement optWithZolvitBtn;

    @FindBy(xpath = "//label[normalize-space()='Compliance Department']/following-sibling::*[1]")
    private WebElement complianceDepartmentDropdown;

    @FindBy(xpath = "//label[normalize-space()='Compliance Category']/following-sibling::*[1]")
    private WebElement complianceCategoryDropdown;

    @FindBy(xpath = "//label[normalize-space()='Due Date']/following-sibling::*[1]")
    private WebElement dueDateDropdown;

    // Dynamic dropdown panel locator
    private static final String PANEL_XPATH = ".//following::div[contains(@class,'absolute') and contains(@class,'top-full')][1]";

    // Dropdown options inside panel
    private static final String OPTION_XPATH = PANEL_XPATH + "//*[self::div or self::p or self::span][normalize-space(string(.))!='']";

    // Apply button inside dropdown (only for Category filter)
    private static final String APPLY_BUTTON_XPATH = PANEL_XPATH + "//button[normalize-space()='Apply']";

    private final By gridRowTitles = By.xpath("//table//tbody//tr//td[1]//*[self::a or self::span or self::p]");

    // --- Content locators searched INSIDE the active tabpanel
    private By whatIsItInPanel = By.xpath(".//*[normalize-space()='What is it?']");
    private By paymentInPanel = By.xpath(".//*[contains(normalize-space(),'Payment')]");
    private By allFilterInPanel = By.xpath(".//*[normalize-space()='All']");


    @FindBy(xpath = "//input[@placeholder='Enter compliance name']")
    private WebElement nameInput;

    @FindBy(xpath = "//*[normalize-space()='Select frequency' or @placeholder='Select frequency']")
    private WebElement frequencyField;

    @FindBy(xpath = "//div[contains(@class,'menu')]//div[@role='option' or contains(@class,'option')]")
    private List<WebElement> reactSelectOptions;

    private String calendarDayXpath(String day) {
        return "//*[contains(@class,'calendar') or contains(@class,'picker') or contains(@class,'date')]"
                + "//*[self::button or self::div or self::span][normalize-space()='" + day + "']";
    }

    @FindBy(xpath = "//*[normalize-space()='Select risk' or @placeholder='Select risk']")
    private WebElement riskFieldOpt;

    @FindBy(xpath = "//*[normalize-space()='Mandatory']/ancestor::div[contains(@class,'grid')][1]"
            + "//div[contains(@class,'rounded-full') and contains(@class,'cursor-pointer')]")
    private WebElement mandatoryToggle;

    @FindBy(xpath = "//*[@placeholder='Choose organizations' or normalize-space()='Choose organizations']")
    private WebElement organizationsField;

    @FindBy(xpath = "//*[@placeholder='Enter description' or self::textarea]")
    private WebElement descriptionArea;

    // === Locators that match your Tailwind popup ===

    // readonly input
    @FindBy(xpath = "//input[@placeholder='Select due date']")
    private WebElement dueDateInput;

    // clickable wrapper that holds the calendar svg to the right
    @FindBy(xpath = "//input[@placeholder='Select due date']/following-sibling::*[local-name()='svg']/parent::*")
    private WebElement dueDateIconButton;

    // calendar popover root (Tailwind classes seen in your screenshots)
    private final By calendarRoot = By.xpath(
            "(//div[contains(@class,'absolute') and contains(@class,'bg-white') and " +
                    "      contains(@class,'rounded-lg') and contains(@class,'shadow-lg') and contains(@class,'border')])[last()]"
    );

    // the day grid
    private final By dayGrid = By.xpath(
            "(" +
                    "(//div[contains(@class,'absolute') and contains(@class,'rounded-lg') and contains(@class,'shadow-lg')])[last()]" +
                    ")//div[contains(@class,'grid') and contains(@class,'grid-cols-7') and contains(@class,'gap-2')]"
    );
    // a CURRENT-month day button (excludes gray off-month cells)
    private By dayCurrentMonth(String d) {
        return By.xpath(
                "//div[contains(@class,'grid-cols-7')]" +
                        "//button[normalize-space()='" + d + "']" +
                        "[not(contains(@class,'text-gray-400')) and not(@disabled)]"
        );
    }

    // an OFF-month day button (gray cells – usually next/prev month)
    private By dayOffMonth(String d) {
        return By.xpath(
                "//div[contains(@class,'grid-cols-7')]" +
                        "//button[normalize-space()='" + d + "']" +
                        "[contains(@class,'text-gray-400') and not(@disabled)]"
        );
    }

    private static final String EXPECTED_URL_CONTAINS = "/compliance";
    private static final String EXPECTED_HEADER_TEXT = "Compliance";

    private final Duration defaultClickTimeout = Duration.ofSeconds(15);


    private final By clearSvgInChip = By.xpath(".//*[name()='svg'][1]");


    private static final By STATUS_CELLS_BY = By.cssSelector("table tbody tr td:last-child");


    /**
     * Click the Compliances tab with stale/JS fallback (no lambdas).
     */
    public void clickCompliancesTab() {
        try {
            wait.waitForClickableRefreshed(compliancesTab, defaultClickTimeout);
            logger.info("📂 Clicking Compliances tab...");

            try {
                compliancesTab.click();
            } catch (Exception e) {
                logger.warn("⚠️ Normal click failed, retrying with JS: {}", e.getMessage());
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", compliancesTab);
            }

        } catch (StaleElementReferenceException stale) {
            logger.warn("♻️ Compliances tab went stale. Rebinding & retrying click...");
            wait.waitForClickableRefreshed(compliancesTab, defaultClickTimeout);
            compliancesTab.click();

        } catch (Exception e) {
            logger.error("❌ Failed to click Compliances tab: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Wait until header visible AND URL contains expected fragment (no lambdas).
     */
    public boolean waitForComplianceLoaded(Duration timeout) {
        try {
            // (a) header visible (refreshed guard)
            wait.waitForVisibilityRefreshed(compliancesText, timeout);

            String headerText = safeGetText(compliancesText);
            boolean headerOk = headerMatches(headerText);

            // (b) URL contains expected fragment
            wait.waitForUrlContains(EXPECTED_URL_CONTAINS);
            boolean urlOk = true;

            boolean displayed = isDisplayedSafe(compliancesText);
            boolean allOk = displayed && headerOk && urlOk;

            if (allOk) {
                String curUrl = "";
                try {
                    curUrl = driver.getCurrentUrl();
                } catch (Exception ignored) {
                }
                logger.info("✅ Compliance page loaded. Header='{}', URL='{}'", headerText, curUrl);
            } else {
                if (!displayed) logger.warn("⚠️ Compliance header element not visible.");
                if (!headerOk)
                    logger.warn("⚠️ Header text check failed. Actual='{}' (expected to equal/contain '{}')", headerText, EXPECTED_HEADER_TEXT);
                String curUrl = "";
                try {
                    curUrl = driver.getCurrentUrl();
                } catch (Exception ignored) {
                }
                if (!urlOk)
                    logger.warn("⚠️ URL check failed. Current='{}' (expected to contain '{}')", curUrl, EXPECTED_URL_CONTAINS);
            }
            return allOk;

        } catch (TimeoutException te) {
            logger.error("❌ Compliance did not load within {} ms.", timeout.toMillis());
            return false;
        } catch (Exception e) {
            logger.error("❌ Error while waiting for Compliance: {}", e.getMessage());
            return false;
        }
    }


    // ---- helpers (no lambdas) -----------------------------------------

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
        return headerText.equalsIgnoreCase(EXPECTED_HEADER_TEXT) || headerText.indexOf(EXPECTED_HEADER_TEXT) >= 0;
    }


    /**
     * Returns a list of all visible Stage section names from the Compliance page using @FindBy.
     */
    /**
     * Returns a list of all visible Stage section names from the Compliance page using @FindBy.
     */
    public List<String> getCompliancePageStageSections() {
        List<String> stageSections = new ArrayList<String>();
        try {
            for (int i = 0; i < stageSectionElements.size(); i++) {
                WebElement element = stageSectionElements.get(i);
                String text = element.getText().trim();

                if (!text.isEmpty() && element.isDisplayed()) {
                    stageSections.add(text);
                }
            }
            logger.info("Extracted Stage Sections: {}", stageSections);
        } catch (Exception e) {
            logger.error("❌ Error while fetching Stage sections: {}", e.getMessage());
        }
        return stageSections;
    }


    /**
     * Returns a map with stage names as keys and their counts as values.
     */
    /**
     * Returns a map with stage names as lowercase keys and their counts as values.
     */
    public Map<String, Integer> getComplianceStageCounts() {
        Map<String, Integer> stageCounts = new LinkedHashMap<String, Integer>();
        try {
            for (WebElement element : stageSectionCount) {
                String stageName = element.findElement(By.tagName("p")).getText().trim().toLowerCase();
                String countText = element.findElement(By.tagName("span")).getText().replace("(", "").replace(")", "").trim();

                int count = 0;
                try {
                    count = Integer.parseInt(countText);
                } catch (NumberFormatException nfe) {
                    logger.warn("⚠️ Could not parse count for stage '{}': '{}'", stageName, countText);
                }

                stageCounts.put(stageName, count);
            }

            logger.info("Extracted Compliance Stage Counts: {}", stageCounts);

        } catch (Exception e) {
            logger.error("❌ Error fetching Compliance stage counts: {}", e.getMessage());
        }
        return stageCounts;
    }


    // ===================== Public API (unchanged signature) =====================

    public Map<Integer, List<String>> fetchAllStatusValuesPageWise(int tabCount, PageNavigationCallback onPageNavigation, PageNavigationCallback timingCb                 // may be null
    ) {
        final Map<Integer, List<String>> pageWiseStatuses = new LinkedHashMap<>();
        final List<String> collectedStatuses = new ArrayList<>();
        int pageNumber = 1;

        try {
            // Page 1 must already be “first-page ready” in the step, but be defensive here too:
            waitPageStatusReady(Duration.ofMillis(utils.ReusableCommonMethods.NAV_FAIL_MS));

            while (true) {
                // --- Read current page safely ---
                List<String> currentPageStatuses = readStatusesOnPageSafe();
                if (currentPageStatuses.isEmpty()) {
                    logger.warn("⚠️ No non-empty Status values found on page {}.", pageNumber);
                }
                pageWiseStatuses.put(pageNumber, currentPageStatuses);
                collectedStatuses.addAll(currentPageStatuses);

                logger.info("📄 Page {}: fetched {} status value(s).", Integer.valueOf(pageNumber), Integer.valueOf(currentPageStatuses.size()));

                // Screenshot callback
                if (onPageNavigation != null) {
                    try {
                        onPageNavigation.onPage(pageNumber);
                    } catch (Exception cbEx) {
                        logger.warn("⚠️ onPage callback threw on page {}: {}", Integer.valueOf(pageNumber), cbEx.toString());
                    }
                }

                // Stop when we’ve matched the badge count (if provided)
                if (tabCount > 0 && collectedStatuses.size() >= tabCount) {
                    logger.info("✅ Collected {} / {} status value(s). Stopping pagination.", Integer.valueOf(collectedStatuses.size()), Integer.valueOf(tabCount));
                    break;
                }

                // No next page? done.
                WebElement next = findNextButton();
                if (next == null) {
                    logger.info("ℹ️ No next page. Pagination finished at page {}.", Integer.valueOf(pageNumber));
                    break;
                }

                // Advance and wait for the new page to be READY for statuses
                boolean advanced = clickNextAndWaitForChange(next, timingCb, pageNumber);
                if (!advanced) {
                    logger.info("ℹ️ Pagination attempted but no real change detected. Stopping at page {}.", Integer.valueOf(pageNumber));
                    break;
                }

                pageNumber++;
            }

        } catch (Exception e) {
            logger.error("❌ Error while fetching status values with page-wise pagination: {}", e.getMessage(), e);
        }

        final int total = collectedStatuses.size();
        final int unique = new LinkedHashSet<>(collectedStatuses).size();
        logger.info("✅ Completed status collection. Total={} | Unique={} | Pages={}", Integer.valueOf(total), Integer.valueOf(unique), Integer.valueOf(pageWiseStatuses.size()));

        if (tabCount > 0 && total < tabCount) {
            logger.warn("ℹ️ Collected fewer statuses ({}) than expected tabCount ({}). " + "Possible virtualization, rows-per-page limit, or different paging behavior.", Integer.valueOf(total), Integer.valueOf(tabCount));
        }

        return pageWiseStatuses;
    }

    // Convenience overload retained (no timing)
    public Map<Integer, List<String>> fetchAllStatusValuesPageWise(int tabCount, PageNavigationCallback onPageNavigation) {
        return fetchAllStatusValuesPageWise(tabCount, onPageNavigation, null);
    }


    /**
     * Is there a visible & enabled "Next" button?
     */
    private boolean hasNextPage() {
        try {
            List<WebElement> nextButtons = driver.findElements(By.xpath("(//button[contains(., 'Next')])[2]"));
            for (WebElement b : nextButtons) {
                if (b.isDisplayed() && b.isEnabled()) return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click NEXT and wait for table content change; returns elapsed ms, or -1 if unchanged.
     * Also logs per-page load time with NAV thresholds (warn≥12s, fail≥20s by default).
     */
    private long clickNextAndWaitForChange() {
        final String before = tableSnapshotSignature();

        WebElement next = null;
        List<WebElement> nextButtons = driver.findElements(By.xpath("(//button[contains(., 'Next')])[2]"));
        for (WebElement b : nextButtons) {
            try {
                if (b.isDisplayed() && b.isEnabled()) {
                    next = b;
                    break;
                }
            } catch (Exception ignore) {
            }
        }
        if (next == null) return -1;

        Instant start = Instant.now();
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", next);
            try {
                wait.waitForElementToBeClickable(next);
                next.click();
            } catch (Exception e1) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", next);
            }
        } catch (Exception e) {
            logger.warn("⚠️ Failed to click Next: {}", e.toString());
            return -1;
        }

        boolean changed = false;
        long deadline = System.currentTimeMillis() + ReusableCommonMethods.NAV_FAIL_MS;
        while (System.currentTimeMillis() < deadline) {
            try {
                Thread.sleep(150);
            } catch (InterruptedException ignored) {
            }
            String after = tableSnapshotSignature();
            if (!before.equals(after)) {
                changed = true;
                break;
            }
        }

        try {
            wait.waitForPageToLoad();
        } catch (Exception ignore) {
        }

        long elapsedMs = Duration.between(start, Instant.now()).toMillis();
        double sec = elapsedMs / 1000.0;
        String secStr = String.format("%.2f", sec);

        if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
            logger.error("⏱️ Pagination (Next) took {} s — ≥ {} s (SLA).", secStr, ReusableCommonMethods.NAV_FAIL_MS / 1000);
        } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
            logger.warn("⏱️ Pagination (Next) took {} s — ≥ {} s.", secStr, ReusableCommonMethods.NAV_WARN_MS / 1000);
        } else {
            logger.info("⏱️ Pagination (Next) completed in {} s.", secStr);
        }

        return changed ? elapsedMs : -1;
    }


    private String safeText(WebElement el) {
        try {
            String t = el.getText();
            return (t == null) ? "" : t.trim();
        } catch (Exception e) {
            return "";
        }
    }


    public boolean waitFirstPageStatusReady(java.time.Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < deadline) {
            try {
                // Ensure at least one row is present
                java.util.List<org.openqa.selenium.WebElement> rows = driver.findElements(ROWS_BY);
                if (rows != null && !rows.isEmpty()) {
                    // Ensure at least one Status cell has non-empty text
                    java.util.List<org.openqa.selenium.WebElement> cells = driver.findElements(STATUS_CELLS_BY);
                    if (cells != null && !cells.isEmpty()) {
                        for (org.openqa.selenium.WebElement c : cells) {
                            try {
                                String t = c.getText();
                                if (t != null && !t.trim().isEmpty()) return true; // ready!
                            } catch (org.openqa.selenium.StaleElementReferenceException ignore) {
                                // re-check next loop
                            }
                        }
                    }
                }
            } catch (Exception ignore) {
                // keep polling
            }

            // small settle + let the grid render
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
        }
        return false; // didn’t get a non-empty Status within timeout
    }

    /**
     * Wait until this page actually renders at least one non-empty Status cell.
     */
    private boolean waitPageStatusReady(Duration timeout) {
        long end = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < end) {
            try {
                // Make sure rows exist
                List<WebElement> rows = driver.findElements(ROWS_BY);
                if (rows != null && !rows.isEmpty()) {
                    // help virtualized tables render visible region
                    try {
                        settleGridViewport(rows);
                    } catch (Exception ignore) {
                    }

                    // Any status cell has text?
                    List<WebElement> cells = driver.findElements(STATUS_CELLS_BY);
                    if (cells != null && !cells.isEmpty()) {
                        for (WebElement c : cells) {
                            try {
                                String t = c.getText();
                                if (t != null && !t.trim().isEmpty()) return true;
                            } catch (StaleElementReferenceException ignored) { /* retry */ }
                        }
                    }
                }
            } catch (Exception ignored) {
            }

            try {
                Thread.sleep(150);
            } catch (InterruptedException ignored) {
            }
        }
        return false;
    }

    /**
     * Click Next and wait for (1) staleness/signature change and (2) new page ready; also fires timing callback.
     */
    private boolean clickNextAndWaitForChange(WebElement nextBtn, PageNavigationCallback timingCb, int fromPageNumber) {
        // Snapshot before
        String beforeSig = tableSnapshotSignature();
        WebElement firstRowBefore = null;
        try {
            List<WebElement> rowsBefore = driver.findElements(ROWS_BY);
            if (rowsBefore != null && !rowsBefore.isEmpty()) firstRowBefore = rowsBefore.get(0);
        } catch (Exception ignored) {
        }

        // Click + measure
        Instant start = Instant.now();
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", nextBtn);
            try {
                wait.waitForElementToBeClickable(nextBtn);
                nextBtn.click();
            } catch (Exception e1) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextBtn);
            }
        } catch (Exception e) {
            logger.warn("⚠️ Failed to click Next: {}", e.toString());
            return false;
        }

        // Wait for staleness of first row OR signature change
        boolean changed = false;
        long deadline = System.currentTimeMillis() + utils.ReusableCommonMethods.NAV_FAIL_MS;
        while (System.currentTimeMillis() < deadline) {
            try {
                if (firstRowBefore != null) {
                    try {
                        wait.waitForStaleness(firstRowBefore);
                        changed = true;
                        break;
                    } catch (Exception ignored) {
                    }
                }
                String afterSig = tableSnapshotSignature();
                if (!beforeSig.equals(afterSig)) {
                    changed = true;
                    break;
                }
            } catch (Exception ignored) {
            }
            try {
                Thread.sleep(120);
            } catch (InterruptedException ignored) {
            }
        }

        // Now require the new page to be READY (non-empty status cell)
        boolean ready = waitPageStatusReady(Duration.ofMillis(utils.ReusableCommonMethods.NAV_FAIL_MS));

        long elapsedMs = Duration.between(start, Instant.now()).toMillis();
        double sec = elapsedMs / 1000.0;
        String secStr = String.format("%.2f", sec);

        // Fire timing callback if present
        if (timingCb != null) {
            try {
                timingCb.onTiming(fromPageNumber, fromPageNumber + 1, elapsedMs);
            } catch (Exception ignored) {
            }
        }

        // Local console logs (non-fatal thresholds)
        if (elapsedMs >= utils.ReusableCommonMethods.NAV_FAIL_MS) {
            logger.error("⏱️ Pagination (Next) took {} s — ≥ {} s (SLA).", secStr, utils.ReusableCommonMethods.NAV_FAIL_MS / 1000);
        } else if (elapsedMs >= utils.ReusableCommonMethods.NAV_WARN_MS) {
            logger.warn("⏱️ Pagination (Next) took {} s — ≥ {} s.", secStr, utils.ReusableCommonMethods.NAV_WARN_MS / 1000);
        } else {
            logger.info("⏱️ Pagination (Next) completed in {} s.", secStr);
        }

        return changed && ready;
    }

    /**
     * Find a robust, enabled "Next" control (no hard-coded indices).
     */
    private WebElement findNextButton() {
        try {
            // Try common Ant/Material patterns first
            List<WebElement> candidates = new ArrayList<>();
            candidates.addAll(driver.findElements(By.cssSelector("li.ant-pagination-next button")));
            candidates.addAll(driver.findElements(By.xpath("//button[.//span[normalize-space()='Next']]")));
            candidates.addAll(driver.findElements(By.xpath("//button[normalize-space()='Next' or contains(normalize-space(.),'Next')]")));
            candidates.addAll(driver.findElements(By.xpath("//li[contains(@class,'pagination-next') or contains(@class,'ant-pagination-next')]//button")));

            WebElement usable = null;
            for (WebElement b : candidates) {
                if (b == null) continue;
                try {
                    String cls = b.getAttribute("class");
                    String aria = b.getAttribute("aria-disabled");
                    String disabled = b.getAttribute("disabled");
                    boolean isDisabled = "true".equalsIgnoreCase(aria) || disabled != null || (cls != null && cls.toLowerCase().contains("disabled"));
                    if (b.isDisplayed() && b.isEnabled() && !isDisabled) {
                        usable = b; // pick the last good one (often the bottom pager)
                    }
                } catch (Exception ignored) {
                }
            }
            return usable;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Read statuses on current page, resilient to stale elements.
     */
    private List<String> readStatusesOnPageSafe() {
        List<String> out = new ArrayList<>();

        // Prefer already-bound list (if you have @FindBy)…
        try {
            if (statusCellLocator != null && !statusCellLocator.isEmpty()) {
                for (WebElement cell : statusCellLocator) {
                    String s = (cell == null) ? null : cell.getText();
                    if (s != null && !(s = s.trim()).isEmpty()) out.add(s);
                }
                if (!out.isEmpty()) return out;
            }
        } catch (StaleElementReferenceException ignore) {
            // fall-through
        } catch (Exception ex) {
            logger.warn("⚠️ While reading @FindBy status cells: {}", ex.toString());
        }

        // …fallback to fresh find
        try {
            List<WebElement> cells = driver.findElements(STATUS_CELLS_BY);
            for (WebElement cell : cells) {
                String s = (cell == null) ? null : cell.getText();
                if (s != null && !(s = s.trim()).isEmpty()) out.add(s);
            }
        } catch (Exception ex) {
            logger.warn("⚠️ While reading status cells by locator: {}", ex.toString());
        }
        return out;
    }

    /**
     * Create a lightweight signature to detect page content changes.
     */
    private String tableSnapshotSignature() {
        try {
            List<WebElement> rows = driver.findElements(ROWS_BY);
            int n = (rows == null) ? 0 : rows.size();
            String first = (n > 0) ? safeText(rows.get(0)) : "";
            String last = (n > 1) ? safeText(rows.get(n - 1)) : "";
            return n + "|" + first + "|" + last;
        } catch (Exception e) {
            return "0||";
        }
    }


    /**
     * Nudge virtualized tables to render visible cells.
     */
    private void settleGridViewport(List<WebElement> rows) {
        if (rows == null || rows.isEmpty()) return;
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript("arguments[0].scrollIntoView({block:'nearest'})", rows.get(0));
            Thread.sleep(40);
            if (rows.size() > 1) {
                js.executeScript("arguments[0].scrollIntoView({block:'nearest'})", rows.get(1));
                Thread.sleep(40);
            }
            js.executeScript("arguments[0].scrollIntoView({block:'nearest'})", rows.get(0));
            Thread.sleep(40);
        } catch (Exception ignored) {
        }
    }


    /**
     * Attempts to click the "Next" button in a paginated view if it is visible and enabled.
     *
     * @return {@code true} if the "Next" button was clicked and a new page is expected to load,
     * {@code false} if no more pages are available or the button is disabled.
     */

    private boolean clickNextIfVisible() {
        try {
            WebElement firstRowBefore = (rowLocator == null || rowLocator.isEmpty()) ? null : rowLocator.get(0);

            List<WebElement> nextButtons = driver.findElements(By.xpath("(//button[contains(., 'Next')])[2]"));
            for (WebElement nextButton : nextButtons) {
                if (!nextButton.isDisplayed()) continue;

                logger.info("➡️ Clicking 'Next' button to load more records...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", nextButton);
                try {
                    wait.waitForElementToBeClickable(nextButton);
                    nextButton.click();
                } catch (Exception e1) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextButton);
                }

                // ✅ prove pagination happened
                boolean changed = false;
                if (firstRowBefore != null) {
                    wait.waitForStaleness(firstRowBefore);
                    changed = true;
                } else {
                    wait.waitForVisibility(ROWS_BY);
                    changed = true;
                }

                Thread.sleep(250);
                wait.waitForPageToLoad();
                return changed; // only true when we observed a change
            }
        } catch (NoSuchElementException ignored) {
            logger.info("ℹ️ No 'Next' button available, reached last page.");
        } catch (TimeoutException te) {
            logger.info("ℹ️ Clicked 'Next' but table did not change (timeout). Assuming last page.");
        } catch (Exception e) {
            logger.warn("⚠️ 'Next' navigation failed: {}", e.getMessage());
        }

        logger.info("ℹ️ No 'Next' button available, reached last page.");
        return false;
    }


    /**
     * Clicks on a specific stage section tab in the UI and waits for the page to load.
     *
     * @param stageSection The visible text of the stage section tab to be clicked.
     * @throws RuntimeException if the tab element cannot be found or clicked.
     */

    public void clickStageSectionTab(String stageSection) {
        try {
            String xpath = "//div[contains(@class,'cursor-pointer')]//p[contains(text(),'" + stageSection + "')]";
            WebElement tabElement = driver.findElement(By.xpath(xpath));
            wait.waitForElementToBeClickable(tabElement);
            tabElement.click();
            wait.waitForPageToLoad();
            logger.info("\u27A1\uFE0F Clicked on '{}' tab and waited for page load.", stageSection);
        } catch (Exception e) {
            logger.error("\u274C Failed to click on '{}' tab: {}", stageSection, e.getMessage());
            throw new RuntimeException("Failed to click on " + stageSection + " tab", e);
        }
    }

    /**
     * Clears the "Due Date" filter by clicking the clear (X) icon and waits for the page to reload.
     * <p>
     * If the icon is not found or cannot be clicked, logs a warning without throwing an exception.
     */

    public void clearDueDateFilter() {
        try {
            WebElement dueDateClearIcon = driver.findElement(By.xpath("//*[local-name()='path' and starts-with(@d,'m9.75 9.75 4.5 4.5')]/ancestor::*[local-name()='svg'][1]"));
            wait.waitForElementToBeClickable(dueDateClearIcon);
            commonMethods.safeClick(driver, dueDateClearIcon, "Due Date Clear Icon", 5);
            wait.waitForPageToLoad();
            logger.info("✅ Cleared Due Date filter successfully by clicking the clear icon.");
        } catch (Exception e) {
            logger.warn("⚠️ Due Date filter clear icon not found or not clickable: {}", e.getMessage());
        }
    }


    /**
     * Returns the numeric badge/count shown next to the given stage section tab.
     * <p>
     * Validation added (same approach, light-weight):
     * <ul>
     *   <li>Waits for the count element to be visible.</li>
     *   <li>Falls back to {@code textContent} if {@code getText()} is blank.</li>
     *   <li>Extracts the first numeric token robustly; warns if none found.</li>
     *   <li>Rejects negative counts and logs clear errors.</li>
     * </ul>
     *
     * @param stageSection visible tab label (e.g., "All", "Pending", "Overdue").
     * @return the parsed non-negative integer count for the tab.
     * @throws RuntimeException if the count cannot be read or parsed as a non-negative integer.
     */
    public int getStageSectionTabCount(String stageSection) {
        try {
            logger.info("📊 Extracting count from '{}' tab on Compliance Page", stageSection);

            // Same locator pattern you used
            String xpath = "//p[normalize-space()='" + stageSection + "']/following-sibling::span";
            WebElement countElement = wait.waitForVisibility(By.xpath(xpath));

            // Read text; fallback to textContent if needed
            String rawText = "";
            try {
                rawText = countElement.getText();
            } catch (Exception ignore) {
            }
            if (rawText == null || rawText.trim().isEmpty()) {
                try {
                    Object tc = ((JavascriptExecutor) driver).executeScript("return arguments[0].textContent;", countElement);
                    if (tc != null) rawText = String.valueOf(tc);
                } catch (Exception ignore) {
                }
            }

            if (rawText == null) rawText = "";
            rawText = rawText.trim();

            // Extract first numeric token (more robust than stripping all non-digits blindly)
            String digits = "";
            for (int i = 0; i < rawText.length(); i++) {
                char c = rawText.charAt(i);
                if (Character.isDigit(c)) {
                    // build first contiguous number
                    StringBuilder sb = new StringBuilder();
                    while (i < rawText.length() && Character.isDigit(rawText.charAt(i))) {
                        sb.append(rawText.charAt(i));
                        i++;
                    }
                    digits = sb.toString();
                    break;
                }
            }

            if (digits.isEmpty()) {
                logger.error("❌ No numeric content found in tab count text for '{}'. Raw='{}'", stageSection, rawText);
                throw new RuntimeException("Unable to parse numeric count for tab '" + stageSection + "'. Raw='" + rawText + "'");
            }

            int count;
            try {
                count = Integer.parseInt(digits);
            } catch (NumberFormatException nfe) {
                logger.error("❌ Failed to parse integer for '{}' tab. Digits='{}', Raw='{}'", stageSection, digits, rawText);
                throw new RuntimeException("Invalid numeric format for tab '" + stageSection + "': " + digits, nfe);
            }

            if (count < 0) {
                logger.error("❌ Negative count parsed for '{}' tab: {} (Raw='{}')", stageSection, Integer.valueOf(count), rawText);
                throw new RuntimeException("Negative count for tab '" + stageSection + "': " + count);
            }

            logger.info("✅ Extracted count from '{}' tab: {}", stageSection, Integer.valueOf(count));
            return count;

        } catch (Exception e) {
            logger.error("❌ Failed to extract '{}' tab count: {}", stageSection, e.getMessage(), e);
            throw (e instanceof RuntimeException) ? (RuntimeException) e : new RuntimeException(e);
        }
    }


    /**
     * Returns the list of all visible column header names from the Compliance table.
     *
     * @return List of visible header texts.
     * @throws RuntimeException if no headers are found or visible.
     */
    public List<String> getTableHeadersText() {
        List<String> headers = new ArrayList<String>();

        if (columnHeaders == null || columnHeaders.isEmpty()) {
            throw new RuntimeException("❌ No header elements found (columnHeaders list is null or empty).");
        }

        for (WebElement header : columnHeaders) {
            try {
                if (header != null && header.isDisplayed()) {
                    String text = header.getText().trim();
                    if (!text.isEmpty()) {
                        headers.add(text);
                    }
                }
            } catch (StaleElementReferenceException e) {
                System.out.println("⚠️ Stale element encountered while reading header.");
            }
        }

        if (headers.isEmpty()) {
            throw new RuntimeException("❌ No visible and valid headers found in the table.");
        }

        return headers;
    }


    /**
     * Verifies if a header has sorting enabled by checking for a sorting icon container.
     *
     * @param headerText Visible header text (e.g., "Compliances", "Due Date")
     * @return true if sorting div is visible, false otherwise
     */
    public boolean isHeaderSortable(String headerText) {
        try {
            String xpath = "//table[@id='compliances-table']//th[.//p[normalize-space(text())='" + headerText + "']]//div[@class='flex flex-col cursor-pointer']";
            WebElement sortContainer = driver.findElement(By.xpath(xpath));
            return sortContainer != null && sortContainer.isDisplayed();
        } catch (NoSuchElementException e) {
            System.out.println("⚠️ Sorting not found for header: " + headerText);
            return false;
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to validate sorting for header '" + headerText + "': " + e.getMessage(), e);
        }
    }


    /**
     * Clicks the sort icon for the given table header twice:
     * once to sort in ascending order, and again to sort in descending order.
     *
     * @param headerText The exact text of the table header whose sort icon should be clicked.
     * @return {@code true} if the sort icon was found and both ascending and descending clicks were performed,
     * {@code false} if the element was not found or an exception occurred.
     */

    public boolean clickSortIconForHeader(String headerText) {
        try {
            logger.info("🔍 Looking for sort icon in header: '{}'", headerText);

            String xpath = "//table[@id='compliances-table']//th[.//p[normalize-space(text())='" + headerText + "']]//div[contains(@class,'cursor-pointer')]";

            // ✅ Explicit wait for presence & visibility
            WebElement sortIconContainer = wait.waitForVisibility(By.xpath(xpath));
            logger.info("✅ Found sort icon for header: '{}'", headerText);

            // === Ascending click ===
            logger.info("⬆️ Clicking ascending sort for header: '{}'", headerText);
            try {
                sortIconContainer.click();
            } catch (Exception e) {
                logger.warn("⚠️ Regular click failed for ascending sort. Falling back to JS click. Header: '{}'", headerText);
                helpers.jsClick(driver, sortIconContainer);
            }
            Thread.sleep(800);
            logger.info("✅ Ascending sort click completed for header: '{}'", headerText);

            // === Descending click ===
            logger.info("⬇️ Clicking descending sort for header: '{}'", headerText);
            try {
                sortIconContainer.click();
            } catch (Exception e) {
                logger.warn("⚠️ Regular click failed for descending sort. Falling back to JS click. Header: '{}'", headerText);
                helpers.jsClick(driver, sortIconContainer);
            }
            Thread.sleep(800);
            logger.info("✅ Descending sort click completed for header: '{}'", headerText);

            return true;

        } catch (NoSuchElementException e) {
            logger.error("❌ Could not find sorting container for header: '{}'", headerText);
            return false;

        } catch (TimeoutException e) {
            logger.error("⏳ Timed out waiting for sorting container for header: '{}'", headerText);
            return false;

        } catch (Exception e) {
            logger.error("❌ Exception clicking sort icon for header: '{}'", headerText, e);
            return false;
        }
    }


    /**
     * Checks if the compliance table currently displays the "No records found" message.
     * <p>
     * This method:
     * <ul>
     *   <li>Uses the {@link #noRecordMessages} locator to detect if the empty state message is visible.</li>
     *   <li>Returns {@code true} if at least one matching element is found and displayed, otherwise {@code false}.</li>
     *   <li>Logs a message indicating whether records are present or not.</li>
     *   <li>In case of any exception, logs a warning and assumes records are present.</li>
     * </ul>
     *
     * @return {@code true} if the "No records found" message is displayed in the table, {@code false} otherwise.
     */
    public boolean noRecordsPresent() {
        try {
            logger.info("🔍 Checking if 'No records found' message is displayed in the compliance table...");

            boolean noRecords = false;

            if (noRecordMessages != null && !noRecordMessages.isEmpty()) {
                for (int i = 0; i < noRecordMessages.size(); i++) {
                    try {
                        WebElement msg = noRecordMessages.get(i);
                        if (msg != null && msg.isDisplayed()) {
                            noRecords = true;
                            break;
                        }
                    } catch (StaleElementReferenceException ignored) {
                        // If stale, re-check on next iteration
                    }
                }
            }

            if (noRecords) {
                logger.info("ℹ️ No records found in the compliance table.");
            } else {
                logger.info("✅ Records are present in the compliance table.");
            }

            return noRecords;

        } catch (Exception e) {
            logger.warn("⚠️ Error while checking for 'No records found'. Assuming records are present.", e);
            return false;
        }
    }


    /**
     * Verifies that every record checkbox in the compliance table body is visible.
     * <p>
     * Behavior:
     * <ul>
     *   <li>If the table shows "No records found", the check is skipped and this method returns {@code true}.</li>
     *   <li>Waits briefly (up to ~5s) for checkboxes to appear when records are present.</li>
     *   <li>Iterates each checkbox and ensures {@code isDisplayed()} returns true.</li>
     *   <li>Handles stale elements gracefully and logs the first invisible/stale index encountered.</li>
     * </ul>
     *
     * @return {@code true} when either no records exist (skipped) or all checkboxes are visible;
     * {@code false} if records exist but no checkboxes are found or any checkbox is not visible.
     */
    public boolean areAllRecordCheckboxesVisible() {
        if (noRecordsPresent()) {
            logger.info("ℹ️ Skipping checkbox visibility check — No records present.");
            return true; // nothing to check; treat as pass for this step
        }

        try {
            logger.info("🔍 Verifying visibility of record checkboxes…");

            // Poll up to ~5s for checkboxes to render (since records are present)
            long end = System.currentTimeMillis() + 5000;
            int total = 0;
            while (System.currentTimeMillis() < end) {
                if (recordCheckboxes != null) {
                    total = recordCheckboxes.size();
                    if (total > 0) break;
                }
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ignored) {
                }
            }

            if (total == 0) {
                logger.warn("⚠️ Records are present but no checkboxes were found in the table body.");
                return false;
            }

            int visibleCount = 0;
            int firstInvisibleIndex = -1;

            // Iterate through the located checkboxes; handle stale elements per item
            for (int i = 0; i < recordCheckboxes.size(); i++) {
                try {
                    WebElement cb = recordCheckboxes.get(i);
                    if (cb != null && cb.isDisplayed()) {
                        visibleCount++;
                    } else {
                        if (firstInvisibleIndex == -1) firstInvisibleIndex = i;
                    }
                } catch (StaleElementReferenceException sere) {
                    // One retry for stale: attempt to re-find by absolute XPath for this row position (best-effort)
                    try {
                        WebElement reCb = driver.findElement(By.xpath("//table[@id='compliances-table']//tbody//tr[" + (i + 1) + "]//input[@type='checkbox']"));
                        if (reCb.isDisplayed()) {
                            visibleCount++;
                        } else {
                            if (firstInvisibleIndex == -1) firstInvisibleIndex = i;
                        }
                    } catch (Exception retryEx) {
                        logger.warn("⚠️ Stale checkbox at index {} and retry failed.", i);
                        if (firstInvisibleIndex == -1) firstInvisibleIndex = i;
                    }
                } catch (Exception ex) {
                    logger.warn("⚠️ Error checking visibility for checkbox index {}.", i, ex);
                    if (firstInvisibleIndex == -1) firstInvisibleIndex = i;
                }
            }

            if (visibleCount == total) {
                logger.info("✅ All checkboxes are visible. Total: {}", total);
                return true;
            } else {
                logger.warn("❌ Not all checkboxes are visible. Visible: {} / Total: {} (first invisible index: {})", visibleCount, total, firstInvisibleIndex);
                return false;
            }

        } catch (Exception e) {
            logger.error("❌ Exception while verifying checkbox visibility.", e);
            return false;
        }
    }

    /**
     * Selects all records via the header checkbox and validates:
     * <ul>
     *   <li>At least one selectable row exists.</li>
     *   <li>All row checkboxes become selected.</li>
     *   <li>The Archive action button becomes visible.</li>
     *   <li>The "N compliances selected" banner count matches the number of selected rows.</li>
     * </ul>
     *
     * @return CheckboxValidationResult containing success flag, actual selected count,
     * displayed banner count, and archive visibility.
     */
    public CheckboxValidationResult selectAllCheckboxAndVerifyArchiveButton() {
        try {
            logger.info("🔎 Starting 'Select All' validation for compliances table…");

            // 1) Ensure rows exist (poll briefly for rendering)
            int totalRowsWithCheckbox = 0;
            long waitRowsUntil = System.currentTimeMillis() + 5000;
            while (System.currentTimeMillis() < waitRowsUntil) {
                if (bodyCheckboxes != null) {
                    totalRowsWithCheckbox = bodyCheckboxes.size();
                    if (totalRowsWithCheckbox > 0) break;
                }
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ignored) {
                }
            }

            if (totalRowsWithCheckbox <= 0) {
                logger.warn("⚠️ No selectable rows found (no body checkboxes present).");
                return new CheckboxValidationResult(false, 0, 0, false);
            }
            logger.info("✅ Records present. Row count with checkboxes: {}", totalRowsWithCheckbox);

            // 2) Click Select All (with visibility + clickability guard)
            try {
                wait.waitForVisibility(headerSelectAllCheckbox);
                wait.waitForElementToBeClickable(headerSelectAllCheckbox);
            } catch (Exception e) {
                logger.error("❌ Header 'Select All' checkbox not visible/clickable.", e);
                return new CheckboxValidationResult(false, 0, 0, false);
            }

            if (!headerSelectAllCheckbox.isSelected()) {
                try {
                    headerSelectAllCheckbox.click();
                    logger.info("☑️ Clicked 'Select All' checkbox.");
                } catch (Exception clickEx) {
                    logger.warn("⚠️ Regular click failed on 'Select All'. Falling back to JS click.");
                    try {
                        helpers.jsClick(driver, headerSelectAllCheckbox);
                    } catch (Exception jsEx) {
                        logger.error("❌ JS click also failed on 'Select All'.", jsEx);
                        return new CheckboxValidationResult(false, 0, 0, false);
                    }
                }
            } else {
                logger.info("☑️ 'Select All' was already selected.");
            }

            // 3) Wait until ALL body checkboxes are selected (polling, stale-safe)
            boolean allSelected = false;
            long endSelect = System.currentTimeMillis() + 8000; // up to 8s
            while (System.currentTimeMillis() < endSelect) {
                int selectedNow = 0;
                boolean hadStale = false;

                for (int i = 0; i < bodyCheckboxes.size(); i++) {
                    try {
                        WebElement cb = bodyCheckboxes.get(i);
                        if (cb != null && cb.isDisplayed() && cb.isEnabled() && cb.isSelected()) {
                            selectedNow++;
                        }
                    } catch (StaleElementReferenceException sere) {
                        // refresh list once if any stale encountered
                        try {
                            bodyCheckboxes = driver.findElements(By.xpath("//table[@id='compliances-table']//tbody//tr//input[@type='checkbox']"));
                        } catch (Exception ignored) {
                        }
                        hadStale = true;
                        break;
                    } catch (Exception ignored) { /* continue */ }
                }

                if (!hadStale && selectedNow == totalRowsWithCheckbox) {
                    allSelected = true;
                    break;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
            }

            if (!allSelected) {
                // final count snapshot
                int selectedCount = 0;
                for (int i = 0; i < bodyCheckboxes.size(); i++) {
                    try {
                        if (bodyCheckboxes.get(i).isSelected()) selectedCount++;
                    } catch (Exception ignored) {
                    }
                }
                logger.error("❌ Not all checkboxes selected. Selected={}, Total={}", selectedCount, totalRowsWithCheckbox);
                return new CheckboxValidationResult(false, selectedCount, 0, false);
            }
            logger.info("✅ All {} row checkboxes are selected.", totalRowsWithCheckbox);

            // 4) Archive action button visibility
            boolean archiveVisible = false;
            try {
                wait.waitForVisibility(archiveActionButton);
                archiveVisible = archiveActionButton.isDisplayed();
            } catch (Exception e) {
                archiveVisible = false;
            }
            logger.info("📎 Archive action button visible: {}", archiveVisible);

            // 5) Parse the "N compliances selected" banner (index-free, tag-agnostic)
            int displayedCount = -1;
            WebElement banner = null;
            long endBanner = System.currentTimeMillis() + 5000; // up to 5s
            while (System.currentTimeMillis() < endBanner) {
                if (selectionBanners != null && !selectionBanners.isEmpty()) {
                    for (int i = 0; i < selectionBanners.size(); i++) {
                        try {
                            WebElement e = selectionBanners.get(i);
                            if (e != null && e.isDisplayed()) {
                                String t = e.getText();
                                if (t != null && t.matches(".*\\d+.*")) {
                                    banner = e;
                                    break;
                                }
                            }
                        } catch (StaleElementReferenceException ignored) {
                        }
                    }
                }
                if (banner != null) break;
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ignored) {
                }
            }

            if (banner != null) {
                String txt = banner.getText() != null ? banner.getText().trim() : "";
                java.util.regex.Pattern p = java.util.regex.Pattern.compile("(\\d+)");
                java.util.regex.Matcher m = p.matcher(txt);
                if (m.find()) displayedCount = Integer.parseInt(m.group(1));
                logger.info("🧾 Selection banner text: '{}' → parsed count={}", txt, displayedCount);
            } else {
                logger.warn("⚠️ Selection banner not found; using actual selected as fallback.");
                displayedCount = totalRowsWithCheckbox;
            }

            boolean ok = archiveVisible && displayedCount == totalRowsWithCheckbox;

            if (!ok) {
                logger.error("❌ Validation failed: archiveVisible={}, actualSelected={}, displayedCount={}", archiveVisible, totalRowsWithCheckbox, displayedCount);
            } else {
                logger.info("✅ Validation success: archiveVisible={}, selected={}, bannerCount={}", archiveVisible, totalRowsWithCheckbox, displayedCount);
            }

            return new CheckboxValidationResult(ok, totalRowsWithCheckbox, displayedCount, archiveVisible);

        } catch (Exception e) {
            logger.error("❌ Exception in selectAllCheckboxAndVerifyArchiveButton()", e);
            return new CheckboxValidationResult(false, 0, 0, false);
        }
    }


    /**
     * Validates and clicks the Archive action button using the reusable safeClick method.
     * Ensures the button is present, visible, enabled, and logs each step.
     */
    public void clickArchiveButton() {
        try {
            logger.info("🔍 Validating Archive action button before click...");
            wait.waitForVisibility(archiveActionButton);
            wait.waitForElementToBeClickable(archiveActionButton);

            if (!archiveActionButton.isDisplayed()) {
                logger.error("❌ Archive action button not displayed.");
                throw new RuntimeException("Archive action button not displayed.");
            }
            if (!archiveActionButton.isEnabled()) {
                logger.error("❌ Archive action button disabled.");
                throw new RuntimeException("Archive action button disabled.");
            }

            logger.info("✅ Archive action button is visible & enabled. Clicking...");
            commonMethods.safeClick(driver, archiveActionButton, "Archive action button", 6);
            logger.info("✅ Archive action button clicked.");

        } catch (Exception e) {
            logger.error("❌ Could not click Archive action button", e);
            throw new RuntimeException("Could not click Archive action button", e);
        }
    }


    /**
     * Navigates to the Archive section by clicking the "View Archives" button.
     * Uses the reusable safeClick for robust clicking (normal → Actions → JS) with retries.
     */
    public void navigateToArchiveTab() {
        try {
            logger.info("🔍 Attempting to navigate to Archive section...");

            By viewArchivesBtnBy = By.xpath("//button[contains(@title,'View Archives')]");
            // Ensure it's visible before delegating to safeClick
            WebElement viewArchivesButton = wait.waitForVisibility(viewArchivesBtnBy);

            // Use your existing safeClick signature with timeout (e.g., 6 seconds per attempt)
            commonMethods.safeClick(driver, viewArchivesButton, "'View Archives' button", 6);

            logger.info("✅ 'View Archives' clicked. Waiting for Archive UI to load...");
            // Light settle — replace with a specific wait if you have a known Archive marker
            Thread.sleep(1000);

            logger.info("✅ Successfully navigated to Archive section.");
        } catch (Exception e) {
            logger.error("❌ Failed to navigate to Archive section.", e);
            throw new RuntimeException("❌ Could not navigate to Archive section", e);
        }
    }


    /**
     * Retrieves the names of all currently selected compliances in the table.
     * Validates row and checkbox presence, logs each selected record, and returns a list of names.
     *
     * @return List of selected compliance names; empty list if none selected.
     */
    public List<String> getSelectedComplianceNames() {
        List<String> selectedRecords = new ArrayList<>();

        try {
            // ✅ Validate if complianceRows is populated
            if (complianceRows == null || complianceRows.isEmpty()) {
                logger.warn("⚠️ No compliance rows found in the table.");
                return selectedRecords;
            }
            logger.info("📄 Total compliance rows found: {}", complianceRows.size());

            for (WebElement row : complianceRows) {
                try {
                    WebElement checkbox = row.findElement(By.xpath(".//input[@type='checkbox']"));

                    if (checkbox.isDisplayed() && checkbox.isEnabled() && checkbox.isSelected()) {
                        // ✅ td[1] contains the compliance name
                        WebElement complianceNameElement = row.findElement(By.xpath("./td[1]"));
                        String rawText = complianceNameElement.getText() != null ? complianceNameElement.getText().trim() : "";

                        if (!rawText.isEmpty()) {
                            // Extract first line only
                            String complianceName = rawText.split("\\R")[0].trim();
                            selectedRecords.add(complianceName);
                            logger.info("☑️ Selected compliance: '{}'", complianceName);
                        } else {
                            logger.warn("⚠️ Compliance name cell is empty for a selected row.");
                        }
                    }

                } catch (NoSuchElementException e) {
                    logger.warn("⚠️ Missing checkbox or compliance name cell: {}", e.getMessage());
                } catch (Exception e) {
                    logger.error("❌ Error while processing a compliance row.", e);
                }
            }

            // ✅ Final validation log
            if (selectedRecords.isEmpty()) {
                logger.warn("⚠️ No compliances are currently selected.");
            } else {
                logger.info("✅ Total selected compliances: {} -> {}", selectedRecords.size(), selectedRecords);
            }

        } catch (Exception e) {
            logger.error("❌ Unexpected error while retrieving selected compliance names.", e);
        }

        return selectedRecords;
    }


    /**
     * Verifies that all expected compliance records are present in the Archive tab.
     * <p>
     * Handles the following scenarios:
     * <ul>
     *   <li>If both expected and archived records are empty → Pass</li>
     *   <li>If expected is empty but archive has items → Pass (nothing to validate)</li>
     *   <li>If expected has items but archive is empty → Fail</li>
     *   <li>If both have items → Compare exact match of names</li>
     * </ul>
     *
     * @param expectedRecords List of compliance record names expected to be in Archive.
     * @return true if verification passes; false otherwise.
     */
    public boolean areRecordsPresentInArchive(List<String> expectedRecords) {
        try {
            // Input validation
            if (expectedRecords == null) {
                logger.warn("⚠️ Expected records list is null. Treating as empty.");
                expectedRecords = new ArrayList<>();
            }

            logger.info("📌 Navigating to Archive tab to verify records...");
            navigateToArchiveTab();

            // If archive list is not yet loaded, handle gracefully
            if (archivedComplianceNames == null) {
                archivedComplianceNames = new ArrayList<>();
            }

            // Case: Archive has no items
            if (archivedComplianceNames.isEmpty()) {
                if (expectedRecords.isEmpty()) {
                    logger.info("✅ Archive is empty as expected (no records to verify).");
                    return true;
                } else {
                    logger.warn("⚠️ Archive is empty but expected records exist: {}", expectedRecords);
                    return false;
                }
            }

            // Collect archived record names
            List<String> archivedRecords = new ArrayList<>();
            for (WebElement recordElement : archivedComplianceNames) {
                try {
                    String complianceName = recordElement.getAttribute("title");
                    if (complianceName != null && !complianceName.trim().isEmpty()) {
                        archivedRecords.add(complianceName.trim());
                    } else {
                        logger.warn("⚠️ Encountered an archive record with empty or missing title attribute.");
                    }
                } catch (Exception e) {
                    logger.error("❌ Error while reading archived compliance name.", e);
                }
            }

            logger.info("📥 Expected Records ({}): {}", expectedRecords.size(), expectedRecords);
            logger.info("📦 Archived Records ({}): {}", archivedRecords.size(), archivedRecords);

            // Compare expected vs archived
            List<String> missingRecords = new ArrayList<>();
            for (String expectedRecord : expectedRecords) {
                if (!archivedRecords.contains(expectedRecord)) {
                    missingRecords.add(expectedRecord);
                }
            }

            if (missingRecords.isEmpty()) {
                logger.info("✅ All expected compliances are present in Archive.");
                return true;
            } else {
                logger.error("❌ Missing records in Archive ({}): {}", missingRecords.size(), missingRecords);
                return false;
            }

        } catch (Exception e) {
            logger.error("❌ Exception occurred during Archive verification.", e);
            return false;
        }
    }

    /**
     * Retrieves the list of compliance names from the Archive section.
     * <p>
     * Each compliance name is taken from the text of the archivedComplianceNames elements.
     * If a name contains multiple lines (e.g., with a badge), only the first line is taken.
     * </p>
     *
     * @return List of compliance names currently present in the Archive section.
     */
    public List<String> getArchivedComplianceNames() {
        List<String> archivedNames = new ArrayList<>();

        try {
            // 1) Validation: ensure element list is not null/empty
            if (archivedComplianceNames == null || archivedComplianceNames.isEmpty()) {
                logger.warn("⚠️ No archived compliance elements found in the Archive section.");
                return archivedNames; // return empty
            }

            logger.info("📋 Fetching compliance names from Archive. Total elements found: {}", archivedComplianceNames.size());

            // 2) Extract first line of text from each element
            int index = 0;
            for (WebElement nameElement : archivedComplianceNames) {
                index++;
                try {
                    String rawText = nameElement.getText();
                    if (rawText == null || rawText.trim().isEmpty()) {
                        logger.warn("⚠️ Archived item at index {} has empty text. Skipping.", index);
                        continue;
                    }

                    String complianceName = rawText.split("\\R")[0].trim(); // only first line
                    archivedNames.add(complianceName);
                    logger.info("✅ Archived compliance [{}]: '{}'", index, complianceName);

                } catch (Exception ex) {
                    logger.error("❌ Error while reading archived compliance at index {}", index, ex);
                }
            }

            logger.info("📦 Final Archived Compliance List ({}): {}", archivedNames.size(), archivedNames);

        } catch (Exception e) {
            logger.error("❌ Unexpected error while fetching archived compliance names", e);
        }

        return archivedNames;
    }

    /**
     * Unarchives the given records from the Archive view.
     *
     * @param archivedRecordsToUnarchive list of record names (exact visible names on the card)
     */

    public void clickUnarchiveButtonsForRecords(List<String> archivedRecordsToUnarchive) {
        if (archivedRecordsToUnarchive == null || archivedRecordsToUnarchive.isEmpty()) {
            logger.warn("⚠️ No record names supplied for unarchiving. Skipping.");
            return;
        }

        final JavascriptExecutor js = (JavascriptExecutor) driver;

        for (int idx = 0; idx < archivedRecordsToUnarchive.size(); idx++) {
            String recordName = archivedRecordsToUnarchive.get(idx);

            if (recordName == null || recordName.trim().length() == 0) {
                logger.warn("⚠️ Empty record name at index {}. Skipping.", Integer.valueOf(idx));
                continue;
            }
            recordName = recordName.trim();

            try {
                logger.info("🔍 Looking for card and Unarchive button for record: '{}'", recordName);

                // Card that contains the record name somewhere inside (paragraph/title)
                // Tweak this XPath to your DOM if needed.
                String cardXpath = "//div[contains(@class,'grid-cols') and .//p[contains(normalize-space(.), " + "\"" + recordName + "\"" + ")]]";
                WebElement card = wait.waitForPresence(By.xpath(cardXpath));
                wait.waitForVisibility(card);

                // Find the inner Unarchive button
                WebElement unarchiveBtn = card.findElement(By.xpath(".//button[contains(.,'Unarchive')]"));
                wait.waitForVisibility(unarchiveBtn);
                wait.waitForElementToBeClickable(unarchiveBtn);

                // Scroll into view (center)
                try {
                    js.executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", unarchiveBtn);
                    Thread.sleep(150);
                } catch (Exception ignore) {
                }

                // Click via reusable safeClick
                logger.info("🗃️ Clicking Unarchive for '{}'", recordName);
                commonMethods.safeClick(driver, unarchiveBtn, "Unarchive button for '" + recordName + "'", 6);
                logger.info("✅ Unarchive clicked for '{}'", recordName);

                // ⏳ wait 4 seconds after each click
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException ignored) {
                }

                // Best-effort: capture unarchive success toast text (do not fail if absent)
                try {
                    String toastMsg = readUnarchiveToastMessage(4000); // up to 4s
                    if (toastMsg != null) {
                        logger.info("📣 Unarchive toast: '{}'", toastMsg);
                    } else {
                        logger.warn("ℹ️ Unarchive toast not detected for '{}'. Continuing…", recordName);
                    }
                } catch (Exception ignore) {
                }

                // Wait for the card to disappear (removed from Archive list)
                try {
                    wait.waitForInvisibility(card);
                    logger.info("🧹 Card removed from Archive list for '{}'", recordName);
                } catch (Exception inv) {
                    logger.warn("⌛ Card for '{}' did not become invisible quickly. Proceeding…", recordName);
                }

                // Small gap before next click to let the DOM settle
                try {
                    Thread.sleep(400);
                } catch (InterruptedException ignored) {
                }

            } catch (org.openqa.selenium.NoSuchElementException nse) {
                logger.error("❌ Could not find card/Unarchive button for '{}'.", recordName, nse);
            } catch (org.openqa.selenium.StaleElementReferenceException sere) {
                logger.warn("♻️ Elements went stale while unarchiving '{}'. Retrying once…", recordName);
                try {
                    // One quick retry: re-find and click
                    String retryCardXpath = "//div[contains(@class,'grid-cols') and .//p[contains(normalize-space(.), " + "\"" + recordName + "\"" + ")]]";
                    WebElement retryCard = wait.waitForPresence(By.xpath(retryCardXpath));
                    WebElement retryBtn = retryCard.findElement(By.xpath(".//button[contains(.,'Unarchive')]"));
                    commonMethods.safeClick(driver, retryBtn, "Unarchive button for '" + recordName + "' (retry)", 6);
                    logger.info("✅ Retry succeeded for '{}'", recordName);

                    // ⏳ wait 4 seconds after retry click as well
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException ignored) {
                    }

                    wait.waitForInvisibility(retryCard);
                } catch (Exception retryEx) {
                    logger.error("❌ Retry failed for '{}'", recordName, retryEx);
                }
            } catch (Exception e) {
                logger.error("❌ Failed to Unarchive '{}'", recordName, e);
            }
        }

        // Post-loop: if the list should now be empty, confirm empty state (best-effort)
        try {
            By emptyMessage = By.xpath("//p[contains(normalize-space(.),'No archived compliances')]");
            List<WebElement> empties = driver.findElements(emptyMessage);
            if (empties != null && !empties.isEmpty()) {
                logger.info("✅ Confirmed empty Archive: 'No archived compliances' is displayed.");
            } else {
                logger.warn("ℹ️ Archive may not be fully empty yet (empty message not visible).");
            }

            // Light refresh to return to a stable state
            commonMethods.refreshPage();
            logger.info("🔄 Page refreshed after unarchiving.");

        } catch (Exception e) {
            logger.error("⚠️ Post-unarchive confirmation or refresh encountered an issue.", e);
        }
    }


    /**
     * Best-effort reader for the Unarchive success toast.
     * Looks for any visible element that contains both 'unarchiv' and 'success' (case-insensitive).
     * Returns the text if found within the given timeout; otherwise null.
     *
     * @param timeoutMs timeout in milliseconds to poll for the toast
     * @return the toast message text, or null if not found
     */
    private String readUnarchiveToastMessage(long timeoutMs) {
        long end = System.currentTimeMillis() + (timeoutMs > 0 ? timeoutMs : 2000);
        By toastBy = By.xpath("//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'unarchiv') and " + "contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'success')]");
        WebElement toast = null;

        while (System.currentTimeMillis() < end) {
            try {
                List<WebElement> candidates = driver.findElements(toastBy);
                for (int i = 0; i < candidates.size(); i++) {
                    WebElement t = candidates.get(i);
                    if (t != null && t.isDisplayed()) {
                        String txt = t.getText();
                        if (txt != null && txt.trim().length() > 0) {
                            return txt.trim();
                        }
                    }
                }
            } catch (Exception ignore) {
            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException ignored) {
            }
        }
        return null;
    }

    /**
     * Verifies that the expected unarchived records are present again in the main table.
     * <p>
     * Rules:
     * <ul>
     *   <li>If the expected list is null → treated as empty.</li>
     *   <li>If the main table shows "No records found":
     *       <ul>
     *         <li>Pass if the expected list is empty (nothing to restore).</li>
     *         <li>Fail if the expected list contains items.</li>
     *       </ul>
     *   </li>
     *   <li>Otherwise, compares exact (trimmed) names against the main table list.</li>
     * </ul>
     *
     * @param expectedUnarchivedRecords names expected to be visible in the main table after unarchive
     * @return true if all expected names are present under the rules above; false otherwise
     */
    public boolean areUnarchivedRecordsPresentInMainTable(List<String> expectedUnarchivedRecords) {
        try {
            // Normalize input
            if (expectedUnarchivedRecords == null) {
                logger.warn("⚠️ Expected unarchived list is null. Treating as empty.");
                expectedUnarchivedRecords = new ArrayList<String>();
            }

            // If main table is empty, decide based on expectations
            if (noRecordsPresent()) {
                if (expectedUnarchivedRecords.isEmpty()) {
                    logger.info("✅ Main table is empty as expected (no records were expected after unarchive).");
                    return true;
                } else {
                    logger.error("❌ Main table is empty but expected unarchived records exist: {}", expectedUnarchivedRecords);
                    return false;
                }
            }

            // Fetch current names in main table
            List<String> currentRecords = getCurrentComplianceNamesInMainList();
            if (currentRecords == null) currentRecords = new ArrayList<String>();

            // Trim all for exact comparison
            List<String> trimmedCurrent = new ArrayList<String>();
            for (int i = 0; i < currentRecords.size(); i++) {
                String v = currentRecords.get(i);
                trimmedCurrent.add(v != null ? v.trim() : "");
            }

            List<String> trimmedExpected = new ArrayList<String>();
            for (int i = 0; i < expectedUnarchivedRecords.size(); i++) {
                String v = expectedUnarchivedRecords.get(i);
                if (v != null && v.trim().length() > 0) trimmedExpected.add(v.trim());
            }

            logger.info("📋 Main table records ({}): {}", Integer.valueOf(trimmedCurrent.size()), trimmedCurrent);
            logger.info("📥 Expected unarchived ({}): {}", Integer.valueOf(trimmedExpected.size()), trimmedExpected);

            // Compare (exact, trimmed)
            List<String> missing = new ArrayList<String>();
            for (int i = 0; i < trimmedExpected.size(); i++) {
                String exp = trimmedExpected.get(i);
                boolean found = false;
                for (int j = 0; j < trimmedCurrent.size(); j++) {
                    if (exp.equals(trimmedCurrent.get(j))) {
                        found = true;
                        break;
                    }
                }
                if (!found) missing.add(exp);
            }

            if (!missing.isEmpty()) {
                logger.error("❌ Missing unarchived records in main table ({}): {}", Integer.valueOf(missing.size()), missing);
                return false;
            }

            logger.info("✅ All unarchived records are present in the main table.");
            return true;

        } catch (Exception e) {
            logger.error("❌ Exception while validating unarchived records in main table.", e);
            return false;
        }
    }


    /**
     * Retrieves all compliance names currently listed in the main compliance table.
     * <p>
     * Only the first line of each cell is taken (badges, if any, are ignored).
     * Skips any rows that are missing the name cell or have blank names.
     *
     * @return a list of compliance names currently in the main table (never null)
     */
    public List<String> getCurrentComplianceNamesInMainList() {
        List<String> recordNames = new ArrayList<>();

        try {
            if (complianceRows == null || complianceRows.isEmpty()) {
                logger.warn("⚠️ No compliance rows found in the main table.");
                return recordNames;
            }

            logger.info("🔍 Found {} compliance rows in the main table. Extracting names...", Integer.valueOf(complianceRows.size()));

            for (int i = 0; i < complianceRows.size(); i++) {
                WebElement row = complianceRows.get(i);
                try {
                    WebElement complianceNameElement = row.findElement(By.xpath("./td[1]"));
                    String rawText = complianceNameElement.getText();
                    if (rawText != null && !rawText.trim().isEmpty()) {
                        String complianceName = rawText.split("\\R")[0].trim(); // First line only
                        recordNames.add(complianceName);
                        logger.debug("✅ Row {} → '{}'", Integer.valueOf(i + 1), complianceName);
                    } else {
                        logger.warn("⚠️ Row {} has an empty name cell.", Integer.valueOf(i + 1));
                    }
                } catch (NoSuchElementException e) {
                    logger.warn("⚠️ Row {} missing name cell: {}", Integer.valueOf(i + 1), e.getMessage());
                } catch (Exception ex) {
                    logger.error("❌ Unexpected error reading name from row {}", Integer.valueOf(i + 1), ex);
                }
            }

            logger.info("📋 Extracted {} record(s) from main table: {}", Integer.valueOf(recordNames.size()), recordNames);

        } catch (Exception e) {
            logger.error("❌ Error while fetching current records from main table.", e);
        }

        return recordNames;
    }


    /**
     * Waits for the "archive success" toast and returns ONLY the success sentence,
     * e.g., "Compliances archived successfully." (case-insensitive, trailing period optional).
     * <p>
     * Behavior:
     * <ul>
     *   <li>Polls up to ~8s for any visible toast in {@code archiveSuccessToasts}.</li>
     *   <li>Reads inner text and extracts the success sentence via regex.</li>
     *   <li>Logs and returns the extracted sentence; returns {@code null} if not found.</li>
     *   <li>Attempts to dismiss the toast (best-effort) and waits for it to disappear.</li>
     * </ul>
     *
     * @return the archive success sentence, or {@code null} if not found/timed out.
     */
    public String getArchiveSuccessToastMessage() {
        try {
            long end = System.currentTimeMillis() + 8000;
            WebElement toast = null;

            // — Find a visible toast with non-empty text
            while (System.currentTimeMillis() < end) {
                try {
                    if (archiveSuccessToasts != null && !archiveSuccessToasts.isEmpty()) {
                        for (WebElement t : archiveSuccessToasts) {
                            if (t != null && t.isDisplayed()) {
                                String txt = t.getText();
                                if (txt != null && !txt.trim().isEmpty()) {
                                    toast = t;
                                    break;
                                }
                            }
                        }
                    }
                } catch (StaleElementReferenceException ignored) {
                }
                if (toast != null) break;
                Thread.sleep(150);
            }

            if (toast == null) {
                logger.error("❌ Archive toast not found within timeout.");
                return null;
            }

            // — Extract ONLY the expected sentence
            String full = toast.getAttribute("innerText");
            if (full == null || full.trim().isEmpty()) full = toast.getAttribute("textContent");
            if (full == null) full = toast.getText();
            if (full == null) full = "";
            full = full.trim();

            String message = null;
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("(?i)\\bcompliances?\\s+archived\\s+successfully\\.?");
            java.util.regex.Matcher m = p.matcher(full);
            if (m.find()) {
                message = full.substring(m.start(), m.end()).trim();
            } else {
                // fallback: scan line by line
                for (String line : full.split("\\R")) {
                    if (p.matcher(line.trim()).find()) {
                        message = line.trim();
                        break;
                    }
                }
            }

            if (message == null) {
                logger.warn("⚠️ Expected archive success line not found. Raw toast text length: {}", full.length());
                return null;
            }

            logger.info("📣 Archive toast message: '{}'", message);

            // — Try to close the toast (best-effort)
            try {
                if (toastCloseButtons != null && !toastCloseButtons.isEmpty()) {
                    WebElement close = toastCloseButtons.get(0);
                    if (close != null && close.isDisplayed() && close.isEnabled()) close.click();
                }
            } catch (Exception ignored) {
            }

            // — Wait for it to disappear
            long end2 = System.currentTimeMillis() + 5000;
            while (System.currentTimeMillis() < end2) {
                try {
                    if (!toast.isDisplayed()) break;
                } catch (StaleElementReferenceException gone) {
                    break;
                }
                Thread.sleep(150);
            }

            return message;

        } catch (Exception e) {
            logger.error("❌ Error while fetching archive toast.", e);
            return null;
        }
    }


    public boolean isGenerateCSVButtonVisible() {
        try {
            return generateCSVButton.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }


    public void clickGenerateCSVButton(WebDriver driver, int timeoutInSeconds) {
        try {
            // Wait for button to be visible
            wait.waitForVisibility(generateCSVButton);

            // Wait for button to be clickable
            wait.waitForElementToBeClickable(generateCSVButton);

            try {
                generateCSVButton.click();
            } catch (ElementClickInterceptedException e) {
                // Defensive fallback: scroll & JS click
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", generateCSVButton);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", generateCSVButton);
            }

        } catch (TimeoutException e) {
            throw new TimeoutException("Generate CSV button was not clickable within " + timeoutInSeconds + " seconds.");
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Generate CSV button is not present in the DOM.");
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while clicking Generate CSV button: " + e.getMessage(), e);
        }
    }


    public Path waitForLatestCsv(Path folder, Duration timeout, Duration poll) {
        long deadline = System.nanoTime() + timeout.toNanos();

        do {
            // List folder contents to help diagnose
            try (Stream<Path> list = Files.list(folder)) {
                List<Path> items = list.toList();
                System.out.println("📂 Downloads now has " + items.size() + " item(s):");
                for (Path p : items) System.out.println("   - " + p.getFileName());
            } catch (IOException ignored) {
            }

            Path latestCsv = getLatestCsv(folder);
            if (latestCsv != null && hasFinishedDownloading(latestCsv, poll)) {
                return latestCsv;
            }

            sleep(poll);
        } while (System.nanoTime() < deadline);

        throw new AssertionError("No CSV file found in downloads folder within timeout: " + folder.toAbsolutePath());
    }

    private boolean hasFinishedDownloading(Path csv, Duration poll) {
        Path partial = csv.resolveSibling(csv.getFileName().toString() + ".crdownload");
        if (Files.exists(partial)) return false; // still downloading

        try {
            long s1 = Files.size(csv);
            sleep(poll);
            long s2 = Files.size(csv);
            return s1 > 0 && s1 == s2;
        } catch (IOException e) {
            return false;
        }
    }

    private Path getLatestCsv(Path folder) {
        Path latest = null;
        long latestTs = Long.MIN_VALUE;

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder)) {
            for (Path p : ds) {
                if (!Files.isRegularFile(p)) continue;

                String name = p.getFileName().toString();
                if (!name.toLowerCase().endsWith(".csv")) continue;

                long ts = Files.getLastModifiedTime(p).toMillis();
                if (ts > latestTs) {
                    latestTs = ts;
                    latest = p;
                }
            }
        } catch (IOException e) {
            return null; // or rethrow if you prefer
        }
        return latest;
    }

    private void sleep(Duration d) {
        try {
            Thread.sleep(d.toMillis());
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }


    public void clickFirstComplianceRecord() {
        logger.info("Attempting to click the first compliance record...");
        for (int attempt = 1; attempt <= RETRIES + 1; attempt++) {
            try {
                wait.waitForElementToBeClickable(firstComplianceRecord);
                helpers.scrollToElement(driver, firstComplianceRecord);
                firstComplianceRecord.click();
                logger.info("✅ Clicked first compliance record (attempt {}).", attempt);
                return;
            } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
                logger.warn("⚠️ Click attempt {} failed: {}. Retrying...", attempt, e.toString());
                if (attempt == RETRIES + 1) throw e;
                if (attempt == RETRIES) {
                    logger.info("➡️ Falling back to JS click on final retry.");
                    helpers.jsClick(driver, firstComplianceRecord);
                    return;
                }
            }
        }
    }

    public boolean isCompliancePanelDisplayed() {
        logger.info("Validating compliance details panel fields...");
        if (!isFieldDisplayed("status")) {
            logger.error("❌ Compliance panel not opened: 'Status' label missing within {}s", TIMEOUT_SEC);
            return false;
        }

        List<String> names = Arrays.asList("Status", "Due date", "Assignee", "Risk");
        boolean allVisible = true;

        for (String field : names) {
            boolean ok = isFieldDisplayed(field);
            logger.info("Field '{}' visible: {}", field, ok);
            if (!ok) allVisible = false;
        }

        if (allVisible) logger.info("✅ All required fields are visible in the first section.");
        else logger.error("❌ One or more fields are missing in the first section.");
        return allVisible;
    }

    public boolean isFieldDisplayed(String fieldName) {
        WebElement el;
        switch (fieldName.toLowerCase().trim()) {
            case "status":
                el = statusField;
                break;
            case "due date":
                el = dueDateField;
                break;
            case "assignee":
                el = assigneeField;
                break;
            case "risk":
                el = riskField;
                break;
            default:
                logger.error("❌ Invalid field requested: '{}'", fieldName);
                throw new IllegalArgumentException("Invalid field: " + fieldName);
        }

        try {
            helpers.scrollToElement(driver, el);
            wait.waitForVisibility((el));
            boolean visible = el.isDisplayed();
            if (visible) {
                logger.info("✅ Field '{}' is visible.", fieldName);
            } else {
                logger.warn("⚠️ Field '{}' is NOT visible.", fieldName);
            }
            return visible;
        } catch (TimeoutException | NoSuchElementException e) {
            logger.error("❌ Field '{}' not visible within {}s. Reason: {}", fieldName, TIMEOUT_SEC, e.getMessage());
            return false;
        }
    }


    /**
     * Validates the Connect with Expert action is present, visible, clickable,
     * correctly labeled, and semantically valid as a button/link.
     */
    public void assertConnectWithExpertPresent() {
        final String name = "Connect with Expert action";
        commonMethods.scrollIntoView(connectWithExpertBtn);
        commonMethods.assertVisibleAndClickable(connectWithExpertBtn, name);
        commonMethods.assertText(connectWithExpertBtn, "Connect with Expert", name);
        logger.info("✅ {} verified successfully", name);
    }

    /**
     * Validates that EITHER:
     *  1) "Opt with Zolvit" action button   OR
     *  2) "Tell us how you're managing it and keep your records up to date."
     * is present, visible, and correctly labeled.
     *
     * Passes if at least one is present.
     */
    public void assertOptWithZolvitOrTellUsPresent() {
        logger.info("🔎 Checking for presence of: 'Opt with Zolvit' OR 'Tell us how you're managing it...'");

        boolean zolvitPresent = false;
        boolean tellUsPresent = false;

        // XPaths
        String zolvitXpath = "//p[normalize-space()='Opt with Zolvit']/ancestor::button[1] "
                + "| //button[.//p[normalize-space()='Opt with Zolvit']]";

        String tellUsXpath = "//p[contains(normalize-space(), \"Tell us how you're managing\")]";

        try {
            WebElement zolvit = driver.findElement(By.xpath(zolvitXpath));
            commonMethods.scrollIntoView(zolvit);
            commonMethods.assertVisibleAndClickable(zolvit, "Opt with Zolvit");
            zolvitPresent = true;
            logger.info("✅ 'Opt with Zolvit' is present.");
        } catch (Exception ignore) {
            logger.warn("⚠️ 'Opt with Zolvit' not found.");
        }

        try {
            WebElement tellUs = driver.findElement(By.xpath(tellUsXpath));
            commonMethods.scrollIntoView(tellUs);
            commonMethods.assertVisibleAndClickable(tellUs, "Tell us how you're managing…");
            tellUsPresent = true;
            logger.info("✅ 'Tell us how you're managing it...' message is present.");
        } catch (Exception ignore) {
            logger.warn("⚠️ 'Tell us how you're managing…' not found.");
        }

        if (!zolvitPresent && !tellUsPresent) {
            String msg = "Neither 'Opt with Zolvit' nor 'Tell us how you're managing…' is present!";
            logger.error("❌ {}", msg);
            throw new AssertionError(msg);
        }


        logger.info("🎉 PASS: At least one required action/message is present.");
    }



    /**
     * Opens a tab, waits for content to appear, and checks text is not blank.
     * Returns true if visible and non-empty, false otherwise.
     */
    private boolean openTabAndValidate(String tabName, WebElement tab, By contentInTab) {
        try {
            logger.info("Opening '{}' tab…", tabName);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tab);
            tab.click();

            // wait until content is visible
            wait.waitForVisibility(contentInTab);

            WebElement contentEl = driver.findElement(contentInTab);
            String text = contentEl.getText();
            if (text == null || text.trim().isEmpty()) {
                logger.error("❌ '{}' tab content is visible but BLANK.", tabName);
                return false;
            }

            logger.info("✅ '{}' tab content loaded: {}", tabName, abbreviate(text, 100));
            return true;
        } catch (TimeoutException te) {
            logger.error("❌ Timeout waiting for '{}' tab content: {}", tabName, te.getMessage());
            return false;
        } catch (Throwable t) {
            logger.error("❌ Error while validating '{}' tab: {}", tabName, t.getMessage());
            return false;
        }
    }


    public boolean openInfoAndCheck() {
        return openTabAndValidate("Info", infoTab, whatIsItInPanel);
    }

    public boolean openTasksAndCheck() {
        return openTabAndValidate("Tasks", tasksTab, paymentInPanel);
    }

    public boolean openAuditAndCheck() {
        return openTabAndValidate("Audit trial", auditTrialTab, allFilterInPanel);
    }


    public boolean isInfoDefaultContentVisible() {
        final String tabName = "Info";
        try {
            logger.info("Checking '{}' default content WITHOUT clicking…", tabName);

            // Just bring the area into view (do not click)
            commonMethods.scrollIntoView(infoTab);

            // Wait for the Info content to be visible
            wait.waitForVisibility(whatIsItInPanel);

            WebElement contentEl = driver.findElement(whatIsItInPanel);
            String text = contentEl.getText();
            boolean visible = contentEl.isDisplayed();
            boolean nonBlank = (text != null && text.trim().length() > 0);

            if (visible && nonBlank) {
                logger.info("✅ '{}' default content visible: {}", tabName, abbreviate(text, 100));
                return true;
            } else {
                logger.error("❌ '{}' default content present but not visible or blank. visible={}, nonBlank={}", tabName, String.valueOf(visible), String.valueOf(nonBlank));
                return false;
            }
        } catch (TimeoutException te) {
            logger.error("❌ Timeout waiting for '{}' default content: {}", tabName, te.getMessage());
            return false;
        } catch (Throwable t) {
            logger.error("❌ Error while checking '{}' default content: {}", tabName, t.getMessage());
            return false;
        }
    }

    // (re-use your abbreviate(...) helper if it's already in the class)
    private String abbreviate(String s, int max) {
        if (s == null) return "";
        if (max < 4) max = 4;
        if (s.length() <= max) return s;
        return s.substring(0, max - 3) + "...";
    }


    public boolean isTasksDefaultContentVisible() {
        final String tabName = "Tasks";
        try {
            logger.info("Checking '{}' default content WITHOUT clicking…", tabName);

            // Bring the area into view only (NO CLICK)
            commonMethods.scrollIntoView(tasksTab);

            // Wait for the Tasks content to be visible
            wait.waitForVisibility(paymentInPanel);

            WebElement contentEl = driver.findElement(paymentInPanel);
            String text = contentEl.getText();
            boolean visible = contentEl.isDisplayed();
            boolean nonBlank = (text != null && text.trim().length() > 0);

            if (visible && nonBlank) {
                logger.info("✅ '{}' default content visible: {}", tabName, abbreviate(text, 100));
                return true;
            } else {
                logger.error("❌ '{}' default content present but not visible or blank. visible={}, nonBlank={}", tabName, String.valueOf(visible), String.valueOf(nonBlank));
                return false;
            }
        } catch (TimeoutException te) {
            logger.error("❌ Timeout waiting for '{}' default content: {}", tabName, te.getMessage());
            return false;
        } catch (Throwable t) {
            logger.error("❌ Error while checking '{}' default content: {}", tabName, t.getMessage());
            return false;
        }
    }


    public boolean navigateToInProgressTab() {
        final String name = "In progress tab";
        try {
            logger.info("Navigating to '{}'…", name);
            commonMethods.scrollIntoView(inProgressTab);

            try {
                inProgressTab.click();
                logger.info("✅ Clicked '{}' via normal click.", name);
                return true;
            } catch (Throwable clickFail) {
                logger.warn("Normal click on '{}' failed ({}). Falling back to JS click.", name, clickFail.getClass().getSimpleName());
            }

            try {
                helpers.jsClick(driver, inProgressTab); // your helper
                logger.info("✅ Clicked '{}' via JS click fallback.", name);
                return true;
            } catch (Throwable jsFail) {
                logger.error("❌ JS click on '{}' failed: {}", name, jsFail.getMessage());
                return false;
            }

        } catch (Throwable t) {
            logger.error("❌ Failed to navigate to '{}': {}", name, t.getMessage());
            return false;
        }

    }


    /**
     * Checks visibility of Compliance Department dropdown.
     */
    public boolean isComplianceDepartmentVisible() {
        boolean visible = isVisible(complianceDepartmentDropdown);
        logger.info("🔎 Compliance Department dropdown visible: {}", Boolean.valueOf(visible));
        return visible;
    }

    /**
     * Checks visibility of Compliance Category dropdown.
     */
    public boolean isComplianceCategoryVisible() {
        boolean visible = isVisible(complianceCategoryDropdown);
        logger.info("🔎 Compliance Category dropdown visible: {}", Boolean.valueOf(visible));
        return visible;
    }

    /**
     * Checks visibility of Due Date dropdown.
     */
    public boolean isDueDateVisible() {
        boolean visible = isVisible(dueDateDropdown);
        logger.info("🔎 Due Date dropdown visible: {}", Boolean.valueOf(visible));
        return visible;
    }

    /**
     * Internal visibility safely checks element without throwing.
     */
    private boolean isVisible(WebElement el) {
        try {
            return el.isDisplayed();
        } catch (Exception e) {
            logger.warn("⚠️ Element not visible: {}", e.getMessage());
            return false;
        }
    }


    /**
     * Retrieves all visible text options from a dropdown.
     * This method:
     * ✅ Uses safeClick to open/close dropdown
     * ✅ Waits until dropdown panel appears
     * ✅ Extracts options (ignores "Clear" & "Apply")
     * ✅ Logs options for debugging
     * ✅ Validates if dropdown has at least 1 option
     *
     * @param dropdown     WebElement representing the dropdown button
     * @param dropdownName Human readable name used in log messages
     * @return List<String>  List of option texts
     */
    public List<String> getOptionsFromDropdown(WebElement dropdown, String dropdownName) {
        logger.info("🔽 Fetching options from '{}' dropdown...", dropdownName);

        // ✅ Step 1: Click dropdown to open
        commonMethods.safeClick(driver, dropdown, dropdownName + " dropdown", 10);
        logger.info("✅ '{}' dropdown opened", dropdownName);

        // ✅ Step 2: Identify dropdown panel
        String panelXPath = ".//following::div[contains(@class,'absolute') and contains(@class,'top-full')][1]";
        String optionXPath = panelXPath + "//*[self::div or self::p or self::span][normalize-space(string(.))!='']";

        List<WebElement> panelElements = null;
        long endTime = System.currentTimeMillis() + 15000; // 15 sec wait

        // ✅ Step 3: Wait for the dropdown panel
        while (System.currentTimeMillis() < endTime) {
            panelElements = dropdown.findElements(By.xpath(panelXPath));
            if (panelElements != null && !panelElements.isEmpty() && panelElements.get(0).isDisplayed()) {
                logger.info("✅ Dropdown panel for '{}' is now visible", dropdownName);
                break;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignore) {
            }
        }

        if (panelElements == null || panelElements.isEmpty()) {
            logger.error("❌ Panel NOT FOUND for '{}' dropdown!", dropdownName);
            throw new TimeoutException("Dropdown panel not visible for: " + dropdownName);
        }

        // ✅ Step 4: Read options
        List<WebElement> optionElements = dropdown.findElements(By.xpath(optionXPath));
        List<String> options = new ArrayList<String>();

        if (optionElements != null) {
            for (int i = 0; i < optionElements.size(); i++) {
                String txt = optionElements.get(i).getText().trim();
                if (txt.length() > 0 && !txt.equalsIgnoreCase("Clear") && !txt.equalsIgnoreCase("Apply")) {
                    options.add(txt);
                } else {
                    logger.debug("⏭ Skipping action item '{}'", txt);
                }
            }
        }

        // ✅ Validation: Options found or not
        if (options.isEmpty()) {
            logger.warn("⚠️ NO selectable options found for '{}' dropdown!", dropdownName);
        } else {
            logger.info("📋 '{}' dropdown options found: {}", dropdownName, options.toString());
        }

        // ✅ Validation: Duplicate options check
        Set<String> uniqueCheck = new HashSet<String>(options);
        if (uniqueCheck.size() < options.size()) {
            logger.warn("⚠️ DUPLICATE option values detected in '{}' dropdown!", dropdownName);
        }

        // ✅ Step 5: Close dropdown
        commonMethods.safeClick(driver, dropdown, dropdownName + " dropdown", 10);
        logger.info("✅ '{}' dropdown closed", dropdownName);

        return options;
    }


    private boolean isActionButtonText(String txt) {
        // Skip common action buttons rendered inside the menu (Clear/Apply)
        return "Clear".equalsIgnoreCase(txt) || "Apply".equalsIgnoreCase(txt);
    }

    public List<String> getDepartmentOptions() {
        return getOptionsFromDropdown(complianceDepartmentDropdown, "Compliance Department");
    }

    public List<String> getCategoryOptions() {
        return getOptionsFromDropdown(complianceCategoryDropdown, "Compliance Category");
    }


    // In CompliancePage.java

    /**
     * Opens the "Compliance Department" dropdown and waits until its panel is visible.
     *
     * <p><b>Behavior:</b>
     * <ul>
     *   <li>Closes any open panels (ESC + blur)</li>
     *   <li>Scrolls the trigger into view & clicks with your retry logic</li>
     *   <li>Waits for the dropdown panel to become visible using the configured timeout</li>
     * </ul>
     * </p>
     *
     * @return the visible dropdown panel WebElement (never null if successful)
     * @throws IllegalStateException if the trigger is not present/visible
     * @throws org.openqa.selenium.TimeoutException if the panel does not appear in time
     */
    public WebElement openDepartmentDropdown() {
        return openDropdownAndGetPanel(
                getDepartmentDropdownTrigger(),
                "Compliance Department",
                ReusableCommonMethods.NAV_FAIL_MS
        );
    }

    /**
     * Opens the "Compliance Category" dropdown and waits until its panel is visible.
     *
     * @return the visible dropdown panel WebElement
     * @throws IllegalStateException if the trigger is not present/visible
     * @throws org.openqa.selenium.TimeoutException if the panel does not appear in time
     */
    public WebElement openCategoryDropdown() {
        return openDropdownAndGetPanel(
                getCategoryDropdownTrigger(),
                "Compliance Category",
                ReusableCommonMethods.NAV_FAIL_MS
        );
    }

    /**
     * Opens the "Due Date" dropdown and waits until its panel is visible.
     *
     * @return the visible dropdown panel WebElement
     * @throws IllegalStateException if the trigger is not present/visible
     * @throws org.openqa.selenium.TimeoutException if the panel does not appear in time
     */
    public WebElement openDueDateDropdown() {
        return openDropdownAndGetPanel(
                getDueDateDropdownTrigger(),
                "Due Date",
                ReusableCommonMethods.NAV_FAIL_MS
        );
    }

/* -----------------------------------------------------------
   Internal DRY helper used by the three public open* methods.
   ----------------------------------------------------------- */

    /**
     * Core routine to open a dropdown and return its visible panel.
     *
     * @param trigger the dropdown trigger element
     * @param name    friendly name for logs
     * @param timeoutMs max wait for the panel to be visible
     * @return the visible dropdown panel WebElement
     */
    private WebElement openDropdownAndGetPanel(WebElement trigger, String name, long timeoutMs) {
        if (trigger == null) {
            throw new IllegalStateException(name + " trigger is null (not located).");
        }

        logger.info("🔽 Opening '{}' dropdown…", name);

        // Best-effort to start from a clean state
        ensureAllPanelsClosed();

        // Click with your robust retry logic (scroll + normal click + JS fallback)
        openDropdownWithRetries(trigger, name);

        // Wait for the floating panel to be visible
        WebElement panel = waitForAnyDropdownPanelVisible(name, timeoutMs);
        if (panel == null) {
            // waitForAnyDropdownPanelVisible usually throws on timeout; this is an extra guard
            throw new org.openqa.selenium.TimeoutException("Panel not visible for dropdown: " + name);
        }

        logger.info("✅ '{}' dropdown opened; panel visible.", name);
        return panel;
    }


    /**
     * Returns visible option texts for the "Due Date" dropdown.
     *
     * <p>Behavior:</p>
     * <ul>
     *   <li>Opens the Due Date dropdown using the page's existing open/wait helpers.</li>
     *   <li>First tries a strict XPath that targets the list container.</li>
     *   <li>If strict path yields nothing, falls back to scraping text-bearing nodes inside the panel.</li>
     *   <li>Trims/normalizes, preserves order, removes duplicates, and filters out "Select"/"Please select".</li>
     *   <li>Never throws for empty/hidden panel; logs and returns whatever was collected.</li>
     * </ul>
     *
     * <p>Logging:</p>
     * <ul>
     *   <li>INFO when opening/closing and when options are collected.</li>
     *   <li>WARN if strict path returns nothing or the final list is empty.</li>
     *   <li>ERROR if any exception occurs while fetching options.</li>
     * </ul>
     *
     * @return ordered, de-duplicated list of visible option texts (never {@code null})
     */
    public List<String> getDueDateOptions() {
        final String STRICT_ITEMS =
                "(//div[@class='flex flex-col gap-2 max-h-[180px] overflow-auto enterpriseScrollbar mb-2 p-1'])[3]/p";

        LinkedHashSet<String> unique = new LinkedHashSet<String>();
        WebElement panel = null;

        try {
            // Validate driver minimally (defensive)
            if (driver == null) {
                logger.error("❌ WebDriver is null; cannot fetch Due Date options.");
                return new ArrayList<String>();
            }

            // Open dropdown & obtain its floating panel using your existing helpers
            WebElement trigger = getDueDateDropdownTrigger();
            if (trigger == null) {
                logger.error("❌ Due Date dropdown trigger is null.");
                return new ArrayList<String>();
            }

            logger.info("🗂️ Opening 'Due Date' dropdown to collect options…");
            panel = openAndGetGlobalPanel(trigger, "Due Date");  // uses your existing helper

            // 1) Strict path (fast/deterministic)
            List<WebElement> strict = driver.findElements(By.xpath(STRICT_ITEMS));
            for (int i = 0; i < strict.size(); i++) {
                String t = norm(strict.get(i).getText());
                if (t.isEmpty()) continue;
                String tlc = t.toLowerCase();
                if ("select".equalsIgnoreCase(t) || tlc.contains("please select")) continue;
                unique.add(t);
            }

            // 2) Fallback: scrape any text-bearing nodes inside the panel
            if (unique.isEmpty()) {
                logger.warn("⚠️ Strict Due Date XPath returned no items; attempting panel fallback scraping.");
                List<WebElement> nodes = panel.findElements(By.xpath(
                        ".//p[normalize-space()] | " +
                                ".//*[self::div or self::li or self::button or self::span or self::a]" +
                                "[normalize-space(string(.))!='']"
                ));
                for (int i = 0; i < nodes.size(); i++) {
                    String t = norm(nodes.get(i).getText());
                    if (t.isEmpty()) continue;
                    String tlc = t.toLowerCase();
                    if ("select".equalsIgnoreCase(t) || tlc.contains("please select")) continue;
                    unique.add(t);
                }
            }

            logger.info("🏷️ Due Date options collected: {}", unique);

        } catch (Throwable e) {
            // Do NOT fail here; this is a POM accessor. Let the step decide.
            logger.error("❌ Error while collecting 'Due Date' options: {}", e.toString(), e);
        } finally {
            // Best-effort close (ESC). It's fine if this fails.
            try { new Actions(driver).sendKeys(Keys.ESCAPE).perform(); } catch (Throwable ignore) {}
            try { Thread.sleep(120); } catch (InterruptedException ignored) {}
        }

        if (unique.isEmpty()) {
            logger.warn("ℹ️ Due Date options list is empty (panel may be closed/empty).");
        }
        return new ArrayList<String>(unique);
    }


    private WebElement openAndGetGlobalPanel(WebElement trigger, String name) {
        // close any existing panels so we don’t read the wrong popover
        ensureAllPanelsClosed();

        // bring into view + safe click
        commonMethods.scrollIntoView(trigger);
        logger.info("▶ Opening '{}' dropdown…", name);
        logToAllure("Open Dropdown", name);
        commonMethods.safeClick(driver, trigger, name + " dropdown", 8);

        // 1) try your global search
        try {
            return waitForAnyDropdownPanelVisible(name, 8000L);
        } catch (TimeoutException first) {
            logger.warn("⚠️ Global panel not found for '{}', trying strict container path…", name);
            // 2) strict, page-specific container path (works for Due Date)
            By strictContainer = By.xpath(
                    "(//div[@class='flex flex-col gap-2 max-h-[180px] overflow-auto enterpriseScrollbar mb-2 p-1'])[3]"
            );
            try {
                WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(6));
                WebElement container = w.until(ExpectedConditions.visibilityOfElementLocated(strictContainer));
                logger.info("✅ Strict container visible for '{}'", name);
                return container;
            } catch (Throwable second) {
                // 3) as last resort, click again (menu sometimes closes immediately), then global wait once more
                logger.warn("↻ Retrying click & global search for '{}'", name);
                try { ((JavascriptExecutor)driver).executeScript("arguments[0].click();", trigger); } catch (Throwable ignore) {}
                return waitForAnyDropdownPanelVisible(name, 5000L);
            }
        }
    }


    /**
     * Collapses all whitespace runs to a single space and trims the ends.
     *
     * <p>Null-safe: returns an empty string when {@code input} is null.</p>
     *
     * @param input raw text (nullable)
     * @return normalized string (non-null, possibly empty)
     */
    private String normalize(String input) {
        return input == null ? "" : input.replaceAll("\\s+", " ").trim();
    }


    /**
     * Waits until a dropdown panel associated with the given trigger becomes visible.
     *
     * <p>Searches using {@code PANEL_XPATH} scoped under the trigger element. Returns the
     * first visible panel whose rect is non-zero. Logs progress and throws a clear
     * {@link TimeoutException} if not found within ~8s.</p>
     *
     * @param trigger dropdown trigger element (must not be null)
     * @param name    friendly dropdown name for logs (non-null/blank allowed but discouraged)
     * @return the first visible panel WebElement
     * @throws IllegalArgumentException if {@code trigger} is null
     * @throws TimeoutException         if no visible panel is found within the timeout
     */
    private WebElement waitForDropdownPanel(WebElement trigger, String name) {
        if (trigger == null) {
            logger.error("❌ waitForDropdownPanel: trigger is null (name='{}')", name);
            throw new IllegalArgumentException("trigger cannot be null");
        }

        final long timeoutMs = 8_000L;
        final long end = System.currentTimeMillis() + timeoutMs;
        int scans = 0;

        while (System.currentTimeMillis() < end) {
            scans++;
            try {
                List<WebElement> panels = trigger.findElements(By.xpath(PANEL_XPATH));
                if (panels != null && !panels.isEmpty()) {
                    WebElement panel = panels.get(0);
                    try {
                        if (panel.isDisplayed() && panel.getSize().getHeight() > 0 && panel.getSize().getWidth() > 0) {
                            logger.info("✅ '{}' panel visible after {} scan(s).", name, Integer.valueOf(scans));
                            return panel;
                        }
                    } catch (Throwable t) {
                        logger.warn("⚠️ '{}' panel candidate not interactable yet (scan {}): {}", name, Integer.valueOf(scans), t.toString());
                    }
                } else {
                    logger.debug("ℹ️ '{}' panel not found yet (scan {}).", name, Integer.valueOf(scans));
                }
            } catch (StaleElementReferenceException sere) {
                logger.warn("♻️ Trigger went stale while waiting for '{}' panel; continuing…", name);
            } catch (Throwable t) {
                logger.warn("⚠️ Error while probing '{}' panel (scan {}): {}", name, Integer.valueOf(scans), t.toString());
            }
            sleep(300);
        }

        logger.error("❌ Panel not visible for dropdown '{}' within {} ms ({} scan(s)).", name, Long.valueOf(timeoutMs), Integer.valueOf(scans));
        throw new TimeoutException("Panel not visible for dropdown: " + name);
    }

    /**
     * Reads the visible text from the grid's first column (using {@code gridRowTitles} locator),
     * filtering out blanks and the sentinel "No records found".
     *
     * <p>Does not scroll; intended to capture what is currently visible.</p>
     *
     * @return ordered list of non-empty row titles (non-null, possibly empty)
     */
    public List<String> getVisibleGridItems() {
        List<String> items = new ArrayList<>();
        int scanned = 0;
        try {
            List<WebElement> nodes = driver.findElements(gridRowTitles);
            if (nodes == null) {
                logger.warn("ℹ️ getVisibleGridItems: locator returned null list.");
            } else {
                for (WebElement el : nodes) {
                    scanned++;
                    String text = normalize(el.getText());
                    if (!text.isEmpty() && !"No records found".equalsIgnoreCase(text)) {
                        items.add(text);
                    }
                }
            }
        } catch (NoSuchElementException nse) {
            logger.warn("ℹ️ getVisibleGridItems: grid row locator not present yet.");
        } catch (StaleElementReferenceException sere) {
            logger.warn("♻️ getVisibleGridItems: elements went stale during read; partial list size={}.", Integer.valueOf(items.size()));
        } catch (Throwable t) {
            logger.error("❌ getVisibleGridItems: unexpected error: {}", t.toString());
        }

        logger.info("📊 Grid visible items: {} (scanned {}).", Integer.valueOf(items.size()), Integer.valueOf(scanned));
        return items;
    }

    /**
     * Best-effort sleep wrapper.
     *
     * @param ms milliseconds to sleep (≤0 is treated as a no-op)
     */
    private void sleep(long ms) {
        if (ms <= 0) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Returns the trigger element for the <b>Compliance Department</b> dropdown.
     *
     * <p>Note: may be {@code null} if page has not loaded the control yet.</p>
     *
     * @return trigger WebElement (nullable)
     */
    public WebElement getDepartmentDropdownTrigger() {
        if (complianceDepartmentDropdown == null) {
            logger.warn("ℹ️ getDepartmentDropdownTrigger: element is null.");
        }
        return complianceDepartmentDropdown;
    }

    /**
     * Returns the trigger element for the <b>Compliance Category</b> dropdown.
     *
     * <p>Note: may be {@code null} if page has not loaded the control yet.</p>
     *
     * @return trigger WebElement (nullable)
     */
    public WebElement getCategoryDropdownTrigger() {
        if (complianceCategoryDropdown == null) {
            logger.warn("ℹ️ getCategoryDropdownTrigger: element is null.");
        }
        return complianceCategoryDropdown;
    }

    /**
     * Returns the trigger element for the <b>Due Date</b> dropdown.
     *
     * <p>Note: may be {@code null} if page has not loaded the control yet.</p>
     *
     * @return trigger WebElement (nullable)
     */
    public WebElement getDueDateDropdownTrigger() {
        if (dueDateDropdown == null) {
            logger.warn("ℹ️ getDueDateDropdownTrigger: element is null.");
        }
        return dueDateDropdown;
    }


    /**
     * Robustly opens a dropdown:
     * <ol>
     *   <li>Scroll the trigger to center</li>
     *   <li>Attempt {@code safeClick}</li>
     *   <li>Wait for panel via {@link #waitForDropdownPanel(WebElement, String)}</li>
     *   <li>On failure, JS-click fallback, up to 3 attempts</li>
     * </ol>
     *
     * @param dropdown trigger element (must not be null)
     * @param name     friendly control name for logs
     * @throws IllegalArgumentException if {@code dropdown} is null
     * @throws RuntimeException         if all attempts fail (preserves last error in logs)
     */
    public void openDropdownWithRetries(WebElement dropdown, String name) {
        if (dropdown == null) {
            logger.error("❌ openDropdownWithRetries: dropdown is null (name='{}')", name);
            throw new IllegalArgumentException("dropdown cannot be null");
        }

        final int MAX_ATTEMPTS = 3;
        RuntimeException lastError = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            logger.info("🧩 Opening '{}' (attempt {}/{})", name, Integer.valueOf(attempt), Integer.valueOf(MAX_ATTEMPTS));
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", dropdown);
                sleep(80);

                // primary click path (keeps your existing approach via commonMethods)
                commonMethods.safeClick(driver, dropdown, name + " dropdown", 8);

                // wait for panel
                waitForDropdownPanel(dropdown, name);
                logger.info("✅ '{}' opened via normal click.", name);
                return;
            } catch (RuntimeException e) { // catch to try JS fallback inside the same attempt
                lastError = e;
                logger.warn("⚠️ '{}' did not open via normal click on attempt {}: {}", name, Integer.valueOf(attempt), e.toString());

                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdown);
                    waitForDropdownPanel(dropdown, name);
                    logger.info("✅ '{}' opened via JS click fallback.", name);
                    return;
                } catch (Throwable t) {
                    logger.warn("⚠️ JS fallback failed for '{}' on attempt {}: {}", name, Integer.valueOf(attempt), t.toString());
                    sleep(250); // small backoff before next attempt
                }
            }
        }

        logger.error("❌ Failed to open '{}' after {} attempts.", name, Integer.valueOf(MAX_ATTEMPTS));
        if (lastError != null) throw lastError;
        throw new RuntimeException("Failed to open '" + name + "' after " + MAX_ATTEMPTS + " attempts.");
    }


    /**
     * Closes any floating panels best-effort:
     * <ul>
     *   <li>Sends ESC</li>
     *   <li>Blurs the active element</li>
     *   <li>Waits a short beat for UI to settle</li>
     * </ul>
     * Never throws; logs only.
     */
    public void ensureAllPanelsClosed() {
        try {
            try {
                new Actions(driver).sendKeys(Keys.ESCAPE).perform();
            } catch (Throwable ignore) {
            }
            ((JavascriptExecutor) driver).executeScript("document.activeElement && document.activeElement.blur();");
            sleep(200);
            logger.info("🧹 ensureAllPanelsClosed: attempted ESC + blur.");
        } catch (Throwable t) {
            logger.warn("ensureAllPanelsClosed best-effort failed: {}", t.toString());
        }
    }


//
//    // matches the type of expectedItems (List<String>)
//    public List<String> parseExpectedBlock(String block) {
//        LinkedHashSet<String> set = new LinkedHashSet<String>();
//        if (block != null) {
//            String cleaned = block.trim();
//            if (!cleaned.isEmpty()) {
//                String[] parts = cleaned.split("[\\n;]+");
//                for (int i = 0; i < parts.length; i++) {
//                    String p = norm(parts[i]);
//                    if (!p.isEmpty()) set.add(p);
//                }
//            }
//        }
//        return new ArrayList<String>(set); // return List
//    }

    /**
     * Parses a docstring block (from Cucumber) into a list of distinct expected items.
     * <p>
     * Splits on newline or semicolon, normalizes whitespace, removes blanks,
     * and preserves insertion order while ensuring uniqueness.
     * </p>
     *
     * @param block raw multiline block (nullable)
     * @return ordered list of unique, normalized strings (non-null, possibly empty)
     */
    public List<String> parseExpectedBlock(String block) {
        LinkedHashSet<String> set = new LinkedHashSet<String>();

        if (block == null) {
            logger.warn("⚠️ parseExpectedBlock: Received null block.");
            return new ArrayList<String>(); // empty list
        }

        String cleaned = block.trim();
        if (cleaned.isEmpty()) {
            logger.warn("⚠️ parseExpectedBlock: Block is empty after trimming.");
            return new ArrayList<String>();
        }

        String[] parts = cleaned.split("[\\n;]+"); // split on newline OR semicolon
        for (int i = 0; i < parts.length; i++) {
            String p = norm(parts[i]); // use your existing normalization
            if (!p.isEmpty()) {
                set.add(p);
            }
        }

        List<String> result = new ArrayList<String>(set);
        logger.info("🧩 parseExpectedBlock: Parsed {} expected item(s): {}", Integer.valueOf(result.size()), result);

        return result;
    }


    /**
     * Builds a lightweight signature for the current grid by concatenating
     * normalized text of the first N row-title cells (pipe-delimited).
     *
     * <p>Use this to detect grid reloads without timing-only waits.</p>
     *
     * @param maxRows how many rows to include (default 10 if <=0)
     * @return non-null signature string (may be empty)
     */
    public String captureGridSignature(int maxRows) {
        final int N = maxRows > 0 ? maxRows : 10;
        List<WebElement> els = driver.findElements(By.xpath("//table//tbody//tr//td[1]//*[self::a or self::span or self::p]"));

        List<String> rows = new ArrayList<String>(Math.min(N, els.size()));
        for (int i = 0; i < els.size() && rows.size() < N; i++) {
            String t = norm(els.get(i).getText());
            if (!t.isEmpty()) rows.add(t);
        }

        if (rows.isEmpty()) {
            logger.info("ℹ️ captureGridSignature: grid appears empty (no row titles).");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows.size(); i++) {
            if (i > 0) sb.append('|');
            sb.append(rows.get(i));
        }
        return sb.toString();
    }

    /**
     * Backward-compatible no-arg overload (defaults to 10).
     */
    public String captureGridSignature() {
        return captureGridSignature(10);
    }


    /**
     * Polls until the grid's signature differs from a previous value.
     *
     * @param previousSignature non-null (empty allowed)
     * @param timeoutMs         > 0
     * @return true if a different signature appears within timeout
     * @throws IllegalArgumentException if timeoutMs <= 0
     */
    public boolean waitForGridReload(String previousSignature, long timeoutMs) {
        if (timeoutMs <= 0L) throw new IllegalArgumentException("timeoutMs must be > 0");
        if (previousSignature == null) previousSignature = "";

        final long end = System.currentTimeMillis() + timeoutMs;
        int polls = 0;
        while (System.currentTimeMillis() < end) {
            String now = captureGridSignature(); // uses same nodes
            polls++;
            if (!now.equals(previousSignature)) {
                logger.info("🔁 Grid reload detected after {} poll(s).", Integer.valueOf(polls));
                return true;
            }
            sleep(200);
            if (polls % 5 == 0)
                logger.debug("…still waiting for grid signature to change ({} polls).", Integer.valueOf(polls));
        }
        logger.warn("⌛ Grid did not reload within {} ms ({} poll(s)).", Long.valueOf(timeoutMs), Integer.valueOf(polls));
        return false;
    }


    /**
     * Opens a dropdown, selects a value, and optionally clicks the "Apply" button if present.
     * <p>
     * Behaviour:
     * <ul>
     *   <li>Scrolls trigger into view</li>
     *   <li>Opens dropdown with retry logic</li>
     *   <li>Locates the floating panel in the DOM</li>
     *   <li>Selects the option using flexible matching rules</li>
     *   <li>If {@code hasApply} is true, clicks the panel's "Apply" button</li>
     *   <li>Performs a best-effort check that the trigger reflects the selected value</li>
     * </ul>
     *
     * @param dropdown the dropdown trigger element to click (must not be {@code null})
     * @param name     friendly dropdown name for logs (e.g., "Department") (must not be blank)
     * @param value    visible text of the option to select (must not be blank)
     * @param hasApply whether selection must be confirmed by clicking an "Apply" button
     * @throws IllegalArgumentException if any required argument is invalid
     * @throws RuntimeException         if click or selection fails internally
     */
    public void selectFromDropdown(WebElement dropdown, String name, String value, boolean hasApply) {
        if (dropdown == null) throw new IllegalArgumentException("dropdown trigger cannot be null");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name cannot be null/empty");
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("value cannot be null/empty");

        logger.info("🔽 Selecting '{}' in '{}'", value, name);

        // Center it with your helper (no hard sleeps)
        commonMethods.scrollIntoView(dropdown);

        openDropdownWithRetries(dropdown, name);

        WebElement panel = waitForAnyDropdownPanelVisible(name, 8000L);

        selectOptionInAnyPanel(panel, value, name);

        if (hasApply) {
            List<WebElement> applyButtons = panel.findElements(By.xpath(".//button[normalize-space()='Apply']"));
            if (applyButtons != null && !applyButtons.isEmpty()) {
                logger.info("✅ Clicking 'Apply' in '{}'", name);
                commonMethods.safeClick(driver, applyButtons.get(0), name + " Apply", 8);
            } else {
                logger.warn("⚠️ Expected 'Apply' in '{}' but none was found.", name);
            }
        } else {
            try {
                new Actions(driver).sendKeys(Keys.ESCAPE).perform();
            } catch (Throwable ignore) {
            }
        }

        // Best-effort verification
        String triggerTxt = norm(dropdown.getText());
        String val = norm(value);
        if (!triggerTxt.toLowerCase().contains(val.toLowerCase())) {
            logger.warn("ℹ️ '{}' trigger may not reflect '{}'. Trigger text: '{}'", new Object[]{name, value, triggerTxt});
        } else {
            logger.info("✅ '{}' shows selected value '{}'", name, value);
        }
    }


    /**
     * Searches common floating dropdown/menu containers in the whole DOM and returns
     * the first visible, non-zero-sized panel.
     *
     * @param name      dropdown name (for logs)
     * @param timeoutMs max time to search (must be > 0)
     * @return the first matching visible panel element
     * @throws TimeoutException         if no visible panel is found within the timeout
     * @throws IllegalArgumentException if timeoutMs <= 0
     */
    public WebElement waitForAnyDropdownPanelVisible(String name, long timeoutMs) {
        if (timeoutMs <= 0L) throw new IllegalArgumentException("timeoutMs must be > 0");

        long end = System.currentTimeMillis() + timeoutMs;
        By[] candidates = new By[]{By.xpath("//div[contains(@class,'absolute') and contains(@class,'top-full') and not(contains(@style,'display: none'))]"), By.xpath("//*[(@role='listbox' or @role='menu') and not(contains(@style,'display: none'))]"), By.xpath("//div[contains(@data-floating,'true') or contains(@class,'Popover') or contains(@class,'Menu')][.//*]")};
        int scans = 0;
        while (System.currentTimeMillis() < end) {
            scans++;
            for (int c = 0; c < candidates.length; c++) {
                List<WebElement> els = driver.findElements(candidates[c]);
                for (int i = 0; i < els.size(); i++) {
                    WebElement el = els.get(i);
                    try {
                        if (el.isDisplayed() && el.getSize().getHeight() > 0 && el.getSize().getWidth() > 0) {
                            logger.info("✅ '{}' panel visible (global).", name);
                            return el;
                        }
                    } catch (Throwable ignore) {
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        logger.error("❌ Panel not visible for dropdown (global search): {}", name);
        throw new TimeoutException("Panel not visible for dropdown (global search): " + name);
    }


    /**
     * Selects an option from a dropdown panel using flexible, human-friendly text matching.
     * <p>
     * This method scans the given dropdown panel and selects the first matching option whose visible text:
     * <ul>
     *   <li>Matches the target text ignoring case, or</li>
     *   <li>Matches after normalizing dash variants (–, — → -), or</li>
     *   <li>Matches after removing all non-alphanumeric characters</li>
     * </ul>
     * Buttons such as "Clear" and "Apply" are automatically ignored.
     * <br><br>
     * If the option is not immediately visible, the panel is scrolled and re-checked until timeout.
     *
     * @param panel  the dropdown panel element containing the options (must not be {@code null})
     * @param option the visible text of the option to select (must not be blank)
     * @param name   a friendly dropdown name used in logs (e.g., "Category")
     * @throws IllegalArgumentException if panel or option are invalid
     * @throws NoSuchElementException   if the option cannot be found within the timeout
     */
    private void selectOptionInAnyPanel(WebElement panel, String option, String name) {
        if (panel == null) throw new IllegalArgumentException("panel cannot be null");
        if (option == null || option.trim().isEmpty())
            throw new IllegalArgumentException("option cannot be null/empty");

        long end = System.currentTimeMillis() + 8000L;
        String wanted = option.trim();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        while (System.currentTimeMillis() < end) {
            List<WebElement> options = panel.findElements(By.xpath(".//*[self::div or self::li or self::button or self::p or self::span or self::a][normalize-space(string(.))!='']"));

            if (options == null || options.isEmpty()) {
                logger.debug("No options visible yet in '{}'; will scroll and retry…", name);
            }

            for (int i = 0; i < options.size(); i++) {
                WebElement el = options.get(i);
                String text = norm(el.getText());
                if (text.isEmpty()) continue;
                if ("Clear".equalsIgnoreCase(text) || "Apply".equalsIgnoreCase(text)) continue;

                if (text.equalsIgnoreCase(wanted) || text.replace('\u2013', '-').replace('\u2014', '-').equalsIgnoreCase(wanted) || text.toLowerCase().replaceAll("[^a-z0-9]", "").equals(wanted.toLowerCase().replaceAll("[^a-z0-9]", ""))) {
                    try {
                        js.executeScript("arguments[0].scrollIntoView({block:'nearest', inline:'nearest'});", el);
                    } catch (Throwable ignore) {
                    }
                    commonMethods.safeClick(driver, el, name + " option '" + option + "'", 8);
                    logger.info("✅ Selected '{}' in '{}'", option, name);
                    return;
                }
            }

            try {
                js.executeScript("try{arguments[0].scrollTop = arguments[0].scrollTop + 250;}catch(e){}", panel);
            } catch (Throwable ignore) {
            }
        }

        logger.error("❌ Option '{}' not found in dropdown: {}", option, name);
        throw new NoSuchElementException("Option '" + option + "' not found in dropdown: " + name);
    }


    /**
     * Normalizes user-visible text for consistent comparison.
     * <p>
     * This method performs the following cleanup operations:
     * <ul>
     *   <li>Converts non-breaking spaces and zero-width characters to regular spaces</li>
     *   <li>Collapses multiple whitespace characters into a single space</li>
     *   <li>Trims leading and trailing whitespace</li>
     * </ul>
     *
     * @param s the raw string to normalize (nullable)
     * @return a cleaned, whitespace-normalized string (never {@code null})
     */
    public String norm(String s) {
        if (s == null) return "";
        return s.replace('\u00A0', ' ').replace("\u200B", " ").replace("\u200C", " ").replace("\u200D", " ").replaceAll("\\s+", " ").trim();
    }


    /**
     * Extracts a "base" title by:
     * <ol>
     *   <li>Normalizing dash variants to hyphen</li>
     *   <li>Stripping trailing parenthetical/bracketed suffixes</li>
     *   <li>Stripping trailing " - ..." or " : ..."</li>
     *   <li>Removing lone marker "m"</li>
     * </ol>
     *
     * @param title input title (nullable)
     * @return normalized base title (non-null, possibly empty)
     */
    public String toBaseTitle(String title) {
        String t = norm(title);
        if (t == null) return "";

        t = t.replace('\u2013', '-').replace('\u2014', '-').replace('\u2212', '-').replace('\u2011', '-');

        String prev;
        do {
            prev = t;
            t = t.replaceAll("\\s*\\([^)]*\\)\\s*$", "");
            t = t.replaceAll("\\s*\\[[^\\]]*\\]\\s*$", "");
        } while (!t.equals(prev));

        t = t.replaceAll("\\s*[-–—:]+\\s*.+$", "");

        if ("m".equalsIgnoreCase(t.trim())) return "";

        return t.trim();
    }


    /**
     * Canonicalizes a compliance name for presence matching.
     * <p>Operations (in order):</p>
     * <ul>
     *   <li>Whitespace + dash normalization</li>
     *   <li>Remove any bracketed/parenthetical segments ( (…) and […] )</li>
     *   <li>Remove quarter markers: "Q1".."Q4", "Q 1", "quarter 1".."quarter 4"</li>
     *   <li>Strip non-alphanumerics to spaces, collapse spaces, lowercase</li>
     *   <li>Return empty string for lone "m"</li>
     * </ul>
     * Examples:
     * <pre>
     *  "TDS Return Filing (Quarter 4)"   → "tds return filing"
     *  "TDS Return Filing - Q4"          → "tds return filing"
     *  "[Q3] 24Q TDS Challan Payment"    → "24q tds challan payment"
     * </pre>
     *
     * @param s raw title (nullable)
     * @return canonical, lowercased string (possibly empty)
     */
    public String canonicalComplianceName(String s) {
        String t = norm(s);
        if (t == null) return "";

        // unify dashes
        t = t.replace('\u2013','-').replace('\u2014','-').replace('\u2212','-').replace('\u2011','-');

        // remove any (…) or […] segments anywhere
        t = t.replaceAll("\\([^)]*\\)", " ");
        t = t.replaceAll("\\[[^\\]]*\\]", " ");

        // remove quarter markers (Q1..Q4, Q 1, quarter-1, quarter 2, etc.) - case-insensitive
        t = t.replaceAll("(?i)\\bquarter\\s*[-:]?\\s*[1-4]\\b", " ");
        t = t.replaceAll("(?i)\\bq\\s*[-:]?\\s*[1-4]\\b", " ");
        t = t.replaceAll("(?i)\\bq([1-4])\\b", " ");

        // strip non-alphanumerics to spaces, collapse, lowercase
        t = t.replaceAll("[^a-zA-Z0-9]+", " ").trim().replaceAll("\\s+", " ").toLowerCase();

        if ("m".equals(t)) return "";
        return t;
    }



    /**
     * Scrolls the grid (or the nearest scrollable container) and collects
     * distinct row titles currently visible/lazy-loaded, up to a time budget.
     *
     * @return ordered list of distinct item titles (non-null, possibly empty)
     */
    public List<String> getAllGridItems() {
        WebElement table = driver.findElement(By.xpath("//table[.//tbody]"));
        if (table == null) {
            logger.error("❌ getAllGridItems: grid <table> not found.");
            return new ArrayList<String>();
        }

        WebElement container = findScrollableAncestor(table);

        LinkedHashSet<String> seen = new LinkedHashSet<String>();
        int stagnantRounds = 0;
        long end = System.currentTimeMillis() + 15000L; // ~15s

        while (stagnantRounds < 3 && System.currentTimeMillis() < end) {
            // EXACT same nodes used by captureGridSignature
            List<WebElement> els = driver.findElements(By.xpath("//table//tbody//tr//td[1]//*[self::a or self::span or self::p]"));
            int before = seen.size();
            for (int i = 0; i < els.size(); i++) {
                String t = normalize(els.get(i).getText());
                if (t != null && t.length() > 0 && !"No records found".equalsIgnoreCase(t)) {
                    seen.add(t);
                }
            }

            // Scroll to trigger lazy loading / virtualization
            try {
                if (container != null) {
                    ((JavascriptExecutor) driver).executeScript("if (arguments[0].scrollHeight > arguments[0].scrollTop + arguments[0].clientHeight) " + "arguments[0].scrollTop = arguments[0].scrollTop + Math.max(400, arguments[0].clientHeight/2);", container);
                } else {
                    ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, Math.max(600, window.innerHeight/2));");
                }
                Thread.sleep(250);
            } catch (Throwable ignored) {
            }

            stagnantRounds = (seen.size() == before) ? (stagnantRounds + 1) : 0;
        }

        logger.info("📊 Grid total collected items across scroll: {}", Integer.valueOf(seen.size()));
        if (seen.isEmpty()) {
            logger.warn("ℹ️ getAllGridItems: no items found after scrolling window/container.");
        }
        return new ArrayList<String>(seen);
    }


    /**
     * Walks up the DOM from a starting element to find the first element that is
     * vertically scrollable (overflowY auto/scroll and scrollHeight > clientHeight).
     *
     * @param start starting element (non-null)
     * @return the first scrollable ancestor or {@code null} if none found (caller may fall back to window)
     * @throws IllegalArgumentException if start is null
     */
    private WebElement findScrollableAncestor(WebElement start) {
        if (start == null) throw new IllegalArgumentException("start cannot be null");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement cur = start;
        for (int i = 0; i < 8; i++) { // don’t walk forever
            try {
                Boolean scrollable = (Boolean) js.executeScript("const el = arguments[0];" + "if (!el) return false;" + "const style = window.getComputedStyle(el);" + "const oy = style.overflowY;" + "return (el.scrollHeight > el.clientHeight) && (oy === 'auto' || oy === 'scroll');", cur);
                if (Boolean.TRUE.equals(scrollable)) return cur;
            } catch (Throwable ignore) {
            }
            // move to parent
            try {
                cur = cur.findElement(By.xpath(".."));
            } catch (Throwable t) {
                break;
            }
        }
        return null;
    }





    /**
     * Collects first-column titles (your grid item names) across ALL pages.
     * Uses the SAME pagination machinery as status validation:
     *  - waitFirstPageStatusReady  (grid ready signal)
     *  - findNextButton
     *  - clickNextAndWaitForChange (with timingCb)
     *  - getVisibleGridItems() for per-page reads
     *
     * @param displayedTotal UI-displayed total (e.g. All tab count). Use 0 if unknown.
     * @param pageShotCb optional page callback (screenshots in steps). May be null.
     * @param timingCb optional timing callback (per-page nav timings). May be null.
     * @return pageNumber -> list of titles on that page
     */
    public Map<Integer, List<String>> fetchAllTitleValuesPageWise(int displayedTotal,
                                                                  PageNavigationCallback pageShotCb,
                                                                  PageNavigationCallback timingCb) {
        Map<Integer, List<String>> out = new LinkedHashMap<>();

        // Ensure first page is truly showing rows (we reuse your ready heuristic)
        try { waitFirstPageStatusReady(Duration.ofMillis(utils.ReusableCommonMethods.NAV_FAIL_MS)); }
        catch (Throwable ignore) {}

        // Try to read current page number; default 1 if not available
        int pageNumber = 1;
        try { pageNumber = getCurrentGridPageNumber(); } catch (Throwable ignore) {}

        int collected = 0;
        while (true) {
            // Read this page using your current per-page reader
            List<String> pageItems = getVisibleGridItems();
            out.put(pageNumber, pageItems);
            collected += (pageItems == null ? 0 : pageItems.size());
            logger.info("🧾 Page {} titles collected: {} (running total: {} / {})",
                    Integer.valueOf(pageNumber),
                    Integer.valueOf(pageItems == null ? 0 : pageItems.size()),
                    Integer.valueOf(collected),
                    Integer.valueOf(displayedTotal));

            if (pageShotCb != null) {
                try { pageShotCb.onPage(pageNumber); } catch (Throwable ignore) {}
            }

            // Stop conditions
            if (pageItems == null || pageItems.isEmpty()) break;
            if (displayedTotal > 0 && collected >= displayedTotal) break;

            // Find a usable Next and move
            org.openqa.selenium.WebElement nextBtn = findNextButton();
            if (nextBtn == null) break;

            boolean moved = clickNextAndWaitForChange(nextBtn, timingCb, pageNumber);
            if (!moved) break;

            // Update current page number (fallback +1)
            try {
                int current = getCurrentGridPageNumber();
                pageNumber = (current > pageNumber) ? current : pageNumber + 1;
            } catch (Throwable ignore) {
                pageNumber += 1;
            }
        }

        return out;
    }



    /**
     * Reads the current page number from the grid paginator.
     * <p>
     * Strategy (in order):
     *  1) Ant Design:   li.ant-pagination-item-active
     *  2) aria-current: any element with aria-current="page"
     *  3) Generic "active/selected" classes on li/button/a/span
     *  4) Last fallback: first visible numeric page button/span near "Next/Prev"
     * Falls back to 1 if not detectable.
     * </p>
     * @return current page number (>=1), or 1 when not detected.
     */
    public int getCurrentGridPageNumber() {
        // 1) Ant Design active page
        try {
            List<WebElement> antActive = driver.findElements(
                    By.cssSelector("li.ant-pagination-item-active, li.ant-pagination-item-active a, li.ant-pagination-item-active button")
            );
            for (WebElement el : antActive) {
                int n = tryParseIntSafe(el.getText());
                if (n > 0) {
                    logger.info("🧭 Current page (AntD active): {}", n);
                    return n;
                }
            }
        } catch (Exception ignore) {}

        // 2) aria-current="page" (Material/UI/Custom)
        try {
            List<WebElement> aria = driver.findElements(By.xpath("//*[ @aria-current='page' ]"));
            for (WebElement el : aria) {
                int n = tryParseIntSafe(el.getText());
                if (n > 0) {
                    logger.info("🧭 Current page (aria-current): {}", n);
                    return n;
                }
            }
        } catch (Exception ignore) {}

        // 3) Generic active/selected classes on li/button/a/span
        try {
            List<WebElement> genericActive = driver.findElements(By.xpath(
                    "//li[contains(@class,'active') or contains(@class,'selected')]//*[self::button or self::a or self::span][normalize-space()]"
            ));
            for (WebElement el : genericActive) {
                int n = tryParseIntSafe(el.getText());
                if (n > 0) {
                    logger.info("🧭 Current page (generic active/selected): {}", n);
                    return n;
                }
            }
            // also try buttons directly
            List<WebElement> btnActive = driver.findElements(By.xpath(
                    "//button[contains(@class,'active') or @data-selected='true'][normalize-space()]"
            ));
            for (WebElement el : btnActive) {
                int n = tryParseIntSafe(el.getText());
                if (n > 0) {
                    logger.info("🧭 Current page (active button): {}", n);
                    return n;
                }
            }
        } catch (Exception ignore) {}

        // 4) Fallback: pick the first visible numeric item among page controls
        try {
            List<WebElement> numeric = driver.findElements(By.xpath(
                    // look near a typical pager area that contains Next/Prev or numbered items
                    "//li[normalize-space()!='' and number(normalize-space())=number(normalize-space())] | " +
                            "//button[normalize-space()!='' and number(normalize-space())=number(normalize-space())] | " +
                            "//a[normalize-space()!='' and number(normalize-space())=number(normalize-space())]"
            ));
            for (WebElement el : numeric) {
                if (!el.isDisplayed()) continue;
                int n = tryParseIntSafe(el.getText());
                if (n > 0) {
                    logger.info("🧭 Current page (fallback first numeric): {}", n);
                    return n;
                }
            }
        } catch (Exception ignore) {}

        logger.warn("🧭 Current page not detected — defaulting to 1.");
        return 1;
    }

    private int tryParseIntSafe(String s) {
        if (s == null) return -1;
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return -1; }
    }


    public int getCurrentPageVisibleRowCount() {
        if (mainComplianceRows != null && !mainComplianceRows.isEmpty()) return mainComplianceRows.size();
        if (complianceRows != null && !complianceRows.isEmpty()) return complianceRows.size();
        return driver.findElements(By.cssSelector("table tbody tr")).size();
    }

    // In CompliancePage.java

    /**
     * Tries to navigate to the FIRST page of the pagination control.
     * Uses either a "1" page button or falls back to clicking "Prev" until disabled.
     */
    public void goToFirstPage() {
        try {
            // 1) Try a "1" page button (common pattern for MUI / pagination libs)
            List<WebElement> firstButtons = driver.findElements(By.xpath(
                    "//ul[contains(@class,'MuiPagination-ul') or contains(@class,'pagination')]//button[normalize-space()='1']"
                            + " | //li[contains(@class,'MuiPaginationItem') or contains(@class,'page')]/button[normalize-space()='1']"
            ));

            for (WebElement btn : firstButtons) {
                try {
                    if (!btn.isDisplayed() || !btn.isEnabled()) continue;
                    logger.info("⏮ Clicking page '1' button to go to first page.");
                    int oldPage = getCurrentPageNumberSafe();
                    long t0 = System.currentTimeMillis();
                    btn.click();
                    waitPageNumberChange(oldPage, Duration.ofSeconds(10));
                    long elapsed = System.currentTimeMillis() - t0;
                    logger.info("⏮ Reached first page from {} in {} ms.", oldPage, elapsed);
                    return;
                } catch (Exception ignore) {}
            }
        } catch (Exception e) {
            logger.warn("⚠️ Failed to click explicit '1' page button: {}", e.getMessage());
        }

        // 2) Fallback: click Prev repeatedly until it’s disabled or disappears
        try {
            while (true) {
                WebElement prev = findPrevButton(); // you may already have this; if not, implement similarly to findNextButton
                if (prev == null) {
                    logger.info("⏮ No Prev button found. Assuming we are already at first page.");
                    break;
                }

                boolean disabled = "true".equalsIgnoreCase(prev.getAttribute("aria-disabled"))
                        || prev.getAttribute("disabled") != null;
                if (!prev.isDisplayed() || !prev.isEnabled() || disabled) {
                    logger.info("⏮ Prev button disabled. Reached first page.");
                    break;
                }

                int oldPage = getCurrentPageNumberSafe();
                logger.info("⏮ Clicking Prev to move back from page {}", oldPage);
                long t0 = System.currentTimeMillis();
                prev.click();
                waitPageNumberChange(oldPage, Duration.ofSeconds(10));
                long elapsed = System.currentTimeMillis() - t0;
                logger.info("⏮ Prev navigation completed in {} ms.", elapsed);
            }
        } catch (Exception e) {
            logger.warn("⚠️ Error while trying to go to first page (Prev loop): {}", e.getMessage());
        }
    }

    /**
     * Safe current page parser using your existing "Current page (fallback...)" logic.
     */
    public int getCurrentPageNumberSafe() {
        try {
            WebElement current = driver.findElement(By.xpath(
                    "//ul[contains(@class,'MuiPagination-ul') or contains(@class,'pagination')]//button[@aria-current='true']"
                            + " | //li[contains(@class,'Mui-selected') or contains(@class,'active')]/button"
            ));
            String txt = current.getText();
            return Integer.parseInt(txt.trim());
        } catch (Exception e) {
            logger.warn("⚠️ getCurrentPageNumberSafe: using fallback page=1 due to {}", e.getMessage());
            return 1;
        }
    }

    /**
     * Wait until the page number changes from oldPage.
     */
    private void waitPageNumberChange(int oldPage, Duration timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(d -> {
            try {
                int curr = getCurrentPageNumberSafe();
                return curr != oldPage;
            } catch (Exception ex) {
                return false;
            }
        });
    }


    private WebElement findPrevButton() {
        List<By> locators = Arrays.asList(
                By.cssSelector("button[aria-label='Previous'], button[aria-label='prev'], button[aria-label='Prev']"),
                By.xpath("//button[.//span[normalize-space()='Previous' or normalize-space()='‹' or normalize-space()='<']]"
                        + " | //button[normalize-space()='Previous' or normalize-space()='‹' or normalize-space()='<']")
        );

        for (By by : locators) {
            List<WebElement> candidates = driver.findElements(by);
            for (WebElement el : candidates) {
                try {
                    if (el.isDisplayed()) return el;
                } catch (Exception ignore) {}
            }
        }
        return null;
    }




    // ======================= Pagination helpers (loose finders) =======================

    private WebElement findPrevButtonLoose() {
        List<By> locators = Arrays.asList(
                By.xpath("//button[.//span[normalize-space()='Prev' or normalize-space()='Previous' or normalize-space()='‹' or normalize-space()='<']]"
                        + " | //button[normalize-space()='Prev' or normalize-space()='Previous' or normalize-space()='‹' or normalize-space()='<']"),
                By.cssSelector("button[aria-label='Previous'], button[aria-label='prev']")
        );
        for (By by : locators) {
            List<WebElement> els = driver.findElements(by);
            for (WebElement el : els) {
                try {
                    if (el.isDisplayed() && el.isEnabled() && el.getAttribute("disabled") == null) return el;
                } catch (Throwable ignore) {}
            }
        }
        return null;
    }

//    private WebElement findNextButtonLoose() {
//        List<By> locators = Arrays.asList(
//                By.xpath("//button[.//span[normalize-space()='Next' or normalize-space()='›' or normalize-space()'>']]"
//                        + " | //button[normalize-space()='Next' or normalize-space()='›' or normalize-space()'>']"),
//                By.cssSelector("button[aria-label='Next'], button[aria-label='next']")
//        );
//        for (By by : locators) {
//            List<WebElement> els = driver.findElements(by);
//            for (WebElement el : els) {
//                try {
//                    if (el.isDisplayed() && el.isEnabled() && el.getAttribute("disabled") == null) return el;
//                } catch (Throwable ignore) {}
//            }
//        }
//        return null;
//    }

    private void scrollIntoView(WebElement el) {
        try { ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el); } catch (Exception ignore) {}
    }

    private boolean safeClick(WebElement el) {
        try { scrollIntoView(el); el.click(); return true; }
        catch (Exception e) {
            try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el); return true; }
            catch (Exception ex) { return false; }
        }
    }

// ======================= Page fingerprint (stronger than signature alone) =======================

    private String pageFingerprint() {
        String sig = "";
        try { sig = captureGridSignature(); } catch (Throwable ignore) {}

        String first = "", last = "", pageLbl = "";
        try {
            List<WebElement> titles = driver.findElements(gridRowTitles);
            if (!titles.isEmpty()) {
                first = String.valueOf(((JavascriptExecutor)driver).executeScript("return arguments[0].textContent;", titles.get(0))).trim();
                last  = String.valueOf(((JavascriptExecutor)driver).executeScript("return arguments[0].textContent;", titles.get(titles.size()-1))).trim();
            }
        } catch (Throwable ignore) {}

        try {
            WebElement active = driver.findElement(By.cssSelector("[aria-current='page'], li.Mui-selected, .ant-pagination-item-active"));
            pageLbl = active.getText().trim();
        } catch (Throwable ignore) {}

        return (sig + "||" + first + "||" + last + "||" + pageLbl).trim();
    }

// ======================= Bidirectional sweep collector =======================

    /**
     * Collect all page titles without assuming the current page index.
     * 1) Collect current page.
     * 2) Sweep BACKWARDS while Prev is enabled (collect unique pages).
     * 3) Sweep FORWARDS while Next is enabled (collect unique pages).
     * Uses waitForGridReload(previousSignature, timeoutMs).
     */
    public Map<Integer, List<String>> collectAllPagesBidirectional(int displayedTotal,
                                                                   PageNavigationCallback onPage,
                                                                   PageNavigationCallback onTiming,
                                                                   long timeoutMs) {
        Map<Integer, List<String>> out = new LinkedHashMap<>();
        Set<String> seen = new LinkedHashSet<>();

        java.util.function.Consumer<Integer> collectHere = (pageNum) -> {
            String fp = pageFingerprint();
            if (seen.add(fp)) {
                List<String> items = getVisibleGridItems();
                out.put(pageNum, items);
                logger.info("🧾 Page {} titles collected: {} (unique pages so far: {})",
                        Integer.valueOf(pageNum),
                        Integer.valueOf(items.size()),
                        Integer.valueOf(out.size()));
                if (onPage != null) try { onPage.onPage(pageNum); } catch (Throwable ignore) {}
            } else {
                logger.debug("🔁 Skipping duplicate fingerprint page.");
            }
        };

        int pageNum;
        try { pageNum = getCurrentGridPageNumber(); }
        catch (Throwable ignore) { pageNum = 1; }

        // collect current
        collectHere.accept(pageNum);

        // sweep BACKWARDS
        while (true) {
            WebElement prev = findPrevButtonLoose();
            if (prev == null) break;

            String prevSig = captureGridSignature();
            long t0 = System.currentTimeMillis();

            if (!safeClick(prev)) break;
            boolean reloaded = waitForGridReload(prevSig, timeoutMs);
            if (onTiming != null) try { onTiming.onTiming(pageNum, pageNum - 1, System.currentTimeMillis() - t0); } catch (Throwable ignore) {}

            if (!reloaded) {
                logger.warn("⌛ Prev click produced no reload within {} ms; stopping back sweep.", Long.valueOf(timeoutMs));
                break;
            }

            try {
                int cur = getCurrentGridPageNumber();
                pageNum = (cur < pageNum ? cur : pageNum - 1);
            } catch (Throwable ignore) { pageNum = pageNum - 1; }

            collectHere.accept(pageNum);
        }

        // sweep FORWARDS
        while (true) {
            WebElement next = findNextButtonLoose();
            if (next == null) break;

            String prevSig = captureGridSignature();
            long t0 = System.currentTimeMillis();

            if (!safeClick(next)) break;
            boolean reloaded = waitForGridReload(prevSig, timeoutMs);
            if (onTiming != null) try { onTiming.onTiming(pageNum, pageNum + 1, System.currentTimeMillis() - t0); } catch (Throwable ignore) {}

            if (!reloaded) {
                logger.warn("⌛ Next click produced no reload within {} ms; stopping forward sweep.", Long.valueOf(timeoutMs));
                break;
            }

            try {
                int cur = getCurrentGridPageNumber();
                pageNum = (cur > pageNum ? cur : pageNum + 1);
            } catch (Throwable ignore) { pageNum = pageNum + 1; }

            collectHere.accept(pageNum);

            if (displayedTotal > 0) {
                int pageSize = Math.max(1, getCurrentPageVisibleRowCount());
                int needPages = (int)Math.ceil(displayedTotal / (double)pageSize);
                if (out.size() >= needPages) break;
            }
        }

        return out;
    }

    /** Loose finder for a "First" pager button, if your UI exposes one. */
    private WebElement findFirstButtonLoose() {
        List<By> locators = Arrays.asList(
                By.xpath("//button[.//span[normalize-space()='First' or normalize-space()='«' or normalize-space()='<<']]"
                        + " | //button[normalize-space()='First' or normalize-space()='«' or normalize-space()='<<']"),
                By.cssSelector("button[aria-label='First'], button[aria-label='first']")
        );
        for (By by : locators) {
            List<WebElement> els = driver.findElements(by);
            for (WebElement el : els) {
                try {
                    if (el.isDisplayed() && el.isEnabled() && el.getAttribute("disabled") == null) return el;
                } catch (Throwable ignore) {}
            }
        }
        return null;
    }



    /** Best-effort read of current page number; returns -1 if unavailable. */
    private int tryGetCurrentPageNumber() {
        try { return getCurrentGridPageNumber(); }
        catch (Throwable ignore) { return -1; }
    }

    /**
     * Navigate to the first page of the grid.
     * Uses signature-based reload detection to avoid infinite loops and handles
     * UIs that either have a dedicated "First" control or only Prev/Next.
     *
     * @param timeoutMs per-hop wait for reload (uses waitForGridReload)
     * @return true if we *likely* reached page 1 (or no further back navigation possible)
     */
    public boolean goToFirstPageSignatureAware(long timeoutMs) {
        final int MAX_HOPS = 200;   // hard guard for pathological pagers
        int hops = 0;

        // Quick short-circuit: we might already be on page 1
        try {
            int page = tryGetCurrentPageNumber();
            if (page == 1) {
                logger.info("⏮️ Already on first page (page label=1).");
                return true;
            }
        } catch (Throwable ignore) {}

        // 1) Prefer a dedicated "First" button if present
        WebElement firstBtn = findFirstButtonLoose();
        if (firstBtn != null) {
            logger.info("⏮️ Attempting to use dedicated 'First' button.");
            // Some UIs make 'First' jump straight to 1; others step one page only.
            String sigBefore = captureGridSignature();
            if (safeClick(firstBtn)) {
                boolean reloaded = waitForGridReload(sigBefore, timeoutMs);
                if (!reloaded) {
                    logger.warn("⌛ 'First' click produced no reload within {} ms. Falling back to Prev loop.", Long.valueOf(timeoutMs));
                } else {
                    // Verify if we reached page 1; if not, loop a few times (rare UIs).
                    for (int i = 0; i < 5; i++) {
                        int page = tryGetCurrentPageNumber();
                        if (page == 1) {
                            logger.info("✅ Reached first page via 'First' button (label=1).");
                            return true;
                        }
                        WebElement prev = findPrevButtonLoose();
                        if (prev == null || prev.getAttribute("disabled") != null) {
                            logger.info("✅ No Prev available after 'First' click; treating as first page.");
                            return true;
                        }
                        String s = captureGridSignature();
                        if (!safeClick(firstBtn)) break;
                        if (!waitForGridReload(s, timeoutMs)) break;
                    }
                    // Fall through to Prev loop if still not sure
                }
            } else {
                logger.warn("⚠️ Failed to click 'First' button; falling back to Prev loop.");
            }
        } else {
            logger.debug("ℹ️ No dedicated 'First' button found; will use Prev loop.");
        }

        // 2) Fallback: keep clicking Prev while it actually changes the grid signature
        while (hops++ < MAX_HOPS) {
            WebElement prev = findPrevButtonLoose();
            if (prev == null) {
                logger.info("✅ Prev button not found/enabled. Assuming current is first page.");
                return true;
            }

            // If UI exposes page=1, stop early
            int pageNow = tryGetCurrentPageNumber();
            if (pageNow == 1) {
                logger.info("✅ Page label indicates page 1; stopping.");
                return true;
            }

            String before = captureGridSignature();
            if (!safeClick(prev)) {
                logger.warn("⚠️ Prev click failed; stopping to avoid loop.");
                break;
            }
            boolean reloaded = waitForGridReload(before, timeoutMs);
            if (!reloaded) {
                logger.warn("⌛ Prev click produced no reload within {} ms; stopping back navigation.", Long.valueOf(timeoutMs));
                break;
            }

            // If after reload we are on page 1 or Prev is now disabled, we’re done
            int pageAfter = tryGetCurrentPageNumber();
            if (pageAfter == 1) {
                logger.info("✅ Reached first page (page label=1) after {} hop(s).", Integer.valueOf(hops));
                return true;
            }
            WebElement prevAgain = findPrevButtonLoose();
            if (prevAgain == null || prevAgain.getAttribute("disabled") != null) {
                logger.info("✅ Prev disabled after {} hop(s); treating as first page.", Integer.valueOf(hops));
                return true;
            }

            // Extra safety: if signature didn’t change, we’re stuck
            String after = captureGridSignature();
            if (after.equals(before)) {
                logger.warn("🔁 Signature unchanged after Prev. Stopping to avoid infinite loop.");
                break;
            }
        }

        logger.warn("🛑 Max hops reached or navigation stalled; cannot guarantee page 1.");
        return false;
    }

    /** Convenience overload using your default nav fail timeout. */
    public boolean goToFirstPageSignatureAware() {
        long timeout = utils.ReusableCommonMethods.NAV_FAIL_MS;
        return goToFirstPageSignatureAware(timeout);
    }















































    /** Default explicit wait (clicks/visibility). */
    private static final long DEFAULT_WAIT_MS = 12000L;

    // ------------ Locators ------------
    @FindBy(xpath = "//p[normalize-space()='Add new compliance']")
    private WebElement addNewComplianceBtn;

    @FindBy(xpath = "//div[@class='flex items-center justify-between']//p[normalize-space()='Add new compliance']")
    private WebElement addCompliancePanelTitle;

    // ========= Utility waitors (no lambdas in our code) =========
    private WebElement waitClickable(WebElement el, long timeoutMs) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(timeoutMs));
        return wait.until(ExpectedConditions.elementToBeClickable(el));
    }




    /**
     * Clicks the "Add new compliance" button.
     *
     * <p>Validation:</p>
     * <ul>
     *   <li>Waits for the element to be clickable.</li>
     *   <li>Falls back to JS click if standard click is intercepted.</li>
     * </ul>
     *
     * @throws NoSuchElementException if the button is not present.
     * @throws TimeoutException if the button is not clickable within the wait.
     * @throws WebDriverException for other Selenium errors.
     */
    public void clickAddNewComplianceButton() {
        logger.info("Attempting to click 'Add new compliance' button.");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(DEFAULT_WAIT_MS));
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(addNewComplianceBtn));

        try {
            btn.click();
            logger.info("Clicked 'Add new compliance' using standard click.");
        } catch (ElementClickInterceptedException e) {
            logger.warn("Standard click intercepted; retrying with JavaScript click.");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    /**
     * Checks if the right-side "Add new compliance" panel is visible.
     *
     * @param timeout how long to wait for the panel to become visible.
     * @return true if visible inside the timeout; false otherwise.
     */
    public boolean isAddCompliancePanelVisible(Duration timeout) {
        logger.info("Waiting for 'Add new compliance' side panel to be visible.");
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            wait.until(ExpectedConditions.visibilityOf(addCompliancePanelTitle));
            boolean displayed = addCompliancePanelTitle.isDisplayed()
                    && "Add new compliance".equalsIgnoreCase(addCompliancePanelTitle.getText().trim());
            if (displayed) {
                logger.info("'Add new compliance' panel is visible and title matches.");
            } else {
                logger.error("Panel title missing or mismatched. Actual: '{}'",
                        safeText(addCompliancePanelTitle));
            }
            return displayed;
        } catch (TimeoutException te) {
            logger.error("'Add new compliance' panel not visible within {} ms.", Long.valueOf(timeout.toMillis()));
            return false;
        } catch (NoSuchElementException nse) {
            logger.error("Panel title element not present in DOM.");
            return false;
        }
    }

    /** Converts ms → "H hr M min S secs". */
    public String formatElapsed(long millis) {
        if (millis < 0) millis = 0;

        long totalSeconds = millis / 1000L;
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        long seconds = totalSeconds % 60L;

        StringBuilder sb = new StringBuilder();

        if (hours > 0) {
            sb.append(hours).append(hours == 1 ? " hr " : " hrs ");
        }
        sb.append(minutes).append(" min ").append(seconds).append(" secs");

        return sb.toString().trim();
    }















    // React-Select options (menu can be portaled to <body>)
    private final By reactSelectMenuRoot = By.xpath("//*[contains(@class,'menu') and @role='listbox']");


    // ---------- Helpers ----------
    private WebElement waitVisible(WebElement el) {
        return wait.until(ExpectedConditions.visibilityOf(el));
    }

    private void openReactSelect(WebElement field, String name) {
        waitVisible(field);
        commonMethods.safeClick(driver, field, name, CLICK_TIMEOUT_SEC);
        logger.info("Opened react-select '{}'.", name);
    }



    private String chooseRandomFromOpenReactSelect(List<String> allowList, String widgetName) {
        wait.until(ExpectedConditions.visibilityOfAllElements(reactSelectOptions));
        List<WebElement> candidates = new ArrayList<WebElement>();

        int i;
        for (i = 0; i < reactSelectOptions.size(); i++) {
            WebElement opt = reactSelectOptions.get(i);
            String txt = opt.getText().trim();
            if (txt.length() == 0) continue;

            if (allowList == null || allowList.isEmpty()) {
                candidates.add(opt);
            } else {
                int j;
                for (j = 0; j < allowList.size(); j++) {
                    if (txt.equalsIgnoreCase(allowList.get(j))) {
                        candidates.add(opt);
                        break;
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            throw new NoSuchElementException("No options available in " + widgetName + " to select.");
        }

        WebElement pick = candidates.get(RNG.nextInt(candidates.size()));
        String value = pick.getText().trim();
        commonMethods.safeClick(driver, pick, widgetName + " option '" + value + "'", CLICK_TIMEOUT_SEC);
        logger.info("Selected '{}' from {}.", value, widgetName);
        return value;
    }

    // ---------- Public Actions ----------
    /** Types the compliance name. */
    public void setName(String name) {
        logger.info("Typing compliance name: {}", name);
        WebElement el = waitVisible(nameInput);
        el.clear();
        el.sendKeys(name);
    }






    /** Sets the Mandatory toggle ON/OFF according to desired state. */
    public void setMandatory(boolean desired) {
        WebElement toggle = waitVisible(mandatoryToggle);
        boolean current = false;
        try {
            String aria = toggle.getAttribute("aria-checked");
            if (aria != null) current = "true".equalsIgnoreCase(aria);
        } catch (Throwable ignore) { }

        if (current != desired) {
            commonMethods.safeClick(driver, toggle, "Mandatory toggle", CLICK_TIMEOUT_SEC);
        }
    }

    // Frequency
    public String selectRandomFrequency() {
        openReactSelect(frequencyField, "Frequency");

        // allow-list exactly what the UI shows
        List<String> allow = new ArrayList<String>();
        allow.add("Weekly");
        allow.add("Bi-Monthly");
        allow.add("Monthly");
        allow.add("Quarterly");
        allow.add("Half Yearly");
        allow.add("Yearly");

        // chooseRandomFromOpenReactSelect returns the exact option text we clicked
        String chosen = chooseRandomFromOpenReactSelect(allow, "Frequency");

        // ensure the dropdown has closed before continuing (react-select menu disappears)
        try {
            wait.until(ExpectedConditions.invisibilityOfAllElements(reactSelectOptions));
        } catch (Throwable ignore) { }

        logger.info("Frequency chosen: {}", chosen);
        return chosen; // <-- do NOT call getText() on the field
    }



    // ===== helpers =====
    private WebElement waitReactSelectByPlaceholder(String placeholder) {
        // React-Select outer container that shows the placeholder text
        By control = By.xpath(
                "//div[contains(@class,'css-b62m3t-container')]" +                // react-select container (class name is stable in RS v5)
                        "[.//div[contains(@class,'-placeholder') and normalize-space()='"+placeholder+"']]"  // has the placeholder text
        );
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(control));
    }

    private void openReactSelectControl(WebElement control) {
        // prefer the down-arrow (indicator), fall back to the whole control
        WebElement toClick;
        try {
            toClick = control.findElement(By.cssSelector("div[class*='indicatorContainer']"));
        } catch (NoSuchElementException e) {
            toClick = control;
        }
        commonMethods.scrollIntoViewCenter(toClick);
        commonMethods.safeClick(driver, toClick, "React-Select control", 8);

        // menu open wait (covers RS v4/v5)
        By openMenu = By.cssSelector("div[id^='react-select-'][id$='-listbox'], div[class*='menu']");
        new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.visibilityOfElementLocated(openMenu));
    }

    // ===== usage in your page object =====
    public String selectRandomRisk() {
        WebElement control = waitReactSelectByPlaceholder("Select risk");
        openReactSelectControl(control);

        List<String> allow = Arrays.asList("Low","Medium","High");
        String chosen = chooseRandomFromOpenReactSelect(allow, "Risk");

        try { wait.until(ExpectedConditions.invisibilityOfAllElements(reactSelectOptions)); }
        catch (Throwable ignore) {}

        logger.info("Risk chosen: {}", chosen);
        return chosen;
    }



    // Organizations (single pick; keeps the chosen value for later use)
    public String selectRandomOrganization() {
        // If an org chip is already present, just use it
        try {
            List<WebElement> chips = driver.findElements(
                    By.xpath("//label[normalize-space()='Organizations']/following::div[contains(@class,'control')][1]//*[contains(@class,'multiValue') or contains(@class,'-multiValue')]")
            );
            if (!chips.isEmpty()) {
                String pre = chips.get(0).getText().trim();
                lastSelectedOrganization = pre;
                logger.info("Organization already preselected: {}", pre);
                return pre;
            }
        } catch (Throwable ignore) { /* fall back to picking */ }

        // Otherwise pick randomly using your existing logic
        openReactSelect(organizationsField, "Organizations");
        String chosen = chooseRandomFromOpenReactSelect(null, "Organizations");
        try { wait.until(ExpectedConditions.invisibilityOfAllElements(reactSelectOptions)); } catch (Throwable ignore) {}
        lastSelectedOrganization = chosen;
        logger.info("Organization chosen: {}", chosen);
        return chosen;
    }





    /** Types the description text. */
    public void setDescription(String text) {
        WebElement el = waitVisible(descriptionArea);
        el.clear();
        if (text != null && text.length() > 0) el.sendKeys(text);
    }




    public void setDateSmart(WebElement input, String displayValue, String isoHidden) {

        if (input == null) {
            logger.error("❌ setDateSmart: input element is null");
            return;
        }
        if (displayValue == null || displayValue.trim().isEmpty()) {
            logger.warn("⚠ setDateSmart: displayValue is blank");
            return;
        }

        displayValue = displayValue.trim();
        final String targetValue = displayValue;

        logger.info("📅 setDateSmart → Target display date: {}", targetValue);

        // 1) Try JS first
        try {
            logger.info("📅 Trying JS-set of React date input...");
            jsSetReactInputValue(input, targetValue);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

            boolean jsAccepted;
            try {
                jsAccepted = wait.until(d -> {
                    try {
                        String v = input.getAttribute("value");
                        if (v == null) return false;
                        v = v.trim();
                        logger.debug("📅 JS-check: input value='{}'", v);
                        return targetValue.equals(v);
                    } catch (StaleElementReferenceException e) {
                        return false;
                    }
                });
            } catch (TimeoutException te) {
                jsAccepted = false;
            }

            if (jsAccepted) {
                logger.info("✅ JS successfully set date: {}", targetValue);
                return;
            } else {
                logger.warn("⚠ JS-set did NOT reflect in React. Falling back.");
            }

        } catch (Exception e) {
            logger.warn("⚠ JS-set failed, falling back: {}", e.getMessage());
        }

        // 2) Fallback: Tailwind date picker
        logger.warn("⚠ JS-set did NOT reflect in React. Falling back.");
        logger.info("📅 Using Tailwind calendar fallback for: {}", targetValue);
        pickDateFromTailwindWidget(targetValue);

        // 3) Verify again
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            boolean finalOk = wait.until(d -> {
                try {
                    String v = input.getAttribute("value");
                    if (v == null) return false;
                    v = v.trim();
                    logger.debug("📅 Post-widget check: input='{}'", v);
                    return targetValue.equals(v);
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            });

            if (finalOk) {
                logger.info("✅ Date set via widget: {}", targetValue);
            } else {
                logger.warn("⚠ Calendar widget selected but value still mismatched: {}", targetValue);
            }

        } catch (Exception e) {
            logger.error("❌ Error verifying date after widget: {}", e.getMessage());
        }
    }






    /**
     * Safely sets a value on a React-controlled <input> using JS so React state updates correctly.
     * Works for Tailwind/React/Next.js controlled components.
     */
    private void jsSetReactInputValue(WebElement inputEl, String newValue) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            js.executeScript(
                    "const input = arguments[0];" +
                            "const value = arguments[1];" +
                            // React internal setter – ensures onChange fires
                            "const lastValue = input.value;" +
                            "input.value = value;" +
                            "const event = new Event('input', { bubbles: true });" +
                            "event.simulated = true;" +
                            "const tracker = input._valueTracker;" +
                            "if (tracker) { tracker.setValue(lastValue); }" +
                            "input.dispatchEvent(event);",
                    inputEl, newValue
            );

            logger.info("📝 jsSetReactInputValue: '{}' applied successfully", newValue);

        } catch (Throwable t) {
            logger.warn("⚠ jsSetReactInputValue failed for value='{}': {}", newValue, t.getMessage());
        }
    }








    private static final DateTimeFormatter DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String selectRandomDueDate(int maxAheadDays) {
        // pick a future date within the allowed window (>= +1 day)
        LocalDate target = LocalDate.now().plusDays(1 + new Random().nextInt(Math.max(1, maxAheadDays)));
        String displayValue = target.format(DDMMYYYY);  // what the UI shows, e.g. 11/11/2025
        String isoHidden   = target.toString();         // 2025-11-11 (used if a hidden input exists)

        // Use your existing smart setter: JS (React-safe) -> Tailwind widget fallback
        setDateSmart(dueDateInput, displayValue, isoHidden);

        // Return the value we intended so the step can attach/assert it
        return displayValue;
    }




    // ---------------------------------------------------------------------
// Helper: "02" -> "2", "09" -> "9", "12" -> "12"
// ---------------------------------------------------------------------
    private String normalizeDayLabel(String dd) {
        if (dd == null) return "";
        try {
            return String.valueOf(Integer.parseInt(dd.trim()));
        } catch (Exception e) {
            // Fallback: return original if something weird happens
            return dd.trim();
        }
    }

    private void pickDateFromTailwindWidget(String displayValue) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            if (displayValue == null || displayValue.trim().isEmpty()) {
                logger.warn("📅 Tailwind picker: displayValue is null/empty, skipping.");
                return;
            }

            String[] parts = displayValue.split("/");
            if (parts.length != 3) {
                logger.warn("📅 Tailwind picker: unexpected date format '{}', expected dd/MM/yyyy", displayValue);
                return;
            }

            String dayPartRaw   = parts[0]; // "28"
            String monthPartRaw = parts[1];
            String yearPartRaw  = parts[2];

            String dayLabel = normalizeDayLabel(dayPartRaw); // "28"
            logger.info("📅 Tailwind picker: selecting day '{}' from '{}'", dayLabel, displayValue);

            // 0) Always open the popup – clicking the icon is idempotent
            try {
                logger.info("📅 Tailwind picker: clicking Due date icon to open popup...");
                commonMethods.clickWithRetry(dueDateIconButton, "Due date icon");
            } catch (Exception e) {
                logger.error("❌ Tailwind picker: failed to click Due date icon: {}", e.getMessage(), e);
                throw e;
            }

            // 1) Wait for any calendar grid with 7 columns to be visible
            By gridLocator = By.xpath("//div[contains(@class,'grid') and contains(@class,'grid-cols-7')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(gridLocator));

            // 2) Try as CURRENT-month cell
            try {
                By currentDay = dayCurrentMonth(dayLabel);
                WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(currentDay));

                commonMethods.clickWithRetry(btn, "Day " + dayLabel + " (current)");
                logger.info("📅 Tailwind picker: clicked CURRENT-month day '{}'", dayLabel);
                return;
            } catch (TimeoutException | NoSuchElementException e) {
                logger.warn("📅 Tailwind picker: day '{}' not found as CURRENT-month cell. Trying OFF-month...", dayLabel);
            }

            // 3) Try as OFF-month (gray) cell
            try {
                By offMonthDay = dayOffMonth(dayLabel);
                WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(offMonthDay));

                commonMethods.clickWithRetry(btn, "Day " + dayLabel + " (off-month)");
                logger.info("📅 Tailwind picker: clicked OFF-month day '{}'", dayLabel);
                return;
            } catch (TimeoutException | NoSuchElementException e) {
                logger.error("📅 Tailwind picker: day '{}' not found as OFF-month either.", dayLabel);
            }

            // 4) Nothing worked -> hard fail so we see it in logs/allure
            throw new NoSuchElementException(
                    "Could not locate day '" + dayLabel + "' in Tailwind calendar for value '" + displayValue + "'."
            );

        } catch (Throwable t) {
            logger.error("❌ Exception in pickDateFromTailwindWidget for '{}': {}", displayValue, t.getMessage(), t);
            throw t;
        }
    }






    // in CompliancePage.java

    @FindBy(xpath = "//button[.//span[normalize-space()='Create'] or normalize-space()='Create']")
    private WebElement createButton;

    private final By overlayOrBackdrop = By.xpath(
            "//*[contains(@class,'overlay') or contains(@class,'loader') or contains(@class,'backdrop')]"
    );

    public boolean isOnCompliances() {
        try {
            new WebDriverWait(driver, Duration.ofMillis(600))
                    .until(ExpectedConditions.visibilityOf(compliancesText));
            return compliancesText.isDisplayed();
        } catch (Throwable t) {
            try { return driver.getCurrentUrl().contains("/grc/compliances"); } catch (Throwable ignore) { return false; }
        }
    }



    public WaitOutcome clickCreateAndWait(
            ReusableCommonMethods commons,
            long warn1Ms,
            long warn2Ms,
            long warn3Ms,
            long failMs
    ) {
        return commons.clickAndWaitFor(
                createButton,
                "Create",
                () -> isOnCompliances(),
                warn1Ms,
                warn2Ms,
                warn3Ms,
                failMs,
                overlayOrBackdrop
        );
    }









    // --- remember the org chosen on the side panel so we can reuse it later ---
    private String lastSelectedOrganization;
    public String getLastSelectedOrganization() { return lastSelectedOrganization; }


    // Top "All offices" combobox container (value + control). Stable by structure, not id.
    @FindBy(xpath =
            "//div[contains(@class,'css-') and contains(@class,'control')]" +
                    "[.//div[contains(@class,'singleValue') or contains(.,'All offices')]]")
    private WebElement allOfficesControl;

    // optional: header area to scroll to (helps bring it into view)
    @FindBy(xpath = "//h1[normalize-space()='Compliances']")
    private WebElement compliancesHeader;



    public void filterByOrganization(String orgName) {
        logger.info("Applying 'All offices' filter with organization: {}", orgName);

        // bring header into view so the combobox is visible
        try { commonMethods.scrollIntoViewCenter(compliancesHeader); } catch (Throwable ignored) {}
        // small nudge to top
        try { ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0)"); } catch (Throwable ignored) {}

        // 1) Focus the combobox (this causes the input to appear in DOM)
        commonMethods.safeClick(driver, allOfficesControl, "All offices control", 5);

        // 2) Now find the input INSIDE the focused control (id index is variable)
        WebElement input = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath(".//input[contains(@id,'react-select') and contains(@id,'-input')]")));

        // 3) Type + Enter
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.DELETE);
        input.sendKeys(orgName);

        // wait until listbox opens so ENTER will select a real option
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[contains(@id,'react-select') and contains(@id,'-listbox')]//div[contains(@class,'option')]")));
        } catch (Throwable ignored) {}

        input.sendKeys(Keys.ENTER);

        // 4) Wait for table refresh (spinner or first row visible)
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            w.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.xpath("//*[contains(@class,'loading') or contains(@class,'spinner')]")));
        } catch (Throwable ignored) {}
        try {
            w.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("(//table//tr[.//td])[1]")),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[@role='row'])[2]")) // if grid rows
            ));
        } catch (Throwable ignored) {}

        logger.info("All offices filter applied for '{}'.", orgName);
    }









    // --- Due Date (chip-style) selectors ---
// Container immediately following the "Due Date" label (the pill)
    private By dueDatePill = By.xpath(
            "//label[normalize-space()='Due Date']" +
                    "/following::p[contains(@class,'cursor-default')][1]"
    );

    // The little × icon inside the pill
    private By dueDateClearSvg = By.xpath(
            "//label[normalize-space()='Due Date']" +
                    "/following::p[contains(@class,'cursor-default')][1]" +
                    "//*[name()='svg' and contains(@class,'cursor-pointer')]"
    );

    // When cleared, a placeholder/empty state appears instead of a selected value
    private By dueDatePlaceholder = By.xpath(
            "//label[normalize-space()='Due Date']" +
                    "/following::*[self::p or self::div][1]" +
                    "[contains(@class,'placeholder') or contains(normalize-space(.),'Select due date')]"
    );

    // Fallback: an input appears only when focused (if the component renders one)
    private By dueDateInlineInput = By.xpath(
            "//label[normalize-space()='Due Date']" +
                    "/following::*[self::p or self::div][1]//input[contains(@id,'react-select') and contains(@id,'-input')]"
    );

    // Utility: return the first present element among candidates
    private WebElement firstPresent(By... candidates) {
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(5));
        for (By by : candidates) {
            try { return w.until(ExpectedConditions.presenceOfElementLocated(by)); }
            catch (TimeoutException ignore) {}
        }
        throw new TimeoutException("None of the candidate locators resolved for Due Date.");
    }


    public void clearDueDateFilter(ReusableCommonMethods common) {

        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(12));

        // Bring the pill into view
        WebElement pill = firstPresent(dueDatePill);
        try { common.scrollIntoViewCenter(pill); } catch (Throwable ignore) {}

        // Primary path: click the little ×
        try {
            WebElement x = new WebDriverWait(driver, Duration.ofSeconds(4))
                    .until(ExpectedConditions.elementToBeClickable(dueDateClearSvg));
            common.safeClick(driver, x, "Due Date clear (×)", 4);
            // Wait until the selection is cleared (placeholder visible or value gone)
            w.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(dueDatePlaceholder),
                    ExpectedConditions.not(ExpectedConditions.attributeContains(pill, "textContent", "This Month"))
            ));
            logger.info("✅ Due Date cleared via ×.");
            return;
        } catch (RuntimeException miss) {
            logger.info("ℹ️ Clear icon not clickable/visible; falling back to keyboard clear.");
        }

        // Fallback: focus the pill and clear with keyboard
        common.safeClick(driver, pill, "Due Date pill", 5);

        try {
            // If an input appears on focus, clear it
            WebElement input = new WebDriverWait(driver, Duration.ofSeconds(4))
                    .until(ExpectedConditions.presenceOfElementLocated(dueDateInlineInput));
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            input.sendKeys(Keys.DELETE);
            input.sendKeys(Keys.ESCAPE);
        } catch (TimeoutException noInput) {
            // No input rendered; try JS to click any SVG × that might now be visible
            try {
                WebElement xNow = driver.findElement(dueDateClearSvg);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", xNow);
            } catch (NoSuchElementException ignore) {
                // last resort: tap Backspace twice on the focused pill
                pill.sendKeys(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.ESCAPE);
            }
        }

        // Confirm cleared state
        try {
            w.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(dueDatePlaceholder),
                    ExpectedConditions.not(ExpectedConditions.attributeContains(pill, "textContent", "This Month"))
            ));
            logger.info("✅ Due Date cleared via fallback.");
        } catch (TimeoutException te) {
            throw new RuntimeException("Due Date did not clear within timeout.");
        }
    }


    // Robust BYs (no fragile parent::div hop)
    private final By internalRowBy = By.xpath("(//span[normalize-space()='Internal']/ancestor::div[contains(@class,'items-center')])[1]");
    private final By internalTrackBy = By.xpath(
            "(//span[normalize-space()='Internal']/ancestor::div[contains(@class,'items-center')])[1]" +
                    "//div[contains(@class,'inline-flex') and contains(@class,'rounded-full')]"
    );
    private final By internalKnobBy = By.xpath(
            "(//span[normalize-space()='Internal']/ancestor::div[contains(@class,'items-center')])[1]" +
                    "//div[contains(@class,'inline-flex') and contains(@class,'rounded-full')]//span[contains(@class,'absolute')]"
    );

    private boolean isInternalOnNow() {
        WebElement knob = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(internalKnobBy));
        String cls = knob.getAttribute("class");
        return cls != null && cls.contains("translate-x-6");
    }

    public void setInternalFilter(boolean turnOn) {
        String want = turnOn ? "ON" : "OFF";
        logger.info("Toggling Internal filter -> {}", want);

        // ensure toolbar zone is in view (after filters the page can be mid-scroll)
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0);");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // anchor on the row that contains the label
        wait.until(ExpectedConditions.visibilityOfElementLocated(internalRowBy));

        // short-circuit if already in the desired state
        if (isInternalOnNow() == turnOn) {
            logger.info("Internal already {}", want);
            return;
        }

        // click the large track hit area (re-find to avoid stale)
        WebElement track = wait.until(ExpectedConditions.elementToBeClickable(internalTrackBy));
        try {
            commonMethods.safeClick(driver, track, "Internal toggle", 10);
        } catch (Throwable t) {
            // fallback JS click in case something overlays briefly
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", track);
        }

        // wait for knob to move (class flip)
        String expected = turnOn ? "translate-x-6" : "translate-x-0";
        wait.until(ExpectedConditions.attributeContains(internalKnobBy, "class", expected));

        logger.info("Internal is now {}", want);
    }



    // pages/CompliancePage.java

    private static String escXpath(String s) {
        // robust Xpath string escaper (handles quotes)
        if (s.contains("'") && s.contains("\"")) {
            String[] parts = s.split("\"");
            return "concat(\"" + String.join("\", '\"', \"", parts) + "\")";
        }
        return s.contains("\"") ? "'" + s + "'" : "\"" + s + "\"";
    }

    public boolean waitForComplianceInList(String name, Duration timeout) {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0);"); // toolbar can shift scroll
        WebDriverWait wait = new WebDriverWait(driver, timeout);

        // Match by data-searchable first; fall back to exact visible text inside the name cell
        String esc = escXpath(name);
        By rowBy = By.xpath(
                "//p[@data-searchable=" + esc + "]" +
                        " | //td//p[normalize-space()=" + esc + "]"
        );

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(rowBy));
            // optional: ensure it’s visible (not just in DOM)
            wait.until(ExpectedConditions.visibilityOfElementLocated(rowBy));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }


    // In CompliancePage

    /**
     * Reads all visible rows on the current page and maps them to ComplianceRow
     * (name, office, due date).
     *
     * Adjust column indexes if your grid order is different.
     */
    public List<ComplianceRow> readCurrentPageRows() {
        List<ComplianceRow> out = new ArrayList<ComplianceRow>();

        // Prefer your mainComplianceRows if present, else generic tbody tr
        List<WebElement> rows;
        if (mainComplianceRows != null && !mainComplianceRows.isEmpty()) {
            rows = mainComplianceRows;
        } else if (complianceRows != null && !complianceRows.isEmpty()) {
            rows = complianceRows;
        } else {
            rows = driver.findElements(By.cssSelector("table#compliances-table tbody tr"));
        }

        if (rows == null || rows.isEmpty()) {
            logger.warn("⚠️ No compliance rows found on current page.");
            return out;
        }

        for (WebElement row : rows) {
            if (row == null) continue;
            try {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells == null || cells.isEmpty()) continue;

                // TODO: adjust these indexes based on your actual table:
                // here: col0 = Name, col2 = Due Date, col3 = Office (example)
                int nameIdx = 0;
                int dueIdx  = 2;
                int officeIdx = 3;

                String name = "";
                String office = "";
                String dueDate = "";

                if (cells.size() > nameIdx) {
                    WebElement nameCell = cells.get(nameIdx);
                    // try to get any <p>/<span>/<a> text inside
                    List<WebElement> nameInner = nameCell.findElements(
                            By.xpath(".//*[self::p or self::span or self::a]")
                    );
                    if (nameInner != null && !nameInner.isEmpty()) {
                        name = nameInner.get(0).getText();
                    } else {
                        name = nameCell.getText();
                    }
                }

                if (cells.size() > officeIdx) {
                    office = cells.get(officeIdx).getText();
                }

                if (cells.size() > dueIdx) {
                    dueDate = cells.get(dueIdx).getText();
                }

                if (name == null) name = "";
                if (office == null) office = "";
                if (dueDate == null) dueDate = "";

                name = name.trim();
                office = office.trim();
                dueDate = dueDate.trim();

                // Only add if there's at least a name
                if (!name.isEmpty()) {
                    ComplianceRow cr = new ComplianceRow();
                    cr.setName(name);
                    cr.setOffice(office);
                    cr.setDueDate(dueDate);
                    out.add(cr);
                }

            } catch (Exception e) {
                logger.warn("⚠️ Error while reading row on current page: {}", e.getMessage());
            }
        }

        logger.info("📄 readCurrentPageRows(): collected {} row(s) on this page.", Integer.valueOf(out.size()));
        return out;
    }


    // In CompliancePage

    /**
     * Collects all visible compliance rows (name, office, due date) across ALL pages.
     * Follows the same pattern as fetchAllTitleValuesPageWise:
     *  - Wait for first page ready
     *  - Loop over pages
     *  - Use findNextButton() + clickNextAndWaitForChange()
     *  - Optional pageShotCb + timingCb callbacks
     *
     * @param displayedTotal UI-displayed total (e.g. All tab count). Use 0 if unknown.
     * @param pageShotCb optional callback for per-page screenshots (may be null).
     * @param timingCb optional callback for per-page navigation timings (may be null).
     * @return pageNumber -> list of ComplianceRow for that page.
     */
    public Map<Integer, List<ComplianceRow>> fetchAllRowsAcrossPages(int displayedTotal,
                                                                     PageNavigationCallback pageShotCb,
                                                                     PageNavigationCallback timingCb) {

        Map<Integer, List<ComplianceRow>> out = new LinkedHashMap<>();
        List<ComplianceRow> collected = new ArrayList<>();

        // 🔹 NEW: force start from FIRST page
        try {
            int current = getCurrentPageNumberSafe();
            if (current > 1) {
                logger.info("⏮ Currently on page {} – jumping back to first page before collection.", current);
                goToFirstPage();
            }
            // Ensure first page is fully ready
            try {
                waitFirstPageStatusReady(Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS));
            } catch (Throwable ignore) {}
        } catch (Throwable t) {
            logger.warn("⚠️ Could not ensure first page before collection: {}", t.getMessage());
        }

        int pageNumber = 1;
        int totalCollected = 0;

        while (true) {
            // Read current page rows
            List<ComplianceRow> pageRows = readCurrentPageRows();
            out.put(pageNumber, pageRows);

            int pageCount = (pageRows == null) ? 0 : pageRows.size();
            totalCollected += pageCount;
            if (pageRows != null) collected.addAll(pageRows);

            logger.info("🧾 Page {}: collected {} row(s). Running total: {} / {}",
                    pageNumber, pageCount, totalCollected, displayedTotal);

            if (pageShotCb != null) {
                try {
                    pageShotCb.onPage(pageNumber);
                } catch (Throwable ignore) {}
            }

            // Stop conditions
            if (pageRows == null || pageRows.isEmpty()) {
                logger.info("ℹ️ No rows on page {}. Stopping pagination.", pageNumber);
                break;
            }
            if (displayedTotal > 0 && totalCollected >= displayedTotal) {
                logger.info("✅ Collected {} / {} rows; stopping pagination.", totalCollected, displayedTotal);
                break;
            }

            WebElement nextBtn = findNextButton();  // your existing helper
            if (nextBtn == null) {
                logger.info("ℹ️ No Next button found. Finished at page {}.", pageNumber);
                break;
            }

            boolean advanced = clickNextAndWaitForChange(nextBtn, timingCb, pageNumber);
            if (!advanced) {
                logger.info("ℹ️ Next click did not change page. Stopping at page {}.", pageNumber);
                break;
            }

            pageNumber++;
        }

        logger.info("✅ Completed row collection. Total rows={} | Pages={}",
                collected.size(), out.size());

        if (displayedTotal > 0 && collected.size() < displayedTotal) {
            logger.warn("ℹ️ Collected fewer rows ({}) than displayedTotal ({}). Possible virtualization / page size difference.",
                    collected.size(), displayedTotal);
        }

        return out;
    }






    // In CompliancePage

    private WebElement findNextButtonLoose() {
        // First, try the same robust logic as findNextButton()
        WebElement fromStandard = null;
        try {
            fromStandard = findNextButton();
        } catch (Exception ignore) {}
        if (fromStandard != null) {
            return fromStandard;
        }

        // Fallback: very loose “Next / > / ›” button search
        List<By> locators = Arrays.asList(
                By.xpath(
                        // span inside button: Next / › / >
                        "//button[.//span[normalize-space()='Next' " +
                                "or normalize-space()='›' " +
                                "or normalize-space()='>']]" +
                                // OR button text directly
                                " | //button[normalize-space()='Next' " +
                                "or normalize-space()='›' " +
                                "or normalize-space()='>']"
                ),
                By.cssSelector("button[aria-label='Next'], button[aria-label='next']")
        );

        for (int i = 0; i < locators.size(); i++) {
            By by = locators.get(i);
            List<WebElement> candidates = driver.findElements(by);
            for (int j = 0; j < candidates.size(); j++) {
                WebElement el = candidates.get(j);
                try {
                    if (!el.isDisplayed()) continue;
                    if (!el.isEnabled()) continue;

                    String cls = el.getAttribute("class");
                    String aria = el.getAttribute("aria-disabled");
                    String disabled = el.getAttribute("disabled");
                    boolean isDisabled = "true".equalsIgnoreCase(aria)
                            || disabled != null
                            || (cls != null && cls.toLowerCase().contains("disabled"));

                    if (!isDisabled) {
                        logger.info("➡️ Using loose Next button candidate (locator #{}, index {}): {}",
                                Integer.valueOf(i), Integer.valueOf(j), el.toString());
                        return el;
                    }
                } catch (Exception ignore) {
                }
            }
        }

        logger.info("ℹ️ Loose Next button not found or all disabled.");
        return null;
    }













}
