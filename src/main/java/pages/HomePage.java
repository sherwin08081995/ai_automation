package pages;

import base.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import java.util.ArrayList;
import java.util.List;

/**
 * HomePage.java
 * <p>
 * Purpose:
 * Page Object Model (POM) for the Home Page of the application.
 * This class encapsulates:
 * <p>
 * ✅ Validation of successful login through logo verification
 * ✅ Interaction with left-side navigation menu and tab selections
 * ✅ Retrieval and validation of compliance counts (Overall, Needs Action, In Progress, etc.)
 * ✅ Compliance tab navigation, record count verification, and screen validations
 * ✅ Return-to-home functionality and dynamic XPath-based element access
 * ✅ Exception-safe click operations with JS fallback and wait handling
 * <p>
 * Related Utilities:
 * - BasePage.java (common foundation for all pages)
 * - Helpers (for JS click, scroll, and wait utilities)
 * - WaitUtils (explicit wait handling)
 * <p>
 * Author:
 *
 * @author Sherwin
 * @since 17-06-2025
 */


public class HomePage extends BasePage {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//img[@alt='Vakilsearch']")
    private WebElement loginSuccessfulConfirmation;

    @FindBy(xpath = "//ul[@class='styles_menu__VVtmo']")
    private WebElement leftTabsContainer;

    @FindBy(xpath = ".//li//span[contains(@class, 'styles_name')]")
    private List<WebElement> tabNames;

    @FindBy(xpath = "//span[normalize-space()='Home']")
    private WebElement homeTab;

    @FindBy(xpath = "//span[normalize-space()='Compliances']")
    private WebElement compliancesTab;

    @FindBy(xpath = "//span[normalize-space()='Consult']")
    private WebElement consultationsTab;

    @FindBy(xpath = "//span[normalize-space()='Calendar']")
    private WebElement calendarTab;

    @FindBy(xpath = "//span[normalize-space()='Documents']")
    private WebElement documentsTab;

    @FindBy(xpath = "//span[normalize-space()='Reports']")
    private WebElement reportsTab;

    @FindBy(xpath = "//span[normalize-space()='Marketplace']")
    private WebElement marketplaceTab;

    @FindBy(xpath = "//span[normalize-space()='Settings']")
    private WebElement settingsTab;

    @FindBy(xpath = "//p[@class='text-[1.8rem] font-bold ']")
    private WebElement homeText;

    @FindBy(xpath = "//p[@class='text-[32px] max-lg:hidden font-semibold'][normalize-space()='Compliances']")
    private WebElement compliancesText;

    @FindBy(xpath = "//p[normalize-space()='Consultations']")
    private WebElement consultationsText;

    @FindBy(xpath = "//p[@class='text-[3rem] font-semibold'][normalize-space()='Calendar']")
    private WebElement calendarText;

    @FindBy(xpath = "//h1[normalize-space()='Documents']")
    private WebElement documentsText;

    @FindBy(xpath = "//h1[normalize-space()='Reports']")
    private WebElement reportsText;

    @FindBy(xpath = "//h1[normalize-space()='Marketplace']")
    private WebElement marketPlaceText;

    @FindBy(xpath = "//p[normalize-space()='Role management']")
    private WebElement settingsText;

    @FindBy(xpath = "//p[normalize-space(text())='Needs action']/preceding-sibling::p")
    private WebElement needsActionCount;

    @FindBy(xpath = "//p[normalize-space(text())='In progress']/preceding-sibling::p")
    private WebElement inProgressCount;

    @FindBy(xpath = "//p[normalize-space(text())='Compliant']/preceding-sibling::p")
    private WebElement compliantCount;

    @FindBy(xpath = "//p[normalize-space(text())='Upcoming']/preceding-sibling::p")
    private WebElement upcomingCount;

    @FindBy(xpath = "//p[contains(normalize-space(), 'Overall Compliances')]/span")
    private WebElement overallComplianceCount;

