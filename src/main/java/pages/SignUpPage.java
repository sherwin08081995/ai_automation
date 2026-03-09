package pages;

import base.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;
import com.github.javafaker.Faker;
import utils.TestDataGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * @author Sherwin
 * @since 19-11-2025
 */
public class SignUpPage extends BasePage {

    private final Faker faker = new Faker();
    // --- Back navigation debug state (no Allure here, just data + logs) ---
    private boolean lastStillConfusedPopupVisible = false;
    private boolean lastStillConfusedLeadSubmitted = false;
    private boolean lastPaymentPageReached = false;
    private final List<String> lastBackNavigationUrls = new ArrayList<>();

    /**
     * Reset debug state before a new navigate-back flow.
     */
    public void resetBackNavigationDebugState() {
        lastStillConfusedPopupVisible = false;
        lastStillConfusedLeadSubmitted = false;
        lastPaymentPageReached = false;
        lastBackNavigationUrls.clear();
    }

    /**
     * Expose debug info to step definitions (read-only).
     */
    public boolean wasLastStillConfusedPopupVisible() {
        return lastStillConfusedPopupVisible;
    }

    public boolean wasLastStillConfusedLeadSubmitted() {
        return lastStillConfusedLeadSubmitted;
    }

    public boolean wasLastPaymentPageReached() {
        return lastPaymentPageReached;
    }

    public List<String> getLastBackNavigationUrls() {
        return new ArrayList<>(lastBackNavigationUrls);
    }


    public static class CompanyDetailsData {
        private String variant;      // Company / Trade / Firm / Society
        private String name;         // Company / Trade / Firm / Society Name
        private String dateValue;    // DOI / Business Start / Registration
        private String state;
        private String industry;


        public CompanyDetailsData(String variant, String name, String dateValue, String state, String industry) {
            this.variant = variant;
            this.name = name;
            this.dateValue = dateValue;
            this.state = state;
            this.industry = industry;
        }

        public String getVariant() {
            return variant;
        }

        public String getName() {
            return name;
        }

        public String getDateValue() {
            return dateValue;
        }

        public String getState() {
            return state;
        }

        public String getIndustry() {
            return industry;
        }
    }

    /**
     * Constructor initializes the WebDriver and invokes BasePage.
     *
     * @param driver active WebDriver instance
     */
    public SignUpPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//h1[normalize-space()='Create an account' or normalize-space()='Create Account']")
    private WebElement createAccountHeader;

    @FindBy(xpath = "//h1[contains(normalize-space(.),'Create')]")
    private WebElement createAccountHeaderFallback;

    @FindBy(xpath = "(//div[contains(@class,'bg-white') and contains(@class,'shadow')])[3]")
    private WebElement signUpContainer;

    @FindBy(xpath = "//a[normalize-space()='Sign Up' or normalize-space()='Signup']")
    private WebElement signUpLinkOnLogin;

    @FindBy(xpath = "//div[contains(@class,'bg-white') and contains(@class,'shadow')]//label")
    private List<WebElement> fieldLabels;

    @FindBy(xpath = "//input[@id='name' or @name='name']")
    private WebElement fullNameInput;

    @FindBy(xpath = "//input[@type='email' or @name='email']")
    private WebElement emailInput;

    @FindBy(xpath = "//label[normalize-space()='Phone number']/ancestor::div[@class='relative']//input")
    private WebElement phoneField;

    @FindBy(xpath = "//input[@type='password' and not(contains(@name,'confirm'))]")
    private WebElement passwordInput;

    @FindBy(xpath = "//input[@type='password' and contains(@name,'confirm')]")
    private WebElement confirmPasswordInput;

    @FindBy(xpath = "//button[normalize-space()='Sign Up']")
    private WebElement signUpButton;

    @FindBy(xpath = "//div[contains(@class,'toast') or contains(text(),'success') or contains(text(),'Account created')]")
    private WebElement successMessageToast;

    @FindBy(xpath = "//h1[normalize-space()='Log into your account']")
    private WebElement loginHeader;

    @FindBy(xpath = "//p[normalize-space()='Login with Password']")
    private WebElement loginWithPasswordLink;

    @FindBy(xpath = "//label[normalize-space()='Email address']" + "/ancestor::div[contains(@class,'relative')]//input")
    private WebElement loginEmailInput;

    @FindBy(xpath = "//input[@type='password']")
    private WebElement loginPasswordInput;

    @FindBy(xpath = "//button[.//p[normalize-space()='Log In'] or normalize-space()='Log In']")
    private WebElement loginButton;

    // Post-login offer popup title
    @FindBy(xpath = "//p[contains(normalize-space(),'Complete Your Profile') " + "and contains(normalize-space(),'Unlock Special Offer')]")
    private WebElement completeProfilePopupTitle;

    // Post-login popup CTA button
    @FindBy(xpath = "//button[.//p[contains(normalize-space(),'Complete Business Profile') " + "and contains(normalize-space(),'Claim Offer')]]")
    private WebElement completeProfilePopupButton;

    @FindBy(xpath = "//h1[normalize-space()='Log into your account']")
    private WebElement loginSubtitleText;


    @FindBy(xpath = "(//label[contains(normalize-space(),'How do you plan to use Zolvit 360')])[2]")
    private WebElement welcomeUsageQuestionLabel;

    @FindBy(xpath = "(//p[contains(normalize-space(),'Business needs')])[2]")
    private WebElement businessNeedsOption;

    @FindBy(xpath = "(//p[contains(normalize-space(),'Personal needs')])[2]")
    private WebElement personalNeedsOption;

    @FindBy(xpath = "(//button[.//p[normalize-space()='Skip to dashboard'] or normalize-space()='Skip to dashboard'])[3]")
    private WebElement skipToDashboardButton;

    @FindBy(xpath = "//p[contains(normalize-space(),'Extra 10% Off') and contains(normalize-space(),'Festive Sale')]")
    private WebElement festiveOfferTitle;

    @FindBy(xpath = "//div[contains(@class,'modal') or contains(@class,'styles_modal')]" + "[.//p[contains(normalize-space(),'Extra 10% Off')]]")
    private WebElement festiveOfferContainer;

    @FindBy(xpath = "//button[.//p[contains(normalize-space(),'Explore Service Hub')] " + "or normalize-space()='Explore Service Hub']")
    private WebElement exploreServiceHubButton;

    @FindBy(xpath = "//h1[normalize-space()='Service Hub']")
    private WebElement serviceHubHeader;

    @FindBy(xpath = "//div[contains(@class,'modal') or contains(@class,'styles_modal')]" + "[.//p[contains(normalize-space(),'Complete Your Profile')]]")
    private WebElement completeProfilePopupContainer;

    @FindBy(xpath = "//button[.//p[normalize-space()='Start Your Business'] " + "or normalize-space()='Start Your Business']")
    private WebElement startYourBusinessCta;

    @FindBy(xpath = "//h1[contains(normalize-space(),'Company Registration Online in India')]")
    private WebElement companyRegistrationHeader;

    @FindBy(xpath = "//p[normalize-space()='x'][@class='absolute top-[-5px] right-2 cursor-pointer text-[26px] font-medium']")
    private WebElement closeStillConfusedPopupIcon;

    @FindBy(xpath = "//a[contains(@href,'/grc/welcome') and normalize-space()='Add Business']")
    private WebElement addBusinessLink;

    @FindBy(xpath = "//div[contains(@class,'get-started-trigger')]")
    private WebElement getStartedWidgetButton;

    @FindBy(xpath = "//p[contains(text(),'Get Started with Zolvit 360')]")
    private WebElement getStartedHeaderTitle;

    @FindBy(xpath = "//p[contains(text(),\"Let's set things up to make your compliance experience smooth and powerful\")]")
    private WebElement getStartedSubText;

    @FindBy(xpath = "(//p[normalize-space()='Continue'])[1]")
    private WebElement getStartedContinueButton;

    @FindBy(xpath = "(//p[contains(text(),'Complete your profile')])[1]")
    private WebElement completeProfileContentText;

    @FindBy(xpath = "//button[contains(@class,'cursor-pointer')]//*[name()='svg']")
    private WebElement completeProfilePopupCloseIcon;

    @FindBy(xpath = "(//p[normalize-space()='Business needs'])[2]")
    private WebElement businessNeedsUsageOption;

    @FindBy(xpath = "(//button[.//p[normalize-space()='Next']])[3]")
    private WebElement welcomeNextButton;

    @FindBy(xpath = "(//label[contains(normalize-space(),'Full Name')])[2]")
    private WebElement onboardingFullNameLabel;

    @FindBy(xpath = "(//div[@class='styles_grcInputField__gkWzp   ']//input[@type='text'])[3]")
    private WebElement onboardingFullNameInput;

    @FindBy(xpath = "(//label[contains(normalize-space(),'Entity Type')])[2]")
    private WebElement entityTypeLabel;

    @FindBy(xpath = "(//label[contains(normalize-space(),'Entity Type')])[2]" + "/following::div[contains(@class,'css-b62m3t-container')][1]")
    private WebElement entityTypeDropdownContainer;

    @FindBy(xpath = "(//label[normalize-space()='CIN'])[2]")
    private WebElement cinLabel;

    @FindBy(xpath = "(//input[@placeholder='Enter your CIN'])[2]")
    private WebElement cinInputField;

    @FindBy(xpath = "(//label[normalize-space()='PAN'])[2]")
    private WebElement panLabel;

    @FindBy(xpath = "(//input[@placeholder='Enter your PAN'])[2]")
    private WebElement panInputField;

    // FCRN label & input (Variant 5 of this onboarding step)
    @FindBy(xpath = "(//label[normalize-space()='FCRN'])[2]")
    private WebElement fcrnLabel;

    @FindBy(xpath = "(//input[@placeholder='Enter your FCRN'])[2]")
    private WebElement fcrnInputField;

    // CIN / Trust / Society / PAN / FCRN / LLPIN step elements
    @FindBy(xpath = "(//label[normalize-space()='LLPIN'])[2]")
    private WebElement llpinLabel;

    @FindBy(xpath = "(//input[@placeholder='Enter your LLPIN'])[2]")
    private WebElement llpinInputField;

    @FindBy(xpath = "(//label[normalize-space()='Trust Registration Number'])[2]")
    private WebElement trustRegNumberLabel;

    @FindBy(xpath = "(//input[@placeholder='Enter your Trust Registration Number'])[2]")
    private WebElement trustRegNumberInputField;

    // Society Registration Number label & input (variant 3 of this step)
    @FindBy(xpath = "(//label[normalize-space()='Society Registration Number'])[2]")
    private WebElement societyRegNumberLabel;

    @FindBy(xpath = "(//input[@placeholder='Enter your Society Registration Number'])[2]")
    private WebElement societyRegNumberInputField;

    @FindBy(xpath = "(//label[contains(normalize-space(),'Company Name')])[2]")
    private WebElement companyNameLabel;

    @FindBy(xpath = "(//input[@placeholder='Enter Company Name'])[2]")
    private WebElement companyNameInput;

    @FindBy(xpath = "(//label[contains(normalize-space(),'Date of Incorporation')])[2]")
    private WebElement doiLabel;

    @FindBy(xpath = "(//input[contains(@placeholder,'Select Date of Incorporation')])[2]")
    private WebElement doiInput;

    @FindBy(xpath = "(//label[contains(normalize-space(),'State')])[2]")
    private WebElement stateLabel;

    @FindBy(xpath = "(//div[contains(@class,'css-b62m3t-container') and starts-with(@id,'State')])[2]")
    private WebElement stateDropdownContainer;

    @FindBy(xpath = "(//label[contains(normalize-space(),'Industry Type')])[2]")
    private WebElement industryTypeLabel;

    @FindBy(xpath = "//div[contains(@class,'css-b62m3t-container') and starts-with(@id,'IndustryType')]")
    private WebElement industryTypeDropdownContainer;

    @FindBy(xpath = "(//label[contains(normalize-space(),'Trade Name')])[2]")
    private WebElement tradeNameLabel;

    @FindBy(xpath = "(//input[@placeholder='Enter Trade Name'])[2]")
    private WebElement tradeNameInput;

    @FindBy(xpath = "(//label[contains(normalize-space(),'Business Start Date')])[2]")
    private WebElement businessStartDateLabel;

    // Assuming first input after the label is the date input
    @FindBy(xpath = "(//label[contains(normalize-space(),'Business Start Date')]/following::input[1])[2]")
    private WebElement businessStartDateInput;

    @FindBy(xpath = "(//label[contains(normalize-space(),'Firm Name')])[2]")
    private WebElement firmNameLabel;

    @FindBy(xpath = "(//input[@placeholder='Enter Firm Name'])[2]")
    private WebElement firmNameInput;

    @FindBy(xpath = "(//label[contains(normalize-space(),'Date of Registration')])[2]")
    private WebElement dateOfRegistrationLabel;

    @FindBy(xpath = "(//input[contains(@placeholder,'Select Date of Registration')])[2]")
    private WebElement dateOfRegistrationInput;

    @FindBy(xpath = "(//label[contains(normalize-space(),'Society Name')])[2]")
    private WebElement societyNameLabel;

    @FindBy(xpath = "(//input[@placeholder='Enter Society Name'])[2]")
    private WebElement societyNameInput;

    @FindBy(xpath = "(//label[contains(normalize-space(),'Trust Name')])[2]")
    private WebElement trustNameLabel;

    @FindBy(xpath = "(//input[@placeholder='Enter Trust Name'])[2]")
    private WebElement trustNameInput;

    // Trust variant – date of registration
    @FindBy(xpath = "(//label[contains(normalize-space(),'Date of Registration')])[2]")
    private WebElement trustDateOfRegistrationLabel;

    @FindBy(xpath = "(//input[contains(@placeholder,'Select Date of Registration')])[2]")
    private WebElement trustDateOfRegistrationInput;


    @FindBy(xpath = "(//label[normalize-space()='Team Size*'])[2]")
    private WebElement teamSizeLabel;

    @FindBy(xpath = "(//div[contains(@class,'css-b62m3t-container')])[2]")
    private WebElement teamSizeDropdownContainer;

    @FindBy(xpath = "(//label[contains(normalize-space(),'Company Name')])[2]")
    private WebElement companyNameEstIndiaLabel;

    @FindBy(xpath = "(//input[@placeholder='Enter Company Name'])[2]")
    private WebElement companyNameEstIndiaInput;

    @FindBy(xpath = "(//label[contains(normalize-space(),'Date of Establishment of Business in India')])[2]")
    private WebElement dateOfEstablishmentLabel;

    @FindBy(xpath = "(//input[contains(@placeholder,'Date of Establishment of Business in India')])[2]")
    private WebElement dateOfEstablishmentInput;

    @FindBy(xpath = "(//label[normalize-space()='Annual Turnover*'])[2]")
    private WebElement annualTurnoverLabel;

    @FindBy(xpath = "(//div[contains(@class,'css-b62m3t-container')])[2]")
    private WebElement annualTurnoverDropdownContainer;

    @FindBy(xpath = "//button[@id='notification-button']")
    private WebElement notificationIcon;

    @FindBy(xpath = "//h2[contains(text(),'Notifications')]")
    private WebElement notificationPopupHeader;

    @FindBy(xpath = "//button[@id='cart-button']")
    private WebElement cartIcon;

    @FindBy(xpath = "//h2[contains(text(),'Cart')]")
    private WebElement cartPopupHeader;

    @FindBy(xpath = "//p[contains(text(),\"No Plan Yet? Let's Make Compliance the Easiest Part of Your Business\")]")
    private WebElement zeroStateHeader;

    @FindBy(xpath = "//p[contains(text(),'Special 10% offer only for new businesses like yours')]")
    private WebElement specialOfferText;

    @FindBy(xpath = "//p[normalize-space()='Explore Plans & Claim Offer']")
    private WebElement explorePlansButton;

    @FindBy(xpath = "(//p[normalize-space()='Analysing your compliance requirements'])[2]")
    private WebElement analysingRequirementsHeading;

    @FindBy(xpath = "(//p[normalize-space()='Overall Progress'])[2]")
    private WebElement overallProgressLabel;

    @FindBy(xpath = "(//p[normalize-space()='Compliances Found'])[2]")
    private WebElement compliancesFoundLabel;


    // ================= New Subscriber Offer POPUP =================

    @FindBy(xpath = "//p[contains(normalize-space(),\"Welcome! Here's your new subscriber offer.\")]")
    private WebElement newSubscriberOfferTitle;

    @FindBy(xpath = "//button[.//p[normalize-space()='Claim My 10% Discount'] or normalize-space()='Claim My 10% Discount']")
    private WebElement claimMyDiscountCta;


// ================= Right Panel – Annual Compliance =================

    @FindBy(xpath = "(//*[normalize-space()='Annual Compliance for Your Business'])[2]")
    private WebElement annualComplianceHeading;

    @FindBy(xpath = "//*[normalize-space()='Why is this mandatory?']")
    private WebElement whyIsThisMandatoryHeading;

    @FindBy(xpath = "//*[normalize-space()='Your Dedicated Account Manager']")
    private WebElement yourDedicatedAccountManagerHeading;

    @FindBy(xpath = "//*[normalize-space()='Additional 10% will be Applied at Checkout']")
    private WebElement additionalTenPercentHeading;


    // Exit survey options (same as before)
    @FindBy(xpath = "//div[@class='styles_optionLists__pGAda flex-col']//div")
    private List<WebElement> exitSurveyOptions;

    // “Thanks for Sharing!” popup
    @FindBy(xpath = "//p[contains(text(),'Thanks for Sharing')]")
    private WebElement thanksPopupTitle;

    // Continue button on exit survey
    @FindBy(xpath = "//p[normalize-space()='Continue']")
    private WebElement continueBtn;


    // 1) First popup: "Get All Your Questions Answered"
    @FindBy(xpath = "//p[contains(text(),'Get All Your Questions Answered')]")
    private WebElement expertQuestionsPopupTitle;

    // 2) CTA inside expert popup: "Request Callback"
    @FindBy(xpath = "(//p[normalize-space()='Request Callback'])[1]")
    private WebElement requestCallbackBtn;

    // 3) Final popup: "Request Received"
    @FindBy(xpath = "//p[contains(text(),'Request Received')]")
    private WebElement requestReceivedTitle;


    /**
     * Title text: "Still Confused?"
     */
    @FindBy(xpath = "//p[contains(normalize-space(),'Still Confused')]")
    private WebElement stillConfusedTitle;

    /**
     * Outer container of the popup
     */
    @FindBy(xpath = "(//div[.//p[contains(normalize-space(),'Still Confused')]])[6]")
    private WebElement stillConfusedContainer;

    /**
     * Email input inside the popup
     */
    @FindBy(xpath = "(//input[@id='email'])[2]")
    private WebElement stillConfusedEmailInput;

    /**
     * Mobile Number input inside the popup
     */
    @FindBy(xpath = "(//input[@id='phone'])[2]")
    private WebElement stillConfusedMobileInput;

    /**
     * City / Pincode input inside the popup
     */
    @FindBy(xpath = "(//label[normalize-space()='City/Pincode'])[2]/parent::div//input")
    private WebElement stillConfusedCityPincodeInput;

    /**
     * CTA button: "Talk to registration expert"
     */
    @FindBy(xpath = "//button[contains(normalize-space(),'Talk to registration expert')]")
    private WebElement talkToRegistrationExpertBtn;

    /**
     * "Choose Payment Method" heading on payment page
     */
    @FindBy(xpath = "//h1[contains(normalize-space(),'Choose Payment Method')]")
    private WebElement choosePaymentMethodHeading;


    // Count inside the “Compliances Found” card
    private static final By COMPLIANCES_FOUND_COUNT = By.xpath("(//p[normalize-space()='Compliances Found']/following::div[contains(@class,'font-bold')][1])[2]");

    private static final By VIEW_MY_COMPLIANCES_BTN = By.xpath("(//p[normalize-space()='View my Compliances']/ancestor::button[1])[2]");

