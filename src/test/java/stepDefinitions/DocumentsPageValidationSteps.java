package stepDefinitions;


import hooks.Hooks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.DocumentPage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.Duration;

import org.testng.asserts.SoftAssert;
import utils.*;

import java.util.*;

import static utils.AllureLoggerUtils.logToAllure;

/**
 * @author Sherwin
 * @since 10-09-2025
 */

public class DocumentsPageValidationSteps {

    WebDriver driver = Hooks.driver;
    DocumentPage documentPage;
    Logger logger;
    WebDriverWait wait;
    AllureLoggerUtils allureLogging;
    ReusableCommonMethods helperMethods;
    private String selectedDocTitle;
    private String lastCreatedFolderName;

    public DocumentsPageValidationSteps() {
        this.driver = Hooks.driver;
        this.documentPage = new DocumentPage(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.logger = LoggerUtils.getLogger(getClass());
        this.helperMethods = new ReusableCommonMethods(driver);
    }

    @Step("{message}")
    public void logStep(String message) {
        logger.info(message);
    }


    @When("the user navigates to the Documents page")
    public void the_user_navigates_to_the_documents_page() {
        SoftAssert softAssert = new SoftAssert();
        final String target = "Documents";

        try {
            logToAllure("🧭 Navigation", "Navigating to: " + target);
            logStep("Navigating to: " + target);

            // 1) Start timers BEFORE the click
            Instant navStart = Instant.now();
            NavContext.start(target);

            // 2) Click target
            documentPage.clickDocumentsTab();

            // 3) Wait up to NAV_FAIL_MS for page "ready"
            Duration maxWait = Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS);
            boolean success = documentPage.waitForDocumentsPageToLoad(maxWait);

            // 4) Stop & log timing via your helper
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs(target, navStart);
            double elapsedSec = elapsedMs / 1000.0;

            // 5) Result logging + screenshot
            String resultMsg = "Navigated to " + target + " → " + success;
            logger.info("{} in {}s", resultMsg, String.format("%.2f", elapsedSec));
            logToAllure("📄 Documents Navigation", resultMsg + " (took " + String.format("%.2f s", elapsedSec) + ")");
            logStep(resultMsg + " in " + String.format("%.2f s", elapsedSec));
            ScreenshotUtils.attachScreenshotToAllure(driver, "Nav_" + target.replaceAll("\\s+", "_"));

            // 6) Threshold handling (warn/fail by SLA)
            if (success) {
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("%s took %.2f s — more than %d s. Failing (SLA %ds).", target, elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Failure", failMsg);
                    softAssert.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("%s took %.2f s — more than %d s.", target, elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                    // soft warning only
                }

                // Optional: refresh to normalize state for subsequent steps
                driver.navigate().refresh();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }

            } else {
                String failMsg = String.format("Unable to reach/verify %s within %d s.", target, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                logger.error(failMsg);
                logToAllure("❌ Navigation Failure", failMsg + " (after " + String.format("%.2f s", elapsedSec) + ")");
                ScreenshotUtils.attachScreenshotToAllure(driver, "NavFail_" + target.replaceAll("\\s+", "_"));
                softAssert.fail(failMsg);
            }

            softAssert.assertAll();

        } catch (Throwable t) {
            String errorMsg = "Exception during navigation to '" + target + "': " + t.getMessage();
            logger.error(errorMsg, t);
            logToAllure("❌ Navigation Error", errorMsg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "NavException_" + target.replaceAll("\\s+", "_"));

            // If you centralize handling here (as your ref step does):
            try {
                documentPage.handleValidationException("Navigation to " + target, t);
            } catch (Throwable ignored) {
                // fall-through so test still fails visibly
            }

            // Ensure failure bubbles to the runner
            throw t;
        }
    }


    @Then("the page should show the following sections")
    public void the_page_should_show_the_following_sections(DataTable dataTable) {
        SoftAssert softAssert = new SoftAssert();
        try {
            // Build expected list (normalize spacing)
            List<String> raw = dataTable.asList();
            List<String> expected = new ArrayList<String>();
            for (int i = 0; i < raw.size(); i++) {
                String s = raw.get(i);
                if (s == null) s = "";
                s = s.trim().replaceAll("\\s+", " ");
                expected.add(s);
            }

            logToAllure("📋 Expected Sections", expected.toString());
            logger.info("Verifying sections: {}", expected);

            // Optional: log the currently visible sections for context
            List<String> actual = documentPage.getVisibleSections();
            logToAllure("👀 Visible Sections (actual)", actual.toString());
            logger.info("Visible sections: {}", actual);

            // Verify each expected section is visible (case-insensitive wait in POM)
            for (int i = 0; i < expected.size(); i++) {
                String sec = expected.get(i);
                boolean ok = documentPage.waitForSectionVisible(sec, 10); // 10s per section
                if (ok) {
                    logStep("✅ Section visible: " + sec);
                } else {
                    String msg = "Section NOT visible: " + sec;
                    logger.error(msg);
                    logToAllure("❌ Missing Section", msg);
                    ScreenshotUtils.attachScreenshotToAllure(driver, "MissingSection_" + sec.replaceAll("\\s+", "_"));
                    softAssert.fail(msg);
                }
            }

            logToAllure("✅ Section Validation", "Completed verification of sections.");
            logStep("Completed verification of sections.");
            softAssert.assertAll();

        } catch (Throwable t) {
            logger.error("Error during section verification: {}", t.toString(), t);
            logToAllure("❌ Section Verification Error", t.toString());
            ScreenshotUtils.attachScreenshotToAllure(driver, "Sections_Error");
            try {
                documentPage.handleValidationException("Section verification", t);
            } catch (Throwable ignored) {
            }
            throw t;
        }
    }

    @Then("the {string} section is active by default")
    public void the_section_is_active_by_default(String sectionName) {
        SoftAssert softAssert = new SoftAssert();
        try {
            String expected = (sectionName == null) ? "" : sectionName.trim().replaceAll("\\s+", " ");
            logToAllure("🔎 Active Section Check", "Expecting active: " + expected);
            logger.info("Verifying default active section: '{}'", expected);

            boolean active = documentPage.isSectionActive(expected, 10); // 10s visibility wait
            if (active) {
                logStep("✅ Section is active by default: " + expected);
                logToAllure("✅ Active by Default", expected);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ActiveSection_" + expected.replaceAll("\\s+", "_"));

            } else {
                String msg = "Section is NOT active by default: " + expected;
                logger.error(msg);
                logToAllure("❌ Not Active by Default", msg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "SectionNotActive_" + expected.replaceAll("\\s+", "_"));
                softAssert.fail(msg);
            }

            softAssert.assertAll();

        } catch (Throwable t) {
            logger.error("Exception during active-section verification: {}", t.toString(), t);
            logToAllure("❌ Active Section Error", t.toString());
            ScreenshotUtils.attachScreenshotToAllure(driver, "ActiveSection_Exception");
            try {
                documentPage.handleValidationException("Active section verification", t);
            } catch (Throwable ignored) {
            }
            throw t;
        }
    }


    @Then("the following folders are displayed in the Documents section")
    public void the_following_folders_are_displayed_in_the_documents_section(io.cucumber.datatable.DataTable table) {
        org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();
        try {
            // ✅ Make a modifiable copy; DataTable.asList() is unmodifiable
            java.util.List<String> incoming = table.asList();
            java.util.List<String> expected = new java.util.ArrayList<String>(incoming.size());
            for (int i = 0; i < incoming.size(); i++) {
                String s = incoming.get(i);
                if (s == null) s = "";
                expected.add(s.trim().replaceAll("\\s+", " "));
            }

            logToAllure("📋 Expected Folders", expected.toString());
            logger.info("Verifying folders in Documents section: {}", expected);

            // Optional context
            java.util.List<String> visible = documentPage.getVisibleFolderNames();
            logger.info("👀 Visible folders currently: {}", visible);
            logToAllure("👀 Visible Folders (actual)", visible.toString());

            // Check each folder
            for (int i = 0; i < expected.size(); i++) {
                String folder = expected.get(i);
                boolean ok = documentPage.waitForFolderVisible(folder, 12);
                if (ok) {
                    logStep("✅ Folder visible: " + folder);
                    logToAllure("✅ Folder Visible", folder);
                    ScreenshotUtils.attachScreenshotToAllure(driver, "FolderVisible_" + folder.replaceAll("\\s+", "_"));
                } else {
                    String msg = "Folder NOT visible: " + folder;
                    logger.error(msg);
                    logToAllure("❌ Folder Missing", msg);
                    ScreenshotUtils.attachScreenshotToAllure(driver, "FolderMissing_" + folder.replaceAll("\\s+", "_"));
                    softAssert.fail(msg);
                }
            }

            logStep("Completed folder visibility verification.");
            logToAllure("✅ Folder Verification", "Completed verification for expected folders.");
            softAssert.assertAll();

        } catch (Throwable t) {
            logger.error("Error during folder verification: {}", t.toString(), t);
            logToAllure("❌ Folder Verification Error", t.toString());
            ScreenshotUtils.attachScreenshotToAllure(driver, "Folders_Exception");
            try {
                documentPage.handleValidationException("Folder verification", t);
            } catch (Throwable ignored) {
            }
            throw t;
        }
    }

    @When("the user opens the {string} tab and the page is ready")
    public void user_click_the_tab(String tabName) {
        SoftAssert softAssert = new SoftAssert();
        final String trimmedTab = (tabName == null) ? "" : tabName.trim();

        if (trimmedTab.isEmpty()) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Tab_Click_Invalid_Name");
            throw new AssertionError("Tab name is empty/null.");
        }

        try {
            logToAllure("🖱️ Tab to Click", trimmedTab);
            logStep("Click & navigate to tab: " + trimmedTab);

            java.time.Instant navStart = java.time.Instant.now();
            NavContext.start(trimmedTab);

            boolean success = false;

            // ✅ Support multiple tabs
            if ("Legal doc generator".equalsIgnoreCase(trimmedTab)) {
                success = documentPage.clickAndVerifyNavigation(); // (rename to this wrapper if you like)
            } else if ("My documents".equalsIgnoreCase(trimmedTab)) {
                success = documentPage.clickAndVerifyMyDocumentsTab();
            } else {
                String msg = "No click/verify implementation for tab: " + trimmedTab;
                logger.warn(msg);
                logToAllure("ℹ️ Not Implemented", msg);
            }

            long elapsedMs = helperMethods.logLoadTimeAndReturnMs(trimmedTab, navStart);
            double elapsedSec = elapsedMs / 1000.0;

            String resultMsg = "Clicked and navigated to tab: " + trimmedTab + " → " + success;
            String loadTimeMsg = "Loading time (" + trimmedTab + "): " + String.format("%.2f s", elapsedSec);

            logger.info(resultMsg);
            logger.info(loadTimeMsg);
            logToAllure("📄 Tab Navigation", resultMsg);
            logToAllure("⏱️ " + trimmedTab + " Load Time", loadTimeMsg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Tab_" + trimmedTab.replaceAll("\\s+", "_"));

            if (success) {
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("%s took %.2f s — FAIL (SLA %ds).", trimmedTab, elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Failure", failMsg);
                    softAssert.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("%s took %.2f s — WARN (> %ds).", trimmedTab, elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                }

                try {
                    driver.navigate().refresh();
                    Thread.sleep(2000L);
                } catch (InterruptedException ignored) {
                }
            } else {
                String failMsg = String.format("Unable to access or verify tab: %s", trimmedTab);
                logger.error(failMsg);
                logToAllure("❌ Navigation Failure", failMsg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "TabFail_" + trimmedTab.replaceAll("\\s+", "_"));
                softAssert.fail(failMsg);
            }

            softAssert.assertAll();

        } catch (Throwable t) {
            String errorMsg = "Exception for tab '" + trimmedTab + "': " + t.getMessage();
            logger.error(errorMsg, t);
            logToAllure("❌ Tab Click Error", errorMsg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "TabException_" + trimmedTab.replaceAll("\\s+", "_"));

            try {
                documentPage.handleValidationException("Tab navigation: " + trimmedTab, t);
            } catch (Throwable ignored) {
            }
            throw t;
        }
    }


    @Then("the user should see multiple legal document widgets")
    public void the_user_should_see_multiple_legal_document_widgets() {
        try {
            SoftAssert softAssert = new SoftAssert();

            // 1) Count widgets
            int count = documentPage.getWidgetCount();
            logger.info("Detected {} legal document widgets.", count);
            logToAllure("📦 Widget Count", String.valueOf(count));
            ScreenshotUtils.attachScreenshotToAllure(driver, "Widgets_Grid");

            if (count < 2) {
                softAssert.fail("Expected multiple widgets, but found only: " + count);
            }

            // 2) Fetch and print titles
            List<String> titles = documentPage.getAllDocumentTitles();
            if (titles == null || titles.isEmpty()) {
                String msg = "No document titles found in Legal doc generator grid.";
                logger.error("❌ {}", msg);
                logToAllure("❌ Document Titles", msg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "DocumentTitles_Empty");
                softAssert.fail(msg);
            } else {
                logger.info("📑 Document titles ({}):", titles.size());
                StringBuilder sb = new StringBuilder();
                for (String t : titles) {
                    logger.info("   • {}", t);
                    sb.append("• ").append(t).append("\n");
                }
                logToAllure("📑 Document Titles", sb.toString());
                ScreenshotUtils.attachScreenshotToAllure(driver, "DocumentTitles_List");
            }

            // 3) (Optional) sanity: titles count should be <= widget count
            if (titles != null && count > 0 && titles.size() > count) {
                softAssert.fail("Titles detected (" + titles.size() + ") exceed widget count (" + count + ").");
            }

            softAssert.assertAll();

        } catch (Throwable t) {
            String err = "Exception while verifying widgets & titles: " + t.getMessage();
            logger.error(err, t);
            logToAllure("❌ Widgets/Titles Error", err);
            ScreenshotUtils.attachScreenshotToAllure(driver, "WidgetsTitles_Exception");
            documentPage.handleValidationException("Widgets & Titles verification", t);
        }
    }


    @Then("each legal document widget should have a visible button labeled {string}")
    public void each_legal_document_widget_should_have_a_visible_button_labeled(String expectedLabel) {
        try {
            SoftAssert softAssert = new SoftAssert();

            java.util.List<String> missing = documentPage.widgetsMissingCreateButton();
            if (missing != null && !missing.isEmpty()) {
                for (String m : missing) {
                    logger.error("Button missing/hidden: {}", m);
                    logToAllure("❌ Missing Button", m);
                }
                ScreenshotUtils.attachScreenshotToAllure(driver, "CreateButtons_Missing");
                softAssert.fail("Some widgets do not have a visible '" + expectedLabel + "' button: " + missing);
            } else {
                logger.info("All widgets contain a visible '{}' button.", expectedLabel);
                logToAllure("✅ Buttons OK", "Every widget has a visible '" + expectedLabel + "' button.");
                ScreenshotUtils.attachScreenshotToAllure(driver, "CreateButtons_AllVisible");
            }

            softAssert.assertAll();

        } catch (Throwable t) {
            String err = "Exception while validating '" + expectedLabel + "' buttons: " + t.getMessage();
            logger.error(err, t);
            logToAllure("❌ Button Validation Error", err);
            ScreenshotUtils.attachScreenshotToAllure(driver, "CreateButtons_Exception");
            documentPage.handleValidationException("'Create document' button check", t);
        }
    }

    @When("the user clicks any legal document widget")
    public void the_user_clicks_any_legal_document_widget() {
        SoftAssert softAssert = new SoftAssert();

        // (A) Gather & log titles (as you already do)
        List<String> titles = documentPage.getAllDocumentTitles();
        if (titles == null || titles.isEmpty()) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "No_Titles_Found");
            throw new AssertionError("No document titles found to click.");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < titles.size(); i++) {
            sb.append(i).append(") ").append(titles.get(i)).append("\n");
        }
        logger.info("📑 Legal document titles ({}):\n{}", Integer.valueOf(titles.size()), sb.toString());
        logToAllure("📑 Legal Document Titles", sb.toString());
        ScreenshotUtils.attachScreenshotToAllure(driver, "Grid_Before_Click");

        // (B) Pick random and log baseline window/URL (diagnostic)
        int index = new java.util.Random().nextInt(titles.size());
        selectedDocTitle = titles.get(index).trim();

        String handleBefore = driver.getWindowHandle();
        int windowsBefore = driver.getWindowHandles().size();
        String urlBefore = driver.getCurrentUrl();

        logger.info("🎯 Random pick index={} => '{}'", Integer.valueOf(index), selectedDocTitle);
        logToAllure("🎯 Random Pick", "Index: " + index + "\nTitle: " + selectedDocTitle);
        ScreenshotUtils.attachScreenshotToAllure(driver, "Before_Click_" + selectedDocTitle);

        // (C) ⏱️ Start the precise timer RIGHT BEFORE the click -> ready sequence
        long clickStartMs = System.currentTimeMillis();

        // Do the click + wait for form (internally: wait for new tab/same tab, then waitForFormReady)
        documentPage.clickTitleAndOpenForm(index, selectedDocTitle, "doc_name");

        // (D) Stop timer as soon as POM returns (form is ready now)
        long clickToOpenMs = System.currentTimeMillis() - clickStartMs;
        double clickToOpenS = clickToOpenMs / 1000.0;

        // (E) Post state and nav mode (diagnostic)
        int windowsAfter = driver.getWindowHandles().size();
        String urlAfter = driver.getCurrentUrl();
        String navMode = windowsAfter > windowsBefore ? "NEW_TAB" : "SAME_TAB";

        logger.info("🧭 Navigation mode: {} | windows {} -> {} | URL After: {}", navMode, Integer.valueOf(windowsBefore), Integer.valueOf(windowsAfter), urlAfter);

        // (F) Allure + screenshot for timing
        String timingMsg = "Click→Open time (" + selectedDocTitle + "): " + String.format("%.2f s", clickToOpenS);
        logToAllure("⏱️ Document Open Time", timingMsg);
        logToAllure("🧭 Navigation Details", "Mode: " + navMode + "\nWindows: " + windowsBefore + " -> " + windowsAfter + "\nURL Before: " + urlBefore + "\nURL After: " + urlAfter);
        logger.info(timingMsg);
        ScreenshotUtils.attachScreenshotToAllure(driver, "After_Click_FormOrNewTab");

        // (G) SLA thresholds (soft warn/fail, same style as your other step)
        if (clickToOpenMs >= ReusableCommonMethods.NAV_FAIL_MS) {
            String failMsg = String.format("Opening '%s' took %.2f s — more than %d s. Failing (SLA %ds).", selectedDocTitle, clickToOpenS, ReusableCommonMethods.NAV_FAIL_MS / 1000, ReusableCommonMethods.NAV_FAIL_MS / 1000);
            logger.error(failMsg);
            logToAllure("❌ Document Open Time Failure", failMsg);
            softAssert.fail(failMsg);

        } else if (clickToOpenMs >= ReusableCommonMethods.NAV_WARN_MS) {
            String warnMsg = String.format("Opening '%s' took %.2f s — more than %d s.", selectedDocTitle, clickToOpenS, ReusableCommonMethods.NAV_WARN_MS / 1000);
            logger.warn(warnMsg);
            logToAllure("⚠️ Document Open Time Warning", warnMsg);
            // soft warning only
        }

        // (H) Aggregate soft assertions for this step
        softAssert.assertAll();
    }

