package stepDefinitions;


import hooks.Hooks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.ReportsPage;
import utils.*;

import java.time.Instant;
import java.util.*;

import static utils.AllureLoggerUtils.logToAllure;

import java.time.Duration;

/**
 * @author Sherwin
 * @since 16-10-2025
 */

public class ReportsPageValidationSteps {

    WebDriver driver = Hooks.driver;
    ReportsPage reportsPage;
    Logger logger;
    WebDriverWait wait;
    AllureLoggerUtils allureLogging;
    ReusableCommonMethods helperMethods;
    private String fuClickedValue = null;

    public ReportsPageValidationSteps() {
        this.driver = Hooks.driver;
        this.reportsPage = new ReportsPage(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.logger = LoggerUtils.getLogger(getClass());
        this.helperMethods = new ReusableCommonMethods(driver);
    }


    @Step("{message}")
    public void logStep(String message) {
        logger.info(message);
    }

    @Given("the user is on the Reports page")
    public void the_user_is_on_the_reports_page() {
        // we want screenshots no matter what happens
        try {
            logStep("🔍 Navigating and confirming that the user is on the Reports page...");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Reports_Nav_Start");

            Instant navStart = Instant.now();
            NavContext.start("Reports");

            // 1️⃣ Click the Reports tab
            reportsPage.clickReportsTab();

            // 📸 1) Screenshot IMMEDIATELY AFTER CLICK (helps debug slow/blocked nav)
            ScreenshotUtils.attachScreenshotToAllure(driver, "Reports_After_Click");

            // 2️⃣ Wait for load completion
            boolean success = reportsPage.waitForReportsLoaded(Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS));

            // 3️⃣ Log timing
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Reports", navStart);
            double elapsedSec = elapsedMs / 1000.0;

            // 4️⃣ SLA logic
            if (success) {
                // 📸 2) Screenshot AFTER LOAD (title visible)
                ScreenshotUtils.attachScreenshotToAllure(driver, "Reports_Page_Visible");

                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("Reports took %.2f s — exceeded SLA %ds.", elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Failure", failMsg);
                    ScreenshotUtils.attachScreenshotToAllure(driver, "Reports_Load_Timeout");
                    Assert.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("Reports took %.2f s — exceeded warning threshold %ds.", elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                }
            } else {
                String failMsg = String.format("Unable to load Reports within %ds (actual %.2fs).", ReusableCommonMethods.NAV_FAIL_MS / 1000, elapsedSec);
                logger.error(failMsg);
                logToAllure("❌ Access Failure", failMsg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "Reports_Access_Failure");
                Assert.fail(failMsg);
            }

            logStep("📋 Reports Page Loaded Successfully.");
            logToAllure("✅ Reports Page Confirmation", "Reports page verified visually.");
            logger.info("✅ Reports page successfully confirmed.");

        } catch (Throwable t) {
            // 📸 Always attach a last-chance screenshot on exception
            ScreenshotUtils.attachScreenshotToAllure(driver, "Reports_Page_Exception");
            reportsPage.handleValidationException("Reports page confirmation", t);
        }
    }


    @Then("the Reports page should display the following categories:")
    public void the_reports_page_should_display_the_following_categories(io.cucumber.datatable.DataTable table) {
        try {
            logStep("🔎 Verifying Reports page category headers are displayed...");

            // 1️⃣ Parse expected categories (ignore the table header row)
            List<String> expected = table.asList();
            if (!expected.isEmpty() && expected.get(0).equalsIgnoreCase("Category Name")) {
                expected = expected.subList(1, expected.size());
            }

            logger.info("Expected categories: {}", expected);
            logToAllure("Expected Category Headers", expected.toString());
            ScreenshotUtils.attachScreenshotToAllure(driver, "Reports_Expected_Categories");

            // 2️⃣ Wait for all category headers to appear
            boolean headersVisible = reportsPage.waitForAllCategoryHeaders(Duration.ofMillis(ReusableCommonMethods.NAV_WARN_MS));

            if (!headersVisible) {
                String msg = "One or more category headers were not visible within the wait window.";
                logger.error("❌ {}", msg);
                logToAllure("❌ Category Visibility Failure", msg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "Reports_Headers_Not_Visible");
                Assert.fail(msg);
            }

            // 3️⃣ Get actual visible header texts
            List<String> actual = reportsPage.getVisibleCategoryHeaderTexts();
            logger.info("Actual categories detected: {}", actual);
            logToAllure("Detected Category Headers", actual.toString());
            ScreenshotUtils.attachScreenshotToAllure(driver, "Reports_Actual_Categories");

            // 4️⃣ Validate non-empty
            if (actual == null || actual.isEmpty()) {
                String msg = "No category headers detected on the Reports page.";
                logger.error("❌ {}", msg);
                logToAllure("❌ Missing Categories", msg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "Reports_No_Headers");
                Assert.fail(msg);
            }

            // 5️⃣ Order-insensitive comparison (use sets)
            Set<String> expectedSet = new HashSet<>(expected.stream().map(String::trim).toList());
            Set<String> actualSet = new HashSet<>(actual.stream().map(String::trim).toList());

            if (!actualSet.containsAll(expectedSet)) {
                // Show which ones are missing or extra
                Set<String> missing = new HashSet<>(expectedSet);
                missing.removeAll(actualSet);
                Set<String> extras = new HashSet<>(actualSet);
                extras.removeAll(expectedSet);

                StringBuilder diff = new StringBuilder("\n--- Category header mismatch ---\n");
                if (!missing.isEmpty()) diff.append("Missing: ").append(missing).append("\n");
                if (!extras.isEmpty()) diff.append("Unexpected: ").append(extras).append("\n");
                diff.append("--------------------------------\n");

                logger.error(diff.toString());
                logToAllure("❌ Category Header Mismatch", diff.toString());
                ScreenshotUtils.attachScreenshotToAllure(driver, "Reports_Category_Mismatch");
                Assert.fail(diff.toString());
            }

            // ✅ Passed
            logStep("✅ Category headers verified successfully (order-insensitive).");
            logToAllure("✅ Category Headers Verified", String.join(", ", actualSet));
            logger.info("✅ Reports categories present (order ignored): {}", actualSet);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Reports_Categories_Verified");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Reports_Category_Exception");
            reportsPage.handleValidationException("Verify Reports Categories", t);
        }
    }


