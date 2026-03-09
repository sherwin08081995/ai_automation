package pages;


import base.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.FindBy;

import java.time.Duration;
import java.util.*;

/**
 * @author Sherwin
 * @since 01-09-2025
 */

public class FAQsPage extends BasePage {

    public FAQsPage(WebDriver driver) {
        super(driver);
    }


    @FindBy(xpath = "//h4[normalize-space()='Choose the topic where you need help']")
    private WebElement faqHeader;

    @FindBy(xpath = "//div[contains(@class,'faqItem')]//p")
    private List<WebElement> topicLabels;

    @FindBy(xpath = "//div[contains(@class,'faqContainer')]")
    private WebElement topicsListWrapper;


    // Header for Compliance Calendar
    @FindBy(xpath = "//h4[normalize-space()='Compliance Calendar']")
    private WebElement topicHeader_ComplianceCalendar;

    // Container + items
    @FindBy(xpath = "//div[contains(@class,'accordionContainer')]")
    private WebElement accordionContainer;

    @FindBy(xpath = "//div[contains(@class,'accordionContainer')]//div[contains(@class,'accordion')]")
    private List<WebElement> accordionItems;

    // Question row text (<p>) and icon (<img>) for each item
    @FindBy(xpath = "//div[contains(@class,'accordionContainer')]//div[contains(@class,'accordion')]//div[contains(@class,'toggle')]//p")
    private List<WebElement> questionTitles;

    @FindBy(xpath = "//div[contains(@class,'accordionContainer')]//div[contains(@class,'accordion')]//div[contains(@class,'toggle')]//img")
    private List<WebElement> toggleIcons;

    // Content paragraph inside each item (visible when expanded)
    @FindBy(xpath = "//div[contains(@class,'accordionContainer')]//div[contains(@class,'accordion')]//div[contains(@class,'content')]//p")
    private List<WebElement> contentParas;

    // Back
    @FindBy(xpath = "(//a[normalize-space()='Back'] | //button[normalize-space()='Back'] | //span[normalize-space()='Back'])[1]")
    private WebElement backButton;

    @FindBy(xpath = "//p[normalize-space()='FAQs']")
    private WebElement faqsMenu;

    @FindBy(xpath = "//h4[normalize-space()='Choose the topic where you need help']")
    private WebElement faqsHeader;



    public void clickFaqsMenu() {
        logger.info("Attempting to click 'FAQs' menu using safeClick…");
        try {
            commonMethods.safeClick(driver, faqsMenu, "FAQs", 10);
            logger.info("'FAQs' menu clicked successfully.");
        } catch (Throwable t) {
            logger.error("❌ Failed to click 'FAQs' menu even after safeClick retries: {}", t.getMessage(), t);
            throw t; // let the step handle it with handleValidationException
        }
    }


    /** Waits for FAQs page to load by verifying the header visibility and text. */
    public boolean waitForFaqsPageToLoad(Duration timeout) {
        final String expected = "Choose the topic where you need help";
        try {
            // use your custom timeout-aware wait helper
            WebElement header = wait.waitForVisibilityCustomTimeOut(
                    By.xpath("//h4[normalize-space()='Choose the topic where you need help']"),
                    timeout
            );

            logger.info("FAQs header is visible.");

            // exact text validation (soft)
            String actual = header.getText() == null ? "" : header.getText().trim();
            if (!expected.equals(actual)) {
                logger.warn("FAQs header text mismatch. Expected='{}' | Actual='{}'", expected, actual);
            } else {
                logger.info("FAQs header text matches expected.");
            }

            return true;

        } catch (TimeoutException te) {
            logger.error("Timed out waiting for FAQs header within ~{} s.",
                    (timeout != null ? timeout.toSeconds() : "n/a"), te);
            return false;

        } catch (Throwable t) {
            logger.error("Error while verifying FAQs page header: {}", t.getMessage(), t);
            return false;
        }
    }



