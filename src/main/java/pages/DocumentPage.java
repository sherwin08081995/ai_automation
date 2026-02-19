package pages;


import base.BasePage;
import io.qameta.allure.Allure;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.TestDataGenerator;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.AllureLoggerUtils.logToAllure;

/**
 * @author Sherwin
 * @since 10-09-2025
 */

public class DocumentPage extends BasePage {

    public DocumentPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//span[normalize-space()='Documents']")
    private WebElement documentsTab;

    @FindBy(xpath = "//p[normalize-space()='Select a folder to view your compliance documents, bills, and related files']")
    private WebElement documentsText;

    @FindBy(xpath = "//span[normalize-space()='Documents' and (ancestor::button[@aria-selected='true'] or contains(@class,'active'))]")
    private WebElement documentsTabActiveIndicator;

    @FindBy(xpath = "//div[contains(@class,'overflow-x-auto') and contains(@class,'hideScrollbar')]")
    private WebElement sectionsStrip;

    @FindBy(xpath = "//div[contains(@class,'overflow-x-auto')]//div[contains(@class,'cursor-pointer')]//p[normalize-space()]")
    private List<WebElement> sectionLabels;

    @FindBy(xpath = "//div[contains(@class,'overflow-x-auto')]//p[normalize-space()='Documents']")
    private WebElement documentsSectionLabel;

    @FindBy(xpath = "//div[contains(@class,'overflow-x-auto')]//div[contains(@class,'cursor-pointer')][.//p[normalize-space()='Documents']]")
    private WebElement documentsSectionContainer;

    @FindBy(xpath = "//p[normalize-space()='Legal doc generator']")
    private WebElement legalDocGeneratorTab;

    @FindBy(xpath = "//div[contains(@class,'grid') and contains(@class,'overflow-auto')]//div[contains(@class,'bg-white') and contains(@class,'rounded')]")
    private List<WebElement> widgetCards;

    @FindBy(xpath = "//button[.//p[normalize-space()='Create document']]")
    private List<WebElement> createButtons;

    @FindBy(xpath = "//div[contains(@class,'grid') and contains(@class,'overflow-auto')]//h3")
    private List<WebElement> documentTitles;

    @FindBy(css = "strong, h1, h2, .form-title, .preview-pane h1, .preview-pane h2")
    private List<WebElement> formHeaders;

    @FindBy(css = "input[type='text'], input[type='email'], input[type='tel'], input[type='number'], " + "input:not([type]), textarea, [contenteditable='true']")
    private List<WebElement> textControls;

    @FindBy(xpath = "(//p[normalize-space()='Next']//following::img[@alt='icon'])[1]")
    private List<WebElement> nextButtons;

    @FindBy(xpath = "(//p[normalize-space()='Submit']//following::img[@alt='icon'])[1]")
    private List<WebElement> submitPs;

    @FindBy(xpath = "//div[@class='flex gap-2 items-center']//p[normalize-space()='Documents']")
    private WebElement documentsLink;

    @FindBy(xpath = "//p[normalize-space()='My Documents' or normalize-space()='My documents']")
    private WebElement myDocumentsTabOrTile;

    @FindBy(xpath = "//p[normalize-space()='My documents']")
    private WebElement myDocumentsHeader;

    @FindBy(xpath = "//p[normalize-space()='New']")
    private WebElement plusNewButton;

    @FindBy(css = "input[placeholder*='Search in'][placeholder*='Legal']")
    private WebElement documentsSearchInput;

    @FindBy(xpath = "//div[contains(@class,'grid') and contains(@class,'overflow-auto')]//div[.//img[contains(@alt,'PDF')]]//div|//div[contains(@class,'grid') and contains(@class,'overflow-auto')]//p")
    private List<WebElement> gridTextNodes;

    @FindBy(xpath = "//p[normalize-space()='Add new folder']")
    private WebElement addNewFolderOption;

    @FindBy(xpath = "//p[normalize-space()='Upload files']")
    private WebElement uploadFilesOption;

    private final By submitBy = By.xpath("(//button[.//span[normalize-space()='Submit']] | //*[contains(@class,'cursor-pointer')])[1]");
    private final By documentsHintBy = By.xpath("//*[contains(normalize-space(.),'Click on a file to view it')]");

    private final By safeNextButtonBy = By.xpath("//button[not(@disabled) and (" + "contains(normalize-space(.), 'Next') or .//span[contains(normalize-space(.), 'Next')]" + ")]" + "[not(contains(normalize-space(.), 'Back')) and " + "not(.//span[contains(normalize-space(.), 'Back')])]");

//    public static final By MODAL_ROOT = By.xpath("//*[contains(@class,'fixed') and contains(@class,'inset-0')]" + "//*[contains(@class,'rounded') or contains(@class,'bg-white') or contains(@class,'white')][1]");
//
//    public static final By MODAL_INPUT = By.xpath("//*[contains(@class,'fixed') and contains(@class,'inset-0')]//input[@type='text' and not(@disabled)]");
//
//    public static final By MODAL_CREATE = By.xpath("//p[normalize-space()='Create']");

    // Centered "My documents" create-folder dialog

    // Root of "My documents" create-folder modal
    private static final By MODAL_ROOT = By.xpath("(//*[self::div or self::section][.//button[.//text()[normalize-space()='Create']]])[6]");

    // The text input inside the modal
    private static final By MODAL_INPUT = By.xpath(
            "(//*[.//h2[normalize-space()='My documents']]//input[@type='text'])[2]"
    );

    // The "Create" button inside the modal
    private static final By MODAL_CREATE = By.xpath(
            "//*[.//h2[normalize-space()='My documents']]//button[.//text()[normalize-space()='Create']]"
    );



    @FindBy(xpath = "//input[@type='text' and @placeholder='Enter undefined']")
    private WebElement folderNameInput;

    @FindBy(xpath = "//p[normalize-space()='Create']")
    private WebElement createFolderButton;


    private static final By SR_TABLE_FIRST_COL = By.xpath("//*[normalize-space()='Search results']/following::*[self::table][1]" + "//tbody/tr/td[1]//*[self::a or self::span or self::div]");

    private static final By DEFAULT_LIST_ROWS = By.xpath("//div[contains(@class,'grid') or contains(@class,'table')]//*[self::a or self::span or self::div]" + "[contains(translate(., 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), '.PDF') or " + " contains(@href,'.pdf') or contains(@title,'.pdf')]");

    private static final By FILE_NAME_CELLS_UNION = By.xpath("(" + SR_TABLE_FIRST_COL.toString().replace("By.xpath: ", "") + ") | (" + DEFAULT_LIST_ROWS.toString().replace("By.xpath: ", "") + ")");

    private static final By NO_RESULTS = By.xpath("//*[normalize-space()='No results found']");


    private static final By PREVIEW_MODAL = By.xpath("//div[contains(@class,'styles_modal_') or contains(@class,'styles_container_')]" + "[.//p[contains(translate(normalize-space(.),'pdf','PDF'),'.PDF')]]");

    private static final By PREVIEW_FILENAME = By.xpath(".//p[contains(translate(normalize-space(.),'pdf','PDF'),'.PDF')]");

    private static final By PREVIEW_DOWNLOAD_BTN = By.xpath(".//*[self::button or self::div][@class][contains(@class,'cursor-pointer')]" + "[.//svg[contains(@class,'size-7') or @viewBox='0 0 24 24']]" + "[not(.//path[contains(@d,'M6 18L18 6')])]");

    // If there’s an explicit data-testid, keep a fallback:
    private static final By PREVIEW_DOWNLOAD_ANY = By.xpath(".//*[self::button or self::a or self::div][@download or @href[contains(.,'.pdf')] or @data-testid='download']");


    private static final By UPLOAD_MODAL = By.xpath("//*[contains(@class,'fixed') and contains(@class,'inset-0')]");

    private static final By UPLOAD_FILES_CTA = By.xpath("//button[.//p[normalize-space()='Upload files']]");

    private static final By DROPZONE_CARD = By.xpath("//p[normalize-space()='Drop a file here or click to browse']");