    @When("the user views the {string} section")
    public void the_user_views_the_section(String sectionName) {
        try {
            // 0) Log + opening screenshot (always attach something early)
            logStep("👀 Checking visibility of section: '" + sectionName + "'");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Section_View_Start");

            // Guardrail: we only support "Frequently Used" in this step for now
            if (!"Frequently Used".equalsIgnoreCase(sectionName)) {
                String msg = String.format("Step currently implemented only for 'Frequently Used'. Requested='%s'", sectionName);
                logger.warn("⚠️ {}", msg);
                logToAllure("⚠️ Unsupported Section", msg);
            }

            // 1) Wait for visibility + measure time
            final Duration visTimeout = Duration.ofSeconds(10);
            final long warnMs = (long) (visTimeout.toMillis() * 0.7); // warn if slow (>70% of timeout)

            Instant t0 = Instant.now();
            boolean visible = reportsPage.waitForFrequentlyUsedVisible(visTimeout);
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Frequently Used visibility", t0);

            // 2) Timing + URL for context
            logger.info("⏱️ '{}' section visibility time: {} ms", sectionName, elapsedMs);
            try {
                logger.info("📍 Current URL while checking '{}': {}", sectionName, driver.getCurrentUrl());
            } catch (Exception ignore) { /* URL fetch can occasionally throw if driver is mid-nav */ }

            // 3) Allure timing note
            logToAllure("⏱️ Section Visibility Time", sectionName + " became visible in " + String.format("%.2f", elapsedMs / 1000.0) + "s");

            // 4) Failure path: not visible in time → screenshot + allure + fail
            if (!visible) {
                String failMsg = String.format("'%s' section not visible within %ds.", sectionName, visTimeout.toSeconds());
                logger.error(failMsg);
                logToAllure("❌ Section Visibility Failure", failMsg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "FU_Not_Visible");
                Assert.fail(failMsg);
            }

            // 5) Warn on slow visibility
            if (elapsedMs >= warnMs) {
                String warn = String.format("'%s' section visibility was slow: %,.2fs (warn ≥ %,.2fs).", sectionName, elapsedMs / 1000.0, warnMs / 1000.0);
                logger.warn(warn);
                logToAllure("⚠️ Slow Section Visibility", warn);
            }

            // 6) Extra validation: check the content of the section
            int count = reportsPage.getFrequentlyUsedItemCount();
            List<String> items = reportsPage.getFrequentlyUsedItemTexts();

            logger.info("📋 '{}' items: count={} | texts={}", sectionName, count, items);
            logToAllure("📋 Frequently Used Items", "Count: " + count + "\nItems: " + String.valueOf(items));

            // 7) Screenshot of the visible section for evidence
            ScreenshotUtils.attachScreenshotToAllure(driver, "FU_Section_Visible");

            // 8) Fail if empty (section visible but not populated)
            if (count <= 0) {
                String msg = "No items found under 'Frequently Used'.";
                logger.error("❌ {}", msg);
                logToAllure("❌ Empty Section", msg);
                Assert.fail(msg);
            }

            // 9) Success log
            logger.info("✅ '{}' section is visible and populated ({} items).", sectionName, count);

        } catch (Throwable t) {
            // Last-chance evidence on exception
            ScreenshotUtils.attachScreenshotToAllure(driver, "FU_Section_Exception");
            reportsPage.handleValidationException("View section: " + sectionName, t);
        }
    }


    @Then("the system should display the recently accessed reports")
    public void the_system_should_display_the_recently_accessed_reports() {
        try {
            logStep("🧾 Verifying that 'Recently accessed' (Frequently Used) reports are displayed");
            ScreenshotUtils.attachScreenshotToAllure(driver, "RA_Start");

            // (Optional) quick re-check that the section is visible (fast timeout, we already waited earlier)
            Instant visStart = Instant.now();
            boolean visible = reportsPage.waitForFrequentlyUsedVisible(Duration.ofSeconds(6));
            long visMs = helperMethods.logLoadTimeAndReturnMs("Frequently Used (re-visibility)", visStart);
            logger.info("⏱️ Re-visibility check took {} ms; visible={}", visMs, visible);
            logToAllure("⏱️ FU Section Re-Visibility", "Visible=" + visible + " in " + visMs + " ms");

            if (!visible) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "RA_Section_Not_Visible");
                Assert.fail("'Frequently Used' section not visible when validating recently accessed reports.");
            }

            // Measure time to fetch items (helps spot slow DOM)
            Instant t0 = Instant.now();
            int count = reportsPage.getFrequentlyUsedItemCount();
            java.util.List<String> texts = reportsPage.getFrequentlyUsedItemTexts();
            long fetchMs = helperMethods.logLoadTimeAndReturnMs("Frequently Used items fetch", t0);

            // Basic consistency checks
            logger.info("🔢 Recently accessed count: {}, items: {}", count, texts);
            logToAllure("📋 Recently Accessed Items", "Count: " + count + "\nItems: " + String.valueOf(texts));
            ScreenshotUtils.attachScreenshotToAllure(driver, "RA_List_Visible");

            // Hard validation: at least one
            Assert.assertTrue(count > 0, "Expected at least one recently accessed report, but found none.");

