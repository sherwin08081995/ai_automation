package pages;


import base.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object representing the **Take Product Tour** feature.
 *
 * <p>This class encapsulates all locators and reusable actions for handling
 * the guided product tour flow: starting the tour, navigating through steps,
 * skipping it, and validating that expected pages or popups are displayed.</p>
 *
 * <p>Supported features include:</p>
 * <ul>
 *   <li>Checking visibility of the product tour button and popups</li>
 *   <li>Clicking through the tour using "Next" or "Got it"</li>
 *   <li>Skipping the tour and verifying navigation to the Compliances page</li>
 *   <li>Handling blocking loaders/spinners gracefully</li>
 * </ul>
 *
 * @author Sherwin
 * @since 22-09-2025
 */

public class TakeProductTour extends BasePage {

    public TakeProductTour(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//p[normalize-space()='Take product tour']")
    private WebElement takeProductTourBtn;

    // 🔹 First popup heading after clicking tour
    @FindBy(xpath = "//div[normalize-space()='Stay on Top of Your Compliances']")
    private WebElement firstTourHeading;

    @FindBy(xpath = "//button[@aria-label='Next']")
    private WebElement nextBtn;

    @FindBy(xpath = "//div[normalize-space()='Stay on Top of Your Compliances']")
    private WebElement headingCompliances;

    @FindBy(xpath = "//div[normalize-space()='Upload Documents with Ease']")
    private WebElement headingUploadDocs;

    @FindBy(xpath = "//div[normalize-space()='Your Legal Documents Hub']")
    private WebElement headingLegalDocs;

    // --- Skip for now button on first popup ---
    @FindBy(xpath = "//button[@aria-label='Skip' or normalize-space()='Skip for now']")
    private WebElement skipForNowBtn;

    // --- Compliances page marker (you provided this) ---
    @FindBy(xpath = "//p[@class='text-[32px] max-lg:hidden font-semibold'][normalize-space()='Compliances']")
    private WebElement compliancesText;

    @FindBy(xpath = "//*[contains(@class,'loader') or contains(@class,'spinner') or @data-testid='global-loader']")
    private List<WebElement> blockingLoaders;

    @FindBy(xpath = "//button[@aria-label='Last' or normalize-space()='Got it']")
    private WebElement gotItBtn;

    /**
     * Checks if the "Take Product Tour" button is currently visible.
     *
     * @return {@code true} if visible, else {@code false}
     */
    public boolean isTakeProductTourVisible() {
        final String ctx = "Take Product Tour";
        try {
            if (takeProductTourBtn == null) {
                logger.error("{}: WebElement is null (PageFactory not initialized?).", ctx);
                return false;
            }

            wait.waitForVisibility(takeProductTourBtn);

            boolean displayed;
            try {
                displayed = takeProductTourBtn.isDisplayed();
            } catch (StaleElementReferenceException sre) {
                logger.warn("{}: element went stale, retrying once…", ctx);
                wait.waitForVisibility(takeProductTourBtn);
                displayed = takeProductTourBtn.isDisplayed();
            }

            logger.info("{}: visible = {}", ctx, displayed);
            return displayed;

        } catch (TimeoutException te) {
            logger.warn("{}: not visible within timeout. {}", ctx, te.toString());
            return false;
        } catch (Exception e) {
            logger.error("{}: unexpected error during visibility check: {}", ctx, e.toString(), e);
            return false;
        }
    }


    /**
     * Performs a click on the "Take Product Tour" button.
     *
     * @param name descriptive name for logging
     * @return {@code true} if the click succeeds, else {@code false}
     */
    public boolean performClick(String name) {
        try {
            commonMethods.safeClick(driver, takeProductTourBtn, name, 10); // your robust method
            return true;
        } catch (Exception e) {
            logger.error("Click failure for '{}': {}", name, e.toString(), e);
            return false;
        }
    }

    /**
     * Waits for the first product tour popup step to appear.
     *
     * @param timeout maximum duration to wait
     * @return {@code true} if the first popup heading is visible, else {@code false}
     */
    public boolean waitForFirstTourStep(Duration timeout) {
        try {
            new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOf(firstTourHeading));
            logger.info("First tour step visible: '{}'", firstTourHeading.getText());
            return true;
        } catch (TimeoutException te) {
            logger.error("First tour step NOT visible within {} ms", timeout.toMillis());
            return false;
        }
    }


