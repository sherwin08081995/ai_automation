package stepDefinitions;

import com.github.javafaker.Faker;
import hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.*;
import utils.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static utils.AllureLoggerUtils.logToAllure;

/**
 * @author Sherwin
 * @since 19-11-2025
 */

public class SignUpValidationSteps {

    WebDriver driver = Hooks.driver;
    SignUpPage signUpPage;
    Logger logger;
    WebDriverWait wait;
    LoginPage loginPage;
    AllureLoggerUtils allureLogging;
    ReusableCommonMethods commonMethods;
    private Instant signUpNavigateStart;
    private Instant loginWithPasswordNavigateStart;
    private Instant loginClickStart;
    private String generatedPassword;
    private String generatedEmail;
    private Instant loginNavigateStart;
    private Instant serviceHubNavigateStart;
    HomePage homePage;
    private Instant startYourBusinessNavigateStart;
    Faker faker;
    private String onboardingFullName;
    private String onboardingDesignation;
    private String onboardingEntityType;
    private String onboardingIdentityVariant;

    private String companyDetailsVariant;
    private String companyOrTradeOrFirmOrSocietyName;
    private String companyDateValue;
    private String companyState;
    private String companyIndustry;

    private String teamSizeValue;
    private String annualTurnoverValue;

    // store values across steps
    private Instant getStartedClickTime;
    private int compliancesFoundCount;


    public SignUpValidationSteps() {
        this.driver = Hooks.driver;
        this.faker = new Faker();
        this.signUpPage = new SignUpPage(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.loginPage = new LoginPage(driver);
        this.homePage = new HomePage(driver);
        this.logger = LoggerUtils.getLogger(getClass());
        this.commonMethods = new ReusableCommonMethods(driver);
    }

    @Step("{message}")
    public void logStep(String message) {
        logger.info(message);
        logToAllure("Step", message);
    }

    private String formatDurationPretty(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        long msPart = millis % 1000;

        if (minutes > 0) {
            return String.format("%d min %d.%03d s", minutes, seconds, msPart);
        } else {
            return String.format("%d.%03d s", seconds, msPart);
        }
    }

    private long logStepDurationToAllure(String stepName, Instant start, Instant end) {
        long millis = Duration.between(start, end).toMillis();
        String pretty = formatDurationPretty(millis);

        String msg = stepName + " completed in <b>" + pretty + "</b> (" + millis + " ms).";
        logToAllure(stepName + " - Timing", msg);
        logger.info("⏱ {} completed in {} ms ({})", stepName, millis, pretty);
        return millis;
    }


    @Given("the user is currently on the Login page")
    public void the_user_is_currently_on_the_login_page() {
        Instant loginNavigateStart = null;

        try {
            logStep("🌐 Launching Login page & validating subtitle...");

            // Start navigation + time measurement
            loginNavigateStart = Instant.now();
            NavContext.start("Open Login Page");

            String baseUrl = ConfigReader.get("baseUrl");
            logger.info("Navigating to Login URL: {}", baseUrl);

            // Navigate to Login page
            driver.get(baseUrl);
            ScreenshotUtils.attachScreenshotToAllure(driver, "LoginPage_After_Navigation");

            // ➤ Validate subtitle visibility
            boolean subtitleVisible = signUpPage.isLoginSubtitleDisplayed();
            logger.info("Login subtitle visible = {}", subtitleVisible);

            Assert.assertTrue(subtitleVisible, "Login subtitle is NOT displayed!");

            // ➤ Validate subtitle text
            String actualSubtitle = signUpPage.getLoginSubtitleText();
            String expectedSubtitle = "Log into your account";

            logger.info("Expected Subtitle = '{}', Actual Subtitle = '{}'", expectedSubtitle, actualSubtitle);

            Assert.assertEquals(actualSubtitle.trim(), expectedSubtitle.trim(), "Login subtitle text mismatched!");

            // ➤ Additional URL validation
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("/login") || currentUrl.contains("/signin"), "Unexpected Login URL: " + currentUrl);

            // ➤ Load time tracking
            long elapsedMs = commonMethods.logLoadTimeAndReturnMs("Open Login Page", loginNavigateStart, ReusableCommonMethods.LOGIN_WARN_MS, ReusableCommonMethods.LOGIN_FAIL_MS);

            if (elapsedMs > ReusableCommonMethods.LOGIN_FAIL_MS) {
                String msg = "Login page load exceeded FAIL threshold: " + (elapsedMs / 1000.0) + " sec";
                logger.error(msg);
                logToAllure("❌ Login Load Failure", msg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "LoginPage_LoadTime_Fail");
                Assert.fail(msg);
            }

            if (elapsedMs > ReusableCommonMethods.LOGIN_WARN_MS) {
                String msg = "Login page load exceeded WARNING threshold: " + (elapsedMs / 1000.0) + " sec";
                logger.warn(msg);
                logToAllure("⚠️ Login Load Warning", msg);
            }

            logToAllure("✅ Login Page Validated", "URL: " + currentUrl + "<br>Subtitle: " + actualSubtitle + "<br>Load Time: " + (elapsedMs / 1000.0) + " sec");

            ScreenshotUtils.attachScreenshotToAllure(driver, "LoginPage_Validation_Success");

        } catch (Throwable t) {
            // Use your existing reusable handler
            signUpPage.handleValidationException("Login Page Launch + Subtitle Validation", t);
        }
    }