    /**
     * Clicks the "Documents" tab using a robust safe-click routine and emits detailed logs.
     * <p>
     * This method delegates the actual click to {@code safeClick(WebDriver, WebElement, String, int)},
     * which handles visibility, clickability, scrolling into view, overlap checks, and multiple click
     * fallbacks (normal, Actions, JS). If the element goes stale once, the method rebinds PageFactory
     * elements and retries a single time.
     * </p>
     *
     * @throws IllegalStateException if the {@code documentsTab} element is not initialized
     * @throws TimeoutException      if waiting/clicking ultimately times out inside {@code safeClick}
     * @throws RuntimeException      if {@code safeClick} exhausts all attempts or any unrecoverable error occurs
     */
    public void clickDocumentsTab() {
        logger.info("➡️ Clicking 'Documents' tab...");
        if (documentsTab == null) {
            logger.error("❌ 'Documents' tab WebElement is null. Was PageFactory.initElements(...) called?");
            throw new IllegalStateException("documentsTab not initialized");
        }
        try {
            // Uses your existing helper
            commonMethods.safeClick(driver, documentsTab, "Documents tab", 15);
            logger.info("✅ 'Documents' tab clicked.");
        } catch (StaleElementReferenceException sere) {
            logger.warn("♻️ 'Documents' tab went stale; re-binding elements and retrying once.");
            try {
                org.openqa.selenium.support.PageFactory.initElements(driver, this);
                commonMethods.safeClick(driver, documentsTab, "Documents tab (retry after stale)", 15);
                logger.info("✅ 'Documents' tab clicked after rebind.");
            } catch (Throwable t2) {
                logger.error("❌ Failed to click 'Documents' tab after stale retry: {}", t2.toString(), t2);
                throw t2;
            }
        } catch (TimeoutException te) {
            logger.error("❌ Timeout while clicking 'Documents' tab: {}", te.toString(), te);
            throw te;
        } catch (Throwable t) {
            logger.error("❌ Unexpected error while clicking 'Documents' tab: {}", t.toString(), t);
            throw t;
        }
    }

    /**
     * Waits for the Documents page to be considered "loaded" by verifying the confirmation text element.
     * <p>
     * This method waits for the confirmation element to become visible and then validates its text content.
     * It logs the actual text found. A strict exact-match is preferred; a secondary lenient check accepts
     * containment of a key phrase with a warning (useful when minor copy changes occur).
     * </p>
     *
     * <h4>Validation rules</h4>
     * <ul>
     *   <li>Element must be visible and displayed.</li>
     *   <li>Exact match to the expected confirmation string → <b>success</b>.</li>
     *   <li>Contains the key phrase "Select a folder" → <b>success with warning</b>.</li>
     *   <li>Anything else → <b>failure</b>.</li>
     * </ul>
     *
     * @param timeout maximum time to wait for the confirmation element to become visible
     * @return {@code true} if the page validation passes (exact match or containment), otherwise {@code false}
     */
    public boolean waitForDocumentsPageToLoad(Duration timeout) {
        final String EXPECTED_TEXT = "Select a folder to view your compliance documents, bills, and related files";

        logger.info("⏳ Waiting for Documents page confirmation (timeout={}s)...", timeout.toSeconds());

        WebDriverWait shortWait = new WebDriverWait(driver, timeout);
        shortWait.ignoring(StaleElementReferenceException.class);

        try {
            // Visibility is the primary readiness signal
            shortWait.until(ExpectedConditions.visibilityOf(documentsText));

            // Basic display check
            if (!documentsText.isDisplayed()) {
                logger.error("❌ Documents confirmation element located but not displayed.");
                return false;
            }

            // Read and validate text
            String actual = "";
            try {
                actual = (documentsText.getText() == null) ? "" : documentsText.getText().trim();
                if (actual.isEmpty()) {
                    logger.warn("⚠️ Documents confirmation text is empty.");
                } else {
                    logger.info("ℹ️ Documents confirmation text: '{}'", actual);
                }
            } catch (Throwable t) {
                logger.warn("⚠️ Unable to read Documents confirmation text: {}", t.toString());
            }

            // Strict match first
            if (EXPECTED_TEXT.equals(actual)) {
                logger.info("✅ Documents confirmation text EXACT match.");
                return true;
            }

            // Lenient containment as a fallback (warn if not exact)
            if (actual.contains("Select a folder")) {
                logger.warn("⚠️ Documents confirmation text contains expected phrase but is not an exact match. " + "expected='{}' | actual='{}'", EXPECTED_TEXT, actual);
                return true; // treat as success but warn
            }

            logger.error("❌ Documents confirmation text mismatch. expected='{}' | actual='{}'", EXPECTED_TEXT, actual);
            return false;

        } catch (TimeoutException te) {
            logger.error("❌ Timed out waiting for Documents confirmation text: {}", te.toString(), te);
            return false;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            logger.error("❌ Element issue while validating Documents page: {}", e.toString(), e);
            return false;
        } catch (Throwable t) {
            logger.error("❌ Unexpected error while validating Documents page: {}", t.toString(), t);
            return false;
        }
    }


    /**
     * Returns all currently visible section labels (trimmed), as shown on the Documents page.
     * <p>Example output: ["Documents", "Legal doc generator"]</p>
     *
     * @return list of section names as displayed (trimmed); empty list if none found
     */
    public List<String> getVisibleSections() {
        List<String> out = new ArrayList<String>();
        try {
            if (sectionLabels != null) {
                for (int i = 0; i < sectionLabels.size(); i++) {
                    WebElement el = sectionLabels.get(i);
                    try {
                        if (el != null && el.isDisplayed()) {
                            String t = el.getText();
                            if (t != null) {
                                t = t.trim();
                                if (t.length() > 0) {
                                    out.add(t);
                                }
                            }
                        }
                    } catch (Throwable ignore) {
                        // skip individual failures
                    }
                }
            }
        } catch (Throwable t) {
            logger.warn("⚠️ Unable to gather visible sections: {}", t.toString());
        }
        return out;
    }

    /**
     * Waits until a section with the given label is visible (case-insensitive, space-normalized).
     * Uses an XPath that matches text case-insensitively via translate() and trims spaces.
     *
     * @param sectionLabel expected label, e.g. "Documents" or "Legal Doc generator"
     * @param timeoutSec   max time to wait in seconds
     * @return true if such a section becomes visible within timeout; false otherwise
     */
    public boolean waitForSectionVisible(String sectionLabel, int timeoutSec) {
        String expected = normalize(sectionLabel);
        logger.info("⏳ Waiting for section '{}'", expected);

        // Case-insensitive, normalized-space match
        String ciXpath = "//div[contains(@class,'overflow-x-auto')]//p[" + "translate(normalize-space(.)," + " 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')=" + " '" + expected.toLowerCase() + "']";

        try {
            WebElement el = wait.waitForVisibility(By.xpath(ciXpath));
            if (el != null && el.isDisplayed()) {
                logger.info("✅ Section '{}' is visible.", expected);
                return true;
            } else {
                logger.error("❌ Section '{}' located but not displayed.", expected);
                return false;
            }
        } catch (TimeoutException te) {
            logger.error("❌ Timed out waiting for section '{}'. {}", expected, te.toString());
            return false;
        } catch (NoSuchElementException e) {
            logger.error("❌ Section '{}' not found. {}", expected, e.toString());
            return false;
        } catch (StaleElementReferenceException e) {
            logger.error("❌ Stale element while waiting for section '{}'. {}", expected, e.toString());
            return false;
        } catch (Throwable t) {
            logger.error("❌ Unexpected error while waiting for section '{}': {}", expected, t.toString(), t);
            return false;
        }
    }

    /**
     * Utility: trims outer spaces and collapses internal multiple spaces to one.
     */
    public String normalize(String s) {
        if (s == null) return "";
        String t = s.trim();
        return t.replaceAll("\\s+", " ");
    }