    /**
     * Waits until the FAQ topics list is ready (header, wrapper, and at least one visible topic label).
     *
     * @return {@code true} if the topics list is visible and contains at least one displayed item; {@code false} otherwise
     */
    public boolean waitForTopicsList() {
        try {
            wait.waitForVisibility(faqHeader);
            wait.waitForVisibility(topicsListWrapper);
            wait.waitForVisibilityOfAllElements(topicLabels);
            return getTopicCount() > 0;
        } catch (TimeoutException e) {
            logger.warn("Timeout waiting for FAQ topics list", e);
            return false;
        } catch (Exception e) {
            logger.error("Error waiting for FAQ topics list", e);
            return false;
        }
    }

    /**
     * Counts displayed topic labels on the FAQ landing view.
     *
     * @return number of displayed topic label elements (with non-empty text); 0 if list is null
     */
    public int getTopicCount() {
        if (topicLabels == null) return 0;
        int count = 0;
        for (WebElement el : topicLabels) {
            try {
                if (el != null && el.isDisplayed() && el.getText() != null && !el.getText().trim().isEmpty()) {
                    count++;
                }
            } catch (Exception ignored) { /* stale or detached */ }
        }
        return count;
    }

    /**
     * Retrieves the visible topic titles (trimmed).
     *
     * @return ordered list of visible topic titles; empty strings are added if an entry is visible but had null text
     */
    public List<String> getTopicTitles() {
        List<String> titles = new ArrayList<>();
        if (topicLabels == null) return titles;

        for (WebElement element : topicLabels) {
            try {
                if (element != null && element.isDisplayed()) {
                    String text = element.getText();
                    if (text != null) {
                        titles.add(text.trim());
                    } else {
                        titles.add("");
                    }
                }
            } catch (Exception ignored) { /* stale or detached */ }
        }
        return titles;
    }

    /**
     * Exact, order-sensitive list comparison.
     *
     * @param expected expected list of strings
     * @param actual   actual list of strings
     * @return {@code true} if both lists are non-null, same size, and each element equals at the same index
     */
    public boolean exactOrderMatch(List<String> expected, List<String> actual) {
        if (expected == null || actual == null) return false;
        if (expected.size() != actual.size()) return false;
        for (int i = 0; i < expected.size(); i++) {
            if (!Objects.equals(expected.get(i), actual.get(i))) return false;
        }
        return true;
    }

    /**
     * Trims all strings in the input list; converts nulls to empty strings.
     *
     * @param input source list (nullable)
     * @return list with {@code trim()} applied; null entries become empty strings
     */
    public List<String> trimAll(List<String> input) {
        List<String> out = new ArrayList<>();
        if (input == null) return out;
        for (String s : input) {
            out.add(s == null ? "" : s.trim());
        }
        return out;
    }

    /**
     * Checks if the provided list has any duplicate values (case-sensitive).
     *
     * @param values list to evaluate
     * @return {@code true} if duplicates exist; {@code false} otherwise
     */
    public boolean hasDuplicates(List<String> values) {
        return !getDuplicates(values).isEmpty();
    }

    /**
     * Returns a list of duplicate values, preserving the order of first duplicate appearance.
     *
     * @param values list to scan (nullable)
     * @return list of duplicate values (case-sensitive), in encounter order
     */
    public List<String> getDuplicates(List<String> values) {
        List<String> dups = new ArrayList<>();
        if (values == null) return dups;

        Set<String> seen = new HashSet<>();
        Set<String> added = new HashSet<>();
        for (String v : values) {
            String key = v == null ? "" : v;
            if (!seen.add(key) && added.add(key)) {
                dups.add(key);
            }
        }
        return dups;
    }

    /**
     * Computes items expected but not present in the actual list.
     *
     * @param expected expected items (nullable)
     * @param actual   actual items (nullable)
     * @return items present in {@code expected} but missing from {@code actual}
     */
    public List<String> listMissing(List<String> expected, List<String> actual) {
        List<String> missing = new ArrayList<>();
        if (expected == null) return missing;
        Set<String> actualSet = new HashSet<>(actual == null ? Collections.emptyList() : actual);
        for (String e : expected) {
            if (!actualSet.contains(e)) {
                missing.add(e);
            }
        }
        return missing;
    }

