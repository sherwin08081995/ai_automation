package stepDefinitions;


import hooks.Hooks;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pages.ReportAnIssueSection;
import pages.TakeProductTour;
import utils.*;

import java.time.Duration;
import java.time.Instant;

import static utils.AllureLoggerUtils.logToAllure;

/**
 * @author Sherwin
 * @since 22-09-2025
 */

public class TakeProductTourValidationSteps {

    WebDriver driver = Hooks.driver;
    TakeProductTour takeProductTour;
    Logger logger;
    WebDriverWait wait;
    AllureLoggerUtils allureLogging;
    ReusableCommonMethods helperMethods;

    public TakeProductTourValidationSteps() {
        this.driver = Hooks.driver;
        this.takeProductTour = new TakeProductTour(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.logger = LoggerUtils.getLogger(getClass());
        this.helperMethods = new ReusableCommonMethods(driver);
    }

    @Step("{message}")
    public void logStep(String message) {
        logger.info(message);
    }


    @When("the user clicks {string} at the bottom")
    public void the_user_clicks_take_product_tour_at_the_bottom(String buttonText) {
        try {
            String label = buttonText == null ? "Take product tour" : buttonText.trim();
            SoftAssert soft = new SoftAssert();

            // ---- 0) Pre-state logging
            logToAllure("🎯 Product Tour: Click Start", "Target: " + label);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Pre_Click_" + label.replaceAll("\\s+","_"));

            // ---- 1) Precondition: control visible & ready
            boolean visible = takeProductTour.isTakeProductTourVisible();
            if (!visible) {
                String msg = "'" + label + "' button not visible on Home page.";
                logger.error(msg);
                logToAllure("❌ Visibility Failure", msg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "Btn_NotVisible_" + label.replaceAll("\\s+","_"));
                soft.fail(msg);
                soft.assertAll();  // stop here, nothing else to do
                return;
            }

            // (Optional hardening) If a loader/spinner sometimes blocks clicks, wait for it to be gone.
            takeProductTour.waitForAnyBlockingLoaderToDisappear(Duration.ofSeconds(5));

            // ---- 2) Start timing & nav context
            Instant clickStart = Instant.now();
            NavContext.start(label);

            // ---- 3) Action: safe click + immediate assertion that click succeeded (no exception)
            boolean clickOk = takeProductTour.performClick(label); // wraps your safeClick + logs
            long afterClickMs = Duration.between(clickStart, Instant.now()).toMillis();
            if (!clickOk) {
                String msg = "Click failed for '" + label + "' (no successful click path).";
                logger.error(msg);
                logToAllure("❌ Click Failure", msg + " at ~" + (afterClickMs/1000.0) + "s");
                ScreenshotUtils.attachScreenshotToAllure(driver, "ClickFail_" + label.replaceAll("\\s+","_"));
                soft.fail(msg);
                soft.assertAll();
                return;
            }

            // ---- 4) Postcondition: first tour step visible within SLA
            boolean firstStepVisible = takeProductTour.waitForFirstTourStep(
                    Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS)); // e.g. 20000ms
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs(label, clickStart);
            double elapsedSec = elapsedMs / 1000.0;

            if (firstStepVisible) {
                String ok = String.format("'%s' clicked → first tour popup visible in %.2f s", label, elapsedSec);
                logger.info(ok);
                logStep(ok);
                logToAllure("✅ Product Tour Started", ok);
                ScreenshotUtils.attachScreenshotToAllure(driver, "Tour_FirstStep_Visible");

                // ---- 5) SLA thresholds (warn/fail)
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("Tour popup took %.2f s (>= %ds) — FAIL.",
                            elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Failure", failMsg);
                    soft.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("Tour popup took %.2f s (>= %ds) — WARN.",
                            elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                }
            } else {
                String fail = String.format(
                        "First tour popup did not appear within %ds after clicking '%s'.",
                        ReusableCommonMethods.NAV_FAIL_MS / 1000, label);
                logger.error(fail);
                logToAllure("❌ Popup Not Visible", fail + " (elapsed ~" + String.format("%.2f s", elapsedSec) + ")");
                ScreenshotUtils.attachScreenshotToAllure(driver, "Tour_FirstStep_NotVisible");
                soft.fail(fail);
            }

            soft.assertAll();

        } catch (Throwable t) {
            takeProductTour.handleValidationException("Product Tour: Click at bottom", t);
        }
    }


    @Then("the {string} popup should be visible")
    public void the_popup_should_be_visible(String popupHeading) {
        try {
            String heading = popupHeading == null ? "" : popupHeading.trim();
            SoftAssert soft = new SoftAssert();

            // 0) Pre-state logging
            logToAllure("🎯 Tour Popup Check", "Expecting: " + heading);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Tour_Pre_" + heading.replaceAll("\\s+","_"));

            // 1) Start timers BEFORE we wait (captures render time)
            Instant start = Instant.now();
            NavContext.start("Tour: " + heading);

            // 2) Wait/verify popup visible (your POM method includes its own wait + logging)
            boolean visible = takeProductTour.isPopupVisible(heading);

            // 3) Stop + log timing via your helper (uses NAV thresholds by default)
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Tour: " + heading, start);
            double elapsedSec = elapsedMs / 1000.0;

            // 4) Result logging + evidence
            String resultMsg = "Popup visible [" + heading + "] → " + visible;
            logStep(resultMsg + " in " + String.format("%.2f s", elapsedSec));
            logger.info("Tour popup '{}': {}s (visible={})", heading, String.format("%.2f", elapsedSec), visible);
            logToAllure(visible ? "✅ Popup Visible" : "❌ Popup Not Visible",
                    heading + " | took " + String.format("%.2f s", elapsedSec));
            ScreenshotUtils.attachScreenshotToAllure(driver,
                    (visible ? "Tour_Visible_" : "Tour_NotVisible_") + heading.replaceAll("\\s+","_"));

            // 5) SLA thresholds (warn ≥ NAV_WARN_MS, fail ≥ NAV_FAIL_MS)
            if (visible) {
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format(
                            "Popup '%s' appeared in %.2f s — exceeds FAIL SLA (%ds).",
                            heading, elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Failure", failMsg);
                    soft.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format(
                            "Popup '%s' appeared in %.2f s — exceeds WARN SLA (%ds).",
                            heading, elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                    // soft warning only
                }
            } else {
                String failMsg = String.format(
                        "Popup not visible within %ds: %s",
                        ReusableCommonMethods.NAV_FAIL_MS / 1000, heading);
                logger.error(failMsg);
                logToAllure("❌ Visibility Failure", failMsg + " (after " + String.format("%.2f s", elapsedSec) + ")");
                soft.fail(failMsg);
            }

            // 6) Assert at end to aggregate multiple soft failures if you chain steps
            soft.assertAll();

        } catch (Throwable t) {
            takeProductTour.handleValidationException("Verify popup visible: " + popupHeading, t);
            throw t;
        }
    }


    @And("the user clicks {string} on the product tour")
    public void the_user_clicks_on_the_product_tour(String btnLabel) {
        try {
            String label = btnLabel == null ? "" : btnLabel.trim();
            SoftAssert soft = new SoftAssert();

            // 1) Pre-state log + screenshot
            logToAllure("🎯 Tour Button Click", "Target: " + label);
            ScreenshotUtils.attachScreenshotToAllure(driver, "TourBtn_Pre_" + label.replaceAll("\\s+", "_"));

            // 2) Start timing
            Instant clickStart = Instant.now();
            NavContext.start("Tour: " + label);

            // 3) Perform click (via POM safeClick wrapper)
            boolean clicked = takeProductTour.clickNextOnPopup();

            // 4) Stop & log elapsed time
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Tour: " + label, clickStart);
            double elapsedSec = elapsedMs / 1000.0;

            // 5) Validation result logging
            if (clicked) {
                String okMsg = String.format("Clicked '%s' successfully in %.2f s", label, elapsedSec);
                logger.info(okMsg);
                logStep(okMsg);
                logToAllure("✅ Button Clicked", okMsg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "TourBtn_Clicked_" + label.replaceAll("\\s+", "_"));

                // SLA threshold handling
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format(
                            "'%s' action took %.2f s — exceeds FAIL SLA (%ds).",
                            label, elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Failure", failMsg);
                    soft.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format(
                            "'%s' action took %.2f s — exceeds WARN SLA (%ds).",
                            label, elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                }
            } else {
                String failMsg = "Failed to click '" + label + "' within timeout.";
                logger.error(failMsg);
                logToAllure("❌ Button Click Failure", failMsg + " (after " + String.format("%.2f s", elapsedSec) + ")");
                ScreenshotUtils.attachScreenshotToAllure(driver, "TourBtn_ClickFail_" + label.replaceAll("\\s+", "_"));
                soft.fail(failMsg);
            }

            // 6) Aggregate assertions
            soft.assertAll();

        } catch (Throwable t) {
            takeProductTour.handleValidationException("Click on tour button: " + btnLabel, t);
            throw t;
        }
    }


    @Then("the final step of product tour should be visible")
    public void the_final_step_of_product_tour_should_be_visible() {
        try {
            SoftAssert soft = new SoftAssert();
            String heading = "Your Legal Documents Hub";

            // Pre-log
            logToAllure("🎯 Final Step Check", "Expecting: " + heading);
            ScreenshotUtils.attachScreenshotToAllure(driver, "FinalStep_Pre");

            Instant start = Instant.now();
            NavContext.start("Tour: Final Step");

            boolean visible = takeProductTour.isFinalStepVisible();

            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Tour: Final Step", start);
            double elapsedSec = elapsedMs / 1000.0;

            if (visible) {
                String ok = String.format("Final step '%s' visible in %.2f s", heading, elapsedSec);
                logger.info(ok);
                logStep(ok);
                logToAllure("✅ Final Step Visible", ok);
                ScreenshotUtils.attachScreenshotToAllure(driver, "FinalStep_Visible");

                // SLA thresholds
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("Final step took %.2f s — exceeds FAIL SLA (%ds).",
                            elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Failure", failMsg);
                    soft.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("Final step took %.2f s — exceeds WARN SLA (%ds).",
                            elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                }
            } else {
                String failMsg = "Final step of product tour not visible within timeout.";
                logger.error(failMsg);
                logToAllure("❌ Final Step Failure", failMsg + " (after " + String.format("%.2f s", elapsedSec) + ")");
                ScreenshotUtils.attachScreenshotToAllure(driver, "FinalStep_NotVisible");
                soft.fail(failMsg);
            }

            soft.assertAll();

        } catch (Throwable t) {
            takeProductTour.handleValidationException("Verify final step", t);
            throw t;
        }
    }

    @And("the user clicks {string} to finish the tour")
    public void the_user_clicks_to_finish_the_tour(String btnLabel) {
        try {
            String label = btnLabel == null ? "" : btnLabel.trim();
            SoftAssert soft = new SoftAssert();

            logToAllure("🎯 Tour Completion", "Clicking: " + label);
            ScreenshotUtils.attachScreenshotToAllure(driver, "TourFinish_Pre");

            Instant start = Instant.now();
            NavContext.start("Tour: " + label);

            boolean clicked = takeProductTour.clickGotIt();

            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Tour: " + label, start);
            double elapsedSec = elapsedMs / 1000.0;

            if (clicked) {
                String ok = String.format("Clicked '%s' and tour closed in %.2f s", label, elapsedSec);
                logger.info(ok);
                logStep(ok);
                logToAllure("✅ Tour Completed", ok);
                ScreenshotUtils.attachScreenshotToAllure(driver, "Tour_Finished");

                // SLA thresholds
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("Tour closure took %.2f s — exceeds FAIL SLA (%ds).",
                            elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Failure", failMsg);
                    soft.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("Tour closure took %.2f s — exceeds WARN SLA (%ds).",
                            elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                }

                // 🔹 Extra post-check: ensure no popup headings remain
                if (takeProductTour.isAnyPopupVisible()) {
                    String failMsg = "Tour should be closed, but a popup is still visible.";
                    logger.error(failMsg);
                    logToAllure("❌ Tour Close Failure", failMsg);
                    ScreenshotUtils.attachScreenshotToAllure(driver, "TourStillOpen");
                    soft.fail(failMsg);
                }
            } else {
                String failMsg = "Failed to click '" + label + "' to finish the tour.";
                logger.error(failMsg);
                logToAllure("❌ Click Failure", failMsg + " (after " + String.format("%.2f s", elapsedSec) + ")");
                ScreenshotUtils.attachScreenshotToAllure(driver, "TourFinish_ClickFail");
                soft.fail(failMsg);
            }

            soft.assertAll();

        } catch (Throwable t) {
            takeProductTour.handleValidationException("Click to finish tour", t);
            throw t;
        }
    }



    @And("the user clicks {string} during the product tour")
    public void the_user_clicks_skip_for_now_on_the_product_tour(String btnLabel) {
        // Enforce this step to be used only for "Skip for now"
        if (btnLabel == null || !btnLabel.trim().equalsIgnoreCase("Skip for now")) {
            String msg = "This step only supports 'Skip for now' — received: '" + btnLabel + "'";
            logger.error(msg);
            Assert.fail(msg);
        }

        final String label = "Skip for now";

        try {
            SoftAssert soft = new SoftAssert();

            // Pre-evidence
            logToAllure("🎯 Tour Button Click", "Target: " + label);
            ScreenshotUtils.attachScreenshotToAllure(driver, "TourBtn_Pre_SkipForNow");

            // Timing
            Instant start = Instant.now();
            NavContext.start("Tour: " + label);

            // Click & verify Compliances stays visible (POM does the heavy lifting)
            boolean ok = takeProductTour.clickSkipForNowAndVerifyCompliances(
                    Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS));

            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Tour: " + label, start);
            double elapsedSec = elapsedMs / 1000.0;

            if (ok) {
                String msg = String.format("Clicked '%s' and remained on Compliances in %.2f s", label, elapsedSec);
                logger.info(msg);
                logStep(msg);
                logToAllure("✅ Tour Skipped", msg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "SkipForNow_Success");

                // SLA checks
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("Skip-for-now took %.2f s — exceeds FAIL SLA (%ds).",
                            elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Failure", failMsg);
                    soft.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("Skip-for-now took %.2f s — exceeds WARN SLA (%ds).",
                            elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                }

                // Extra guard: tour must be closed
                if (takeProductTour.isAnyPopupVisible()) {
                    String failMsg = "Tour should be closed after 'Skip for now', but a popup is still visible.";
                    logger.error(failMsg);
                    logToAllure("❌ Tour Close Failure", failMsg);
                    ScreenshotUtils.attachScreenshotToAllure(driver, "SkipForNow_TourStillOpen");
                    soft.fail(failMsg);
                }
            } else {
                String fail = "Skip-for-now did not keep user on Compliances within SLA.";
                logger.error(fail);
                logToAllure("❌ Skip Failure", fail + " (after " + String.format("%.2f s", elapsedSec) + ")");
                ScreenshotUtils.attachScreenshotToAllure(driver, "SkipForNow_Fail");
                soft.fail(fail);
            }

            soft.assertAll();

        } catch (Throwable t) {
            takeProductTour.handleValidationException("Click Skip for now", t);
            throw t;
        }
    }


    @Then("the user should stays in Compliances page")
    public void the_compliances_page_should_be_visible() {
        try {
            SoftAssert soft = new SoftAssert();

            logToAllure("🔎 Compliances Visibility Check", "Verifying header and absence of tour.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Compliances_Check_Pre");

            Instant start = Instant.now();
            NavContext.start("Compliances visible after Skip");

            boolean visible = takeProductTour.isCompliancesPageVisible();

            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Compliances visible after Skip", start);
            double elapsedSec = elapsedMs / 1000.0;

            if (visible) {
                String ok = String.format("Compliances page visible in %.2f s", elapsedSec);
                logger.info(ok);
                logStep(ok);
                logToAllure("✅ Compliances Visible", ok);
                ScreenshotUtils.attachScreenshotToAllure(driver, "Compliances_Visible");

                // Tour should not be visible anymore
                if (takeProductTour.isAnyPopupVisible()) {
                    String failMsg = "Tour container/popup still visible after 'Skip for now'.";
                    logger.error(failMsg);
                    logToAllure("❌ Tour Still Visible", failMsg);
                    ScreenshotUtils.attachScreenshotToAllure(driver, "Compliances_TourStillOpen");
                    soft.fail(failMsg);
                }

            } else {
                String fail = "Compliances page not visible after 'Skip for now'.";
                logger.error(fail);
                logToAllure("❌ Compliances Not Visible", fail + " (after " + String.format("%.2f s", elapsedSec) + ")");
                ScreenshotUtils.attachScreenshotToAllure(driver, "Compliances_NotVisible");
                soft.fail(fail);
            }

            // (Optional) SLA thresholds for visibility check
            if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                String failMsg = String.format("Compliances visibility took %.2f s — exceeds FAIL SLA (%ds).",
                        elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                logger.error(failMsg);
                logToAllure("❌ Load Time Failure", failMsg);
                soft.fail(failMsg);
            } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                String warnMsg = String.format("Compliances visibility took %.2f s — exceeds WARN SLA (%ds).",
                        elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                logger.warn(warnMsg);
                logToAllure("⚠️ Load Time Warning", warnMsg);
            }

            soft.assertAll();

        } catch (Throwable t) {
            takeProductTour.handleValidationException("Verify Compliances page visible after Skip", t);
            throw t;
        }
    }




}