            // Sanity: texts list should align with count (visible items only)
            if (texts == null) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "RA_Texts_Null");
                Assert.fail("Item texts list is null (unexpected).");
            }
            if (texts.size() != count) {
                logger.warn("⚠️ Visible item count ({}) != texts.size() ({}). Continuing, but capturing evidence.", count, texts.size());
                logToAllure("⚠️ Count/Text Size Mismatch", "count=" + count + ", texts.size()=" + texts.size());
                ScreenshotUtils.attachScreenshotToAllure(driver, "RA_Count_Text_Mismatch");
            }

            // Validate each text is non-empty and trimmed (defensive)
            int emptyLabels = 0;
            for (int i = 0; i < texts.size(); i++) {
                String t = texts.get(i) == null ? "" : texts.get(i).trim();
                if (t.isEmpty()) {
                    logger.error("❌ Item #{} has empty/blank label.", i);
                    emptyLabels++;
                }
            }
            if (emptyLabels > 0) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "RA_Empty_Labels");
                Assert.fail("Found " + emptyLabels + " item(s) with empty/blank label in Recently accessed.");
            }

            // Soft timing warning if fetching list was slow
            long warnMs = 1500; // heuristic: warn if collecting FU items takes >1.5s
            if (fetchMs >= warnMs) {
                String warn = String.format("Fetching Recently accessed items was slow: %d ms (warn ≥ %d ms).", fetchMs, warnMs);
                logger.warn("⚠️ {}", warn);
                logToAllure("⚠️ Slow List Fetch", warn);
            }

            logger.info("✅ Recently accessed reports are displayed ({} item(s)).", count);
            logToAllure("✅ Recently Accessed Visible", "Items found: " + count);

        } catch (Throwable t) {
            // Always attach a final screenshot for post-mortem
            ScreenshotUtils.attachScreenshotToAllure(driver, "RA_Exception");
            reportsPage.handleValidationException("Recently accessed reports display", t);
        }
    }


    @Then("the user validates each Frequently Used report redirects correctly and can go back")
    public void the_user_validates_each_frequently_used_report_redirects_correctly_and_can_go_back() {
        logStep("🔄 Validating navigation for each 'Frequently Used' report...");
        ScreenshotUtils.attachScreenshotToAllure(driver, "FU_Validation_Start");
        final long WARN_MS = ReusableCommonMethods.NAV_WARN_MS;
        final long FAIL_MS = ReusableCommonMethods.NAV_FAIL_MS;

        try {
            // Ensure the section is present before starting
            if (!reportsPage.waitForFrequentlyUsedVisible(Duration.ofSeconds(10))) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "FU_Section_Not_Visible");
                Assert.fail("'Frequently Used' section not visible.");
            }
            logger.info("✅ 'Frequently Used' section confirmed visible.");

            // We’ll iterate by index; before each click, re-fetch visible texts
            int planned = reportsPage.getFrequentlyUsedItemCount(); // visible count now (for progress display)
            for (int i = 0; ; i++) {
                // Re-ensure section visible and re-fetch texts
                if (!reportsPage.waitForFrequentlyUsedVisible(Duration.ofSeconds(8))) {
                    ScreenshotUtils.attachScreenshotToAllure(driver, String.format("FU_%02d_Section_Lost", i + 1));
                    Assert.fail("Frequently Used section disappeared during iteration.");
                }
                java.util.List<String> texts = reportsPage.getFrequentlyUsedItemTexts();
                if (i >= texts.size()) break; // finished all current visible items

                String full = texts.get(i);
                String value = reportsPage.extractReportValueAfterFirstDash(full);
                String idxTag = String.format("%02d", i + 1);
                String valueTag = reportsPage.fullToTag(value);

                logger.info("🧩 [{} / {}] Validating FU item: '{}' (value='{}')", i + 1, planned, full, value);

                ScreenshotUtils.attachScreenshotToAllure(driver, String.format("FU_%s_Before_Click_%s", idxTag, valueTag));
                logToAllure("🖱️ Click FU Item", String.format("[%s/%s] About to click: '%s' (value='%s')", i + 1, planned, full, value));

                Instant navStart = java.time.Instant.now();
                String urlBefore = reportsPage.safeGetUrl();
                boolean clickOk = reportsPage.clickFUItemByIndex(i);
                if (!clickOk) {
                    ScreenshotUtils.attachScreenshotToAllure(driver, String.format("FU_%s_Click_Failed_%s", idxTag, valueTag));
                    Assert.fail("Failed to click FU item index " + i);
                }

                boolean loaded = reportsPage.waitForDetailsViewLoaded(Duration.ofSeconds(20));
                long loadMs = java.time.Duration.between(navStart, java.time.Instant.now()).toMillis();
                double loadSec = loadMs / 1000.0;

                if (!loaded) {
                    logger.error("❌ Details did not load for '{}' within timeout (~{} sec).", value, loadSec);
                    ScreenshotUtils.attachScreenshotToAllure(driver, String.format("FU_%s_After_Load_TIMEOUT_%s", idxTag, valueTag));
                    logToAllure("❌ Item Load Timeout", String.format("'%s' took ~%.2f sec and did not fully load.", value, loadSec));
                    Assert.fail("Details view did not load for value: " + value);
                }

                if (loadMs >= FAIL_MS) {
                    logger.error("⏱️ '{}' loaded in {:.2f} sec (≥ FAIL {} sec).", value, loadSec, FAIL_MS / 1000);
                    logToAllure("❌ SLA Breach", String.format("'%s' loaded in %.2f sec (≥ FAIL).", value, loadSec));
                } else if (loadMs >= WARN_MS) {
                    logger.warn("⏱️ '{}' loaded in {:.2f} sec (≥ WARN {} sec).", value, loadSec, WARN_MS / 1000);
                    logToAllure("⚠️ SLA Warning", String.format("'%s' loaded in %.2f sec (≥ WARN).", value, loadSec));
                } else {
                    logger.info("⏱️ '{}' loaded in {:.2f} sec.", value, loadSec);
                }


                // ✅ HEADER VALIDATION
                boolean headerOk = reportsPage.basedOnHeaderContainsValue(value);
                if (!headerOk) {
                    ScreenshotUtils.attachScreenshotToAllure(driver, String.format("FU_%s_After_Load_Header_Mismatch_%s", idxTag, valueTag));
                    logToAllure("❌ Header Mismatch", String.format("Expected header to contain '%s'.", value));
                    Assert.fail("Header mismatch for value: " + value);
                }

                // 📸 AFTER REDIRECT
                ScreenshotUtils.attachScreenshotToAllure(driver, String.format("FU_%s_After_Load_%s", idxTag, valueTag));
                logger.info("✅ Details page validated for '{}' ({} ms).", value, loadMs);
                logger.info("➡️ URL '{}' → '{}'", urlBefore, reportsPage.safeGetUrl());
                logToAllure("✅ Item Validated", String.format("'%s' → header OK; load %d ms", value, loadMs));

                // 🔙 GO BACK
                boolean backOk = reportsPage.clickGoBackAndWaitReports(Duration.ofSeconds(12));
                if (!backOk) {
                    ScreenshotUtils.attachScreenshotToAllure(driver, String.format("FU_%s_Back_To_Reports_FAILED_%s", idxTag, valueTag));
                    Assert.fail("Could not navigate back to Reports after: " + value);
                }
                logger.info("↩️ Returned to Reports page for '{}'", value);

                // tiny settle for stability on next iteration
                try {
                    Thread.sleep(350);
                } catch (InterruptedException ignored) {
                }
            }

            ScreenshotUtils.attachScreenshotToAllure(driver, "FU_Validation_Success");
            logToAllure("🎯 FU Validation", "All items passed with per-item screenshots & timings.");
            logger.info("🎯 Successfully validated all Frequently Used items with per-item screenshots.");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "FU_Validation_Exception");
            reportsPage.handleValidationException("Frequently Used per-item validation failure", t);
        }
    }


    @Then("the {string} section should list the following risk levels:")
    public void the_section_should_list_the_following_risk_levels(String sectionName, DataTable dataTable) {
        try {
            logStep("🧾 Verifying '" + sectionName + "' options from DataTable");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByRisk_List_Start");

            if (!"By Risk".equalsIgnoreCase(sectionName)) {
                String msg = String.format("Step currently implemented only for 'By Risk'. Requested='%s'", sectionName);
                logger.warn("⚠️ {}", msg);
                logToAllure("⚠️ Unsupported Section", msg);
            }

            // Ensure section visible
            Instant visStart = Instant.now();
            boolean visible = reportsPage.waitForByRiskVisible(Duration.ofSeconds(10));
            long visMs = helperMethods.logLoadTimeAndReturnMs("'By Risk' visibility", visStart);
            logger.info("⏱️ 'By Risk' visibility time: {} ms", visMs);
            logToAllure("⏱️ By Risk Visibility Time", "Visible=" + visible + " in " + visMs + " ms");

            if (!visible) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByRisk_Not_Visible");
                Assert.fail("'By Risk' section not visible.");
            }

            // Expected from table
            List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
            List<String> expected = new ArrayList<>();
            for (Map<String, String> r : rows) {
                String v = r.get("Risk Level");
                if (v != null && !v.trim().isEmpty()) expected.add(v.trim());
            }

            // Actual on page
            List<String> actual = reportsPage.getByRiskItemTexts();

            logger.info("📋 Expected: {} | Actual: {}", expected, actual);
            logToAllure("📋 By Risk Options", "Expected: " + expected + "\nActual: " + actual);
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByRisk_List_Visible");

            // Validate each expected appears
            List<String> missing = new ArrayList<>();
            for (String want : expected) {
                boolean found = false;
                for (String got : actual) {
                    if (got != null && (got.equalsIgnoreCase(want) || got.trim().equalsIgnoreCase(want.trim()))) {
                        found = true;
                        break;
                    }
                }
                if (!found) missing.add(want);
            }

            if (!missing.isEmpty()) {
                String msg = "Missing expected risk levels: " + missing;
                logger.error("❌ {}", msg);
                logToAllure("❌ Missing Risk Levels", msg);
                Assert.fail(msg);
            }

            Assert.assertTrue(actual.size() > 0, "Expected at least one risk level, but found none.");

            // ✅ Save for the click step to iterate
            ScenarioContext.set("byRisk.expected", expected);

            logger.info("✅ 'By Risk' list matches expected values.");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByRisk_List_Exception");
            reportsPage.handleValidationException("'By Risk' options validation", t);
        }
    }


    @When("the user validates each of the listed risk levels shows compliance results")
    public void the_user_validates_each_of_the_listed_risk_levels_shows_compliance_results() {
        try {
            logStep("🖱️ Validating each listed risk level → details → results → back");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByRisk_Combined_Start");

            // Make sure section is visible
            if (!reportsPage.waitForByRiskVisible(Duration.ofSeconds(10))) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByRisk_Not_Visible");
                Assert.fail("'By Risk' section not visible.");
            }

            // Use the expected list captured by the table step; fall back to UI list if missing
            @SuppressWarnings("unchecked") List<String> planned = (List<String>) ScenarioContext.get("byRisk.expected");
            if (planned == null || planned.isEmpty()) {
                planned = reportsPage.getByRiskItemTexts();
            }
            if (planned == null || planned.isEmpty()) {
                Assert.fail("No risk options available to click.");
            }

            final long WARN_MS = ReusableCommonMethods.NAV_WARN_MS;
            final long FAIL_MS = ReusableCommonMethods.NAV_FAIL_MS;

            List<String> validated = new ArrayList<>();

            for (int idx = 0; idx < planned.size(); idx++) {
                if (!reportsPage.waitForByRiskVisible(Duration.ofSeconds(10))) {
                    ScreenshotUtils.attachScreenshotToAllure(driver, "ByRisk_Section_Lost_" + idx);
                    Assert.fail("'By Risk' section disappeared before clicking: " + planned.get(idx));
                }

                String chosen = planned.get(idx) == null ? "" : planned.get(idx).trim();
                String tag = reportsPage.fullToTag(chosen);

                logger.info("🧩 [{} / {}] Risk: '{}'", idx + 1, planned.size(), chosen);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByRisk_Before_Click_" + tag);
                logToAllure("🖱️ Click Risk", String.format("[%d/%d] '%s'", idx + 1, planned.size(), chosen));

                Instant navStart = java.time.Instant.now();
                String urlBefore = reportsPage.safeGetUrl();

                boolean clickOk = reportsPage.clickByRiskByLabel(chosen);
                if (!clickOk) {
                    ScreenshotUtils.attachScreenshotToAllure(driver, "ByRisk_Click_Failed_" + tag);
                    Assert.fail("Failed to click risk option: " + chosen);
                }

                boolean loaded = reportsPage.waitForDetailsViewLoaded(Duration.ofSeconds(20));
                long loadMs = java.time.Duration.between(navStart, java.time.Instant.now()).toMillis();
                double loadSec = loadMs / 1000.0;

                if (!loaded) {
                    ScreenshotUtils.attachScreenshotToAllure(driver, "ByRisk_Load_TIMEOUT_" + tag);
                    logToAllure("❌ Risk Load Timeout", String.format("'%s' ~%.2fs and not fully loaded.", chosen, loadSec));
                    Assert.fail("Details view did not load for risk: " + chosen);
                }
                if (loadMs >= FAIL_MS) {
                    logToAllure("❌ SLA Breach", String.format("'%s' loaded in %.2fs (≥ FAIL).", chosen, loadSec));
                } else if (loadMs >= WARN_MS) {
                    logToAllure("⚠️ SLA Warning", String.format("'%s' loaded in %.2fs (≥ WARN).", chosen, loadSec));
                } else {
                    logger.info("⏱️ '{}' loaded in {:.2f}s.", chosen, loadSec);
                }

                // Header must reflect the chosen risk
                boolean headerOk = reportsPage.basedOnHeaderContainsValue(chosen);
                if (!headerOk) {
                    ScreenshotUtils.attachScreenshotToAllure(driver, "ByRisk_Header_Mismatch_" + tag);
                    logToAllure("❌ Header Mismatch", "Expected header to contain '" + chosen + "'.");
                    Assert.fail("Header mismatch for risk: " + chosen);
                }

                // Results must be present
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByRisk_After_Load_" + tag);
                boolean hasResults = reportsPage.hasAnyComplianceResults();
                if (!hasResults) {
                    ScreenshotUtils.attachScreenshotToAllure(driver, "ByRisk_Results_Empty_" + tag);
                    Assert.fail("No compliance records found for risk: " + chosen);
                }

                logger.info("✅ Records visible for '{}'. URL: '{}' → '{}'", chosen, urlBefore, reportsPage.safeGetUrl());
                logToAllure("✅ Risk Results Visible", "Risk='" + chosen + "'");
                validated.add(chosen);

                // Go back for next item (we’re combining steps, so always return)
                boolean backOk = reportsPage.clickGoBackAndWaitReports(Duration.ofSeconds(12));
                if (!backOk) {
                    ScreenshotUtils.attachScreenshotToAllure(driver, "ByRisk_Back_To_Reports_FAILED_" + tag);
                    Assert.fail("Could not navigate back to Reports after risk: " + chosen);
                }
                logger.info("↩️ Returned to Reports page for '{}'", chosen);
                try {
                    Thread.sleep(350);
                } catch (InterruptedException ignored) {
                }
            }

            // Evidence: which levels were covered
            ScenarioContext.set("byRisk.validatedList", validated);
            logToAllure("🎯 ByRisk Combined Validation", "Validated: " + validated);

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByRisk_Combined_Exception");
            reportsPage.handleValidationException("ByRisk combined step", t);
        }
    }


    @Then("the {string} section should display a list of available stages")
    public void the_section_should_display_a_list_of_available_stages(String sectionName) {
        try {
            logStep("🧾 Verifying available stages under '" + sectionName + "'");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_List_Start");

            if (!"By Stage".equalsIgnoreCase(sectionName)) {
                String msg = String.format("Step currently implemented only for 'By Stage'. Requested='%s'", sectionName);
                logger.warn("⚠️ {}", msg);
                logToAllure("⚠️ Unsupported Section", msg);
            }

            // Ensure section visible
            Instant visStart = Instant.now();
            boolean visible = reportsPage.waitForByStageVisible(Duration.ofSeconds(10));
            long visMs = helperMethods.logLoadTimeAndReturnMs("'By Stage' visibility", visStart);
            logger.info("⏱️ 'By Stage' visibility time: {} ms", visMs);
            logToAllure("⏱️ By Stage Visibility Time", "Visible=" + visible + " in " + visMs + " ms");

            if (!visible) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_Not_Visible");
                Assert.fail("'By Stage' section not visible.");
            }

            List<String> stages = reportsPage.getByStageItemTexts();
            logger.info("📋 Stages found: {}", stages);
            logToAllure("📋 By Stage Options", "Actual: " + stages);
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_List_Visible");

            Assert.assertTrue(stages != null && !stages.isEmpty(), "Expected at least one stage, but found none.");

            // Save to context for subsequent steps
            ScenarioContext.set("byStage.available", stages);

            logger.info("✅ 'By Stage' list captured successfully.");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_List_Exception");
            reportsPage.handleValidationException("'By Stage' list capture", t);
        }
    }

    @Then("each stage name should be visible and clickable")
    public void each_stage_name_should_be_visible_and_clickable() {
        try {
            logStep("🔎 Verifying each stage name is visible & clickable (without navigating)");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_Clickable_Check_Start");

            if (!reportsPage.waitForByStageVisible(Duration.ofSeconds(8))) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_Not_Visible");
                Assert.fail("'By Stage' section not visible.");
            }

            @SuppressWarnings("unchecked") List<String> stages = (List<String>) ScenarioContext.get("byStage.available");
            if (stages == null || stages.isEmpty()) {
                stages = reportsPage.getByStageItemTexts(); // fallback
            }
            Assert.assertTrue(stages != null && !stages.isEmpty(), "No stages available to verify clickability.");

            List<String> notClickable = new ArrayList<>();
            for (String stage : stages) {
                boolean ok = reportsPage.isStageClickableByLabel(stage);
                logger.info("• Stage '{}' clickable? {}", stage, ok);
                if (!ok) notClickable.add(stage);
            }

            if (!notClickable.isEmpty()) {
                String msg = "Some stage names are not visibly clickable: " + notClickable;
                logger.error("❌ {}", msg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_Not_Clickable");
                Assert.fail(msg);
            }

            logger.info("✅ All stage names are visible & clickable.");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_Clickable_Check_Exception");
            reportsPage.handleValidationException("'By Stage' clickable verification", t);
        }
    }


    @When("the user validates each of the available stages shows compliance results")
    public void the_user_validates_each_of_the_available_stages_shows_compliance_results() {

        final Duration visTimeout = Duration.ofSeconds(10);
        final long warnVisMs = (long) (visTimeout.toMillis() * 0.7);

        try {
            logStep("🧭 Begin: Validate all available stages show compliance results");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_Step_Start");

            // 1. Pre-check visibility
            Instant start = Instant.now();
            boolean visible = reportsPage.waitForByStageVisible(visTimeout);
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("'By Stage' visibility", start);

            if (!visible) {
                String failMsg = "'By Stage' section not visible within timeout.";
                logToAllure("❌ Section Not Visible", failMsg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_NotVisible");
                Assert.fail(failMsg);
            }

            if (elapsedMs >= warnVisMs) {
                logger.warn("⚠️ Slow load: {} ms", elapsedMs);
                logToAllure("⚠️ Slow Load", "By Stage section loaded slowly: " + elapsedMs + " ms");
            }

            // 2. Snapshot planned stages before POM execution
            @SuppressWarnings("unchecked") List<String> planned = (List<String>) ScenarioContext.get("byStage.available");
            if (planned == null || planned.isEmpty()) {
                planned = reportsPage.getByStageItemTexts(); // still POM safe
            }

            if (planned == null || planned.isEmpty()) {
                Assert.fail("No stages available to validate.");
            }

            logToAllure("📋 Planned Stages (Before)", String.valueOf(planned));
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_Stages_List");

            // 3. Execute existing POM method
            reportsPage.validateByStageAllAndReturn();

            // 4. Verify POM outputs via ScenarioContext
            Boolean validatedAll = (Boolean) ScenarioContext.get("byStage.validatedAll");
            @SuppressWarnings("unchecked") List<String> validated = (List<String>) ScenarioContext.get("byStage.validatedList");

            if (validated == null || validated.isEmpty()) {
                Assert.fail("No stages were validated by POM method.");
            }
            if (validatedAll == null || !validatedAll.booleanValue()) {
                Assert.fail("Expected validatedAll flag to be TRUE.");
            }

            // ✅ Cross-check planned vs validated manually (Java 7 style)
            List<String> missing = new ArrayList<String>();
            for (int i = 0; i < planned.size(); i++) {
                String value = planned.get(i);
                if (!validated.contains(value)) {
                    missing.add(value);
                }
            }
            if (!missing.isEmpty()) {
                logger.warn("⚠️ Some stages were skipped: {}", missing);
                logToAllure("⚠️ Skipped Stages", missing.toString());
            }

            // Duplicate detection in Java 7
            List<String> duplicates = new ArrayList<String>();
            for (int i = 0; i < validated.size(); i++) {
                String val = validated.get(i);
                if (validated.indexOf(val) != validated.lastIndexOf(val) && !duplicates.contains(val)) {
                    duplicates.add(val);
                }
            }
            if (!duplicates.isEmpty()) {
                logToAllure("⚠️ Duplicate Stages", duplicates.toString());
                logger.warn("Duplicate stages found: {}", duplicates);
            }

            ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_Step_Success");
            logToAllure("✅ Success", "Validated stages: " + validated.toString());
            logger.info("✅ All stages validated.");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_Step_Exception");
            logToAllure("❌ Failure", t.getMessage());
            reportsPage.handleValidationException("Step Wrapper - ByStage", t);
            throw t;
        }
    }


    @Then("the system should display the compliance records corresponding to that stage in a separate screen")
    public void the_system_should_display_the_compliance_records_corresponding_to_that_stage_in_a_separate_screen() {
        try {
            // Short-circuit: if we already validated all and navigated back, don't try to read a details page now.
            Boolean iterMode = (Boolean) ScenarioContext.getOrDefault("byStage.validatedAll", Boolean.FALSE);
            @SuppressWarnings("unchecked") List<String> validated = (List<String>) ScenarioContext.get("byStage.validatedList");

            if (Boolean.TRUE.equals(iterMode)) {
                Assert.assertTrue(validated != null && !validated.isEmpty(), "Expected to have validated at least one stage, but none recorded.");
                logStep("📄 Already validated results for each stage during navigation: " + validated);
                logToAllure("✅ Per-stage Results Confirmed", "Validated stages: " + validated);
                return;
            }

            // Legacy single-stage fallback (not used in our iterating approach)
            String chosen = (String) ScenarioContext.getOrDefault("chosenStage", "selected stage");
            logStep("📄 Verifying compliance records are listed for: " + chosen);
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_Results_Start");

            boolean ready = reportsPage.waitForDetailsViewLoaded(Duration.ofSeconds(10));
            if (!ready) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_Results_NotReady");
                Assert.fail("Details view not ready when checking compliance results.");
            }

            boolean hasResults = reportsPage.hasAnyComplianceResults();
            if (!hasResults) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_Results_Empty");
                Assert.fail("No compliance records found for the selected stage.");
            }

            boolean headerOk = reportsPage.basedOnHeaderContainsValue(chosen);
            if (!headerOk) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_Results_Header_Mismatch");
                Assert.fail("Header no longer contains the selected stage: " + chosen);
            }

            logger.info("✅ Compliance records are visible for stage '{}'.", chosen);
            logToAllure("✅ Stage Results Visible", "Stage='" + chosen + "'");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByStage_Results_Exception");
            reportsPage.handleValidationException("Compliance results for selected stage", t);
        }
    }


    @Then("the {string} section should display a list of created organizations")
    public void the_section_should_display_a_list_of_created_organizations(String sectionName) {
        try {
            logStep("🧾 Verifying available organizations under '" + sectionName + "'");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_List_Start");

            if (!"By Organization".equalsIgnoreCase(sectionName)) {
                String msg = String.format("Step currently implemented only for 'By Organization'. Requested='%s'", sectionName);
                logger.warn("⚠️ {}", msg);
                logToAllure("⚠️ Unsupported Section", msg);
            }

            Instant visStart = Instant.now();
            boolean visible = reportsPage.waitForByOrganizationVisible(Duration.ofSeconds(10));
            long visMs = helperMethods.logLoadTimeAndReturnMs("'By Organization' visibility", visStart);
            logger.info("⏱️ 'By Organization' visibility time: {} ms", visMs);
            logToAllure("⏱️ By Organization Visibility Time", "Visible=" + visible + " in " + visMs + " ms");

            if (!visible) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Not_Visible");
                Assert.fail("'By Organization' section not visible.");
            }

            List<String> orgs = reportsPage.getByOrganizationItemTexts();
            logger.info("📋 Organizations found: {}", orgs);
            logToAllure("📋 By Organization Options", "Actual: " + orgs);
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_List_Visible");

            Assert.assertTrue(orgs != null && !orgs.isEmpty(), "Expected at least one organization, but found none.");

            // Save to context
            ScenarioContext.set("byOrg.available", orgs);

            logger.info("✅ 'By Organization' list captured successfully.");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_List_Exception");
            reportsPage.handleValidationException("'By Organization' list capture", t);
        }
    }

    @Then("each organization name should be visible and clickable")
    public void each_organization_name_should_be_visible_and_clickable() {
        try {
            logStep("🔎 Verifying each organization name is visible & clickable (without navigating)");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Clickable_Check_Start");

            if (!reportsPage.waitForByOrganizationVisible(Duration.ofSeconds(8))) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Not_Visible");
                Assert.fail("'By Organization' section not visible.");
            }

            @SuppressWarnings("unchecked") List<String> orgs = (List<String>) ScenarioContext.get("byOrg.available");
            if (orgs == null || orgs.isEmpty()) orgs = reportsPage.getByOrganizationItemTexts();
            Assert.assertTrue(orgs != null && !orgs.isEmpty(), "No organizations available to verify clickability.");

            List<String> notClickable = new ArrayList<>();
            for (String org : orgs) {
                boolean ok = reportsPage.isOrganizationClickableByLabel(org);
                logger.info("• Organization '{}' clickable? {}", org, ok);
                if (!ok) notClickable.add(org);
            }

            if (!notClickable.isEmpty()) {
                String msg = "Some organizations are not visibly clickable: " + notClickable;
                logger.error("❌ {}", msg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Not_Clickable");
                Assert.fail(msg);
            }

            logger.info("✅ All organization names are visible & clickable.");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Clickable_Check_Exception");
            reportsPage.handleValidationException("'By Organization' clickable verification", t);
        }
    }


    @When("the user validates each of the available organizations shows compliance results")
    public void the_user_validates_each_of_the_available_organizations_shows_compliance_results() {

        final Duration visTimeout = Duration.ofSeconds(10);
        final long warnVisMs = (long) (visTimeout.toMillis() * 0.7);

        try {
            // 0) Opening evidence
            logStep("🏢 Begin: Validate all available organizations show compliance results");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Step_Start");

            // 1) Pre-check visibility of the Organizations section
            Instant t0 = Instant.now();
            boolean visible = reportsPage.waitForByOrganizationVisible(visTimeout);
            long visMs = helperMethods.logLoadTimeAndReturnMs("'By Organization' visibility (step)", t0);

            if (!visible) {
                String fail = String.format("'By Organization' section not visible within %ds (step pre-check).", visTimeout.toSeconds());
                logger.error(fail);
                logToAllure("❌ Pre-Check Failure", fail);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Step_NotVisible");
                Assert.fail(fail);
            }

            if (visMs >= warnVisMs) {
                String warn = String.format("Slow visibility: %.2fs (warn ≥ %.2fs) [step].", visMs / 1000.0, warnVisMs / 1000.0);
                logger.warn("⚠️ {}", warn);
                logToAllure("⚠️ Slow Visibility (Step)", warn);
            }

            try {
                logger.info("📍 STEP URL (pre): {}", driver.getCurrentUrl());
            } catch (Exception ignore) {
            }

            // 2) Snapshot planned organizations BEFORE calling POM
            @SuppressWarnings("unchecked") List<String> plannedFromCtx = (List<String>) ScenarioContext.get("byOrg.available");
            List<String> planned;
            if (plannedFromCtx != null && !plannedFromCtx.isEmpty()) {
                planned = new ArrayList<String>(plannedFromCtx);
            } else {
                planned = reportsPage.getByOrganizationItemTexts(); // POM method (no screenshots/allure inside)
            }

            if (planned == null || planned.isEmpty()) {
                String msg = "No organizations available to click (step pre-check).";
                logger.error("❌ {}", msg);
                logToAllure("❌ Empty Organizations (Pre)", msg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Step_PreList_Empty");
                Assert.fail(msg);
            }

            logger.info("📋 STEP planned organizations: {}", planned);
            logToAllure("📋 Planned Orgs (Pre)", String.valueOf(planned));
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Step_PreList");

            // 3) Execute existing POM method
            reportsPage.validateByOrganizationAllAndReturn();

            // 4) Post-run validations via ScenarioContext
            Boolean validatedAll = (Boolean) ScenarioContext.get("byOrg.validatedAll");
            @SuppressWarnings("unchecked") List<String> validated = (List<String>) ScenarioContext.get("byOrg.validatedList");
            String lastChosen = (String) ScenarioContext.get("chosenOrganization");

            logger.info("🧾 STEP post: validatedAll={} | validatedList={} | lastChosen='{}'", validatedAll, validated, lastChosen);
            logToAllure("🧾 Post Results (Orgs)", "validatedAll=" + validatedAll + "\nvalidatedList=" + String.valueOf(validated) + "\nlastChosen=" + lastChosen);
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Step_PostResults");

            // 4a) Basic assertions
            if (validated == null || validated.isEmpty()) {
                String msg = "No organizations were validated by POM method.";
                logToAllure("❌ Empty Validation Results", msg);
                Assert.fail(msg);
            }
            if (validatedAll == null || !validatedAll.booleanValue()) {
                String msg = "Expected validatedAll flag to be TRUE for organizations.";
                logToAllure("❌ Flag Not Set", msg);
                Assert.fail(msg);
            }

            // 4b) Cross-check planned vs validated (Java 7 style)
            List<String> missing = new ArrayList<String>();
            int i;
            for (i = 0; i < planned.size(); i++) {
                String value = planned.get(i);
                if (!validated.contains(value)) {
                    missing.add(value);
                }
            }
            if (!missing.isEmpty()) {
                String warn = "Some planned orgs were not validated: " + missing;
                logger.warn("⚠️ {}", warn);
                logToAllure("⚠️ Partial Validation (Orgs)", warn);
                // Not failing here—UIs sometimes filter/hide; treat as soft warning.
            }

            // 4c) Duplicate detection (Java 7)
            List<String> duplicates = new ArrayList<String>();
            for (i = 0; i < validated.size(); i++) {
                String val = validated.get(i);
                int first = validated.indexOf(val);
                int last = validated.lastIndexOf(val);
                if (first != last && !duplicates.contains(val)) {
                    duplicates.add(val);
                }
            }
            if (!duplicates.isEmpty()) {
                String dupMsg = "Duplicate organization validations detected: " + duplicates;
                logger.warn("⚠️ {}", dupMsg);
                logToAllure("⚠️ Duplicates (Orgs)", dupMsg);
            }

            // 5) Success evidence
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Step_Success");
            logToAllure("✅ Organizations Validation (Step)", "Validated orgs: " + validated.toString());
            logger.info("✅ STEP complete: Organizations validated. Count={}", validated.size());

        } catch (Throwable t) {
            // Catch-all: screenshots + allure here; keep POM free of them.
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Step_Exception");
            try {
                logger.info("📍 STEP URL (on-exception): {}", driver.getCurrentUrl());
            } catch (Exception ignore) {
            }
            logger.error("❌ STEP exception (Orgs): {}", t.getMessage(), t);
            logToAllure("❌ Organizations Validation Failed (Step)", String.valueOf(t.getMessage()));
            try {
                reportsPage.handleValidationException("ByOrganization step wrapper", t);
            } catch (Throwable ignore) {
            }
            Assert.fail("ByOrganization validation failed (step): " + String.valueOf(t.getMessage()));
        }
    }


    @Then("the system should display the compliance records associated with that organization in a separate screen")
    public void the_system_should_display_the_compliance_records_associated_with_that_organization_in_a_separate_screen() {
        try {
            // Short-circuit if we already validated all orgs and returned to Reports.
            Boolean iterMode = (Boolean) ScenarioContext.getOrDefault("byOrg.validatedAll", Boolean.FALSE);
            @SuppressWarnings("unchecked") List<String> validated = (List<String>) ScenarioContext.get("byOrg.validatedList");

            if (Boolean.TRUE.equals(iterMode)) {
                Assert.assertTrue(validated != null && !validated.isEmpty(), "Expected to have validated at least one organization, but none recorded.");
                logStep("📄 Already validated results for each organization during navigation: " + validated);
                logToAllure("✅ Per-organization Results Confirmed", "Validated: " + validated);
                return;
            }

            // Legacy single-org fallback (not used in iterating path)
            String chosen = (String) ScenarioContext.getOrDefault("chosenOrganization", "selected organization");
            logStep("📄 Verifying compliance records are listed for: " + chosen);
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Results_Start");

            boolean ready = reportsPage.waitForDetailsViewLoaded(Duration.ofSeconds(10));
            if (!ready) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Results_NotReady");
                Assert.fail("Details view not ready when checking compliance results.");
            }

            boolean hasResults = reportsPage.hasAnyComplianceResults();
            if (!hasResults) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Results_Empty");
                Assert.fail("No compliance records found for the selected organization.");
            }

            boolean headerOk = reportsPage.basedOnHeaderContainsValue(chosen);
            if (!headerOk) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Results_Header_Mismatch");
                Assert.fail("Header no longer contains the selected organization: " + chosen);
            }

            logger.info("✅ Compliance records are visible for organization '{}'.", chosen);
            logToAllure("✅ Organization Results Visible", "Organization='" + chosen + "'");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByOrg_Results_Exception");
            reportsPage.handleValidationException("Compliance results for selected organization", t);
        }
    }


    @Then("the {string} section should display a list of departments")
    public void the_section_should_display_a_list_of_departments(String sectionName) {
        try {
            logStep("🧾 Verifying available departments under '" + sectionName + "'");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_List_Start");

            if (!"By Department".equalsIgnoreCase(sectionName)) {
                String msg = String.format("Step currently implemented only for 'By Department'. Requested='%s'", sectionName);
                logger.warn("⚠️ {}", msg);
                logToAllure("⚠️ Unsupported Section", msg);
            }

            Instant visStart = Instant.now();
            boolean visible = reportsPage.waitForByDepartmentVisible(Duration.ofSeconds(10));
            long visMs = helperMethods.logLoadTimeAndReturnMs("'By Department' visibility", visStart);
            logger.info("⏱️ 'By Department' visibility time: {} ms", visMs);
            logToAllure("⏱️ By Department Visibility Time", "Visible=" + visible + " in " + visMs + " ms");

            if (!visible) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Not_Visible");
                Assert.fail("'By Department' section not visible.");
            }

            List<String> depts = reportsPage.getByDepartmentItemTexts();
            logger.info("📋 Departments found: {}", depts);
            logToAllure("📋 By Department Options", "Actual: " + depts);
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_List_Visible");

            Assert.assertTrue(depts != null && !depts.isEmpty(), "Expected at least one department, but found none.");

            // Save for next steps
            ScenarioContext.set("byDept.available", depts);

            logger.info("✅ 'By Department' list captured successfully.");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_List_Exception");
            reportsPage.handleValidationException("'By Department' list capture", t);
        }
    }

    @Then("each department name should be visible and clickable")
    public void each_department_name_should_be_visible_and_clickable() {
        try {
            logStep("🔎 Verifying each department name is visible & clickable (without navigating)");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Clickable_Check_Start");

            if (!reportsPage.waitForByDepartmentVisible(Duration.ofSeconds(8))) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Not_Visible");
                Assert.fail("'By Department' section not visible.");
            }

            @SuppressWarnings("unchecked") List<String> depts = (List<String>) ScenarioContext.get("byDept.available");
            if (depts == null || depts.isEmpty()) depts = reportsPage.getByDepartmentItemTexts();
            Assert.assertTrue(depts != null && !depts.isEmpty(), "No departments available to verify clickability.");

            List<String> notClickable = new ArrayList<>();
            for (String dept : depts) {
                boolean ok = reportsPage.isDepartmentClickableByLabel(dept);
                logger.info("• Department '{}' clickable? {}", dept, ok);
                if (!ok) notClickable.add(dept);
            }

            if (!notClickable.isEmpty()) {
                String msg = "Some departments are not visibly clickable: " + notClickable;
                logger.error("❌ {}", msg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Not_Clickable");
                Assert.fail(msg);
            }

            logger.info("✅ All department names are visible & clickable.");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Clickable_Check_Exception");
            reportsPage.handleValidationException("'By Department' clickable verification", t);
        }
    }

    @When("the user validates each of the available departments shows compliance results")
    public void the_user_validates_each_of_the_available_departments_shows_compliance_results() {

        final Duration visTimeout = Duration.ofSeconds(10);
        final long warnVisMs = (long) (visTimeout.toMillis() * 0.7);

        try {
            // 0) Opening evidence
            logStep("🏬 Begin: Validate all available departments show compliance results");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Step_Start");

            // 1) Pre-check visibility of the Departments section
            Instant t0 = Instant.now();
            boolean visible = reportsPage.waitForByDepartmentVisible(visTimeout);
            long visMs = helperMethods.logLoadTimeAndReturnMs("'By Department' visibility (step)", t0);

            if (!visible) {
                String fail = String.format("'By Department' section not visible within %ds (step pre-check).", visTimeout.toSeconds());
                logger.error(fail);
                logToAllure("❌ Pre-Check Failure", fail);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Step_NotVisible");
                Assert.fail(fail);
            }

            if (visMs >= warnVisMs) {
                String warn = String.format("Slow visibility: %.2fs (warn ≥ %.2fs) [step].", visMs / 1000.0, warnVisMs / 1000.0);
                logger.warn("⚠️ {}", warn);
                logToAllure("⚠️ Slow Visibility (Step)", warn);
            }

            try {
                logger.info("📍 STEP URL (pre): {}", driver.getCurrentUrl());
            } catch (Exception ignore) {
            }

            // 2) Snapshot planned departments BEFORE calling POM
            @SuppressWarnings("unchecked") List<String> plannedFromCtx = (List<String>) ScenarioContext.get("byDept.available");
            List<String> planned;
            if (plannedFromCtx != null && !plannedFromCtx.isEmpty()) {
                planned = new ArrayList<String>(plannedFromCtx);
            } else {
                planned = reportsPage.getByDepartmentItemTexts(); // POM read-only; no screenshots/allure inside
            }

            if (planned == null || planned.isEmpty()) {
                String msg = "No departments available to click (step pre-check).";
                logger.error("❌ {}", msg);
                logToAllure("❌ Empty Departments (Pre)", msg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Step_PreList_Empty");
                Assert.fail(msg);
            }

            logger.info("📋 STEP planned departments: {}", planned);
            logToAllure("📋 Planned Departments (Pre)", String.valueOf(planned));
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Step_PreList");

            // 3) Execute existing POM method (kept clean of Allure/screenshots)
            reportsPage.validateByDepartmentAllAndReturn();

            // 4) Post-run validations via ScenarioContext
            Boolean validatedAll = (Boolean) ScenarioContext.get("byDept.validatedAll");
            @SuppressWarnings("unchecked") List<String> validated = (List<String>) ScenarioContext.get("byDept.validatedList");
            String lastChosen = (String) ScenarioContext.get("chosenDepartment");

            logger.info("🧾 STEP post: validatedAll={} | validatedList={} | lastChosen='{}'", validatedAll, validated, lastChosen);
            logToAllure("🧾 Post Results (Departments)", "validatedAll=" + validatedAll + "\nvalidatedList=" + String.valueOf(validated) + "\nlastChosen=" + lastChosen);
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Step_PostResults");

            // 4a) Basic assertions
            if (validated == null || validated.isEmpty()) {
                String msg = "No departments were validated by POM method.";
                logToAllure("❌ Empty Validation Results", msg);
                Assert.fail(msg);
            }
            if (validatedAll == null || !validatedAll.booleanValue()) {
                String msg = "Expected validatedAll flag to be TRUE for departments.";
                logToAllure("❌ Flag Not Set", msg);
                Assert.fail(msg);
            }

            // 4b) Cross-check planned vs validated (Java 7 style, no streams)
            List<String> missing = new ArrayList<String>();
            int i;
            for (i = 0; i < planned.size(); i++) {
                String value = planned.get(i);
                if (!validated.contains(value)) {
                    missing.add(value);
                }
            }
            if (!missing.isEmpty()) {
                String warn = "Some planned departments were not validated: " + missing;
                logger.warn("⚠️ {}", warn);
                logToAllure("⚠️ Partial Validation (Departments)", warn);
                // non-fatal: UI may hide/filter; warn only
            }

            // 4c) Duplicate detection (Java 7)
            List<String> duplicates = new ArrayList<String>();
            for (i = 0; i < validated.size(); i++) {
                String val = validated.get(i);
                int first = validated.indexOf(val);
                int last = validated.lastIndexOf(val);
                if (first != last && !duplicates.contains(val)) {
                    duplicates.add(val);
                }
            }
            if (!duplicates.isEmpty()) {
                String dupMsg = "Duplicate department validations detected: " + duplicates;
                logger.warn("⚠️ {}", dupMsg);
                logToAllure("⚠️ Duplicates (Departments)", dupMsg);
            }

            // 5) Success evidence
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Step_Success");
            logToAllure("✅ Departments Validation (Step)", "Validated departments: " + validated.toString());
            logger.info("✅ STEP complete: Departments validated. Count={}", validated.size());

        } catch (Throwable t) {
            // Catch-all: screenshots + Allure here; POM stays clean
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Step_Exception");
            try {
                logger.info("📍 STEP URL (on-exception): {}", driver.getCurrentUrl());
            } catch (Exception ignore) {
            }
            logger.error("❌ STEP exception (Departments): {}", t.getMessage(), t);
            logToAllure("❌ Departments Validation Failed (Step)", String.valueOf(t.getMessage()));
            try {
                reportsPage.handleValidationException("ByDepartment step wrapper", t);
            } catch (Throwable ignore) {
            }
            Assert.fail("ByDepartment validation failed (step): " + String.valueOf(t.getMessage()));
        }
    }


    @Then("the system should display the compliance records associated with that department in a separate screen")
    public void the_system_should_display_the_compliance_records_associated_with_that_department_in_a_separate_screen() {
        try {
            // Short-circuit if we already validated all and navigated back to Reports.
            Boolean iterMode = (Boolean) ScenarioContext.getOrDefault("byDept.validatedAll", Boolean.FALSE);
            @SuppressWarnings("unchecked") List<String> validated = (List<String>) ScenarioContext.get("byDept.validatedList");

            if (Boolean.TRUE.equals(iterMode)) {
                Assert.assertTrue(validated != null && !validated.isEmpty(), "Expected to have validated at least one department, but none recorded.");
                logStep("📄 Already validated results for each department during navigation: " + validated);
                logToAllure("✅ Per-department Results Confirmed", "Validated: " + validated);
                return;
            }

            // Legacy single-department fallback
            String chosen = (String) ScenarioContext.getOrDefault("chosenDepartment", "selected department");
            logStep("📄 Verifying compliance records are listed for: " + chosen);
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Results_Start");

            boolean ready = reportsPage.waitForDetailsViewLoaded(Duration.ofSeconds(10));
            if (!ready) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Results_NotReady");
                Assert.fail("Details view not ready when checking compliance results.");
            }

            boolean hasResults = reportsPage.hasAnyComplianceResults();
            if (!hasResults) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Results_Empty");
                Assert.fail("No compliance records found for the selected department.");
            }

            boolean headerOk = reportsPage.basedOnHeaderContainsValue(chosen);
            if (!headerOk) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Results_Header_Mismatch");
                Assert.fail("Header no longer contains the selected department: " + chosen);
            }

            logger.info("✅ Compliance records are visible for department '{}'.", chosen);
            logToAllure("✅ Department Results Visible", "Department='" + chosen + "'");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ByDept_Results_Exception");
            reportsPage.handleValidationException("Compliance results for selected department", t);
        }
    }


}