    /**
     * Computes items present in actual but not in expected.
     *
     * @param expected expected items (nullable)
     * @param actual   actual items (nullable)
     * @return items present in {@code actual} but not in {@code expected}
     */
    public List<String> listUnexpected(List<String> expected, List<String> actual) {
        List<String> unexpected = new ArrayList<>();
        Set<String> expectedSet = new HashSet<>(expected == null ? Collections.emptyList() : expected);
        if (actual == null) return unexpected;
        for (String a : actual) {
            if (!expectedSet.contains(a)) {
                unexpected.add(a);
            }
        }
        return unexpected;
    }

    /**
     * Reports items that exist in both lists but appear at different indices (duplicates in expected not supported).
     *
     * @param expected expected ordered list
     * @param actual   actual ordered list
     * @return human-readable descriptors like {@code "Item (expected @1, actual @3)"} for out-of-order entries
     */
    public List<String> listOutOfOrder(List<String> expected, List<String> actual) {
        List<String> out = new ArrayList<>();
        if (expected == null || actual == null) return out;

        Map<String, Integer> actualIndex = new HashMap<>();
        for (int i = 0; i < actual.size(); i++) {
            // store first index only
            actualIndex.putIfAbsent(actual.get(i), i);
        }

        for (int i = 0; i < expected.size(); i++) {
            String e = expected.get(i);
            Integer ai = actualIndex.get(e);
            if (ai != null && ai != i) {
                out.add(String.format("%s (expected @%d, actual @%d)", e, i, ai));
            }
        }
        return out;
    }

    /**
     * Validates visibility and basic structure for the "Compliance Calendar" topic block.
     *
     * @return {@code true} if the header and container are visible, at least one item exists,
     * and no blank question labels are found; {@code false} otherwise
     */
    public boolean isComplianceHeaderVisible() {
        logger.info("🔎 Validating topic header 'Compliance Calendar' and base structure");
        try {
            wait.waitForVisibility(topicHeader_ComplianceCalendar);
            wait.waitForVisibility(accordionContainer);

            boolean headerDisplayed = topicHeader_ComplianceCalendar != null && topicHeader_ComplianceCalendar.isDisplayed();
            boolean containerDisplayed = accordionContainer != null && accordionContainer.isDisplayed();
            int items = (accordionItems == null) ? 0 : accordionItems.size();
            int qTitles = (questionTitles == null) ? 0 : questionTitles.size();
            int icons = (toggleIcons == null) ? 0 : toggleIcons.size();

            logger.info("📊 headerDisplayed={}, containerDisplayed={}, items={}, questionTitles={}, icons={}", headerDisplayed, containerDisplayed, items, qTitles, icons);

            if (!headerDisplayed) logger.error("❌ Topic header 'Compliance Calendar' is not displayed");
            if (!containerDisplayed) logger.error("❌ Accordion container is not displayed");
            if (items == 0) logger.error("❌ No accordion items found under this topic");
            if (qTitles != items) logger.warn("⚠️ Questions count ({}) != item count ({})", qTitles, items);
            if (icons != items) logger.warn("⚠️ Icon count ({}) != item count ({})", icons, items);

            // Blank-question sanity
            int blanks = 0;
            for (int i = 0; i < qTitles; i++) {
                String txt = questionTitles.get(i).getText();
                txt = (txt == null) ? "" : txt.trim();
                if (txt.isEmpty()) {
                    blanks++;
                    logger.warn("⚠️ Question #{} text is empty/blank", i + 1);
                }
            }
            if (blanks > 0) logger.warn("⚠️ Found {} blank question label(s)", blanks);

            boolean ok = headerDisplayed && containerDisplayed && items > 0 && blanks == 0;
            logger.info(ok ? "✅ Topic header & structure OK" : "❌ Topic header/structure validation failed");
            return ok;

        } catch (TimeoutException e) {
            logger.error("⏳ Timeout waiting for topic header/container: {}", e.toString());
            return false;
        } catch (Exception e) {
            logger.error("💥 Unexpected error in isComplianceHeaderVisible(): {}", e.toString());
            return false;
        }
    }

