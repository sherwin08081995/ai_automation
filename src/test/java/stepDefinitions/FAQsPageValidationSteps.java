package stepDefinitions;

import hooks.Hooks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pages.CustomerProfilePanel;
import pages.FAQsPage;
import utils.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static utils.AllureLoggerUtils.logToAllure;

/**
 * @author Sherwin
 * @since 01-09-2025
 */
public class FAQsPageValidationSteps {

    WebDriver driver = Hooks.driver;
    FAQsPage faQsPage;
    Logger logger;
    WebDriverWait wait;
    AllureLoggerUtils allureLogging;
    CustomerProfilePanel customerProfilePanel;
    ReusableCommonMethods helperMethods;
    private Instant profileMenuNavStart;
    private final SoftAssert softAssert = new SoftAssert();

    public FAQsPageValidationSteps() {
        this.driver = Hooks.driver;
        this.faQsPage = new FAQsPage(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.logger = LoggerUtils.getLogger(getClass());
        this.helperMethods = new ReusableCommonMethods(driver);

    }

    @Step("{message}")
    public void logStep(String message) {
        logger.info(message);
    }

    @When("the user select {string} from the Customer profile panel")
    public void the_user_selects_from_customer_profile_panel(String menuName) {
        String trimmed = menuName.trim(); // expect "FAQs"
        try {
            // —— START TIMER AT THE CLICK ——
            NavContext.start(trimmed);

            logToAllure("🖱️ Click Menu", "About to click: " + trimmed);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_Click_" + trimmed.replaceAll("\\s+", "_"));

            // Click FAQs only
            faQsPage.clickFaqsMenu();

            logToAllure("✅ Click Performed", "Clicked: " + trimmed);
            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Click_" + trimmed.replaceAll("\\s+", "_"));

        } catch (Throwable t) {
            faQsPage.handleValidationException("Validate FAQ topics content", t);
        }
    }

    @Then("the user should redirected to {string} page")
    public void the_user_should_redirected_to_page(String pageName) {
        String trimmed = pageName.trim(); // expect "FAQs"
        try {
            // 1) Wait for FAQs page header
            boolean headerVisible = faQsPage.waitForFaqsPageToLoad(
                    Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS));

            // 2) STOP TIMER AFTER header is verified
            Duration elapsed = NavContext.stopDuration();
            long elapsedMs = elapsed.toMillis();
            double elapsedSec = elapsedMs / 1000.0;

            // 3) Result logging + screenshot
            String resultMsg = "Redirected to " + trimmed + " → " + headerVisible;
            logStep(resultMsg + " in " + String.format("%.2f s", elapsedSec));
            logToAllure("📄 Redirection Check", resultMsg + " (took " + String.format("%.2f s", elapsedSec) + ")");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Destination_" + trimmed.replaceAll("\\s+", "_"));

            // 4) Threshold handling
            if (headerVisible) {
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format(
                            "%s took %.2f s — more than %d s. Failing (SLA %ds).",
                            trimmed, elapsedSec,
                            ReusableCommonMethods.NAV_FAIL_MS / 1000,
                            ReusableCommonMethods.NAV_FAIL_MS / 1000
                    );
                    logToAllure("❌ Load Time Failure", failMsg);
                    softAssert.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format(
                            "%s took %.2f s — more than %d s.",
                            trimmed, elapsedSec,
                            ReusableCommonMethods.NAV_WARN_MS / 1000
                    );
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                }

                // Optional: refresh between checks
                driver.navigate().refresh();
                Thread.sleep(2000);

            } else {
                String failMsg = String.format(
                        "Unable to verify %s page header within %d s (elapsed %.2f s).",
                        trimmed, ReusableCommonMethods.NAV_FAIL_MS / 1000, elapsedSec
                );
                logToAllure("❌ Access Failure", failMsg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "Validation_Failed_" + trimmed.replaceAll("\\s+", "_"));
                softAssert.fail(failMsg);
            }

            softAssert.assertAll();

        } catch (Throwable t) {
            faQsPage.handleValidationException("Validate FAQ topics content", t);
        }
    }






    @And("the FAQ topics should include:")
    public void theFaqTopicsShouldInclude(DataTable table) {
        try {
            logStep("🔎 Validating FAQ topics against expected table");

            List<String> expected = table.asList();
            List<String> actual = faQsPage.getTopicTitles();

            // normalize (trim) for safer comparison
            expected = faQsPage.trimAll(expected);
            actual = faQsPage.trimAll(actual);

            // quick sanity checks
            Assert.assertTrue(actual != null && actual.size() > 0, "Actual topics list is empty");
            Assert.assertFalse(faQsPage.hasDuplicates(actual), "Duplicate topic names found: " + faQsPage.getDuplicates(actual));

            // order-sensitive exact match
            boolean match = faQsPage.exactOrderMatch(expected, actual);
            if (!match) {
                // build a detailed diff
                StringBuilder sb = new StringBuilder();
                sb.append("\n--- FAQ Topics Mismatch ---\n");
                sb.append("Expected (").append(expected.size()).append("): ").append(expected).append("\n");
                sb.append("Actual   (").append(actual.size()).append("): ").append(actual).append("\n");

                List<String> missing = faQsPage.listMissing(expected, actual);
                List<String> unexpected = faQsPage.listUnexpected(expected, actual);
                List<String> outOfOrder = faQsPage.listOutOfOrder(expected, actual);

                if (!missing.isEmpty()) sb.append("Missing: ").append(missing).append("\n");
                if (!unexpected.isEmpty()) sb.append("Unexpected: ").append(unexpected).append("\n");
                if (!outOfOrder.isEmpty()) sb.append("Out of order: ").append(outOfOrder).append("\n");

                ScreenshotUtils.attachScreenshotToAllure(driver, "FAQ_Topics_Mismatch");
                logToAllure("❌ Topics mismatch", sb.toString());
                logger.error("❌ {}", sb);

                Assert.fail(sb.toString());
            }

            ScreenshotUtils.attachScreenshotToAllure(driver, "FAQ_Topics_Validated");
            logToAllure("✅ Verified", "Topics match exactly in content and order.");
            logger.info("✅ Topics match exactly. Expected/Actual: {}", expected);

        } catch (Throwable t) {
            // fixed variable name
            faQsPage.handleValidationException("Validate FAQ topics content", t);
        }
    }


    @When("the user opens the {string} topic in FAQs")
    public void opensTopic(String rawTopicName) {
        final String topicName = rawTopicName == null ? "" : rawTopicName.trim();

        try {
            logStep("🧭 Opening topic: " + topicName);
            logToAllure("Step • Open Topic", "Requested: " + topicName);

            Assert.assertTrue(faQsPage.waitForTopicsList(), "FAQ topics list is not visible.");
            List<String> actual = faQsPage.trimAll(faQsPage.getTopicTitles());
            Assert.assertTrue(actual.size() > 0, "Actual topics list is empty");
            Assert.assertFalse(faQsPage.hasDuplicates(actual), "Duplicate topics: " + faQsPage.getDuplicates(actual));
            Assert.assertTrue(actual.contains(topicName),
                    "Requested topic not present. Visible: " + actual);

            faQsPage.clickTopicByName(topicName, 10);
            logToAllure("Action Result", "Clicked: " + topicName);

            // Wait for the new topic header right away to stabilize navigation
            boolean headerReady = faQsPage.waitForTopicHeader(topicName, 8);
            logToAllure("Header ready after click?", String.valueOf(headerReady));
            Assert.assertTrue(headerReady, "Topic header did not appear after click: " + topicName);

            ScreenshotUtils.attachScreenshotToAllure(driver, "OpenTopic_Clicked_" + topicName.replaceAll("\\s+", "_"));

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "OpenTopic_Failure_" + rawTopicName);
            logToAllure("❌ Open Topic Failed", t.getMessage());
            faQsPage.handleValidationException("Open topic '" + rawTopicName + "'", t);
        }
    }


    @Then("the topic header {string} is visible")
    public void topicHeaderVisible(String rawTopicName) {
        final String topicName = rawTopicName == null ? "" : rawTopicName.trim();

        try {
            logStep("🔎 Verifying header: " + topicName);
            logToAllure("Step • Verify Topic Header", "Topic: " + topicName);

            boolean headerOk = faQsPage.isTopicHeaderVisible(topicName);
            logToAllure("Header Visible?", String.valueOf(headerOk));
            Assert.assertTrue(headerOk, "Topic header not visible: " + topicName);

            // Questions present & clean (unchanged)
            List<String> questions = faQsPage.getQuestionTitles();
            List<String> qTrim = faQsPage.trimAll(questions);

            logToAllure("Questions (raw)", String.valueOf(questions));
            logToAllure("Questions (trimmed)", String.valueOf(qTrim));
            logToAllure("Questions Count", String.valueOf(qTrim.size()));

            Assert.assertTrue(qTrim.size() > 0, "No questions found under topic: " + topicName);

            boolean dupQ = faQsPage.hasDuplicates(qTrim);
            List<String> dupList = faQsPage.getDuplicates(qTrim);
            logToAllure("Question Duplicates?", dupQ ? dupList.toString() : "No");
            Assert.assertFalse(dupQ, "Duplicate question labels found: " + dupList);

            ScreenshotUtils.attachScreenshotToAllure(driver, "Topic_Header_" + topicName.replaceAll("\\s+", "_"));
            logToAllure("✅ Header verified", "Topic: " + topicName + " • Question count: " + qTrim.size());

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Topic_Header_Failure_" + rawTopicName);
            logToAllure("❌ Header verification failed", t.getMessage());
            faQsPage.handleValidationException("Verify topic header visible: " + rawTopicName, t);
        }
    }


    @And("the questions listed under {string} should be:")
    public void questionsShouldBe(String rawTopicName, DataTable table) {
        final String topicName = rawTopicName == null ? "" : rawTopicName.trim();
        try {
            logStep("🧾 Validating questions under topic: " + topicName);
            logToAllure("Step • Validate Questions List", "Topic: " + topicName);

            // Expected vs actual (trimmed)
            List<String> expected = faQsPage.trimAll(table.asList());
            List<String> actual = faQsPage.trimAll(faQsPage.getQuestionTitles());

            logToAllure("Expected (" + expected.size() + ")", expected.toString());
            logToAllure("Actual (" + actual.size() + ")", actual.toString());

            // Sanity checks similar to your topics validation
            Assert.assertTrue(actual.size() > 0, "No questions found under topic: " + topicName);

            boolean dup = faQsPage.hasDuplicates(actual);
            List<String> dupList = faQsPage.getDuplicates(actual);
            logToAllure("Duplicates in Actual?", dup ? dupList.toString() : "No");
            Assert.assertFalse(dup, "Duplicate question labels found: " + dupList);

            // Order-sensitive exact match
            boolean match = faQsPage.exactOrderMatch(expected, actual);
            if (!match) {
                String diff = "\n--- Questions Mismatch ---\n" + "Expected (" + expected.size() + "): " + expected + "\n" + "Actual   (" + actual.size() + "): " + actual + "\n" + "Missing: " + faQsPage.listMissing(expected, actual) + "\n" + "Unexpected: " + faQsPage.listUnexpected(expected, actual) + "\n" + "Out of order: " + faQsPage.listOutOfOrder(expected, actual) + "\n";

                ScreenshotUtils.attachScreenshotToAllure(driver, "Topic_Questions_Mismatch");
                logToAllure("❌ Questions mismatch", diff);
                Assert.fail(diff);
            }

            ScreenshotUtils.attachScreenshotToAllure(driver, "Topic_Questions_List");
            logToAllure("✅ Questions verified", "Content & order match for topic: " + topicName);
            logger.info("✅ Questions match exactly for '{}'", topicName);

        } catch (Throwable t) {
            faQsPage.handleValidationException("Validate questions list for topic: " + rawTopicName, t);
        }
    }


    @When("the user expands every question under {string}")
    public void expandsEveryQuestion(String rawTopicName) {
        final String topicName = rawTopicName == null ? "" : rawTopicName.trim();

        try {
            logStep("🪗 Expanding all accordions under: " + topicName);
            logToAllure("Step • Expand All", "Topic: " + topicName);

            // Pre-check: ensure we have items to expand (from POM)
            List<String> beforeTitles = faQsPage.trimAll(faQsPage.getQuestionTitles());
            Assert.assertTrue(beforeTitles.size() > 0, "No questions found to expand under: " + topicName);
            logToAllure("Questions Before Expand (" + beforeTitles.size() + ")", beforeTitles.toString());

            // Optional: record icon states before
            List<String> iconsBefore = faQsPage.getIconStates();
            logToAllure("Icon States • Before", iconsBefore.toString());

            // Expand all (POM uses your safeClick + internal retries)
            faQsPage.expandAll();

            // Basic validation after expand:
            // 1) At least one item shows content visible & non-empty
            List<Boolean> contentChecks = faQsPage.contentsVisibleAndNonEmpty();
            logToAllure("Content Visible ", contentChecks.toString());

            int okCount = 0;
            List<Integer> badIdx = new java.util.ArrayList<>();
            for (int i = 0; i < contentChecks.size(); i++) {
                if (Boolean.TRUE.equals(contentChecks.get(i))) okCount++;
                else badIdx.add(i + 1);
            }

            // If none expanded to visible content, fail here (strong validation)
            Assert.assertTrue(okCount > 0, "None of the questions showed visible, non-empty content after expand under: " + topicName);

            // 2) Icon states should generally change away from '+' (informational here; strict check is another step)
            List<String> iconsAfter = faQsPage.getIconStates();
            logToAllure("Icon States • After", iconsAfter.toString());

            ScreenshotUtils.attachScreenshotToAllure(driver, "All_Expanded_" + topicName.replaceAll("\\s+", "_"));

            // If any content checks failed, surface them as WARN (content step will hard-fail if needed)
            if (!badIdx.isEmpty()) {
                logToAllure("⚠️ Content issues after expand", "Items without visible/non-empty content: " + badIdx);
                logger.warn("⚠️ Content not visible/non-empty for items: {}", badIdx);
            } else {
                logToAllure("✅ Expand All", "All items show visible, non-empty content.");
                logger.info("✅ All items show visible, non-empty content after expand");
            }

        } catch (Throwable t) {
            faQsPage.handleValidationException("Expand all questions under topic: " + rawTopicName, t);
        }
    }

    @Then("each expanded question content is visible and not empty")
    public void contentVisibleAndNotEmpty() {
        try {
            logStep("🔍 Verifying each expanded question shows visible, non-empty content");
            logToAllure("Step • Content Visible & Non-Empty", "Validating all expanded accordions");

            // Use POM to check content; also fetch titles so we can name failures
            final List<Boolean> checks = faQsPage.contentsVisibleAndNonEmpty();
            final List<String> titles = faQsPage.trimAll(faQsPage.getQuestionTitles());

            // Allure: per-item breakdown
            StringBuilder perItem = new StringBuilder();
            List<String> bad = new ArrayList<>();
            for (int i = 0; i < checks.size(); i++) {
                String title = (i < titles.size() && titles.get(i) != null) ? titles.get(i) : "(unknown)";
                boolean ok = Boolean.TRUE.equals(checks.get(i));
                perItem.append(String.format("#%d :: %-5s :: %s%n", i + 1, ok ? "OK" : "FAIL", title));
                if (!ok) bad.add("#" + (i + 1) + " \"" + title + "\"");
            }
            logToAllure("Per-Item Result", perItem.toString());
            logToAllure("Count", "Total=" + checks.size() + ", Failures=" + bad.size());

            if (!bad.isEmpty()) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "Empty_Content");
                logToAllure("❌ Empty/Invisible Content", "Items: " + bad);
                Assert.fail("Content empty/not visible for items: " + bad);
            }

            ScreenshotUtils.attachScreenshotToAllure(driver, "Content_OK");
            logToAllure("✅ Content OK", "All expanded items have visible, non-empty content.");
            logger.info("✅ All expanded items have visible, non-empty content ({} items).", checks.size());

        } catch (Throwable t) {
            faQsPage.handleValidationException("Validate expanded content visible & non-empty", t);
        }
    }

    @And("each accordion shows a \"+\" icon when collapsed and changes state when expanded")
    public void iconChangesOnExpand() {
        try {
            logStep("🔁 Validating icon toggles: '+' when collapsed, different when expanded");
            logToAllure("Step • Icon Toggle Validation", "Checking collapsed/expanded icon states");

            // Snapshot BEFORE (optional)
            final List<String> iconsBefore = faQsPage.getIconStates();
            logToAllure("Icon States • Before", iconsBefore.toString());

            // 1) Collapse all -> expect '+'
            faQsPage.collapseAll();
            final List<String> collapsed = faQsPage.getIconStates();
            List<Integer> notPlus = new ArrayList<>();
            for (int i = 0; i < collapsed.size(); i++) {
                if (!"+".equals(collapsed.get(i))) notPlus.add(i + 1);
            }
            logToAllure("Icon States • After Collapse", collapsed.toString());
            logToAllure("Not '+' After Collapse", notPlus.isEmpty() ? "None" : notPlus.toString());

            // 2) Expand all -> expect NOT '+'
            faQsPage.expandAll();
            final List<String> expanded = faQsPage.getIconStates();
            List<Integer> stillPlus = new ArrayList<>();
            for (int i = 0; i < expanded.size(); i++) {
                if ("+".equals(expanded.get(i))) stillPlus.add(i + 1);
            }
            logToAllure("Icon States • After Expand", expanded.toString());
            logToAllure("Still '+' After Expand", stillPlus.isEmpty() ? "None" : stillPlus.toString());

            // 🔒 3) Strict content assertion AFTER expand (your snippet goes here)
            final List<Boolean> contentAfter = faQsPage.contentsVisibleAndNonEmpty();
            int okCount = 0;
            List<Integer> bad = new ArrayList<>();
            for (int i = 0; i < contentAfter.size(); i++) {
                if (Boolean.TRUE.equals(contentAfter.get(i))) okCount++;
                else bad.add(i + 1);
            }
            logToAllure("Content Visible & Non-Empty • After Expand", contentAfter.toString());
            Assert.assertEquals(okCount, contentAfter.size(), "Some items did not show content after expand. Failing indices: " + bad);

            // 4) Fail if icon states misbehaved
            if (!notPlus.isEmpty() || !stillPlus.isEmpty()) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "Icon_State_Issues");
                String msg = "Collapsed not '+': " + notPlus + " | Expanded still '+': " + stillPlus;
                logToAllure("❌ Icon State Mismatch", msg);
                Assert.fail(msg);
            }

            ScreenshotUtils.attachScreenshotToAllure(driver, "Icon_Toggle_OK");
            logToAllure("✅ Icon & Content OK", "All accordions toggle '+'→not '+' and content is visible & non-empty.");

        } catch (Throwable t) {
            faQsPage.handleValidationException("Validate icon toggle & content after expand", t);
        }
    }


    @When("the user clicks {string}")
    public void userClicksBack(String text) {
        final String label = text == null ? "" : text.trim();
        try {
            // Guard: only Back supported here
            Assert.assertEquals(label, "Back", "Only 'Back' supported here");

            logStep("⬅️ Clicking Back");
            logToAllure("Step • Click", "Control: " + label);

            // (Optional) sanity: we appear to be on a topic page before clicking Back
            // If your current flow is always "topic -> back", this helps catch state mistakes.
            boolean onTopicView = faQsPage.isComplianceHeaderVisible(); // OK even if current topic is Compliance Calendar
            logToAllure("On Topic View Before Click?", String.valueOf(onTopicView));

            // Perform the click via POM (uses your robust safeClick)
            faQsPage.clickBack();
            logToAllure("Action", "Clicked: Back");

            // Attach a post-click screenshot (assertion comes in the next step)
            ScreenshotUtils.attachScreenshotToAllure(driver, "Back_Clicked");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Back_Click_Failure");
            logToAllure("❌ Back click failed", t.getMessage());
            // Use *topic* page’s handler since we’re leaving the topic view
            faQsPage.handleValidationException("Click Back", t);
        }
    }


    @Then("the FAQ topics list is visible")
    public void topicsListVisible() {
        try {
            logStep("🔎 Verifying FAQ topics list visibility after Back");
            logToAllure("Step • Verify List", "Expect: topics list is visible");

            // Visible?
            boolean visible = faQsPage.waitForTopicsList();
            logToAllure("List Visible?", String.valueOf(visible));
            org.testng.Assert.assertTrue(visible, "FAQ topics list is not visible after Back");

            // Gather and normalize titles
            List<String> raw = faQsPage.getTopicTitles();
            List<String> titles = faQsPage.trimAll(raw);

            // Log details for Allure
            logToAllure("Topics (raw)", String.valueOf(raw));
            logToAllure("Topics (trimmed)", String.valueOf(titles));
            logToAllure("Topic Count", String.valueOf(titles.size()));

            // Strong validations
            Assert.assertTrue(titles.size() > 0, "No topics found after Back");

            boolean hasDups = faQsPage.hasDuplicates(titles);
            List<String> dups = faQsPage.getDuplicates(titles);
            logToAllure("Duplicates?", hasDups ? dups.toString() : "No");
            Assert.assertFalse(hasDups, "Duplicate topic names found after Back: " + dups);

            // Ensure each label is non-empty (extra hardening)
            List<Integer> blanks = new ArrayList<>();
            for (int i = 0; i < titles.size(); i++) {
                String t = titles.get(i) == null ? "" : titles.get(i).trim();
                if (t.isEmpty()) blanks.add(i + 1);
            }
            if (!blanks.isEmpty()) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "Topics_Blank_Labels");
                logToAllure("❌ Blank topic labels", "Indices: " + blanks);
                Assert.fail("Blank topic labels at indices: " + blanks);
            }

            // Artifacts
            ScreenshotUtils.attachScreenshotToAllure(driver, "Topics_List_After_Back");
            logToAllure("✅ Returned", "Topics list visible and valid after Back. Count=" + titles.size());
            logger.info("✅ Topics list visible after Back ({} items).", titles.size());

        } catch (Throwable t) {
            faQsPage.handleValidationException("Verify topics list after Back", t);
        }
    }


}