    @FindBy(xpath = "//p[normalize-space()='%s' and contains(@class,'text-gray-400')]/preceding-sibling::p[contains(@class,'text-[2.4rem]')]")
    private WebElement complianceStatusTabs;

    @FindBy(xpath = "//div[contains(@class,'css-wujj2v-control')]")
    private WebElement dueDateDropdown;

    @FindBy(xpath = "//p[contains(normalize-space(),'Overall Compliances')]/span")
    private WebElement toGetOverallComplianceCount;

    /**
     * Verifies if login is successful by checking the presence of a confirmation logo.
     *
     * @param expectedAltText The expected alt text of the logo image.
     * @return true if the login confirmation image has the expected alt text.
     */

    public boolean isLoginSuccessful(String expectedAltText) {
        try {
            String actualAltText = loginSuccessfulConfirmation.getAttribute("alt");
            if (actualAltText != null && actualAltText.equalsIgnoreCase(expectedAltText)) {
                return true;
            } else {
                throw new AssertionError("❌ Login alt text mismatch. Expected: " + expectedAltText + ", Found: " + actualAltText);
            }
        } catch (Exception e) {
            throw new RuntimeException("❌ Login validation failed.", e);
        }
    }


    /**
     * Checks if the left side navigation menu is visible.
     *
     * @return true if the menu is displayed.
     */
    public boolean isLeftMenuVisible() {
        return leftTabsContainer.isDisplayed();
    }

    /**
     * Retrieves all the visible left menu tab names.
     *
     * @return A list of menu item names.
     */

    public List<String> getLeftMenuItems() {
        List<String> menuTexts = new ArrayList<>();
        for (WebElement element : tabNames) {
            String text = element.getText();
            if (text != null && !text.trim().isEmpty()) {
                menuTexts.add(text.trim());
            }
        }
        return menuTexts;
    }

    /**
     * Clicks on a given tab from the left menu based on its name.
     * Uses normal click, and falls back to JS click if necessary.
     *
     * @param menuName The tab/menu name to click.
     */

    public void clickLeftMenu(String menuName) {
        WebElement menuItem = switch (menuName.trim().toLowerCase()) {
            case "home" -> homeTab;
            case "compliances" -> compliancesTab;
            case "consult", "consultations" -> consultationsTab;
            case "calendar" -> calendarTab;
            case "documents" -> documentsTab;
            case "reports" -> reportsTab;
            case "marketplace" -> marketplaceTab;
            case "settings" -> settingsTab;
            default -> throw new IllegalArgumentException("Menu item not found: " + menuName);
        };

        try {
            System.out.println("Clicking left menu: " + menuName);
            wait.waitForElementToBeClickable(menuItem);
            helpers.scrollToElement(driver, menuItem);
            helpers.click(menuItem);
        } catch (Exception e) {
            try {
                helpers.jsClick(driver, menuItem);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to click menu item '" + menuName + "' even with JS fallback.", ex);
            }
        }
    }

    /**
     * Waits for a specific menu's page to load by checking for the header or marker element.
     *
     * @param menuName The menu name whose content is expected.
     * @return true if the corresponding page marker is visible.
     */