    @Then("the opened form title matches the Selected document")
    public void the_opened_form_title_matches_the_clicked_document_name() {
        final String context = "Form title vs selected document";
        SoftAssert softAssert = new SoftAssert();

        try {
            // 1) Wait until form is ready
            documentPage.waitForFormReady("doc_name");

            // 2) Gather values
            String expectedRaw = selectedDocTitle;
            String actualTitle = documentPage.getOpenedFormTitleSafe(); // header or URL ?doc_name=
            String url = driver.getCurrentUrl();

            String expectedN = documentPage.normalize(expectedRaw);
            String actualN = documentPage.normalize(actualTitle);

            // 3) Logging + Allure
            logger.info("Selected (expected): '{}'", expectedRaw);
            logger.info("Form page (actual) : '{}'", actualTitle);
            logger.info("Form URL           : {}", url);

            logToAllure("🧾 Form Verification Inputs", "Expected : " + expectedRaw + "\nActual   : " + actualTitle + "\nURL      : " + url);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Form_Page");

            // 4) Validations
            // 4a) title should not be empty
            softAssert.assertTrue(actualN.length() > 0, "Form title/header is empty (could not read visible title).");

            // 4b) primary header vs clicked doc title
            boolean match = actualN.contains(expectedN) || expectedN.contains(actualN);
            softAssert.assertTrue(match, "Form title does not match.\nExpected: " + expectedRaw + "\nActual: " + actualTitle);

            // 4c) sanity check on URL
            softAssert.assertTrue(url != null && url.contains("doc_name="), "Form URL does not contain expected 'doc_name' token: " + url);

            // 5) Aggregate failures if any
            softAssert.assertAll();

        } catch (Throwable t) {
            // 6) Exception handling like reference
            String errorMsg = "Exception during " + context + ": " + t.getMessage();
            logger.error(errorMsg, t);
            logToAllure("❌ " + context, errorMsg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Form_Title_Exception");

            try {
                documentPage.handleValidationException(context, t);
            } catch (Throwable ignored) {
            }

            throw t; // bubble up so test fails
        }
    }


    @When("the user fills all sections and submits the form")
    public void fill_sections_and_submit() {
        final String context = "Fill form until Submit and post-submit redirect";
        SoftAssert softAssert = new SoftAssert();

        try {
            // -------- Part A: fill sections --------
            long fillStart = System.currentTimeMillis();
            int sectionsFilled = documentPage.fillFormUntilSubmitAppears();
            long fillElapsedMs = System.currentTimeMillis() - fillStart;

            logger.info("📝 Filled {} sections in {}s", sectionsFilled, String.format("%.2f", fillElapsedMs / 1000.0));
            logToAllure("📝 Form Filling Summary", "Sections filled : " + sectionsFilled + "\nDuration       : " + String.format("%.2f s", fillElapsedMs / 1000.0));
            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Fill_Form");

            softAssert.assertTrue(sectionsFilled > 0, "No sections were filled.");
            softAssert.assertTrue(documentPage.isSubmitVisible(), "Submit button was not visible after filling form sections.");

            // -------- Part B: click Submit and measure redirect to Documents hint --------
            // Start timing RIGHT BEFORE the click
            Instant navStart = Instant.now();
            NavContext.start("Post-Submit Redirect");

            documentPage.clickSubmit();
            logger.info("✅ Submit button clicked.");
            logToAllure("✅ Submit", "Submit button was clicked successfully.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Submit_Clicked");

            // Wait up to your standard nav fail SLA for the Documents hint
            Duration maxWait = Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS);
            boolean landed = documentPage.waitForDocumentsLanding(maxWait);

            // Stop & log timing via your helper (same pattern as your reference step)
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Post-Submit Redirect", navStart);
            double elapsedSec = elapsedMs / 1000.0;

            String resultMsg = "Redirected to Documents (hint visible) → " + landed;
            logger.info("{} in {}s", resultMsg, String.format("%.2f", elapsedSec));
            logToAllure("📄 Post-Submit Redirect", resultMsg + " (took " + String.format("%.2f s", elapsedSec) + ")");
            logStep(resultMsg + " in " + String.format("%.2f s", elapsedSec));
            ScreenshotUtils.attachScreenshotToAllure(driver, "Documents_Landing");

            // Threshold handling (same as your navigation step)
            if (landed) {
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("Post-submit redirect took %.2f s — more than %d s. Failing (SLA %ds).", elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Failure", failMsg);
                    softAssert.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("Post-submit redirect took %.2f s — more than %d s.", elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                }
            } else {
                String failMsg = String.format("Did not land on Documents page (hint) within %d s.", ReusableCommonMethods.NAV_FAIL_MS / 1000);
                logger.error(failMsg);
                logToAllure("❌ Redirect Failure", failMsg + " (after " + String.format("%.2f s", elapsedSec) + ")");
                ScreenshotUtils.attachScreenshotToAllure(driver, "Documents_Landing_Failed");
                softAssert.fail(failMsg);
            }

            // Validate the hint text explicitly
            String hintText = documentPage.getDocumentsHintTextSafe();
            logger.info("📌 Documents hint text: '{}'", hintText);
            softAssert.assertTrue(hintText.toLowerCase().contains("click on a file to view it"), "Expected hint 'Click on a file to view it' not visible on Documents page.");

            // Aggregate all validations
            softAssert.assertAll();

        } catch (Throwable t) {
            String errorMsg = "Exception during " + context + ": " + t.getMessage();
            logger.error(errorMsg, t);
            logToAllure("❌ " + context, errorMsg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Post_Submit_Exception");
            try {
                documentPage.handleValidationException(context, t);
            } catch (Throwable ignored) {
            }
            throw t;
        }
    }

    @Then("the generated document should be present in the Documents section")
    public void verify_generated_document_present() {
        final String context = "Generated document presence validation";
        SoftAssert softAssert = new SoftAssert();
        try {
            Duration maxWait = Duration.ofSeconds(30);

            // ✅ normalize human title for the search box
            String searchText = DocumentPage.buildNameQueryForSearch(selectedDocTitle);
            logger.info("🔍 Searching Documents with name query: '{}'", searchText);
            logToAllure("🔍 Generated Document Name Query", searchText);

            boolean found = documentPage.isGeneratedDocumentPresentUsingTitle(searchText, maxWait);

            logger.info("📄 Document present with name '{}' ? {}", searchText, found);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Generated_Document_Search_By_Name");

            softAssert.assertTrue(found, "Expected generated document not found when searching by name: " + searchText);
            softAssert.assertAll();
        } catch (Throwable t) { /* unchanged */ }
    }


    @Then("the generated document can be opened and downloaded")
    public void open_and_download_generated_document() throws InterruptedException {
        final String context = "Open & Download generated PDF from Documents search results";
        SoftAssert softAssert = new SoftAssert();

        // numbered driver screenshots after each action
        class Shots {
            int n = 1;

            void shot(String label) {
                ScreenshotUtils.attachScreenshotToAllure(driver, String.format("%02d) %s", n++, label));
            }
        }
        Shots shots = new Shots();

        try {
            Duration maxWait = Duration.ofSeconds(30);

            // 1) Expected base
            logToAllure("Step 1: Build expected base", "selectedDocTitle = " + selectedDocTitle);
            String searchText = DocumentPage.buildNameQueryForSearch(selectedDocTitle);
            String expectedBase = documentPage.canonicalBase(searchText);
            logger.info("🔎 Will open a result matching base: '{}'", expectedBase);
            logToAllure("🔎 Open Target (base)", expectedBase);
            shots.shot("Documents landing (pre-open)");

            // 2) Try the modal path first
            logToAllure("Step 2: Try opening preview modal", "Waiting up to " + maxWait.getSeconds() + "s for preview header");
            boolean openedModal = documentPage.openFirstMatchingSearchResultAndWait(expectedBase, maxWait);
            shots.shot("After attempt to open preview modal");

            if (openedModal) {
                // 2a) Modal open — validate filename
                logToAllure("Step 3: Preview opened", "Validate preview filename vs expected base");
                String previewName = documentPage.getPreviewFilename();
                String previewBase = documentPage.canonicalBase(previewName);
                logger.info("🧾 Preview shows: raw='{}' | base='{}'", previewName, previewBase);
                logToAllure("🧾 Preview Filename", "Raw: " + previewName + "\nBase: " + previewBase);
                shots.shot("Preview header + filename");

                softAssert.assertTrue(previewBase.contains(expectedBase) || expectedBase.contains(previewBase), "Preview filename does not match generated doc.\nExpected base: " + expectedBase + "\nActual base: " + previewBase);

                // 2b) Download from modal
                logToAllure("Step 4: Download via preview", "Clicking preview download button");
                documentPage.clickPreviewDownload();
                shots.shot("Clicked Download (modal)");
                Path downloadDir = Paths.get(System.getProperty("download.dir", System.getProperty("user.dir") + "/downloads"));
                logToAllure("Step 5: Wait for PDF in downloads", "Directory = " + downloadDir.toString() + "\nMatch prefix = " + expectedBase);
                documentPage.attachDownloadDirListing(downloadDir, "Downloads BEFORE wait");
                boolean gotIt = documentPage.waitForPdfDownloaded(downloadDir, expectedBase, Duration.ofSeconds(30));
                documentPage.attachDownloadDirListing(downloadDir, "Downloads AFTER wait");
                softAssert.assertTrue(gotIt, "Downloaded file not found in: " + downloadDir);
                shots.shot("Downloads after modal download");

                // 2c) Close preview
                logToAllure("Step 6: Close preview modal", "Closing if still open");
                documentPage.closePreviewIfOpen();
                shots.shot("After closing preview modal");

            } else {
                // 3) Fallback path — direct row click + download detection
                logToAllure("Step 3 (Fallback): Open via table row", "No preview modal detected; clicking matching table row directly");
                logger.info("No preview modal detected; proceeding with direct row click.");
                shots.shot("Before direct row click");

                Path dlDir = Paths.get(System.getProperty("download.dir", System.getProperty("user.dir") + "/downloads"));

                logToAllure("Step 4 (Fallback): Wait for download", "Directory = " + dlDir.toString() + "\nPrefix = " + expectedBase);
                Thread.sleep(5000);
                boolean downloaded = documentPage.clickFirstMatchingRowAndWaitDownloadPrefix(expectedBase, dlDir, Duration.ofSeconds(45));
                shots.shot("After direct row click");
                Thread.sleep(5000);
                documentPage.attachDownloadDirListing(dlDir, "Downloads AFTER row click");

                softAssert.assertTrue(downloaded, "No matching PDF appeared in downloads (fallback). Expected base prefix: " + expectedBase);
            }

            // 4) Final assertions
            logToAllure("Step 7: Assertions", "Asserting all validations");
            softAssert.assertAll();
            shots.shot("End of step - success");

        } catch (Throwable t) {
            String msg = "Exception during " + context + ": " + t.getMessage();
            logger.error(msg, t);
            logToAllure("❌ " + context, msg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "!! Open_Download_Exception");
            try {
                documentPage.handleValidationException(context, t);
            } catch (Throwable ignored) {
            }
            throw t;
        }
    }


    @When("the user clicks the {string} button and sees options:")
    public void user_clicks_button_and_sees_options(String buttonName, DataTable table) {
        SoftAssert softAssert = new SoftAssert();
        final String btn = (buttonName == null) ? "" : buttonName.trim();

        if (btn.isEmpty()) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Btn_Invalid_Name");
            throw new AssertionError("Button name is empty/null.");
        }

        try {
            // intent + pre-click snap
            logToAllure("🖱️ Button to Click", btn);
            logStep("Click button & verify options: " + btn);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_Click_" + btn.replaceAll("\\s+", "_"));

            // expected options from the DataTable
            java.util.List<String> expected = table.asList();
            java.util.List<String> actual = new java.util.ArrayList<String>();

            java.time.Instant start = java.time.Instant.now();

            boolean success = false;
            if ("+ New".equalsIgnoreCase(btn) || "New".equalsIgnoreCase(btn)) {
                // Get actual visible items from the POM
                actual = documentPage.clickNewAndGetOptionsText();
                // compare ignoring order
                success = actual.containsAll(expected) && expected.containsAll(actual);
            } else {
                String msg = "No implementation for button: " + btn;
                logger.warn(msg);
                logToAllure("ℹ️ Not Implemented", msg);
            }

            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Button:" + btn, start);
            double elapsedSec = elapsedMs / 1000.0;

            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Click_" + btn.replaceAll("\\s+", "_"));

            logToAllure("📄 Options Check", "Expected: " + expected + "\nActual: " + actual + "\nMatch: " + success);
            logToAllure("⏱️ Button Action Time", String.format("%.2f s", elapsedSec));

            if (!success) {
                String failMsg = "Dropdown options mismatch for button: " + btn;
                logger.error(failMsg + " | expected=" + expected + ", actual=" + actual);
                logToAllure("❌ Verification Failure", failMsg + "\nExpected: " + expected + "\nActual: " + actual);
                ScreenshotUtils.attachScreenshotToAllure(driver, "BtnFail_" + btn.replaceAll("\\s+", "_"));
                softAssert.fail(failMsg);
            }

            softAssert.assertAll();

        } catch (Throwable t) {
            String errorMsg = "Exception clicking button '" + btn + "': " + t.getMessage();
            logger.error(errorMsg, t);
            logToAllure("❌ Button Click Error", errorMsg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "BtnException_" + btn.replaceAll("\\s+", "_"));
            throw t;
        }
    }



    @When("the user selects {string}")
    public void the_user_selects(String option) {
        SoftAssert sa = new SoftAssert();
        final String opt = (option == null) ? "" : option.trim();

        if (opt.isEmpty()) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Select_Option_Invalid");
            throw new AssertionError("Option is empty/null.");
        }

        try {
            logToAllure("🧩 Menu Option", opt);
            logStep("Select option from +New: " + opt);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_Select_" + opt.replaceAll("\\s+", "_"));

            Instant t0 = Instant.now();
            boolean opened = false;

            if ("Add new folder".equalsIgnoreCase(opt)) {
                opened = documentPage.openAddNewFolderModal();
            } else {
                String msg = "No handler for option: " + opt;
                logger.warn(msg);
                logToAllure("ℹ️ Not Implemented", msg);
            }

            long ms = helperMethods.logLoadTimeAndReturnMs("Select:" + opt, t0);
            logToAllure("⏱️ Timing", String.format("%s in %.2f s", opt, ms / 1000.0));
            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Select_" + opt.replaceAll("\\s+", "_"));

            if (!opened) {
                String fail = "Could not open create-folder modal via option: " + opt;
                logger.error(fail);
                logToAllure("❌ Failure", fail);
                ScreenshotUtils.attachScreenshotToAllure(driver, "SelectFail_" + opt.replaceAll("\\s+", "_"));
                sa.fail(fail);
            }

            sa.assertAll();
        } catch (Throwable t) {
            logToAllure("❌ Option Select Error", t.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "Select_Exception");
            throw t;
        }
    }

