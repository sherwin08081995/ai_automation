package stepDefinitions;


import hooks.Hooks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.asserts.SoftAssert;
import pages.CompliancePage;
import supportingclass.CheckboxValidationResult;
import utils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;


import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static utils.AllureLoggerUtils.logToAllure;

/**
 * @author Sherwin
 * @since 16-07-2025
 */

public class CompliancePageValidationSteps {


    WebDriver driver = Hooks.driver;
    CompliancePage compliancePage;
    Logger logger;
    WebDriverWait wait;
    AllureLoggerUtils allureLogging;
    ReusableCommonMethods helperMethods;
    private List<String> archivedRecordsBeforeUnarchive;
    private Path lastDownloadedCsvPath;


    private static final long PANEL_WAIT_MS = 12000L;
    private static final int DUE_DATE_MAX_AHEAD_DAYS = 20;
    private String lastSeenGridSignature = "";           // snapshot of grid for reload detection
    private List<String> expectedItems = new ArrayList<String>();
    private String currentCategory = null;


    public CompliancePageValidationSteps() {
        this.driver = Hooks.driver;
        this.compliancePage = new CompliancePage(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.logger = LoggerUtils.getLogger(getClass());
        this.helperMethods = new ReusableCommonMethods(driver);
    }

    // at class level
    private boolean noRecordsInMain = false;

    private void skipIfNoRecords() {
        if (noRecordsInMain) {
            logger.info("⏭️ Skipping remaining steps: no records in main table.");
            throw new SkipException("No records in compliance table — skipping archive/unarchive flow.");
        }
    }


    @Step("{message}")
    public void logStep(String message) {
        logger.info(message);
    }


    @Given("the user is on the Compliance page")
    public void the_user_is_on_the_compliance_page() {
        try {
            logStep("🔍 Navigating and confirming that the user is on the Compliance page...");

            // 1) Start timers BEFORE the click (captures click → nav → render)
            Instant navStart = Instant.now();
            NavContext.start("Compliance");

            // 2) Click the Compliances tab
            compliancePage.clickCompliancesTab();

            // 3) Wait up to NAV_FAIL_MS for the page to be ready (config-driven)
            boolean success = compliancePage.waitForComplianceLoaded(Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS));

            // 4) Stop & log timing (defaults to NAV thresholds 12s/20s)
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Compliance", navStart);
            double elapsedSec = elapsedMs / 1000.0;

            // 5) Threshold handling for NAV (warn ≥ NAV_WARN_MS, fail ≥ NAV_FAIL_MS)
            if (success) {
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("Compliance took %.2f s — more than %d s. Failing (SLA %ds).", elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Failure", failMsg);
                    Assert.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("Compliance took %.2f s — more than %d s.", elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                }
            } else {
                String failMsg = String.format("Unable to load Compliance within %d s (actual: %.2f s).", ReusableCommonMethods.NAV_FAIL_MS / 1000, elapsedSec);
                logger.error(failMsg);
                logToAllure("❌ Access Failure", failMsg);
                Assert.fail(failMsg);
            }

            // 6) Final artifacts
            logToAllure("📋 Compliance Page Loaded", String.valueOf(success));
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "CompliancePage_Confirmation");
            logger.info("✅ Compliance page successfully confirmed.");

        } catch (Throwable t) {
            compliancePage.handleValidationException("Compliance page confirmation", t);
        }
    }


    @Then("the user validates that the following Stage sections are displayed:")
    public void the_user_validates_that_the_following_stage_sections_are_displayed(io.cucumber.datatable.DataTable dataTable) {
        try {
            logStep("🔍 Validating Stage sections on Compliance page...");

            List<String> expectedSections = dataTable.asList();
            List<String> actualSections = compliancePage.getCompliancePageStageSections();

            List<String> lowerExpected = new ArrayList<String>();
            List<String> lowerActual = new ArrayList<String>();

            for (int i = 0; i < expectedSections.size(); i++) {
                lowerExpected.add(expectedSections.get(i).trim().toLowerCase());
            }

            for (int i = 0; i < actualSections.size(); i++) {
                lowerActual.add(actualSections.get(i).trim().toLowerCase());
            }

            Collections.sort(lowerExpected);
            Collections.sort(lowerActual);

            logger.info("\n========== Stage Sections Validation ==========");
            logger.info("Expected Stage Sections:\n{}", lowerExpected);
            logger.info("Actual Stage Sections:\n{}", lowerActual);

            logToAllure("Expected Stage Sections", String.join("\n", lowerExpected));
            logToAllure("Actual Stage Sections", String.join("\n", lowerActual));

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Compliance_Stage_Sections");

            if (!lowerExpected.equals(lowerActual)) {
                logger.error("❌ Stage sections mismatch detected!");

                for (int i = 0; i < Math.max(lowerExpected.size(), lowerActual.size()); i++) {
                    String expected = (i < lowerExpected.size()) ? lowerExpected.get(i) : "N/A";
                    String actual = (i < lowerActual.size()) ? lowerActual.get(i) : "N/A";

                    if (!expected.equals(actual)) {
                        logger.error("Position {} → Expected: '{}' | Actual: '{}'", (i + 1), expected, actual);
                        logToAllure("Mismatch at Position " + (i + 1), "Expected: " + expected + " | Actual: " + actual);
                    }
                }

                Assert.fail("❌ Stage section lists do not match! See logs and Allure report for details.");
            } else {
                logger.info("✅ Stage sections validated successfully — no mismatches found.");
            }

        } catch (Throwable t) {
            compliancePage.handleValidationException("Stage sections validation", t);
        }
    }


    @Then("the user validates that the 'All' stage count equals the sum of Need Action, In Progress, Completed, and Upcoming stage counts")
    public void validate_all_stage_count_equals_sum_of_other_stages() {
        try {
            logStep("🔍 Validating each Compliance stage count...");

            Map<String, Integer> stageCounts = compliancePage.getComplianceStageCounts();

            List<String> requiredStages = Arrays.asList("all", "needs action", "in progress", "completed", "upcoming");

            for (String stage : requiredStages) {
                if (!stageCounts.containsKey(stage)) {
                    logger.error("❌ Missing stage in extracted counts: {}", stage);
                    throw new AssertionError("Stage '" + stage + "' not found in Compliance page stage sections.");
                }
            }

            int allCount = stageCounts.get("all");
            int needsActionCount = stageCounts.get("needs action");
            int inProgressCount = stageCounts.get("in progress");
            int completedCount = stageCounts.get("completed");
            int upcomingCount = stageCounts.get("upcoming");

            int calculatedSum = needsActionCount + inProgressCount + completedCount + upcomingCount;

            logger.info("\n========== Compliance Stage Counts ==========");
            logger.info("All: {}", allCount);
            logger.info("Needs Action: {}", needsActionCount);
            logger.info("In Progress: {}", inProgressCount);
            logger.info("Completed: {}", completedCount);
            logger.info("Upcoming: {}", upcomingCount);
            logger.info("Calculated Total: {}", calculatedSum);

            logToAllure("Stage Counts", "All: " + allCount + "\n" + "Needs Action: " + needsActionCount + "\n" + "In Progress: " + inProgressCount + "\n" + "Completed: " + completedCount + "\n" + "Upcoming: " + upcomingCount + "\n" + "Calculated Total: " + calculatedSum);

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Compliance_Stage_Count_Validation");

            if (allCount != calculatedSum) {
                logger.error("❌ Mismatch Detected → All: {} | Calculated: {}", allCount, calculatedSum);
            }

            Assert.assertEquals(allCount, calculatedSum, "❌ 'All' stage count does not match the sum of other stages!");

            logger.info("✅ 'All' stage count validated successfully.");

        } catch (Throwable t) {
            compliancePage.handleValidationException("Stage count validation", t);
        }
    }


//    @Then("the user validates stage section tabs and their expected statuses")
//    public void validate_stage_section_tabs_and_statuses() {
//        try {
//            logStep("🔍 Starting full stage section validation flow...");
//
//            Map<String, String> stageSectionExpectedStatuses = new LinkedHashMap<String, String>();
//            stageSectionExpectedStatuses.put("All", "Pending,Delayed,Overdue,Completed,Not yet started,In progress");
//            stageSectionExpectedStatuses.put("Needs action", "Delayed,Pending");
//            stageSectionExpectedStatuses.put("In progress", "In progress,Overdue");
//            stageSectionExpectedStatuses.put("Completed", "Completed");
//            stageSectionExpectedStatuses.put("Upcoming", "Not yet started");
//
//            for (Map.Entry<String, String> entry : stageSectionExpectedStatuses.entrySet()) {
//                final String stageSection = entry.getKey();
//                String expectedStatusesCsv = entry.getValue();
//
//                List<String> expectedStatuses = new ArrayList<String>();
//                for (String status : expectedStatusesCsv.split(",")) {
//                    String lower = status.trim().toLowerCase();
//                    if (!lower.isEmpty() && !expectedStatuses.contains(lower)) {
//                        expectedStatuses.add(lower);
//                    }
//                }
//
//                // Navigate to tab and get UI-displayed count
//                compliancePage.clearDueDateFilter();
//                compliancePage.clickStageSectionTab(stageSection);
//
//                int tabDisplayedCount = compliancePage.getStageSectionTabCount(stageSection);
//                logger.info("📄 '{}' tab contains {} records", stageSection, tabDisplayedCount);
//                logToAllure("Displayed Count for " + stageSection, String.valueOf(tabDisplayedCount));
//
//                if (tabDisplayedCount == 0) {
//                    logger.warn("⚠️ No compliance records found under '{}' tab. Skipping status validation.", stageSection);
//                    logToAllure("Skipped Status Validation", "No records in '" + stageSection + "' tab");
//                    continue;
//                }
//
//                // Fetch statuses across pagination (your existing paginator)
//                Map<Integer, List<String>> pageWiseStatuses = compliancePage.fetchAllStatusValuesPageWise(tabDisplayedCount, new PageNavigationCallback() {
//                    public void onPage(int pageNumber) {
//                        ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Compliance_Page_Stage_Sections_" + stageSection.replace(" ", "_") + "_page" + pageNumber);
//                        logger.info("✅ Screenshot captured for Page {} of '{}'", pageNumber, stageSection);
//                    }
//                });
//
//                List<String> allStatusesCombined = new ArrayList<String>();
//                for (List<String> statusesPerPage : pageWiseStatuses.values()) {
//                    allStatusesCombined.addAll(statusesPerPage);
//                }
//
//                List<String> lowerActualStatuses = new ArrayList<String>();
//                for (String status : allStatusesCombined) {
//                    String lower = (status == null) ? "" : status.trim().toLowerCase();
//                    if (!lower.isEmpty() && !lowerActualStatuses.contains(lower)) {
//                        lowerActualStatuses.add(lower);
//                    }
//                }
//
//                logger.info("\n========== Status Validation for '{}' ==========\n", stageSection);
//                logger.info("Expected (allowed) statuses: {}", expectedStatuses);
//                logger.info("Actual statuses present: {}", lowerActualStatuses);
//                logToAllure("Expected (allowed) for " + stageSection, expectedStatuses.toString());
//                logToAllure("Actual Present for " + stageSection, lowerActualStatuses.toString());
//
//                Set<String> expectedSet = new LinkedHashSet<String>(expectedStatuses);
//                Set<String> actualSet = new LinkedHashSet<String>(lowerActualStatuses);
//
//                // Fail only if any unexpected status appears
//                Set<String> unexpected = new LinkedHashSet<String>(actualSet);
//                unexpected.removeAll(expectedSet);
//                Assert.assertTrue(unexpected.isEmpty(), "❌ Unexpected statuses found in '" + stageSection + "': " + unexpected + ". Allowed: " + expectedSet);
//
//                // Missing allowed statuses are OK — just log
//                Set<String> missing = new LinkedHashSet<String>(expectedSet);
//                missing.removeAll(actualSet);
//                if (!missing.isEmpty()) {
//                    logger.warn("ℹ️ '{}' did not show these allowed statuses (acceptable): {}", stageSection, missing);
//                    logToAllure("Missing (but allowed) in " + stageSection, missing.toString());
//                }
//
//                // Optional strict rule for Completed tab (keep/remove as you like)
//                if ("completed".equalsIgnoreCase(stageSection)) {
//                    Assert.assertTrue(actualSet.isEmpty() || (actualSet.size() == 1 && actualSet.contains("completed")), "Only 'Completed' allowed under Completed tab. Actual: " + actualSet);
//                }
//
//                logger.info("✅ '{}' tab status subset check passed. Actual Present: {}", stageSection, actualSet);
//
//                // COUNT CHECK
//                int fetchedRecordCount = allStatusesCombined.size();
//                Assert.assertEquals(fetchedRecordCount, tabDisplayedCount, "❌ Mismatch in count: Fetched = " + fetchedRecordCount + ", Displayed = " + tabDisplayedCount + " for '" + stageSection + "' tab.");
//                logToAllure("✅ Status Count Match for " + stageSection, "Fetched: " + fetchedRecordCount + ", Displayed: " + tabDisplayedCount);
//
//                logger.info("✅ '{}' tab validated: subset OK & count matched ({} records).", stageSection, fetchedRecordCount);
//            }
//
//        } catch (Throwable t) {
//            compliancePage.handleValidationException("stage section tabs validation", t);
//        }
//    }