    @When("the user clicks on the {string} link on the login page")
    public void the_user_clicks_on_the_link_on_the_login_page(String linkText) {
        try {
            logStep("🖱 Clicking on '" + linkText + "' link...");
            signUpNavigateStart = Instant.now();
            NavContext.start("Open Sign Up Page");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_Click_SignUp_Link");

            // Try clicking the link
            signUpPage.clickSignUpLink();

            // Validate page loaded
            boolean loaded = signUpPage.waitForSignUpPageToLoad(Duration.ofSeconds(20));

            logger.info("Sign Up page load result = {}", loaded);
            Assert.assertTrue(loaded, "Sign Up page did not load correctly after clicking the link.");

            // Measure timings
            long elapsedMs = commonMethods.logLoadTimeAndReturnMs("Open Sign Up Page", signUpNavigateStart, ReusableCommonMethods.LOGIN_WARN_MS, ReusableCommonMethods.LOGIN_FAIL_MS);

            // Check timing thresholds
            if (elapsedMs > ReusableCommonMethods.LOGIN_FAIL_MS) {
                String msg = "Opening Sign Up page exceeded fail limit: " + (elapsedMs / 1000.0) + " seconds";
                logToAllure("❌ Load Time Failure", msg);
                logger.error(msg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "SignUp_Navigation_FailTime");
                Assert.fail(msg);
            }

            if (elapsedMs > ReusableCommonMethods.LOGIN_WARN_MS) {
                String msg = "Opening Sign Up page exceeded warning limit: " + (elapsedMs / 1000.0) + " seconds";
                logger.warn(msg);
                logToAllure("⚠️ Load Time Warning", msg);
            }

            ScreenshotUtils.attachScreenshotToAllure(driver, "SignUp_Navigation_Success");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "SignUp_Navigation_Exception");
            signUpPage.handleValidationException("Sign Up page navigation", e);
        }
    }

    @Then("the user should redirect to signup page")
    public void the_user_should_redirect_to_signup_page() {
        try {
            logStep("🔎 Validating that user is redirected to Sign Up page...");

            String expectedHeader = "Create an account";

            String actualHeader = signUpPage.getHeaderText();
            logger.info("Header found after redirect = '{}'", actualHeader);

            Assert.assertEquals(actualHeader.trim(), expectedHeader.trim(), "User is NOT on the Sign Up page after redirect!");

            String currentUrl = driver.getCurrentUrl();
            logger.info("Current URL after supposed redirect = {}", currentUrl);

            logToAllure("✅ Redirect to Sign Up Validation", "Expected header: " + expectedHeader + "<br>Actual header: " + actualHeader + "<br>URL: " + currentUrl);

            ScreenshotUtils.attachScreenshotToAllure(driver, "Redirected_To_SignUp_Page");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "SignUp_Redirect_Exception");
            signUpPage.handleValidationException("Redirect to Sign Up page validation", e);
        }
    }


    @Then("the Sign Up form should contain fields:")
    public void the_sign_up_form_should_contain_fields(DataTable table) {
        try {
            List<String> expectedFields = table.asList();
            logStep("🧾 Validating form fields: " + expectedFields);

            List<String> actualFields = signUpPage.getFormFieldLabels();
            logger.info("Fields found in UI: {}", actualFields);

            for (int i = 0; i < expectedFields.size(); i++) {
                String expected = expectedFields.get(i).trim();
                boolean found = false;

                for (int j = 0; j < actualFields.size(); j++) {
                    String UIlabel = actualFields.get(j).trim();
                    if (UIlabel.equalsIgnoreCase(expected)) {
                        found = true;
                        break;
                    }
                }

                Assert.assertTrue(found, "Field missing on Sign Up form: " + expected);
            }

            logToAllure("✅ Field Validation", "All form fields present");
            ScreenshotUtils.attachScreenshotToAllure(driver, "SignUp_Fields_Validated");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "SignUp_FieldCheck_Exception");
            signUpPage.handleValidationException("Sign Up form fields validation", e);
        }
    }

    @Then("the \"Sign Up\" button should be enabled by default")
    public void the_sign_up_button_should_be_enabled_by_default() {
        try {
            logStep("🔘 Checking if 'Sign Up' button is enabled...");

            boolean enabled = signUpPage.isSignUpButtonEnabled();
            logger.info("Sign Up button enabled = {}", enabled);

            Assert.assertTrue(enabled, "'Sign Up' button is NOT enabled by default!");

            logToAllure("✅ Button Validation", "Sign Up button is enabled by default");
            ScreenshotUtils.attachScreenshotToAllure(driver, "SignUp_Button_Enabled");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "SignUp_Button_Exception");
            signUpPage.handleValidationException("Sign Up button state validation", e);
        }
    }


    @When("the user enters random valid full name in Full name field")
    public void the_user_enters_random_valid_full_name_in_full_name_field() {
        try {
            logStep("✍️ Entering random valid Full Name...");

            Faker faker = new Faker();
            String fullName = faker.name().fullName();

            Assert.assertNotNull(fullName, "Generated full name is null");
            Assert.assertTrue(fullName.trim().length() >= 3, "Generated full name seems too short: " + fullName);

            signUpPage.typeFullName(fullName);

            logger.info("Generated Full Name = {}", fullName);
            logToAllure("Full Name Entered", fullName);
            ScreenshotUtils.attachScreenshotToAllure(driver, "FullName_Entered");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "FullName_Exception");
            signUpPage.handleValidationException("Enter random valid full name", e);
        }
    }

    @When("the user enters random valid email in Email address field")
    public void the_user_enters_random_valid_email_in_email_address_field() {
        try {
            logStep("✍️ Entering random valid Email...");

            String email = faker.internet().emailAddress();

            Assert.assertNotNull(email, "Generated email is null");
            Assert.assertTrue(email.contains("@"), "Generated email does not contain '@': " + email);

            generatedEmail = email;

            signUpPage.typeEmail(email);

            logger.info("Generated Email = {}", email);
            logToAllure("Email Entered", email);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Email_Entered");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Email_Exception");
            signUpPage.handleValidationException("Enter random valid email", e);
        }
    }

    @When("the user enters random valid phone number in Phone number field")
    public void the_user_enters_random_valid_phone_number_in_phone_number_field() {
        try {
            logStep("📱 Entering random valid Phone Number...");

            String phone = signUpPage.generateValidIndianMobile();

            Assert.assertNotNull(phone, "Generated phone number is null");
            Assert.assertTrue(phone.trim().length() >= 10, "Generated phone number seems too short: " + phone);

            signUpPage.typePhone(phone);

            logger.info("Generated Phone Number = {}", phone);
            logToAllure("Phone Number Entered", phone);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Phone_Entered");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Phone_Exception");
            signUpPage.handleValidationException("Enter random valid phone number", e);
        }
    }

    @When("the user enters random valid password in Password field")
    public void the_user_enters_random_valid_password_in_password_field() {
        try {
            logStep("🔐 Entering random valid Password...");

            generatedPassword = signUpPage.generateStrongPassword(12);

            Assert.assertNotNull(generatedPassword, "Generated password is null");
            Assert.assertTrue(generatedPassword.length() >= 8, "Generated password length < 8");

            signUpPage.typePassword(generatedPassword);

            logger.info("Generated Password = {}", generatedPassword);
            logToAllure("Password Entered", generatedPassword);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Password_Entered");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Password_Exception");
            signUpPage.handleValidationException("Enter random valid password", e);
        }
    }

    @When("the user enters the same password in Confirm password field")
    public void the_user_enters_the_same_password_in_confirm_password_field() {
        try {
            logStep("🔁 Re-entering same Password in Confirm Password field...");

            Assert.assertNotNull(generatedPassword, "Generated password is null for confirm password step");

            signUpPage.typeConfirmPassword(generatedPassword);

            logger.info("Confirm Password = {}", generatedPassword);
            logToAllure("Confirm Password Entered", generatedPassword);
            ScreenshotUtils.attachScreenshotToAllure(driver, "ConfirmPassword_Entered");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ConfirmPassword_Exception");
            signUpPage.handleValidationException("Enter same password in confirm password field", e);
        }
    }

    @When("the user clicks on the \"Sign Up\" button")
    public void the_user_clicks_on_the_sign_up_button() {
        try {
            logStep("🖱 Clicking on Sign Up button...");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_SignUp_Click");
            signUpPage.clickSignUpButton();
            ScreenshotUtils.attachScreenshotToAllure(driver, "SignUp_Clicked");

            logToAllure("Sign Up Click", "Clicked on Sign Up button");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "SignUp_Click_Exception");
            signUpPage.handleValidationException("Click on Sign Up button", e);
        }
    }

    @Then("the account should be created successfully")
    public void the_account_should_be_created_successfully() {
        try {
            logStep("🎉 Verifying account creation success...");

            boolean success = signUpPage.isAccountCreatedSuccessMessageVisible();
            logger.info("Account creation success message visibility = {}", success);

            Assert.assertTrue(success, "Account creation success message NOT visible!");

            logToAllure("✅ Account Creation", "Account created successfully and success banner is visible");
            ScreenshotUtils.attachScreenshotToAllure(driver, "AccountCreation_Success");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "AccountCreation_Exception");
            signUpPage.handleValidationException("Account creation success validation", e);
        }
    }

    @Then("the user should be redirected to the login page")
    public void the_user_should_be_redirected_to_the_login_page() {
        try {
            logStep("↩ Verifying user redirected to Login page...");

            String currentUrl = driver.getCurrentUrl();
            logger.info("URL after Sign Up = {}", currentUrl);

            boolean redirected = currentUrl.contains("/signin");
            Assert.assertTrue(redirected, "User was NOT redirected to login page!");

            logToAllure("✅ Redirect to Login", "User redirected to login URL: " + currentUrl);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Redirected_LoginPage");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Redirect_Failed_Exception");
            signUpPage.handleValidationException("Redirect to login page after Sign Up", e);
        }
    }

    @Then("the Login page header should be {string}")
    public void the_login_page_header_should_be(String expectedHeader) {
        try {
            logStep("🔎 Validating Login page header: " + expectedHeader);

            String actualHeader = signUpPage.getLoginHeaderText();
            logger.info("Login header found = '{}'", actualHeader);

            Assert.assertEquals(actualHeader.trim(), expectedHeader.trim(), "Login page header mismatch!");

            logToAllure("✅ Login Header Validation", "Expected: " + expectedHeader + "<br>Actual: " + actualHeader);

            ScreenshotUtils.attachScreenshotToAllure(driver, "Login_Header_Validated");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Login_Header_Exception");
            signUpPage.handleValidationException("Login page header validation", e);
        }
    }

    @When("the user clicks on the Login with Password link on the login page")
    public void the_user_clicks_on_the_login_with_password_link_on_the_login_page() throws InterruptedException {
        try {
            logStep("🖱 Clicking on 'Login with Password' link...");

            loginWithPasswordNavigateStart = Instant.now();
            NavContext.start("Open Login With Password Form");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_LoginWithPassword_Click");

            signUpPage.clickLoginWithPassword();
            Thread.sleep(5000);
            signUpPage.clickLoginWithPassword();

            signUpPage.waitForLoginWithPasswordForm(Duration.ofSeconds(10));

            long elapsedMs = commonMethods.logLoadTimeAndReturnMs("Open Login With Password Form", loginWithPasswordNavigateStart, ReusableCommonMethods.LOGIN_WARN_MS, ReusableCommonMethods.LOGIN_FAIL_MS);

            if (elapsedMs > ReusableCommonMethods.LOGIN_FAIL_MS) {
                String msg = "Opening Login With Password form exceeded fail limit: " + (elapsedMs / 1000.0) + " seconds";
                logger.error(msg);
                logToAllure("❌ Load Time Failure", msg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "LoginWithPassword_LoadTime_Fail");
                Assert.fail(msg);
            }

            if (elapsedMs > ReusableCommonMethods.LOGIN_WARN_MS) {
                String msg = "Opening Login With Password form exceeded warning limit: " + (elapsedMs / 1000.0) + " seconds";
                logger.warn(msg);
                logToAllure("⚠️ Load Time Warning", msg);
            }

            logToAllure("Login With Password", "Login with password form opened successfully");
            ScreenshotUtils.attachScreenshotToAllure(driver, "LoginWithPassword_Clicked");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "LoginWithPassword_Click_Exception");
            signUpPage.handleValidationException("Click 'Login with Password' link", e);
        }
    }


    @When("the user enters the registered email in Login email field")
    public void the_user_enters_the_registered_email_in_login_email_field() {
        try {
            logStep("✍️ Entering previously registered email into Login page...");

            Assert.assertNotNull(generatedEmail, "Generated email is null. Sign Up email may not have been stored.");

            signUpPage.typeLoginEmail(generatedEmail);

            logger.info("Using registered Email for login = {}", generatedEmail);
            logToAllure("Login Email Entered", generatedEmail);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Login_Email_Entered");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Login_Email_Exception");
            signUpPage.handleValidationException("Enter registered email in login email field", e);
        }
    }

    @When("the user enters the registered password in Login password field")
    public void the_user_enters_the_registered_password_in_login_password_field() {
        try {
            logStep("🔐 Entering previously registered password into Login page...");

            Assert.assertNotNull(generatedPassword, "Generated password is null. Sign Up password may not have been stored.");

            signUpPage.typeLoginPassword(generatedPassword);

            logger.info("Using registered Password for login = {}", generatedPassword);
            logToAllure("Login Password Entered", generatedPassword);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Login_Password_Entered");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Login_Password_Exception");
            signUpPage.handleValidationException("Enter registered password in login password field", e);
        }
    }

    @When("the user clicks on the Log In button")
    public void the_user_clicks_on_the_log_in_button() {
        try {
            logStep("🖱 Clicking on 'Log In' button...");

            loginClickStart = Instant.now();
            NavContext.start("Post Login Home Load");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_LoginButton_Click");
            signUpPage.clickLogInButton();
            ScreenshotUtils.attachScreenshotToAllure(driver, "Login_Button_Clicked");

            logToAllure("Login Button Click", "Clicked on Log In button");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Login_Button_Click_Exception");
            signUpPage.handleValidationException("Click on Log In button", e);
        }
    }

    @Then("the user should be logged in successfully")
    public void the_user_should_be_logged_in_successfully() {
        try {
            logStep("✅ Verifying user is logged in successfully...");

            boolean loggedIn = signUpPage.waitForPostLoginHome(Duration.ofSeconds(20));
            logger.info("Post login home visible = {}", loggedIn);

            Assert.assertTrue(loggedIn, "User was NOT logged in successfully!");

            if (loginClickStart != null) {
                long elapsedMs = commonMethods.logLoadTimeAndReturnMs("Post Login Home Load", loginClickStart, ReusableCommonMethods.LOGIN_WARN_MS, ReusableCommonMethods.LOGIN_FAIL_MS);

                if (elapsedMs > ReusableCommonMethods.LOGIN_FAIL_MS) {
                    String msg = "Post Login home load exceeded fail limit: " + (elapsedMs / 1000.0) + " seconds";
                    logger.error(msg);
                    logToAllure("❌ Post Login Load Time Failure", msg);
                    ScreenshotUtils.attachScreenshotToAllure(driver, "PostLogin_LoadTime_Fail");
                    Assert.fail(msg);
                }

                if (elapsedMs > ReusableCommonMethods.LOGIN_WARN_MS) {
                    String msg = "Post Login home load exceeded warning limit: " + (elapsedMs / 1000.0) + " seconds";
                    logger.warn(msg);
                    logToAllure("⚠️ Post Login Load Time Warning", msg);
                }
            } else {
                logger.warn("loginClickStart is null. Skipping login load time measurement.");
                logToAllure("ℹ️ Load Time Skipped", "loginClickStart was null, load time not measured");
            }

            logToAllure("✅ Login Success", "User logged in and home page visible");
            ScreenshotUtils.attachScreenshotToAllure(driver, "PostLogin_Home");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Login_Verification_Exception");
            signUpPage.handleValidationException("Login success verification", e);
        }
    }


    @Then("the complete profile popup should be visible with correct content")
    public void the_complete_profile_popup_should_be_visible_with_correct_content() {
        try {
            logStep("🔎 Validating 'Complete Your Profile and Unlock Special Offer!' popup...");

            boolean visible = signUpPage.isCompleteProfilePopupVisible();
            Assert.assertTrue(visible, "Complete Profile popup is NOT visible after login.");

            String popupText = signUpPage.getCompleteProfilePopupText();
            logToAllure("Complete Profile Popup Content", popupText == null ? "No text captured" : popupText.replace("\n", "<br>"));

            logger.info("Popup content captured for 'Complete Your Profile': {}", popupText);
            ScreenshotUtils.attachScreenshotToAllure(driver, "CompleteProfile_Popup_Visible");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "CompleteProfile_Popup_Exception");
            signUpPage.handleValidationException("Complete Profile popup presence & content validation", e);
        }
    }

    @When("the user clicks on the complete profile popup CTA")
    public void the_user_clicks_on_the_complete_profile_popup_cta() {
        try {
            logStep("🖱 Clicking on 'Complete Business Profile & Claim Offer' CTA in popup...");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_CompleteProfile_CTA_Click");
            signUpPage.clickCompleteProfilePopupButton();
            ScreenshotUtils.attachScreenshotToAllure(driver, "After_CompleteProfile_CTA_Click");

            logToAllure("Complete Profile CTA Click", "Clicked on 'Complete Business Profile & Claim Offer'");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "CompleteProfile_CTA_Click_Exception");
            signUpPage.handleValidationException("Click Complete Profile popup CTA", e);
        }
    }

    @Then("the welcome usage selection page should be displayed")
    public void the_welcome_usage_selection_page_should_be_displayed() {
        try {
            logStep("🔎 Validating Welcome usage selection page (/grc/welcome)...");

            boolean ok = signUpPage.waitForWelcomeUsagePage(Duration.ofSeconds(20));
            Assert.assertTrue(ok, "Welcome usage page (/grc/welcome) NOT visible or incomplete.");

            String url = driver.getCurrentUrl();
            String question = signUpPage.getWelcomeUsageQuestionText();

            logToAllure("Welcome Usage Page Validation", "URL: " + url + "<br>Question: " + question + "<br>Expected to contain: 'How do you plan to use Zolvit 360?'");

            logger.info("Welcome page URL = {}", url);
            logger.info("Welcome page question = {}", question);

            ScreenshotUtils.attachScreenshotToAllure(driver, "WelcomeUsage_Page_Visible");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "WelcomeUsage_Page_Exception");
            signUpPage.handleValidationException("Welcome usage selection page validation", e);
        }
    }

    @When("the user clicks on skip to dashboard on the welcome page")
    public void the_user_clicks_on_skip_to_dashboard_on_the_welcome_page() {
        try {
            logStep("🖱 Clicking 'Skip to dashboard' on Welcome page...");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_SkipToDashboard_Click");
            signUpPage.clickSkipToDashboard();
            ScreenshotUtils.attachScreenshotToAllure(driver, "After_SkipToDashboard_Click");

            logToAllure("Skip to Dashboard Click", "Clicked on 'Skip to dashboard' from Welcome page");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "SkipToDashboard_Click_Exception");
            signUpPage.handleValidationException("Click 'Skip to dashboard' on Welcome page", e);
        }
    }

    @Then("the dashboard home with festive offer popup should be displayed")
    public void the_dashboard_home_with_festive_offer_popup_should_be_displayed() {
        try {
            logStep("🔎 Validating dashboard home with 'Extra 10% Off - Festive Sale!' popup...");

            boolean ok = signUpPage.waitForDashboardWithFestiveOffer(Duration.ofSeconds(20));
            Assert.assertTrue(ok, "Dashboard with 'Extra 10% Off - Festive Sale!' popup NOT visible.");

            String url = driver.getCurrentUrl();
            String festiveTitle = signUpPage.getFestiveOfferTitleText();

            logToAllure("Festive Offer Popup Validation", "URL: " + url + "<br>Popup Title: " + festiveTitle + "<br>Expected to contain: 'Extra 10% Off - Festive Sale!'");

            logger.info("Dashboard URL after Skip = {}", url);
            logger.info("Festive popup title = {}", festiveTitle);

            ScreenshotUtils.attachScreenshotToAllure(driver, "Dashboard_FestiveOffer_Visible");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Dashboard_FestiveOffer_Exception");
            signUpPage.handleValidationException("Dashboard festive offer popup validation", e);
        }
    }


    @Then("the festive offer popup should show {string} CTA")
    public void the_festive_offer_popup_should_show_cta(String expectedCtaText) {
        try {
            logStep("🎉 Validating Festive popup contains CTA: '" + expectedCtaText + "'...");

            boolean visible = signUpPage.isExploreServiceHubCtaVisible();
            logger.info("'Explore Service Hub' CTA visible = {}", visible);
            Assert.assertTrue(visible, "'Explore Service Hub' CTA is NOT visible on Festive popup!");

            String actualText = signUpPage.getExploreServiceHubCtaText();
            logger.info("Expected CTA text='{}', actual='{}'", expectedCtaText, actualText);

            Assert.assertEquals(actualText.trim(), expectedCtaText.trim(), "CTA text on Festive popup does not match!");

            logToAllure("✅ Festive Popup CTA Validation", "Expected CTA: " + expectedCtaText + "<br>Actual CTA: " + actualText);

            ScreenshotUtils.attachScreenshotToAllure(driver, "FestivePopup_ExploreServiceHub_CTA");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "FestivePopup_ExploreServiceHub_CTA_Exception");
            signUpPage.handleValidationException("Festive popup 'Explore Service Hub' CTA validation", t);
        }
    }


    @When("the user clicks on the {string} CTA on the festive popup")
    public void the_user_clicks_on_the_cta_on_the_festive_popup(String ctaText) {
        try {
            logStep("🖱 Clicking on Festive popup CTA: '" + ctaText + "'...");

            serviceHubNavigateStart = Instant.now();
            NavContext.start("Open Service Hub From Festive Popup");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_ExploreServiceHub_Click");

            signUpPage.clickExploreServiceHubCta();

            ScreenshotUtils.attachScreenshotToAllure(driver, "After_ExploreServiceHub_Click");

            logToAllure("Festive Popup CTA Click", "Clicked on CTA: " + ctaText);

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ExploreServiceHub_Click_Exception");
            signUpPage.handleValidationException("Click 'Explore Service Hub' CTA on Festive popup", t);
        }
    }


    @Then("the Service Hub page should be displayed with URL containing {string}")
    public void the_service_hub_page_should_be_displayed_with_url_containing(String expectedUrlFragment) {
        try {
            logStep("🔎 Validating Service Hub page is displayed with URL containing '" + expectedUrlFragment + "'...");

            boolean hubLoaded = signUpPage.waitForServiceHubPage(Duration.ofSeconds(20));
            logger.info("Service Hub page load result = {}", hubLoaded);
            Assert.assertTrue(hubLoaded, "Service Hub page did NOT load correctly after clicking CTA!");

            String currentUrl = driver.getCurrentUrl();
            logger.info("Service Hub current URL = {}", currentUrl);
            Assert.assertTrue(currentUrl.contains(expectedUrlFragment), "Service Hub URL does not contain expected fragment: " + expectedUrlFragment);

            long elapsedMs = 0L;
            if (serviceHubNavigateStart != null) {
                elapsedMs = commonMethods.logLoadTimeAndReturnMs("Open Service Hub From Festive Popup", serviceHubNavigateStart, ReusableCommonMethods.LOGIN_WARN_MS, ReusableCommonMethods.LOGIN_FAIL_MS);

                if (elapsedMs > ReusableCommonMethods.LOGIN_FAIL_MS) {
                    String msg = "Service Hub load exceeded FAIL threshold: " + (elapsedMs / 1000.0) + " sec";
                    logger.error(msg);
                    logToAllure("❌ Service Hub Load Failure", msg);
                    ScreenshotUtils.attachScreenshotToAllure(driver, "ServiceHub_LoadTime_Fail");
                    Assert.fail(msg);
                }

                if (elapsedMs > ReusableCommonMethods.LOGIN_WARN_MS) {
                    String msg = "Service Hub load exceeded WARNING threshold: " + (elapsedMs / 1000.0) + " sec";
                    logger.warn(msg);
                    logToAllure("⚠️ Service Hub Load Warning", msg);
                }
            } else {
                logger.warn("serviceHubNavigateStart is null; skipping load-time measurement for Service Hub.");
                logToAllure("ℹ️ Service Hub Load Time Skipped", "serviceHubNavigateStart was null.");
            }

            logToAllure("✅ Service Hub Page Validated", "URL: " + currentUrl + "<br>Expected fragment: " + expectedUrlFragment + "<br>Load Time: " + (elapsedMs / 1000.0) + " sec");

            ScreenshotUtils.attachScreenshotToAllure(driver, "ServiceHub_Page_Validated");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ServiceHub_Page_Exception");
            signUpPage.handleValidationException("Service Hub page & URL validation", t);
        }
    }


    @Given("the user navigate back to Home page")
    public void the_user_navigate_back_home_page() {
        try {
            logStep("🔍 Navigating and confirming that the user is on the Home page...");
            // 1) Start timers BEFORE the click (captures click → nav → render)
            Instant navStart = Instant.now();
            NavContext.start("Home");

            // 2) Click Home
            homePage.clickHomeTab();

            // 3) Wait up to NAV_FAIL_MS for the Home page to be "loaded" (config-driven)
            boolean success = homePage.waitForHomeLoaded(Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS));

            // 4) Stop & log timing via reusable helper (defaults to NAV thresholds 12s/20s)
            long elapsedMs = commonMethods.logLoadTimeAndReturnMs("Home", navStart);
            double elapsedSec = elapsedMs / 1000.0;

            // 5) Threshold handling for NAV (warn ≥ NAV_WARN_MS, fail ≥ NAV_FAIL_MS)
            if (success) {
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("Home took %.2f s — more than %d s. Failing (SLA %ds).", elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Failure", failMsg);
                    Assert.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("Home took %.2f s — more than %d s.", elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                }
            } else {
                String failMsg = String.format("Unable to load Home within %d s (actual: %.2f s).", ReusableCommonMethods.NAV_FAIL_MS / 1000, elapsedSec);
                logger.error(failMsg);
                logToAllure("❌ Access Failure", failMsg);
                Assert.fail(failMsg);
            }

            // 6) Final assertions + artifacts
            Assert.assertTrue(success, "❌ User is not on the Home page.");
            logToAllure("🏠 Home Page Loaded", String.valueOf(success));
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "HomePage_Confirmation");
            logger.info("✅ Home page successfully confirmed.");

        } catch (Throwable t) {
            signUpPage.handleValidationException("Home page confirmation", t);
        }
    }


    @When("the user clicks on the {string} CTA on the dashboard")
    public void the_user_clicks_on_the_cta_on_the_dashboard(String ctaText) {
        try {
            logStep("🖱 Clicking dashboard CTA: '" + ctaText + "' (Start Your Business)...");

            startYourBusinessNavigateStart = Instant.now();
            NavContext.start("Open Company Registration From Dashboard");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_StartYourBusiness_Click");

            signUpPage.clickStartYourBusinessCtaAndSwitchToNewTab();

            ScreenshotUtils.attachScreenshotToAllure(driver, "After_StartYourBusiness_Click");

            logToAllure("Dashboard CTA Click", "Clicked on '" + ctaText + "' CTA from Home dashboard");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "StartYourBusiness_Click_Exception");
            signUpPage.handleValidationException("Click 'Start Your Business' CTA on dashboard", t);
        }
    }


    @Then("the Company Registration page should load successfully")
    public void the_company_registration_page_should_load_successfully() {
        try {
            logStep("🔎 Validating Company Registration page (URL + Header)...");

            boolean pageLoaded = signUpPage.waitForCompanyRegistrationPage(Duration.ofSeconds(20));
            logger.info("Company Registration page load result = {}", pageLoaded);
            Assert.assertTrue(pageLoaded, "Company Registration page did NOT load correctly!");

            String currentUrl = driver.getCurrentUrl();
            logger.info("Company Registration current URL = {}", currentUrl);

            String expectedUrlFragment = "vakilsearch.com/company-registration";
            Assert.assertTrue(currentUrl.contains(expectedUrlFragment), "URL does NOT contain expected fragment: " + expectedUrlFragment);

            String expectedHeader = "Company Registration Online in India";
            String actualHeader = signUpPage.getCompanyRegistrationHeaderText();
            logger.info("Expected header='{}', actual='{}'", expectedHeader, actualHeader);

            Assert.assertTrue(actualHeader.contains(expectedHeader), "Header mismatch! Expected to contain: " + expectedHeader);

            long elapsedMs = 0L;
            if (startYourBusinessNavigateStart != null) {
                elapsedMs = commonMethods.logLoadTimeAndReturnMs("Open Company Registration From Dashboard", startYourBusinessNavigateStart, ReusableCommonMethods.LOGIN_WARN_MS, ReusableCommonMethods.LOGIN_FAIL_MS);

                if (elapsedMs > ReusableCommonMethods.LOGIN_FAIL_MS) {
                    String msg = "Company Registration load EXCEEDED FAIL threshold: " + (elapsedMs / 1000.0) + " sec";
                    logger.error(msg);
                    logToAllure("❌ Company Registration Load Failure", msg);
                    ScreenshotUtils.attachScreenshotToAllure(driver, "CompanyRegistration_Load_Fail");
                    Assert.fail(msg);
                }

                if (elapsedMs > ReusableCommonMethods.LOGIN_WARN_MS) {
                    String msg = "Company Registration load EXCEEDED WARNING threshold: " + (elapsedMs / 1000.0) + " sec";
                    logger.warn(msg);
                    logToAllure("⚠️ Company Registration Load Warning", msg);
                }
            } else {
                logger.warn("startYourBusinessNavigateStart is null; skipping load-time measurement.");
                logToAllure("ℹ️ Load Time Skipped", "startYourBusinessNavigateStart was null.");
            }

            logToAllure("✅ Company Registration Page Validated", "URL: " + currentUrl + "<br>Header: " + actualHeader + "<br>Load Time: " + (elapsedMs / 1000.0) + " sec");

            ScreenshotUtils.attachScreenshotToAllure(driver, "CompanyRegistration_Page_Validated");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "CompanyRegistration_Exception");
            signUpPage.handleValidationException("Company Registration Page Validation", t);
        }
    }


    @Then("the user navigates back to the previous page which is home page")
    public void the_user_navigates_back_to_the_previous_page() {
        try {
            logStep("↩️ Navigating back to Home page (handling 'Still Confused?' popup and Payment redirect if needed)...");

            // reset POM debug state so this step only reports info for current run
            signUpPage.resetBackNavigationDebugState();

            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_Navigate_Back");

            startYourBusinessNavigateStart = Instant.now();
            NavContext.start("Navigate Back to Home Page");

            signUpPage.navigateBackToPreviousPage();

            // Screenshot after the whole flow
            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Navigate_Back");

            // 🔎 Read debug info from POM and push them to Allure as PROOF
            boolean leadSubmitted = signUpPage.wasLastStillConfusedLeadSubmitted();
            boolean paymentReached = signUpPage.wasLastPaymentPageReached();
            List<String> navTrace = signUpPage.getLastBackNavigationUrls();

            StringBuilder traceHtml = new StringBuilder();
            traceHtml.append("lastStillConfusedLeadSubmitted = ").append(leadSubmitted).append("<br>").append("lastPaymentPageReached = ").append(paymentReached).append("<br><br>").append("<b>Back Navigation Trace:</b><br>");

            for (String entry : navTrace) {
                traceHtml.append(entry).append("<br>");
            }

            logToAllure("🔍 Back Navigation Debug Trace", traceHtml.toString());

            // Optionally, if payment was involved, attach one more screenshot for clarity
            if (paymentReached) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "Post_Payment_BackFlow_State");
            }

            // ✅ Final home validation
            Duration homeTimeout = Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS);
            boolean homeLoaded = homePage.waitForHomeLoaded(homeTimeout);
            logger.info("Home page loaded after navigating back = {}", homeLoaded);
            Assert.assertTrue(homeLoaded, "Home page NOT loaded after navigating BACK!");

            long elapsedMs = commonMethods.logLoadTimeAndReturnMs("Navigate Back to Home Page", startYourBusinessNavigateStart, ReusableCommonMethods.NAV_WARN_MS, ReusableCommonMethods.NAV_FAIL_MS);

            logToAllure("↩️ Back Navigation Complete", "Returned to Home page<br>Load Time: " + (elapsedMs / 1000.0) + " sec");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "BackNavigation_Exception");
            signUpPage.handleValidationException("Navigate BACK to Home page", t);
        }
    }


    @When("the user clicks on the Add Business link on the dashboard")
    public void the_user_clicks_on_the_add_business_link_on_the_dashboard() {
        try {
            logStep("🖱 Clicking on 'Add Business' link on dashboard...");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_AddBusiness_Click");
            boolean ok = signUpPage.clickAddBusinessLinkAndWaitForWelcome(Duration.ofSeconds(20));
            ScreenshotUtils.attachScreenshotToAllure(driver, "After_AddBusiness_Click");

            Assert.assertTrue(ok, "Welcome usage page did NOT load after clicking 'Add Business' link.");

            logToAllure("Add Business Click", "Clicked on 'Add Business' and navigated to Welcome usage page");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "AddBusiness_Click_Exception");
            signUpPage.handleValidationException("Click 'Add Business' link on dashboard", e);
        }
    }


    @When("the user clicks on the Get Started widget on the dashboard")
    public void the_user_clicks_on_the_get_started_widget_on_the_dashboard() {
        try {
            logStep("🖱 Clicking Get Started widget on the dashboard...");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_GetStarted_Click");

            signUpPage.clickGetStartedWidget();

            ScreenshotUtils.attachScreenshotToAllure(driver, "After_GetStarted_Click");

            logToAllure("Get Started Widget", "Clicked on Get Started");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "GetStarted_Click_Exception");
            signUpPage.handleValidationException("Click Get Started widget", e);
        }
    }


    @Then("the Get Started widget should open with correct header content")
    public void the_get_started_widget_should_open_with_correct_header_content() {
        try {
            logStep("🔎 Validating 'Get Started' widget header and visibility...");

            ScreenshotUtils.attachScreenshotToAllure(driver, "GetStarted_Widget_Before_Validation");

            boolean visible = signUpPage.isGetStartedWidgetVisible();
            Assert.assertTrue(visible, "❌ 'Get Started' widget did NOT open correctly!");

            ScreenshotUtils.attachScreenshotToAllure(driver, "GetStarted_Widget_Visible");

            // You already expect this header text – keep it documented in Allure
            logToAllure("Get Started Widget Validation", "Verified that <b>'Get Started with Zolvit 360'</b> widget is visible.<br>" + "Header and subtext content validated at UI level.");

            logStep("🟢 'Get Started' widget validation completed successfully.");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "GetStarted_Widget_Validation_Exception");
            signUpPage.handleValidationException("'Get Started' widget content validation", e);
        }
    }


    @When("the user clicks Continue on the Get Started widget")
    public void the_user_clicks_continue_on_the_get_started_widget() {
        try {
            logStep("🖱 Clicking 'Continue' on 'Get Started' widget...");

            ScreenshotUtils.attachScreenshotToAllure(driver, "GetStarted_Continue_Before_Click");

            // Pre-validation: widget should still be visible before clicking Continue
            boolean widgetVisible = signUpPage.isGetStartedWidgetVisible();
            Assert.assertTrue(widgetVisible, "❌ 'Get Started' widget is not visible before clicking Continue!");

            logToAllure("Get Started Widget State", "'Get Started' widget is visible before clicking <b>Continue</b>.");

            // Click action on Continue CTA
            signUpPage.clickGetStartedContinue();
            logStep("✔ Click action performed on 'Continue' in 'Get Started' widget.");

            ScreenshotUtils.attachScreenshotToAllure(driver, "GetStarted_Continue_After_Click");

            logToAllure("Get Started Continue", "Successfully clicked on the <b>Continue</b> CTA in 'Get Started' widget.");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "GetStarted_Continue_Click_Exception");
            signUpPage.handleValidationException("Click 'Continue' in 'Get Started' widget", e);
        }
    }


    @When("the user selects Business needs usage and clicks Next on the welcome page")
    public void the_user_selects_business_needs_usage_and_clicks_next_on_the_welcome_page() {
        try {
            logStep("✅ Selecting 'Business needs' usage and clicking Next on Welcome page...");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Welcome_Usage_Before_Select_Business");

            // Select Business Needs card / option
            signUpPage.selectBusinessNeedsUsage();
            logStep("✔ 'Business needs' usage option selected on Welcome usage page.");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Welcome_Usage_After_Select_Business");

            // Click Next
            signUpPage.clickWelcomeNextButton();
            logStep("✔ Clicked on 'Next' button on Welcome usage page.");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Welcome_Usage_After_Business_Next");

            logToAllure("Business Needs Usage Selection", "Selected <b>Business needs</b> usage and clicked <b>Next</b> on the Welcome page.");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Welcome_Usage_Business_Next_Exception");
            signUpPage.handleValidationException("Select 'Business needs' usage and click Next", e);
        }
    }


    @Then("the Full Name step should be displayed and the user enters a random full name and clicks Next")
    public void the_full_name_step_should_be_displayed_and_the_user_enters_random_full_name_and_clicks_next() {
        Instant start = Instant.now();
        try {
            logStep("🔎 Validating Full Name onboarding step and entering random full name...");

            boolean visible = signUpPage.isFullNameStepVisible();
            Assert.assertTrue(visible, "Full Name onboarding step is NOT visible.");

            onboardingFullName = faker.name().fullName();
            logger.info("Generated onboarding Full Name = {}", onboardingFullName);

            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_FullName_Before_Typing");
            signUpPage.enterFullNameOnOnboarding(onboardingFullName);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_FullName_After_Typing");

            logToAllure("Full Name Step - Data", "Entered Full Name: <b>" + onboardingFullName + "</b>");

            signUpPage.clickWelcomeNextButton();

            Instant end = Instant.now();
            logStepDurationToAllure("Full Name Step", start, end);

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_FullName_Exception");
            signUpPage.handleValidationException("Full Name step visibility & input", e);
        }
    }


    @Then("the Designation step should be displayed and the user selects a designation from the dropdown")
    public void the_designation_step_should_be_displayed_and_the_user_selects_a_designation_from_the_dropdown() {
        Instant start = Instant.now();
        try {
            logStep("🔎 Validating Designation onboarding step and selecting a designation...");

            Assert.assertTrue(signUpPage.isDesignationStepVisible(), "Designation onboarding step is NOT visible.");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_Designation_Before_Select");
            onboardingDesignation = signUpPage.selectAnyDesignationFromDropdown();
            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_Designation_After_Select");

            logToAllure("Designation Step - Data", "Selected Designation: <b>" + onboardingDesignation + "</b>");

            Instant end = Instant.now();
            logStepDurationToAllure("Designation Step", start, end);

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_Designation_Exception");
            signUpPage.handleValidationException("Designation step visibility & selection", e);
        }
    }


    @Then("the Entity Type step should be displayed and the user selects a random entity type and clicks Next")
    public void user_selects_random_entity_type() {
        Instant start = Instant.now();
        try {
            logStep("🔎 Validating Entity Type onboarding step and selecting a random value...");

            Assert.assertTrue(signUpPage.isEntityTypeStepVisible(), "Entity Type onboarding step is NOT visible.");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_EntityType_Before_Select");
            onboardingEntityType = signUpPage.selectEntityType();
            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_EntityType_After_Select");

            logToAllure("Entity Type Step - Data", "Selected Entity Type: <b>" + onboardingEntityType + "</b>");

            Instant end = Instant.now();
            logStepDurationToAllure("Entity Type Step", start, end);

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_EntityType_Exception");
            signUpPage.handleValidationException("Entity Type step selection", e);
        }
    }


    @Then("the CIN step should be displayed and the user clicks Skip and Next without entering CIN")
    public void the_cin_step_should_be_displayed_and_the_user_clicks_skip_and_next_without_entering_cin() {
        Instant start = Instant.now();
        try {
            logStep("🔎 Validating CIN / Trust / Society / PAN / FCRN onboarding step and clicking 'Skip and Next' without entering any value...");

            boolean visible = signUpPage.isCinStepVisible();
            Assert.assertTrue(visible, "CIN onboarding step is NOT visible.");

            onboardingIdentityVariant = signUpPage.detectCinVariantOnOnboarding();
            logStep("👀 Detected onboarding variant: " + onboardingIdentityVariant);

            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_CIN_Before_Skip_" + onboardingIdentityVariant);

            signUpPage.clickSkipAndNextOnCinStep();

            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_CIN_After_Skip_" + onboardingIdentityVariant);

            logToAllure("CIN / Trust / Society / PAN / FCRN Step - Data", "Variant detected: <b>" + onboardingIdentityVariant + "</b>. " + "Skipped without entering any value.");

            Instant end = Instant.now();
            logStepDurationToAllure("Identity (CIN / Trust / Society / PAN / FCRN) Step", start, end);

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_CIN_Exception");
            signUpPage.handleValidationException("CIN / Trust / Society / PAN / FCRN step visibility & Skip and Next click", e);
        }
    }


    @Then("the Company Details step should be displayed and the user fills all details and clicks Next")
    public void the_company_details_step_should_be_displayed_and_the_user_fills_all_details_and_clicks_next() {
        Instant start = Instant.now();
        try {
            logStep("🏢 Validating Company Details onboarding step and filling details...");

            boolean visible = signUpPage.isCompanyDetailsStepVisible();
            Assert.assertTrue(visible, "Company Details onboarding step is NOT visible.");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_CompanyDetails_Before_Fill");

            SignUpPage.CompanyDetailsData data = signUpPage.fillCompanyDetailsAndClickNext();

            // Save for later verification
            companyDetailsVariant = data.getVariant();
            companyOrTradeOrFirmOrSocietyName = data.getName();
            companyDateValue = data.getDateValue();
            companyState = data.getState();
            companyIndustry = data.getIndustry();

            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_CompanyDetails_After_Fill");

            logToAllure("Company Details Step - Data", "🏷 Variant  -> " + companyDetailsVariant + "\n" + "🏢 Name     -> " + companyOrTradeOrFirmOrSocietyName + "\n" + "📅 Date     -> " + companyDateValue + "\n" + "🗺 State    -> " + companyState + "\n" + "🏭 Industry -> " + companyIndustry);


            Instant end = Instant.now();
            logStepDurationToAllure("Company Details Step", start, end);

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_CompanyDetails_Exception");
            signUpPage.handleValidationException("Company Details onboarding step", e);
        }
    }


    @Then("the Team Size step should be displayed and the user selects any Team Size and clicks Next")
    public void the_team_size_step_should_be_displayed_and_the_user_selects_any_team_size_and_clicks_next() {
        Instant start = Instant.now();
        try {
            logStep("📊 Validating Team Size onboarding step and selecting Team Size...");

            boolean visible = signUpPage.isTeamSizeStepVisible();
            Assert.assertTrue(visible, "Team Size onboarding step is NOT visible.");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_TeamSize_Before");

            teamSizeValue = signUpPage.selectRandomTeamSizeAndClickNext();

            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_TeamSize_After");

            logToAllure("Team Size Step - Data", "Selected Team Size: <b>" + teamSizeValue + "</b>");

            Instant end = Instant.now();
            logStepDurationToAllure("Team Size Step", start, end);

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_TeamSize_Exception");
            signUpPage.handleValidationException("Team Size onboarding step", e);
        }
    }


    @Then("the Annual Turnover step should be displayed and the user selects any turnover and clicks Get Started")
    public void the_annual_turnover_step_should_be_displayed_and_the_user_selects_any_turnover_and_clicks_get_started() {
        Instant stepStart = Instant.now();
        try {
            logStep("💰 Validating Annual Turnover onboarding step and selecting turnover...");

            boolean visible = signUpPage.isAnnualTurnoverStepVisible();
            Assert.assertTrue(visible, "Annual Turnover onboarding step is NOT visible.");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_AnnualTurnover_Before");

            annualTurnoverValue = signUpPage.selectRandomAnnualTurnoverAndClickGetStarted();

            // ⏱ this is the moment Get Started was clicked
            getStartedClickTime = Instant.now();

            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_AnnualTurnover_After");

            logToAllure("Annual Turnover Step - Data", "Annual Turnover --> <b>" + annualTurnoverValue + "</b>");

            logStepDurationToAllure("Annual Turnover Step", stepStart, getStartedClickTime);

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Onboarding_AnnualTurnover_Exception");
            signUpPage.handleValidationException("Annual Turnover onboarding step", e);
        }
    }


    @Then("the compliance analysis screen should be displayed with compliances count and user clicks View my Compliances")
    public void the_compliance_analysis_screen_should_be_displayed_and_user_clicks_view_my_compliances() {

        // Fallback so we don't NPE if someone calls this step directly
        Instant start = (getStartedClickTime != null) ? getStartedClickTime : Instant.now();

        try {
            logStep("📈 Validating compliance analysis screen (Analysing + Overall Progress + Compliances Found)...");

            // 1) Wait until "View my Compliances" is visible – overall load time
            boolean viewVisible = signUpPage.waitForViewMyCompliancesVisible();
            Assert.assertTrue(viewVisible, "'View my Compliances' CTA is NOT visible after clicking Get Started.");

            Instant end = Instant.now();
            long totalMs = logStepDurationToAllure("Get Started → View my Compliances visible", start, end);

            ScreenshotUtils.attachScreenshotToAllure(driver, "AnalysisScreen_After_Load");

            // 2) Header validation
            boolean headerVisible = signUpPage.isComplianceAnalysisHeaderVisible();
            Assert.assertTrue(headerVisible, "Either 'Analysing your compliance requirements' or 'Overall Progress' is NOT visible.");

            // 3) Wait until Overall Progress is 100%
            int progressValue = signUpPage.waitUntilOverallProgressIsHundredPercent();
            Assert.assertEquals(progressValue, 100, "Overall Progress did not reach 100% before reading compliances count.");

            Thread.sleep(4000);
            // 4) Now it is safe to read 'Compliances Found' count
            compliancesFoundCount = signUpPage.getCompliancesFoundCount();

            // 5) Log everything to Allure in pretty format
            String prettyTime = formatDurationPretty(totalMs);

            logToAllure("Compliance Analysis Screen - Data", "📊 Overall Progress     -> " + progressValue + "%\n" + "📦 Compliances Found    -> " + compliancesFoundCount + "\n" + "⏱ Load Time            -> " + prettyTime);

            // 6) Finally click "View my Compliances"
            ScreenshotUtils.attachScreenshotToAllure(driver, "AnalysisScreen_Before_ViewClick");
            signUpPage.clickViewMyCompliances();
            ScreenshotUtils.attachScreenshotToAllure(driver, "AnalysisScreen_After_ViewClick");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "AnalysisScreen_Exception");
            signUpPage.handleValidationException("Compliance analysis screen", e);
        }
    }


    @Then("the compliances list All count should match analysis count and Due Date should be This Financial Year")
    public void the_compliances_list_all_count_and_due_date_should_be_valid() {
        try {
            logStep("📋 Validating Compliances list 'All' count and Due Date filter...");

            // 1) Read All tab count
            int allTabCount = signUpPage.getAllTabCount();
            logger.info("📦 'All' tab compliances count = {}", allTabCount);
            logger.info("📦 Analysis screen compliances count = {}", compliancesFoundCount);

            Assert.assertEquals(allTabCount, compliancesFoundCount, "Compliances count mismatch between analysis screen and 'All' tab.");

            // 2) Due Date filter validation via POM
            String rawDueDateText = signUpPage.getDueDateFilterText();
            boolean dueDateOk = signUpPage.isDueDateFilterThisFinancialYear();

            Assert.assertTrue(dueDateOk, "Due Date filter is not 'This Financial Year' (logical match failed). Raw UI text: " + rawDueDateText);

            logToAllure("Compliances List - Data", "📦 Compliances Found (Analysis) -> " + compliancesFoundCount + "<br>" + "📋 'All' Tab Count            -> " + allTabCount + "<br>" + "📅 Due Date Filter (raw)      -> " + rawDueDateText + "<br>");

            ScreenshotUtils.attachScreenshotToAllure(driver, "CompliancesList_Validated");
            logger.info("✅ Compliances 'All' count & Due Date filter validated successfully.");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "CompliancesList_Validation_Exception");
            signUpPage.handleValidationException("Compliances list All count & Due Date validation", e);
        }
    }


    @Then("the new subscriber offer popup should be visible and user explores Annual Compliance plans")
    public void the_new_subscriber_offer_popup_should_be_visible_and_user_explores_annual_compliance_plans() {
        try {
            logStep("🎁 Validating new subscriber offer popup and Annual Compliance plans panel...");

            // 1) Ensure popup is visible
            boolean popupVisible = signUpPage.isNewSubscriberOfferPopupVisible();
            Assert.assertTrue(popupVisible, "'Welcome! Here's your new subscriber offer.' popup is NOT visible on Compliances page.");

            String popupTitle = signUpPage.getNewSubscriberOfferTitleText();

            ScreenshotUtils.attachScreenshotToAllure(driver, "SubscriberOffer_Popup_Visible");

            logToAllure("New Subscriber Offer Popup - Data", "Title   -> " + popupTitle + "\n" + "CTA     -> Claim My 10% Discount");

            // 2) Click CTA
            signUpPage.clickClaimMyTenPercentDiscount();
            ScreenshotUtils.attachScreenshotToAllure(driver, "SubscriberOffer_Popup_After_CTA_Click");

            // 3) Validate right-side Annual Compliance panel contents
            boolean panelValid = signUpPage.isAnnualCompliancePanelContentVisible();
            Assert.assertTrue(panelValid, "Annual Compliance right panel with required sections is NOT fully visible.");

            ScreenshotUtils.attachScreenshotToAllure(driver, "AnnualCompliancePanel_Visible");

            logToAllure("Annual Compliance Panel - Data", "Heading                -> Annual Compliance for Your Business\n" + "Section 1              -> Why is this mandatory?\n" + "Section 2              -> Your Dedicated Account Manager\n" + "Offer Banner           -> Additional 10% will be Applied at Checkout");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "SubscriberOffer_Exception");
            signUpPage.handleValidationException("New subscriber offer popup & Annual Compliance panel", e);
        }
    }


    @Then("user clicks Explore Plans and verifies pricing page is loaded")
    public void user_clicks_explore_plans_and_verifies_pricing_page_is_loaded() {

        try {
            logStep("🛒 Clicking 'Explore Plans' CTA and validating Pricing page...");

            // Click Explore Plans from drawer
            signUpPage.clickExplorePlans();

            // Validate URL redirected correctly
            boolean redirected = signUpPage.isPricingPageUrlLoaded();
            Assert.assertTrue(redirected, "❌ Pricing page URL was not loaded as expected.");

            // Validate Launch-Ready Plans heading is present
            boolean headingVisible = signUpPage.isLaunchReadyPlansHeadingVisible();

            Assert.assertTrue(headingVisible, "❌ 'Launch-Ready Plans' heading not found on pricing page.");

            logToAllure("Pricing Page Loaded", "URL Verified -> <b>Yes</b><br>" + "Heading 'Launch-Ready Plans' Visible -> <b>Yes</b>");

            ScreenshotUtils.attachScreenshotToAllure(driver, "PricingPage_Success");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "PricingPage_Exception");
            throw new RuntimeException("Failed while validating Pricing page", e);
        }
    }


    @Then("the exit popup should appear with message {string}")
    public void the_exit_popup_should_appear_with_message(String expectedTitle) {

        try {
            logStep("⏳ Waiting for exit popup with title: '" + expectedTitle + "' on Pricing page...");
            logger.info("⏳ [Exit Popup] Waiting for popup to appear on Pricing page...");

            // 1) Wait up to configured time for popup to appear
            boolean popupVisible = signUpPage.waitForExitPopupVisible();
            ScreenshotUtils.attachScreenshotToAllure(driver, "ExitPopup_Wait_Result");

            Assert.assertTrue(popupVisible, "❌ Mandatory exit popup did NOT appear! Expected 'Wait! Before you go...' popup after ~1 minute on Pricing page.");

            // 2) Get actual title text
            String actualTitle = signUpPage.getExitPopupTitleText();
            logger.info("🔎 [Exit Popup] Title -> expected='{}', actual='{}'", expectedTitle, actualTitle);

            // 3) Normalise both strings so dots/case/extra spaces don't matter
            String normalisedExpected = expectedTitle.replace(".", "").trim().toLowerCase();

            String normalisedActual = actualTitle.replace(".", "").trim().toLowerCase();

            logger.info("🧮 [Exit Popup] Normalised expected='{}', normalised actual='{}'", normalisedExpected, normalisedActual);

            Assert.assertEquals(normalisedActual, normalisedExpected, "❌ Exit popup title text did not match after normalising (ignoring dots/case/spaces).");

            // 4) Log to Allure
            logToAllure("✅ Exit Popup - Validation", "Popup visible  -> <b>true</b><br>" + "Expected title -> <b>" + expectedTitle + "</b><br>" + "Actual title   -> <b>" + actualTitle + "</b><br>" + "Comparison     -> <b>normalised (dots, case, spaces ignored)</b>");

            ScreenshotUtils.attachScreenshotToAllure(driver, "ExitPopup_Validated");
            logger.info("✅ [Exit Popup] Successfully validated title on Pricing page.");

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ExitPopup_Exception");
            signUpPage.handleValidationException("Exit popup on Pricing page", e);
        }
    }


    @Then("the user randomly selects an exit reason and closes the thanks popup")
    public void select_random_exit_reason_and_close_popup() {

        try {
            logStep("🎯 Selecting random exit reason and handling subsequent popup(s)...");

            // Step 1: Click random survey option
            String selected = signUpPage.clickRandomSurveyOption();
            logger.info("🎯 [Exit Survey] Selected exit reason: {}", selected);
            logToAllure("🎯 Exit Survey - Selected Option", "User selected exit reason -> <b>" + selected + "</b>");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ExitSurvey_Selected_Option");

            // Step 2: Click Continue
            signUpPage.clickSurveyContinue();
            logger.info("➡ [Exit Survey] Clicked Continue after selecting exit reason.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ExitSurvey_After_Continue");

            // Normalise for case-insensitive comparison
            String normalized = selected == null ? "" : selected.trim().toLowerCase();

            // If "I need to talk to an expert first" -> expert callback flow
            if (normalized.contains("need to talk to an expert")) {

                logger.info("🧠 [Exit Survey] Expert callback flow triggered for selection: {}", selected);
                logToAllure("🧠 Exit Survey Flow", "Expert callback path chosen for option -> <b>" + selected + "</b>");

                // 1) Verify "Get All Your Questions Answered" popup
                boolean expertPopupVisible = signUpPage.isExpertQuestionsPopupVisible();
                ScreenshotUtils.attachScreenshotToAllure(driver, "ExpertPopup_Visible_State");

                Assert.assertTrue(expertPopupVisible, "❌ Expert questions popup did not appear!");

                String expertTitle = signUpPage.getExpertQuestionsPopupTitleText();
                logger.info("📋 [Expert Popup] Title text='{}'", expertTitle);

                Assert.assertEquals(expertTitle, "Get All Your Questions Answered", "❌ Expert questions popup title mismatch!");

                logToAllure("✅ Expert Questions Popup - Validation", "Popup visible -> <b>true</b><br>" + "Title text   -> <b>" + expertTitle + "</b>");

                // 2) Click Request Callback
                logger.info("📞 [Expert Popup] Clicking 'Request Callback' button...");
                signUpPage.clickRequestCallback();
                ScreenshotUtils.attachScreenshotToAllure(driver, "ExpertPopup_After_RequestCallback_Click");

                // 3) Verify "Request Received" popup
                boolean requestReceivedVisible = signUpPage.isRequestReceivedPopupVisible();
                ScreenshotUtils.attachScreenshotToAllure(driver, "RequestReceived_Visible_State");

                Assert.assertTrue(requestReceivedVisible, "❌ 'Request Received' popup did not appear after Request Callback!");

                String requestReceivedTitle = signUpPage.getRequestReceivedTitleText();
                logger.info("📨 [Request Received Popup] Title text='{}'", requestReceivedTitle);

                Assert.assertEquals(requestReceivedTitle, "Request Received", "❌ 'Request Received' popup title mismatch!");

                logToAllure("✅ Request Received Popup - Validation", "Popup visible -> <b>true</b><br>" + "Title text   -> <b>" + requestReceivedTitle + "</b>");

                // 4) Refresh page
                logger.info("🔄 [Expert Flow] Refreshing Pricing page after 'Request Received' popup...");
                signUpPage.refreshPricingPageAfterThanksPopup();
                ScreenshotUtils.attachScreenshotToAllure(driver, "ExpertFlow_After_Refresh");
                logger.info("✔ [Expert Flow] Expert callback flow validated and page refreshed.");

            } else {
                // Normal “Thanks for Sharing” flow
                logger.info("🙏 [Exit Survey] Normal 'Thanks for Sharing' flow for selection: {}", selected);
                logToAllure("🙏 Exit Survey Flow", "Normal thanks path chosen for option -> <b>" + selected + "</b>");

                boolean thanksVisible = signUpPage.isThanksPopupVisible();
                ScreenshotUtils.attachScreenshotToAllure(driver, "ThanksPopup_Visible_State");

                Assert.assertTrue(thanksVisible, "❌ Thanks popup did not appear!");

                String thanksTitle = signUpPage.getThanksPopupTitleText();
                logger.info("📋 [Thanks Popup] Title text='{}'", thanksTitle);

                Assert.assertEquals(thanksTitle, "Thanks for Sharing!", "❌ Popup message mismatch!");

                logToAllure("✅ Thanks Popup - Validation", "Popup visible -> <b>true</b><br>" + "Title text   -> <b>" + thanksTitle + "</b>");

                logger.info("🔄 [Thanks Flow] Refreshing Pricing page after 'Thanks for Sharing' popup...");
                signUpPage.refreshPricingPageAfterThanksPopup();
                ScreenshotUtils.attachScreenshotToAllure(driver, "ThanksFlow_After_Refresh");
                logger.info("✔ [Thanks Flow] 'Thanks for Sharing' popup validated and page refreshed.");
            }

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ExitSurvey_Exception");
            signUpPage.handleValidationException("Exit survey exit-reason flow on Pricing page", t);
        }
    }


    @When("the user clicks Report an issue option")
    public void the_user_clicks_Report_an_issue_option() {
        try {
            logStep("🔍 Navigating and confirming that the user is on the Report an Issue page...");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ReportAnIssue_Before_Click");

            ReportAnIssueSection reportAnIssue = new ReportAnIssueSection(driver);

            // 1) Start timers BEFORE the click (captures click → nav → render)
            Instant navStart = Instant.now();
            NavContext.start("Report an Issue");

            // 2) Click the tab/entry in the UI
            logger.info("🖱 [Report an Issue] Clicking Report an Issue tab...");
            reportAnIssue.clickReportAnIssueTab();

            // 3) Wait up to NAV_FAIL_MS for the page to be ready (config-driven)
            boolean success = reportAnIssue.waitForReportAnIssueLoaded(Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS));

            // 4) Stop & log timing via reusable helper (defaults to NAV thresholds 12s/20s)
            long elapsedMs = commonMethods.logLoadTimeAndReturnMs("Report an Issue", navStart);
            double elapsedSec = elapsedMs / 1000.0;

            // 5) Threshold handling for NAV (warn ≥ NAV_WARN_MS, fail ≥ NAV_FAIL_MS)
            if (success) {
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("Report an Issue took %.2f s — more than %d s. Failing (SLA %ds).", elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Failure", failMsg);
                    ScreenshotUtils.attachScreenshotToAllure(driver, "ReportAnIssue_LoadTime_Failed");
                    Assert.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("Report an Issue took %.2f s — more than %d s.", elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                }
            } else {
                String failMsg = String.format("Unable to load Report an Issue within %d s (actual: %.2f s).", ReusableCommonMethods.NAV_FAIL_MS / 1000, elapsedSec);
                logger.error(failMsg);
                logToAllure("❌ Access Failure", failMsg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ReportAnIssue_Load_Failed");
                Assert.fail(failMsg);
            }

            // 6) Final artifacts
            logToAllure("📋 Report an Issue Page Loaded", String.valueOf(success));
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "ReportAnIssuePage_Confirmation");
            logger.info("✅ Report an Issue page successfully confirmed.");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ReportAnIssue_Exception");
            signUpPage.handleValidationException("Report an Issue page confirmation", t);
        }
    }


    @Then("the user navigates back to the previous page")
    public void user_navigates_back_to_previous_page() {

        try {
            logStep("🔙 Navigating back from Pricing page to Compliance page...");

            String urlBefore = driver.getCurrentUrl();
            logger.info("🌐 [Back Nav] Current URL (before back) = {}", urlBefore);
            ScreenshotUtils.attachScreenshotToAllure(driver, "BackNav_FromPricing_Before");

            // 1) Start nav timing + context
            Instant navStart = Instant.now();
            NavContext.start("Back from Pricing");

            // 2) Delegate actual back action to POM
            logger.info("🔁 [Back Nav] Calling SignUpPage.navigateBackFromPricing()...");
            signUpPage.navigateBackFromPricing();   // wraps driver.navigate().back() or equivalent

            // 3) Wait until URL actually changes from Pricing URL
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ReusableCommonMethods.NAV_FAIL_MS / 1000));

            boolean urlChanged = wait.until(drv -> {
                String current = drv.getCurrentUrl();
                logger.debug("🌐 [Back Nav] Polling URL during back: {}", current);
                return current != null && !current.equals(urlBefore);
            });

            long elapsedMs = commonMethods.logLoadTimeAndReturnMs("Back from Pricing", navStart);
            double elapsedSec = elapsedMs / 1000.0;
            String urlAfter = driver.getCurrentUrl();

            logger.info("🌐 [Back Nav] URL after back navigation = {}", urlAfter);
            logger.info("⏱ [Back Nav] Back from Pricing raw navigation took {} ms (~{} s)", elapsedMs, elapsedSec);

            Assert.assertTrue(urlChanged, "❌ Back navigation from Pricing did not change the URL. Still on: " + urlAfter);

            // 4) Now confirm Compliance page is actually loaded using your existing POM method
            logger.info("📄 [Back Nav] Waiting for Compliance page to fully load after back nav...");
            CompliancePage compliancePage = new CompliancePage(driver);   // adjust class name if needed

            boolean complianceLoaded = compliancePage.waitForComplianceLoaded(Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS));

            if (!complianceLoaded) {
                String failMsg = String.format("Compliance page did NOT load successfully after navigating back from Pricing. " + "URL after back = %s", urlAfter);
                logger.error("❌ {}", failMsg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "BackNav_ComplianceNotLoaded");
                logToAllure("❌ Back Navigation – Compliance Not Loaded", failMsg);
                Assert.fail(failMsg);
            }

            // 5) SLA-style checks on overall back navigation time
            if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                String failMsg = String.format("Back from Pricing to Compliance took %.2f s — more than %d s. Failing (SLA %ds).", elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                logger.error(failMsg);
                logToAllure("❌ Back Navigation SLA Failure (Pricing → Compliance)", failMsg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "BackNav_FromPricing_Failed_SLA");
                Assert.fail(failMsg);
            } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                String warnMsg = String.format("Back from Pricing to Compliance took %.2f s — more than %d s.", elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                logger.warn(warnMsg);
                logToAllure("⚠️ Back Navigation Warning (Pricing → Compliance)", warnMsg);
            }

            // 6) Final success logging + screenshot
            logToAllure("✅ Back Navigation from Pricing to Compliance", "Navigation result -> <b>SUCCESS</b><br>" + "URL before -> <b>" + urlBefore + "</b><br>" + "URL after  -> <b>" + urlAfter + "</b><br>" + "Elapsed    -> <b>" + String.format("%.2f s", elapsedSec) + "</b>");

            ScreenshotUtils.attachScreenshotToAllure(driver, "BackNav_FromPricing_Compliance_Success");
            logger.info("✅ [Back Nav] User navigated back from Pricing page to Compliance page successfully.");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "BackNav_FromPricing_Exception");
            signUpPage.handleValidationException("Back navigation from Pricing page to Compliance page", t);
        }
    }


    @When("User clicks on Notification icon")
    public void user_clicks_notification_icon() {
        try {
            logStep("🖱 Initiating click operation on Notification Icon...");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Notification_Before_Click");

            boolean iconVisible = signUpPage.isNotificationIconVisible();
            Assert.assertTrue(iconVisible, "❌ Notification icon is not visible / not present on UI");
            logToAllure("Notification Icon Visibility", "Notification icon is visible before clicking");

            signUpPage.clickNotificationIcon();
            logStep("✔ Click action on Notification Icon completed");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Notification_After_Click");
            logToAllure("Notification Click", "Successfully clicked on Notification icon");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Notification_Click_Exception");
            logStep("❌ Failure while attempting to click Notification icon");
            throw t;
        }
    }

    @Then("Notification popup should be visible")
    public void validate_notification_popup() {
        try {
            logStep("🔍 Validating Notification popup visibility");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Notification_Popup_PreValidation");

            boolean visible = signUpPage.isNotificationPopupDisplayed();
            Assert.assertTrue(visible, "❌ Notification popup is NOT visible!");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Notification_Popup_Visible");
            logToAllure("Notification Popup", "Notification popup displayed successfully");
            logStep("🟢 Notification popup validation completed.");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Notification_Popup_Validation_Failed");
            throw t;
        }
    }


    @When("User clicks on Cart icon")
    public void user_clicks_cart_icon() {
        try {
            logStep("🛒 Initiating click operation on Cart Icon...");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Cart_Before_Click");

            boolean iconVisible = signUpPage.isCartIconVisible();
            Assert.assertTrue(iconVisible, "❌ Cart icon is not visible / missing from UI");
            logToAllure("Cart Icon Visibility", "Cart icon confirmed visible before clicking");

            signUpPage.clickCartIcon();
            logStep("✔ Click action on Cart Icon completed");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Cart_After_Click");
            logToAllure("Cart Click", "Successfully clicked on Cart icon");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Cart_Click_Exception");
            logStep("❌ Error occurred while clicking Cart icon");
            throw t;
        }
    }

    @Then("Cart popup should be visible")
    public void validate_cart_popup() {
        try {
            logStep("🔍 Validating Cart Popup visibility");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Cart_Popup_PreValidation");

            boolean visible = signUpPage.isCartPopupDisplayed();
            Assert.assertTrue(visible, "❌ Cart popup is NOT visible!");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Cart_Popup_Visible");
            logToAllure("Cart Popup", "Cart popup validated successfully");
            logStep("🟢 Cart popup validation completed.");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Cart_Popup_Validation_Failed");
            throw t;
        }
    }


    @Then("the user should redirect to Zero state Subscription page")
    public void validate_zero_state_page() {
        try {
            logStep("🔍 Checking for Zero State Subscription Page...");

            ScreenshotUtils.attachScreenshotToAllure(driver, "ZeroState_Before_Validation");

            boolean headerPresent = signUpPage.isZeroStateHeaderDisplayed();
            boolean offerPresent = signUpPage.isSpecialOfferDisplayed();

            Assert.assertTrue(headerPresent, "❌ 'No Plan Yet' header not found");
            Assert.assertTrue(offerPresent, "❌ Special 10% offer text missing");

            ScreenshotUtils.attachScreenshotToAllure(driver, "ZeroState_Visible");
            logToAllure("Zero State Page Validation", "Zero State page confirmed successfully");
            logStep("🟢 Zero-State Subscription Page validation complete");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ZeroState_Page_Validation_Failed");
            throw t;
        }
    }


    @Then("the user click Explore Plans & Claim Offer CTA")
    public void user_click_explore_plans_claim_offer_cta() {
        try {
            logStep("🖱 Initiating click on Explore Plans & Claim Offer CTA...");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ExplorePlans_Before_Click");

            boolean ctaVisible = signUpPage.isExplorePlansCTAVisible();
            Assert.assertTrue(ctaVisible, "❌ Explore Plans CTA not visible!");

            signUpPage.clickExplorePlansCTA();

            ScreenshotUtils.attachScreenshotToAllure(driver, "ExplorePlans_After_Click");
            logToAllure("CTA Click", "Explore Plans CTA clicked successfully");
            logStep("🟢 CTA click completed successfully");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "ExplorePlans_Click_Failed");
            throw t;
        }
    }


    @Then("the Pricing page should be displayed after clicking the Explore Plans CTA")
    public void validate_pricing_page_after_CTA() {
        try {
            logStep("🔄 Validating Pricing Page redirection...");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Pricing_Page_PreValidation");

            boolean urlLoaded = signUpPage.isPricingPageUrlLoaded();
            boolean launchPlansVisible = signUpPage.isLaunchReadyPlansHeadingVisible();

            Assert.assertTrue(urlLoaded, "❌ Pricing page URL did not match expected pattern");
            Assert.assertTrue(launchPlansVisible, "❌ 'Launch Ready Plans' heading not visible");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Pricing_Page_Validated");
            logToAllure("Pricing Page Redirect", "Pricing page redirection validated successfully");
            logStep("🟢 Pricing page validation successful");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Pricing_Page_Validation_Failed");
            throw t;
        }
    }


    @When("the user navigates to the previous page")
    public void user_navigates_to_previous_page() {
        try {
            logStep("↩ Initiating browser back navigation...");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_Back_Navigation");
            driver.navigate().back();
            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Back_Navigation");

            logToAllure("Browser Navigation", "Navigated back successfully");

            logStep("🔁 Validating Zero State page after back navigation...");

            boolean headerVisible = signUpPage.isZeroStateHeaderDisplayed();
            Assert.assertTrue(headerVisible, "❌ Zero State page not visible after navigating back");

            ScreenshotUtils.attachScreenshotToAllure(driver, "ZeroState_Post_Back");
            logToAllure("Zero State Validation", "Zero State page visible after browser back");
            logStep("🟢 Zero State page validation after back navigation completed");

        } catch (Throwable t) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Back_Navigation_Validation_Failed");
            throw t;
        }
    }


    @Given("the user navigate back to previous page which is Compliance page")
    public void the_user_navigate_back_to_previous_page_which_is_Compliance_page() {
        try {
            logStep("🔙 Navigating back to previous page (Compliance page)...");

            CompliancePage compliancePage = new CompliancePage(driver);
            // 1) Navigate back
            Hooks.driver.navigate().back();

            // 2) Wait up to NAV_FAIL_MS for the Compliance page to be ready
            boolean success = compliancePage.waitForComplianceLoaded(
                    Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS)
            );

            // 3) Assert Compliance page is loaded
            Assert.assertTrue(success, "❌ Compliance page did not load after navigating back.");

            logToAllure("📄 Compliance Page Loaded", String.valueOf(success));
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "CompliancePage_BackNavigation");
            logger.info("✅ Successfully navigated back and Compliance page confirmed.");

        } catch (Throwable t) {
            signUpPage.handleValidationException("Compliance page back navigation/confirmation", t);
        }
    }



}