    /**
     * Checks if a given section tab is marked as "active".
     * <p>
     * Heuristics (in order):
     * <ul>
     *   <li>If container (or an ancestor) has <code>aria-selected='true'</code> → <b>active</b>.</li>
     *   <li>If container class contains <code>border-transparent</code> → <b>inactive</b>.</li>
     *   <li>If container class suggests a visible border (e.g., contains <code>border-</code> color/utilities) → <b>active</b>.</li>
     *   <li>Fallback: check computed CSS for a non-zero, non-transparent bottom border → <b>active</b>.</li>
     * </ul>
     * The matching is case-insensitive on the text.
     * </p>
     *
     * @param sectionLabel expected section label (e.g., "Documents", "Legal Doc generator")
     * @param timeoutSec   wait timeout for the section label/container to become visible
     * @return {@code true} if the section is considered active; otherwise {@code false}
     */
    public boolean isSectionActive(String sectionLabel, int timeoutSec) {
        String expected = normalize(sectionLabel);
        logger.info("🔎 Checking if section '{}' is active (timeout={}s)...", expected, Integer.valueOf(timeoutSec));

        // Find the container of the tab by its <p> label (case-insensitive, space-normalized)
        String ciXpath = "//div[contains(@class,'overflow-x-auto')]//div[contains(@class,'cursor-pointer')]" + "[.//p[translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='" + expected.toLowerCase() + "']]";

        try {
            WebElement container = wait.waitForVisibility(By.xpath(ciXpath));

            // 1) aria-selected=true on self or ancestor
            try {
                String ariaSelected = container.getAttribute("aria-selected");
                if ("true".equalsIgnoreCase(ariaSelected)) {
                    logger.info("✅ '{}' is active (aria-selected=true on container).", expected);
                    return true;
                }
                // ancestor role=tab with aria-selected?
                WebElement maybeTab = container.findElement(By.xpath("ancestor-or-self::*[@role='tab' and @aria-selected='true']"));
                if (maybeTab != null) {
                    logger.info("✅ '{}' is active (ancestor role=tab aria-selected=true).", expected);
                    return true;
                }
            } catch (Throwable ignore) { /* continue heuristics */ }

            // 2) Class-based heuristics
            String klass = "";
            try {
                klass = container.getAttribute("class");
            } catch (Throwable t) { /* ignore */ }
            if (klass != null) {
                if (klass.toLowerCase().contains("border-transparent")) {
                    logger.warn("⚠️ '{}' container has 'border-transparent' → treating as INACTIVE.", expected);
                    return false;
                }
                // If we see an explicit border utility that is not transparent, treat as active.
                // (Covers Tailwind classes like border-[#0E225B], border-b-2, etc.)
                if (klass.toLowerCase().contains("border-") || klass.toLowerCase().contains("border-b-")) {
                    logger.info("✅ '{}' container class suggests a visible border → ACTIVE. class='{}'", expected, klass);
                    // fall through to CSS check as a best-effort confirmation (but we'll already return true if CSS agrees)
                }
            }

            // 3) Computed CSS border-bottom check (robust fallback)
            try {
                Object res = ((JavascriptExecutor) driver).executeScript("var el=arguments[0];" + "var s=window.getComputedStyle(el);" + "var bw=parseFloat(s.borderBottomWidth||'0');" + "var bc=(s.borderBottomColor||'').toString();" + "return {w:bw, c:bc};", container);

                if (res instanceof java.util.Map) {
                    java.util.Map map = (java.util.Map) res;
                    double w = 0.0;
                    String c = "";
                    try {
                        w = Double.parseDouble(String.valueOf(map.get("w")));
                    } catch (Throwable ignore) {
                    }
                    try {
                        c = String.valueOf(map.get("c"));
                    } catch (Throwable ignore) {
                    }

                    logger.info("ℹ️ '{}' computed border-bottom: width={} color={}", expected, Double.valueOf(w), c);
                    if (w > 0.0 && c != null && c.length() > 0 && !"rgba(0, 0, 0, 0)".equalsIgnoreCase(c) && !"transparent".equalsIgnoreCase(c)) {
                        logger.info("✅ '{}' is ACTIVE by computed border-bottom.", expected);
                        return true;
                    }
                }
            } catch (Throwable t) {
                logger.warn("⚠️ Could not read computed CSS for '{}': {}", expected, t.toString());
            }

            // If none of the heuristics proved active, assume inactive
            logger.error("❌ '{}' does not appear active by any heuristic.", expected);
            return false;

        } catch (TimeoutException te) {
            logger.error("❌ Timed out locating section container for '{}': {}", expected, te.toString());
            return false;
        } catch (NoSuchElementException e) {
            logger.error("❌ Section container not found for '{}': {}", expected, e.toString());
            return false;
        } catch (Throwable t) {
            logger.error("❌ Unexpected error while checking active section '{}': {}", expected, t.toString(), t);
            return false;
        }
    }


    /**
     * Returns the folder names currently visible in the Documents section.
     * <p>
     * Strategy:
     * <ul>
     *   <li>Collect visible {@code @title} from folder tiles (e.g., {@code <div title='Zolvit Documents'>})</li>
     *   <li>Also collect visible label {@code <p>} texts near folder tiles</li>
     *   <li>Trim and de-dupe results</li>
     * </ul>
     *
     * @return list of visible folder names (trimmed); may be empty if none are visible
     */
    public List<String> getVisibleFolderNames() {
        Set<String> out = new HashSet<String>();
        try {
            // 1) via @title
            List<WebElement> byTitle = driver.findElements(By.xpath("//div[@title and contains(@class,'cursor-pointer')]"));
            for (int i = 0; i < byTitle.size(); i++) {
                try {
                    WebElement el = byTitle.get(i);
                    if (el != null && el.isDisplayed()) {
                        String t = el.getAttribute("title");
                        if (t != null) {
                            t = t.trim();
                            if (t.length() > 0) out.add(t);
                        }
                    }
                } catch (Throwable ignore) {
                }
            }

            // 2) via <p> labels under/near the card
            List<WebElement> byP = driver.findElements(By.xpath("//div[contains(@class,'cursor-pointer')]//p[normalize-space()]"));
            for (int i = 0; i < byP.size(); i++) {
                try {
                    WebElement el = byP.get(i);
                    if (el != null && el.isDisplayed()) {
                        String t = el.getText();
                        if (t != null) {
                            t = t.trim();
                            if (t.length() > 0) out.add(t);
                        }
                    }
                } catch (Throwable ignore) {
                }
            }
        } catch (Throwable t) {
            logger.warn("⚠️ Unable to collect visible folder names: {}", t.toString());
        }

        // Convert to list
        return new ArrayList<String>(out);
    }