    @Then("the user validates stage section tabs and their expected statuses")
    public void validate_stage_section_tabs_and_statuses() {
        try {
            logStep("🔍 Starting full stage section validation flow...");
            SoftAssert soft = new org.testng.asserts.SoftAssert();

            final boolean PERF_ENFORCE = Boolean.parseBoolean(System.getProperty("perf.enforce", "false"));
            final List<String> perfBreaches = new java.util.ArrayList<>();

            Map<String, String> stageSectionExpectedStatuses = new LinkedHashMap<>();
            stageSectionExpectedStatuses.put("All", "Pending,Delayed,Overdue,Completed,Not yet started,In progress");
            stageSectionExpectedStatuses.put("Needs action", "Delayed,Pending");
            stageSectionExpectedStatuses.put("In progress", "In progress,Overdue");
            stageSectionExpectedStatuses.put("Completed", "Completed");
            stageSectionExpectedStatuses.put("Upcoming", "Not yet started");

            for (Map.Entry<String, String> entry : stageSectionExpectedStatuses.entrySet()) {
                final String stageSection = entry.getKey();
                final String expectedStatusesCsv = entry.getValue();

                // expected set (lower-cased, unique)
                List<String> expectedStatuses = new ArrayList<>();
                for (String s : expectedStatusesCsv.split(",")) {
                    String lower = s.trim().toLowerCase();
                    if (!lower.isEmpty() && !expectedStatuses.contains(lower)) expectedStatuses.add(lower);
                }

                // ====== Clear filter → go to tab → wait until first page really shows Status text ======
                final String readyLabel = "Clear filter → '" + stageSection + "' first page ready";
                Instant readyStart = Instant.now();
                NavContext.start(readyLabel);

                compliancePage.clearDueDateFilter();
                compliancePage.clickStageSectionTab(stageSection);

                boolean firstPageReady = compliancePage.waitFirstPageStatusReady(Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS));

                long readyMs = helperMethods.logLoadTimeAndReturnMs(readyLabel, readyStart);
                double readyS = readyMs / 1000.0;
                String readyMsg = "'" + stageSection + "' table ready in " + String.format("%.2f s", readyS);
                logStep(readyMsg);
                logToAllure("⏱️ " + readyLabel, readyMsg);

                if (readyMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("%s took %.2f s — more than %d s (SLA).", readyLabel, readyS, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Breach", failMsg);
                    if (PERF_ENFORCE) perfBreaches.add(failMsg); // only stored; not soft.fail anymore
                } else if (readyMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("%s took %.2f s — more than %d s.", readyLabel, readyS, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                }

                if (!firstPageReady) {
                    logger.warn("⚠️ First page didn’t expose Status values before timeout; proceeding anyway.");
                }

                int tabDisplayedCount = compliancePage.getStageSectionTabCount(stageSection);
                logger.info("📄 '{}' tab contains {} records", stageSection, tabDisplayedCount);
                logToAllure("Displayed Count for " + stageSection, String.valueOf(tabDisplayedCount));
                if (tabDisplayedCount == 0) {
                    logger.warn("⚠️ No records in '{}'; skipping status validation.", stageSection);
                    logToAllure("Skipped Status Validation", "No records in '" + stageSection + "'");
                    continue;
                }

                // Screenshot on each page
                PageNavigationCallback pageShotCb = new PageNavigationCallback() {
                    @Override
                    public void onPage(int pageNumber) {
                        ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Compliance_Page_Stage_Sections_" + stageSection.replace(" ", "_") + "_page" + pageNumber);
                        logger.info("✅ Screenshot captured for Page {} of '{}'", Integer.valueOf(pageNumber), stageSection);
                    }
                };

                // Per-page timing (WARN-only unless perf.enforce=true)
                PageNavigationCallback timingCb = new PageNavigationCallback() {
                    @Override
                    public void onTiming(int fromPage, int toPage, long elapsedMs) {
                        double sec = elapsedMs / 1000.0;
                        String secStr = String.format("%.2f", sec);
                        String label = "StageTab '" + stageSection + "' page " + fromPage + "→" + toPage;

                        logToAllure("⏱️ Load Time (" + label + ")", secStr + " seconds");
                        logStep(label + " took " + secStr + " s");

                        if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                            String msg = String.format("%s took %s s — more than %d s (SLA).", label, secStr, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                            logger.error(msg);
                            logToAllure("❌ Load Time Breach", msg);
                            if (PERF_ENFORCE) perfBreaches.add(msg);
                        } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                            String warnMsg = String.format("%s took %s s — more than %d s.", label, secStr, ReusableCommonMethods.NAV_WARN_MS / 1000);
                            logger.warn(warnMsg);
                            logToAllure("⚠️ Load Time Warning", warnMsg);
                        }
                    }
                };

                // Collect statuses across all pages
                Map<Integer, List<String>> pageWiseStatuses = compliancePage.fetchAllStatusValuesPageWise(tabDisplayedCount, pageShotCb, timingCb);

                List<String> allStatusesCombined = new ArrayList<>();
                for (List<String> perPage : pageWiseStatuses.values()) allStatusesCombined.addAll(perPage);

                List<String> lowerActualStatuses = new ArrayList<>();
                for (String status : allStatusesCombined) {
                    String lower = (status == null) ? "" : status.trim().toLowerCase();
                    if (!lower.isEmpty() && !lowerActualStatuses.contains(lower)) lowerActualStatuses.add(lower);
                }

                logger.info("\n========== Status Validation for '{}' ==========\n", stageSection);
                logger.info("Expected (allowed): {}", expectedStatuses);
                logger.info("Actual present    : {}", lowerActualStatuses);
                logToAllure("Expected (allowed) for " + stageSection, expectedStatuses.toString());
                logToAllure("Actual Present for " + stageSection, lowerActualStatuses.toString());

                Set<String> expectedSet = new LinkedHashSet<>(expectedStatuses);
                Set<String> actualSet = new LinkedHashSet<>(lowerActualStatuses);

                // Fail if any unexpected status appears
                Set<String> unexpected = new LinkedHashSet<>(actualSet);
                unexpected.removeAll(expectedSet);
                org.testng.Assert.assertTrue(unexpected.isEmpty(), "❌ Unexpected statuses in '" + stageSection + "': " + unexpected + ". Allowed: " + expectedSet);

                // Missing allowed statuses are OK — log only
                Set<String> missing = new LinkedHashSet<>(expectedSet);
                missing.removeAll(actualSet);
                if (!missing.isEmpty()) {
                    logger.warn("ℹ️ '{}' missing (allowed) statuses: {}", stageSection, missing);
                    logToAllure("Missing (but allowed) in " + stageSection, missing.toString());
                }

                if ("completed".equalsIgnoreCase(stageSection)) {
                    Assert.assertTrue(actualSet.isEmpty() || (actualSet.size() == 1 && actualSet.contains("completed")), "Only 'Completed' allowed under Completed tab. Actual: " + actualSet);
                }

                // Count check (functional)
                int fetchedRecordCount = allStatusesCombined.size();
                Assert.assertEquals(fetchedRecordCount, tabDisplayedCount, "❌ Mismatch in count: Fetched = " + fetchedRecordCount + ", Displayed = " + tabDisplayedCount + " for '" + stageSection + "' tab.");
                logToAllure("✅ Status Count Match for " + stageSection, "Fetched: " + fetchedRecordCount + ", Displayed: " + tabDisplayedCount);

                logger.info("✅ '{}' validated: subset OK & count matched ({} records).", stageSection, Integer.valueOf(fetchedRecordCount));
            }

            // Only enforce perf if explicitly requested
            if (!perfBreaches.isEmpty() && PERF_ENFORCE) {
                Assert.fail("Performance breaches detected:\n - " + String.join("\n - ", perfBreaches));
            }
            soft.assertAll();

        } catch (Throwable t) {
            compliancePage.handleValidationException("stage section tabs validation", t);
        }
    }


    @Then("the Compliance table should display the following headers:")
    public void verify_compliance_table_headers(DataTable dataTable) {
        try {
            List<String> expectedHeaders = dataTable.asList(String.class);
            List<String> actualHeaders = compliancePage.getTableHeadersText();

            logToAllure("🧾 Expected Headers", expectedHeaders.toString());
            logToAllure("📋 Actual Headers from Table", actualHeaders.toString());
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Table_Headers_UI");

            for (int i = 0; i < expectedHeaders.size(); i++) {
                String expected = expectedHeaders.get(i);
                boolean matched = false;

                for (int j = 0; j < actualHeaders.size(); j++) {
                    String actual = actualHeaders.get(j);
                    if (expected.trim().equalsIgnoreCase(actual.trim())) {
                        matched = true;
                        logToAllure("✅ Header Found", expected);
                        logger.info("✅ Header matched: {}", expected);
                        break;
                    }
                }

                if (!matched) {
                    logToAllure("❌ Missing Header", expected);
                    logger.error("❌ Expected header '{}' not found in actual headers.", expected);
                    throw new AssertionError("Expected header not found: " + expected);
                }
            }

            if (expectedHeaders.size() != actualHeaders.size()) {
                String mismatch = "Header count mismatch. Expected: " + expectedHeaders.size() + ", Actual: " + actualHeaders.size();
                logToAllure("⚠️ Header Count Mismatch", mismatch);
                logger.warn(mismatch);
            }

        } catch (Throwable t) {
            compliancePage.handleValidationException("Header validation on Compliance table", t);
        }
    }