    public boolean waitForMenuPageToLoad(String menuName) {
        try {
            switch (menuName.trim().toLowerCase()) {
                case "home":
                    return wait.waitForVisibility(homeText).isDisplayed();

                case "compliances":
                    return wait.waitForVisibility(compliancesText).isDisplayed();

                case "consult":
                case "consultations":
                    return wait.waitForVisibility(consultationsText).isDisplayed();

                case "calendar":
                    return wait.waitForVisibility(calendarText).isDisplayed();

                case "documents":
                    return wait.waitForVisibility(documentsText).isDisplayed();

                case "reports":
                    return wait.waitForVisibility(reportsText).isDisplayed();

                case "marketplace":
                    return wait.waitForVisibility(marketPlaceText).isDisplayed();

                case "settings":
                    return wait.waitForVisibility(settingsText).isDisplayed();

                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retrieves and parses the count from a web element located using the given XPath.
     * This utility method is used internally to extract numerical data such as compliance counts
     * by locating the element via its XPath and converting its text content into an integer.
     *
     * @param label A friendly label used in error messages to identify what the count represents.
     * @param xpath The XPath string used to locate the web element containing the count.
     * @return The parsed integer value from the element's text.
     * @throws RuntimeException if the element is not found, times out, or the text cannot be parsed to an integer.
     */

    private int getCountByXPath(String label, String xpath) {
        try {
            WebElement element = wait.waitForVisibility(By.xpath(xpath));
            return Integer.parseInt(element.getText().trim());
        } catch (NoSuchElementException | TimeoutException e) {
            throw new RuntimeException("❌ '" + label + "' count element not found.", e);
        } catch (NumberFormatException e) {
            throw new RuntimeException("❌ Failed to parse '" + label + "' count. Invalid number format.", e);
        }
    }


    /**
     * Returns the overall compliance count from the home page.
     *
     * @return An integer value of the overall compliance count.
     */

    public int getOverallCompliancesCount() {
        return getCountByXPath("Overall Compliances", "//p[contains(normalize-space(),'Overall Compliances')]/span");
    }

    /**
     * Gets the count displayed for the "Needs Action" compliance tab.
     *
     * @return Integer count for Needs Action.
     */

    public int getNeedsActionCount() {
        return getCountByXPath("Needs action", "//p[normalize-space(text())='Needs action']/preceding-sibling::p");
    }

    /**
     * Gets the count displayed for the "In Progress" compliance tab.
     *
     * @return Integer count for In Progress.
     */

    public int getInProgressCount() {
        return getCountByXPath("In progress", "//p[normalize-space(text())='In progress']/preceding-sibling::p");
    }

    /**
     * Gets the count displayed for the "Compliant" compliance tab.
     *
     * @return Integer count for Compliant.
     */

    public int getCompliantCount() {
        return getCountByXPath("Compliant", "//p[normalize-space(text())='Compliant']/preceding-sibling::p");
    }

    /**
     * Gets the count displayed for the "Upcoming" compliance tab.
     *
     * @return Integer count for Upcoming.
     */

    public int getUpcomingCount() {
        return getCountByXPath("Upcoming", "//p[normalize-space(text())='Upcoming']/preceding-sibling::p");
    }


    /**
     * Retrieves the WebElement representing the clickable compliance status tab card
     * for the given tab name.
     *
     * @param tabName The visible label of the compliance tab (e.g., "Needs action", "In progress").
     * @return WebElement of the clickable tab card container.
     */
    private WebElement getComplianceTabCard(String tabName) {
        String cardXpath = String.format("//p[normalize-space()='%s' and contains(@class,'text-gray-400')]/ancestor::div[contains(@class,'cursor-pointer')]", tabName);
        return wait.waitForElementToBeClickable(By.xpath(cardXpath));
    }

    /**
     * Retrieves the WebElement representing the count text displayed on the
     * compliance status tab card before the label.
     *
     * @param tabName The visible label of the compliance tab (e.g., "Needs action", "In progress").
     * @return WebElement of the count value preceding the tab label.
     */
    private WebElement getComplianceTabCount(String tabName) {
        String countXpath = String.format("//p[normalize-space()='%s' and contains(@class,'text-gray-400')]/preceding-sibling::p[contains(@class,'text-[2.4rem]')]", tabName);
        return wait.waitForVisibility(By.xpath(countXpath));
    }

    /**
     * Clicks on the compliance status card for the given tab and returns its count.
     *
     * @param tabName The name of the tab to click.
     * @return The count displayed on the tab before clicking it.
     */

    public int clickComplianceStatusTabAndGetCount(String tabName) {
        try {
            WebElement countElement = getComplianceTabCount(tabName);
            int count = Integer.parseInt(countElement.getText().trim());

            helpers.scrollToElement(driver, countElement);
            WebElement cardElement = getComplianceTabCard(tabName);
            helpers.scrollToElement(driver, cardElement);
            helpers.click(cardElement);

            return count;
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to click tab or retrieve count for: " + tabName, e);
        }
    }


    /**
     * Verifies whether the Compliance screen is visible after navigation.
     *
     * @return true if the screen's header is visible.
     */
    public boolean isComplianceScreenVisible() {
        try {
            WebElement complianceNavigated = wait.waitForVisibility(compliancesText);
            return complianceNavigated != null && complianceNavigated.isDisplayed();
        } catch (TimeoutException e) {
            System.err.println("❌ Timeout: Compliance screen element not visible.");
        } catch (Exception e) {
            System.err.println("❌ Error while checking compliance screen visibility:");
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Gets the actual record count for a specific section in the Compliance screen.
     *
     * @param sectionName The label of the section to retrieve count from.
     * @return The count of records, or -1 if not found or unparseable.
     */
    public int getRecordCountForSection(String sectionName) {
        try {
            String xpath = String.format("//p[translate(normalize-space(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='%s']/following-sibling::span", sectionName.toLowerCase());

            WebElement countElement = wait.waitForVisibility(By.xpath(xpath));
            String countText = countElement.getText().replaceAll("[^\\d]", "").trim(); // remove non-digits

            return Integer.parseInt(countText);
        } catch (TimeoutException e) {
            System.err.println("❌ Timeout: Could not find record count span for section: " + sectionName);
        } catch (NumberFormatException e) {
            System.err.println("❌ Count value is not a valid number for section: " + sectionName);
        } catch (Exception e) {
            System.err.println("❌ Error fetching record count for section: " + sectionName);
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * Navigates back to the home page by clicking the Home menu item.
     *
     * @return true if the home screen loads successfully.
     */
    public boolean goBackToHomePage() {
        try {
            WebElement homeMenu = wait.waitForElementToBeClickable(By.xpath("//span[normalize-space()='Home']"));
            homeMenu.click();

            WebElement validationElement = wait.waitForVisibility(By.xpath("//p[contains(text(),'Overall Compliances')]"));
            return validationElement != null && validationElement.isDisplayed();
        } catch (TimeoutException e) {
            System.err.println("❌ Timeout while trying to return to Home page.");
        } catch (Exception e) {
            System.err.println("❌ Error while returning to Home page:");
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Returns the count displayed on the given Compliance status tab without clicking it.
     *
     * @param tabName The name of the compliance tab (e.g., "Pending", "Completed")
     * @return The integer count shown on the tab label.
     */
    public int getDisplayedTabCount(String tabName) {
        try {
            String xpath = String.format("//p[normalize-space()='%s' and contains(@class,'text-gray-400')]/preceding-sibling::p[contains(@class,'text-[2.4rem]')]", tabName);

            WebElement countElement = driver.findElement(By.xpath(xpath));
            String countText = countElement.getText().replaceAll("[^0-9]", "");
            return Integer.parseInt(countText);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("❌ Count element not found for tab: " + tabName, e);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("❌ Count text could not be parsed for tab: " + tabName);
        }
    }

    public String getSelectedDueDateFilter() {
        try {
            // Locate the selected value dynamically (class changes slightly, so we use contains)
            WebElement selectedValueElement = wait.waitForVisibility(By.xpath("//div[@class='css-2p0j19-singleValue'][normalize-space()='This Month']"));
            String selectedText = selectedValueElement.getText().trim();
            if (selectedText.isEmpty()) {
                throw new IllegalStateException("Selected Due Date dropdown value is blank.");
            }
            return selectedText;
        } catch (TimeoutException e) {
            throw new IllegalStateException("Due Date selected value not found after waiting.", e);
        }
    }


    /**
     * Clicks the Due Date filter dropdown after validating its presence and interactivity.
     */
    public void clickDueDateDropdown() {
        try {
            WebElement dropdown = wait.waitForElementToBeClickable(dueDateDropdown);

            if (dropdown == null || !dropdown.isDisplayed() || !dropdown.isEnabled()) {
                throw new IllegalStateException("Due Date dropdown is either not visible or not enabled.");
            }
            dropdown.click();
            logger.info("Clicked the Due Date dropdown successfully.");

        } catch (Exception e) {
            logger.error("Failed to click the Due Date dropdown: {}", e.getMessage());
            throw new RuntimeException("Unable to click Due Date dropdown.", e);
        }
    }


    /**
     * Retrieves all options listed in the Due Date filter dropdown.
     *
     * @return list of dropdown option texts.
     */
    public List<String> getDueDateFilterOptions() {
        try {
            // Wait for dropdown menu with options to be visible
            List<WebElement> options = wait.waitForVisibilityOfAllElements(By.xpath("//div[@role='option']"));

            if (options.isEmpty()) {
                throw new RuntimeException("No dropdown options found.");
            }

            List<String> optionTexts = new ArrayList<>();
            for (WebElement option : options) {
                String rawText = option.getText();
                String cleanedText = rawText.replace("\u00A0", " ") // replace non-breaking space
                        .replaceAll("\\s+", " ") // normalize multiple spaces
                        .trim();
                logger.info("Dropdown option found: '{}'", cleanedText);
                optionTexts.add(cleanedText);
            }

            logger.info("Final normalized options list: {}", optionTexts);
            return optionTexts;

        } catch (Exception e) {
            logger.error("Failed to fetch Due Date dropdown options: {}", e.getMessage());
            throw e;
        }
    }


    /**
     * Selects a specific option from the Due Date filter dropdown.
     *
     * @param valueToSelect The visible text of the dropdown option to select (e.g., "This Month", "Previous Quarter").
     * @throws RuntimeException if the dropdown or the specified option is not found or clickable.
     */

    public void selectDueDateFromDropdown(String valueToSelect) {
        try {
            // Click the dropdown trigger
            WebElement dropdownTrigger = wait.waitForElementToBeClickable(By.cssSelector(".css-wujj2v-control"));
            dropdownTrigger.click();

            // Build XPath for the desired option
            String optionXpath = String.format("//div[@role='option' and normalize-space()='%s']", valueToSelect);

            // Wait until the desired option is clickable (not just visible)
            wait.waitForElementToBeClickable(By.xpath(optionXpath)).click();

            // Optional: wait for loading/refresh after selection (if any)
            wait.waitForInvisibility(By.cssSelector(".loading-indicator"));

            logger.info("✅ Successfully selected Due Date option: {}", valueToSelect);

        } catch (StaleElementReferenceException staleEx) {
            logger.warn("♻️ Retrying after stale element for '{}'", valueToSelect);
            // Retry once more (optionally add a loop or retry logic here)
            WebElement dropdownTrigger = wait.waitForElementToBeClickable(By.cssSelector(".css-wujj2v-control"));
            dropdownTrigger.click();

            String optionXpath = String.format("//div[@role='option' and normalize-space()='%s']", valueToSelect);
            wait.waitForElementToBeClickable(By.xpath(optionXpath)).click();

            logger.info("✅ Successfully retried selection for: {}", valueToSelect);

        } catch (Exception e) {
            logger.error("❌ Failed in HomePage.selectDueDateFromDropdown: {}", e.getMessage());
            throw e;
        }
    }


    /**
     * Gets the total of 'Risk Based Compliances'.
     **/

    public int getRiskBasedCompliancesCount() {
        return getCountByXPath("Risk Based Compliances", "//p[contains(text(),'Risk Based Compliances')]/following-sibling::p");
    }

    /**
     * Clicks on the 'View all' button in the Compliance section to navigate
     * to the full Compliance page.
     */

    public void clickViewAllCompliance() {
        try {
            By viewAllBtnLocator = By.xpath("//p[normalize-space()='View all']");

            WebElement viewAllBtn = wait.waitForElementToBeClickable(viewAllBtnLocator);

            if (viewAllBtn == null || !viewAllBtn.isDisplayed() || !viewAllBtn.isEnabled()) {
                throw new IllegalStateException("❌ 'View all' <p> tag is not visible or enabled.");
            }

            viewAllBtn.click();
            logger.info("✅ Clicked 'View all' to navigate to Compliance Page");

            // Optional: wait for page load or next expected element
            wait.waitForInvisibility(By.cssSelector(".loading-indicator"));

        } catch (Exception e) {
            logger.error("❌ Failed to click 'View all': {}", e.getMessage());
            throw new RuntimeException("Unable to click 'View all'.", e);
        }
    }


    /**
     * Selects a Due Date filter option from the dropdown on the Compliance page.
     * Performs a two-step dropdown reset and selection using JavaScript.
     *
     * @param valueToSelect the exact visible text of the Due Date filter to select
     */

    public void selectDueDateDropdownFromCompliancePage(String valueToSelect) {
        try {
            logger.info("🕒 Selecting Due Date filter: '{}'", valueToSelect);

            By dropdownTriggerLocator = By.xpath("//label[normalize-space()='Due Date']/following-sibling::div[1]");
            By dropdownTextLocator = By.xpath("//label[normalize-space()='Due Date']/following-sibling::div[1]//p");

            // Step 1: Click to open dropdown
            WebElement dropdownTrigger = wait.waitForElementToBeClickable(dropdownTriggerLocator);
            dropdownTrigger.click();

            // Step 2: Wait for it to reset to "Select"
            wait.waitForTextToBePresent(dropdownTextLocator, "Select");

            // Step 3: Click again to re-open dropdown
            dropdownTrigger = wait.waitForElementToBeClickable(dropdownTriggerLocator);
            dropdownTrigger.click();

            // Step 4: Wait until dropdown container is visible
            By dropdownContainerLocator = By.xpath("//div[contains(@class,'z-30') and contains(@class,'pointer-events-auto')]");
            wait.waitForVisibility(dropdownContainerLocator);

            // Step 5: Locate the desired option
            By optionLocator = By.xpath(String.format("//div[contains(@class,'z-30')]//p[normalize-space()='%s']", valueToSelect));
            wait.waitForPresence(optionLocator);
            WebElement option = driver.findElement(optionLocator);

            // Step 6: Perform JS click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);

            // Step 7: Wait for the loading spinner to disappear
            wait.waitForInvisibility(By.cssSelector(".loading-indicator"));

            logger.info("✅ Successfully selected Due Date option: '{}'", valueToSelect);

        } catch (Exception e) {
            logger.error("❌ Failed to select Due Date '{}': {}", valueToSelect, e.getMessage());
            throw e;
        }
    }


    /**
     * Extracts the numerical count displayed in the 'All()' tab on the Compliance page.
     *
     * @return the integer count shown next to the 'All' tab
     */

    public int getAllTabComplianceCountFromCompliancePage() {
        try {
            logger.info("📊 Extracting count from 'All()' tab on Compliance Page");

            By allTabCountLocator = By.xpath("//p[normalize-space()='All']/following-sibling::span");

            WebElement countElement = wait.waitForVisibility(allTabCountLocator);
            String countText = countElement.getText().replaceAll("[^0-9]", "");

            int count = Integer.parseInt(countText);
            logger.info("✅ Extracted count from 'All' tab: {}", count);
            return count;

        } catch (Exception e) {
            logger.error("❌ Failed to extract 'All' tab count: {}", e.getMessage());
            throw e;
        }
    }


}