    /**
     * Reads all question titles under the current accordion container.
     *
     * @return ordered list of question titles (trimmed); empty strings added where text is null or unreadable
     */
    public List<String> getQuestionTitles() {
        List<String> out = new ArrayList<>();
        int count = (questionTitles == null) ? 0 : questionTitles.size();
        logger.info("📥 Reading {} question title(s)", count);
        for (int i = 0; i < count; i++) {
            try {
                WebElement p = questionTitles.get(i);
                String txt = (p.getText() == null) ? "" : p.getText().trim();
                if (txt.isEmpty()) {
                    logger.warn("⚠️ Question #{} is empty/blank", i + 1);
                } else {
                    logger.info("• Q#{}: {}", i + 1, txt);
                }
                out.add(txt);
            } catch (Exception ex) {
                logger.error("💥 Error reading question title #{}: {}", i + 1, ex.toString());
                out.add("");
            }
        }
        return out;
    }

    /**
     * Expands all accordion items; attempts up to 3 clicks per item and verifies by content visibility or icon state.
     *
     * <p>Logs any items that fail to expand. Does not throw by default (toggle commented
     * assertion if you want a hard fail).</p>
     */
    public void expandAll() {
        int n = (accordionItems == null) ? 0 : accordionItems.size();
        logger.info("🪗 Expanding all {} accordion item(s)", n);
        List<Integer> failed = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            WebElement item = accordionItems.get(i);
            WebElement toggle;
            try {
                toggle = item.findElement(By.xpath(".//div[contains(@class,'toggle')]"));
            } catch (Exception e) {
                logger.error("❌ Item #{}: toggle not found: {}", i + 1, e.toString());
                failed.add(i + 1);
                continue;
            }

            String beforeIcon = iconStateForIndex(i);
            boolean beforeContent = isContentVisible(item);
            logger.info("▶️  Expand item #{} (iconBefore='{}', contentVisibleBefore={})", i + 1, beforeIcon, beforeContent);

            boolean success = false;
            for (int attempt = 1; attempt <= 3; attempt++) {
                try {
                    commonMethods.safeClick(driver, toggle, "Accordion toggle #" + (i + 1), 8);
                    // verify expansion via content OR icon flip
                    boolean contentNow = isContentVisible(item);
                    String iconNow = iconStateForIndex(i);
                    if (contentNow || "-".equals(iconNow)) {
                        success = true;
                        logger.info("✅ Expanded item #{} on attempt {} (iconNow='{}', contentVisibleNow={})", i + 1, attempt, iconNow, contentNow);
                        break;
                    }
                    logger.warn("⚠️ Item #{} not expanded yet after attempt {} (iconNow='{}', contentVisibleNow={})", i + 1, attempt, iconNow, contentNow);
                } catch (Exception e) {
                    logger.warn("⚠️ Click attempt {} failed for item #{}: {}", attempt, i + 1, e.toString());
                }
            }
            if (!success) {
                logger.error("❌ Failed to expand item #{} after 3 attempts", i + 1);
                failed.add(i + 1);
            }
        }