    /**
     * Waits for any blocking loader/spinner to disappear before proceeding.
     *
     * @param timeout maximum time to wait before giving up
     */
    public void waitForAnyBlockingLoaderToDisappear(Duration timeout) {
        if (blockingLoaders == null) return;
        long end = System.currentTimeMillis() + timeout.toMillis();
        try {
            while (System.currentTimeMillis() < end) {
                boolean anyDisplayed = false;
                for (WebElement e : blockingLoaders) {
                    try {
                        if (e.isDisplayed()) {
                            anyDisplayed = true;
                            break;
                        }
                    } catch (StaleElementReferenceException ignored) {
                    }
                }
                if (!anyDisplayed) {
                    logger.info("No blocking loader detected.");
                    return;
                }
                Thread.sleep(150);
            }
            logger.warn("Blocking loader may still be present after {} ms", timeout.toMillis());
        } catch (InterruptedException ignored) {
        }
    }


    /**
     * Resolves the WebElement corresponding to the given popup heading.
     * Falls back to a generic locator if text changes slightly.
     *
     * @param popupHeading visible heading text in the popup
     * @return the matching WebElement for the heading
     */
    private WebElement resolveHeadingElement(String popupHeading) {
        String h = popupHeading.trim();
        if (h.equalsIgnoreCase("Stay on Top of Your Compliances")) return headingCompliances;
        if (h.equalsIgnoreCase("Upload Documents with Ease")) return headingUploadDocs;
        if (h.equalsIgnoreCase("Your Legal Documents Hub")) return headingLegalDocs;

        // Fallback: try a generic normalized contains match if copy changes slightly
        logger.warn("Unknown popup heading '{}'; falling back to generic contains locator.", h);
        return driver.findElement(By.xpath("//div[contains(normalize-space(),\"" + h + "\")]"));
    }

    /**
     * Verifies whether the specified popup is visible.
     *
     * @param popupHeading the expected popup heading
     * @return {@code true} if visible, else {@code false}
     */
    public boolean isPopupVisible(String popupHeading) {
        final String ctx = "Product Tour → '" + popupHeading + "' visible";
        try {
            WebElement headingEl = resolveHeadingElement(popupHeading);
            if (headingEl == null) {
                logger.error("{}: resolved heading element is null.", ctx);
                return false;
            }

            logger.info("{}: waiting for visibility…", ctx);
            wait.waitForVisibility(headingEl);

            boolean displayed;
            try {
                displayed = headingEl.isDisplayed();
            } catch (StaleElementReferenceException sre) {
                logger.warn("{}: heading went stale; retrying once.", ctx);
                wait.waitForVisibility(headingEl);
                displayed = headingEl.isDisplayed();
            }

            logger.info("{}: displayed={}", ctx, displayed);
            return displayed;

        } catch (TimeoutException te) {
            logger.warn("{}: not visible within timeout. {}", ctx, te.toString());
            return false;
        } catch (NoSuchElementException nse) {
            logger.warn("{}: not present in DOM.", ctx);
            return false;
        } catch (Exception e) {
            logger.error("{}: unexpected error: {}", ctx, e.toString(), e);
            return false;
        }
    }

    /**
     * Clicks the "Next" button in the current tour popup.
     *
     * @return {@code true} if the click succeeds, else {@code false}
     */
    public boolean clickNextOnPopup() {
        final String ctx = "Product Tour → Next";
        try {
            commonMethods.safeClick(driver, nextBtn, ctx, 10);
            logger.info("{}: clicked successfully.", ctx);
            return true;
        } catch (Exception e) {
            logger.error("{}: failed to click. {}", ctx, e.toString(), e);
            return false;
        }
    }

