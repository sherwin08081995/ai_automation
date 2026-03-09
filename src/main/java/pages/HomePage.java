package pages;

import base.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.ReusableCommonMethods;
import utils.TestDataGenerator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

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

    // stamp set in clickGetStarted()
    private volatile long lastGetStartedClickNanos = -1L;
    private volatile double lastComplianceLoadSeconds = -1.0;
    private volatile String lastComplianceLoadFormatted = "";
    private volatile boolean lastComplianceOverOneMinute = false;


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

    @FindBy(xpath = "//span[normalize-space()='Service Hub']")
    private WebElement servicehub;

    @FindBy(xpath = "//span[normalize-space()='Users & Roles']")
    private WebElement usersandroles;

    @FindBy(xpath = "//p[@class='text-[1.8rem] font-bold ']")
    private WebElement homeText;

    @FindBy(xpath = "//label[normalize-space()='Compliance Department']")
    private WebElement compliancesText;

    @FindBy(xpath = "//p[normalize-space()='Consultations']")
    private WebElement consultationsText;

    @FindBy(xpath = "//span[normalize-space()='Mandatory compliances']")
    private WebElement calendarText;

    @FindBy(xpath = "//p[normalize-space()='Select a folder to view your compliance documents, bills, and related files']")
    private WebElement documentsText;

    @FindBy(xpath = "//h1[normalize-space()='Reports']")
    private WebElement reportsText;

    @FindBy(xpath = "//h1[normalize-space()='Service Hub']")
    private WebElement serviceHubText;

    @FindBy(xpath = "//p[normalize-space()='User list']")
    private WebElement usersandrolesText;

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
    private WebElement overallCompliancesSection;

    @FindBy(xpath = "//div[@class='css-1xc3v61-indicatorContainer']")
    private WebElement orgDropDown;

    @FindBy(xpath = "//div[contains(@class, 'css-17b41la-menu')]//div[normalize-space()=\"")
    private WebElement visibleNamefromDropdown;

    @FindBy(xpath = "//div[contains(@class, 'css-17b41la-menu')]/div")
    private WebElement orgDropDownDatas;

    @FindBy(css = "div[class*='fixed'][class*='inset-0'][class*='bg-white']")
    private WebElement overlayElement;

    @FindBy(xpath = "//tr[contains(@class,'cursor-pointer')]/td[2]//p")
    private List<WebElement> complianceOrgColumnValues;

    @FindBy(xpath = "//div[contains(@class,'css-d2lvuu-singleValue')]")
    private WebElement selectedOrgElement;

    @FindBy(xpath = "//*[text()[normalize-space()='+ Add Organization']]")
    private List<WebElement> addOrgDropdownCandidates;

    @FindBy(xpath = "//p[normalize-space()='+ Add Organization']")
    private WebElement topRightAddOrgButton;

    @FindBy(xpath = "//span[contains(@class,'styles_name') and normalize-space()='Home']")
    private WebElement homeTabElement;

    @FindBy(xpath = "//p[@class='text-[1.8rem] font-bold ']")
    private WebElement homePageHeaderText;

    @FindBy(xpath = "//p[text()='High']/parent::div/following-sibling::p")
    private WebElement highRiskCountPath;

    @FindBy(xpath = "//table[contains(@class,'table-auto')]")
    public WebElement complianceReportTable;

    @FindBy(xpath = "//p[normalize-space()='High']")
    private WebElement highCategoryElement;

    @FindBy(xpath = "//p[normalize-space()='Medium']")
    private WebElement mediumCategoryElement;

    @FindBy(xpath = "//p[normalize-space()='Low']")
    private WebElement lowCategoryElement;

    @FindBy(xpath = "//table[contains(@class,'table-auto')]//tbody/tr[contains(@class,'cursor-pointer')]")
    private List<WebElement> complianceReportRows;

    @FindBy(xpath = "//div[contains(@class,'styles_container__SCu3N')]//p[contains(@class,'font-bold truncate')]")
    private WebElement complianceDetailsTitle;

    @FindBy(xpath = "//label[text()='Month']")
    private WebElement monthLabel;

    @FindBy(xpath = "//label[text()='Year']")
    private WebElement yearLabel;

    @FindBy(xpath = "//label[text()='Month']/following::div[contains(@class,'singleValue')][1]")
    private WebElement selectedMonth;

    @FindBy(xpath = "//label[text()='Year']/following::div[contains(@class,'singleValue')][1]")
    private WebElement selectedYear;

    @FindBy(xpath = "//div[@id='monthly-compliance-list']//p[contains(@class,'font-semibold') and contains(@class,'text-[#667383]')]")
    private List<WebElement> complianceCalendarRecords;

    @FindBy(xpath = "//p[contains(@class, 'font-medium') and contains(text(), '20')]")
    private WebElement rightPanelDueDate;

    @FindBy(xpath = "//p[contains(text(),'Compliance Calendar')]/following::div[contains(@id,'Month')][1]//div[contains(@class,'control')]")
    private WebElement monthDropdown;

    @FindBy(xpath = "//p[contains(text(),'Compliance Calendar')]/following::div[contains(@id,'Year')][1]//div[contains(@class,'control')]")
    private WebElement yearDropdown;

    @FindBy(id = "monthly-compliance-list")
    private WebElement scrollContainer;

    @FindBy(id = "monthly-compliance-list")
    private WebElement monthlyList;


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
     * Retrieves the list of visible and non-empty text labels from the left menu tab elements.
     * It filters out hidden, empty, or null text entries and returns a clean, trimmed list.
     *
     * @return List of visible, non-empty left menu tab names
     */
    public List<String> getLeftMenuItems() {
        List<String> menuTexts = new ArrayList<>();

        for (WebElement element : tabNames) {
            try {
                if (element.isDisplayed()) {
                    String text = element.getText();
                    if (text != null && !text.trim().isEmpty()) {
                        menuTexts.add(text.trim());
                    } else {
                        System.out.println("⚠️ Skipped element with empty or blank text.");
                    }
                } else {
                    System.out.println("⚠️ Skipped hidden menu item.");
                }
            } catch (StaleElementReferenceException | NoSuchElementException e) {
                System.out.println("❌ Error accessing menu element: " + e.getMessage());
            }
        }

        if (menuTexts.isEmpty()) {
            System.out.println("❗ No visible menu items were found.");
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
            case "service hub" -> servicehub;
            case "users & roles" -> usersandroles;
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
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {
        }
    }


    public boolean waitForMenuPageToLoad(String menuName, Duration timeout) {
        try {
            // small pause after clicking menu (optional, your existing code)
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }

            switch (menuName.trim().toLowerCase()) {
                case "home":
                    return wait.waitForVisibilitywithacustomtimeout(homeText, timeout).isDisplayed();

                case "compliances":
                    return wait.waitForVisibilitywithacustomtimeout(compliancesText, timeout).isDisplayed();

                case "consult":
                case "consultations":
                    return wait.waitForVisibilitywithacustomtimeout(consultationsText, timeout).isDisplayed();

                case "calendar":
                    return wait.waitForVisibilitywithacustomtimeout(calendarText, timeout).isDisplayed();

                case "documents":
                    return wait.waitForVisibilitywithacustomtimeout(documentsText, timeout).isDisplayed();

                case "reports":
                    return wait.waitForVisibilitywithacustomtimeout(reportsText, timeout).isDisplayed();

                case "service hub":
                    return wait.waitForVisibilitywithacustomtimeout(serviceHubText, timeout).isDisplayed();

                case "users & roles":
                    return wait.waitForVisibilitywithacustomtimeout(usersandrolesText, timeout).isDisplayed();

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
            String rawText = element.getText();
            String digitsOnly = rawText.replaceAll("[^0-9]", "");
            return Integer.parseInt(digitsOnly);
        } catch (NoSuchElementException | TimeoutException e) {
            throw new RuntimeException("❌ '" + label + "' count element not found.", e);
        } catch (NumberFormatException e) {
            throw new RuntimeException("❌ Failed to parse '" + label + "' count. Invalid number format.", e);
        }
    }


    /**
     * Returns the overall compliance count displayed on the Home page.
     *
     * @return The total number of overall compliances as an integer.
     * @throws RuntimeException if the element is not found or the text cannot be parsed into a number.
     */

    public int getOverallCompliancesCount() {
        return getCountByXPath("Overall Compliances", "//p[contains(normalize-space(),'Overall Compliances')]/span");
    }

    /**
     * Returns the compliance record count displayed under the Compliance Calendar section.
     *
     * @return The number of compliance records.
     * @throws RuntimeException if the element is not found or text cannot be parsed to a number.
     */

    public int getComplianceCalendarRecordCount() {
        return getCountByXPath("Compliance Calendar Records", "//p[contains(@class,'font-semibold')]/span[contains(@class,'font-medium')]");
    }


    /**
     * Checks whether the "Overall Compliances" section is visible on the Home page.
     *
     * @return true if the Overall Compliances section is visible; false otherwise.
     */

    public boolean isOverallCompliancesSectionVisible() {
        try {
            wait.waitForVisibility(overallCompliancesSection);
            return overallCompliancesSection.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
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
     * Gets the count displayed for the "Completed" compliance tab.
     *
     * @return Integer count for Completed.
     */

    public int getCompliantCount() {
        return getCountByXPath("Completed", "//p[normalize-space(text())='Completed']/preceding-sibling::p");
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
     * Retrieves the WebElement representing the numeric count displayed on a
     * compliance status card before its label (e.g., "2" for "Needs action").
     *
     * @param tabName The visible label of the compliance tab (e.g., "Needs action", "In progress").
     * @return WebElement of the count preceding the given tab label.
     * @throws NoSuchElementException if the count element is not found or not visible.
     */
    private WebElement getComplianceTabCount(String tabName) {
        String countXpath = String.format("//p[normalize-space()='%s' and contains(@class,'text-gray-400')]" + "/preceding-sibling::p[contains(@class,'text-[2.4rem]')]", tabName);

        try {
            WebElement element = wait.waitForVisibility(By.xpath(countXpath));
            if (element.isDisplayed()) {
                return element;
            } else {
                throw new NoSuchElementException("Count element found but not visible for tab: " + tabName);
            }
        } catch (TimeoutException | NoSuchElementException e) {
            System.out.println("❌ Count element not found for tab '" + tabName + "': " + e.getMessage());
            throw e; // propagate if calling method needs to assert it
        }
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
        // keep existing default behavior (your current method)
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

    // NEW: timeout-aware version used by the Then step
    public boolean isComplianceScreenVisible(Duration timeout) {
        try {
            WebElement el = wait.waitForVisibilitywithacustomtimeout(compliancesText, timeout);
            return el != null && el.isDisplayed();
        } catch (TimeoutException e) {
            System.err.println("❌ Timeout: Compliance screen element not visible within " + timeout.getSeconds() + "s.");
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error while checking compliance screen visibility:");
            e.printStackTrace();
            return false;
        }
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

//    /**
//     * Clicks on the 'View all' button in the Compliance section to navigate
//     * to the full Compliance page.
//     */

//    public void clickViewAllCompliance() {
//        try {
//            By viewAllBtnLocator = By.xpath("//p[normalize-space()='View all']");
//
//            WebElement viewAllBtn = wait.waitForElementToBeClickable(viewAllBtnLocator);
//
//            if (viewAllBtn == null || !viewAllBtn.isDisplayed() || !viewAllBtn.isEnabled()) {
//                throw new IllegalStateException("❌ 'View all' <p> tag is not visible or enabled.");
//            }
//
//            viewAllBtn.click();
//            logger.info("✅ Clicked 'View all' to navigate to Compliance Page");
//
//            // Optional: wait for page load or next expected element
//            wait.waitForInvisibility(By.cssSelector(".loading-indicator"));
//
//        } catch (Exception e) {
//            logger.error("❌ Failed to click 'View all': {}", e.getMessage());
//            throw new RuntimeException("Unable to click 'View all'.", e);
//        }
//    }
    /**
     * Clicks on the 'View all' button in the Compliance section to navigate
     * to the full Compliance page.
     */
    public void clickViewAllCompliance() {
        try {
            // 1) Try to close chat bot if it is present (non-blocking)
            closeChatBotIfPresent();

            // 2) Proceed with normal flow
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
     * @throws Exception if the count element is not found or not parseable
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


    /**
     * Clicks the Home tab via normal click → JS fallback if needed.
     */
    public void clickHomeTab() {
        try {
            wait.waitForElementToBeClickable(homeTab);
            logger.info("🔄 Clicking Home tab to navigate...");

            try {
                homeTab.click();
            } catch (Exception e) {
                logger.warn("⚠️ Standard click failed, retrying with JavaScript… ({})", e.getMessage());
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", homeTab);
            }

        } catch (StaleElementReferenceException stale) {
            // Re-wire page & retry once (stale after navigation/DOM swap)
            logger.warn("♻️ Home tab went stale, re-initializing elements and retrying…");
            wait.waitForElementToBeClickable(homeTab);
            homeTab.click();

        } catch (Exception e) {
            logger.error("❌ Failed to click Home tab: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Waits for the Home page’s unique marker to be visible within the given timeout.
     */
    public boolean waitForHomeLoaded(Duration timeout) {
        try {
            wait.waitForVisibility(homeText);

            boolean displayed = homeText.isDisplayed();
            if (displayed) {
                try {
                    logger.info("✅ Home page loaded successfully: '{}'", homeText.getText());
                } catch (Exception ignored) {
                    logger.info("✅ Home page loaded successfully.");
                }
            } else {
                logger.warn("⚠️ Home page marker not visible after wait.");
            }
            return displayed;

        } catch (TimeoutException te) {
            logger.error("❌ Home page did not load within {} ms.", timeout.toMillis());
            return false;
        } catch (Exception e) {
            logger.error("❌ Error while waiting for Home page: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Back-compat: your old helper now delegates to click + wait with NAV SLA.
     */
    public boolean navigateAndConfirmHomePage() {
        try {
            clickHomeTab();
            // Use your global NAV fail SLA (e.g., 20s) if you want to keep this path
            long failMs = ReusableCommonMethods.NAV_FAIL_MS;
            return waitForHomeLoaded(Duration.ofMillis(failMs));
        } catch (Exception e) {
            logger.error("❌ Failed to navigate and confirm Home page: {}", e.getMessage());
            return false;
        }
    }


    /**
     * Checks if the '+ Add Organization' button is visible in the top-right corner of the Home page.
     *
     * @return true if the button is visible, false otherwise
     */


    public boolean isAddOrgButtonTopRightVisible() {
        try {
            By topRightAddBtn = By.xpath("//p[normalize-space()='+ Add Organization']");
            return wait.waitForVisibility(topRightAddBtn).isDisplayed();
        } catch (Exception e) {
            logger.error("Top-right '+ Add Organization' button not visible: {}", e.getMessage());
            return false;
        }
    }


    /**
     * Opens the organization dropdown and checks for visibility of the '+ Add Organization' option inside it.
     *
     * @return true if the '+ Add Organization' option is visible inside the dropdown, false otherwise
     */

    public boolean isAddOrgButtonUnderDropdownVisible() {
        try {
            logger.info("🔽 Trying to open organization dropdown...");

            WebElement dropdown = wait.waitForElementToBeClickable(By.xpath("//div[@class='css-1xc3v61-indicatorContainer']"));

            try {
                dropdown.click();
            } catch (Exception e) {
                logger.warn("⚠️ Normal click failed, retrying with JavaScript...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdown);
            }

            Thread.sleep(3000); // allow menu to render

            logger.info("🌍 Searching globally for '+ Add Organization'...");

            List<WebElement> candidates = driver.findElements(By.xpath("//*[text()[normalize-space()='+ Add Organization']]"));

            logger.info("📌 Found {} candidates", candidates.size());

            for (WebElement el : candidates) {
                String tag = el.getTagName();
                String cls = el.getAttribute("class");
                boolean visible = el.isDisplayed();
                String txt = el.getText().trim();
                logger.info("➡️ Candidate: tag=<{}> class='{}' visible={} text='{}'", tag, cls, visible, txt);

                if (visible) {
                    logger.info("✅ Found visible '+ Add Organization' inside dropdown/portal.");
                    return true;
                }
            }

            logger.warn("❌ No visible '+ Add Organization' option found after dropdown click.");
            return false;

        } catch (Exception e) {
            logger.error("❌ Exception in isAddOrgButtonUnderDropdownVisible(): {}", e.getMessage());
            return false;
        }
    }


    /**
     * Navigates to the Compliance page by clicking the 'Compliances' tab and waits for the page to load.
     *
     * @throws RuntimeException if navigation fails
     */

    public boolean goToCompliancePage() {
        try {
            logger.info("📂 Navigating to the Compliance page...");

            wait.waitForElementToBeClickable(compliancesTab);

            try {
                compliancesTab.click();
            } catch (Exception e) {
                logger.warn("⚠️ Standard click failed, retrying with JavaScript...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", compliancesTab);
            }

            wait.waitForVisibility(compliancesText);
            boolean displayed = compliancesText.isDisplayed();

            logger.info(displayed ? "✅ Compliance page loaded successfully: '{}'" : "⚠️ Compliance page text not visible.", compliancesText.getText());

            return displayed; // ✅ Change: Added boolean return

        } catch (Exception e) {
            logger.error("❌ Failed to navigate to Compliance page: {}", e.getMessage());
            return false; // ✅ Change: Added return false on failure
        }
    }


    /**
     * Retrieves the name of the currently selected organization from the dropdown.
     *
     * @return the trimmed name of the selected organization, or an empty string if not found
     */

    public String getSelectedOrganizationName() {
        try {
            By selectedOrgLocator = By.xpath("//div[@class='css-d2lvuu-singleValue']");
            WebElement selectedElement = wait.waitForVisibility(selectedOrgLocator);
            String selectedOrg = selectedElement.getText().trim();
            logger.info("✅ Selected organization name: {}", selectedOrg);
            return selectedOrg;
        } catch (Exception e) {
            logger.error("❌ Failed to fetch selected organization name: {}", e.getMessage());
            return "";
        }
    }


    /**
     * Verifies that all office entries listed on the Compliance page match the currently selected organization.
     *
     * @param expectedOrg the organization name expected in each compliance row
     * @return true if all rows match the expected organization, false otherwise
     */

    public boolean areAllOfficeValuesMatchingSelectedOrg(String expectedOrg) {
        List<WebElement> orgElements = driver.findElements(By.xpath("//tr[contains(@class,'cursor-pointer')]/td[2]//p"));

        if (orgElements.isEmpty()) {
            logger.warn("⚠️ No compliance rows found.");
            return true;
        }

        for (WebElement el : orgElements) {
            String actual = el.getText().trim();
            if (actual.isBlank()) {
                logger.info("⚠️ Skipping empty compliance org row");
                continue;
            }

            if (!actual.equalsIgnoreCase(expectedOrg)) {
                logger.error("❌ Org mismatch: Expected '{}', but found '{}'", expectedOrg, actual);
                return false;
            }
        }

        logger.info("✅ All compliance rows match the selected organization: {}", expectedOrg);
        return true;
    }


    /**
     * Waits for overlay or modal elements (like loaders) to disappear from the UI.
     * Logs a warning if it times out.
     */
    public void waitForOverlayToDisappear() {
        try {

            wait.waitForInvisibility(By.cssSelector("div[class*='fixed'][class*='inset-0'][class*='bg-white']"));
            logger.info("✅ Overlay/modal disappeared.");
        } catch (TimeoutException e) {
            logger.warn("⚠️ Overlay still visible after timeout, proceeding anyway.");
        }
    }


    /**
     * Parses the 'All offices' dropdown block to extract individual organization names listed under it.
     *
     * @return list of organization names as strings
     */

    public List<String> getAllOrgNamesFromDropdown() {
        List<String> orgNames = new ArrayList<>();
        try {
            waitForOverlayToDisappear();

            WebElement dropdown = wait.waitForElementToBeClickable(By.xpath("//div[@class='css-1xc3v61-indicatorContainer']"));
            dropdown.click();

            By dropdownOptionsLocator = By.xpath("//div[contains(@class, 'css-17b41la-menu')]/div");
            wait.waitForVisibility(dropdownOptionsLocator);

            List<WebElement> allOptions = driver.findElements(dropdownOptionsLocator);

            for (WebElement el : allOptions) {
                String text = el.getText().trim();

                if (text.startsWith("All offices") && text.contains("\n")) {
                    String[] lines = text.split("\\n");
                    for (int i = 1; i < lines.length; i++) {
                        String org = lines[i].trim();
                        if (!org.isEmpty()) {
                            logger.info("✅ Extracted org from All Offices block: '{}'", org);
                            orgNames.add(org);
                        }
                    }
                    break; // Only one block expected
                }
            }

            logger.info("✅ Total organizations extracted: {}", orgNames.size());
            return orgNames;

        } catch (Exception e) {
            logger.error("❌ Error in getAllOrgNamesFromDropdown: {}", e.getMessage());
            return Collections.emptyList();
        }
    }


    /**
     * Selects the given organization from the dropdown using its visible name.
     *
     * @param orgName the exact name of the organization to be selected
     * @throws RuntimeException if the organization cannot be clicked or found
     */


    public void selectOrganizationFromDropdown(String orgName) {
        try {

            WebElement dropdown = wait.waitForElementToBeClickable(By.xpath("//div[@class='css-1xc3v61-indicatorContainer']"));
            dropdown.click();
            String xpath = "//div[contains(@class, 'css-17b41la-menu')]//div[normalize-space()=\"" + orgName + "\"]";
            WebElement orgToSelect = wait.waitForElementToBeClickable(By.xpath(xpath));

            try {
                orgToSelect.click();
            } catch (Exception e) {
                logger.warn("⚠️ Standard click failed for '{}', retrying with JS...", orgName);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", orgToSelect);
            }

            logger.info("✅ Selected organization from dropdown: {}", orgName);
        } catch (Exception e) {
            logger.error("❌ Failed to select organization '{}': {}", orgName, e.getMessage());
            throw new RuntimeException("Failed to select organization: " + orgName, e);
        }
    }

    /**
     * Clicks the "+ Add Organization" button using the visible button text.
     *
     * @param buttonText The expected button text, used for logging or validation (optional usage).
     * @throws RuntimeException if the button is not found or not clickable.
     */
    public void clickAddOrganisationButton(String buttonText) {
        By addOrgBtn = By.xpath("//p[normalize-space()='+ Add Organization']");
        By blockingOverlay = By.cssSelector("div.fixed.inset-0.z-\\[999999\\]");

        try {
            // 1) Wait for overlay to go away (if present)
            wait.waitForInvisibilityOfElement(blockingOverlay, 30);

            // 2) Wait for button clickable
            WebElement button = wait.waitForElementToBeClickable(addOrgBtn);

            // 3) Scroll + click
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", button);
            button.click();

        } catch (Exception e) {
            // Fallback: JS click (last resort)
            try {
                WebElement button = driver.findElement(addOrgBtn);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
            } catch (Exception jsEx) {
                throw new RuntimeException(
                        "Failed to click the '+ Add Organization' button. Text: '" + buttonText + "'", e
                );
            }
        }
    }



    /**
     * Verifies if the "Create Organization" popup is visible by checking the business type prompt.
     *
     * @return true if the popup is visible, false otherwise.
     */
    public boolean verifyCreateOrganizationPopupIsVisible() {
        String xpath = "//p[normalize-space()='What is the type of your business?']";
        try {
            WebElement popup = wait.waitForVisibility(By.xpath(xpath));
            return popup != null && popup.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Selects a value from a searchable dropdown based on the visible question text label.
     *
     * <p>This method:
     * <ul>
     *     <li>Finds the dropdown container associated with the given question text</li>
     *     <li>Clicks the dropdown (with JavaScript fallback)</li>
     *     <li>Types the desired value and presses ENTER</li>
     *     <li>Validates that the correct value was selected</li>
     * </ul>
     *
     * @param questionText  The visible text label of the question preceding the dropdown
     * @param valueToSelect The exact value to type and select in the searchable dropdown
     * @throws RuntimeException if the dropdown, input field, or selected value is not found or incorrect
     */
    public void selectSearchableDropdown(String questionText, String valueToSelect) {
        String containerXpath = "//p[contains(text(),'" + questionText + "')]/following::div[contains(@class,'control')][1]";
        WebElement dropdownContainer;

        try {
            dropdownContainer = wait.waitForElementToBeClickable(By.xpath(containerXpath));
        } catch (TimeoutException e) {
            throw new RuntimeException("Dropdown container not clickable for question: '" + questionText + "'", e);
        }

        try {
            dropdownContainer.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdownContainer);
        } catch (ElementNotInteractableException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdownContainer);
        }


        WebElement inputField = driver.switchTo().activeElement();

        if (inputField == null) {
            throw new RuntimeException("Input field not focused after clicking dropdown for question: '" + questionText + "'");
        }

        inputField.clear();
        inputField.sendKeys(valueToSelect);
        inputField.sendKeys(Keys.ENTER);

        String selectedValueXpath = "//p[contains(text(),'" + questionText + "')]/following::div[contains(@class,'control')][1]//div[contains(@class,'singleValue') or contains(@class,'multiValue')]";

        try {
            WebElement selectedValue = wait.waitForVisibility(By.xpath(selectedValueXpath));
            String actualText = selectedValue.getText().trim();

            if (!actualText.equalsIgnoreCase(valueToSelect)) {
                throw new RuntimeException("Dropdown selection mismatch for '" + questionText + "'. Expected: '" + valueToSelect + "', but found: '" + actualText + "'");
            }
        } catch (TimeoutException e) {
            throw new RuntimeException("Selected value not visible after selecting: '" + valueToSelect + "'", e);
        }
    }


    /**
     * Enters the company name in the input field.
     *
     * @param companyName The name of the company to be entered.
     */
    public void enterCompanyName(String companyName) {
        try {
            WebElement input = wait.waitForVisibility(By.xpath("//input[@type='text'][@placeholder='Enter your company name']"));
            input.clear();
            input.sendKeys(companyName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter company name: " + companyName, e);
        }
    }

    /**
     * Selects a business type from the searchable dropdown.
     *
     * @param type The business type to select.
     */
    public void selectBusinessType(String type) {
        selectSearchableDropdown("What is the type of your business?", type);
    }

    /**
     * Selects a state from the searchable dropdown.
     *
     * @param state The state to select.
     */
    public void selectState(String state) {
        selectSearchableDropdown("Where is your business located?", state);
    }

    /**
     * Selects the employee size from the dropdown.
     *
     * @param size The size to select.
     */
    public void selectEmployeeSize(String size) {
        selectSearchableDropdown("How many employees do you have?", size);
    }

    /**
     * Selects the industry type from the dropdown.
     *
     * @param industry The industry to select.
     */
    public void selectIndustryType(String industry) {
        selectSearchableDropdown("Which industry does your business fall under?", industry);
    }

    /**
     * Selects the annual turnover from the dropdown.
     *
     * @param turnover The turnover value to select.
     */
    public void selectAnnualTurnover(String turnover) {
        selectSearchableDropdown("What is your annual turnover?", turnover);
    }

    /**
     * Clicks the "Get Started" button and waits for background processing to complete.
     */
    public void clickGetStarted() {
        lastGetStartedClickNanos = System.nanoTime();
        logger.info("🖱️ Clicking 'Get Started'… (start timestamp captured)");

        try {
            By primary = By.xpath("(//button[normalize-space()='Get Started'])[2]");
            By fallback = By.xpath("//*[self::button or self::a or @role='button']" + "[translate(normalize-space(.),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')='GET STARTED']");

            WebElement button;
            try {
                button = wait.waitForElementToBeClickable(primary);
            } catch (Exception ignored) {
                button = wait.waitForElementToBeClickable(fallback);
            }
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", button);
                Thread.sleep(120);
            } catch (Exception ignored) {
            }

            // Robust click using your helper; retry if stale
            try {
                commonMethods.safeClick(driver, button, "Get Started", 10);
            } catch (StaleElementReferenceException se) {
                logger.warn("♻️ Button went stale during click, re-locating and retrying…");
                // re-find using fallback logic
                WebElement fresh = wait.waitForElementToBeClickable(primary); // try primary again
                try {
                    commonMethods.safeClick(driver, fresh, "Get Started (retry)", 10);
                } catch (Exception inner) {
                    // final fallback
                    WebElement freshFallback = wait.waitForElementToBeClickable(fallback);
                    commonMethods.safeClick(driver, freshFallback, "Get Started (fallback retry)", 10);
                }
            }

            try {
                wait.waitForInvisibility(button);
            } catch (Exception ignored) {
            }

            logger.info("✅ 'Get Started' clicked. Measuring time until Mandatory Compliance count appears…");

        } catch (Exception e) {
            throw new RuntimeException("Failed to click 'Get Started' button or post-click stabilization", e);
        }
    }

    public double getLastComplianceLoadSeconds() {
        return lastComplianceLoadSeconds;
    }

    public String getLastComplianceLoadFormatted() {
        return lastComplianceLoadFormatted;
    }

    public boolean isLastComplianceOverOneMinute() {
        return lastComplianceOverOneMinute;
    }


    public int getMandatoryComplianceCount() {
//        By countLocator = By.xpath("//p[contains(@class,'text-[#284B6A]') and contains(@class,'leading-[60px]')]");

        By countLocator = By.xpath("//div[contains(@class,'bg-[#E8F3FE]')]//div[contains(@class,'font-bold')]");

        long startNs = (lastGetStartedClickNanos > 0) ? lastGetStartedClickNanos : System.nanoTime();

        try {
            Thread.sleep(2000);
            WebElement countElement = wait.waitForVisibilityCustomTimeOut(countLocator, Duration.ofSeconds(60));
            long endNs = System.nanoTime();

            String countText = countElement.getText().trim();
            int count = Integer.parseInt(countText);
            double secs = (endNs - startNs) / 1_000_000_000.0;
            lastComplianceLoadSeconds = secs;
            long mins = (long) secs / 60;
            double secPart = secs % 60;
            lastComplianceLoadFormatted = String.format("%d min %.2f sec", mins, secPart);
            lastComplianceOverOneMinute = secs > 60.0;

            lastGetStartedClickNanos = -1L;

            logger.info("⏱️ Time taken to create compliances: {} (raw {} sec). Count = {}", lastComplianceLoadFormatted, String.format("%.2f", secs), count);
            if (lastComplianceOverOneMinute) {
                logger.warn("⚠️ Compliance creation took > 1 minute ({} sec)", String.format("%.2f", secs));
            }

            return count;

        } catch (NumberFormatException e) {
            throw new RuntimeException("Compliance count is not a valid number.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve compliance count.", e);
        }
    }


    /**
     * Clicks the "View my compliances" button.
     */
    public void clickViewComplianceButton() {
        By buttonLocator = By.xpath("//p[normalize-space()='View My Compliances']");
        try {
            WebElement button = wait.waitForElementToBeClickable(buttonLocator);
            button.click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to click 'View my compliances' button.", e);
        }
    }

    /**
     * Checks if the compliance popup is visible (e.g., Got it button present).
     *
     * @return true if popup is visible, false otherwise.
     */
    public boolean isCompliancePopupVisible() {
        try {
            return driver.findElement(By.xpath("//button[normalize-space()='Got it']")).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Clicks the "Got it" button on the compliance popup.
     */
    public void clickGotItButton() {
        By gotItLocator = By.xpath("//button[normalize-space()='Got it']");

        try {
            WebElement gotItButton = wait.waitForVisibilityCustomTimeOut(gotItLocator, Duration.ofSeconds(40));
            Thread.sleep(500);
            gotItButton.click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to click 'Got it' button.", e);
        }
    }

    /**
     * Closes the guidance popup by clicking the close icon.
     */
    public void closeGuidancePopup() {
        By closeIcon = By.xpath("//div[@class='flex gap-4 items-center justify-end min-w-max h-fit']");
        try {
            WebElement closeBtn = wait.waitForElementToBeClickable(closeIcon);
            closeBtn.click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to close the guidance popup.", e);
        }
    }




    private List<WebElement> waitForPresent(By by, long timeoutMs) {
        long end = System.currentTimeMillis() + timeoutMs;
        List<WebElement> els;
        do {
            els = driver.findElements(by);
            if (els != null && !els.isEmpty()) return els;
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        } while (System.currentTimeMillis() < end);
        return Collections.emptyList();
    }

    private boolean isActuallyVisible(WebElement el) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});", el);
            try { Thread.sleep(120); } catch (InterruptedException ignored) {}
            Rectangle r = el.getRect();
            return el.isDisplayed() && r != null && r.getHeight() > 0 && r.getWidth() > 0;
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }

    private boolean anyVisible(By by, long timeoutMs) {
        List<WebElement> candidates = waitForPresent(by, timeoutMs);
        if (candidates.isEmpty()) return false;

        // try each candidate; skip hidden twins
        for (int i = 0; i < candidates.size(); i++) {
            WebElement el = candidates.get(i);
            if (isActuallyVisible(el)) return true;
            try {
                List<WebElement> retry = driver.findElements(by);
                if (i < retry.size() && isActuallyVisible(retry.get(i))) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    public boolean isTalkToLawyerVisible() {
        final By by = By.xpath("//p[normalize-space()='Talk to a Lawyer']");
        try {
            if (anyVisible(by, 10_000L)) return true;
            System.out.println("❌ Matched 'Talk to a Lawyer' but no visible candidate.");
            return false;
        } catch (Exception e) {
            System.out.println("❌ Exception in isTalkToLawyerVisible: " + e.getMessage());
            return false;
        }
    }

    public boolean isTalkToCAVisible() {
        final By by = By.xpath("//p[normalize-space()='Talk to CA']");
        try {
            if (anyVisible(by, 10_000L)) return true;
            System.out.println("❌ Matched 'Talk to CA' but no visible candidate.");
            return false;
        } catch (Exception e) {
            System.out.println("❌ Exception in isTalkToCAVisible: " + e.getMessage());
            return false;
        }
    }



    /**
     * Validates the presence of expected risk categories in the "Risk Based Compliances" section
     * and returns a map containing both the missing and found categories.
     *
     * @param expectedCategories List of risk categories expected to be displayed (e.g., High, Medium, Low)
     * @return Map with keys:
     * - "missing": List of categories not found or not visible on the UI
     * - "found": List of categories successfully located and visible
     * @author Sherwin
     * @since 10-07-2025
     */
    public Map<String, List<String>> validateAndFetchRiskCategories(List<String> expectedCategories) {
        List<String> missing = new ArrayList<>();
        List<String> found = new ArrayList<>();

        for (String category : expectedCategories) {
            String xpath = "//p[text()='Risk Based Compliances']/ancestor::div[contains(@class,'flex')][1]" + "//following-sibling::div//div[contains(@class,'items-center')]//p[normalize-space()='" + category + "']";

            List<WebElement> elements = driver.findElements(By.xpath(xpath));
            boolean isVisible = elements.stream().anyMatch(WebElement::isDisplayed);

            if (isVisible) {
                found.add(category);
            } else {
                missing.add(category);
            }
        }

        Map<String, List<String>> result = new HashMap<>();
        result.put("missing", missing);
        result.put("found", found);
        return result;
    }


    /**
     * Fetches the background color (in RGBA format) of the color blocks for each given risk category
     * displayed under the "Risk Based Compliances" section on the dashboard.
     *
     * <p>This method uses strict XPath and JavaScript execution to retrieve the actual computed
     * background color for visible color indicator elements. It ensures proper visibility checks
     * and returns descriptive statuses for missing or hidden elements.</p>
     *
     * @param categories List of risk category names (e.g., "High", "Medium", "Low")
     * @return A map where the key is the category name and the value is:
     * - the RGBA color string (if found and visible),
     * - "Not Visible" if the element exists but is hidden,
     * - "Not Found" if the element is missing.
     */

    public Map<String, String> getRiskCategoryColors(List<String> categories) {
        Map<String, String> colorMap = new LinkedHashMap<>();

        for (String category : categories) {
            try {
                String xpath = "(//p[text()='Risk Based Compliances']/ancestor::div[contains(@class,'flex')][1]" + "//following-sibling::div//div[contains(@class,'items-center') and .//p[normalize-space()='" + category + "']][1]" + "//div[contains(@class,'bg-')])[1]";

                WebElement colorBlock = driver.findElement(By.xpath(xpath));

                if (colorBlock.isDisplayed()) {
                    String rgba = (String) ((JavascriptExecutor) driver).executeScript("return window.getComputedStyle(arguments[0]).getPropertyValue('background-color');", colorBlock);
                    colorMap.put(category, rgba);
                } else {
                    colorMap.put(category, "Not Visible");
                }

            } catch (NoSuchElementException e) {
                colorMap.put(category, "Not Found");
            }
        }

        return colorMap;
    }

    /**
     * Gets the count of 'High Risk' compliances.
     **/
    public int getHighRiskCount() {
        return getCountByXPath("High Risk", ("//p[text()='High']/parent::div/following-sibling::p"));
    }

    /**
     * Gets the count of 'Medium Risk' compliances.
     **/
    public int getMediumRiskCount() {
        return getCountByXPath("Medium Risk", ("//p[text()='Medium']/parent::div/following-sibling::p"));
    }

    /**
     * Gets the count of 'Low Risk' compliances.
     **/
    public int getLowRiskCount() {
        return getCountByXPath("Low Risk", ("//p[text()='Low']/parent::div/following-sibling::p"));
    }


    /**
     * Clicks on the 'High' risk category from the Home page.
     */
    public void clickHighCategory() {
        if (highCategoryElement == null || !highCategoryElement.isDisplayed() || !highCategoryElement.isEnabled()) {
            throw new IllegalStateException("❌ 'High' category element is not visible or not enabled.");
        }
        highCategoryElement.click();
    }

    /**
     * Clicks on the 'Medium' risk category from the Home page.
     */
    public void clickMediumCategory() {
        if (mediumCategoryElement == null || !mediumCategoryElement.isDisplayed() || !mediumCategoryElement.isEnabled()) {
            throw new IllegalStateException("❌ 'Medium' category element is not visible or not enabled.");
        }
        mediumCategoryElement.click();
    }

    /**
     * Clicks on the 'Low' risk category from the Home page.
     */
    public void clickLowCategory() {
        if (lowCategoryElement == null || !lowCategoryElement.isDisplayed() || !lowCategoryElement.isEnabled()) {
            throw new IllegalStateException("❌ 'Low' category element is not visible or not enabled.");
        }
        lowCategoryElement.click();
    }


    /**
     * Returns the WebElement corresponding to the specified risk category.
     *
     * @param category The risk category name (High, Medium, or Low)
     * @return WebElement of the specified category
     */
    public WebElement getRiskCategoryElement(String category) {
        switch (category.toLowerCase()) {
            case "high":
                return driver.findElement(By.xpath("//p[text()='High']/parent::div"));
            case "medium":
                return driver.findElement(By.xpath("//p[text()='Medium']/parent::div"));
            case "low":
                return driver.findElement(By.xpath("//p[text()='Low']/parent::div"));
            default:
                throw new IllegalArgumentException("Invalid risk category: " + category);
        }
    }

    /**
     * Hovers over the specified risk category element using Selenium actions.
     *
     * @param category The risk category name (High, Medium, or Low)
     */
    public void hoverOverRiskCategory(String category) {
        if (category == null || category.isEmpty()) {
            throw new IllegalArgumentException("Risk category cannot be null or empty.");
        }

        if (!(category.equalsIgnoreCase("High") || category.equalsIgnoreCase("Medium") || category.equalsIgnoreCase("Low"))) {
            throw new IllegalArgumentException("Invalid risk category provided: " + category);
        }

        WebElement element = getRiskCategoryElement(category);

        if (element == null) {
            throw new NoSuchElementException("Risk category element not found for: " + category);
        }

        if (!element.isDisplayed()) {
            throw new IllegalStateException("Risk category element is not displayed: " + category);
        }

        try {
            helpers.hover(driver, element);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hover over risk category element: " + category, e);
        }

    }


    /**
     * Fetches the column headers from the Compliance Report table.
     *
     * <p>This method scrolls to the Compliance Report table and extracts all
     * column headers using their XPath. It validates that the headers are present,
     * non-null, and non-empty, before returning them as a list of strings.</p>
     *
     * @return a list of column header texts from the Compliance Report table
     * @throws RuntimeException if no headers are found, if any header element is null or empty,
     *                          or if any other error occurs while fetching the headers
     */

    public List<String> getComplianceReportColumnHeaders() {
        try {
            helpers.scrollToElement(driver, complianceReportTable);

            List<WebElement> headerElements = driver.findElements(By.xpath("//tr[contains(@class,'bg-[#F7F8F9]') and contains(@class,'rounded-md')]//th"));

            if (headerElements == null || headerElements.isEmpty()) {
                throw new IllegalStateException("No header elements found in Compliance Report table.");
            }

            List<String> columnHeaders = new ArrayList<>();
            for (WebElement headerElement : headerElements) {
                if (headerElement == null) {
                    throw new IllegalStateException("Found a null WebElement while reading Compliance Report column headers.");
                }
                String headerText = headerElement.getText().trim();
                if (headerText.isEmpty()) {
                    throw new IllegalStateException("Empty header text found in Compliance Report table.");
                }
                columnHeaders.add(headerText);
            }

            return columnHeaders;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Compliance Report column headers: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches the column headers from the Compliance Page table.
     *
     * <p>This method locates the headers of the Compliance Page table by its table ID
     * and extracts all header texts. It validates that the headers are present,
     * non-null, and non-empty, before returning them as a list of strings.</p>
     *
     * @return a list of column header texts from the Compliance Page table
     * @throws RuntimeException if no headers are found, if any header element is null or empty,
     *                          or if any other error occurs while fetching the headers
     */
    public List<String> getCompliancePageColumnHeaders() {
        try {
            List<WebElement> headerElements = driver.findElements(By.xpath("//table[@id='compliances-table']//tr[contains(@class,'border-b-[1px]')]//th"));

            if (headerElements == null || headerElements.isEmpty()) {
                throw new IllegalStateException("No header elements found in Compliance Page table.");
            }

            List<String> compliancePageHeaders = new ArrayList<>();
            for (WebElement headerElement : headerElements) {
                if (headerElement == null) {
                    throw new IllegalStateException("Found a null WebElement while reading Compliance Page column headers.");
                }
                String headerText = headerElement.getText().trim();
                if (headerText.isEmpty()) {
                    throw new IllegalStateException("Empty header text found in Compliance Page table.");
                }
                compliancePageHeaders.add(headerText);
            }

            return compliancePageHeaders;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Compliance Page column headers: " + e.getMessage(), e);
        }
    }


    /**
     * Scrolls the page to the Compliance Report table section.
     *
     * <p>Uses a helper method to ensure the Compliance Report table is brought into view.</p>
     *
     * @throws RuntimeException if the table cannot be scrolled into view
     */
    public void scrollToComplianceReportSection() {
        try {
            helpers.scrollToElement(driver, complianceReportTable);
            logger.info("✅ Scrolled to Compliance Report table section.");
        } catch (Exception e) {
            logger.error("❌ Failed to scroll to Compliance Report table.", e);
            throw new RuntimeException("❌ Could not scroll to Compliance Report table.", e);
        }
    }


    /**
     * Selects (clicks) the first available record from the Compliance Report section.
     *
     * <p>This method ensures that records are present, visible, and enabled before
     * attempting to click the first one. It scrolls to the element and performs
     * a standard click. If a click is intercepted, it falls back to a JavaScript click.</p>
     *
     * @throws IllegalStateException if no records are found or if the first record
     *                               is not visible or not enabled
     */
    public void selectAnyComplianceReportRecord() {
        if (complianceReportRows == null || complianceReportRows.isEmpty()) {
            throw new IllegalStateException("❌ No records found in Compliance Report section.");
        }

        WebElement firstRecord = complianceReportRows.get(0);

        if (!firstRecord.isDisplayed() || !firstRecord.isEnabled()) {
            throw new IllegalStateException("❌ First record in Compliance Report is not visible or not enabled.");
        }

        // Scroll to the element
        helpers.scrollToElement(driver, firstRecord);

        try {
            wait.waitForElementToBeClickable(firstRecord).click();
            logger.info("✅ Clicked using standard click.");
        } catch (ElementClickInterceptedException e) {
            logger.warn("⚠️ Standard click intercepted. Trying JS click as fallback.");
            helpers.jsClick(driver, firstRecord);
            logger.info("✅ Clicked using JS click as fallback.");
        }
    }


    /**
     * Verifies if the compliance details panel content is visible (header/title check).
     */
    public boolean isComplianceDetailsPanelVisible() {
        try {
            return complianceDetailsTitle != null && complianceDetailsTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the number of rows available in the Compliance Report section.
     */
    public int getComplianceReportRowCount() {
        try {
            if (complianceReportRows == null) {
                logger.warn("⚠️ Compliance report rows list is null.");
                return 0;
            }
            int rowCount = complianceReportRows.size();
            logger.info("📊 Compliance Report Row Count: {}", rowCount);
            return rowCount;
        } catch (Exception e) {
            logger.error("❌ Error while fetching Compliance Report row count", e);
            return 0;
        }
    }


    /**
     * Checks if the month label is visible on the page.
     *
     * @return true if the month label is visible, false if not visible or a TimeoutException occurs.
     */
    public boolean isMonthLabelVisible() {
        try {

            wait.waitForVisibility((monthLabel));

            return monthLabel.isDisplayed();
        } catch (TimeoutException e) {
            System.out.println("Month label not visible: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the year label is visible on the page.
     *
     * @return true if the year label is visible, false if not visible or a TimeoutException occurs.
     */
    public boolean isYearLabelVisible() {
        try {
            wait.waitForVisibility(yearLabel);
            return yearLabel.isDisplayed();
        } catch (TimeoutException e) {
            System.out.println("Year label not visible: " + e.getMessage());
            return false;
        }
    }


    /**
     * Returns the currently selected Month from the dropdown.
     *
     * @return Selected month as text (e.g., "July")
     * @throws RuntimeException if the month is not visible or is empty
     */
    public String getSelectedMonth() {
        try {
            wait.waitForVisibility(selectedMonth);
            String month = selectedMonth.getText().trim();

            if (month.isEmpty()) {
                throw new RuntimeException("❌ Selected Month is empty.");
            }

            logger.info("📅 Selected Month: {}", month);
            return month;

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to retrieve selected Month from the dropdown.", e);
        }
    }


    /**
     * Returns the currently selected Year from the dropdown.
     *
     * @return Selected year as text (e.g., "2025")
     * @throws RuntimeException if the year is not visible or is empty
     */
    public String getSelectedYear() {
        try {
            wait.waitForVisibility(selectedYear);
            String year = selectedYear.getText().trim();

            if (year.isEmpty()) {
                throw new RuntimeException("❌ Selected Year is empty.");
            }

            logger.info("📆 Selected Year: {}", year);
            return year;

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to retrieve selected Year from the dropdown.", e);
        }
    }

    /**
     * Selects a specific month from the Month dropdown with validation.
     *
     * @param monthToSelect the visible text of the month to select (e.g., "July").
     * @throws RuntimeException if dropdown or desired option is not interactable or not visible.
     */
    public void selectMonthFromDropdown(String monthToSelect) {
        try {
            wait.waitForVisibility(monthDropdown);
            if (!monthDropdown.isEnabled()) {
                throw new RuntimeException("Month dropdown is disabled.");
            }
            monthDropdown.click();

            By monthOptionLocator = By.xpath("//div[contains(@class,'option') and normalize-space(text())='" + monthToSelect + "']");
            WebElement monthOption = wait.waitForVisibility(monthOptionLocator);
            if (!monthOption.isDisplayed()) {
                throw new RuntimeException("Month option '" + monthToSelect + "' is not visible in dropdown.");
            }
            monthOption.click();

            // Optional: Add post-selection validation if UI updates header
        } catch (Exception e) {
            throw new RuntimeException("Failed to select month '" + monthToSelect + "': " + e.getMessage(), e);
        }
    }


    /**
     * Selects a specific year from the Year dropdown with validation.
     *
     * @param yearToSelect the visible text of the year to select (e.g., "2025").
     * @throws RuntimeException if dropdown or desired option is not interactable or not visible.
     */

    public void selectYearFromDropdown(String yearToSelect) {
        try {
            wait.waitForVisibility(yearDropdown);
            if (!yearDropdown.isEnabled()) {
                throw new RuntimeException("Year dropdown is disabled.");
            }
            yearDropdown.click();

            By yearOptionLocator = By.xpath("//div[contains(@class,'option') and normalize-space(text())='" + yearToSelect + "']");
            WebElement yearOption = wait.waitForVisibility(yearOptionLocator);

            if (!yearOption.isDisplayed()) {
                throw new RuntimeException("Year option '" + yearToSelect + "' is not visible in dropdown.");
            }
            yearOption.click();

            logger.info("🔒 Year '{}' selected from dropdown.", yearToSelect);

        } catch (Exception e) {
            throw new RuntimeException("Failed to select year '" + yearToSelect + "': " + e.getMessage(), e);
        }
    }


    /**
     * Retrieves the current month name in full textual format (e.g., "July").
     *
     * @return the full name of the current month in English.
     */
    public String getCurrentMonthName() {
        return LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    /**
     * Retrieves the current year as a string (e.g., "2025").
     *
     * @return the current year in string format.
     */
    public String getCurrentYear() {
        return String.valueOf(LocalDate.now().getYear());
    }


    /**
     * Retrieves all due dates listed in the "monthly-compliance-list" container,
     * scrolling if needed to load all content.
     *
     * <p>This method checks for scrollability, loads all items if required,
     * and returns due dates in the format "dd MMM yyyy".</p>
     *
     * @return a list of due date strings (e.g., "11 Jul 2025"). Returns an empty list if no records are present.
     * @throws RuntimeException if the scroll container is not found or scrolling fails unexpectedly.
     */

    public List<String> getAllDueDatesSmart() {
        List<String> dueDates = new ArrayList<String>();

        try {
            if (scrollContainer == null || !scrollContainer.isDisplayed()) {
                throw new RuntimeException("Scroll container 'monthly-compliance-list' is not present or not visible.");
            }

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Scroll if the container is scrollable
            Boolean hasScroll = (Boolean) js.executeScript("return arguments[0].scrollHeight > arguments[0].clientHeight;", scrollContainer);

            if (Boolean.TRUE.equals(hasScroll)) {
                long lastHeight = ((Number) js.executeScript("return arguments[0].scrollHeight;", scrollContainer)).longValue();
                while (true) {
                    js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", scrollContainer);
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }

                    long newHeight = ((Number) js.executeScript("return arguments[0].scrollHeight;", scrollContainer)).longValue();
                    if (newHeight == lastHeight) {
                        break;
                    }
                    lastHeight = newHeight;
                }
            }

            // ✅ Use node string-value (.) instead of text() so nested spans are matched too
            List<WebElement> dueDateElements = scrollContainer.findElements(By.xpath(".//p[contains(normalize-space(.),'Due date')]"));

            for (int i = 0; i < dueDateElements.size(); i++) {
                WebElement element = dueDateElements.get(i);
                String text = element.getText();
                if (text != null) {
                    // normalize internal whitespace once
                    text = text.trim().replaceAll("\\s+", " ");
                    int idx = text.indexOf(':');
                    if (idx >= 0 && idx + 1 < text.length()) {
                        String actualDate = text.substring(idx + 1).trim();
                        if (actualDate.length() > 0) {
                            dueDates.add(actualDate);
                        }
                    }
                }
            }

        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread was interrupted during scrolling.", e);
            }
            throw new RuntimeException("Failed to retrieve due dates: " + e.getMessage(), e);
        }

        return dueDates;
    }


    /**
     * Waits (up to ~10s) for the compliance list to reflect the newly selected Month/Year.
     * Success criteria:
     * • The list becomes empty (no "Due date" rows), OR
     * • At least one "Due date" row contains the target month abbreviation and the target year.
     * <p>
     * Notes:
     * • Uses simple polling (no Java 8, no new WebDriverWait).
     * • Works with @FindBy. We query rows inside the container each poll to avoid stale lists.
     *
     * @param fullMonth The month label shown in the dropdown (e.g., "August")
     * @param year      The year string shown in the dropdown (e.g., "2025")
     */
    public void waitForMonthYearToApply(String fullMonth, String year) {
        final String monthAbbr = TestDataGenerator.getMonthAbbreviation(fullMonth);
        final By DUE_ROWS_IN_CONTAINER = By.xpath(".//p[contains(normalize-space(.),'Due date')]");

        // Ensure container is (best-effort) visible first
        try {
            wait.waitForVisibility(monthlyList);
        } catch (Exception ignore) {
            // It's okay if this throws; the polling below will keep checking.
        }

        final long timeoutMs = 10_000L; // 10 seconds max
        final long pollMs = 250L;
        final long deadline = System.currentTimeMillis() + timeoutMs;

        while (System.currentTimeMillis() < deadline) {
            try {
                // Find current rows under the container each poll (avoids stale elements)
                List<WebElement> rows = monthlyList.findElements(DUE_ROWS_IN_CONTAINER);

                // If no items, consider the filter applied but with zero results
                if (rows == null || rows.isEmpty()) {
                    return;
                }

                // Check any visible row text for month abbreviation and year
                boolean matchFound = false;
                for (int i = 0; i < rows.size(); i++) {
                    WebElement row = rows.get(i);
                    try {
                        if (row != null && row.isDisplayed()) {
                            String txt = row.getText();
                            if (txt != null && txt.indexOf(monthAbbr) >= 0 && txt.indexOf(year) >= 0) {
                                matchFound = true;
                                break;
                            }
                        }
                    } catch (Exception ignore) {
                        // keep polling
                    }
                }

                if (matchFound) {
                    return; // state reflects the selected month/year
                }

            } catch (Exception ignore) {
                // keep polling
            }

            try {
                Thread.sleep(pollMs);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Soft warn only; your later validation will still assert exact matches
        logger.warn("⏳ Timed out waiting for Month/Year to apply ({} / {}). Continuing.", monthAbbr, year);
    }


    /**
     * Clicks a random visible compliance record from the list and returns its text.
     * Handles cases where only one record is present or visible.
     *
     * @return the trimmed text of the randomly selected compliance record,
     * or null if no visible records are found.
     * @throws RuntimeException if the selected element is not interactable or has no text.
     */
    public String clickRandomComplianceRecord() {
        if (complianceCalendarRecords == null || complianceCalendarRecords.isEmpty()) {
            System.out.println("No compliance records available to click.");
            return null;
        }

        List<WebElement> visibleRecords = new ArrayList<WebElement>();
        for (int i = 0; i < complianceCalendarRecords.size(); i++) {
            WebElement record = complianceCalendarRecords.get(i);
            if (record != null && record.isDisplayed()) {
                visibleRecords.add(record);
            }
        }

        if (visibleRecords.isEmpty()) {
            System.out.println("No visible compliance records found to click.");
            return null;
        }

        WebElement selectedElement;
        if (visibleRecords.size() == 1) {
            selectedElement = visibleRecords.get(0);
        } else {
            selectedElement = visibleRecords.get(new Random().nextInt(visibleRecords.size()));
        }

        try {
            String recordText = selectedElement.getText().trim();
            if (recordText.isEmpty()) {
                throw new RuntimeException("Selected compliance record has no visible text.");
            }

            // ⬇️ use your safeClick instead of raw click
            commonMethods.safeClick(driver, selectedElement, "Compliance record: " + recordText, 8);
            return recordText;

        } catch (Exception e) {
            throw new RuntimeException("Failed to click compliance record: " + e.getMessage(), e);
        }
    }


    /**
     * Retrieves the due date text from the right-side panel after a compliance record is clicked.
     *
     * @return the trimmed due date text (e.g., "11 Jul 2025").
     * @throws RuntimeException if the element is not visible or contains no text.
     */
    public String getRightPanelDueDate() {
        try {
            wait.waitForVisibility(rightPanelDueDate);

            if (!rightPanelDueDate.isDisplayed()) {
                throw new RuntimeException("Right panel due date element is not visible.");
            }

            String dueDateText = rightPanelDueDate.getText().trim();
            if (dueDateText.isEmpty()) {
                throw new RuntimeException("Right panel due date text is empty.");
            }

            return dueDateText;

        } catch (TimeoutException e) {
            throw new RuntimeException("Timed out waiting for the due date element to be visible.", e);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving right panel due date: " + e.getMessage(), e);
        }
    }


}