        if (!failed.isEmpty()) {
            logger.error("❌ expandAll(): failures at item(s) {}", failed);
            // optional: throw to hard-fail here
            // throw new AssertionError("expandAll failed for items: " + failed);
        } else {
            logger.info("✅ All items expanded");
        }
    }

    /**
     * Validates for each accordion item that content is visible and non-empty.
     *
     * @return list of booleans (per item) where {@code true} indicates visible content with non-empty text
     */
    public List<Boolean> contentsVisibleAndNonEmpty() {
        List<Boolean> out = new ArrayList<>();
        int n = (accordionItems == null) ? 0 : accordionItems.size();
        logger.info("🔍 Validating content visible & non-empty for {} item(s)", n);

        for (int i = 0; i < n; i++) {
            boolean ok = false;
            try {
                WebElement item = accordionItems.get(i);
                WebElement p = item.findElement(By.xpath(".//div[contains(@class,'content')]//p"));
                String txt = (p.getText() == null) ? "" : p.getText().trim();
                boolean vis = p.isDisplayed();
                ok = vis && !txt.isEmpty();

                if (ok) {
                    logger.info("✅ Content OK for item #{} (len={})", i + 1, txt.length());
                } else {
                    logger.warn("⚠️ Content issue at item #{} -> visible={}, len={}", i + 1, vis, txt.length());
                }
            } catch (NoSuchElementException nse) {
                logger.error("❌ Item #{} content <p> not found", i + 1);
            } catch (Exception e) {
                logger.error("💥 Error validating content for item #{}: {}", i + 1, e.toString());
            }
            out.add(ok);
        }
        return out;
    }

    /**
     * Returns icon states for each accordion row.
     *
     * @return list of icon states where {@code "+"} means collapsed, {@code "-"} means expanded,
     * and {@code "?"} indicates an indeterminate/unknown state
     */
    public List<String> getIconStates() {
        List<String> out = new ArrayList<>();
        int n = (toggleIcons == null) ? 0 : toggleIcons.size();
        logger.info("🖼️ Reading icon states for {} item(s)", n);

        for (int i = 0; i < n; i++) {
            String state = "?";
            try {
                WebElement icon = toggleIcons.get(i);
                String src = icon.getAttribute("src");
                if (src != null && src.contains("plus.svg")) state = "+";
                else if (src != null && src.contains("minus.svg")) state = "-";
                else state = "?";

                if ("?".equals(state)) {
                    logger.warn("⚠️ Unknown icon state item #{} (src='{}')", i + 1, src);
                } else {
                    logger.info("• Icon #{} = '{}' (src='{}')", i + 1, state, src);
                }
            } catch (Exception e) {
                logger.error("💥 Error reading icon #{}: {}", i + 1, e.toString());
            }
            out.add(state);
        }
        return out;
    }

    /**
     * Collapses all accordion items; attempts up to 3 clicks per item and verifies by hidden content and '+' icon.
     *
     * <p>Logs any items that fail to collapse. Does not throw by default (toggle commented
     * assertion if you want a hard fail).</p>
     */
    public void collapseAll() {
        int n = (accordionItems == null) ? 0 : accordionItems.size();
        logger.info("🧹 Collapsing all {} accordion item(s)", n);
        List<Integer> failed = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            WebElement item = accordionItems.get(i);
            WebElement toggle;
            try {
                toggle = item.findElement(By.xpath(".//div[contains(@class,'toggle')]"));
            } catch (Exception e) {
                logger.error("❌ Item #{}: toggle not found: {}", i + 1, e.toString());
                failed.add(i + 1);
                continue;
            }

            String beforeIcon = iconStateForIndex(i);
            boolean beforeContent = isContentVisible(item);
            logger.info("◀️  Collapse item #{} (iconBefore='{}', contentVisibleBefore={})", i + 1, beforeIcon, beforeContent);

            boolean success = false;
            for (int attempt = 1; attempt <= 3; attempt++) {
                try {
                    commonMethods.safeClick(driver, toggle, "Accordion toggle #" + (i + 1) + " (collapse)", 8);
                    boolean contentNow = isContentVisible(item);
                    String iconNow = iconStateForIndex(i);
                    // treat collapsed as: content hidden AND icon '+'
                    if (!contentNow && "+".equals(iconNow)) {
                        success = true;
                        logger.info("✅ Collapsed item #{} on attempt {} (iconNow='{}', contentVisibleNow={})", i + 1, attempt, iconNow, contentNow);
                        break;
                    }
                    logger.warn("⚠️ Item #{} not collapsed after attempt {} (iconNow='{}', contentVisibleNow={})", i + 1, attempt, iconNow, contentNow);
                } catch (Exception e) {
                    logger.warn("⚠️ Collapse attempt {} failed for item #{}: {}", attempt, i + 1, e.toString());
                }
            }
            if (!success) {
                logger.error("❌ Failed to collapse item #{} after 3 attempts", i + 1);
                failed.add(i + 1);
            }
        }

        if (!failed.isEmpty()) {
            logger.error("❌ collapseAll(): failures at item(s) {}", failed);
            // optional: throw new AssertionError("collapseAll failed for items: " + failed);
        } else {
            logger.info("✅ All items collapsed");
        }
    }

    /**
     * Clicks the "Back" button/element from the topic view.
     *
     * @throws RuntimeException if the click fails for any reason
     */
    public void clickBack() {
        logger.info("⬅️ Clicking 'Back' from topic view");
        try {
            commonMethods.safeClick(driver, backButton, "Back", 6);
            logger.info("✅ Back clicked");
        } catch (Exception e) {
            logger.error("💥 Failed to click Back: {}", e.toString());
            throw e;
        }
    }

    /**
     * Checks if the accordion content box within the provided item is displayed.
     *
     * @param item accordion item root element
     * @return {@code true} if the content box is present and displayed; {@code false} otherwise
     */
    private boolean isContentVisible(WebElement item) {
        try {
            WebElement box = item.findElement(By.xpath(".//div[contains(@class,'content')]"));
            return box.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Resolves icon state at the given index.
     *
     * @param i zero-based index of the accordion item
     * @return {@code "+"} if icon src contains {@code plus.svg}, {@code "-"} if {@code minus.svg}, otherwise {@code "?"}
     */
    private String iconStateForIndex(int i) {
        try {
            WebElement icon = toggleIcons.get(i);
            String src = icon.getAttribute("src");
            if (src != null && src.contains("plus.svg")) return "+";
            if (src != null && src.contains("minus.svg")) return "-";
        } catch (Exception ignored) {
        }
        return "?";
    }


    /**
     * Clicks a topic tile by its visible text, waiting for the topics list first and using a custom timeout.
     *
     * @param topicName  exact visible text of the topic to click
     * @param timeoutSec max seconds to allow for the click operation
     * @throws AssertionError   if the topics list is not visible or the topic cannot be located
     * @throws RuntimeException if the click fails due to interaction issues
     */
    public void clickTopicByName(String topicName, int timeoutSec) {
        logger.info("🧭 Attempting to click topic '{}'", topicName);

        // Precondition: list visible
        boolean listOk = waitForTopicsList();
        if (!listOk) {
            logger.error("❌ FAQ topics list is not visible; cannot click '{}'", topicName);
            throw new AssertionError("FAQ topics list not visible.");
        }

        // Snapshot visible titles (useful on failure)
        List<String> visible = getTopicTitles();
        logger.info("📋 Visible topics ({}): {}", visible.size(), visible);

        // Locate target (prefer @FindBy cache; fallback dynamic)
        WebElement target = null;
        int idx = -1;
        for (int i = 0; i < topicLabels.size(); i++) {
            try {
                WebElement el = topicLabels.get(i);
                String txt = (el.getText() == null) ? "" : el.getText().trim();
                if (el.isDisplayed() && topicName.equals(txt)) {
                    target = el;
                    idx = i;
                    break;
                }
            } catch (StaleElementReferenceException sere) {
                logger.warn("♻️ topicLabels[{}] stale; will try dynamic locator", i);
            } catch (Exception ex) {
                logger.warn("⚠️ Error reading topicLabels[{}]: {}", i, ex.toString());
            }
        }
        if (target == null) {
            By dyn = By.xpath("//div[contains(@class,'faqItem')]//p[normalize-space()='" + topicName + "']");
            logger.warn("🔄 Falling back to dynamic locator for '{}'", topicName);
            try {
                target = driver.findElement(dyn);
            } catch (Exception e) {
                logger.error("❌ Topic '{}' not found. Visible: {}", topicName, visible);
                throw new AssertionError("Topic not found: '" + topicName + "'. Visible: " + visible);
            }
        } else {
            logger.info("✅ Found '{}' at index {}", topicName, idx + 1);
        }

        // Scroll + click
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", target);
        } catch (Exception se) {
            logger.warn("⚠️ scrollIntoView failed for '{}': {}", topicName, se.toString());
        }

        try {
            commonMethods.safeClick(driver, target, "FAQ Topic: " + topicName, timeoutSec);
            logger.info("✅ Clicked FAQ topic '{}'", topicName);
        } catch (Exception e) {
            logger.error("💥 Click failed for topic '{}': {}", topicName, e.toString());
            throw e;
        }
    }


    /**
     * Waits until a topic view with the provided header text appears (best-effort matching across H1–H6).
     *
     * <p>Tries exact H4, exact any heading (H1–H6), then CONTAINS() match on any heading.
     * Also attempts to wait for the accordion container for better readiness.</p>
     *
     * @param topicName  expected heading text
     * @param timeoutSec maximum wait time (seconds)
     * @return {@code true} if a matching header and container are displayed within the timeout; {@code false} otherwise
     */
    public boolean waitForTopicHeader(String topicName, int timeoutSec) {
        long end = System.currentTimeMillis() + timeoutSec * 1000L;
        String xExactH4 = "//h4[normalize-space()='" + topicName + "']";
        String xExactAny = "//*[self::h1 or self::h2 or self::h3 or self::h4 or self::h5 or self::h6][normalize-space()='" + topicName + "']";
        String xContainsAny = "//*[self::h1 or self::h2 or self::h3 or self::h4 or self::h5 or self::h6][contains(normalize-space(.),'" + topicName + "')]";

        while (System.currentTimeMillis() < end) {
            try {
                WebElement header = null;
                try {
                    header = driver.findElement(By.xpath(xExactH4));
                } catch (Exception ignore) {
                }
                if (header == null) {
                    try {
                        header = driver.findElement(By.xpath(xExactAny));
                    } catch (Exception ignore) {
                    }
                }
                if (header == null) {
                    try {
                        header = driver.findElement(By.xpath(xContainsAny));
                        if (header != null) {
                            logger.warn("⚠️ Using CONTAINS() match for header '{}'", topicName);
                        }
                    } catch (Exception ignore) {
                    }
                }

                if (header != null) {
                    try {
                        wait.waitForVisibility(header);
                    } catch (Exception ignore) {
                    }
                    try {
                        wait.waitForVisibility(accordionContainer);
                    } catch (Exception ignore) {
                    }

                    boolean ok = header.isDisplayed() && accordionContainer.isDisplayed();
                    if (ok) return true;
                }
            } catch (Exception ignore) {
            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException ignored) {
            }
        }
        return false;
    }

    /**
     * One-shot visibility and basic structure check for a topic header by name.
     *
     * @param topicName expected topic header text
     * @return {@code true} if header and container are visible (with minimal structure logging), {@code false} otherwise
     */
    public boolean isTopicHeaderVisible(String topicName) {
        logger.info("🔎 Validating topic header '{}' and base structure", topicName);
        boolean ok = waitForTopicHeader(topicName, 8);
        if (!ok) {
            logger.error("❌ Topic header '{}' not visible within timeout", topicName);
            return false;
        }

        // Additional context (safe logging)
        int items = 0, titlesCount = 0, iconsCount = 0;
        try {
            items = accordionItems == null ? 0 : accordionItems.size();
        } catch (Exception ignore) {
        }
        try {
            titlesCount = questionTitles == null ? 0 : questionTitles.size();
        } catch (Exception ignore) {
        }
        try {
            iconsCount = toggleIcons == null ? 0 : toggleIcons.size();
        } catch (Exception ignore) {
        }

        logger.info("ℹ️ headerDisplayed=true, containerDisplayed=true, items={}, questionTitles={}, icons={}", items, titlesCount, iconsCount);

        // Minimal structural sanity:
        if (items <= 0 || titlesCount <= 0 || iconsCount <= 0) {
            logger.warn("⚠️ Structure looks light for '{}': items={}, titles={}, icons={}", topicName, items, titlesCount, iconsCount);
        } else {
            logger.info("✅ Topic header & structure OK for '{}'", topicName);
        }
        return true;
    }


}