    /**
     * Verifies if the final step (Legal Documents popup) is visible.
     *
     * @return {@code true} if visible, else {@code false}
     */
    public boolean isFinalStepVisible() {
        final String ctx = "Product Tour → Final Step";
        try {
            wait.waitForVisibility(headingLegalDocs);
            boolean displayed = headingLegalDocs.isDisplayed();
            logger.info("{}: heading visible = {}", ctx, displayed);
            return displayed;
        } catch (TimeoutException te) {
            logger.warn("{}: not visible within timeout. {}", ctx, te.toString());
            return false;
        } catch (StaleElementReferenceException sre) {
            logger.warn("{}: heading went stale; retrying once.", ctx);
            wait.waitForVisibility(headingLegalDocs);
            return headingLegalDocs.isDisplayed();
        } catch (Exception e) {
            logger.error("{}: unexpected error: {}", ctx, e.toString(), e);
            return false;
        }
    }

    /**
     * Clicks the "Got it" button to finish the product tour.
     *
     * @return {@code true} if the click succeeds, else {@code false}
     */
    public boolean clickGotIt() {
        final String ctx = "Product Tour → Got it";
        try {
            commonMethods.safeClick(driver, gotItBtn, ctx, 10);
            logger.info("{}: clicked successfully.", ctx);
            return true;
        } catch (Exception e) {
            logger.error("{}: failed to click. {}", ctx, e.toString(), e);
            return false;
        }
    }


    /**
     * Checks if any product tour popup is currently visible.
     *
     * @return {@code true} if at least one popup is visible, else {@code false}
     */
    public boolean isAnyPopupVisible() {
        try {
            // Reuse your known popup headings
            if (isElementVisibleNow(headingCompliances)) return true;
            if (isElementVisibleNow(headingUploadDocs)) return true;
            if (isElementVisibleNow(headingLegalDocs)) return true;

            // Optional: check for generic popup container (if your app uses a wrapper)
            List<WebElement> containers = driver.findElements(By.cssSelector("div.__floater__body, div._floater_body"));
            for (WebElement c : containers) {
                try {
                    if (c.isDisplayed()) return true;
                } catch (StaleElementReferenceException ignored) {
                }
            }

            return false;
        } catch (Exception e) {
            logger.warn("Tour popup visibility check failed: {}", e.toString());
            return false;
        }
    }

    /**
     * Safely checks if the given element is visible without throwing exceptions.
     *
     * @param el the WebElement to check
     * @return {@code true} if visible, else {@code false}
     */
    private boolean isElementVisibleNow(WebElement el) {
        try {
            return el != null && el.isDisplayed();
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Clicks "Skip for now" and verifies that the Compliances page is displayed.
     *
     * @param timeout maximum time to wait for Compliances page
     * @return {@code true} if the Compliances page is visible, else {@code false}
     */
    public boolean clickSkipForNowAndVerifyCompliances(Duration timeout) {
        final String ctx = "Product Tour → Skip for now → Compliances";
        try {
            // Make sure first popup is there (best-effort)
            try {
                wait.waitForVisibility(headingCompliances);
            } catch (Exception ignored) {
            }

            commonMethods.safeClick(driver, skipForNowBtn, "Skip for now", 10);

            new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOf(compliancesText));

            logger.info("{}: Compliances page visible after skipping tour.", ctx);
            return true;
        } catch (TimeoutException te) {
            logger.error("{}: Compliances header not visible within {} ms. {}", ctx, timeout.toMillis(), te.toString());
            return false;
        } catch (Exception e) {
            logger.error("{}: failure: {}", ctx, e.toString(), e);
            return false;
        }
    }

    /**
     * Checks if the Compliances page is currently visible.
     *
     * @return {@code true} if visible, else {@code false}
     */
    public boolean isCompliancesPageVisible() {
        final String ctx = "Compliances Page Visible";
        try {
            wait.waitForVisibility(compliancesText);
            boolean displayed;
            try {
                displayed = compliancesText.isDisplayed();
            } catch (StaleElementReferenceException sre) {
                logger.warn("{}: header went stale; retrying once.", ctx);
                wait.waitForVisibility(compliancesText);
                displayed = compliancesText.isDisplayed();
            }
            logger.info("{}: {}", ctx, displayed);
            return displayed;
        } catch (TimeoutException te) {
            logger.warn("{}: not visible within timeout. {}", ctx, te.toString());
            return false;
        } catch (Exception e) {
            logger.error("{}: unexpected error: {}", ctx, e.toString(), e);
            return false;
        }
    }

}