    /**
     * Waits for a folder card to be visible by its name (case-insensitive, whitespace-normalized).
     * <p>
     * Matches either the folder tile's {@code @title} or its visible label {@code <p>} text.
     * Uses an XPath with {@code translate()} for case-insensitive compare.
     * </p>
     *
     * @param folderName expected folder name (e.g., "Zolvit Documents")
     * @param timeoutSec timeout in seconds to wait for visibility
     * @return {@code true} if the folder is visible within timeout; otherwise {@code false}
     */
    public boolean waitForFolderVisible(String folderName, int timeoutSec) {
        String expected = normalize(folderName);
        logger.info("⏳ Waiting for folder '{}' in Documents section (timeout={}s)...", expected, Integer.valueOf(timeoutSec));

        // CI match against @title or <p> text
        String ciXpath = "(" + " //div[@title and contains(@class,'cursor-pointer')]" + "   [translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='" + expected.toLowerCase() + "']" + " |" + " //div[contains(@class,'cursor-pointer')]//p" + "   [translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='" + expected.toLowerCase() + "']" + ")";

        try {

            WebElement el = wait.waitForVisibility(By.xpath(ciXpath));

            // Scroll into view (best effort) for stable screenshots
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", el);
            } catch (Throwable ignore) {
            }

            boolean displayed = (el != null && el.isDisplayed());
            if (displayed) {
                logger.info("✅ Folder '{}' is visible.", expected);
            } else {
                logger.error("❌ Folder '{}' located but not displayed.", expected);
            }
            return displayed;

        } catch (TimeoutException te) {
            logger.error("❌ Timed out waiting for folder '{}': {}", expected, te.toString());
            return false;
        } catch (NoSuchElementException e) {
            logger.error("❌ Folder '{}' not found: {}", expected, e.toString());
            return false;
        } catch (StaleElementReferenceException e) {
            logger.error("❌ Stale element while locating folder '{}': {}", expected, e.toString());
            return false;
        } catch (Throwable t) {
            logger.error("❌ Unexpected error while locating folder '{}': {}", expected, t.toString(), t);
            return false;
        }
    }


    public boolean clickAndVerifyNavigation() {
        try {
            logger.info("➡️ Clicking on 'Legal doc generator' tab...");
            legalDocGeneratorTab.click();

            boolean headerDisplayed = false;
            boolean urlCorrect = false;

            try {
                headerDisplayed = wait.until(ExpectedConditions.visibilityOf(legalDocGeneratorTab)).isDisplayed();
                if (headerDisplayed) logger.info("✅ Header 'Legal doc generator' is displayed.");
            } catch (TimeoutException te) {
                logger.error("❌ Header 'Legal doc generator' not visible within timeout.", te);
            }

            try {
                urlCorrect = driver.getCurrentUrl().contains("/documents/documents-generator");
                if (urlCorrect) logger.info("✅ URL validation passed: " + driver.getCurrentUrl());
            } catch (Exception e) {
                logger.error("❌ Error while validating URL.", e);
            }

            if (headerDisplayed && urlCorrect) {
                logger.info("🎯 Navigation to Legal doc generator page verified successfully.");
                return true;
            } else {
                logger.error("❌ Navigation verification failed. headerDisplayed=" + headerDisplayed + ", urlCorrect=" + urlCorrect);
                return false;
            }

        } catch (Exception e) {
            logger.error("❌ Exception occurred while navigating to Legal doc generator page.", e);
            return false;
        }
    }


    public boolean clickAndVerifyMyDocumentsTab() {
        try {
            logger.info("➡️ Clicking on 'My documents'...");
            wait.until(ExpectedConditions.elementToBeClickable(myDocumentsTabOrTile)).click();

            boolean headerDisplayed = false;
            boolean urlCorrect = false;
            boolean newBtnVisible = false;

            // Wait for header
            try {
                headerDisplayed = wait.until(ExpectedConditions.visibilityOf(myDocumentsHeader)).isDisplayed();
                if (headerDisplayed) logger.info("✅ Header 'My documents' is displayed.");
            } catch (TimeoutException te) {
                logger.error("❌ Header 'My documents' not visible within timeout.", te);
            }

            // URL check (from your screenshots it looks like /grc/documents/my-documents)
            try {
                urlCorrect = wait.until(d -> d.getCurrentUrl().contains("/documents/my-documents"));
                if (urlCorrect) logger.info("✅ URL validation passed: " + driver.getCurrentUrl());
                else logger.warn("⚠️ URL validation failed. Current URL: " + driver.getCurrentUrl());
            } catch (Exception e) {
                logger.error("❌ Error while validating URL for My documents.", e);
            }

            // “+ New” button visible
            try {
                newBtnVisible = wait.until(ExpectedConditions.visibilityOf(plusNewButton)).isDisplayed();
                if (newBtnVisible) logger.info("✅ '+ New' button is visible.");
                else logger.warn("⚠️ '+ New' button not visible.");
            } catch (TimeoutException te) {
                logger.error("❌ '+ New' button not visible within timeout.", te);
            }

            boolean ok = headerDisplayed && urlCorrect && newBtnVisible;
            if (ok) {
                logger.info("🎯 Navigation to My documents verified successfully.");
            } else {
                logger.error("❌ My documents verification failed. headerDisplayed=" + headerDisplayed + ", urlCorrect=" + urlCorrect + ", newBtnVisible=" + newBtnVisible);
            }
            return ok;

        } catch (Exception e) {
            logger.error("❌ Exception occurred while navigating to My documents.", e);
            return false;
        }
    }


    public List<String> getAllDocumentTitles() {
        List<String> titles = new ArrayList<String>();
        if (documentTitles != null) {
            for (WebElement title : documentTitles) {
                try {
                    String text = title.getText().trim();
                    if (!text.isEmpty()) {
                        titles.add(text);
                    }
                } catch (Exception e) {
                    // Ignore broken elements
                }
            }
        }
        return titles;
    }


    public int getWidgetCount() {
        return widgetCards == null ? 0 : widgetCards.size();
    }

    public int getCreateButtonCount() {
        return createButtons == null ? 0 : createButtons.size();
    }

    public List<String> widgetsMissingCreateButton() {
        List<String> missing = new ArrayList<String>();
        if (widgetCards == null) return missing;

        for (int i = 0; i < widgetCards.size(); i++) {
            try {
                WebElement btn = widgetCards.get(i).findElement(By.xpath(".//button[.//p[normalize-space()='Create document']]"));
                if (!btn.isDisplayed()) {
                    missing.add("Widget#" + (i + 1) + " - button not visible");
                }
            } catch (NoSuchElementException nse) {
                missing.add("Widget#" + (i + 1) + " - button not found");
            }
        }
        return missing;
    }


    public WebElement getDocumentElementByIndex(int index) {
        if (documentTitles == null || documentTitles.isEmpty()) {
            throw new RuntimeException("No document elements found.");
        }
        return documentTitles.get(index);
    }

    /**
     * Returns true if any header element is visible on the form page
     */
    public boolean hasFormHeader() {
        if (formHeaders == null || formHeaders.isEmpty()) return false;
        try {
            for (int i = 0; i < formHeaders.size(); i++) {
                WebElement h = formHeaders.get(i);
                if (h != null && h.isDisplayed()) return true;
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    /**
     * Wait until either a header appears or URL suggests the generator
     */
    public void waitForFormReady(final String urlToken) {
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                String url = d.getCurrentUrl();
                return hasFormHeader() || (url != null && (url.contains(urlToken) || url.contains("/dox/generate-documents")));
            }
        });
    }

    /**
     * Robust: click the TITLE (<h3>) first, THEN wait for a new window, THEN switch, THEN wait for form
     */
    public void clickTitleAndOpenForm(int index, String visibleTitle, String urlToken) {
        String parent = driver.getWindowHandle();
        int beforeCount = driver.getWindowHandles().size();

        WebElement titleEl = getDocumentElementByIndex(index);

        // 1) Click the title using your safeClick
        commonMethods.safeClick(driver, titleEl, "Title: " + visibleTitle, 20);

        // 2) Wait for a new window to appear (because it opens only AFTER the click)
        try {
            // tune if needed
            wait.waitForNumberOfWindowsToBe(beforeCount + 1);
        } catch (org.openqa.selenium.TimeoutException te) {
            // If your app sometimes navigates same tab, you could allow that here:
            // driver.switchTo().window(parent);
            throw new RuntimeException("Expected a new window after clicking the title, but it did not appear.");
        }

        // 3) Switch to the newly opened window (the one that's not the parent)
        java.util.Set<String> handles = driver.getWindowHandles();
        java.util.Iterator<String> it = handles.iterator();
        while (it.hasNext()) {
            String h = it.next();
            if (!h.equals(parent)) {
                driver.switchTo().window(h);
                break;
            }
        }

        // 4) Now wait until the form is ready on the new tab
        waitForFormReady(urlToken);
    }

    /**
     * Prefer header text; fallback to ?doc_name=
     */
    public String getOpenedFormTitleSafe() {
        // header first
        if (formHeaders != null) {
            for (int i = 0; i < formHeaders.size(); i++) {
                try {
                    WebElement h = formHeaders.get(i);
                    if (h != null && h.isDisplayed()) {
                        String t = h.getText();
                        if (t != null) {
                            t = t.trim();
                            if (t.length() > 0) return t;
                        }
                    }
                } catch (Exception ignore) {
                }
            }
        }
        // fallback: URL param
        try {
            String url = driver.getCurrentUrl();
            String raw = getQueryParam(url, "doc_name");
            if (raw != null && raw.length() > 0) {
                String plusFixed = raw.replace('+', ' ');
                try {
                    String dec = java.net.URLDecoder.decode(plusFixed, "UTF-8");
                    if (dec != null) {
                        dec = dec.trim();
                        if (dec.length() > 0) return dec;
                    }
                } catch (Exception ignore) {
                    String best = plusFixed.trim();
                    if (best.length() > 0) return best;
                }
            }
        } catch (Exception ignore) {
        }
        return "";
    }

    private String getQueryParam(String url, String key) {
        if (url == null) return null;
        int q = url.indexOf('?');
        if (q == -1) return null;
        int hash = url.indexOf('#', q + 1);
        String query = (hash == -1) ? url.substring(q + 1) : url.substring(q + 1, hash);
        String[] pairs = query.split("&");
        for (int i = 0; i < pairs.length; i++) {
            String p = pairs[i];
            if (p.length() == 0) continue;
            int eq = p.indexOf('=');
            String k = (eq == -1) ? p : p.substring(0, eq);
            String v = (eq == -1) ? "" : p.substring(eq + 1);
            if (key.equals(k)) return v;
        }
        return null;
    }


    /**
     * Fill every section’s inputs with random text and click Next until Submit is visible.
     */
    public int fillFormUntilSubmitAppears() {
        int sections = 0;
        while (!isSubmitVisible()) {
            sections++;
            fillCurrentSection();
            WebElement next = getNextButton();
            if (next == null) break;
            commonMethods.safeClick(driver, next, "Next", 15);
            waitForSectionChange();
        }
        return sections;
    }


    /**
     * True if Submit is displayed.
     */
    public boolean isSubmitVisible() {
        return submitPs.stream().anyMatch(e -> e.isDisplayed() && e.isEnabled());
    }

    //    /** Click Submit. */
    public void clickSubmit() {
        if (!isSubmitVisible()) throw new RuntimeException("Submit not visible");
        WebElement clickable = submitPs.get(0);
        WebElement ancestor;
        try {
            ancestor = clickable.findElement(By.xpath("./ancestor::*[self::button or contains(@class,'cursor-pointer')][1]"));
        } catch (Exception ignore) {
            ancestor = clickable;
        }
        commonMethods.safeClick(driver, ancestor, "Submit", 15);
    }

    private void fillCurrentSection() {
        for (WebElement el : getVisibleControls()) {
            try {
                String value = "Auto_" + System.currentTimeMillis() % 100000;
                el.clear();
                el.sendKeys(value);
            } catch (Exception ignore) {
            }
        }
    }

    private List<WebElement> getVisibleControls() {
        List<WebElement> list = new ArrayList<>();
        for (WebElement el : textControls) {
            try {
                if (el.isDisplayed() && el.isEnabled()) list.add(el);
            } catch (Exception ignore) {
            }
        }
        return list;
    }

    private WebElement getNextButton() {
        return nextButtons.stream().filter(e -> e.isDisplayed() && e.isEnabled()).findFirst().orElse(null);
    }

    private void waitForSectionChange() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
    }


    /**
     * Wait until we're back on the Documents page and the hint is visible.
     */
    public boolean waitForDocumentsLanding(Duration timeout) {
        driver.switchTo().defaultContent();  // important if previous page used iframes

        WebDriverWait w = new WebDriverWait(driver, timeout);
        try {
            // either URL matches or the hint is present in the new DOM
            w.until(ExpectedConditions.or(ExpectedConditions.urlMatches(".*/documents/legal-documents.*"), ExpectedConditions.presenceOfElementLocated(documentsHintBy)));

            // ensure the hint is actually visible
            w.until(ExpectedConditions.visibilityOfElementLocated(documentsHintBy));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }


    /**
     * Safe getter: returns the hint text if visible, else empty string.
     */
    public String getDocumentsHintTextSafe() {
        try {
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement hint = w.until(ExpectedConditions.visibilityOfElementLocated(documentsHintBy));
            return hint.getText().trim();
        } catch (Exception ignore) {
            return "";
        }
    }


    // 2) type the search term here (so the step stays simple)
    private void typeInDocumentsSearch(String query) {
        WebElement searchBox = driver.findElement(By.xpath("//input[@placeholder='Search in \"Documents\"']"));
        searchBox.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        searchBox.sendKeys(Keys.DELETE);
        searchBox.sendKeys(query);
        searchBox.sendKeys(Keys.ENTER);
    }

    // 3) a robust wait that succeeds when *either* UI has something to read
    private void waitForResultsEitherUI(Duration timeout) {
        new WebDriverWait(driver, timeout).until(d -> {
            boolean hasTable = !d.findElements(SR_TABLE_FIRST_COL).isEmpty();
            boolean hasList = !d.findElements(DEFAULT_LIST_ROWS).isEmpty();
            boolean noRes = !d.findElements(NO_RESULTS).isEmpty();
            return hasTable || hasList || noRes;
        });
    }


    private static String safeGet(Supplier<String> s) {
        try {
            String v = s.get();
            return v == null ? "" : v;
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isGeneratedDocumentPresentUsingTitle(String searchText, Duration timeout) {
        // type normalized text into search box
        typeInDocumentsSearch(searchText);
        waitForResultsEitherUI(timeout);

        String expectedBase = canonicalBase(searchText);
        logger.info("Expect (base): {}", expectedBase);

        long end = System.currentTimeMillis() + timeout.toMillis();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement table = null;
        try {
            table = driver.findElement(By.cssSelector("#table-container, div[id^='table-container']"));
        } catch (Exception ignored) {
        }

        while (System.currentTimeMillis() < end) {
            // try inside table if present, else whole page
            List<WebElement> cells = (table != null ? table.findElements(FILE_NAME_CELLS_UNION) : driver.findElements(FILE_NAME_CELLS_UNION));

            // if nothing visible and UI says "no results", bail
            if (cells.isEmpty() && !driver.findElements(NO_RESULTS).isEmpty()) return false;

            for (WebElement c : cells) {
                String[] sources = {safeGet(c::getText), safeGet(() -> c.getAttribute("title")), safeGet(() -> c.getAttribute("href")), safeGet(() -> c.getAttribute("download")), safeGet(() -> String.valueOf(js.executeScript("return arguments[0].textContent;", c))), safeGet(() -> c.getAttribute("data-filename"))};

                for (String s : sources) {
                    if (s == null || s.isBlank()) continue;
                    String base = canonicalBase(s);
                    logger.info("Candidate raw='{}' | base='{}'", s, base);

                    // ✅ presence is enough
                    if (base.contains(expectedBase)) {
                        return true;
                    }
                }
            }

            // nudge virtualization
            try {
                if (table != null) js.executeScript("arguments[0].scrollBy(0, 600);", table);
                else js.executeScript("window.scrollBy(0, 800);");
            } catch (Exception ignored) {
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {
            }
        }
        return false;
    }

    public String canonicalBase(String raw) {
        if (raw == null) return "";
        // Unicode normalize & strip zero-widths/control chars
        String up = Normalizer.normalize(raw, Normalizer.Form.NFKC);
        up = INVISIBLES.matcher(up).replaceAll("");

        // uppercase for stable compare
        up = up.toUpperCase(Locale.ENGLISH);

        // unify whitespace & dashes -> underscore (keep explicit hyphens if doubled)
        // First collapse spaces
        up = up.replaceAll("\\s+", "_");
        // Then turn single legacy dashes surrounded by spaces into underscores
        up = up.replaceAll(" ?- ?", "_");

        // keep existing underscores/hyphens characters in names (like "PROTECTION-_LEGAL")
        // Remove extension
        up = up.replaceFirst("\\.PDF$", "");

        // strip trailing timestamp: _Sep_14_12_41_AM (or PM)
        up = up.replaceFirst("_[A-Z][a-z]{2}_\\d{2}_\\d{2}_\\d{2}_[AP]M$", "");

        // collapse multiple underscores
        up = up.replaceAll("_+", "_").replaceAll("^_|_$", "");
        return up;
    }


    // --- helpers ---
    private static final Pattern INVISIBLES = Pattern.compile("\\p{Cf}|\\p{Cc}");

    // Build what we actually type in the search box
    public static String buildNameQueryForSearch(String title) {
        if (title == null) return "";
        String up = title.toUpperCase(Locale.ENGLISH);

        // normalize zero-width & control chars
        up = INVISIBLES.matcher(up).replaceAll("");

        // normalize Unicode dashes to ASCII hyphen, and collapse whitespace to underscores
        up = up.replace('\u2013', '-')     // en dash
                .replace('\u2014', '-')     // em dash
                .replaceAll("\\s+", "_");   // spaces -> underscores

        // do NOT touch hyphens; they are significant (e.g., OFFER_OF-_EMPLOYMENT)

        // drop a trailing timestamp suffix if present (so search is just the base)
        up = up.replaceFirst("_[A-Z][a-z]{2}_\\d{2}_\\d{2}_\\d{2}_[AP]M$", "");

        // drop .pdf if someone passed a full filename
        up = up.replaceFirst("\\.PDF$", "");

        // collapse multiple underscores that might appear after cleanup
        up = up.replaceAll("_+", "_").replaceAll("^_|_$", "");
        return up;
    }

    // Open first matching result (by base name) from current results and wait for preview modal.
    public boolean openFirstMatchingSearchResultAndWait(String expectedBase, Duration timeout) {
        WebDriverWait w = new WebDriverWait(driver, timeout);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        List<WebElement> cells = driver.findElements(FILE_NAME_CELLS_UNION);
        if (cells.isEmpty() && !driver.findElements(NO_RESULTS).isEmpty()) return false;

        for (WebElement c : cells) {
            try {
                String raw = Optional.ofNullable(c.getAttribute("title")).orElseGet(() -> c.getText());
                if (raw == null || raw.isBlank()) {
                    raw = String.valueOf(js.executeScript("return arguments[0].textContent;", c));
                }
                String base = canonicalBase(raw);
                logger.info("📄 Result candidate raw='{}' | base='{}'", raw, base);

                if (base.contains(expectedBase)) {
                    commonMethods.safeClick(driver, c, "Search result: " + raw, 10);
                    // the app shows a PDF preview modal — wait for it
                    w.until(ExpectedConditions.visibilityOfElementLocated(PREVIEW_MODAL));
                    return true;
                }
            } catch (Throwable ignore) { /* try next */ }
        }
        return false;
    }

    // Read the filename shown in the preview modal
    public String getPreviewFilename() {
        try {
            WebElement modal = driver.findElement(PREVIEW_MODAL);
            WebElement nameEl = modal.findElement(PREVIEW_FILENAME);
            String t = nameEl.getText();
            return t == null ? "" : t.trim();
        } catch (Throwable t) {
            logger.warn("⚠️ Unable to read preview filename: {}", t.toString());
            return "";
        }
    }

    // Click the Download control in the preview modal (robust: primary locator then fallback)
    public void clickPreviewDownload() {
        WebElement modal = driver.findElement(PREVIEW_MODAL);
        List<By> tries = Arrays.asList(PREVIEW_DOWNLOAD_BTN, PREVIEW_DOWNLOAD_ANY);
        for (By by : tries) {
            List<WebElement> els = modal.findElements(by);
            for (WebElement el : els) {
                try {
                    if (el.isDisplayed() && el.isEnabled()) {
                        commonMethods.safeClick(driver, el, "Preview Download", 10);
                        return;
                    }
                } catch (Throwable ignore) {
                }
            }
        }
        throw new RuntimeException("Could not locate a visible Download control in preview modal.");
    }

    // Optional: close preview modal (if needed)
    public void closePreviewIfOpen() {
        try {
            WebElement modal = driver.findElement(PREVIEW_MODAL);
            // close by pressing ESC (many Tailwind modals close on ESC)
            modal.sendKeys(Keys.ESCAPE);
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.invisibilityOfElementLocated(PREVIEW_MODAL));
        } catch (Throwable ignore) {
        }
    }

    // Wait for a .pdf to appear in download folder whose base matches expected (ignoring timestamp suffix)
    public boolean waitForPdfDownloaded(Path downloadDir, String expectedBase, Duration timeout) {
        long end = System.currentTimeMillis() + timeout.toMillis();
        if (!java.nio.file.Files.isDirectory(downloadDir)) {
            logger.error("❌ Download directory does not exist: {}", downloadDir);
            return false;
        }
        while (System.currentTimeMillis() < end) {
            try (java.util.stream.Stream<java.nio.file.Path> s = java.nio.file.Files.list(downloadDir)) {
                Optional<java.nio.file.Path> hit = s.filter(p -> p.getFileName().toString().toLowerCase(Locale.ENGLISH).endsWith(".pdf")).filter(p -> {
                    String base = canonicalBase(p.getFileName().toString());
                    return base.contains(expectedBase);
                }).findFirst();
                if (hit.isPresent()) {
                    logger.info("✅ Downloaded file detected: {}", hit.get());
                    return true;
                }
            } catch (Throwable ignore) {
            }
            try {
                Thread.sleep(400);
            } catch (InterruptedException ignored) {
            }
        }
        return false;
    }

    private String extractFullFilename(WebElement row) {
        try {
            // look for first column links/spans
            WebElement firstCell = row.findElement(By.xpath("./td[1]//*"));
            String[] attrs = {"title", "download", "data-filename", "href"};
            for (String a : attrs) {
                String val = firstCell.getAttribute(a);
                if (val != null && val.toLowerCase().contains(".pdf")) {
                    return val.substring(val.lastIndexOf("/") + 1); // trim path if needed
                }
            }
            // fallback: textContent via JS
            return (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].textContent;", firstCell);
        } catch (Exception e) {
            return row.getText();
        }
    }


    public boolean clickFirstMatchingRowAndWaitDownloadPrefix(String expectedBasePrefix, Path downloadDir, Duration timeout) {
        final String expected = canonicalBase(expectedBasePrefix);

        // Collect candidate elements that represent document rows/links.
        // Cover both table and card/list UIs.
        List<WebElement> candidates = new ArrayList<>();
        candidates.addAll(driver.findElements(By.xpath("//*[@id='table-container']//tbody/tr[contains(@class,'cursor-pointer')]")));
        candidates.addAll(driver.findElements(By.xpath(
                // card/list fallback: items under the documents section that contain '.pdf'
                "//*[@id='table-container']//*[contains(translate(normalize-space(.), 'PDF', 'pdf'), '.pdf')]")));

        if (candidates.isEmpty()) {
            logger.warn("No document candidates found under #table-container.");
            return false;
        }

        for (WebElement el : candidates) {
            String fileName = extractFullPdfName(el);
            String base = canonicalBase(fileName);
            logger.info("Row filename raw='{}' | base='{}'", fileName, base);

            if (!fileName.isBlank() && base.startsWith(expected)) {
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
                } catch (Exception ignore) {
                }

                try {
                    commonMethods.safeClick(driver, el, "Document row/link: " + fileName, 10);
                } catch (Throwable t) {
                    logger.warn("Normal click failed, retrying via JS: {}", t.getMessage());
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                }

                return waitForPdfDownloaded(downloadDir, expected, timeout);
            }
        }

        logger.warn("No row matched expected base '{}'.", expected);
        return false;
    }

    /**
     * Try hard to get the *full* filename even if the UI truncates it.
     */
    private String extractFullPdfName(WebElement container) {
        try {
            // Prefer a direct anchor in the first cell
            List<By> probes = List.of(By.xpath(".//a[contains(@href,'.pdf') or contains(@download,'.pdf')]"), By.xpath(".//*[contains(@title,'.pdf') or contains(@data-filename,'.pdf')]"), By.xpath(".//*[contains(translate(normalize-space(.),'PDF','pdf'),'.pdf')]"));

            for (By by : probes) {
                List<WebElement> hits = container.findElements(by);
                for (WebElement hit : hits) {
                    String fromAttr = firstNonEmpty(hit.getAttribute("download"), hit.getAttribute("data-filename"),
                            // if href ends with a file, strip path/query
                            lastPathSegment(hit.getAttribute("href")), hit.getAttribute("title"));
                    if (fromAttr != null && fromAttr.toLowerCase().endsWith(".pdf")) {
                        return fromAttr;
                    }

                    // As a last resort for this hit, use innerText via JS (captures hidden/text nodes)
                    String inner = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText || arguments[0].textContent || '';", hit);
                    inner = inner == null ? "" : inner.trim();
                    // heuristics: pick the line that actually has ".pdf"
                    if (!inner.isBlank() && inner.toLowerCase().contains(".pdf")) {
                        String best = extractPdfToken(inner);
                        if (!best.isBlank()) return best;
                    }
                }
            }

            // Nothing from children; try the container's own innerText
            String own = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText || arguments[0].textContent || '';", container);
            own = own == null ? "" : own.trim();
            if (!own.isBlank() && own.toLowerCase().contains(".pdf")) {
                String best = extractPdfToken(own);
                if (!best.isBlank()) return best;
            }

            // Absolute last fallback: WebElement#getText (may be truncated/empty)
            return container.getText().trim();
        } catch (Exception e) {
            return container.getText().trim();
        }
    }

    private String firstNonEmpty(String... vals) {
        if (vals == null) return null;
        for (String v : vals) {
            if (v != null && !v.trim().isEmpty()) return v.trim();
        }
        return null;
    }

    private String lastPathSegment(String href) {
        if (href == null || href.isBlank()) return null;
        try {
            // strip query/fragment
            String p = href.split("[?#]", 2)[0];
            int slash = p.lastIndexOf('/');
            return slash >= 0 ? p.substring(slash + 1) : p;
        } catch (Exception e) {
            return href;
        }
    }

    /**
     * From a blob of text, pull the most likely PDF filename token.
     */
    private String extractPdfToken(String text) {
        // Grab the first token that looks like a filename, tolerating spaces/underscores
        // e.g. "RESIGNATION_LETTER_Sep_14_01_21_PM.pdf 16.88 KB"
        Matcher m = Pattern.compile("([\\p{L}\\p{N}_\\- ()]+?\\.pdf)", Pattern.CASE_INSENSITIVE).matcher(text);
        if (m.find()) {
            return m.group(1).trim();
        }
        // Fallback: return the first line with .pdf
        for (String line : text.split("\\R")) {
            if (line.toLowerCase().contains(".pdf")) return line.trim();
        }
        return "";
    }


    /**
     * Attaches a simple text listing of regular files in the given directory to Allure via
     * {@code logToAllure}. The listing includes each file's name and byte size (if available).
     * <p>
     * Behavior:
     * <ul>
     *   <li>Validates the input path (null, existence, directory, readability) and logs warnings/errors.</li>
     *   <li>Uses a {@link java.nio.file.DirectoryStream} to iterate entries, preserving your original approach.</li>
     *   <li>Collects "<code>filename (N bytes)</code>" lines, sorts them, and attaches to Allure.</li>
     *   <li>If the directory is empty or contains no regular files, attaches the special "— (empty) —" marker.</li>
     *   <li>On any unexpected exception, logs an error and attaches a failure note to Allure.</li>
     * </ul>
     *
     * @param dir   the directory to list (must exist, be a directory, and be readable)
     * @param title the title used for the Allure attachment
     */
    public void attachDownloadDirListing(Path dir, String title) {
        // ---- Validation with logging ----------------------------------------
        if (dir == null) {
            // fatal input error; do not proceed
            logger.error("attachDownloadDirListing called with null 'dir'. Title='{}'", title);
            logToAllure(title + " [invalid]", "Directory path is null.");
            return;
        }
        if (!Files.exists(dir)) {
            logger.warn("Directory does not exist: '{}'. Title='{}'", dir, title);
            logToAllure(title + " [missing]", "Directory does not exist: " + dir);
            return;
        }
        if (!Files.isDirectory(dir)) {
            logger.warn("Path is not a directory: '{}'. Title='{}'", dir, title);
            logToAllure(title + " [invalid]", "Path is not a directory: " + dir);
            return;
        }
        if (!Files.isReadable(dir)) {
            logger.warn("Directory is not readable: '{}'. Title='{}'", dir, title);
            logToAllure(title + " [unreadable]", "Directory is not readable: " + dir);
            return;
        }

        DirectoryStream<Path> ds = null;
        try {
            logger.info("Listing directory '{}' for Allure attachment. Title='{}'", dir, title);

            List<String> lines = new ArrayList<>();
            ds = Files.newDirectoryStream(dir);

            int examined = 0;
            for (Path p : ds) {
                examined++;
                if (Files.isRegularFile(p)) {
                    String name = String.valueOf(p.getFileName());
                    long size = -1L;
                    try {
                        size = Files.size(p);
                    } catch (IOException ioe) {
                        // size read failure is non-fatal; record and continue
                        logger.warn("Unable to read size for file '{}': {}", p, ioe.toString());
                    }
                    String sizeText = (size >= 0L ? (size + " bytes") : "size: ? bytes");
                    lines.add(name + " (" + sizeText + ")");
                }
            }

            Collections.sort(lines);

            if (lines.isEmpty()) {
                logger.warn("No regular files found in '{}'. Examined entries: {}", dir, examined);
                logToAllure(title, "— (empty) —");
            } else {
                logger.info("Attaching {} file(s) from '{}' to Allure. Examined entries: {}", lines.size(), dir, examined);
                logToAllure(title, lines);
            }
        } catch (Exception e) {
            logger.error("Failed to list directory '{}': {}", dir, e.toString(), e);
            logToAllure(title + " [error]", "Failed to list: " + dir + "\n" + e);
        } finally {
            if (ds != null) {
                try {
                    ds.close();
                } catch (IOException ioe) {
                    // stream close failure is non-fatal; warn and continue
                    logger.warn("Failed to close DirectoryStream for '{}': {}", dir, ioe.toString());
                }
            }
        }
    }

    public List<String> clickNewAndGetOptionsText() {
        List<String> items = new ArrayList<String>();
        try {
            logger.info("➡️ Clicking '+ New' button...");
            wait.until(ExpectedConditions.elementToBeClickable(plusNewButton)).click();

            try {
                if (wait.until(ExpectedConditions.visibilityOf(addNewFolderOption)).isDisplayed()) {
                    items.add("Add new folder");
                    logger.info("✅ 'Add new folder' visible");
                }
            } catch (Exception ignored) {
                logger.warn("⚠️ 'Add new folder' not visible");
            }

            try {
                if (wait.until(ExpectedConditions.visibilityOf(uploadFilesOption)).isDisplayed()) {
                    items.add("Upload files");
                    logger.info("✅ 'Upload files' visible");
                }
            } catch (Exception ignored) {
                logger.warn("⚠️ 'Upload files' not visible");
            }

        } catch (Exception e) {
            logger.error("❌ Exception while opening '+ New' menu.", e);
        }
        return items;
    }




    public boolean openAddNewFolderModal() {
        closeChatBotIfPresent();
        final By addNewFolderOptionBy = By.xpath("//p[normalize-space()='Add new folder']");
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));

        int attempts = 0;
        while (attempts < 2) {
            attempts++;
            try {
                logger.info("➡️ Try {}: ensuring '+ New' menu is open, then clicking 'Add new folder'...", attempts);

                // 1) Ensure the +New menu is open.
                boolean optionVisible;
                try {
                    WebElement optVisible = shortWait.until(
                            ExpectedConditions.visibilityOfElementLocated(addNewFolderOptionBy)
                    );
                    optionVisible = (optVisible != null && optVisible.isDisplayed());
                } catch (TimeoutException ignore) {
                    optionVisible = false;
                }

                if (!optionVisible) {
                    // Menu is not open → open it
                    try {
                        ((JavascriptExecutor) driver)
                                .executeScript("arguments[0].scrollIntoView({block:'center'});", plusNewButton);
                    } catch (Exception ignored) {}

                    wait.until(ExpectedConditions.elementToBeClickable(plusNewButton)).click();
                    logger.info("✅ '+ New' clicked (attempt {}). Waiting for options…", attempts);

                    wait.until(ExpectedConditions.visibilityOfElementLocated(addNewFolderOptionBy));
                } else {
                    logger.info("ℹ️ '+ New' menu already open; using existing options (attempt {}).", attempts);
                }

                // 2) Click the "Add new folder" option (with fallbacks)
                WebElement addFolderOpt = wait.until(
                        ExpectedConditions.elementToBeClickable(addNewFolderOptionBy)
                );

                try {
                    addFolderOpt.click();
                    logger.info("✅ Clicked 'Add new folder' via normal click.");
                } catch (Exception clickIntercept) {
                    logger.warn("⚠️ Normal click on 'Add new folder' failed: {}. Trying Actions…",
                            clickIntercept.getMessage());
                    try {
                        new Actions(driver)
                                .moveToElement(addFolderOpt)
                                .pause(Duration.ofMillis(150))
                                .click()
                                .perform();
                        logger.info("✅ Clicked 'Add new folder' via Actions.");
                    } catch (Exception actionFail) {
                        logger.warn("⚠️ Actions click failed: {}. Trying JS click…", actionFail.getMessage());
                        ((JavascriptExecutor) driver)
                                .executeScript("arguments[0].click();", addFolderOpt);
                        logger.info("✅ Clicked 'Add new folder' via JS.");
                    }
                }

                // 3) Wait for the create-folder modal (now with correct locator)
                WebElement root = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(MODAL_ROOT)
                );
                wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL_INPUT));
                wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL_CREATE));

                logger.info("✅ Create-folder modal is visible (attempt {}). Root: {}", attempts, root);
                return true;

            } catch (TimeoutException te) {
                logger.warn("⌛ Modal not visible after attempt {} ({}).", attempts, te.getMessage());
                try {
                    Thread.sleep(600L);
                } catch (InterruptedException ignored) {}
            } catch (Exception e) {
                logger.error("❌ Unexpected error while opening modal (attempt {}).", attempts, e);
                try {
                    Thread.sleep(600L);
                } catch (InterruptedException ignored) {}
            }
        }

        logger.error("❌ Failed to open 'Add new folder' modal after {} attempts.", attempts);
        return false;
    }





    /**
     * True if the create-folder modal is visible (input + create button).
     */
    public boolean isCreateFolderModalVisible() {
        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL_INPUT));
            WebElement create = wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL_CREATE));
            return input.isDisplayed() && create.isDisplayed();
        } catch (Exception e) {
            logger.error("❌ Modal not visible / timed out.", e);
            return false;
        }
    }

    /**
     * Type a folder name (random or given) and click "Create"
     */
    public boolean createFolder(String folderName) {
        try {
            wait.until(ExpectedConditions.visibilityOf(folderNameInput)).clear();
            folderNameInput.sendKeys(folderName);
            logger.info("✍️ Entered folder name: {}", folderName);

            wait.until(ExpectedConditions.elementToBeClickable(createFolderButton)).click();
            logger.info("🟦 Clicked 'Create' button.");

            return true;
        } catch (Exception e) {
            logger.error("❌ Failed to create folder.", e);
            return false;
        }
    }


    /**
     * Confirms we are navigated inside the newly created folder.
     */
    public boolean verifyNavigatedInsideFolder(final String folderName) {
        try {
            // Either breadcrumb contains exact name OR search placeholder mentions it
            By crumbWithName = By.xpath("//nav//*[self::p or self::span][normalize-space()='" + folderName + "']");
            By searchWithName = By.xpath("//input[contains(@placeholder, \"" + folderName + "\")]");

            wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOfElementLocated(crumbWithName), ExpectedConditions.visibilityOfElementLocated(searchWithName)));
            logger.info("✅ Inside folder: {}", folderName);
            return true;
        } catch (Exception e) {
            logger.error("❌ Not navigated into folder '{}'.", folderName, e);
            return false;
        }
    }