    @Then("the create folder pop-up should appear")
    public void the_create_folder_pop_up_should_appear() {
        SoftAssert sa = new SoftAssert();
        try {
            logToAllure("🔎 Verify", "Create-folder modal visible");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Modal_Visible");

            boolean visible = documentPage.isCreateFolderModalVisible();
            if (!visible) {
                String fail = "Create-folder modal not visible.";
                logger.error(fail);
                logToAllure("❌ Modal Failure", fail);
                ScreenshotUtils.attachScreenshotToAllure(driver, "Modal_Fail");
                sa.fail(fail);
            }

            sa.assertAll();
        } catch (Throwable t) {
            logToAllure("❌ Modal Check Error", t.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "Modal_Exception");
            throw t;
        }
    }


    /**
     * Creates a folder with a RANDOM name, with full timing + validations.
     */
    @When("the user creates a folder with random name")
    public void the_user_creates_a_folder_with_random_name() {
        SoftAssert softAssert = new SoftAssert();

        try {
            // 0) Generate & remember the random name
            final String randomName = utils.TestDataGenerator.getRandomFolderName();
            lastCreatedFolderName = randomName;

            logToAllure("🆕 Random Folder Name", randomName);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_Create_" + randomName.replaceAll("\\s+", "_"));

            // 1) Ensure the Create Folder modal is open (be resilient)
            if (!documentPage.isCreateFolderModalVisible()) {
                logToAllure("ℹ️ Modal", "Create-folder modal not visible. Opening via '+ New' → 'Add new folder'…");
                boolean opened = documentPage.openAddNewFolderModal();
                if (!opened) {
                    String fail = "Could not open Create Folder modal before creating: " + randomName;
                    logger.error(fail);
                    logToAllure("❌ Modal Open Failure", fail);
                    ScreenshotUtils.attachScreenshotToAllure(driver, "Create_ModalOpen_Fail");
                    Assert.fail(fail);
                }
            }

            // 2) Start timers BEFORE clicking Create
            Instant t0 = Instant.now();
            NavContext.start("CreateFolder:" + randomName);

            boolean created = documentPage.createFolder(randomName);

            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("CreateFolder:" + randomName, t0);
            double elapsedSec = elapsedMs / 1000.0;

            // 3) Logs & screenshots
            String result = "Create clicked for folder: " + randomName + " → " + created;
            logStep(result + " in " + String.format("%.2f s", elapsedSec));
            logger.info(result);
            logToAllure("✅ Create Folder", result + " (took " + String.format("%.2f s", elapsedSec) + ")");
            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Create_Click_" + randomName.replaceAll("\\s+", "_"));

            // 4) SLA handling (use NAV thresholds)
            if (created) {
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("Create folder took %.2f s — FAIL (SLA %ds).", elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Create SLA Failure", failMsg);
                    softAssert.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("Create folder took %.2f s — WARN (> %ds).", elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Create SLA Warning", warnMsg);
                }
            } else {
                String fail = "Failed to create folder: " + randomName;
                logger.error(fail);
                logToAllure("❌ Create Failure", fail + " (after " + String.format("%.2f s", elapsedSec) + ")");
                ScreenshotUtils.attachScreenshotToAllure(driver, "Create_Fail_" + randomName.replaceAll("\\s+", "_"));
                softAssert.fail(fail);
            }

            softAssert.assertAll();

        } catch (Throwable t) {
            String err = "Exception while creating random folder: " + t.getMessage();
            logger.error(err, t);
            logToAllure("❌ Create Exception", err);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Create_Exception");
            throw t;
        }
    }

    /**
     * Verifies we got auto-navigated into the folder we just created,
     * with the same timing + SLA pattern.
     */
    @Then("the user should be inside the created folder")
    public void the_user_should_be_inside_the_created_folder() {
        SoftAssert softAssert = new SoftAssert();
        final String expected = (lastCreatedFolderName == null || lastCreatedFolderName.isEmpty()) ? "<unknown>" : lastCreatedFolderName;

        try {
            logToAllure("🔎 Verify Inside Folder", "Expecting breadcrumb/header: " + expected);

            // 1) Start timers BEFORE the wait/verify
            Instant t0 = Instant.now();
            NavContext.start("VerifyInside:" + expected);

            boolean inside = documentPage.verifyNavigatedInsideFolder(expected);

            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("VerifyInside:" + expected, t0);
            double elapsedSec = elapsedMs / 1000.0;

            // 2) Logs & screenshots
            String result = "Inside created folder '" + expected + "' → " + inside;
            logStep(result + " in " + String.format("%.2f s", elapsedSec));
            logger.info(result);
            logToAllure("✅ Inside Folder Check", result + " (took " + String.format("%.2f s", elapsedSec) + ")");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Inside_Folder_" + expected.replaceAll("\\s+", "_"));

            // 3) SLA handling (use NAV thresholds again)
            if (inside) {
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("Verify inside folder took %.2f s — FAIL (SLA %ds).", elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Verify SLA Failure", failMsg);
                    softAssert.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("Verify inside folder took %.2f s — WARN (> %ds).", elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Verify SLA Warning", warnMsg);
                }
            } else {
                String fail = "Not inside created folder: " + expected;
                logger.error(fail);
                logToAllure("❌ Inside Folder Failure", fail + " (after " + String.format("%.2f s", elapsedSec) + ")");
                ScreenshotUtils.attachScreenshotToAllure(driver, "Inside_Folder_Fail_" + expected.replaceAll("\\s+", "_"));
                softAssert.fail(fail);
            }

            softAssert.assertAll();

        } catch (Throwable t) {
            String err = "Exception while verifying inside created folder '" + expected + "': " + t.getMessage();
            logger.error(err, t);
            logToAllure("❌ Inside Verify Exception", err);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Inside_Verify_Exception");
            throw t;
        }
    }


    // keep with your other fields
    private String lastUploadedFileName;
    private String lastUploadedFilePath;


    private String createSampleUploadFile() throws Exception {
        String stamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new java.util.Date());
        String name = "SampleDoc_" + stamp + ".txt";
        java.nio.file.Path dir = java.nio.file.Paths.get("target", "upload-samples");
        java.nio.file.Files.createDirectories(dir);
        java.nio.file.Path file = dir.resolve(name);
        java.nio.file.Files.writeString(file, "Automated upload file\nTimestamp=" + stamp + "\n#auto\n", java.nio.charset.StandardCharsets.UTF_8);
        lastUploadedFileName = name;
        lastUploadedFilePath = file.toAbsolutePath().toString();
        return lastUploadedFilePath;
    }

    @When("the upload dropzone is clickable")
    public void the_upload_dropzone_is_clickable() {
        SoftAssert sa = new SoftAssert();
        try {
            logToAllure("📤 Upload Modal", "Opening modal");
            documentPage.openUploadFilesModal();
            ScreenshotUtils.attachScreenshotToAllure(driver, "UploadModal_Open");

            boolean clickable = documentPage.isDropzoneClickable();
            logToAllure("🧪 Dropzone Clickable", String.valueOf(clickable));
            ScreenshotUtils.attachScreenshotToAllure(driver, "Dropzone_Check");

            sa.assertTrue(clickable, "Upload dropzone is NOT clickable.");
            sa.assertAll();
        } catch (Throwable t) {
            logToAllure("❌ Dropzone Check Error", t.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "Dropzone_Check_Exception");
            throw t;
        }
    }


    @When("the user uploads a sample file and clicks {string}")
    public void the_user_uploads_a_sample_file_and_clicks(String buttonText) {
        SoftAssert sa = new SoftAssert();
        try {
            // Only validate clickability now
            logToAllure("📤 Upload Modal", "Opening modal");
            documentPage.openUploadFilesModal();
            ScreenshotUtils.attachScreenshotToAllure(driver, "UploadModal_Open");

            boolean clickable = documentPage.isDropzoneClickable();
            logToAllure("🧪 Dropzone Clickable", String.valueOf(clickable));
            ScreenshotUtils.attachScreenshotToAllure(driver, "Dropzone_Check");

            sa.assertTrue(clickable, "Upload dropzone is NOT clickable.");
            sa.assertAll();
        } catch (Throwable t) {
            logToAllure("❌ Dropzone Check Error", t.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "Dropzone_Check_Exception");
            throw t;
        }
    }


}