    private static final By TEAM_SIZE_INPUT = By.xpath("(//input[starts-with(@id,'react-select') and @type='text'])[2]");

    private static final By TEAM_SIZE_NEXT_CTA = By.xpath("(//p[normalize-space()='Next'])[3]");

    private static final By ANNUAL_TURNOVER_INPUT = By.xpath("(//input[starts-with(@id,'react-select') and @type='text'])[2]");

    private static final By GET_STARTED_CTA = By.xpath("(//p[normalize-space()='Get Started'])[2]");

    private static final By CIN_SKIP_AND_NEXT_CTA = By.xpath("(//p[normalize-space()='Skip and Next'])[2]");

    private static final By STATE_INPUT = By.xpath("(//label[contains(normalize-space(),'State')]/following::input[starts-with(@id,'react-select')][1])[2]");

    private static final By INDUSTRY_TYPE_INPUT = By.xpath("(//label[contains(normalize-space(),'Industry Type')]/following::input[starts-with(@id,'react-select')][1])[2]");

    private static final By COMPANY_DETAILS_NEXT_CTA = By.xpath("(//button[.//p[normalize-space()='Next']])[3]");

    private static final By ENABLED_CALENDAR_DAYS = By.xpath("//div[contains(@class,'shadow-lg') and .//div[contains(@class,'grid-cols-7')]]" + "//button[not(@disabled)]");

    private boolean isDisplayedSafely(WebElement element) {
        try {
            return element != null && element.isDisplayed();
        } catch (Exception e) {   // NoSuchElementException, StaleElementReferenceException, etc.
            return false;
        }
    }

    private static final By ENTITY_TYPE_INPUT = By.xpath("(//label[contains(normalize-space(),'Entity Type')])[2]" + "/following::input[starts-with(@id,'react-select')][1]");

    /**
     * Verifies whether the Login subtitle
     * {@code <h1>Log into your account</h1>} is actually visible to the user.
     * <p>
     * This method performs the following checks:
     * <ul>
     *   <li>Ensures the WebElement reference is not {@code null}</li>
     *   <li>Waits explicitly for the subtitle to become visible</li>
     *   <li>Checks {@link WebElement#isDisplayed()}</li>
     *   <li>Logs a warning if the element is present but blank</li>
     * </ul>
     * <p>
     * In case of any exception (e.g., timeout, stale element, etc.), the method:
     * <ul>
     *   <li>Logs the error with full stack trace</li>
     *   <li>Returns {@code false} to allow the caller to assert and fail with context</li>
     * </ul>
     *
     * @return {@code true} if the subtitle element is visible and displayed on the page; {@code false} otherwise
     */
    public boolean isLoginSubtitleDisplayed() {
        logger.info("🔎 Checking if Login subtitle element is visible on the page...");

        try {
            if (loginSubtitleText == null) {
                logger.error("❌ loginSubtitleText WebElement is null. Check @FindBy locator or PageFactory initialization.");
                return false;
            }

            // Wait for element to be visible
            wait.waitForVisibility(loginSubtitleText);

            boolean displayed = loginSubtitleText.isDisplayed();
            logger.info("👀 Login subtitle displayed = {}", displayed);

            // Extra soft validation: element exists but no text
            if (displayed) {
                String text = loginSubtitleText.getText();
                if (text == null || text.trim().isEmpty()) {
                    logger.warn("⚠️ Login subtitle element is displayed but text is BLANK.");
                } else {
                    logger.debug("📝 Raw subtitle text while visibility check = '{}'", text);
                }
            }

            return displayed;

        } catch (org.openqa.selenium.StaleElementReferenceException stale) {
            logger.error("💥 StaleElementReferenceException while checking subtitle visibility: {}", stale.getMessage(), stale);
            return false;
        } catch (Exception e) {
            logger.error("💥 Error checking login subtitle visibility: {}", e.getMessage(), e);
            return false;
        }
    }