public void openUploadFilesModal() {
    closeChatBotIfPresent();

    final By uploadFilesOptionBy = By.xpath("//p[normalize-space()='Upload files']");
    WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));

    int attempts = 0;
    while (attempts < 2) {
        attempts++;
        try {
            logger.info("➡️ Try {}: ensuring '+ New' menu is open, then clicking 'Upload files'...", attempts);

            // 1) Ensure the + New menu is open
            boolean optionVisible;
            try {
                WebElement optVisible = shortWait.until(
                        ExpectedConditions.visibilityOfElementLocated(uploadFilesOptionBy)
                );
                optionVisible = (optVisible != null && optVisible.isDisplayed());
            } catch (TimeoutException ignore) {
                optionVisible = false;
            }

            if (!optionVisible) {
                // Menu is not open → open it
                try {
                    ((JavascriptExecutor) driver)
                            .executeScript("arguments[0].scrollIntoView({block:'center'});", plusNewButton);
                } catch (Exception ignored) {}

                wait.until(ExpectedConditions.elementToBeClickable(plusNewButton)).click();
                logger.info("✅ '+ New' clicked (attempt {}). Waiting for 'Upload files' option…", attempts);

                wait.until(ExpectedConditions.visibilityOfElementLocated(uploadFilesOptionBy));
            } else {
                logger.info("ℹ️ '+ New' menu already open; using existing options (attempt {}).", attempts);
            }

            // 2) Click the "Upload files" option (with fallbacks)
            WebElement uploadOpt = wait.until(
                    ExpectedConditions.elementToBeClickable(uploadFilesOptionBy)
            );

            try {
                uploadOpt.click();
                logger.info("✅ Clicked 'Upload files' via normal click.");
            } catch (Exception clickIntercept) {
                logger.warn("⚠️ Normal click on 'Upload files' failed: {}. Trying Actions…",
                        clickIntercept.getMessage());
                try {
                    new Actions(driver)
                            .moveToElement(uploadOpt)
                            .pause(Duration.ofMillis(150))
                            .click()
                            .perform();
                    logger.info("✅ Clicked 'Upload files' via Actions.");
                } catch (Exception actionFail) {
                    logger.warn("⚠️ Actions click failed: {}. Trying JS click…", actionFail.getMessage());
                    ((JavascriptExecutor) driver)
                            .executeScript("arguments[0].click();", uploadOpt);
                    logger.info("✅ Clicked 'Upload files' via JS.");
                }
            }

            // 3) Wait for the dropzone card instead of a generic fixed modal
            wait.until(ExpectedConditions.visibilityOfElementLocated(DROPZONE_CARD));
            logger.info("✅ Upload dropzone is visible after clicking 'Upload files' (attempt {}).", attempts);
            return;

        } catch (TimeoutException te) {
            logger.warn("⌛ Upload dropzone not visible after attempt {} ({}).",
                    attempts, te.getMessage());
            try {
                Thread.sleep(600L);
            } catch (InterruptedException ignored) {}
        } catch (Exception e) {
            logger.error("❌ Unexpected error while opening upload dropzone (attempt {}).", attempts, e);
            try {
                Thread.sleep(600L);
            } catch (InterruptedException ignored) {}
        }
    }

    logger.error("❌ Failed to open upload dropzone via 'Upload files' after {} attempts.", attempts);
    throw new TimeoutException("Upload dropzone did not become visible after clicking 'Upload files'");
}


    /**
     * Returns true if the dropzone looks clickable (visible, enabled, pointer-events != none).
     */
    public boolean isDropzoneClickable() {
        try {
            WebElement dz = wait.until(ExpectedConditions.visibilityOfElementLocated(DROPZONE_CARD));

            // Basic checks
            boolean visible = dz.isDisplayed();
            boolean enabled = dz.isEnabled();

            // Defensive CSS checks to avoid false positives
            String pe = String.valueOf(((JavascriptExecutor) driver).executeScript("return window.getComputedStyle(arguments[0]).pointerEvents;", dz));
            String cur = String.valueOf(((JavascriptExecutor) driver).executeScript("return window.getComputedStyle(arguments[0]).cursor;", dz));
            boolean pointerOk = !"none".equalsIgnoreCase(pe);
            boolean cursorOk = !"not-allowed".equalsIgnoreCase(cur);

            // Hover only (no click), helps ensure it’s interactable in viewport
            new org.openqa.selenium.interactions.Actions(driver).moveToElement(dz).pause(java.time.Duration.ofMillis(120)).perform();

            boolean clickable = visible && enabled && pointerOk && cursorOk;
            logger.info("Dropzone clickable? {}", clickable);
            return clickable;
        } catch (Exception e) {
            logger.error("Dropzone clickable check failed.", e);
            return false;
        }
    }


    public void closeChatBotIfPresent() {
        try {
            // Find all iframes on the page – we don't know which one is the chat yet
            List<WebElement> iframes = driver.findElements(By.tagName("iframe"));

            if (iframes.isEmpty()) {
                logger.info("Chat widget NOT found: no iframes on page.");
                return;
            }

            boolean closed = false;

            for (WebElement frame : iframes) {
                try {
                    driver.switchTo().frame(frame);

                    // Look for the chat text inside this frame
                    List<WebElement> bubbles = driver.findElements(
                            By.cssSelector("p.chat-popup-widget-text")
                    );

                    if (bubbles.isEmpty()) {
                        // Not this frame – go back out and try next
                        driver.switchTo().defaultContent();
                        continue;
                    }

                    // We found the chat widget in this iframe
                    WebElement textBubble = bubbles.get(0);

                    // Hover so the close button appears
                    new Actions(driver)
                            .moveToElement(textBubble)
                            .perform();

                    // Click the close button container (appears only on hover)
                    WebElement closeBtn = wait.waitForElementToBeClickable(
                            By.cssSelector("div.chat-popup-widget-close-btn-container")

                    );
                    closeBtn.click();

                    logger.info("✅ Chat widget closed successfully.");
                    closed = true;

                    // Back to main document and stop searching
                    driver.switchTo().defaultContent();
                    break;

                } catch (Exception inner) {
                    // If anything fails in this frame, go back to main and try next frame
                    try {
                        driver.switchTo().defaultContent();
                    } catch (Exception ignore) {}
                }
            }

            if (!closed) {
                logger.info("Chat widget NOT found in any iframe.");
            }

        } catch (Exception e) {
            logger.warn("⚠️ Error while trying to close chat widget: {}", e.getMessage());
            try {
                driver.switchTo().defaultContent();
            } catch (Exception ignore) {}
        }
    }

}