    @Then("the following headers should have sorting enabled:")
    public void verify_sorting_enabled_headers(DataTable dataTable) {
        try {
            List<String> sortableHeaders = dataTable.asList(String.class);
            logToAllure("🔽 Headers expected to have sorting", sortableHeaders.toString());

            for (int i = 0; i < sortableHeaders.size(); i++) {
                String header = sortableHeaders.get(i);
                boolean isSortable = compliancePage.isHeaderSortable(header);

                if (isSortable) {
                    logToAllure("✅ Sorting Enabled", header);
                    logger.info("✅ Sorting is enabled for: {}", header);
                } else {
                    logToAllure("❌ Sorting Missing", header);
                    logger.error("❌ Sorting not found for: {}", header);
                    ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Missing_Sorting_" + header.replace(" ", "_"));
                    throw new AssertionError("Sorting not enabled for header: " + header);
                }
            }

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Sorting_Verification_Overview");

        } catch (Throwable t) {
            compliancePage.handleValidationException("Sorting validation on Compliance table", t);
        }
    }


    @Then("the sorting arrows should be clickable for the following headers:")
    public void verify_sorting_arrows_clickable(DataTable dataTable) {
        List<String> headers = dataTable.asList(String.class);
        logToAllure("🔽 Headers to check sorting arrow click", headers.toString());

        try {
            for (String header : headers) {
                logger.info("🔘 Clicking sort icon for header: {}", header);

                // Ascending click
                boolean ascClicked = compliancePage.clickSortIconForHeader(header);
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Sort_Click_Asc_" + header.replace(" ", "_"));

                // Small delay
                Thread.sleep(800);

                // Descending click
                boolean descClicked = compliancePage.clickSortIconForHeader(header);
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Sort_Click_Desc_" + header.replace(" ", "_"));

                if (ascClicked && descClicked) {
                    logToAllure("✅ Sort Icons Clicked (Asc/Desc)", header);
                    logger.info("✅ Successfully clicked sort icons for: {}", header);
                } else {
                    logToAllure("❌ Sort Icon Click Failed", header);
                    logger.error("❌ Could not click sort icon for: {}", header);
                    ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "SortIcon_Click_Failure_" + header.replace(" ", "_"));
                    throw new AssertionError("Sort icon not clickable for header: " + header);
                }
            }

        } catch (Throwable t) {
            compliancePage.handleValidationException("Sorting arrow click check", t);
        }
    }


    @Then("the checkboxes for each record should be Visible")
    public void verify_record_checkboxes_visible() {
        try {
            logStep("🧾 Verifying that each record in the compliance table has a visible checkbox...");

            if (compliancePage.noRecordsPresent()) {
                noRecordsInMain = true;
                logger.info("ℹ️ No records available — checkboxes not expected.");
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "No_Records");
                logToAllure("ℹ️ Skipped", "No records available, so no checkboxes expected.");
                return; // step passes; later steps will be skipped by guard
            }

            boolean allVisible = compliancePage.areAllRecordCheckboxesVisible();
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Checkboxes_Visibility_Checked");

            if (allVisible) {
                logger.info("✅ All checkboxes are visible for records.");
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "All_Checkboxes_Visible");
                logToAllure("✅ All Checkboxes Visible", "Every row has a visible checkbox.");
            } else {
                logger.error("❌ Some record checkboxes are not visible.");
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Missing_Record_Checkbox");
                logToAllure("❌ Missing Checkboxes", "Not all checkboxes are visible.");
                throw new AssertionError("❌ Not all record checkboxes are visible");
            }

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Exception_Record_Checkbox");
            compliancePage.handleValidationException("Record checkboxes visibility check", e);
        }
    }


    @Then("the select all checkbox should select all records on the current page and show the Archive button with count")
    public void verify_select_all_checkbox_behavior() {
        skipIfNoRecords();
        try {
            logStep("✅ Verifying that the Select All checkbox selects all records and shows Archive button...");

            if (compliancePage.noRecordsPresent()) {
                logger.info("ℹ️ No records available — skipping select-all verification.");
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "No_Records_SelectAll");
                logToAllure("ℹ️ Skipped", "No records available, so select-all checkbox check skipped.");
                return;
            }

            CheckboxValidationResult result = compliancePage.selectAllCheckboxAndVerifyArchiveButton();
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "After_Selecting_All_Checkboxes");

            if (result.isSuccess()) {
                logToAllure("✅ Success", "All checkboxes selected.\n" + "✅ Displayed count: **" + result.getDisplayedCount() + "**\n" + "✅ Actual checkbox count: **" + result.getActualSelectedCount() + "**\n" + "Archive button is visible.");
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "SelectAll_Success");
            } else {
                logToAllure("❌ Failure", "Validation failed:\n" + "🔹 Displayed count: **" + result.getDisplayedCount() + "**\n" + "🔹 Actual checkbox count: **" + result.getActualSelectedCount() + "**\n" + "Archive button visible: **" + result.isArchiveVisible() + "**");
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "SelectAll_Checkbox_Or_Archive_Failure");
                throw new AssertionError("❌ Validation failed: Checkbox/Archive/Count mismatch.");
            }
        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Exception_SelectAll_Archive");
            compliancePage.handleValidationException("Select-all checkbox & Archive button verification", e);
        }
    }


    List<String> selectedRecordsBeforeArchive = new ArrayList<>();

    @And("capture the selected record names before archiving")
    public void capture_selected_record_names_before_archiving() {
        skipIfNoRecords();
        try {
            logStep("📋 Capturing selected compliance records BEFORE clicking Archive...");

            if (compliancePage.noRecordsPresent()) {
                logger.info("ℹ️ No records available to capture.");
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "No_Records_To_Capture");
                logToAllure("ℹ️ Skipped", "No records to capture before archiving.");
                return;
            }

            selectedRecordsBeforeArchive = compliancePage.getSelectedComplianceNames();

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Captured_Selected_Records");
            logToAllure("✅ Captured Records", "Selected records: " + selectedRecordsBeforeArchive);

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Exception_Capturing_Selected_Records");
            compliancePage.handleValidationException("Capturing Selected Record Names", e);
        }
    }

    @When("the user clicks the Archive button")
    public void user_clicks_archive_button() {
        skipIfNoRecords();
        try {
            logStep("🗃️ Validating and clicking Archive button...");

            if (compliancePage.noRecordsPresent()) {
                logger.info("ℹ️ No records present on the page. Skipping Archive button validation.");
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "No_Records_Archive_Skip");
                logToAllure("ℹ️ Skipped", "No records available. Archive button click not applicable.");
                return;
            }

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Before_Archive_Click");

            Instant navStart = Instant.now();
            NavContext.start("Archive → Toast");

            compliancePage.clickArchiveButton();
            String toastMessage = compliancePage.getArchiveSuccessToastMessage();

            // ⏱️ Stop & log timing (defaults to NAV thresholds 12s/20s)
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Archive → Toast", navStart);
            double elapsedS = elapsedMs / 1000.0;

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "After_Archive_Click");

            if (toastMessage == null) {
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "ArchiveToast_Missing");
                String msg = "Archive success toast not shown (after " + String.format("%.2f s", elapsedS) + ").";
                logToAllure("❌ Archive Toast Missing", msg);
                Assert.fail(msg);
                return;
            }

            if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                String failMsg = String.format("Archive → Toast took %.2f s — more than %d s. Failing (SLA %ds).", elapsedS, ReusableCommonMethods.NAV_FAIL_MS / 1000, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                logger.error(failMsg);
                logToAllure("❌ Load Time Failure", failMsg);
                Assert.fail(failMsg);
            } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                String warnMsg = String.format("Archive → Toast took %.2f s — more than %d s.", elapsedS, ReusableCommonMethods.NAV_WARN_MS / 1000);
                logger.warn(warnMsg);
                logToAllure("⚠️ Load Time Warning", warnMsg);
            }

            String lower = toastMessage.toLowerCase();
            if (lower.indexOf("archived") == -1 || lower.indexOf("success") == -1) {
                String msg = "Unexpected archive toast text: " + toastMessage;
                logToAllure("❌ Archive Toast Unexpected", msg);
                Assert.fail(msg);
            }

            logger.info("✅ Archive success message: {}", toastMessage);
            logToAllure("Archive Toast Message", toastMessage);
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Archive_Success_Toast");
            logToAllure("✅ Archive Clicked", "Archive button clicked and toast verified: " + toastMessage);

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Exception_Archive_Click");
            logToAllure("❌ Archive Click Failed", "Exception during Archive button click.");
            compliancePage.handleValidationException("Click Archive button", e);
        }
    }

    @Then("the system should move the selected records to Archive and display them in Archive list")
    public void verify_selected_records_in_archive() {
        skipIfNoRecords();
        try {
            logStep("🗃️ Navigating to Archive and validating previously selected records...");
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Before_Archive_Validation");

            Instant navStart = Instant.now();
            NavContext.start("Open Archive list");

            boolean isValidationPassed = compliancePage.areRecordsPresentInArchive(selectedRecordsBeforeArchive);

            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Open Archive list", navStart);
            double elapsedS = elapsedMs / 1000.0;

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "After_Archive_Validation");

            if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                String msg = String.format("Open Archive list took %.2f s — more than %d s. Failing (SLA %ds).", elapsedS, ReusableCommonMethods.NAV_FAIL_MS / 1000, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                logger.error(msg);
                logToAllure("❌ Load Time Failure", msg);
                Assert.fail(msg);
            } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                String msg = String.format("Open Archive list took %.2f s — more than %d s.", elapsedS, ReusableCommonMethods.NAV_WARN_MS / 1000);
                logger.warn(msg);
                logToAllure("⚠️ Load Time Warning", msg);
            }

            if (isValidationPassed) {
                logToAllure("✅ Archive Validation Passed", "All selected records are successfully archived.");
            } else {
                logToAllure("❌ Archive Validation Failed", "Some records are missing in Archive. Check logs for details.");
                Assert.fail("Archive Validation Failed.");
            }

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Exception_Archive_Validation");
            compliancePage.handleValidationException("Archive Validation", e);
        }
    }


    @When("the user Unarchives the records")
    public void user_unarchives_the_records() {
        skipIfNoRecords();
        try {
            logStep("♻️ Fetching archived records and clicking Unarchive...");

            //Fetch the list of archived record names before Unarchive
            archivedRecordsBeforeUnarchive = compliancePage.getArchivedComplianceNames();

            logToAllure("📦 Archived Records Fetched", "Records found in Archive before unarchiving: " + archivedRecordsBeforeUnarchive);
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Before_Unarchive_Click");

            //Click Unarchive button for each archived record
            compliancePage.clickUnarchiveButtonsForRecords(archivedRecordsBeforeUnarchive);

            //Wait for Unarchive to reflect (optional explicit wait inside method)
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "After_Unarchive_Click");

            logToAllure("✅ Unarchive Action Completed", "Unarchive buttons clicked for all archived records.");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Exception_Unarchive_Action");
            compliancePage.handleValidationException("Unarchive Action", e);
            Assert.fail("❌ Exception during Unarchive action. Check logs.");
        }
    }


    @Then("the records should be moved back to the Original destination")
    public void verify_unarchived_records_restored_to_main_table() {
        skipIfNoRecords();
        try {
            logStep("🔁 Navigating to Main Compliance Table and validating unarchived records...");
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Before_Unarchive_Validation");

            boolean isUnarchiveValidationPassed = compliancePage.areUnarchivedRecordsPresentInMainTable(archivedRecordsBeforeUnarchive);

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "After_Unarchive_Validation");

            if (isUnarchiveValidationPassed) {
                logToAllure("✅ Unarchive Validation Passed", "All unarchived records are successfully restored to the main table.");
            } else {
                logToAllure("❌ Unarchive Validation Failed", "Some unarchived records are missing in the main table. Check logs for details.");
                Assert.fail("Unarchive Validation Failed.");
            }

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Exception_Unarchive_Validation");
            compliancePage.handleValidationException("Unarchive Validation", e);
        }
    }

    @When("the user clicks the {string} button and the CSV file is downloaded successfully")
    public void the_user_clicks_button_and_csv_is_downloaded(String buttonText) {
        try {
            logStep("📥 Validating CSV download via '" + buttonText + "' button...");
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Before_CSV_Generation");

            if (!buttonText.equalsIgnoreCase("Generate CSV")) {
                logToAllure("❌ Invalid Button Text", "Unsupported button: " + buttonText);
                Assert.fail("Unsupported button: " + buttonText);
                return;
            }

            Assert.assertTrue(compliancePage.isGenerateCSVButtonVisible(), "❌ Generate CSV button is not visible");

            // SAME folder Chrome is using (because Hooks set system property download.dir)
            File downloadDir = FileUtils.getDefaultDownloadDir();
            if (!downloadDir.exists() && !downloadDir.mkdirs()) {
                Assert.fail("Download directory not available: " + downloadDir.getAbsolutePath());
            }

            final long clickEpochMs = System.currentTimeMillis();
            compliancePage.clickGenerateCSVButton(driver, 10);

            File downloadedFile = FileUtils.waitForMatchingDownload(downloadDir, "compliance_data", ".csv", clickEpochMs, 60);

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "After_CSV_Generation");

            if (downloadedFile != null && downloadedFile.exists()) {
                lastDownloadedCsvPath = downloadedFile.toPath(); // pass to next step
                logToAllure("✅ CSV Download Passed", "CSV file downloaded: " + downloadedFile.getAbsolutePath());
            } else {
                logToAllure("❌ CSV Download Failed", "No CSV found in: " + downloadDir.getAbsolutePath());
                Assert.fail("CSV file was not downloaded.");
            }

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Exception_CSV_Generation");
            compliancePage.handleValidationException("CSV Generation and Download", e);
        }
    }


    @And("the CSV file should contain all the available records")
    public void the_csv_file_should_contain_all_the_available_records() {
        try {
            File downloadDirFile = FileUtils.getDefaultDownloadDir();
            Path downloadDir = downloadDirFile.toPath();
            Files.createDirectories(downloadDir);

            logStep("📖 Reading CSV file contents from: " + downloadDir.toAbsolutePath());
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Before_CSV_Read");

            Path csvPath = (lastDownloadedCsvPath != null && Files.exists(lastDownloadedCsvPath)) ? lastDownloadedCsvPath : (FileUtils.latestMatchingFile(downloadDirFile, "compliance_data", ".csv") != null ? FileUtils.latestMatchingFile(downloadDirFile, "compliance_data", ".csv").toPath() : null);

            if (csvPath == null || !Files.exists(csvPath)) {
                logToAllure("❌ CSV not found", "No CSV present under: " + downloadDir.toAbsolutePath());
                Assert.fail("CSV file not found.");
                return;
            }

            long lineNo = 0;
            StringBuilder captured = new StringBuilder();
            try (BufferedReader br = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
                String line;
                while ((line = br.readLine()) != null) {
                    lineNo++;
                    String printed = String.format("%04d | %s", lineNo, line);
                    System.out.println(printed);
                    captured.append(printed).append(System.lineSeparator());
                }
            }

            try (InputStream is = Files.newInputStream(csvPath)) {
                Allure.addAttachment("CSV file (" + csvPath.getFileName() + ")", is);
            } catch (IOException ignore) {
            }

            Allure.addAttachment("CSV preview (line-numbered)", "text/plain", captured.toString(), ".txt");

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "After_CSV_Read");
            logToAllure("✅ CSV read complete", "File: " + csvPath.toAbsolutePath() + " | Lines (incl. header): " + lineNo);

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Exception_CSV_Read");
            compliancePage.handleValidationException("CSV Read/Print", e);
        }
    }


    @When("the user click on In Progress to get active compliance")
    public void the_user_click_on_in_progress_to_get_active_compliance() {
        try {
            logStep("➡️ Navigating to the 'In progress' section/tab…");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_Navigate_InProgress");

            boolean ok = compliancePage.navigateToInProgressTab();

            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Navigate_InProgress");
            logToAllure("In progress navigation", "Clicked tab: " + ok);

            if (!ok) {
                logToAllure("❌ In progress navigation failed", "Could not click the 'In progress' tab (normal & JS).");
                Assert.fail("Failed to navigate to 'In progress' tab.");
            } else {
                logToAllure("✅ In progress navigation passed", "Clicked the 'In progress' tab.");
            }
        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Exception_Navigate_InProgress");
            compliancePage.handleValidationException("Navigate to In progress tab", e);
        }
    }


    @When("the user clicks any compliance record in the list")
    public void the_user_clicks_any_compliance_record_in_the_list() {
        try {
            logStep("🖱️ Clicking the first compliance record in the list...");
            Thread.sleep(10000);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_Click_First_Record");
            compliancePage.clearDueDateFilter();
            Thread.sleep(20000);
            compliancePage.clickFirstComplianceRecord();
            helperMethods.pauseForScreenshot();
            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Click_First_Record");
            logToAllure("✅ Click Successful", "First compliance record clicked.");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Exception_Click_First_Record");
            compliancePage.handleValidationException("Click First Compliance Record", e);
        }
    }


    @Then("the compliance details panel should open on the right side of the page")
    public void the_compliance_details_panel_should_open_on_the_right_side_of_the_page() {
        try {
            logStep("🧭 Validating that the compliance details panel has opened on the right...");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_Panel_Validation");

            boolean isOpen = compliancePage.isCompliancePanelDisplayed();

            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Panel_Validation");

            if (isOpen) {
                logToAllure("✅ Panel Validation Passed", "Compliance details panel is visible with anchor fields.");
            } else {
                logToAllure("❌ Panel Validation Failed", "Details panel did not display required fields.");
                Assert.fail("Compliance details panel is not displayed.");
            }

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Exception_Panel_Validation");
            compliancePage.handleValidationException("Compliance Panel Visibility", e);
        }
    }


    @Then("the user should see the following fields")
    public void the_user_should_see_the_following_fields(DataTable dataTable) {
        try {
            logStep("🔎 Verifying presence of fields in the first section...");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_Field_Validation");

            // Accept table with or without header "field"
            List<String> rows = dataTable.asList();
            List<String> expectedFields = new ArrayList<>();
            for (String r : rows) {
                if (!"field".equalsIgnoreCase(r.trim())) {
                    expectedFields.add(r.trim());
                }
            }

            // Defensive: empty table
            if (expectedFields.isEmpty()) {
                logToAllure("⚠️ No Fields Provided", "The datatable for expected fields was empty after header normalization.");
                Assert.fail("No fields provided in the DataTable for validation.");
            }

            // Validate each field and collect any misses
            List<String> missing = new ArrayList<>();
            for (String field : expectedFields) {
                boolean visible = compliancePage.isFieldDisplayed(field);
                logToAllure("Field Check", String.format("'%s' visible: %s", field, visible));
                if (!visible) missing.add(field);
            }

            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Field_Validation");

            if (!missing.isEmpty()) {
                logToAllure("❌ Field Visibility Failed", "Missing/Not visible fields in first section: " + String.join(", ", missing));
                Assert.fail("First section missing fields: " + String.join(", ", missing));
            } else {
                logToAllure("✅ Field Visibility Passed", "All expected fields are visible: " + String.join(", ", expectedFields));
            }

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Exception_Field_Validation");
            compliancePage.handleValidationException("First Section Fields Validation", e);
        }
    }

    @Then("the user should be able to access Info, Tasks, and Audit trial tabs and see their content, and Opt with Zolvit action should be present")
    public void the_user_should_be_able_to_access_tabs_and_opt_with_zolvit_action() {
        try {
            logStep("🔎 Verifying presence of Opt with Zolvit, then accessibility of Info/Tasks/Audit tabs...");
            ScreenshotUtils.attachScreenshotToAllure(driver, "01_Before_Tab_And_Action_Validation");

            List<String> missing = new ArrayList<String>();

            // --- 1) Opt with Zolvit
            Instant startExpert = Instant.now();
            NavContext.start("Opt with Zolvit");
            boolean optWithZolvitVisible = false;
            try {
                compliancePage.assertOptWithZolvitOrTellUsPresent();
                optWithZolvitVisible = true;
                ScreenshotUtils.attachScreenshotToAllure(driver, "02_Opt_With_Zolvit");
                logToAllure("✅ Opt with Zolvit", "Action is visible, clickable, and correctly labeled.");
            } catch (Throwable t) {
                missing.add("Opt with Zolvit");
                logToAllure("❌ Opt with Zolvit", "Not accessible: " + t.getMessage());
                ScreenshotUtils.attachScreenshotToAllure(driver, "02_Fail_Opt_With_Zolvit");
            }
            long msExpert = helperMethods.logLoadTimeAndReturnMs("Opt with Zolvit", startExpert);
            double sExpert = msExpert / 1000.0;
            if (optWithZolvitVisible) {
                if (msExpert >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String m = String.format("Opt with Zolvit took %.2f s — more than %d s.",
                            sExpert, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logToAllure("❌ Load Time Failure", m);
                    missing.add("Opt with Zolvit (slow)");
                } else if (msExpert >= ReusableCommonMethods.NAV_WARN_MS) {
                    String m = String.format("Opt with Zolvit took %.2f s — more than %d s.",
                            sExpert, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logToAllure("⚠️ Load Time Warning", m);
                }
            }

            // --- 2) Info Tab
            Instant startInfo = Instant.now();
            NavContext.start("Info Tab");
            boolean infoVisible = compliancePage.openInfoAndCheck();
            ScreenshotUtils.attachScreenshotToAllure(driver, "03_Info_Tab_Content");
            logToAllure("Tab Check", "Info tab accessible and content visible: " + infoVisible);
            if (!infoVisible) missing.add("Info tab");
            long msInfo = helperMethods.logLoadTimeAndReturnMs("Info Tab", startInfo);
            double sInfo = msInfo / 1000.0;
            if (infoVisible) {
                if (msInfo >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String m = String.format("Info Tab took %.2f s — more than %d s.",
                            sInfo, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logToAllure("❌ Load Time Failure", m);
                    missing.add("Info tab (slow)");
                } else if (msInfo >= ReusableCommonMethods.NAV_WARN_MS) {
                    String m = String.format("Info Tab took %.2f s — more than %d s.",
                            sInfo, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logToAllure("⚠️ Load Time Warning", m);
                }
            }

            // --- 3) Tasks Tab
            Instant startTasks = Instant.now();
            NavContext.start("Tasks Tab");
            boolean tasksVisible = compliancePage.openTasksAndCheck();
            ScreenshotUtils.attachScreenshotToAllure(driver, "04_Tasks_Tab_Content");
            logToAllure("Tab Check", "Tasks tab accessible and content visible: " + tasksVisible);
            if (!tasksVisible) missing.add("Tasks tab");
            long msTasks = helperMethods.logLoadTimeAndReturnMs("Tasks Tab", startTasks);
            double sTasks = msTasks / 1000.0;
            if (tasksVisible) {
                if (msTasks >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String m = String.format("Tasks Tab took %.2f s — more than %d s.",
                            sTasks, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logToAllure("❌ Load Time Failure", m);
                    missing.add("Tasks tab (slow)");
                } else if (msTasks >= ReusableCommonMethods.NAV_WARN_MS) {
                    String m = String.format("Tasks Tab took %.2f s — more than %d s.",
                            sTasks, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logToAllure("⚠️ Load Time Warning", m);
                }
            }

            // --- 4) Audit Trial Tab
            Instant startAudit = Instant.now();
            NavContext.start("Audit Trial Tab");
            boolean auditVisible = compliancePage.openAuditAndCheck();
            ScreenshotUtils.attachScreenshotToAllure(driver, "05_Audit_Tab_Content");
            logToAllure("Tab Check", "Audit trial tab accessible and content visible: " + auditVisible);
            if (!auditVisible) missing.add("Audit trial tab");
            long msAudit = helperMethods.logLoadTimeAndReturnMs("Audit Trial Tab", startAudit);
            double sAudit = msAudit / 1000.0;
            if (auditVisible) {
                if (msAudit >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String m = String.format("Audit trial tab took %.2f s — more than %d s.",
                            sAudit, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logToAllure("❌ Load Time Failure", m);
                    missing.add("Audit trial tab (slow)");
                } else if (msAudit >= ReusableCommonMethods.NAV_WARN_MS) {
                    String m = String.format("Audit trial tab took %.2f s — more than %d s.",
                            sAudit, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logToAllure("⚠️ Load Time Warning", m);
                }
            }

            ScreenshotUtils.attachScreenshotToAllure(driver, "06_After_Tab_And_Action_Validation");

            // --- Final validation
            if (!missing.isEmpty()) {
                logToAllure("❌ Validation Failed", "Missing/slow elements: " + String.join(", ", missing));
                Assert.fail("Validation failed for: " + String.join(", ", missing));
            } else {
                logToAllure("✅ Validation Passed",
                        "Opt with Zolvit present, and all tabs accessible with expected content: Info, Tasks, Audit trial.");
            }

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ZZ_Exception_Tab_And_Action_Validation");
            compliancePage.handleValidationException("Tabs & Opt with Zolvit Validation", e);
        }
    }



    @Then("the default landing is the info {string} tab in the active panel")
    public void the_default_landing_is_the_infotab_in_the_active_panel(String tabName) {
        try {
            if (!"Info".equalsIgnoreCase(tabName)) {
                Assert.fail("Unsupported tab for this step: " + tabName + ". This step is intended for 'Info'.");
            }

            logStep("🔎 Verifying default landing is : 'Info'");
            boolean ok = compliancePage.isInfoDefaultContentVisible();
            helperMethods.pauseForScreenshot();
            ScreenshotUtils.attachScreenshotToAllure(driver, "Default_Info_NoClick_Content");
            logToAllure("Default Landing - Info", "Visible & non-blank: " + ok);

            if (!ok) {
                logToAllure("❌ Default Landing - Info Failed", "Expected 'What is it?' to be visible by default.");
                Assert.fail("Default landing 'Info' failed (content missing/blank/timeout).");
            } else {
                logToAllure("✅ Default Landing - Info Passed", "Default landing to 'Info' field and Content is visible");
            }

            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Default_Info_NoClick");
        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Exception_Default_Info_NoClick");
            compliancePage.handleValidationException("Default Landing - Info (no click)", e);
        }
    }

    @Then("the default landing is the task {string} tab in the active panel")
    public void the_default_landing_is_the_taskstab_in_the_active_panel(String tabName) {
        try {
            if (!"Tasks".equalsIgnoreCase(tabName)) {
                Assert.fail("This step expects 'Tasks' but received: " + tabName);
            }

            logStep("🔎 Verifying default landing (no click): 'Tasks' shows text containing 'Payment'");
            helperMethods.pauseForScreenshot();
            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_Default_Tasks_NoClick");

            // Pure POM call (no click): waits for and verifies `paymentInPanel`
            boolean ok = compliancePage.isTasksDefaultContentVisible();

            ScreenshotUtils.attachScreenshotToAllure(driver, "Default_Tasks_NoClick_Content");
            logToAllure("Default Landing - Tasks (no click)", "Visible & non-blank: " + ok);
            helperMethods.pauseForScreenshot();
            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Default_Tasks_NoClick");

            if (!ok) {
                logToAllure("❌ Default Landing - Tasks Failed", "Expected text containing 'Payment' to be visible by default.");
                Assert.fail("Default landing 'Tasks' failed (content missing/blank/timeout).");
            } else {
                logToAllure("✅ Default Landing - Tasks Passed", "Default landing to 'Tasks' field and Content is visible");
            }
        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Exception_Default_Tasks_NoClick");
            compliancePage.handleValidationException("Default Landing - Tasks (no click)", e);
        }
    }


    @Then("I should see and print the dropdown filters and their options")
    public void verifyAndPrintDropdownsAndOptions() {
        try {
            logger.info("🔎 Verifying Compliance filters and collecting options…");

            // ===== Validate filters visibility =====
            Assert.assertTrue(compliancePage.isComplianceDepartmentVisible(), "Department dropdown not visible");
            Assert.assertTrue(compliancePage.isComplianceCategoryVisible(), "Category dropdown not visible");
            Assert.assertTrue(compliancePage.isDueDateVisible(), "Due Date dropdown not visible");
            logger.info("👀 All dropdown filters are visible: Department, Category, Due Date.");

            // ===== Department =====
            compliancePage.openDepartmentDropdown();
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Department_Dropdown_Opened");

            final List<String> departmentOptions = compliancePage.getDepartmentOptions();
            if (departmentOptions.isEmpty()) {
                logger.warn("⚠️ Department options list is EMPTY.");
            }
            logToAllure("🏷️ Department Options", departmentOptions.toString());
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Department_Options");
            try {
                new org.openqa.selenium.interactions.Actions(Hooks.driver).sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
            } catch (Throwable ignore) {
            }

            // ===== Category =====
            compliancePage.openCategoryDropdown();
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Category_Dropdown_Opened");

            final List<String> categoryOptions = compliancePage.getCategoryOptions();
            if (categoryOptions.isEmpty()) {
                logger.warn("⚠️ Category options list is EMPTY.");
            }
            logToAllure("🏷️ Category Options", categoryOptions.toString());
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Category_Options");
            try {
                new org.openqa.selenium.interactions.Actions(Hooks.driver).sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
            } catch (Throwable ignore) {
            }

            // ===== Due Date =====
            compliancePage.openDueDateDropdown();
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "DueDate_Dropdown_Opened");

            final List<String> dueDateOptions = compliancePage.getDueDateOptions();
            if (dueDateOptions.isEmpty()) {
                logger.warn("⚠️ Due Date options list is EMPTY.");
            }
            logToAllure("🏷️ Due Date Options", dueDateOptions.toString());
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "DueDate_Options");
            try {
                new org.openqa.selenium.interactions.Actions(Hooks.driver).sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
            } catch (Throwable ignore) {
            }

            // ===== Summary =====
            logger.info("✅ Options count — Department: {}, Category: {}, Due Date: {}", Integer.valueOf(departmentOptions.size()), Integer.valueOf(categoryOptions.size()), Integer.valueOf(dueDateOptions.size()));
            logToAllure("📊 Options Summary", String.format("Department=%d, Category=%d, DueDate=%d", departmentOptions.size(), categoryOptions.size(), dueDateOptions.size()));

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "All_Dropdowns_Summary");

        } catch (Throwable t) {
            compliancePage.handleValidationException("Dropdown options validation", t);
        }
    }


    /**
     * Applies three filters in sequence: Department (no Apply), Category (Apply), Due Date (no Apply).
     * After each action, waits for a grid reload using the configured timeout — logs if it doesn't reload,
     * but does not fail the test. Finally logs/dumps all visible grid rows to logger and Allure.
     */
    @When("I filter compliances by Department {string}, Category {string}, and Due Date {string}")
    public void filterByDeptCategoryDueDate(String department, String category, String dueDate) {
        try {
            // Input validation
            if (department == null || department.trim().isEmpty())
                throw new IllegalArgumentException("Department filter cannot be null/empty.");
            if (category == null || category.trim().isEmpty())
                throw new IllegalArgumentException("Category filter cannot be null/empty.");
            if (dueDate == null || dueDate.trim().isEmpty())
                throw new IllegalArgumentException("Due Date filter cannot be null/empty.");

            logStep(String.format("🎛️ Applying filters: Dept='%s' → Category(with Apply)='%s' → DueDate='%s'", department, category, dueDate));
            logToAllure("🎛️ Filters", String.format("Department='%s', Category='%s', DueDate='%s'", department, category, dueDate));

            // Baseline grid signature
            lastSeenGridSignature = compliancePage.captureGridSignature();
            logToAllure("Baseline Grid Signature", lastSeenGridSignature);

            final long TIMEOUT = ReusableCommonMethods.NAV_FAIL_MS;

            // Helper: wait for reload, log only (no Assert)
            Consumer<String> waitReload = (label) -> {
                boolean ok = compliancePage.waitForGridReload(lastSeenGridSignature, TIMEOUT);
                if (!ok) {
                    String msg = label + ": grid did not reload within configured timeout. Continuing.";
                    logger.error("❌ {}", msg);
                    logToAllure("❌ Reload Timeout", msg);
                    ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, label.replace(" ", "_") + "_Timeout");
                } else {
                    lastSeenGridSignature = compliancePage.captureGridSignature();
                    logToAllure(label + " → New Signature", lastSeenGridSignature);
                }
            };

            // 1) Department (no Apply)
            compliancePage.ensureAllPanelsClosed();
            logger.info("➡ Selecting Compliance Department = {}", department);
            logToAllure("Step", "Select Department: " + department);
            compliancePage.selectFromDropdown(compliancePage.getDepartmentDropdownTrigger(), "Compliance Department", department, false);
            waitReload.accept("Grid reload after Department");

            // 2) Category (with Apply)
            compliancePage.ensureAllPanelsClosed();
            logger.info("➡ Selecting Compliance Category = {} (Apply)", category);
            logToAllure("Step", "Select Category (Apply): " + category);
            currentCategory = category;
            compliancePage.selectFromDropdown(compliancePage.getCategoryDropdownTrigger(), "Compliance Category", category, true // APPLY
            );
            waitReload.accept("Grid reload after Category (Apply)");

            // 3) Due Date (no Apply)
            compliancePage.ensureAllPanelsClosed();
            logger.info("➡ Selecting Due Date = {}", dueDate);
            logToAllure("Step", "Select Due Date: " + dueDate);
            compliancePage.selectFromDropdown(compliancePage.getDueDateDropdownTrigger(), "Due Date", dueDate, false);
            waitReload.accept("Grid reload after Due Date");

            // Artifacts: log all visible rows
            List<String> rows = compliancePage.getVisibleGridItems();
            logger.info("📊 Final grid item count after filters: {}", Integer.valueOf(rows.size()));
            for (int i = 0; i < rows.size(); i++) {
                logger.info("   • [{}] {}", Integer.valueOf(i + 1), rows.get(i));
            }
            logToAllure("📋 Filtered Grid Items", rows.toString());
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Final_Filtered_Grid");

        } catch (Throwable t) {
            compliancePage.handleValidationException("Dept→Category→DueDate filter flow", t);
        }
    }


    /**
     * Waits for the grid to reload relative to the last captured signature.
     * Uses configured timeout, logs if it didn’t reload, but does not fail on time.
     * Updates the lastSeenGridSignature on success and records artifacts.
     */
    @Then("^the results grid reloads$")
    public void resultsGridReloads() {
        try {
            logStep("Waiting for results grid to reload…");
            logToAllure("Action", "Wait for results grid reload");

            boolean ok = compliancePage.waitForGridReload(lastSeenGridSignature, ReusableCommonMethods.NAV_FAIL_MS);

            if (!ok) {
                String msg = "Results grid did not reload within configured timeout. Continuing.";
                logger.error("❌ {}", msg);
                logToAllure("❌ Reload Timeout", msg);
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Grid_AfterReload_Timeout");
            } else {
                lastSeenGridSignature = compliancePage.captureGridSignature();
                logToAllure("Grid Reload", "Observed new signature.");
            }

            int count = compliancePage.getVisibleGridItems().size();
            logger.info("📄 Grid shows {} item(s) after reload.", Integer.valueOf(count));
            logToAllure("📄 Grid Count After Reload", String.valueOf(count));
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Grid_AfterReload");

        } catch (Throwable t) {
            compliancePage.handleValidationException("Results grid reload wait", t);
        }
    }


    @And("^the following compliance names should be present:$")
    public void theFollowingComplianceNamesShouldBePresent(String expectedBlock) {
        try {
            if (expectedBlock == null || expectedBlock.trim().isEmpty()) {
                throw new IllegalArgumentException("Expected compliance list cannot be null/empty.");
            }

            List<String> expected = compliancePage.parseExpectedBlock(expectedBlock);
            logToAllure("📥 Expected Compliances", expected.toString());

            // Read the UI-displayed total from the "All" tab (or pass 0 if you prefer)
            int displayedTotal = compliancePage.getStageSectionTabCount("All");

            // Screenshot each page
            PageNavigationCallback pageShotCb = new PageNavigationCallback() {
                @Override public void onPage(int pageNumber) {
                    ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Grid_Page_" + pageNumber);
                    logger.info("📸 Captured Grid_Page_{}", Integer.valueOf(pageNumber));
                }
            };

            // Per-page timing (optional)
            PageNavigationCallback timingCb = new PageNavigationCallback() {
                @Override public void onTiming(int fromPage, int toPage, long elapsedMs) {
                    logToAllure("⏱ Page " + fromPage + "→" + toPage, String.format("%.2f s", elapsedMs / 1000.0));
                }
            };

            // >>> Collect titles across ALL pages using your pagination helpers
            Map<Integer, List<String>> pageWise = compliancePage.fetchAllTitleValuesPageWise(displayedTotal, pageShotCb, timingCb);

            // Flatten + canonicalize
            List<String> flat = new ArrayList<>();
            for (List<String> p : pageWise.values()) flat.addAll(p);

            LinkedHashSet<String> actualCanon = new LinkedHashSet<String>();
            for (String item : flat) {
                String base = compliancePage.toBaseTitle(item);
                if (base == null) base = "";
                String canon = compliancePage.canonicalComplianceName(base);
                if (!canon.isEmpty()) actualCanon.add(canon);
            }

            logToAllure("📦 Compliances on Screen (ALL pages)", actualCanon.toString());
            logger.info("🧮 Total titles collected across pages: {}", Integer.valueOf(flat.size()));

            // Compare (equals/contains either way as before)
            LinkedHashSet<String> missing = new LinkedHashSet<String>();
            for (String exp : expected) {
                String expCanon = compliancePage.canonicalComplianceName(exp);
                if (expCanon.isEmpty()) continue;

                boolean found = false;
                for (String a : actualCanon) {
                    if (a.equals(expCanon) || a.contains(expCanon) || expCanon.contains(a)) {
                        found = true; break;
                    }
                }
                if (!found) missing.add(exp);
            }

            if (!missing.isEmpty()) {
                String msg = "Missing compliances: " + missing + "\nCompliances on screen: " + actualCanon;
                logger.error("❌ {}", msg);
                logToAllure("❌ Missing Compliances", missing.toString());
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Compliances_Missing");
                Assert.fail(msg);
            } else {
                logger.info("✅ All expected compliance found. Matched={}", Integer.valueOf(expected.size()));
                logToAllure("✅ Compliances Presence", "All expected compliance found (ALL pages).");
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Compliances_Present_AllPages");
            }

        } catch (Throwable t) {
            compliancePage.handleValidationException("Compliance presence validation (all pages)", t);
        }
    }











    /**
     * Applies two filters in sequence for the "no category" flow:
     * 1) Department (no Apply)
     * 2) Due Date (no Apply)
     *
     * - Uses signature-based waits so we only continue after a real grid change.
     * - Does NOT fail the test if a reload times out; it logs and continues.
     * - After both filters, if the list is multi-page, it proactively resets the pager to Page 1
     *   (signature-aware) so subsequent steps collect titles from the beginning.
     * - Emits clear logs/Allure notes and screenshots at key points.
     */
    @When("I filter compliances by Department {string}, and Due Date {string}")
    public void filterByDeptAndDueDate(String department, String dueDate) {
        try {
            // ---- Input validation ----
            if (department == null || department.trim().isEmpty()) {
                throw new IllegalArgumentException("Department filter cannot be null/empty.");
            }
            if (dueDate == null || dueDate.trim().isEmpty()) {
                throw new IllegalArgumentException("Due Date filter cannot be null/empty.");
            }

            logStep(String.format("🎛️ Applying filters (no category): Dept='%s' → DueDate='%s'", department, dueDate));
            logToAllure("🎛️ Filters (No Category)",
                    String.format("Department='%s', DueDate='%s'", department, dueDate));

            // Baseline grid signature (used by our signature-based waits)
            lastSeenGridSignature = compliancePage.captureGridSignature();
            logToAllure("Baseline Grid Signature", lastSeenGridSignature);

            final long TIMEOUT = ReusableCommonMethods.NAV_FAIL_MS;

            // Helper: wait for reload; log/screenshot on timeout, do not Assert.fail
            java.util.function.Consumer<String> waitReload = (label) -> {
                boolean ok = compliancePage.waitForGridReload(lastSeenGridSignature, TIMEOUT);
                if (!ok) {
                    String msg = label + ": grid did not reload within configured timeout. Continuing.";
                    logger.error("❌ {}", msg);
                    logToAllure("❌ Reload Timeout", msg);
                    ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, label.replace(" ", "_") + "_Timeout");
                } else {
                    lastSeenGridSignature = compliancePage.captureGridSignature();
                    logToAllure(label + " → New Signature", lastSeenGridSignature);
                }
            };

            // ===== 1) Department (no Apply) =====
            compliancePage.ensureAllPanelsClosed();
            logger.info("➡ Selecting Compliance Department = {}", department);
            logToAllure("Step", "Select Department: " + department);
            compliancePage.selectFromDropdown(
                    compliancePage.getDepartmentDropdownTrigger(),
                    "Compliance Department",
                    department,
                    false
            );
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "After_Department_Filter");
            waitReload.accept("Grid reload after Department");

            // ===== 2) Due Date (no Apply) =====
            compliancePage.ensureAllPanelsClosed();
            logger.info("➡ Selecting Due Date = {}", dueDate);
            logToAllure("Step", "Select Due Date: " + dueDate);
            compliancePage.selectFromDropdown(
                    compliancePage.getDueDateDropdownTrigger(),
                    "Due Date",
                    dueDate,
                    false
            );
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "After_DueDate_Filter");
            waitReload.accept("Grid reload after Due Date");

            // ===== Ensure we start at Page 1 for subsequent steps =====
            // If the tab shows more items than fit on the current page, reset to page 1.
            try {
                int displayedTotal = compliancePage.getStageSectionTabCount("All");
                int currentPageSize = compliancePage.getCurrentPageVisibleRowCount();
                if (displayedTotal > currentPageSize) {
                    logger.info("↩️ Multi-page detected (total={}, pageSize={}) → navigating to Page 1 before assertions.",
                            Integer.valueOf(displayedTotal), Integer.valueOf(currentPageSize));

                    // Use signature-aware pager navigation (relies on your waitForGridReload(prevSig, timeout))
                    String sigBefore = compliancePage.captureGridSignature();
                    compliancePage.goToFirstPageSignatureAware(TIMEOUT);

                    // Confirm with signature wait (in case pager emits another grid update)
                    compliancePage.waitForGridReload(sigBefore, TIMEOUT);

                    // Optional: log current page indicator if you expose it
                    try {
                        int cur = compliancePage.getCurrentGridPageNumber();
                        logger.info("📍 Current page after reset: {}", Integer.valueOf(cur));
                    } catch (Throwable ignore) { /* non-fatal */ }

                    ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "After_Reset_To_Page_1");
                } else {
                    logger.info("➡ Single-page result (total <= pageSize). No pager reset needed.");
                }
            } catch (Throwable pagerIgnore) {
                logger.warn("⚠️ Pager reset check skipped due to: {}", pagerIgnore.toString());
            }

            // ===== Artifacts: dump final rows on the (now) first page =====
            java.util.List<String> rows = compliancePage.getVisibleGridItems();
            logger.info("📊 Final grid item count after filters (no category): {}", Integer.valueOf(rows.size()));
            for (int i = 0; i < rows.size(); i++) {
                logger.info("   • [{}] {}", Integer.valueOf(i + 1), rows.get(i));
            }
            logToAllure("📋 Filtered Grid Items (No Category)", rows.toString());
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Final_Filtered_Grid_NoCategory");

        } catch (Throwable t) {
            compliancePage.handleValidationException("Dept→DueDate (no category) filter flow", t);
        }
    }











    @When("the user clicks the {string} button")
    public void the_user_clicks_the_button(String btnText) throws InterruptedException {

        Thread.sleep(5000);
        Assert.assertTrue("Add new compliance".equalsIgnoreCase(btnText),
                "Unexpected button label passed to step: " + btnText);

        try {
            logStep("🖱 Clicking '" + btnText + "' button...");

            long startTime = System.currentTimeMillis();

            compliancePage.clickAddNewComplianceButton();

            long elapsedMs = System.currentTimeMillis() - startTime;
            String elapsedPretty = compliancePage.formatElapsed(elapsedMs);

            Allure.addAttachment("Click Time (" + btnText + ")", elapsedPretty);
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "After_Click_AddNewCompliance");

            // SLA: Enterprise Benchmarks
            long warnMs = ReusableCommonMethods.NAV_WARN_MS; // e.g., 8000ms
            long failMs = ReusableCommonMethods.NAV_FAIL_MS; // e.g., 15000ms

            if (elapsedMs >= failMs) {
                String failMsg = "Click response slow: " + elapsedPretty +
                        " (>= " + compliancePage.formatElapsed(failMs) + ").";
                logger.error(failMsg);
                Allure.addAttachment("❌ SLA Failure", failMsg);
                Assert.fail(failMsg);
            } else if (elapsedMs >= warnMs) {
                String warnMsg = "Click response slow: " + elapsedPretty +
                        " (>= " + compliancePage.formatElapsed(warnMs) + ").";
                logger.warn(warnMsg);
                Allure.addAttachment("⚠️ SLA Warning", warnMsg);
            }

            logger.info("✅ '{}' clicked successfully ({})", btnText, elapsedPretty);

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Click_AddNewCompliance_Error");
            logger.error("Failed to click '{}': {}", btnText, t.getMessage(), t);
            Allure.addAttachment("❌ Click Error", t.getMessage());
            Assert.fail("Unable to click '" + btnText + "' button. " + t.getMessage());
        }
    }

    @Then("the {string} side panel should appear")
    public void the_side_panel_should_appear(String expectedTitle) {

        Assert.assertTrue("Add new compliance".equalsIgnoreCase(expectedTitle),
                "Unexpected panel title passed to step: " + expectedTitle);

        try {
            logStep("🔎 Verifying '" + expectedTitle + "' side panel visibility...");

            long startTime = System.currentTimeMillis();

            boolean isVisible = compliancePage.isAddCompliancePanelVisible(
                    Duration.ofMillis(PANEL_WAIT_MS));

            long elapsedMs = System.currentTimeMillis() - startTime;
            String elapsedPretty = compliancePage.formatElapsed(elapsedMs);

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "AddCompliancePanel_State");
            Allure.addAttachment("Panel Load Time", elapsedPretty);

            long warnMs = ReusableCommonMethods.NAV_WARN_MS;
            long failMs = ReusableCommonMethods.NAV_FAIL_MS;

            if (!isVisible) {
                String failMsg = "'" + expectedTitle + "' panel NOT visible within "
                        + compliancePage.formatElapsed(PANEL_WAIT_MS) + ". Actual: " + elapsedPretty;
                logger.error(failMsg);
                Allure.addAttachment("❌ Panel Not Visible", failMsg);
                Assert.fail(failMsg);
            }

            // SLA logic
            if (elapsedMs >= failMs) {
                String failMsg = "Panel visible but slow: " + elapsedPretty +
                        " (>= " + compliancePage.formatElapsed(failMs) + ").";
                logger.error(failMsg);
                Allure.addAttachment("❌ SLA Failure", failMsg);
                Assert.fail(failMsg);
            } else if (elapsedMs >= warnMs) {
                String warnMsg = "Panel visible, but slow: " + elapsedPretty +
                        " (>= " + compliancePage.formatElapsed(warnMs) + ").";
                logger.warn(warnMsg);
                Allure.addAttachment("⚠️ SLA Warning", warnMsg);
            }

            logger.info("✅ '{}' panel is visible ({})", expectedTitle, elapsedPretty);
            Allure.step("Panel visible and validated.");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "AddCompliancePanel_Error");
            logger.error("Error during panel visibility validation: {}", t.getMessage(), t);
            Allure.addAttachment("❌ Panel Check Error", t.getMessage());
            Assert.fail("Panel visibility validation failed: " + t.getMessage());
        }
    }










    @And("the user enters a randomized valid compliance name")
    public void enter_randomized_name() {
        String name = TestDataGenerator.getRandomCompanyName();
        logStep("📝 Entering randomized compliance name: " + name);

        long t0 = System.currentTimeMillis();
        compliancePage.setName(name);
        long elapsed = System.currentTimeMillis() - t0;

        ScenarioState.setCreatedComplianceName(name); // <-- save for assertions

        Allure.addAttachment("Name", name);
        Allure.addAttachment("Name Entry Time", compliancePage.formatElapsed(elapsed));
        ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Name_Entered");
        Assert.assertTrue(name != null && !name.isEmpty(), "Generated name is empty.");
    }


    @And("the user selects a randomized valid frequency")
    public void select_random_frequency() {
        logStep("📅 Selecting randomized Frequency...");
        long t0 = System.currentTimeMillis();

        String chosen = compliancePage.selectRandomFrequency();

        long elapsed = System.currentTimeMillis() - t0;
        Allure.addAttachment("Frequency chosen", chosen);
        Allure.addAttachment("Frequency Selection Time", compliancePage.formatElapsed(elapsed));
        ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Frequency_Selected");
        Assert.assertTrue(chosen != null && chosen.length() > 0, "Frequency selection failed.");
    }


    @And("the user selects a randomized valid due date")
    public void select_random_due_date() {
        logStep("🗓 Selecting randomized Due Date (future)...");

        long t0 = System.currentTimeMillis();
        String picked = compliancePage.selectRandomDueDate(DUE_DATE_MAX_AHEAD_DAYS);
        long elapsed = System.currentTimeMillis() - t0;
        String pretty = compliancePage.formatElapsed(elapsed);

        Allure.addAttachment("Due Date (Chosen)", picked);
        Allure.addAttachment("Due Date Selection Time", pretty);
        ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "DueDate_Selected");

        Assert.assertNotNull(picked, "Due Date returned is null.");
        Assert.assertFalse(picked.isEmpty(), "Due date selection failed.");

        logger.info("📅 Random Due Date selected: {} (Time: {})", picked, pretty);
    }






    @And("the user selects a randomized valid risk")
    public void select_random_risk() {
        logStep("⚠️ Selecting randomized Risk...");
        long t0 = System.currentTimeMillis();

        String risk = compliancePage.selectRandomRisk();

        long elapsed = System.currentTimeMillis() - t0;
        Allure.addAttachment("Risk chosen", risk);
        Allure.addAttachment("Risk Selection Time", compliancePage.formatElapsed(elapsed));
        ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Risk_Selected");
        Assert.assertTrue(risk != null && risk.length() > 0, "Risk selection failed.");
    }

    @And("the user sets the {string} toggle per the randomized value")
    public void set_mandatory_toggle(String toggleName) {
        Assert.assertTrue("Mandatory".equalsIgnoreCase(toggleName),
                "Unexpected toggle name: " + toggleName);

        boolean desired = new java.util.Random().nextBoolean();
        logStep("🔘 Setting '" + toggleName + "' to: " + desired);

        long t0 = System.currentTimeMillis();
        compliancePage.setMandatory(desired);
        long elapsed = System.currentTimeMillis() - t0;

        Allure.addAttachment("Toggle '" + toggleName + "'", String.valueOf(desired));
        Allure.addAttachment("Toggle Action Time", compliancePage.formatElapsed(elapsed));
        ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Mandatory_Toggle_Set");
    }

    @And("the user selects randomized valid organization")
    public void select_random_orgs() {
        logStep("🏢 Selecting randomized organization...");
        long t0 = System.currentTimeMillis();
        String org = compliancePage.selectRandomOrganization();
        long elapsed = System.currentTimeMillis() - t0;

        Allure.addAttachment("Organization chosen", org);
        Allure.addAttachment("Organization Selection Time", compliancePage.formatElapsed(elapsed));
        ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Organization_Selected");
        Assert.assertTrue(org != null && !org.isEmpty(), "Organization selection failed.");
    }


    @And("the user enters a randomized description")
    public void enter_random_description() {
        String desc = TestDataGenerator.getRandomFeedbackMessage(); // reuse your generator
        logStep("🗒 Entering randomized description...");

        long t0 = System.currentTimeMillis();
        compliancePage.setDescription(desc);
        long elapsed = System.currentTimeMillis() - t0;

        Allure.addAttachment("Description length", String.valueOf(desc.length()));
        Allure.addAttachment("Description Entry Time", compliancePage.formatElapsed(elapsed));
        ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Description_Entered");
        Assert.assertTrue(desc.length() > 0, "Generated description is empty.");
    }


    @And("the user clicks Create button at the end")
    public void the_user_clicks_create_button() {
        logStep("🖱 Clicking 'Create' and waiting for Compliances...");

        try {
            // NOW PASS NEW 4-MIN FAIL TIME TO PAGE FUNCTION
            WaitOutcome out = compliancePage.clickCreateAndWait(
                    helperMethods,
                    60_000L,   // warn1 @ 1 min
                    120_000L,  // warn2 @ 2 min
                    180_000L,  // warn3 @ 3 min
                    240_000L   // fail @ 4 min
            );

            long elapsed = out.elapsedMs;
            String pretty = String.format("%.2f s", elapsed / 1000.0);

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "After_Create_Compliances");
            Allure.addAttachment("Create → Compliances Time", pretty);

            if (out.warned1)
                logToAllure("⚠️ 1 min warning", "Create → Compliances crossed 1 min. Final: " + pretty);

            if (out.warned2)
                logToAllure("⚠️ 2 min warning", "Create → Compliances crossed 2 min. Final: " + pretty);

            if (out.warned3)
                logToAllure("⚠️⚠️ 3 min warning", "Create → Compliances crossed 3 min. Final: " + pretty);

            logger.info("✅ Arrived on Compliances in {}", pretty);

        } catch (TimeoutException te) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Create_Timeout");
            logToAllure("❌ SLA Failure (4 min)", te.getMessage());
            Assert.fail(te.getMessage());
        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Create_Click_Error");
            logToAllure("❌ Create Error", t.getMessage());
            Assert.fail("Create flow failed. " + t.getMessage());
        }
    }





    @And("the user selects the entity associated with the created internal compliances")
    public void select_entity_associated_with_created_compliances() {
        logStep("🏢 Selecting entity associated with created internal compliances...");

        try {
            String orgName = compliancePage.getLastSelectedOrganization();
            Assert.assertNotNull(orgName, "No organization stored from previous step.");

            long t0 = System.currentTimeMillis();
            compliancePage.filterByOrganization(orgName);
            long elapsed = System.currentTimeMillis() - t0;

            Allure.addAttachment("Entity (Organization) applied", orgName);
            Allure.addAttachment("Filter time", compliancePage.formatElapsed(elapsed));
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Filtered_By_Organization");

            logger.info("✅ Filtered by organization '{}'", orgName);
        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Filter_Organization_Error");
            logger.error("❌ Filtering by organization failed: {}", t.getMessage(), t);
            Allure.addAttachment("❌ Filter Error", t.getMessage());
            Assert.fail("Filtering by organization failed: " + t.getMessage());
        }
    }


    @And("the user clears the Due Date filter")
    public void clear_due_date_filter() {
        logStep("🧹 Clearing the Due Date filter...");
        long t0 = System.currentTimeMillis();
        try {
            compliancePage.clearDueDateFilter(helperMethods);
            long elapsed = System.currentTimeMillis() - t0;
            String pretty = String.format("%.2f s", elapsed / 1000.0);
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "After_Clear_DueDate");
            io.qameta.allure.Allure.addAttachment("Due Date → Clear Time", pretty);
            logger.info("✅ Due Date filter cleared in {}", pretty);
        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Clear_DueDate_Error");
            logger.error("❌ Failed to clear Due Date filter: {}", t.getMessage(), t);
            io.qameta.allure.Allure.addAttachment("❌ Due Date Clear Error", t.getMessage());
            Assert.fail("Clearing Due Date filter failed. " + t.getMessage());
        }
    }



    @And("the user toggles the Internal filter on")
    public void toggle_internal_on() {
        logStep("🔁 Toggling Internal filter ON...");
        long t0 = System.currentTimeMillis();

        try {
            compliancePage.setInternalFilter(true);

            long ms = System.currentTimeMillis() - t0;
            String pretty = compliancePage.formatElapsed(ms);

            // step-only artifacts
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Internal_Filter_ON");
            io.qameta.allure.Allure.addAttachment("Internal Filter Toggle Time", pretty);

            logger.info("✅ Internal filter toggled ON ({})", pretty);
        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Internal_Filter_ON_Failure");
            logger.error("❌ Failed to toggle Internal ON: {}", t.getMessage(), t);
            logToAllure("❌ Internal Toggle Error", t.getMessage());
            Assert.fail("Toggling Internal filter ON failed. " + t.getMessage());
        }
    }



    @Then("the created internal compliances should be visible in the compliances list")
    public void verify_created_internal_compliance_visible() {
        // Retrieve the name from ScenarioState
        String expected = ScenarioState.getCreatedComplianceName();
        Assert.assertNotNull(expected, "No created compliance name found in ScenarioState.");

        logStep("🔎 Verifying created Internal compliance is listed: " + expected);
        logger.info("🧾 Expected Compliance Name: {}", expected);

        long t0 = System.currentTimeMillis();
        boolean found = compliancePage.waitForComplianceInList(expected, Duration.ofSeconds(20));
        long elapsed = System.currentTimeMillis() - t0;
        String pretty = compliancePage.formatElapsed(elapsed);

        // Always attach screenshot
        ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Compliance_List_Check");

        // Add Allure logs
        logToAllure("Compliance Name (Expected)", expected);
        logToAllure("Verification Time", pretty);
        logToAllure("Verification Result", found ? "✅ Found" : "❌ Not Found");

        if (found) {
            logger.info("✅ Created Internal Compliance '{}' is visible in the list. ({}).", expected, pretty);
        } else {
            logger.error("❌ Compliance '{}' not found in the list after {}.", expected, pretty);
            Assert.fail("Expected to find compliance named '" + expected + "' in the list, but it was not present.");
        }
    }


    @And("print only the created internal compliance across all pages")
    public void print_only_created_internal_compliance_across_pages() {
        try {
            String expectedName = ScenarioState.getCreatedComplianceName();
            Assert.assertNotNull(expectedName, "No created compliance name found in ScenarioState.");

            logger.info("🎯 Printing ONLY created Internal compliance '{}' across ALL pages.", expectedName);
            logToAllure("🎯 Expected Created Compliance (print only)", expectedName);

            // 1️⃣ Get total count from All tab
            int displayedTotal = compliancePage.getStageSectionTabCount("All");

            // 2️⃣ Collect rows across pages + screenshot for each page (without lambda)
            Map<Integer, List<ComplianceRow>> pageWise =
                    compliancePage.fetchAllRowsAcrossPages(
                            displayedTotal,
                            new PageNavigationCallback() {
                                @Override
                                public void onPage(int pageNo) {
                                    // This executes while that page is shown
                                    ScreenshotUtils.attachScreenshotToAllure(
                                            Hooks.driver,
                                            "Compliances_Page_" + pageNo
                                    );
                                }
                            },
                            null   // no timing callback
                    );

            // 3️⃣ Cache for the next verification step
            ScenarioState.setAllComplianceRows(pageWise);

            String expectedCanon = compliancePage.canonicalComplianceName(expectedName);

            StringBuilder sb = new StringBuilder();
            sb.append("🎯 Created compliance occurrences across pages:\n");

            boolean foundAny = false;

            // 4️⃣ Classic for-each over map entries
            for (Map.Entry<Integer, List<ComplianceRow>> entry : pageWise.entrySet()) {
                int pageNo = entry.getKey().intValue();
                List<ComplianceRow> rows = entry.getValue();
                if (rows == null || rows.isEmpty()) {
                    continue;
                }

                for (int i = 0; i < rows.size(); i++) {
                    ComplianceRow cr = rows.get(i);
                    if (cr == null) {
                        continue;
                    }

                    String name = (cr.getName() == null) ? "" : cr.getName().trim();
                    String canon = compliancePage.canonicalComplianceName(name);

                    if (!canon.isEmpty()
                            && (canon.equals(expectedCanon)
                            || canon.contains(expectedCanon)
                            || expectedCanon.contains(canon))) {

                        foundAny = true;

                        String office  = (cr.getOffice()   == null) ? "" : cr.getOffice().trim();
                        String dueDate = (cr.getDueDate() == null) ? "" : cr.getDueDate().trim();

                        sb.append(String.format(
                                "  [Page %d, Row %d] Name: %s | Office: %s | Due Date: %s%n",
                                pageNo, (i + 1), name, office, dueDate
                        ));
                    }
                }
            }

            if (foundAny) {
                String msg = sb.toString();
                logger.info(msg);
                logToAllure("🎯 Created Compliance Row(s) Across Pages", msg);

                // Optional: one more summary screenshot (you already have per-page shots)
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Created_Compliance_Row_Only_Summary");

            } else {
                String msg = "❌ Created compliance '" + expectedName + "' not found on any page.";
                logger.error(msg);
                logToAllure("❌ Created Compliance Not Found (print only)", msg);
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Created_Compliance_Not_Found_PrintOnly");
                Assert.fail(msg);
            }

        } catch (Throwable t) {
            compliancePage.handleValidationException("Print created internal compliance across pages", t);
        }
    }













    @And("the created internal compliance row should exist across all pages")
    public void verify_created_internal_row_across_pages() {
        try {
            String expectedName = ScenarioState.getCreatedComplianceName();
            Assert.assertNotNull(expectedName, "No created compliance name found in ScenarioState.");

            logger.info("🔎 Verifying created Internal compliance '{}' exists across ALL pages.", expectedName);
            logToAllure("🔎 Expected Compliance Name (row search)", expectedName);

            // 1️⃣ Prefer the snapshot collected in the previous step
            Map<Integer, List<ComplianceRow>> pageWise = ScenarioState.getAllComplianceRows();

            // Fallback (should rarely happen): re-collect from UI
            if (pageWise == null || pageWise.isEmpty()) {
                logger.warn("⚠️ No cached compliance rows in ScenarioState – recollecting from UI.");
                int displayedTotal = compliancePage.getStageSectionTabCount("All");
                pageWise = compliancePage.fetchAllRowsAcrossPages(displayedTotal, null, null);
            }

            String expectedCanon = compliancePage.canonicalComplianceName(expectedName);

            boolean found = false;
            String foundOffice = "";
            String foundDue    = "";
            int foundPage  = -1;
            int foundIndex = -1;

            for (Map.Entry<Integer, List<ComplianceRow>> e : pageWise.entrySet()) {
                Integer pageNo = e.getKey();
                List<ComplianceRow> rows = e.getValue();
                if (rows == null) continue;

                for (int i = 0; i < rows.size(); i++) {
                    ComplianceRow cr = rows.get(i);
                    if (cr == null) continue;

                    String name = (cr.getName() == null) ? "" : cr.getName().trim();
                    String nameCanon = compliancePage.canonicalComplianceName(name);

                    if (!nameCanon.isEmpty()
                            && (nameCanon.equals(expectedCanon)
                            || nameCanon.contains(expectedCanon)
                            || expectedCanon.contains(nameCanon))) {

                        found = true;
                        foundPage  = pageNo;
                        foundIndex = i + 1;
                        foundOffice = (cr.getOffice()   == null) ? "" : cr.getOffice().trim();
                        foundDue    = (cr.getDueDate() == null) ? "" : cr.getDueDate().trim();
                        break;
                    }
                }
                if (found) break;
            }

            if (found) {
                String summaryMsg = "✅ Created compliance row found on page "
                        + foundPage + " at index " + foundIndex
                        + " | Office='" + foundOffice + "', DueDate='" + foundDue + "'";

                logger.info(summaryMsg);
                logToAllure("✅ Created Compliance Row", summaryMsg);

                String exactRow =
                        "Name    : " + expectedName + System.lineSeparator() +
                                "Office  : " + foundOffice  + System.lineSeparator() +
                                "Due Date: " + foundDue;

                logger.info("🎯 Exact created compliance row:\n{}", exactRow);
                logToAllure("🎯 Exact Created Compliance Row (Name / Office / Due Date)", exactRow);

                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Created_Compliance_Row_Exact");

            } else {
                String msg = "❌ Created compliance '" + expectedName + "' not found in any page rows.";
                logger.error(msg);
                logToAllure("❌ Created Compliance Row", msg);
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Created_Compliance_Not_Found_AllPages");
                Assert.fail(msg);
            }

        } catch (Throwable t) {
            compliancePage.handleValidationException("Created internal row presence (all pages)", t);
        }
    }








}