    /**
     * Retrieves the text contents of the Login subtitle
     * {@code <h1>Log into your account</h1>} in a safe, defensive way.
     * <p>
     * Behavior:
     * <ul>
     *   <li>Waits for the subtitle element to be visible</li>
     *   <li>Reads and trims the text</li>
     *   <li>Logs the extracted value at INFO level</li>
     *   <li>Logs a warning if the text is {@code null} or blank</li>
     * </ul>
     * <p>
     * If any exception occurs during lookup or reading:
     * <ul>
     *   <li>The error is logged with stack trace</li>
     *   <li>An empty string ({@code ""}) is returned to the caller</li>
     * </ul>
     *
     * @return trimmed subtitle text if available; otherwise an empty string
     */
    public String getLoginSubtitleText() {
        logger.info("📥 Attempting to read Login subtitle text...");

        try {
            if (loginSubtitleText == null) {
                logger.error("❌ loginSubtitleText WebElement is null. Cannot read subtitle text.");
                return "";
            }

            // Ensure it's visible before reading
            wait.waitForVisibility(loginSubtitleText);

            String rawText = loginSubtitleText.getText();
            String text = (rawText == null) ? "" : rawText.trim();

            if (text.isEmpty()) {
                logger.warn("⚠️ Login subtitle text is BLANK after reading from the element.");
            } else {
                logger.info("📄 Login subtitle text = '{}'", text);
            }

            return text;

        } catch (org.openqa.selenium.StaleElementReferenceException stale) {
            logger.error("💥 StaleElementReferenceException while reading subtitle text: {}", stale.getMessage(), stale);
            return "";
        } catch (Exception e) {
            logger.error("💥 Error reading login subtitle text: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * Clicks the "Sign Up" link located on the login page header.
     * <p>
     * Uses:
     * <ul>
     *     <li>{@code wait.waitForVisibility}</li>
     *     <li>{@code wait.waitForElementToBeClickable}</li>
     *     <li>{@code commonMethods.safeClick}</li>
     * </ul>
     *
     * @throws RuntimeException if clicking fails
     */
    public void clickSignUpLink() {
        logger.info("🔗 Attempting to click 'Sign Up' link on Login page...");

        try {
            wait.waitForVisibility(signUpLinkOnLogin);
            wait.waitForElementToBeClickable(signUpLinkOnLogin);

            commonMethods.safeClick(driver, signUpLinkOnLogin, "Sign Up Link", 10);

            logger.info("✅ 'Sign Up' link clicked successfully.");
        } catch (Exception e) {
            logger.error("❌ Failed to click 'Sign Up' link: {}", e.getMessage(), e);
            throw e;
        }
    }


    /**
     * Waits for the Sign Up page to load fully by verifying:
     * <ul>
     *     <li>Header is visible</li>
     *     <li>Form container is visible</li>
     *     <li>“Sign Up” button is visible</li>
     * </ul>
     * <p>
     * This method retries until timeout is reached.
     *
     * @param timeout maximum duration to wait
     * @return true if page loads successfully, false otherwise
     */
    public boolean waitForSignUpPageToLoad(Duration timeout) {

        logger.info("⏳ Waiting for Sign Up page to load...");

        long end = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < end) {
            try {
                WebElement header = getHeaderElementSafely();

                if (header == null || !header.isDisplayed()) {
                    logger.debug("Header not visible yet...");
                    sleep(300);
                    continue;
                }

                if (!isDisplayedSafe(signUpContainer)) {
                    logger.debug("Sign Up container not found yet...");
                    sleep(300);
                    continue;
                }

                if (!isDisplayedSafe(signUpButton)) {
                    logger.debug("Sign Up button not visible yet...");
                    sleep(300);
                    continue;
                }

                logger.info("✅ Sign Up page loaded successfully.");
                return true;

            } catch (Exception ignored) {
                logger.debug("Still waiting...");
            }

            sleep(300);
        }

        logger.error("❌ Sign Up page did NOT load within {} seconds!", timeout.getSeconds());
        return false;
    }


    /**
     * Returns header element if available. Attempts fallback as well.
     *
     * @return visible header element or null
     */
    private WebElement getHeaderElementSafely() {
        try {
            if (createAccountHeader != null && createAccountHeader.isDisplayed()) return createAccountHeader;
        } catch (Exception ignored) {
        }

        try {
            if (createAccountHeaderFallback != null && createAccountHeaderFallback.isDisplayed())
                return createAccountHeaderFallback;
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * Safely checks if an element is displayed.
     *
     * @param el WebElement to test
     * @return true if displayed, false if hidden or stale
     */
    private boolean isDisplayedSafe(WebElement el) {
        try {
            return el != null && el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Simple sleep wrapper to avoid repeated try-catch blocks.
     *
     * @param ms milliseconds to sleep
     */
    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }

    // ============================================================
    // GETTERS / VALIDATORS
    // ============================================================

    /**
     * Reads and returns the Sign Up header text.
     *
     * @return header text or an empty string
     */
    public String getHeaderText() {
        try {
            WebElement header = getHeaderElementSafely();
            if (header == null) {
                logger.error("❌ Header element not found.");
                return "";
            }
            return header.getText().trim();
        } catch (Exception e) {
            logger.error("💥 Failed to read header text: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * Fetches list of text values from all field labels within the Sign Up form.
     *
     * @return list of field label names
     */
    public List<String> getFormFieldLabels() {
        List<String> labels = new ArrayList<>();

        try {
            if (fieldLabels == null || fieldLabels.isEmpty()) {
                logger.error("❌ No field labels found in Sign Up page!");
                return labels;
            }

            for (WebElement el : fieldLabels) {
                try {
                    if (el.isDisplayed()) {
                        String text = (el.getText() == null) ? "" : el.getText().trim();
                        labels.add(text);
                    }
                } catch (Exception ignore) {
                    labels.add("");
                }
            }

        } catch (Exception e) {
            logger.error("💥 Error retrieving form labels: {}", e.getMessage(), e);
        }

        logger.info("📋 Captured Sign Up form labels: {}", labels);
        return labels;
    }

    /**
     * Checks if the Sign Up button is enabled.
     *
     * @return true if enabled; false otherwise
     */
    public boolean isSignUpButtonEnabled() {
        try {
            boolean enabled = signUpButton.isEnabled();
            logger.info("🔘 Sign Up button enabled = {}", enabled);
            return enabled;
        } catch (Exception e) {
            logger.error("💥 Error checking Sign Up button state: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Types value into Full Name input.
     */
    public void typeFullName(String fullName) {
        commonMethods.safeType(fullNameInput, fullName, "Full Name");
    }

    /**
     * Types value into Email input.
     */
    public void typeEmail(String email) {
        commonMethods.safeType(emailInput, email, "Email");
    }

    /**
     * Types a phone number into the Phone Number input field.
     *
     * @param phoneNumber 10-digit valid phone number
     */
    public void typePhone(String phoneNumber) {
        logger.info("Typing Phone Number: {}", phoneNumber);

        try {
            commonMethods.safeType(phoneField, phoneNumber, "Phone Number");
            logger.info("✓ Phone number typed successfully: {}", phoneNumber);

        } catch (Exception e) {
            logger.error("❌ Failed to type phone number: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Generates a valid 10-digit Indian mobile number
     * starting with 7, 8, or 9.
     */
    public String generateValidIndianMobile() {
        Random random = new Random();

        int firstDigit = 7 + random.nextInt(3); // 7, 8, or 9
        long remainingDigits = (long) (Math.random() * 1_000_000_000L);

        return firstDigit + String.format("%09d", remainingDigits);
    }


    /**
     * Types value into Password input.
     */
    public void typePassword(String password) {
        commonMethods.safeType(passwordInput, password, "Password");
    }

    /**
     * Types value into Confirm Password input.
     */
    public void typeConfirmPassword(String confirmPassword) {
        commonMethods.safeType(confirmPasswordInput, confirmPassword, "Confirm Password");
    }

    // At class level (you already have this, just keeping for context)
    private String generatedPassword;

    /**
     * Generates a strong random password that satisfies:
     * <ul>
     *   <li>Minimum length: 8 characters</li>
     *   <li>At least one uppercase letter</li>
     *   <li>At least one lowercase letter</li>
     *   <li>At least one digit</li>
     *   <li>At least one special character</li>
     * </ul>
     *
     * @param length desired password length (must be >= 8)
     * @return generated strong password string
     */
    public String generateStrongPassword(int length) {
        if (length < 8) {
            length = 8; // enforce minimum length
        }

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()-_=+[]{}|;:,.<>?";

        String allChars = upper + lower + digits + special;

        java.util.Random random = new java.util.Random();
        java.util.List<Character> passwordChars = new java.util.ArrayList<Character>();

        // Ensure at least one from each required category
        passwordChars.add(upper.charAt(random.nextInt(upper.length())));
        passwordChars.add(lower.charAt(random.nextInt(lower.length())));
        passwordChars.add(digits.charAt(random.nextInt(digits.length())));
        passwordChars.add(special.charAt(random.nextInt(special.length())));

        // Fill the remaining length with random characters from all categories
        while (passwordChars.size() < length) {
            passwordChars.add(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle so the first 4 positions aren’t predictable
        Collections.shuffle(passwordChars, random);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < passwordChars.size(); i++) {
            sb.append(passwordChars.get(i));
        }

        return sb.toString();
    }

    /**
     * Clicks the main “Sign Up” button.
     */
    public void clickSignUpButton() {
        commonMethods.safeClick(driver, signUpButton, "Sign Up button", 10);
    }

    /**
     * Checks if the success message for account creation is visible.
     *
     * @return true if the success toast/banner is visible; false otherwise
     */
    public boolean isAccountCreatedSuccessMessageVisible() {
        try {
            logger.info("🔍 Checking if account creation success message is visible...");

            wait.waitForVisibility(successMessageToast);  // custom wait from BasePage
            boolean visible = successMessageToast.isDisplayed();

            logger.info("🎉 Success message visible: {}", visible);
            return visible;

        } catch (Exception e) {
            logger.warn("⚠️ Success message NOT visible yet: {}", e.getMessage());
            return false;
        }
    }


    /**
     * Returns the Login page header text (e.g. "Log into your account").
     * <p>
     * This will:
     * <ul>
     *     <li>Wait for the header to be visible</li>
     *     <li>Return the trimmed text</li>
     *     <li>Log an error and return empty string if anything goes wrong</li>
     * </ul>
     *
     * @return header text if found, otherwise empty string
     */
    public String getLoginHeaderText() {
        logger.info("🔎 Attempting to read Login page header text...");

        try {
            wait.waitForVisibility(loginHeader);
            String headerText = (loginHeader.getText() == null) ? "" : loginHeader.getText().trim();

            logger.info("✅ Login header found = '{}'", headerText);
            return headerText;
        } catch (Exception e) {
            logger.error("💥 Failed to get Login header: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * Clicks the "Login with Password" link on the Login page.
     * <p>
     * Steps:
     * <ul>
     *     <li>Wait for the link to be visible & clickable</li>
     *     <li>Use {@link utils.ReusableCommonMethods#safeClick} for a robust click</li>
     * </ul>
     *
     * @throws RuntimeException if click fails
     */
    public void clickLoginWithPassword() {
        logger.info("🖱 Attempting to click 'Login with Password' link...");

        try {
            wait.waitForVisibility(loginWithPasswordLink);
            wait.waitForElementToBeClickable(loginWithPasswordLink);

            commonMethods.safeClick(driver, loginWithPasswordLink, "Login with Password link", 10);

            logger.info("✅ 'Login with Password' link clicked successfully.");
        } catch (Exception e) {
            logger.error("❌ Failed to click 'Login with Password' link: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Types the given email address into the Login form email field.
     * <p>
     * Uses {@link utils.ReusableCommonMethods#safeType} for reliable typing.
     *
     * @param email email address to be entered
     */
    public void typeLoginEmail(String email) {
        logger.info("✍️ Typing Login email: {}", email);

        try {
            wait.waitForVisibility(loginEmailInput);

            if (!loginEmailInput.isEnabled()) {
                logger.error("❌ Login Email field is not enabled.");
                throw new IllegalStateException("Login Email field is disabled.");
            }

            commonMethods.safeType(loginEmailInput, email, "Login Email");
            logger.info("✅ Login email typed successfully.");

        } catch (Exception e) {
            logger.error("💥 Failed to type into Login Email field: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Types the given password into the Login form password field.
     * <p>
     * Uses {@link utils.ReusableCommonMethods#safeType} for reliable typing.
     *
     * @param password password to be entered
     */
    public void typeLoginPassword(String password) {
        logger.info("🔐 Typing Login password (value masked in logs)...");

        try {
            wait.waitForVisibility(loginPasswordInput);

            if (!loginPasswordInput.isEnabled()) {
                logger.error("❌ Login Password field is not enabled.");
                throw new IllegalStateException("Login Password field is disabled.");
            }

            commonMethods.safeType(loginPasswordInput, password, "Login Password");
            logger.info("✅ Login password typed successfully.");

        } catch (Exception e) {
            logger.error("💥 Failed to type into Login Password field: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Clicks the main "Log In" button on the Login form.
     * <p>
     * Uses {@link utils.ReusableCommonMethods#safeClick} to avoid common click issues.
     */
    public void clickLogInButton() {
        logger.info("🖱 Attempting to click 'Log In' button...");

        try {
            wait.waitForVisibility(loginButton);
            wait.waitForElementToBeClickable(loginButton);

            commonMethods.safeClick(driver, loginButton, "Log In button", 10);

            logger.info("✅ 'Log In' button clicked successfully.");
        } catch (Exception e) {
            logger.error("❌ Failed to click 'Log In' button: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Waits for the post-login Home/Dashboard to load and verifies that
     * the "Complete Your Profile and Unlock Special Offer!" popup appears.
     * <p>
     * Validation:
     * <ul>
     *     <li>Current URL contains <code>/grc/dashboard</code></li>
     *     <li>AND either the popup title or its CTA button is visible</li>
     * </ul>
     *
     * @param timeout maximum time to wait for the dashboard + popup
     * @return {@code true} if both dashboard and popup are detected within timeout; otherwise {@code false}
     */
    public boolean waitForPostLoginHome(Duration timeout) {
        logger.info("⏳ Waiting for post-login dashboard and 'Complete Your Profile' popup...");

        long end = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < end) {
            try {
                String url = driver.getCurrentUrl();
                boolean onDashboard = url.contains("/grc/dashboard");

                boolean popupTitleVisible = isDisplayedSafe(completeProfilePopupTitle);
                boolean popupButtonVisible = isDisplayedSafe(completeProfilePopupButton);

                if (onDashboard && (popupTitleVisible || popupButtonVisible)) {
                    logger.info("✅ Post-login dashboard loaded and 'Complete Your Profile' popup is visible.");
                    logger.info("   URL: {}", url);
                    return true;
                } else {
                    logger.debug("… Still waiting for dashboard/popup. URL='{}', titleVisible={}, buttonVisible={}", url, popupTitleVisible, popupButtonVisible);
                }

            } catch (Exception e) {
                logger.debug("… Exception while polling for post-login state (will retry): {}", e.getMessage());
            }

            sleep(300);
        }

        logger.error("❌ Post-login dashboard or 'Complete Your Profile' popup NOT visible within {} seconds", timeout.getSeconds());
        return false;
    }

    /**
     * Waits until the "Login with Password" form is fully visible:
     * <ul>
     *     <li>Email field is displayed</li>
     *     <li>Password field is displayed</li>
     * </ul>
     *
     * @param timeout maximum time to wait
     * @throws RuntimeException if the form does not appear within the timeout
     */
    public void waitForLoginWithPasswordForm(Duration timeout) {
        logger.info("⏳ Waiting for 'Login with Password' form (email & password fields)...");

        long end = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < end) {
            try {
                boolean emailVisible = isDisplayedSafe(loginEmailInput);
                boolean passwordVisible = isDisplayedSafe(loginPasswordInput);

                if (emailVisible && passwordVisible) {
                    logger.info("✅ 'Login with Password' form is visible.");
                    return;
                } else {
                    logger.debug("… Waiting for form. EmailVisible={}, PasswordVisible={}", emailVisible, passwordVisible);
                }

            } catch (Exception ignored) {
                logger.debug("… Exception while polling for 'Login with Password' form; will retry.");
            }

            sleep(300);
        }

        String msg = "Login with Password form did not appear in time.";
        logger.error("❌ {}", msg);
        throw new RuntimeException(msg);
    }


    /**
     * Reads all visible text inside the "Complete Your Profile" popup.
     */
    public String getCompleteProfilePopupText() {
        try {
            if (isDisplayedSafe(completeProfilePopupContainer)) {
                String text = completeProfilePopupContainer.getText();
                logger.info("📄 Complete Profile popup content:\n{}", text);
                return text;
            } else if (isDisplayedSafe(completeProfilePopupTitle)) {
                String text = completeProfilePopupTitle.getText();
                logger.info("📄 Complete Profile popup title only: {}", text);
                return text;
            } else {
                logger.warn("⚠️ Complete Profile popup container not visible for text capture.");
                return "";
            }
        } catch (Exception e) {
            logger.error("💥 Error while reading Complete Profile popup content: {}", e.getMessage(), e);
            return "";
        }
    }


    /**
     * Determines whether the "Complete Your Profile" popup is currently visible.
     * <p>
     * This popup usually appears after login or when navigating to the dashboard.
     * This method checks for visibility of at least one of:
     * <ul>
     *     <li>The popup title (e.g., "Complete Your Profile and Unlock Special Offer!")</li>
     *     <li>The CTA button ("Complete Business Profile & Claim Offer")</li>
     * </ul>
     * <p>
     * Uses a safe non-throwing visibility check.
     * Returns {@code false} if elements are not found or not visible.
     *
     * @return {@code true} if the Complete Profile popup is visible; otherwise {@code false}.
     */
    public boolean isCompleteProfilePopupVisible() {
        boolean titleVisible = isDisplayedSafe(completeProfilePopupTitle);
        boolean ctaVisible = isDisplayedSafe(completeProfilePopupButton);

        boolean visible = titleVisible || ctaVisible;

        logger.info("👀 Complete Profile popup visible = {} (titleVisible={}, ctaVisible={})", visible, titleVisible, ctaVisible);

        return visible;
    }


    /**
     * Clicks the CTA button inside the "Complete Your Profile" popup:
     * <b>Complete Business Profile & Claim Offer</b>.
     * <p>
     * Behaviour:
     * <ul>
     *     <li>Logs URL and pre-click state</li>
     *     <li>Waits for the CTA to be visible & clickable</li>
     *     <li>Uses safeClick() for robust multi-strategy clicking</li>
     *     <li>Logs success or detailed error information</li>
     * </ul>
     *
     * @throws RuntimeException if the CTA cannot be clicked.
     */
    public void clickCompleteProfilePopupButton() {
        logger.info("🖱 Attempting to click 'Complete Business Profile & Claim Offer' CTA...");

        try {
            String beforeUrl = driver.getCurrentUrl();
            logger.info("URL before CTA click = {}", beforeUrl);

            wait.waitForVisibility(completeProfilePopupButton);
            wait.waitForElementToBeClickable(completeProfilePopupButton);

            // Robust click
            commonMethods.safeClick(driver, completeProfilePopupButton, "Complete Profile Popup CTA", 10);

            String afterUrl = driver.getCurrentUrl();
            logger.info("✅ Complete Profile popup CTA clicked. URL after click = {}", afterUrl);

        } catch (Exception ex) {
            logger.error("❌ Failed to click Complete Profile popup CTA: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to click Complete Profile popup CTA", ex);
        }
    }


    /**
     * Waits until the welcome page (/grc/welcome) with question & radio options is visible.
     */
    public boolean waitForWelcomeUsagePage(Duration timeout) {
        logger.info("⏳ Waiting for Welcome usage page (/grc/welcome)...");

        long end = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < end) {
            try {
                String url = driver.getCurrentUrl();
                boolean onWelcome = url.contains("/grc/welcome");

                boolean qVisible = isDisplayedSafe(welcomeUsageQuestionLabel);
                boolean bVisible = isDisplayedSafe(businessNeedsOption);
                boolean pVisible = isDisplayedSafe(personalNeedsOption);

                if (onWelcome && qVisible && bVisible && pVisible) {
                    logger.info("✅ Welcome usage page detected. URL={}, questionVisible={}, businessVisible={}, personalVisible={}", url, qVisible, bVisible, pVisible);
                    return true;
                } else {
                    logger.debug("… Waiting for welcome usage page. URL='{}', q={}, b={}, p={}", url, qVisible, bVisible, pVisible);
                }
            } catch (Exception e) {
                logger.debug("… Exception while polling for Welcome page: {}", e.getMessage());
            }

            sleep(300);
        }

        logger.error("❌ Welcome usage page NOT visible within {} seconds", timeout.getSeconds());
        return false;
    }

    /**
     * Returns the question text "How do you plan to use Zolvit 360?*"
     */
    public String getWelcomeUsageQuestionText() {
        try {
            if (isDisplayedSafe(welcomeUsageQuestionLabel)) {
                String txt = welcomeUsageQuestionLabel.getText();
                logger.info("📄 Welcome question text = {}", txt);
                return txt;
            }
        } catch (Exception e) {
            logger.error("💥 Failed to read Welcome question text: {}", e.getMessage(), e);
        }
        return "";
    }


    /**
     * Clicks the "Skip to dashboard" CTA on the Welcome usage page.
     * <p>
     * Behaviour:
     * <ul>
     *   <li>Waits for the button to be visible and clickable.</li>
     *   <li>Logs the URL before and immediately after the click.</li>
     * </ul>
     * <p>
     * This method does <b>not</b> assert that the dashboard is loaded; the calling
     * step usually performs that verification via {@code homePage.waitForHomeLoaded(...)}.
     */
    public void clickSkipToDashboard() {
        logger.info("🖱 Clicking 'Skip to dashboard' on Welcome page...");

        try {
            String beforeUrl = driver.getCurrentUrl();
            logger.info("Current URL before 'Skip to dashboard' click = {}", beforeUrl);

            // Make sure the button is ready
            wait.waitForVisibility(skipToDashboardButton);
            wait.waitForElementToBeClickable(skipToDashboardButton);

            // Robust click
            commonMethods.safeClick(driver, skipToDashboardButton, "Skip to dashboard", 10);

            String afterUrl = driver.getCurrentUrl();
            logger.info("✅ 'Skip to dashboard' clicked. URL after click = {}", afterUrl);

        } catch (Exception e) {
            logger.error("❌ Failed to click 'Skip to dashboard' on Welcome page: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to click 'Skip to dashboard' on Welcome page", e);
        }
    }


    /**
     * Waits for dashboard with 'Extra 10% Off - Festive Sale!' popup.
     */
    public boolean waitForDashboardWithFestiveOffer(Duration timeout) {
        logger.info("⏳ Waiting for dashboard with 'Extra 10% Off - Festive Sale!' popup...");

        long end = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < end) {
            try {
                String url = driver.getCurrentUrl();
                boolean onDashboard = url.contains("/grc/dashboard");
                boolean festiveVisible = isDisplayedSafe(festiveOfferTitle);

                if (onDashboard && festiveVisible) {
                    logger.info("✅ Dashboard with Festive Offer popup visible. URL='{}'", url);
                    return true;
                } else {
                    logger.debug("… Waiting for Festive popup. URL='{}', festiveVisible={}", url, festiveVisible);
                }
            } catch (Exception e) {
                logger.debug("… Exception while polling for Festive Offer popup: {}", e.getMessage());
            }

            sleep(300);
        }

        logger.error("❌ Dashboard with Festive Offer popup NOT visible within {} seconds", timeout.getSeconds());
        return false;
    }

    /**
     * Returns the Festive offer title text (e.g. "Extra 10% Off - Festive Sale!").
     * <p>
     * Behaviour:
     * <ul>
     *   <li>Checks if the festive title element is visible</li>
     *   <li>Reads and trims the text</li>
     *   <li>Logs warnings if element not visible or text is blank</li>
     *   <li>Returns empty string on any failure</li>
     * </ul>
     *
     * @return trimmed festive offer title text, or {@code ""} on failure
     */
    public String getFestiveOfferTitleText() {
        logger.info("📥 Attempting to read Festive offer title text...");

        try {
            if (!isDisplayedSafe(festiveOfferTitle)) {
                logger.warn("⚠️ Festive offer title element is not visible.");
                return "";
            }

            String raw = festiveOfferTitle.getText();
            String txt = raw == null ? "" : raw.trim();

            if (txt.isEmpty()) {
                logger.warn("⚠️ Festive offer title text is BLANK.");
            } else {
                logger.info("📄 Festive offer title = '{}'", txt);
            }

            return txt;

        } catch (Exception e) {
            logger.error("💥 Failed to read Festive offer title: {}", e.getMessage(), e);
            return "";
        }
    }


    /**
     * Verifies that the "Explore Service Hub" CTA button is actually present
     * inside the Festive Offer popup.
     * <p>
     * Behaviour:
     * <ul>
     *   <li>Checks underlying WebElement reference for null</li>
     *   <li>Waits for the button to be visible</li>
     *   <li>Returns {@code true} if displayed, otherwise {@code false}</li>
     * </ul>
     * <p>
     * Any exception (timeout, stale element, etc.) is logged and the method
     * safely returns {@code false} to let the caller assert with context.
     *
     * @return {@code true} if the CTA is visible; {@code false} otherwise
     */
    public boolean isExploreServiceHubCtaVisible() {
        logger.info("🔎 Checking visibility of 'Explore Service Hub' CTA on Festive popup...");

        try {
            if (exploreServiceHubButton == null) {
                logger.error("❌ exploreServiceHubButton is null. Check @FindBy locator or PageFactory init.");
                return false;
            }

            wait.waitForVisibility(exploreServiceHubButton);
            boolean visible = exploreServiceHubButton.isDisplayed();
            logger.info("👀 'Explore Service Hub' CTA visible = {}", visible);

            String rawText = exploreServiceHubButton.getText();
            String txt = rawText == null ? "" : rawText.trim();
            logger.info("📝 CTA raw text = '{}'", txt);

            return visible;
        } catch (org.openqa.selenium.StaleElementReferenceException stale) {
            logger.error("💥 StaleElementReferenceException while checking CTA visibility: {}", stale.getMessage(), stale);
            return false;
        } catch (Exception e) {
            logger.error("💥 Error checking 'Explore Service Hub' CTA visibility: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Returns the trimmed text of the 'Explore Service Hub' CTA button.
     * If anything fails, an empty string is returned.
     *
     * @return CTA button text or {@code ""} on failure
     */
    public String getExploreServiceHubCtaText() {
        logger.info("📥 Reading 'Explore Service Hub' CTA button text...");

        try {
            if (exploreServiceHubButton == null) {
                logger.error("❌ exploreServiceHubButton is null. Cannot read CTA text.");
                return "";
            }

            wait.waitForVisibility(exploreServiceHubButton);
            String raw = exploreServiceHubButton.getText();
            String txt = raw == null ? "" : raw.trim();

            if (txt.isEmpty()) {
                logger.warn("⚠️ 'Explore Service Hub' CTA text is blank.");
            } else {
                logger.info("📄 CTA text = '{}'", txt);
            }

            return txt;
        } catch (org.openqa.selenium.StaleElementReferenceException stale) {
            logger.error("💥 StaleElementReferenceException while reading CTA text: {}", stale.getMessage(), stale);
            return "";
        } catch (Exception e) {
            logger.error("💥 Error reading 'Explore Service Hub' CTA text: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * Clicks on the 'Explore Service Hub' CTA on the Festive Offer popup in a safe way.
     *
     * @throws RuntimeException if the click operation fails
     */
    public void clickExploreServiceHubCta() {
        logger.info("🖱 Attempting to click 'Explore Service Hub' CTA on Festive popup...");

        try {
            wait.waitForVisibility(exploreServiceHubButton);
            wait.waitForElementToBeClickable(exploreServiceHubButton);

            commonMethods.safeClick(driver, exploreServiceHubButton, "'Explore Service Hub' CTA", 10);

            logger.info("✅ 'Explore Service Hub' CTA clicked successfully.");
        } catch (Exception e) {
            logger.error("❌ Failed to click 'Explore Service Hub' CTA: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Waits for the Service Hub page to be loaded after clicking
     * the Festive popup CTA.
     * <p>
     * Validation:
     * <ul>
     *   <li>Current URL contains <code>/grc/marketplace</code></li>
     *   <li>AND Service Hub header is visible</li>
     * </ul>
     *
     * @param timeout maximum duration to wait
     * @return {@code true} if both URL and header match expected; otherwise {@code false}
     */
    public boolean waitForServiceHubPage(Duration timeout) {
        logger.info("⏳ Waiting for Service Hub page (/grc/marketplace) to load...");

        long end = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < end) {
            try {
                String url = driver.getCurrentUrl();
                boolean correctUrl = url.contains("/grc/marketplace");
                boolean headerVisible = isDisplayedSafe(serviceHubHeader);

                if (correctUrl && headerVisible) {
                    logger.info("✅ Service Hub page detected. URL='{}', headerVisible={}", url, headerVisible);
                    return true;
                } else {
                    logger.debug("… Still waiting for Service Hub. URL='{}', headerVisible={}", url, headerVisible);
                }
            } catch (Exception e) {
                logger.debug("… Exception while polling Service Hub page: {}", e.getMessage());
            }

            sleep(300);
        }

        logger.error("❌ Service Hub page NOT visible within {} seconds", timeout.getSeconds());
        return false;
    }


    /**
     * Clicks the "Start Your Business" CTA on the dashboard.
     * <p>
     * NOTE: This CTA performs a same-tab redirect to the Vakilsearch
     * Company Registration page – it does NOT open a new window.
     * <br>
     * Navigation and URL/header validation are handled by the caller.
     */
    public void clickStartYourBusinessCtaAndSwitchToNewTab() {
        logger.info("🖱 Attempting to click 'Start Your Business' CTA on dashboard...");

        try {
            if (startYourBusinessCta == null) {
                String msg = "'Start Your Business' CTA WebElement is null. Check @FindBy and PageFactory init.";
                logger.error("❌ {}", msg);
                throw new RuntimeException(msg);
            }

            // Wait until visible & clickable
            wait.waitForVisibility(startYourBusinessCta);
            wait.waitForElementToBeClickable(startYourBusinessCta);

            // Click using robust helper
            commonMethods.safeClick(driver, startYourBusinessCta, "'Start Your Business' CTA", 10);

            logger.info("✅ 'Start Your Business' CTA clicked. " + "Expecting SAME-TAB redirect to Company Registration page.");

            // Do NOT check for new window here – redirect is same-tab.
            // The step definition will call waitForCompanyRegistrationPage(...)
            // and perform URL + header assertions.

        } catch (Exception e) {
            logger.error("💥 Failed to click 'Start Your Business' CTA and trigger redirect: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to click 'Start Your Business' CTA", e);
        }
    }


    /**
     * Waits for the external Company Registration page to be fully loaded.
     * <p>
     * Validation:
     * <ul>
     *   <li>URL contains <code>/company-registration</code></li>
     *   <li>AND the main H1 header 'Company Registration Online in India' is visible</li>
     * </ul>
     *
     * @param timeout maximum duration to wait
     * @return {@code true} if page is detected correctly; {@code false} otherwise
     */
    public boolean waitForCompanyRegistrationPage(Duration timeout) {
        logger.info("⏳ Waiting for Company Registration page (/company-registration) to load...");

        long end = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < end) {
            try {
                String url = driver.getCurrentUrl();
                boolean correctUrl = url.contains("/company-registration");
                boolean headerVisible = isDisplayedSafe(companyRegistrationHeader);

                if (correctUrl && headerVisible) {
                    logger.info("✅ Company Registration page detected. URL='{}', headerVisible={}", url, headerVisible);
                    return true;
                } else {
                    logger.debug("… Still waiting for Company Registration page. URL='{}', headerVisible={}", url, headerVisible);
                }
            } catch (Exception e) {
                logger.debug("… Exception while polling Company Registration page: {}", e.getMessage());
            }

            sleep(300);
        }

        logger.error("❌ Company Registration page NOT visible within {} seconds", timeout.getSeconds());
        return false;
    }

    /**
     * Reads and returns the H1 header from the Company Registration page.
     *
     * @return trimmed header text, or empty string if element is missing / unreadable
     */
    public String getCompanyRegistrationHeaderText() {
        logger.info("📥 Attempting to read Company Registration page header text...");

        try {
            if (companyRegistrationHeader == null) {
                logger.error("❌ companyRegistrationHeader element is null. Check @FindBy locator.");
                return "";
            }

            wait.waitForVisibility(companyRegistrationHeader);
            String raw = companyRegistrationHeader.getText();
            String text = raw == null ? "" : raw.trim();

            if (text.isEmpty()) {
                logger.warn("⚠️ Company Registration header text is blank.");
            } else {
                logger.info("📄 Company Registration header text = '{}'", text);
            }

            return text;
        } catch (org.openqa.selenium.StaleElementReferenceException stale) {
            logger.error("💥 StaleElementReferenceException while reading Company Registration header: {}", stale.getMessage(), stale);
            return "";
        } catch (Exception e) {
            logger.error("💥 Error reading Company Registration header text: {}", e.getMessage(), e);
            return "";
        }
    }


    /**
     * Complex back navigation helper used from Pricing:
     * <p>
     * Flow:
     * 1) From Pricing page → browser back().
     * 2) If "Still Confused?" popup appears:
     * - fill email / mobile / pincode,
     * - click "Talk to registration expert",
     * - wait for Payment page ("Choose Payment Method"),
     * - browser back() twice (Payment → previous → Home),
     * - confirm Home loaded.
     * 3) If popup does NOT appear after first back():
     * - back() once more.
     * - If popup appears now → same as step 2.
     * - Else → assume we are closer to Home and just wait for Home.
     *
     * @param overallTimeout overall timeout for the whole flow
     * @return true if we successfully reach Home page, false otherwise
     */
    public boolean navigateBackFromPricingWithStillConfusedToHome(Duration overallTimeout) {
        long timeoutMs = overallTimeout.toMillis();
        long startMs = System.currentTimeMillis();

        HomePage homePage = new HomePage(driver);
        String urlBefore = safeGetCurrentUrl();
        logger.info("🔁 [POM] Starting complex back navigation from Pricing. Initial URL='{}'", urlBefore);

        try {
            // ---- FIRST BACK FROM PRICING ----
            logger.info("⬅️ [POM] Performing first back() from Pricing...");
            driver.navigate().back();
            waitForPageToLoad();

            String urlAfterFirst = safeGetCurrentUrl();
            logger.info("🌐 [POM] After first back() from Pricing. URL='{}'", urlAfterFirst);

            // Check for Still Confused popup
            if (isStillConfusedPopupVisible()) {
                logger.info("🧩 'Still Confused?' popup detected after first back(). Proceeding with lead form flow...");
                return handleStillConfusedFlowThenReturnHome(startMs, timeoutMs);
            }

            logger.info("ℹ️ 'Still Confused?' popup NOT shown after first back. Performing one more back()...");

            // ---- SECOND BACK (if popup was not shown) ----
            driver.navigate().back();
            waitForPageToLoad();

            String urlAfterSecond = safeGetCurrentUrl();
            logger.info("🌐 [POM] After second back() from Pricing. URL='{}'", urlAfterSecond);

            // Again check for popup
            if (isStillConfusedPopupVisible()) {
                logger.info("🧩 'Still Confused?' popup detected after second back(). Proceeding with lead form flow...");
                return handleStillConfusedFlowThenReturnHome(startMs, timeoutMs);
            }

            // No popup at all → we just try to ensure Home page is loaded
            long elapsedMs = System.currentTimeMillis() - startMs;
            long remainingMs = timeoutMs - elapsedMs;
            if (remainingMs < 5_000L) {
                remainingMs = 5_000L; // minimal wait
            }

            logger.info("ℹ️ No 'Still Confused?' popup appeared. Waiting for Home page directly (remaining {} ms)...", Long.valueOf(remainingMs));


            boolean homeLoaded = homePage.waitForHomeLoaded(Duration.ofMillis(remainingMs));
            logger.info("🏠 Home page loaded without Still Confused flow = {}", Boolean.valueOf(homeLoaded));
            return homeLoaded;

        } catch (Exception e) {
            logger.error("❌ Error during complex back navigation from Pricing: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Internal helper:
     * Assumes the "Still Confused?" popup is already visible.
     * Handles:
     * - filling the popup
     * - submitting ("Talk to registration expert")
     * - waiting for Payment page
     * - back() twice
     * - confirming Home page.
     */
    private boolean handleStillConfusedFlowThenReturnHome(long startMs, long overallTimeoutMs) {
        // 1) Fill and submit popup
        fillStillConfusedPopupAndSubmit();

        HomePage homePage = new HomePage(driver);
        // 2) Wait for Payment page
        long elapsedMs = System.currentTimeMillis() - startMs;
        long remainingMs = overallTimeoutMs - elapsedMs;
        if (remainingMs < 10_000L) {
            remainingMs = 10_000L; // at least 10s for payment page
        }

        boolean paymentLoaded = waitForPaymentPageLoaded(Duration.ofMillis(remainingMs));
        if (!paymentLoaded) {
            logger.error("❌ Payment page did not load after 'Talk to registration expert' click.");
            return false;
        }

        // 3) From Payment → back twice
        logger.info("⬅️ [POM] From Payment page: performing first back()...");
        driver.navigate().back();
        waitForPageToLoad();

        logger.info("⬅️ [POM] From intermediate page: performing second back() (towards Home)...");
        driver.navigate().back();
        waitForPageToLoad();

        // 4) Confirm Home page
        elapsedMs = System.currentTimeMillis() - startMs;
        long remainingHomeMs = overallTimeoutMs - elapsedMs;
        if (remainingHomeMs < 5_000L) {
            remainingHomeMs = 5_000L;
        }

        logger.info("🏠 Waiting for Home page after payment back navigations (remaining {} ms)...", Long.valueOf(remainingHomeMs));

        boolean homeLoaded = homePage.waitForHomeLoaded(Duration.ofMillis(remainingHomeMs));
        logger.info("🏠 Home page loaded after Still Confused + Payment flow = {}", Boolean.valueOf(homeLoaded));
        return homeLoaded;
    }


    /**
     * Clicks on the "Add Business" link on the dashboard and waits for the
     * Welcome usage selection page (/grc/welcome) to be loaded.
     * <p>
     * Behaviour:
     * <ul>
     *   <li>Logs the URL before the click.</li>
     *   <li>Waits for the "Add Business" link to be visible & clickable.</li>
     *   <li>Performs a safe click with multiple fallbacks.</li>
     *   <li>Waits for the Welcome usage page via {@code waitForWelcomeUsagePage(timeout)}.</li>
     *   <li>Logs the URL and result after navigation.</li>
     * </ul>
     *
     * @param timeout maximum time to wait for the Welcome usage page to load
     * @return {@code true} if the Welcome usage page is detected within the timeout;
     * {@code false} otherwise (though in practice this method throws on failure).
     */
    public boolean clickAddBusinessLinkAndWaitForWelcome(Duration timeout) {
        logger.info("🖱 Attempting to click 'Add Business' link on dashboard...");

        String beforeUrl = driver.getCurrentUrl();
        logger.info("Current URL before 'Add Business' click = {}", beforeUrl);

        try {
            // Ensure the link is interactable
            wait.waitForVisibility(addBusinessLink);
            wait.waitForElementToBeClickable(addBusinessLink);

            // Robust click
            commonMethods.safeClick(driver, addBusinessLink, "Add Business link", (int) timeout.getSeconds());
            logger.info("✅ 'Add Business' link clicked. Waiting for Welcome usage page...");

            // Reuse your existing Welcome-page wait
            boolean welcomeLoaded = waitForWelcomeUsagePage(timeout);
            String afterUrl = driver.getCurrentUrl();

            logger.info("URL after 'Add Business' click = {}", afterUrl);
            logger.info("Welcome usage page loaded after 'Add Business' click = {}", welcomeLoaded);

            if (!welcomeLoaded) {
                logger.error("❌ Welcome usage page did NOT load after clicking 'Add Business' " + "within {} sec. Current URL = {}", timeout.getSeconds(), afterUrl);
                throw new RuntimeException("Welcome usage page did not load after clicking 'Add Business'.");
            }

            return true;

        } catch (Exception e) {
            logger.error("❌ Failed to click 'Add Business' link or wait for Welcome page: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to click 'Add Business' link and navigate to Welcome page", e);
        }
    }


    /**
     * Clicks the "Get Started" widget button on the dashboard.
     * Includes full validation, visibility checks and safe click.
     */
    public void clickGetStartedWidget() {
        logger.info("🖱 Attempting to click 'Get Started' widget on Dashboard...");

        try {
            wait.waitForVisibility(getStartedWidgetButton);
            wait.waitForElementToBeClickable(getStartedWidgetButton);

            commonMethods.safeClick(driver, getStartedWidgetButton, "Get Started Widget", 10);

            logger.info("✅ 'Get Started' widget clicked successfully.");

        } catch (Exception e) {
            logger.error("❌ Failed to click 'Get Started' widget: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to click Get Started widget", e);
        }
    }


    /**
     * Validates that the Get Started widget is visible
     * (Header + Subtext displayed correctly).
     *
     * @return true if widget content is correct
     */
    public boolean isGetStartedWidgetVisible() {
        logger.info("🔍 Validating Get Started widget header and subtext...");

        try {
            boolean headerVisible = isDisplayedSafe(getStartedHeaderTitle);
            boolean subTextVisible = isDisplayedSafe(getStartedSubText);

            logger.info("Header visible = {}", headerVisible);
            logger.info("Subtext visible = {}", subTextVisible);

            return headerVisible && subTextVisible;

        } catch (Exception e) {
            logger.error("❌ Error validating Get Started widget: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Clicks the "Continue" CTA inside the Get Started widget.
     * Also logs the widget descriptive content.
     */
    public void clickGetStartedContinue() {

        logger.info("🔍 Reading Get Started content before clicking Continue...");

        try {
            String content = completeProfileContentText.getText().trim();
            logger.info("📝 Get Started widget content: {}", content);


        } catch (Exception e) {
            logger.warn("⚠️ Unable to read Complete Profile content: {}", e.getMessage());
        }

        logger.info("🖱 Clicking 'Continue' CTA inside Get Started widget...");

        try {
            wait.waitForVisibility(getStartedContinueButton);
            wait.waitForElementToBeClickable(getStartedContinueButton);

            commonMethods.safeClick(driver, getStartedContinueButton, "Get Started Continue CTA", 10);

            logger.info("✅ 'Continue' clicked successfully.");

        } catch (Exception e) {
            logger.error("❌ Failed to click Continue inside Get Started widget: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to click Continue in Get Started widget", e);
        }
    }


    /**
     * Selects "Business needs" usage on the welcome page and logs the action.
     * Validates that the option is visible and clickable before clicking.
     */
    public void selectBusinessNeedsUsage() {
        logger.info("🧭 Selecting 'Business needs' usage option on Welcome page...");

        try {
            wait.waitForVisibility(businessNeedsUsageOption);
            wait.waitForElementToBeClickable(businessNeedsUsageOption);

            if (!businessNeedsUsageOption.isDisplayed()) {
                throw new IllegalStateException("'Business needs' usage option is not displayed.");
            }

            commonMethods.safeClick(driver, businessNeedsUsageOption, "'Business needs' usage option", 10);

            logger.info("✅ 'Business needs' usage option selected.");
        } catch (Exception e) {
            logger.error("❌ Failed to select 'Business needs' usage option: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to select 'Business needs' usage option", e);
        }
    }

    /**
     * Clicks the generic "Next" CTA used in the onboarding wizard.
     * Ensures the button is visible and enabled before clicking.
     */
    public void clickWelcomeNextButton() {
        logger.info("🖱 Clicking 'Next' CTA on onboarding / welcome flow...");

        try {
            wait.waitForVisibility(welcomeNextButton);
            wait.waitForElementToBeClickable(welcomeNextButton);

            if (!welcomeNextButton.isEnabled()) {
                throw new IllegalStateException("'Next' CTA on welcome flow is disabled.");
            }

            commonMethods.safeClick(driver, welcomeNextButton, "'Next' CTA", 10);

            logger.info("✅ 'Next' CTA clicked.");
        } catch (Exception e) {
            logger.error("❌ Failed to click 'Next' CTA on welcome flow: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to click 'Next' CTA on welcome flow", e);
        }
    }

    /**
     * Verifies that the Full Name step of the onboarding wizard is visible.
     *
     * @return true if Full Name label and input are both displayed; false otherwise.
     */
    public boolean isFullNameStepVisible() {
        logger.info("🔎 Checking visibility of 'Full Name' onboarding step...");

        try {
            wait.waitForVisibility(onboardingFullNameLabel);
            wait.waitForVisibility(onboardingFullNameInput);

            boolean labelVisible = isDisplayedSafely(onboardingFullNameLabel);
            boolean inputVisible = isDisplayedSafely(onboardingFullNameInput);

            logger.info("   • Full Name label visible = {}", labelVisible);
            logger.info("   • Full Name input visible = {}", inputVisible);

            return labelVisible && inputVisible;
        } catch (Exception e) {
            logger.error("❌ Error while checking Full Name step visibility: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Clears the Full Name input field and types the provided value.
     *
     * @param fullName full name to type into the onboarding Full Name field.
     */
    public void enterFullNameOnOnboarding(String fullName) {
        logger.info("⌨️  Typing Full Name '{}' on onboarding step...", fullName);

        try {
            if (fullName == null || fullName.trim().isEmpty()) {
                throw new IllegalArgumentException("Full Name cannot be null or empty.");
            }

            wait.waitForVisibility(onboardingFullNameInput);
            wait.waitForElementToBeClickable(onboardingFullNameInput);

            onboardingFullNameInput.click();
            onboardingFullNameInput.clear();
            onboardingFullNameInput.sendKeys(fullName);

            logger.info("✅ Full Name '{}' entered on onboarding step.", fullName);
        } catch (Exception e) {
            logger.error("❌ Failed to enter Full Name on onboarding step: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to enter Full Name on onboarding step", e);
        }
    }

    /**
     * Returns true if the Designation step is visible on the onboarding flow.
     * Uses stable locators based on the label text & React-select input
     * instead of the dynamic container id (Designation-0.xxxxx).
     */
    public boolean isDesignationStepVisible() {
        logger.info("🔎 Checking visibility of Designation onboarding step...");

        try {
            WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Label: "Designation*"
            WebElement label = localWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//label[contains(normalize-space(),'Designation')])[2]")));

            // React-select input that belongs to Designation
            WebElement input = localWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//label[contains(normalize-space(),'Designation')]/following::input[starts-with(@id,'react-select')][1])[2]")));

            boolean labelVisible = isDisplayedSafely(label);
            boolean inputVisible = isDisplayedSafely(input);
            boolean visible = labelVisible && inputVisible;

            logger.info("👀 Designation step visible = {} (label={}, input={})", visible, labelVisible, inputVisible);

            return visible;
        } catch (TimeoutException te) {
            logger.warn("⏳ Designation step NOT visible within timeout. URL={}", driver.getCurrentUrl());
            return false;
        } catch (Exception e) {
            logger.error("❌ Error while checking Designation step visibility: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Types a random designation into the React-select textbox and presses ENTER.
     * Also clicks the Next CTA once a value is selected.
     *
     * @return designation text that was finally used.
     */
    public String selectAnyDesignationFromDropdown() {
        logger.info("🎯 Selecting random designation via typing...");

        try {
            String randomDesignation = TestDataGenerator.getRandomDesignation();
            if (randomDesignation == null || randomDesignation.trim().isEmpty()) {
                throw new IllegalStateException("Random designation generated is null or empty.");
            }
            logger.info("📝 Random designation chosen: {}", randomDesignation);

            WebElement input = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("react-select-3-input")));

            input.click();
            input.clear();
            input.sendKeys(randomDesignation);
            logger.info("⌨ Typed designation: {}", randomDesignation);

            input.sendKeys(Keys.ENTER);
            logger.info("✔ ENTER key pressed to select designation.");

            WebElement nextCta = driver.findElement(By.xpath("(//p[normalize-space()='Next'])[3]"));
            commonMethods.safeClick(driver, nextCta, "'Next' CTA after Designation", 10);

            logger.info("➡️ Clicked Next CTA after designation selection.");
            return randomDesignation;

        } catch (Exception e) {
            logger.error("❌ Failed while selecting designation via typing: {}", e.getMessage(), e);
            throw new RuntimeException("Designation selection failed", e);
        }
    }


    /**
     * Returns true if the Entity Type step is visible on the onboarding flow.
     * Waits for both the "Entity Type" label and its dropdown container.
     */
    public boolean isEntityTypeStepVisible() {
        logger.info("🔎 Checking visibility of Entity Type onboarding step...");

        try {
            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement label = webDriverWait.until(ExpectedConditions.visibilityOf(entityTypeLabel));
            WebElement dropdown = webDriverWait.until(ExpectedConditions.visibilityOf(entityTypeDropdownContainer));

            boolean labelVisible = isDisplayedSafely(label);
            boolean dropdownVisible = isDisplayedSafely(dropdown);

            boolean visible = labelVisible && dropdownVisible;
            logger.info("👀 Entity Type step visible = {} (label={}, dropdown={})", visible, labelVisible, dropdownVisible);

            return visible;

        } catch (Exception e) {
            logger.error("❌ Error while checking Entity Type step visibility: {}", e.getMessage(), e);
            return false;
        }
    }


    /**
     * Selects a random Entity Type by typing into the react-select textbox and pressing ENTER,
     * then clicks the Next CTA.
     *
     * @return Entity Type text that was selected.
     */
    public String selectEntityType() {
        logger.info("🎯 Selecting random Entity Type on onboarding step...");

        try {
            String randomEntityType = TestDataGenerator.getRandomBusinessTypeInGrcWelcome();
            if (randomEntityType == null || randomEntityType.trim().isEmpty()) {
                throw new IllegalStateException("Random Entity Type generated is null or empty.");
            }
            logger.info("🎯 Random Entity Type chosen = '{}'", randomEntityType);

            wait.waitForVisibility(entityTypeDropdownContainer);
            commonMethods.safeClick(driver, entityTypeDropdownContainer, "Entity Type dropdown", 10);

            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement input = webDriverWait.until(ExpectedConditions.elementToBeClickable(ENTITY_TYPE_INPUT));

            input.clear();
            input.sendKeys(randomEntityType);
            input.sendKeys(Keys.ENTER);
            logger.info("✅ Entity Type '{}' selected successfully.", randomEntityType);

            WebElement nextButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//p[normalize-space()='Next'])[3]")));
            commonMethods.safeClick(driver, nextButton, "'Next' CTA after Entity Type", 10);
            logger.info("➡️ Clicked Next CTA after Entity Type selection.");

            return randomEntityType;

        } catch (Exception e) {
            logger.error("❌ Failed to select Entity Type: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to select Entity Type from dropdown", e);
        }
    }


    /**
     * Returns true if any one of the following onboarding variants is visible:
     * 1) CIN
     * 2) Trust Registration Number
     * 3) Society Registration Number
     * 4) PAN
     * 5) FCRN
     * 6) LLPIN  <-- newly added
     */
    public boolean isCinStepVisible() {
        logger.info("🔎 Checking visibility of CIN / Trust / Society / PAN / FCRN / LLPIN onboarding step...");

        try {
            WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Wait until ANY one label appears
            localWait.until(ExpectedConditions.or(ExpectedConditions.visibilityOf(cinLabel), ExpectedConditions.visibilityOf(trustRegNumberLabel), ExpectedConditions.visibilityOf(societyRegNumberLabel), ExpectedConditions.visibilityOf(panLabel), ExpectedConditions.visibilityOf(fcrnLabel), ExpectedConditions.visibilityOf(llpinLabel)));

            // Safe visibility checks
            boolean cinLabelVisible = isDisplayedSafely(cinLabel);
            boolean cinInputVisible = isDisplayedSafely(cinInputField);

            boolean trustLabelVisible = isDisplayedSafely(trustRegNumberLabel);
            boolean trustInputVisible = isDisplayedSafely(trustRegNumberInputField);

            boolean societyLabelVisible = isDisplayedSafely(societyRegNumberLabel);
            boolean societyInputVisible = isDisplayedSafely(societyRegNumberInputField);

            boolean panLabelVisible = isDisplayedSafely(panLabel);
            boolean panInputVisible = isDisplayedSafely(panInputField);

            boolean fcrnLabelVisible = isDisplayedSafely(fcrnLabel);
            boolean fcrnInputVisible = isDisplayedSafely(fcrnInputField);

            boolean llpinLabelVisible = isDisplayedSafely(llpinLabel);
            boolean llpinInputVisible = isDisplayedSafely(llpinInputField);

            // Variant 1: CIN
            if (cinLabelVisible && cinInputVisible) {
                logger.info("👀 Detected CIN variant.");
                return true;
            }

            // Variant 2: Trust Registration Number
            if (trustLabelVisible && trustInputVisible) {
                logger.info("👀 Detected Trust Registration Number variant.");
                return true;
            }

            // Variant 3: Society Registration Number
            if (societyLabelVisible && societyInputVisible) {
                logger.info("👀 Detected Society Registration Number variant.");
                return true;
            }

            // Variant 4: PAN
            if (panLabelVisible && panInputVisible) {
                logger.info("👀 Detected PAN variant.");
                return true;
            }

            // Variant 5: FCRN
            if (fcrnLabelVisible && fcrnInputVisible) {
                logger.info("👀 Detected FCRN variant.");
                return true;
            }

            // Variant 6: LLPIN
            if (llpinLabelVisible && llpinInputVisible) {
                logger.info("👀 Detected LLPIN variant.");
                return true;
            }

            logger.warn("⚠ No valid onboarding field detected! " + "CIN(label={},input={}), Trust(label={},input={}), " + "Society(label={},input={}), PAN(label={},input={}), " + "FCRN(label={},input={}), LLPIN(label={},input={})", cinLabelVisible, cinInputVisible, trustLabelVisible, trustInputVisible, societyLabelVisible, societyInputVisible, panLabelVisible, panInputVisible, fcrnLabelVisible, fcrnInputVisible, llpinLabelVisible, llpinInputVisible);

            return false;

        } catch (Exception e) {
            logger.error("❌ Error detecting CIN/Trust/Society/PAN/FCRN/LLPIN onboarding variant: {}", e.getMessage(), e);
            return false;
        }
    }


    /**
     * Clicks the "Skip and Next" CTA on CIN step WITHOUT entering any CIN.
     * This method is also used when the Trust Registration Number variant is shown.
     */
    public void clickSkipAndNextOnCinStep() {
        logger.info("➡️ Clicking 'Skip and Next' on CIN / Trust Registration step without entering value...");

        try {
            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement skipAndNext = webDriverWait.until(ExpectedConditions.elementToBeClickable(CIN_SKIP_AND_NEXT_CTA));

            commonMethods.safeClick(driver, skipAndNext, "'Skip and Next' CTA on CIN step", 10);

            logger.info("✅ 'Skip and Next' CTA clicked successfully on CIN / Trust Registration step.");

        } catch (Exception e) {
            logger.error("❌ Failed to click 'Skip and Next' on CIN step: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to click 'Skip and Next' on CIN step", e);
        }
    }


    /**
     * Returns true if the Company Details step is visible on the onboarding flow.
     * <p>
     * Supports multiple variants:
     * 1) Company Name + Date of Incorporation
     * 2) Trade Name + Business Start Date
     * 3) Firm Name + Date of Registration
     * 4) Society Name + Date of Registration
     * 5) Company Name + Date of Establishment of Business in India
     * 6) Trust Name + Date of Registration           <-- NEW
     * <p>
     * State and Industry must be present in all variants.
     */
    public boolean isCompanyDetailsStepVisible() {
        logger.info("🔎 Checking visibility of Company Details onboarding step (Company / Trade / Firm / Society / Establishment / Trust variants)...");

        try {
            WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Wait until ANY one company-like label is visible
            localWait.until(ExpectedConditions.or(ExpectedConditions.visibilityOf(companyNameLabel), ExpectedConditions.visibilityOf(tradeNameLabel), ExpectedConditions.visibilityOf(firmNameLabel), ExpectedConditions.visibilityOf(societyNameLabel), ExpectedConditions.visibilityOf(companyNameEstIndiaLabel), ExpectedConditions.visibilityOf(trustNameLabel)));

            // Variant 1: Company Name + DOI
            boolean companyVisible = isDisplayedSafely(companyNameLabel) && isDisplayedSafely(companyNameInput) && isDisplayedSafely(doiLabel) && isDisplayedSafely(doiInput);

            // Variant 2: Trade Name + Business Start Date
            boolean tradeVisible = isDisplayedSafely(tradeNameLabel) && isDisplayedSafely(tradeNameInput) && isDisplayedSafely(businessStartDateLabel) && isDisplayedSafely(businessStartDateInput);

            // Variant 3: Firm Name + Date of Registration
            boolean firmVisible = isDisplayedSafely(firmNameLabel) && isDisplayedSafely(firmNameInput) && isDisplayedSafely(dateOfRegistrationLabel) && isDisplayedSafely(dateOfRegistrationInput);

            // Variant 4: Society Name + Date of Registration
            boolean societyVisible = isDisplayedSafely(societyNameLabel) && isDisplayedSafely(societyNameInput) && isDisplayedSafely(dateOfRegistrationLabel) && isDisplayedSafely(dateOfRegistrationInput);

            // Variant 5: Company Name + Date of Establishment of Business in India
            boolean establishmentVisible = isDisplayedSafely(companyNameEstIndiaLabel) && isDisplayedSafely(companyNameEstIndiaInput) && isDisplayedSafely(dateOfEstablishmentLabel) && isDisplayedSafely(dateOfEstablishmentInput);

            // Variant 6: Trust Name + Date of Registration
            boolean trustVisible = isDisplayedSafely(trustNameLabel) && isDisplayedSafely(trustNameInput) && isDisplayedSafely(trustDateOfRegistrationLabel) && isDisplayedSafely(trustDateOfRegistrationInput);

            // State & Industry labels must always be visible
            boolean stateVisible = isDisplayedSafely(stateLabel);
            boolean industryVisible = isDisplayedSafely(industryTypeLabel);

            logger.info("   • Company variant visible           = {}", companyVisible);
            logger.info("   • Trade variant visible             = {}", tradeVisible);
            logger.info("   • Firm variant visible              = {}", firmVisible);
            logger.info("   • Society variant visible           = {}", societyVisible);
            logger.info("   • Establishment variant visible     = {}", establishmentVisible);
            logger.info("   • Trust variant visible             = {}", trustVisible);
            logger.info("   • State label visible               = {}", stateVisible);
            logger.info("   • Industry label visible            = {}", industryVisible);

            boolean stepVisible = (companyVisible || tradeVisible || firmVisible || societyVisible || establishmentVisible || trustVisible) && stateVisible && industryVisible;

            if (stepVisible) {
                if (companyVisible) {
                    logger.info("👀 Company Details detected as **Company Name + Date of Incorporation** variant.");
                }
                if (tradeVisible) {
                    logger.info("👀 Company Details detected as **Trade Name + Business Start Date** variant.");
                }
                if (firmVisible) {
                    logger.info("👀 Company Details detected as **Firm Name + Date of Registration** variant.");
                }
                if (societyVisible) {
                    logger.info("👀 Company Details detected as **Society Name + Date of Registration** variant.");
                }
                if (establishmentVisible) {
                    logger.info("👀 Company Details detected as **Company Name + Date of Establishment of Business in India** variant.");
                }
                if (trustVisible) {
                    logger.info("👀 Company Details detected as **Trust Name + Date of Registration** variant.");
                }
            } else {
                logger.warn("⚠ No valid Company Details variant fully visible.");
            }

            return stepVisible;

        } catch (Exception e) {
            logger.error("❌ Error detecting Company Details step: {}", e.getMessage(), e);
            return false;
        }
    }


    // 🔹 Helper: pick a random enabled day & click it
    private String pickRandomCalendarDay(WebDriverWait webDriverWait, String contextLabel) throws InterruptedException {

        List<WebElement> enabledDays = webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(ENABLED_CALENDAR_DAYS));

        if (enabledDays == null || enabledDays.isEmpty()) {
            throw new RuntimeException("No enabled days found in calendar for: " + contextLabel);
        }

        Thread.sleep(7000);
        int randomIndex = new Random().nextInt(enabledDays.size());
        WebElement randomDay = enabledDays.get(randomIndex);

        String dayText = randomDay.getText().trim();
        if (dayText.isEmpty()) {
            dayText = randomDay.getAttribute("aria-label");
        }

        logger.info("📅 [{}] Random calendar day picked = '{}'", contextLabel, dayText);

        // JS click to avoid overlap / intercept issues
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", randomDay);
        logger.info("✅ [{}] Random calendar day clicked", contextLabel);

        return dayText;
    }

    // 🔹 Your full method using the helper everywhere
    public CompanyDetailsData fillCompanyDetailsAndClickNext() {
        logger.info("📝 Filling Company Details step with random data (handling Company / Trade / Firm / Society / Establishment / Trust variants)...");

        String variantLabel = null;
        String primaryName = null;
        String dateValue = null;
        String finalState = null;
        String finalIndustry = null;

        // day texts per variant (handy if you log them separately)
        String dayText = null; // Company – DOI
        String bsDayText = null; // Trade – Business Start Date
        String regDayText = null; // Firm – Date of Registration
        String socRegDayText = null; // Society – Date of Registration
        String estDayText = null; // Company – Establishment Date
        String trustRegDayText = null; // Trust – Date of Registration

        try {
            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // ---------- detect which variant is visible ----------
            boolean companyVariantVisible = isDisplayedSafely(companyNameLabel) && isDisplayedSafely(companyNameInput) && isDisplayedSafely(doiLabel) && isDisplayedSafely(doiInput);

            boolean tradeVariantVisible = isDisplayedSafely(tradeNameLabel) && isDisplayedSafely(tradeNameInput) && isDisplayedSafely(businessStartDateLabel) && isDisplayedSafely(businessStartDateInput);

            boolean firmVariantVisible = isDisplayedSafely(firmNameLabel) && isDisplayedSafely(firmNameInput) && isDisplayedSafely(dateOfRegistrationLabel) && isDisplayedSafely(dateOfRegistrationInput);

            boolean societyVariantVisible = isDisplayedSafely(societyNameLabel) && isDisplayedSafely(societyNameInput) && isDisplayedSafely(dateOfRegistrationLabel) && isDisplayedSafely(dateOfRegistrationInput);

            boolean establishmentVariantVisible = isDisplayedSafely(companyNameEstIndiaLabel) && isDisplayedSafely(companyNameEstIndiaInput) && isDisplayedSafely(dateOfEstablishmentLabel) && isDisplayedSafely(dateOfEstablishmentInput);

            boolean trustVariantVisible = isDisplayedSafely(trustNameLabel) && isDisplayedSafely(trustNameInput) && isDisplayedSafely(trustDateOfRegistrationLabel) && isDisplayedSafely(trustDateOfRegistrationInput);

            logger.info("🔁 Visible variants -> company={}, trade={}, firm={}, society={}, establishment={}, trust={}", companyVariantVisible, tradeVariantVisible, firmVariantVisible, societyVariantVisible, establishmentVariantVisible, trustVariantVisible);

            if (!companyVariantVisible && !tradeVariantVisible && !firmVariantVisible && !societyVariantVisible && !establishmentVariantVisible && !trustVariantVisible) {
                throw new IllegalStateException("None of the Company Details variants is fully visible. Expected one of: " + "[Company Name + DOI] / [Trade Name + Business Start Date] / " + "[Firm Name + Date of Registration] / [Society Name + Date of Registration] / " + "[Company Name + Date of Establishment of Business in India] / " + "[Trust Name + Date of Registration].");
            }

            // ---------- fill ONE variant & capture name + date text ----------
            if (companyVariantVisible) {

                variantLabel = "Company";
                String companyName = TestDataGenerator.getRandomCompanyName();
                if (companyName == null || companyName.trim().isEmpty()) {
                    throw new IllegalStateException("Random Company Name generated is null or empty.");
                }

                logger.info("🏢 [Company Variant] Random Company Name = '{}'", companyName);

                wait.waitForVisibility(companyNameInput);
                commonMethods.typeText(companyNameInput, companyName, "Company Name");

                logger.info("📅 [Company Variant] Selecting random Date of Incorporation...");
                commonMethods.safeClick(driver, doiInput, "Date of Incorporation input", 10);

                Thread.sleep(10000);
                dayText = pickRandomCalendarDay(webDriverWait, "Company – DOI");

                primaryName = companyName;
                dateValue = dayText;

            } else if (establishmentVariantVisible) {

                variantLabel = "Company (Establishment in India)";
                String companyName = TestDataGenerator.getRandomCompanyName();
                if (companyName == null || companyName.trim().isEmpty()) {
                    throw new IllegalStateException("Random Company Name generated is null or empty (Establishment variant).");
                }

                logger.info("🏢 [Establishment Variant] Random Company Name = '{}'", companyName);

                wait.waitForVisibility(companyNameEstIndiaInput);
                commonMethods.typeText(companyNameEstIndiaInput, companyName, "Company Name (Establishment in India)");

                logger.info("📆 [Establishment Variant] Selecting random Date of Establishment of Business in India...");
                commonMethods.safeClick(driver, dateOfEstablishmentInput, "Date of Establishment of Business in India input", 10);

                estDayText = pickRandomCalendarDay(webDriverWait, "Establishment – Date of Establishment");

                primaryName = companyName;
                dateValue = estDayText;

            } else if (tradeVariantVisible) {

                variantLabel = "Trade";
                String tradeName = TestDataGenerator.getRandomCompanyName();
                if (tradeName == null || tradeName.trim().isEmpty()) {
                    throw new IllegalStateException("Random Trade Name generated is null or empty.");
                }

                logger.info("🏷 [Trade Variant] Random Trade Name = '{}'", tradeName);

                wait.waitForVisibility(tradeNameInput);
                commonMethods.typeText(tradeNameInput, tradeName, "Trade Name");

                logger.info("📆 [Trade Variant] Selecting random Business Start Date...");
                commonMethods.safeClick(driver, businessStartDateInput, "Business Start Date input", 10);

                bsDayText = pickRandomCalendarDay(webDriverWait, "Trade – Business Start Date");

                primaryName = tradeName;
                dateValue = bsDayText;

            } else if (firmVariantVisible) {

                variantLabel = "Firm";
                String firmName = TestDataGenerator.getRandomCompanyName();
                if (firmName == null || firmName.trim().isEmpty()) {
                    throw new IllegalStateException("Random Firm Name generated is null or empty.");
                }

                logger.info("🏛 [Firm Variant] Random Firm Name = '{}'", firmName);

                wait.waitForVisibility(firmNameInput);
                commonMethods.typeText(firmNameInput, firmName, "Firm Name");

                logger.info("📜 [Firm Variant] Selecting random Date of Registration...");
                commonMethods.safeClick(driver, dateOfRegistrationInput, "Date of Registration input", 10);

                regDayText = pickRandomCalendarDay(webDriverWait, "Firm – Date of Registration");

                primaryName = firmName;
                dateValue = regDayText;

            } else if (trustVariantVisible) {

                // NEW TRUST BRANCH
                variantLabel = "Trust";
                String trustName = TestDataGenerator.getRandomCompanyName();
                if (trustName == null || trustName.trim().isEmpty()) {
                    throw new IllegalStateException("Random Trust Name generated is null or empty.");
                }

                logger.info("🏛 [Trust Variant] Random Trust Name = '{}'", trustName);

                wait.waitForVisibility(trustNameInput);
                commonMethods.typeText(trustNameInput, trustName, "Trust Name");

                logger.info("📜 [Trust Variant] Selecting random Date of Registration...");
                commonMethods.safeClick(driver, trustDateOfRegistrationInput, "Trust Date of Registration input", 10);

                trustRegDayText = pickRandomCalendarDay(webDriverWait, "Trust – Date of Registration");

                primaryName = trustName;
                dateValue = trustRegDayText;

            } else {

                // Society variant
                variantLabel = "Society";
                String societyName = TestDataGenerator.getRandomCompanyName();
                if (societyName == null || societyName.trim().isEmpty()) {
                    throw new IllegalStateException("Random Society Name generated is null or empty.");
                }

                logger.info("🏛 [Society Variant] Random Society Name = '{}'", societyName);

                wait.waitForVisibility(societyNameInput);
                commonMethods.typeText(societyNameInput, societyName, "Society Name");

                logger.info("📜 [Society Variant] Selecting random Date of Registration...");
                commonMethods.safeClick(driver, dateOfRegistrationInput, "Date of Registration input", 10);

                socRegDayText = pickRandomCalendarDay(webDriverWait, "Society – Date of Registration");

                primaryName = societyName;
                dateValue = socRegDayText;
            }

            // ---------- State selection ----------
            String randomState = TestDataGenerator.getRandomLocation();
            if (randomState == null || randomState.trim().isEmpty()) {
                throw new IllegalStateException("Random State generated is null or empty.");
            }
            logger.info("🗺 Random State chosen = '{}'", randomState);

            wait.waitForVisibility(stateDropdownContainer);
            commonMethods.safeClick(driver, stateDropdownContainer, "State dropdown", 10);

            WebElement stateInput = webDriverWait.until(ExpectedConditions.elementToBeClickable(STATE_INPUT));
            stateInput.clear();
            stateInput.sendKeys(randomState);
            stateInput.sendKeys(Keys.ENTER);
            logger.info("✅ State '{}' selected.", randomState);

            finalState = randomState;

            // ---------- Industry selection ----------
            String randomIndustry = TestDataGenerator.getRandomIndustry();
            if (randomIndustry == null || randomIndustry.trim().isEmpty()) {
                throw new IllegalStateException("Random Industry Type generated is null or empty.");
            }
            logger.info("🏭 Random Industry Type chosen = '{}'", randomIndustry);

            WebElement industryInput = webDriverWait.until(ExpectedConditions.elementToBeClickable(INDUSTRY_TYPE_INPUT));
            industryInput.clear();
            industryInput.sendKeys(randomIndustry);
            industryInput.sendKeys(Keys.ENTER);
            logger.info("✅ Industry Type '{}' selected.", randomIndustry);

            finalIndustry = randomIndustry;

            // ---------- Next CTA ----------
            WebElement nextCta = webDriverWait.until(ExpectedConditions.elementToBeClickable(COMPANY_DETAILS_NEXT_CTA));
            commonMethods.safeClick(driver, nextCta, "'Next' CTA on Company Details step", 10);
            logger.info("➡️ Clicked Next CTA on Company Details step.");

            logger.info("📦 Company Details summary -> variant={}, name='{}', date='{}', state='{}', industry='{}'", variantLabel, primaryName, dateValue, finalState, finalIndustry);

            return new CompanyDetailsData(variantLabel, primaryName, dateValue, finalState, finalIndustry);

        } catch (Exception e) {
            logger.error("❌ Failed to fill Company Details step: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to complete Company Details onboarding step", e);
        }
    }


    /**
     * Detects which onboarding identity variant is currently displayed on the step:
     * - CIN
     * - Trust Registration Number
     * - Society Registration Number
     * - PAN
     * - FCRN
     * - LLPIN
     *
     * @return Human-readable variant name, or "Unknown Variant" if nothing matches.
     */
    public String detectCinVariantOnOnboarding() {
        try {
            class VariantLocator {
                final String name;
                final By by;

                VariantLocator(String name, By by) {
                    this.name = name;
                    this.by = by;
                }
            }

            VariantLocator[] variants = new VariantLocator[]{new VariantLocator("CIN", By.xpath("(//label[normalize-space()='CIN'])[2]")), new VariantLocator("Trust Registration Number", By.xpath("(//label[normalize-space()='Trust Registration Number'])[2]")), new VariantLocator("Society Registration Number", By.xpath("(//label[normalize-space()='Society Registration Number'])[2]")), new VariantLocator("PAN", By.xpath("(//label[normalize-space()='PAN'])[2]")), new VariantLocator("FCRN", By.xpath("(//label[normalize-space()='FCRN'])[2]")), new VariantLocator("LLPIN", By.xpath("(//label[normalize-space()='LLPIN'])[2]")) // 👈 new
            };

            for (VariantLocator v : variants) {
                java.util.List<org.openqa.selenium.WebElement> labels = driver.findElements(v.by);
                if (labels != null && !labels.isEmpty()) {
                    for (org.openqa.selenium.WebElement label : labels) {
                        try {
                            if (label.isDisplayed()) {
                                logger.info("👀 Detected onboarding identity variant label displayed: {}", v.name);
                                return v.name;
                            }
                        } catch (Exception ignored) {
                            // element might go stale; ignore and continue
                        }
                    }
                }
            }

            logger.warn("⚠ No CIN / Trust / Society / PAN / FCRN / LLPIN variant label is visibly detected on the page.");
            return "Unknown Variant";

        } catch (Exception e) {
            logger.error("❌ Error while detecting onboarding identity variant: {}", e.getMessage(), e);
            return "Unknown Variant";
        }
    }


    /**
     * Returns true if the Team Size onboarding step is visible.
     * Validates the presence of the label and dropdown container.
     */
    public boolean isTeamSizeStepVisible() {
        logger.info("🔎 Checking visibility of Team Size onboarding step...");

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            boolean labelVisible = isDisplayedSafely(teamSizeLabel);
            boolean dropdownVisible = isDisplayedSafely(teamSizeDropdownContainer);

            logger.info("   • Team Size label visible    = {}", labelVisible);
            logger.info("   • Team Size dropdown visible = {}", dropdownVisible);

            boolean visible = labelVisible && dropdownVisible;

            if (visible) {
                logger.info("👀 Team Size step is visible.");
            } else {
                logger.warn("⚠ Team Size step NOT visible.");
            }

            return visible;

        } catch (Exception e) {
            logger.error("❌ Error while checking Team Size step visibility: {}", e.getMessage(), e);
            return false;
        }
    }


    public String selectRandomTeamSizeAndClickNext() {
        logger.info("🎯 Selecting random Team Size from dropdown...");

        try {
            String randomTeamSize = TestDataGenerator.getRandomEmployeeCountDuringOnboarding();
            if (randomTeamSize == null || randomTeamSize.trim().isEmpty()) {
                throw new IllegalStateException("Random Team Size generated is null or empty.");
            }
            logger.info("🧮 Random Team Size chosen = '{}'", randomTeamSize);

            // Open dropdown
            wait.waitForVisibility(teamSizeDropdownContainer);
            commonMethods.safeClick(driver, teamSizeDropdownContainer, "Team Size dropdown", 10);

            WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement input = localWait.until(ExpectedConditions.elementToBeClickable(TEAM_SIZE_INPUT));

            // Type team size and press ENTER
            input.clear();
            input.sendKeys(randomTeamSize);
            input.sendKeys(Keys.ENTER);

            logger.info("✅ Team Size '{}' selected.", randomTeamSize);

            // Click Next CTA
            WebElement nextBtn = localWait.until(ExpectedConditions.elementToBeClickable(TEAM_SIZE_NEXT_CTA));
            commonMethods.safeClick(driver, nextBtn, "'Next' CTA on Team Size step", 10);

            // [existing selection logic]

            logger.info("➡️ Clicked Next CTA after selecting Team Size.");
            return randomTeamSize;

        } catch (Exception e) {
            logger.error("❌ Failed to select Team Size: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to complete Team Size onboarding step", e);
        }
    }


    /**
     * Returns true if the Annual Turnover onboarding step is visible.
     * Validates the presence of the label and dropdown container.
     */
    public boolean isAnnualTurnoverStepVisible() {
        logger.info("🔎 Checking visibility of Annual Turnover onboarding step...");

        try {
            WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // small explicit waits so we don’t hit stale / timing issues
            localWait.until(ExpectedConditions.visibilityOf(annualTurnoverLabel));
            localWait.until(ExpectedConditions.visibilityOf(annualTurnoverDropdownContainer));

            boolean labelVisible = isDisplayedSafely(annualTurnoverLabel);
            boolean dropdownVisible = isDisplayedSafely(annualTurnoverDropdownContainer);

            logger.info("   • Annual Turnover label visible    = {}", labelVisible);
            logger.info("   • Annual Turnover dropdown visible = {}", dropdownVisible);

            boolean visible = labelVisible && dropdownVisible;

            if (visible) {
                logger.info("👀 Annual Turnover step is visible.");
            } else {
                logger.warn("⚠ Annual Turnover step NOT visible.");
            }

            return visible;

        } catch (Exception e) {
            logger.error("❌ Error while checking Annual Turnover step visibility: {}", e.getMessage(), e);
            return false;
        }
    }


    public String selectRandomAnnualTurnoverAndClickGetStarted() {
        logger.info("💰 Selecting random Annual Turnover and clicking 'Get Started'...");

        try {
            WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(10));

            String randomTurnover = TestDataGenerator.getRandomTurnoverDuringOnboarding();
            if (randomTurnover == null || randomTurnover.trim().isEmpty()) {
                throw new IllegalStateException("Random Annual Turnover generated is null or empty.");
            }
            logger.info("💹 Random Annual Turnover chosen = '{}'", randomTurnover);

            // 2) Open dropdown
            wait.waitForVisibility(annualTurnoverDropdownContainer);
            commonMethods.safeClick(driver, annualTurnoverDropdownContainer, "Annual Turnover dropdown", 10);

            // 3) Type into react-select input and press ENTER
            WebElement input = localWait.until(ExpectedConditions.elementToBeClickable(ANNUAL_TURNOVER_INPUT));
            input.clear();
            input.sendKeys(randomTurnover);
            input.sendKeys(Keys.ENTER);

            logger.info("✅ Annual Turnover '{}' selected.", randomTurnover);

            // 4) Click "Get Started" CTA
            WebElement getStartedBtn = localWait.until(ExpectedConditions.elementToBeClickable(GET_STARTED_CTA));
            commonMethods.safeClick(driver, getStartedBtn, "'Get Started' CTA on Annual Turnover step", 10);

            logger.info("🚀 Clicked 'Get Started' CTA after selecting Annual Turnover.");
            return randomTurnover;

        } catch (Exception e) {
            logger.error("❌ Failed to complete Annual Turnover step: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to complete Annual Turnover onboarding step", e);
        }
    }


    /**
     * Returns true if the analysis screen header is visible:
     * - "Analysing your compliance requirements"
     * - "Overall Progress"
     */
    public boolean isComplianceAnalysisHeaderVisible() {
        logger.info("🔎 Checking analysis screen header (Analysing + Overall Progress)...");

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

            wait.until(ExpectedConditions.visibilityOf(analysingRequirementsHeading));
            wait.until(ExpectedConditions.visibilityOf(overallProgressLabel));

            boolean analysingVisible = isDisplayedSafely(analysingRequirementsHeading);
            boolean overallVisible = isDisplayedSafely(overallProgressLabel);

            logger.info("   • Analysing heading visible  = {}", analysingVisible);
            logger.info("   • Overall Progress visible   = {}", overallVisible);

            return analysingVisible && overallVisible;

        } catch (Exception e) {
            logger.error("❌ Error while checking analysis screen header: {}", e.getMessage(), e);
            return false;
        }
    }


    /**
     * Reads the numeric "Compliances Found" value from the analysis screen.
     * The count animates from 0 -> final value, so we wait until it becomes
     * a non-zero integer (after the "View my Compliances" CTA is enabled).
     */
    public int getCompliancesFoundCount() {
        logger.info("📊 Reading 'Compliances Found' count on analysis screen...");

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));

            // 1) Make sure the CTA is clickable → analysis completed
            wait.until(ExpectedConditions.elementToBeClickable(VIEW_MY_COMPLIANCES_BTN));

            // 2) Make sure the label itself is there
            wait.until(ExpectedConditions.visibilityOf(compliancesFoundLabel));

            // 3) Now poll until the count becomes a non-zero integer
            Integer finalValue = wait.until(driver -> {
                WebElement el = driver.findElement(COMPLIANCES_FOUND_COUNT);
                String rawText = el.getText().trim();
                logger.debug("📊 Polled 'Compliances Found' text = '{}'", rawText);

                String digitsOnly = rawText.replaceAll("[^0-9]", "");
                if (digitsOnly.isEmpty()) {
                    return null; // keep waiting
                }

                int value = Integer.parseInt(digitsOnly);

                // If final count is never 0 in your flow, this is safe.
                // While it's 0 (animation phase), keep waiting.
                if (value == 0) {
                    return null; // still animating, wait more
                }

                return value; // non-zero → treat as final
            });

            logger.info("📊 Parsed 'Compliances Found' = {}", finalValue);
            return finalValue;

        } catch (Exception e) {
            logger.error("❌ Unable to read 'Compliances Found' count: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read 'Compliances Found' count", e);
        }
    }


    /**
     * Waits until the "View my Compliances" CTA becomes visible.
     * Returns true if visible within timeout.
     */
    public boolean waitForViewMyCompliancesVisible() {
        logger.info("⏱ Waiting for 'View my Compliances' CTA to be visible...");

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(180)); // heavy analysis

            WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(VIEW_MY_COMPLIANCES_BTN));

            boolean visible = isDisplayedSafely(btn);
            logger.info("👀 'View my Compliances' visible = {}", visible);
            return visible;

        } catch (Exception e) {
            logger.error("❌ 'View my Compliances' CTA did not become visible: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Clicks the "View my Compliances" CTA on the analysis screen.
     */
    public void clickViewMyCompliances() {
        logger.info("🖱 Clicking 'View my Compliances' CTA...");

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(VIEW_MY_COMPLIANCES_BTN));

            commonMethods.safeClick(driver, btn, "'View my Compliances' CTA", 10);
            logger.info("✅ 'View my Compliances' CTA clicked.");

        } catch (Exception e) {
            logger.error("❌ Failed to click 'View my Compliances' CTA: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to click 'View my Compliances' CTA", e);
        }
    }


    // All (45) count
    @FindBy(xpath = "//p[normalize-space()='All']/following-sibling::span")
    private WebElement allTabCountSpan;

    // Due Date selected value – e.g. "This Financial Year"
    @FindBy(xpath = "//label[normalize-space()='Due Date']//following::div//p[normalize-space()='This Financial Y...']")
    private WebElement dueDateFilterValue;

    /**
     * Returns the integer count from the "All (X)" tab.
     */
    public int getAllTabCount() {
        logger.info("📊 Reading 'All' tab compliances count...");
        wait.waitForVisibility(allTabCountSpan);

        String raw = allTabCountSpan.getText().trim();   // e.g. "45" or "(45)"
        logger.info("📊 Raw All tab text = '{}'", raw);

        String digitsOnly = raw.replaceAll("[^0-9]", "");
        int value = Integer.parseInt(digitsOnly);

        logger.info("📊 Parsed All tab count = {}", value);
        return value;
    }

    /**
     * Returns the current Due Date filter text as shown in the UI
     * (may be truncated, e.g. "This Financial Y...").
     */
    public String getDueDateFilterText() {
        logger.info("📅 Reading Due Date filter text...");
        wait.waitForVisibility(dueDateFilterValue);
        String text = dueDateFilterValue.getText().trim();
        logger.info("📅 Due Date filter text (raw) = '{}'", text);
        return text;
    }


    /**
     * Returns normalized Due Date filter text:
     * - removes "..." and "."
     * - collapses multiple spaces
     * - lower-cases
     */
    public String getDueDateFilterTextNormalized() {
        String raw = getDueDateFilterText(); // uses the method above

        String normalized = raw.replace("...", "").replace(".", "").replaceAll("\\s+", " ").trim().toLowerCase();

        logger.info("📅 Due Date filter text (normalized) = '{}'", normalized);
        return normalized;
    }

    /**
     * Normalises the Due Date filter text:
     * - removes "..." and "."
     * - collapses multiple spaces
     * - lower-cases
     */
    private String normalizeDueDateText(String text) {
        if (text == null) return "";
        return text.replace("...", "").replace(".", "").replaceAll("\\s+", " ").trim().toLowerCase();
    }

    /**
     * Semantic check: is the Due Date filter effectively
     * "This Financial Year"? (handles truncated UI text like "This Financial Y...")
     */
    public boolean isDueDateFilterThisFinancialYear() {
        final String expected = "This Financial Year";

        String raw = getDueDateFilterText();
        String normalizedActual = normalizeDueDateText(raw);
        String normalizedExpected = normalizeDueDateText(expected);

        boolean match = normalizedActual.equals(normalizedExpected)
                // extra safety: allow "startsWith" in case UI truncates even earlier
                || normalizedActual.startsWith(normalizedExpected.substring(0, 15)); // "this financial y"

        logger.info("📅 Comparing Due Date filter -> raw='{}', actual(normalized)='{}', expected(normalized)='{}', match={}", raw, normalizedActual, normalizedExpected, Boolean.valueOf(match));

        return match;
    }


    // In SignUpPage.java

    // Overall Progress % value on analysis screen (e.g. "100%")
    @FindBy(xpath = "(//p[normalize-space()='Overall Progress']/ancestor::div[contains(@class,'justify-between')][1]//p[contains(normalize-space(),'%')])[2]")
    private WebElement overallProgressPercentText;

    /**
     * Waits until Overall Progress reaches 100%.
     *
     * @return final progress value (should be 100)
     */
    public int waitUntilOverallProgressIsHundredPercent() {
        logger.info("⏱ Waiting for 'Overall Progress' to reach 100%...");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(180)); // generous timeout

        Boolean reached = wait.until(d -> {
            try {
                String text = overallProgressPercentText.getText().trim(); // e.g. "100%"
                logger.debug("Current Overall Progress text='{}'", text);

                String digits = text.replaceAll("[^0-9]", "");
                if (digits.isEmpty()) {
                    return false;
                }
                int value = Integer.parseInt(digits);
                return value >= 100;
            } catch (org.openqa.selenium.StaleElementReferenceException ex) {
                // element may rerender while animating → just retry
                return false;
            } catch (Exception ex) {
                logger.warn("Error while reading Overall Progress: {}", ex.getMessage());
                return false;
            }
        });

        if (!Boolean.TRUE.equals(reached)) {
            throw new RuntimeException("'Overall Progress' did not reach 100% within the expected time.");
        }

        // Read final value once more for logging / return
        String finalText = overallProgressPercentText.getText().trim();
        int finalVal = Integer.parseInt(finalText.replaceAll("[^0-9]", ""));
        logger.info("✅ 'Overall Progress' reached {}%", finalVal);

        return finalVal;
    }


    /**
     * Returns true if the "Welcome! Here's your new subscriber offer." popup
     * is visible on the Compliances page (title + CTA).
     */
    public boolean isNewSubscriberOfferPopupVisible() {
        logger.info("🔎 Checking visibility of 'new subscriber offer' popup...");

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Wait for title to appear
            wait.until(ExpectedConditions.visibilityOf(newSubscriberOfferTitle));

            boolean titleVisible = isDisplayedSafely(newSubscriberOfferTitle);
            boolean ctaVisible = isDisplayedSafely(claimMyDiscountCta);

            logger.info("   • Popup title visible = {}", titleVisible);
            logger.info("   • 'Claim My 10% Discount' CTA visible = {}", ctaVisible);

            return titleVisible && ctaVisible;
        } catch (Exception e) {
            logger.error("❌ Error while checking new subscriber offer popup: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Returns the popup title text, mainly for logging.
     */
    public String getNewSubscriberOfferTitleText() {
        try {
            String text = newSubscriberOfferTitle.getText().trim();
            logger.info("📝 New subscriber offer popup title = '{}'", text);
            return text;
        } catch (Exception e) {
            logger.error("❌ Failed to read new subscriber offer popup title: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * Clicks the "Claim My 10% Discount" CTA on the popup.
     */
    public void clickClaimMyTenPercentDiscount() {
        logger.info("🖱 Clicking 'Claim My 10% Discount' CTA on subscriber offer popup...");

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(claimMyDiscountCta));

            commonMethods.safeClick(driver, btn, "'Claim My 10% Discount' CTA", 10);
            logger.info("✅ 'Claim My 10% Discount' CTA clicked.");
        } catch (Exception e) {
            logger.error("❌ Failed to click 'Claim My 10% Discount' CTA: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to click 'Claim My 10% Discount' CTA", e);
        }
    }

    /**
     * Validates that the Annual Compliance right panel is visible with all key sections.
     */
    public boolean isAnnualCompliancePanelContentVisible() {
        logger.info("🔎 Validating Annual Compliance right panel contents...");

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Wait for main heading of the panel
            wait.until(ExpectedConditions.visibilityOf(annualComplianceHeading));

            boolean headingVisible = isDisplayedSafely(annualComplianceHeading);
            boolean whyVisible = isDisplayedSafely(whyIsThisMandatoryHeading);
            boolean accountMgrVisible = isDisplayedSafely(yourDedicatedAccountManagerHeading);
            boolean extraTenVisible = isDisplayedSafely(additionalTenPercentHeading);

            logger.info("   • 'Annual Compliance for Your Business' visible     = {}", headingVisible);
            logger.info("   • 'Why is this mandatory?' visible                  = {}", whyVisible);
            logger.info("   • 'Your Dedicated Account Manager' visible          = {}", accountMgrVisible);
            logger.info("   • 'Additional 10% will be Applied at Checkout' visible = {}", extraTenVisible);

            return headingVisible && whyVisible && accountMgrVisible && extraTenVisible;
        } catch (Exception e) {
            logger.error("❌ Error while validating Annual Compliance panel contents: {}", e.getMessage(), e);
            return false;
        }
    }


    @FindBy(xpath = "//p[normalize-space()='Explore Plans']")
    private WebElement explorePlansCTA;

    @FindBy(xpath = "//p[contains(normalize-space(),'Launch-Ready Plans')]")
    private WebElement launchReadyPlansHeading;


    /**
     * Clicks the "Explore Plans" CTA in the Annual Compliance drawer.
     * <p>
     * Uses an explicit wait + safeClick to handle visibility, clickability and overlap issues.
     * Throws a RuntimeException if the click cannot be completed.
     */
    public void clickExplorePlans() {
        logger.info("🖱 Preparing to click 'Explore Plans' CTA in Annual Compliance drawer...");

        try {
            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(30));

            logger.info("⏳ Waiting for 'Explore Plans' CTA to become clickable...");
            WebElement btn = webDriverWait.until(ExpectedConditions.elementToBeClickable(explorePlansCTA));

            String btnText;
            try {
                btnText = btn.getText();
            } catch (Exception e) {
                btnText = "";
            }
            if (btnText == null) {
                btnText = "";
            }
            btnText = btnText.trim();

            logger.info("🟢 'Explore Plans' CTA is clickable. Button text='{}'", btnText);

            // Prefer your reusable safeClick to handle intercepts/overlap
            commonMethods.safeClick(driver, btn, "'Explore Plans' CTA", 20);

            logger.info("✅ 'Explore Plans' CTA clicked successfully.");

        } catch (TimeoutException te) {
            logger.error("❌ Timeout waiting for 'Explore Plans' CTA to become clickable: {}", te.getMessage(), te);
            throw new RuntimeException("Timeout waiting for 'Explore Plans' CTA to become clickable", te);
        } catch (Exception e) {
            logger.error("❌ Error while clicking 'Explore Plans' CTA: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to click 'Explore Plans' CTA", e);
        }
    }

    /**
     * Waits for the Pricing page to load by checking that the URL contains
     * the expected fragment "/grc/subscriptions/pricing".
     *
     * @return true if the URL contains the expected fragment within the timeout, false otherwise
     */
    public boolean isPricingPageUrlLoaded() {
        logger.info("🔁 Waiting for Pricing page URL to be loaded (contains '/grc/subscriptions/pricing')...");

        String expectedFragment = "/grc/subscriptions/pricing";

        try {
            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(20));

            boolean matched = webDriverWait.until(ExpectedConditions.urlContains(expectedFragment));

            String currentUrl = "";
            try {
                currentUrl = driver.getCurrentUrl();
            } catch (Exception ignored) {
            }

            logger.info("🟢 Pricing page URL check -> matched={}, currentUrl='{}'", Boolean.valueOf(matched), currentUrl);

            return matched;

        } catch (TimeoutException te) {
            String currentUrl = "";
            try {
                currentUrl = driver.getCurrentUrl();
            } catch (Exception ignored) {
            }
            logger.error("❌ Pricing page URL did NOT contain '{}' within timeout. Current URL='{}'. Error={}", expectedFragment, currentUrl, te.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("❌ Error while waiting for Pricing page URL: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Verifies that the "Launch-Ready Plans" heading is visible on the Pricing page.
     *
     * @return true if the heading is visible, false otherwise
     */
    public boolean isLaunchReadyPlansHeadingVisible() {
        logger.info("🔍 Checking if 'Launch-Ready Plans' heading is visible on Pricing page...");

        try {
            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(15));

            logger.info("⏳ Waiting for 'Launch-Ready Plans' heading element to become visible...");
            webDriverWait.until(ExpectedConditions.visibilityOf(launchReadyPlansHeading));

            boolean displayed;
            try {
                displayed = launchReadyPlansHeading.isDisplayed();
            } catch (Exception e) {
                logger.warn("⚠️ Exception while calling isDisplayed() on 'Launch-Ready Plans' heading: {}", e.getMessage());
                displayed = false;
            }

            String headingText = "";
            try {
                headingText = launchReadyPlansHeading.getText();
            } catch (Exception ignored) {
            }
            if (headingText == null) {
                headingText = "";
            }
            headingText = headingText.trim();

            if (displayed) {
                logger.info("🟢 'Launch-Ready Plans' heading is visible. Text='{}'", headingText);
            } else {
                logger.warn("⚠️ 'Launch-Ready Plans' heading element was found but isDisplayed() returned false. Text='{}'", headingText);
            }

            return displayed;

        } catch (TimeoutException te) {
            logger.error("❌ 'Launch-Ready Plans' heading did NOT become visible within timeout: {}", te.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("❌ Error while checking 'Launch-Ready Plans' heading visibility: {}", e.getMessage(), e);
            return false;
        }
    }


    // Title: "Wait! Before you go..."
    @FindBy(xpath = "//div[contains(@class,'styles_modal') or contains(@class,'fixed top-0 left-0')]//p[contains(normalize-space(),'Wait! Before you go')]")
    private WebElement exitPopupTitle;

    // Optional: whole popup container (nice for safety checks)
    @FindBy(xpath = "//div[contains(@class,'styles_modal') or contains(@class,'fixed top-0 left-0')]//p[contains(normalize-space(),'Wait! Before you go')]/ancestor::div[contains(@class,'bg-white')]")
    private WebElement exitPopupContainer;


    /**
     * Waits for 'Wait! Before you go...' exit popup.
     * Primary wait = 60s
     * Grace retry = 30s (6 cycles * 5s)
     * Total max wait = 90s
     */
    public boolean waitForExitPopupVisible() {
        logger.info("⏳ Waiting for mandatory exit popup 'Wait! Before you go...' on Pricing page...");

        Instant start = Instant.now();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

        try {
            // -------- PRIMARY WAIT (up to 60s) --------
            logger.info("🕒 Primary wait (60s) started...");
            WebElement title = wait.until(ExpectedConditions.visibilityOf(exitPopupTitle));

            if (isDisplayedSafely(title)) {
                long elapsed = Duration.between(start, Instant.now()).toMillis();
                logger.info("🟢 Exit popup appeared within {} ms (~{} s)", elapsed, elapsed / 1000.0);
                return true;
            }

        } catch (Exception first) {
            logger.warn("⚠ Primary 60s wait did NOT detect exit popup. Trying grace retries...");
        }

        // -------- GRACE PERIOD RETRY LOOP --------
        final int GRACE_ATTEMPTS = 6;  // 6 retries * 5s = 30s extra
        final int SLEEP_MS = 5000;
        int attempt = 0;

        while (attempt < GRACE_ATTEMPTS) {
            attempt++;
            logger.info("🔁 Grace retry attempt {} of {}...", attempt, GRACE_ATTEMPTS);

            try {
                if (isDisplayedSafely(exitPopupTitle)) {
                    long elapsed = Duration.between(start, Instant.now()).toMillis();
                    logger.info("🟢 Exit popup appeared during grace retry at {} ms (~{} s)", elapsed, elapsed / 1000.0);
                    return true;
                }
            } catch (Exception ignored) {
            }

            try {
                Thread.sleep(SLEEP_MS);
            } catch (InterruptedException ignored) {
            }
        }

        // -------- FINAL FAILURE --------
        long totalWait = Duration.between(start, Instant.now()).toMillis();
        logger.error("❌ Exit popup did NOT appear even after {} ms (~{} s). Failing test.", totalWait, totalWait / 1000.0);

        return false;
    }


    /**
     * Returns the text of the exit popup title (for assertion/logging).
     */
    public String getExitPopupTitleText() {
        try {
            String text = exitPopupTitle.getText().trim();
            logger.info("📖 Exit popup title text = '{}'", text);
            return text;
        } catch (Exception e) {
            logger.error("❌ Failed to read exit popup title text: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to read 'Wait! Before you go...' popup title", e);
        }
    }


    /**
     * STEP 1: SELECT RANDOM EXIT REASON
     * <p>
     * Picks a random option from the exit survey list and clicks it using safeClick.
     * Logs the list size, chosen index, and text. Fails fast if the list is empty.
     *
     * @return the trimmed text of the selected option
     */
    public String clickRandomSurveyOption() {
        logger.info("📝 Attempting to select a random exit survey option...");

        int count = exitSurveyOptions.size();
        logger.info("📊 Exit survey options found = {}", Integer.valueOf(count));

        if (count == 0) {
            logger.error("❌ Exit survey options list is empty. Cannot proceed with random selection.");
            throw new RuntimeException("Exit survey options list is empty.");
        }

        int index = new Random().nextInt(count);
        WebElement option = exitSurveyOptions.get(index);

        String rawText;
        try {
            rawText = option.getText();
        } catch (Exception e) {
            logger.warn("⚠️ Failed to read text from selected option at index {}: {}", Integer.valueOf(index), e.getMessage());
            rawText = "";
        }

        String selected = rawText == null ? "" : rawText.trim();

        logger.info("🎯 Random exit survey option chosen -> index={}, text='{}'", Integer.valueOf(index), selected);

        commonMethods.safeClick(driver, option, "Exit Survey Option: " + selected, 30);
        logger.info("✅ Clicked exit survey option: '{}'", selected);

        return selected;
    }

    /**
     * STEP 2: CLICK CONTINUE AFTER SELECTING AN OPTION
     * <p>
     * Waits for the Continue button in the exit survey popup and clicks it.
     * Uses safeClick to handle overlap / JS issues.
     */
    public void clickSurveyContinue() {
        logger.info("➡ Preparing to click 'Continue' button in Exit Survey popup...");

        try {
            wait.waitForVisibility(continueBtn);
            logger.info("🟢 'Continue' button is visible. Attempting safeClick...");
            commonMethods.safeClick(driver, continueBtn, "Continue button", 20);
            logger.info("✅ Clicked 'Continue' in Exit Survey popup.");
        } catch (Exception e) {
            logger.error("❌ Failed to click 'Continue' in Exit Survey popup: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to click 'Continue' in Exit Survey popup", e);
        }
    }


    /**
     * Waits for the "Thanks for Sharing!" popup title to be visible
     * and returns true if it is displayed.
     *
     * @return true if the Thanks popup title is visible, false otherwise
     */
    public boolean isThanksPopupVisible() {
        logger.info("⏳ Waiting for 'Thanks for Sharing!' popup to become visible...");

        try {
            wait.waitForVisibility(thanksPopupTitle);
            boolean visible = isDisplayedSafely(thanksPopupTitle);
            logger.info("🟢 'Thanks for Sharing!' popup visibility = {}", Boolean.valueOf(visible));
            return visible;
        } catch (Exception e) {
            logger.error("❌ 'Thanks for Sharing!' popup did NOT become visible: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Returns the trimmed title text of the "Thanks for Sharing!" popup.
     *
     * @return popup title text, or empty string on failure
     */
    public String getThanksPopupTitleText() {
        logger.info("🔎 Reading 'Thanks for Sharing!' popup title text...");
        try {
            String text = thanksPopupTitle.getText();
            String trimmed = text == null ? "" : text.trim();
            logger.info("📄 'Thanks for Sharing!' title text = '{}'", trimmed);
            return trimmed;
        } catch (Exception e) {
            logger.error("❌ Failed to read 'Thanks for Sharing!' popup title: {}", e.getMessage());
            return "";
        }
    }

    /* ========================================================= */
    /*   NEW FLOW: EXPERT QUESTIONS POPUP                        */
    /* ========================================================= */

    /**
     * Checks whether the "Get All Your Questions Answered" popup is visible.
     *
     * @return true if visible, false otherwise
     */
    public boolean isExpertQuestionsPopupVisible() {
        logger.info("⏳ Waiting for 'Get All Your Questions Answered' popup to become visible...");

        try {
            wait.waitForVisibility(expertQuestionsPopupTitle);
            boolean visible = isDisplayedSafely(expertQuestionsPopupTitle);
            logger.info("🟢 Expert Questions popup visibility = {}", Boolean.valueOf(visible));
            return visible;
        } catch (Exception e) {
            logger.error("❌ Expert Questions popup did NOT become visible: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Returns the title text of the "Get All Your Questions Answered" popup.
     *
     * @return popup title text, or empty string on failure
     */
    public String getExpertQuestionsPopupTitleText() {
        logger.info("🔎 Reading 'Get All Your Questions Answered' popup title text...");
        try {
            String text = expertQuestionsPopupTitle.getText();
            String trimmed = text == null ? "" : text.trim();
            logger.info("📄 Expert Questions popup title text = '{}'", trimmed);
            return trimmed;
        } catch (Exception e) {
            logger.error("❌ Failed to read Expert Questions popup title: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Clicks the "Request Callback" button on the Expert Questions popup.
     */
    public void clickRequestCallback() {
        logger.info("📞 Preparing to click 'Request Callback' on Expert Questions popup...");

        try {
            wait.waitForVisibility(requestCallbackBtn);
            logger.info("🟢 'Request Callback' button is visible. Attempting safeClick...");
            commonMethods.safeClick(driver, requestCallbackBtn, "Request Callback button", 20);
            logger.info("✅ Clicked 'Request Callback' on Expert Questions popup.");
        } catch (Exception e) {
            logger.error("❌ Failed to click 'Request Callback' button: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to click 'Request Callback' button", e);
        }
    }


    /**
     * Checks whether the "Request Received" popup is visible.
     *
     * @return true if visible, false otherwise
     */
    public boolean isRequestReceivedPopupVisible() {
        logger.info("⏳ Waiting for 'Request Received' popup to become visible...");

        try {
            wait.waitForVisibility(requestReceivedTitle);
            boolean visible = isDisplayedSafely(requestReceivedTitle);
            logger.info("🟢 'Request Received' popup visibility = {}", Boolean.valueOf(visible));
            return visible;
        } catch (Exception e) {
            logger.error("❌ 'Request Received' popup did NOT become visible: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Returns the title text of the "Request Received" popup.
     *
     * @return popup title text, or empty string on failure
     */
    public String getRequestReceivedTitleText() {
        logger.info("🔎 Reading 'Request Received' popup title text...");
        try {
            String text = requestReceivedTitle.getText();
            String trimmed = text == null ? "" : text.trim();
            logger.info("📄 'Request Received' popup title text = '{}'", trimmed);
            return trimmed;
        } catch (Exception e) {
            logger.error("❌ Failed to read 'Request Received' popup title: {}", e.getMessage());
            return "";
        }
    }


    /**
     * Refreshes the Pricing page after completing any exit popup flow.
     * Invokes a generic page-load wait afterwards.
     */
    public void refreshPricingPageAfterThanksPopup() {
        logger.info("🔄 Refreshing Pricing page after exit popup flow...");
        try {
            driver.navigate().refresh();
            waitForPageToLoad();
            logger.info("✅ Pricing page refresh completed and page load detected.");
        } catch (Exception e) {
            logger.error("❌ Error while refreshing Pricing page: {}", e.getMessage(), e);
            // Let it bubble up or swallow depending on your preference
            throw new RuntimeException("Failed to refresh Pricing page after exit popup flow", e);
        }
    }

    /* ========================================================= */
    /*   GENERIC BACK NAVIGATION / PAGE LOAD                     */
    /* ========================================================= */

    /**
     * Navigates one step back in the browser history from a named page,
     * then waits for the document.readyState to be 'complete'.
     *
     * @param pageName logical name of the current page for logging (e.g., "Pricing", "Report an Issue")
     */
    public void navigateBack(String pageName) {
        if (pageName == null) {
            pageName = "Unknown Page";
        }

        String urlBefore = "";
        try {
            urlBefore = driver.getCurrentUrl();
        } catch (Exception ignored) {
        }

        logger.info("🔙 Navigating back from {} (URL before back='{}')", pageName, urlBefore);

        try {
            driver.navigate().back();
            waitForPageToLoad();

            String urlAfter = "";
            try {
                urlAfter = driver.getCurrentUrl();
            } catch (Exception ignored) {
            }

            logger.info("⬅️ Successfully navigated back from {}. URL after back='{}'", pageName, urlAfter);
        } catch (Exception e) {
            logger.error("❌ Failed during navigateBack from {}: {}", pageName, e.getMessage(), e);
            throw new RuntimeException("Failed to navigate back from " + pageName, e);
        }
    }

    /**
     * Waits until the document.readyState is 'complete'.
     * Classic ExpectedCondition (no Java 8 lambdas).
     */
    public void waitForPageToLoad() {
        logger.info("⏳ Waiting for page to reach readyState='complete'...");

        try {
            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver webDriver) {
                    try {
                        Object result = ((JavascriptExecutor) webDriver).executeScript("return document.readyState");
                        String state = result == null ? "" : result.toString();
                        logger.debug("🔍 Current document.readyState = '{}'", state);
                        return "complete".equalsIgnoreCase(state);
                    } catch (Exception e) {
                        logger.warn("⚠️ Exception while checking document.readyState: {}", e.getMessage());
                        return Boolean.FALSE;
                    }
                }
            });
            logger.info("✅ Page readyState is 'complete'.");
        } catch (TimeoutException te) {
            logger.error("❌ Page did NOT reach readyState='complete' within the timeout: {}", te.getMessage());
        } catch (Exception e) {
            logger.error("❌ Error while waiting for page load: {}", e.getMessage(), e);
        }
    }

    /**
     * Specific helper used from the flow where two back navigations are required
     * from Pricing (e.g., Pricing -> Compliances -> some previous page).
     * <p>
     * This keeps the existing approach (two back() calls) but adds logging
     * and page load waits.
     */
    public void navigateBackFromPricing() {
        String urlBefore = "";
        try {
            urlBefore = driver.getCurrentUrl();
        } catch (Exception ignored) {
        }

        logger.info("🔁 [POM] Navigating back from Pricing using browser back() twice. URL before='{}'", urlBefore);

        try {
            // First back
            driver.navigate().back();
            waitForPageToLoad();

            String urlAfterFirst = "";
            try {
                urlAfterFirst = driver.getCurrentUrl();
            } catch (Exception ignored) {
            }
            logger.info("⬅️ [POM] After first back() from Pricing. URL='{}'", urlAfterFirst);

            // Second back
            driver.navigate().back();
            waitForPageToLoad();

            String urlAfterSecond = "";
            try {
                urlAfterSecond = driver.getCurrentUrl();
            } catch (Exception ignored) {
            }
            logger.info("⬅️ [POM] After second back() from Pricing. Final URL='{}'", urlAfterSecond);

        } catch (Exception e) {
            logger.error("❌ Error while performing double back navigation from Pricing: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to navigate back from Pricing (double back)", e);
        }
    }


    /**
     * Safe helper to read current URL (never throws).
     * Adds logging + basic sanity checks.
     */
    private String safeGetCurrentUrl() {
        try {
            String url = driver.getCurrentUrl();

            if (url == null) {
                logger.warn("🌐 [POM] safeGetCurrentUrl() -> WebDriver returned NULL URL.");
                return "";
            }

            url = url.trim();

            if (url.isEmpty()) {
                logger.warn("🌐 [POM] safeGetCurrentUrl() -> WebDriver returned EMPTY URL.");
                return "";
            }

            // Optional: basic sanity check
            if (!url.startsWith("http")) {
                logger.warn("🌐 [POM] safeGetCurrentUrl() -> Suspicious URL value: '{}'", url);
            } else {
                logger.info("🌐 [POM] safeGetCurrentUrl() -> '{}'", url);
            }

            return url;
        } catch (org.openqa.selenium.WebDriverException wde) {
            logger.error("❌ [POM] WebDriverException while reading current URL: {}", wde.getMessage(), wde);
            return "";
        } catch (Exception e) {
            logger.error("❌ [POM] Unexpected exception while reading current URL: {}", e.getMessage(), e);
            return "";
        }
    }


    /**
     * Returns one random pincode from the fixed list.
     * Adds validation + logging for better traceability.
     */
    private String getRandomPincode() {
        String[] pincodes = new String[]{"600078", "600018", "600078", "600002", "600090", "600113"};

        // Fallback in case of misconfiguration
        String fallbackPincode = "600090";

        if (pincodes == null || pincodes.length == 0) {
            logger.error("❌ [POM] getRandomPincode() -> No pincodes configured. Falling back to '{}'", fallbackPincode);
            return fallbackPincode;
        }

        // Pick random index safely
        Random r = new Random();
        int idx = r.nextInt(pincodes.length);
        String chosen = pincodes[idx];

        // Basic format validation: 6-digit numeric
        if (chosen == null || !chosen.matches("\\d{6}")) {
            logger.warn("⚠️ [POM] getRandomPincode() -> Chosen pincode '{}' is invalid. Falling back to '{}'", chosen, fallbackPincode);
            chosen = fallbackPincode;
        }

        logger.info("📍 [POM] getRandomPincode() -> Selected test pincode='{}' from {} configured values", chosen, pincodes.length);

        return chosen;
    }


    public boolean isStillConfusedPopupVisible() {
        logger.info("🔍 Checking for 'Still Confused?' popup visibility...");

        // default for this check
        lastStillConfusedPopupVisible = false;

        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            shortWait.until(ExpectedConditions.visibilityOf(stillConfusedTitle));

            boolean titleVisible = false;
            boolean containerVisible = false;

            try {
                titleVisible = stillConfusedTitle.isDisplayed();
            } catch (Exception ignored) {
            }

            try {
                if (stillConfusedContainer != null) {
                    containerVisible = stillConfusedContainer.isDisplayed();
                }
            } catch (Exception ignored) {
            }

            logger.info("🟢 'Still Confused?' title visible = {}, container visible = {}", Boolean.valueOf(titleVisible), Boolean.valueOf(containerVisible));

            lastStillConfusedPopupVisible = titleVisible && containerVisible;
            logger.info("📊 [POM-STATE] lastStillConfusedPopupVisible = {}", lastStillConfusedPopupVisible);

            return lastStillConfusedPopupVisible;

        } catch (Exception e) {
            logger.info("ℹ️ 'Still Confused?' popup not visible (or not present): {}", e.getMessage());
            lastStillConfusedPopupVisible = false;
            logger.info("📊 [POM-STATE] lastStillConfusedPopupVisible = false (exception path)");
            return false;
        }
    }


    public void fillStillConfusedPopupAndSubmit() {
        logger.info("✍️ Filling 'Still Confused?' popup lead form...");

        try {
            lastStillConfusedLeadSubmitted = false; // reset for this run

            // Ensure popup is visible
            wait.waitForVisibility(stillConfusedContainer);

            // Email using Faker
            String email = faker.internet().emailAddress();
            wait.waitForVisibility(stillConfusedEmailInput);
            stillConfusedEmailInput.clear();
            stillConfusedEmailInput.sendKeys(email);
            logger.info("📧 Still Confused ? email entered: {}", email);

            // Mobile number (Faker India mobile-style, normalized to start 7/8/9)
            String mobile = faker.phoneNumber().subscriberNumber(10);
            char first = mobile.charAt(0);
            if (first < '7') {
                mobile = "9" + mobile.substring(1);
            }

            wait.waitForVisibility(stillConfusedMobileInput);
            stillConfusedMobileInput.clear();
            stillConfusedMobileInput.sendKeys(mobile);
            logger.info("📱 Still Confused ? mobile entered: {}", mobile);

            // Pin code (using your controlled list)
            String pincode = getRandomPincode();
            wait.waitForVisibility(stillConfusedCityPincodeInput);

            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", stillConfusedCityPincodeInput);
            } catch (Exception ignored) {
            }

            try {
                stillConfusedCityPincodeInput.click();
            } catch (Exception ignored) {
            }

            stillConfusedCityPincodeInput.clear();
            stillConfusedCityPincodeInput.sendKeys(pincode);
            Thread.sleep(4000);
            stillConfusedCityPincodeInput.sendKeys(Keys.ENTER);

            logger.info("📍 Still Confused ? pincode entered: {}", pincode);

            // Click CTA
            wait.waitForVisibility(talkToRegistrationExpertBtn);
            commonMethods.safeClick(driver, talkToRegistrationExpertBtn, "'Talk to registration expert' CTA", 20);

            lastStillConfusedLeadSubmitted = true;
            logger.info("🚀 Submitted lead form in 'Still Confused?' popup");
            logger.info("📊 [POM-STATE] lastStillConfusedLeadSubmitted = true");

        } catch (Exception e) {
            lastStillConfusedLeadSubmitted = false;
            logger.error("❌ Error while filling 'Still Confused?' popup: {}", e.getMessage(), e);
            logger.info("📊 [POM-STATE] lastStillConfusedLeadSubmitted = false (exception path)");
            throw new RuntimeException("Failed to fill 'Still Confused?' popup", e);
        }
    }


    public boolean waitForPaymentPageLoaded(Duration timeout) {
        logger.info("⏳ Waiting for Payment page ('Choose Payment Method') to load...");

        lastPaymentPageReached = false;

        try {
            WebDriverWait w = new WebDriverWait(driver, timeout);
            w.until(ExpectedConditions.visibilityOf(choosePaymentMethodHeading));

            boolean visible = false;
            try {
                visible = choosePaymentMethodHeading.isDisplayed();
            } catch (Exception ignored) {
            }

            String url = safeGetCurrentUrl();
            lastPaymentPageReached = visible;
            logger.info("💳 Payment page loaded = {}, URL='{}'", Boolean.valueOf(visible), url);
            logger.info("📊 [POM-STATE] lastPaymentPageReached = {}", lastPaymentPageReached);
            recordNavUrl("PaymentPageLoaded", url);

            return visible;
        } catch (TimeoutException te) {
            logger.error("❌ Payment page did not load within {} ms.", timeout.toMillis());
            lastPaymentPageReached = false;
            logger.info("📊 [POM-STATE] lastPaymentPageReached = false (timeout)");
            return false;
        } catch (Exception e) {
            logger.error("❌ Error while waiting for Payment page: {}", e.getMessage());
            lastPaymentPageReached = false;
            logger.info("📊 [POM-STATE] lastPaymentPageReached = false (exception path)");
            return false;
        }
    }


    /**
     * Navigates BACK from the current flow to the previous page (expected Home).
     * <p>
     * Behaviour:
     * 1) browser.back() once.
     * - if "Still Confused?" popup appears -> fill lead, go to Payment,
     * then back() twice and stop (Home will be validated in step).
     * 2) If popup did NOT appear:
     * browser.back() again.
     * - if popup appears now -> same as above.
     * - else -> stop; caller will check for Home directly.
     * <p>
     * All logging and error handling happens here; final Home validation
     * stays in the step via HomePage.waitForHomeLoaded().
     */
    public void navigateBackToPreviousPage() {
        resetBackNavigationDebugState();
        String initialUrl = safeGetCurrentUrl();
        logger.info("🔁 [POM] Starting navigateBackToPreviousPage. Initial URL='{}'", initialUrl);

        try {
            // -------- FIRST BACK --------
            logger.info("⬅️ [POM] Performing first back()...");
            driver.navigate().back();
            waitForPageToLoad();

            String urlAfterFirst = safeGetCurrentUrl();
            logger.info("🌐 [POM] After first back(). URL='{}'", urlAfterFirst);
            recordNavUrl("AfterFirstBack", urlAfterFirst);

            if (isStillConfusedPopupVisible()) {
                logger.info("🧩 [POM] 'Still Confused?' popup detected after first back.");
                handleStillConfusedFlowThenDoubleBackToHome();
                return;
            }

            logger.info("ℹ️ [POM] No 'Still Confused?' popup after first back. Performing second back()...");

            // -------- SECOND BACK --------
            driver.navigate().back();
            waitForPageToLoad();

            String urlAfterSecond = safeGetCurrentUrl();
            logger.info("🌐 [POM] After second back(). URL='{}'", urlAfterSecond);
            recordNavUrl("AfterSecondBack", urlAfterSecond);

            if (isStillConfusedPopupVisible()) {
                logger.info("🧩 [POM] 'Still Confused?' popup detected after second back.");
                handleStillConfusedFlowThenDoubleBackToHome();
            } else {
                logger.info("ℹ️ [POM] 'Still Confused?' popup not shown after second back. " + "Expecting browser to be on/near Home – final check will be done in step.");
            }

        } catch (Exception e) {
            logger.error("❌ Error during navigateBackToPreviousPage flow: {}", e.getMessage(), e);
            throw new RuntimeException("Failed during navigateBackToPreviousPage complex back flow", e);
        }
    }


    /**
     * Internal helper:
     * - assumes 'Still Confused?' popup is visible,
     * - fills the popup and submits,
     * - waits for Payment,
     * - then performs back() calls to escape Company Registration,
     * - if still stuck on Company Registration, force navigate to GRC dashboard,
     * - final Home assertion is done in the calling step.
     */
    private void handleStillConfusedFlowThenDoubleBackToHome() {
        logger.info("🔧 [POM] handleStillConfusedFlowThenDoubleBackToHome START");

        String currentUrl = safeGetCurrentUrl();
        recordNavUrl("StillConfused_Start", currentUrl);

        // 1) Fill and submit popup
        logger.info("✍️ [POM] Filling & submitting 'Still Confused?' popup lead form...");
        lastStillConfusedLeadSubmitted = false;

        fillStillConfusedPopupAndSubmit();

        if (!lastStillConfusedLeadSubmitted) {
            logger.warn("⚠️ [POM] Lead form submission flag is FALSE right after fillStillConfusedPopupAndSubmit.");
        } else {
            logger.info("✅ [POM] Lead form submission flag is TRUE.");
        }

        // 2) Wait for Payment page
        logger.info("⏳ [POM] Waiting for Payment page after 'Talk to registration expert' click...");
        lastPaymentPageReached = false;
        boolean paymentLoaded = waitForPaymentPageLoaded(Duration.ofSeconds(30));
        lastPaymentPageReached = paymentLoaded;

        currentUrl = safeGetCurrentUrl();
        recordNavUrl("After_StillConfused_Submit", currentUrl);

        if (!paymentLoaded) {
            logger.error("❌ [POM] Payment page did not load after 'Talk to registration expert' click. Current URL='{}'", currentUrl);
            logger.info("📊 [POM-STATE] lastPaymentPageReached = false");
            throw new RuntimeException("Payment page not loaded after Still Confused popup submission");
        }

        logger.info("✅ [POM] Payment page detected successfully. Current URL='{}'", currentUrl);
        logger.info("📊 [POM-STATE] lastPaymentPageReached = true");

        // 3) From Payment → try to go back up to 3 times
        logger.info("💳 [POM] Starting back navigation from Payment. Current URL='{}'", currentUrl);
        recordNavUrl("Payment_StartBackFlow", currentUrl);

        for (int i = 1; i <= 3; i++) {
            logger.info("⬅️ [POM] Back navigation from Payment: performing back() attempt {}...", i);
            driver.navigate().back();
            waitForPageToLoad();

            currentUrl = safeGetCurrentUrl();
            logger.info("🌐 [POM] URL after back attempt {} = '{}'", i, currentUrl);
            recordNavUrl("Payment_BackAttempt_" + i, currentUrl);

            if (currentUrl.contains("grc.vakilsearch.com/grc/dashboard")) {
                logger.info("🏠 [POM] GRC Home/Dashboard reached after {} back() attempts.", i);
                break;
            }

            if (!currentUrl.contains("vakilsearch.com/company-registration")) {
                logger.info("ℹ️ [POM] Left Company Registration page after {} back() attempts. " + "Current URL='{}'. Final Home validation will be done in step.", i, currentUrl);
                break;
            }
        }

        // 4) Fallback
        if (currentUrl.contains("vakilsearch.com/company-registration")) {
            logger.warn("⚠️ [POM] Still on Company Registration after back() attempts. " + "Falling back to direct navigation to GRC dashboard.");

            String dashboardUrl = "https://grc.vakilsearch.com/grc/dashboard";
            logger.info("➡️ [POM] Forcing navigation to GRC dashboard: '{}'", dashboardUrl);

            driver.get(dashboardUrl);
            waitForPageToLoad();

            currentUrl = safeGetCurrentUrl();
            recordNavUrl("Forced_Dashboard_Nav", currentUrl);

            logger.info("✅ [POM] Forced navigation to GRC dashboard completed. Current URL='{}'", currentUrl);
        }

        recordNavUrl("StillConfused_Flow_End", currentUrl);
        logger.info("🏁 [POM] Back navigation after Still Confused + Payment flow done. " + "Final Home validation will be performed in the step. Final URL='{}'", currentUrl);
    }


    /**
     * Convenience: record any URL transitions during the complex flow.
     *
     * @param context human-readable description of the transition
     * @param url     URL that should be stored for trace / debugging
     */
    private void recordNavUrl(String context, String url) {
        if (url == null) url = "";
        String entry = context + " -> " + url;
        lastBackNavigationUrls.add(entry);
        logger.info("🧭 [POM-TRACE] {}", entry);
    }

    /**
     * Safely clicks the Notification icon in the header.
     * <p>
     * Behaviour:
     * <ul>
     *     <li>Logs the current URL before the click.</li>
     *     <li>Performs a safe click using {@code commonMethods.safeClick}.</li>
     *     <li>Logs and records the URL after the click for navigation trace.</li>
     *     <li>Logs warnings if the element reference is null or not displayed.</li>
     * </ul>
     * No assertions or reporting are performed in this layer.
     */
    public void clickNotificationIcon() {
        logger.info("🔔 Attempting to click Notification icon...");

        if (notificationIcon == null) {
            logger.error("❌ Notification icon WebElement is null. Cannot perform click.");
            return;
        }

        String beforeUrl = "";
        try {
            beforeUrl = driver.getCurrentUrl();
        } catch (Exception e) {
            logger.warn("⚠️ Unable to read current URL before Notification icon click: {}", e.getMessage());
        }
        recordNavUrl("Before Notification icon click", beforeUrl);

        try {
            boolean displayed = false;
            try {
                displayed = notificationIcon.isDisplayed();
            } catch (Exception e) {
                logger.warn("⚠️ Exception while checking Notification icon visibility: {}", e.getMessage());
            }
            logger.info("📌 Notification icon displayed before click: {}", displayed);

            commonMethods.safeClick(driver, notificationIcon, "Notification Icon", 10);
            logger.info("✅ Notification icon clicked successfully.");

        } catch (Exception e) {
            logger.error("❌ Exception while clicking Notification icon: {}", e.getMessage(), e);
        }

        String afterUrl = "";
        try {
            afterUrl = driver.getCurrentUrl();
        } catch (Exception e) {
            logger.warn("⚠️ Unable to read current URL after Notification icon click: {}", e.getMessage());
        }
        recordNavUrl("After Notification icon click", afterUrl);
    }

    /**
     * Checks whether the 'Notifications' popup header is present and visible.
     *
     * @return {@code true} if the header WebElement is found and {@link WebElement#isDisplayed()} returns true;
     * {@code false} otherwise.
     */
    public boolean isNotificationPopupDisplayed() {
        boolean visible = false;
        String text = "";

        try {
            if (notificationPopupHeader == null) {
                logger.warn("⚠️ Notification popup header WebElement is null.");
            } else {
                try {
                    visible = notificationPopupHeader.isDisplayed();
                } catch (Exception e) {
                    logger.warn("⚠️ Exception while checking Notification popup visibility: {}", e.getMessage());
                }

                try {
                    text = notificationPopupHeader.getText();
                } catch (Exception ignore) {
                }
                if (text == null) {
                    text = "";
                }
                text = text.trim();
            }
        } catch (Exception e) {
            logger.error("❌ Unexpected error while checking Notification popup visibility: {}", e.getMessage(), e);
        }

        logger.info("📌 Notification Popup visible: {}, text='{}'", visible, text);
        return visible;
    }

    /**
     * Safely clicks the Cart icon in the header.
     * <p>
     * Behaviour:
     * <ul>
     *     <li>Logs current URL before the click.</li>
     *     <li>Performs a safe click via {@code commonMethods.safeClick}.</li>
     *     <li>Logs and records URL after the click.</li>
     *     <li>Logs warnings when element is null or not displayed.</li>
     * </ul>
     */
    public void clickCartIcon() {
        logger.info("🛒 Attempting to click Cart icon...");

        if (cartIcon == null) {
            logger.error("❌ Cart icon WebElement is null. Cannot perform click.");
            return;
        }

        String beforeUrl = "";
        try {
            beforeUrl = driver.getCurrentUrl();
        } catch (Exception e) {
            logger.warn("⚠️ Unable to read current URL before Cart icon click: {}", e.getMessage());
        }
        recordNavUrl("Before Cart icon click", beforeUrl);

        try {
            boolean displayed = false;
            try {
                displayed = cartIcon.isDisplayed();
            } catch (Exception e) {
                logger.warn("⚠️ Exception while checking Cart icon visibility: {}", e.getMessage());
            }
            logger.info("📌 Cart icon displayed before click: {}", displayed);

            commonMethods.safeClick(driver, cartIcon, "Cart Icon", 10);
            logger.info("✅ Cart icon clicked successfully.");

        } catch (Exception e) {
            logger.error("❌ Exception while clicking Cart icon: {}", e.getMessage(), e);
        }

        String afterUrl = "";
        try {
            afterUrl = driver.getCurrentUrl();
        } catch (Exception e) {
            logger.warn("⚠️ Unable to read current URL after Cart icon click: {}", e.getMessage());
        }
        recordNavUrl("After Cart icon click", afterUrl);
    }

    /**
     * Checks whether the Cart popup header is displayed.
     *
     * @return {@code true} if the Cart popup header is displayed; {@code false} otherwise.
     */
    public boolean isCartPopupDisplayed() {
        boolean visible = false;
        String text = "";

        try {
            if (cartPopupHeader == null) {
                logger.warn("⚠️ Cart popup header WebElement is null.");
            } else {
                try {
                    visible = cartPopupHeader.isDisplayed();
                } catch (Exception e) {
                    logger.warn("⚠️ Exception while checking Cart popup visibility: {}", e.getMessage());
                }

                try {
                    text = cartPopupHeader.getText();
                } catch (Exception ignore) {
                }
                if (text == null) {
                    text = "";
                }
                text = text.trim();
            }
        } catch (Exception e) {
            logger.error("❌ Unexpected error while checking Cart popup visibility: {}", e.getMessage(), e);
        }

        logger.info("📌 Cart Popup visible: {}, text='{}'", visible, text);
        return visible;
    }

    /**
     * Checks whether the Zero-State header is present on the Subscription page.
     * <p>
     * This indicates that the user currently has no active subscription plan.
     *
     * @return {@code true} if the header is present and visible; {@code false} otherwise.
     */
    public boolean isZeroStateHeaderDisplayed() {
        boolean present = false;
        String text = "";

        try {
            if (zeroStateHeader == null) {
                logger.warn("⚠️ Zero-State header WebElement is null.");
            } else {
                try {
                    present = zeroStateHeader.isDisplayed();
                } catch (Exception e) {
                    logger.warn("⚠️ Exception while checking Zero-State header visibility: {}", e.getMessage());
                }

                try {
                    text = zeroStateHeader.getText();
                } catch (Exception ignore) {
                }
                if (text == null) {
                    text = "";
                }
                text = text.trim();
            }
        } catch (Exception e) {
            logger.error("❌ Unexpected error while checking Zero-State header visibility: {}", e.getMessage(), e);
        }

        logger.info("📌 Zero-State Header Visible: {}, text='{}'", present, text);
        return present;
    }

    /**
     * Checks if the special offer text for new businesses is present on the Zero-State Subscription page.
     * <p>
     * Example text:
     * <pre>
     *     "Special 10% offer only for new businesses like yours."
     * </pre>
     *
     * @return {@code true} if the special offer text element is present and visible; {@code false} otherwise.
     */
    public boolean isSpecialOfferDisplayed() {
        boolean present = false;
        String text = "";

        try {
            if (specialOfferText == null) {
                logger.warn("⚠️ Special Offer text WebElement is null.");
            } else {
                try {
                    present = specialOfferText.isDisplayed();
                } catch (Exception e) {
                    logger.warn("⚠️ Exception while checking Special Offer visibility: {}", e.getMessage());
                }

                try {
                    text = specialOfferText.getText();
                } catch (Exception ignore) {
                }
                if (text == null) {
                    text = "";
                }
                text = text.trim();
            }
        } catch (Exception e) {
            logger.error("❌ Unexpected error while checking Special Offer text visibility: {}", e.getMessage(), e);
        }

        logger.info("🎁 Special Offer Text Visible: {}, text='{}'", present, text);
        return present;
    }

    /**
     * Clicks the "Explore Plans & Claim Offer" CTA on the Zero-State Subscription page.
     * <p>
     * Behaviour:
     * <ul>
     *     <li>Logs URL before the click.</li>
     *     <li>Performs a safe click using {@code commonMethods.safeClick}.</li>
     *     <li>Records and logs URL after the click for navigation tracing.</li>
     *     <li>Logs null/visibility issues instead of throwing assertions.</li>
     * </ul>
     */
    public void clickExplorePlansCTA() {
        logger.info("🖱 Attempting to click 'Explore Plans & Claim Offer' CTA...");

        if (explorePlansButton == null) {
            logger.error("❌ 'Explore Plans & Claim Offer' CTA WebElement is null. Cannot perform click.");
            return;
        }

        String beforeUrl = "";
        try {
            beforeUrl = driver.getCurrentUrl();
        } catch (Exception e) {
            logger.warn("⚠️ Unable to read current URL before Explore Plans CTA click: {}", e.getMessage());
        }
        recordNavUrl("Before Explore Plans CTA click", beforeUrl);

        try {
            boolean displayed = false;
            try {
                displayed = explorePlansButton.isDisplayed();
            } catch (Exception e) {
                logger.warn("⚠️ Exception while checking Explore Plans CTA visibility: {}", e.getMessage());
            }
            logger.info("📌 'Explore Plans & Claim Offer' CTA displayed before click: {}", displayed);

            commonMethods.safeClick(driver, explorePlansButton, "Explore Plans & Claim Offer CTA", 10);
            logger.info("✅ 'Explore Plans & Claim Offer' CTA clicked successfully.");

        } catch (Exception e) {
            logger.error("❌ Exception while clicking 'Explore Plans & Claim Offer' CTA: {}", e.getMessage(), e);
        }

        String afterUrl = "";
        try {
            afterUrl = driver.getCurrentUrl();
        } catch (Exception e) {
            logger.warn("⚠️ Unable to read current URL after Explore Plans CTA click: {}", e.getMessage());
        }
        recordNavUrl("After Explore Plans CTA click", afterUrl);
    }


    /**
     * Checks whether the Notification icon is present and visible in the header.
     *
     * @return {@code true} if the Notification icon WebElement is non-null and displayed; {@code false} otherwise.
     */
    public boolean isNotificationIconVisible() {
        boolean visible = false;

        try {
            if (notificationIcon == null) {
                logger.warn("⚠️ Notification icon WebElement is null. Cannot verify visibility.");
            } else {
                try {
                    visible = notificationIcon.isDisplayed();
                } catch (Exception e) {
                    logger.warn("⚠️ Exception while checking Notification icon visibility: {}", e.getMessage());
                }

                String altText = "";
                try {
                    altText = notificationIcon.getAttribute("title");
                } catch (Exception ignore) {
                }
                if (altText == null) {
                    altText = "";
                }
                altText = altText.trim();

                logger.info("📌 Notification icon visible: {}, title='{}'", Boolean.valueOf(visible), altText);
            }
        } catch (Exception e) {
            logger.error("❌ Unexpected error while checking Notification icon visibility: {}", e.getMessage(), e);
        }

        return visible;
    }

    /**
     * Checks whether the Cart icon is present and visible in the header.
     *
     * @return {@code true} if the Cart icon WebElement is non-null and displayed; {@code false} otherwise.
     */
    public boolean isCartIconVisible() {
        boolean visible = false;

        try {
            if (cartIcon == null) {
                logger.warn("⚠️ Cart icon WebElement is null. Cannot verify visibility.");
            } else {
                try {
                    visible = cartIcon.isDisplayed();
                } catch (Exception e) {
                    logger.warn("⚠️ Exception while checking Cart icon visibility: {}", e.getMessage());
                }

                String title = "";
                try {
                    title = cartIcon.getAttribute("title");
                } catch (Exception ignore) {
                }
                if (title == null) {
                    title = "";
                }
                title = title.trim();

                logger.info("📌 Cart icon visible: {}, title='{}'", Boolean.valueOf(visible), title);
            }
        } catch (Exception e) {
            logger.error("❌ Unexpected error while checking Cart icon visibility: {}", e.getMessage(), e);
        }

        return visible;
    }

    /**
     * Checks whether the "Explore Plans & Claim Offer" CTA button is present and visible
     * on the Zero-State Subscription page.
     *
     * @return {@code true} if the CTA WebElement is non-null and displayed; {@code false} otherwise.
     */
    public boolean isExplorePlansCTAVisible() {
        boolean visible = false;
        String text = "";

        try {
            if (explorePlansButton == null) {
                logger.warn("⚠️ 'Explore Plans & Claim Offer' CTA WebElement is null. Cannot verify visibility.");
            } else {
                try {
                    visible = explorePlansButton.isDisplayed();
                } catch (Exception e) {
                    logger.warn("⚠️ Exception while checking 'Explore Plans & Claim Offer' CTA visibility: {}", e.getMessage());
                }

                try {
                    text = explorePlansButton.getText();
                } catch (Exception ignore) {
                }
                if (text == null) {
                    text = "";
                }
                text = text.trim();

                logger.info("📌 'Explore Plans & Claim Offer' CTA visible: {}, text='{}'", Boolean.valueOf(visible), text);
            }
        } catch (Exception e) {
            logger.error("❌ Unexpected error while checking 'Explore Plans & Claim Offer' CTA visibility: {}", e.getMessage(), e);
        }

        return visible;
    }


}










