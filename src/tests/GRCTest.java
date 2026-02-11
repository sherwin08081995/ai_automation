package src.tests;

import com.microsoft.playwright.*;
import org.testng.annotations.*;
import org.testng.Assert;
import src.pages.GRCPage;

/**
 * Test class for GRC login page functionality
 * Tests login scenarios including valid phone number entry, empty field validation,
 * and Login with Password link functionality
 */
public class GRCTest {
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private GRCPage grcPage;
    private Playwright playwright;
    
    @BeforeClass
    public void setupClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }
    
    @BeforeMethod
    public void setup() {
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1280, 720));
        page = context.newPage();
        grcPage = new GRCPage(page);
        grcPage.navigateToLoginPage();
        grcPage.waitForPageLoad();
    }
    
    @AfterMethod
    public void teardown() {
        if (context != null) {
            context.close();
        }
    }
    
    @AfterClass
    public void teardownClass() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
    
    /**
     * Test entering valid phone number and clicking Get OTP button
     * Verifies that the phone number is accepted and OTP process can be initiated
     */
    @Test(priority = 1, description = "Test entering valid phone number 8148438075 and clicking Get OTP")
    public void testValidPhoneNumberAndGetOTP() {
        // Enter the specified phone number
        grcPage.enterEmailOrPhone("8148438075");
        
        // Verify the phone number is entered correctly
        Assert.assertEquals(grcPage.getEmailFieldValue(), "8148438075", 
            "Phone number should be entered correctly in the email field");
        
        // Verify Get OTP button is enabled
        Assert.assertTrue(grcPage.isGetOTPButtonEnabled(), 
            "Get OTP button should be enabled after entering valid phone number");
        
        // Click Get OTP button
        grcPage.clickGetOTP();
        
        // Wait for page response (OTP screen or validation)
        page.waitForTimeout(2000);
        
        // Verify no validation errors are shown for valid phone number
        Assert.assertFalse(grcPage.isValidationMessageVisible(), 
            "No validation error should be displayed for valid phone number");
    }
    
    /**
     * Test empty field validation
     * Verifies that appropriate validation message is shown when trying to submit with empty field
     */
    @Test(priority = 2, description = "Test empty field validation when clicking Get OTP with no input")
    public void testEmptyFieldValidation() {
        // Ensure field is empty
        grcPage.enterEmailOrPhone("");
        
        // Verify field is empty
        Assert.assertEquals(grcPage.getEmailFieldValue(), "", 
            "Email field should be empty initially");
        
        // Click Get OTP button with empty field
        grcPage.clickGetOTP();
        
        // Wait for validation message to appear
        page.waitForTimeout(1000);
        
        // Verify validation message is displayed
        Assert.assertTrue(grcPage.isValidationMessageVisible(), 
            "Validation message should be visible when field is empty");
        
        // Verify validation message content
        String validationMessage = grcPage.getValidationMessage();
        Assert.assertTrue(validationMessage.contains("Email or Mobile number is required") || 
                         validationMessage.contains("required"),
            "Validation message should indicate that email or mobile number is required. Actual message: " + validationMessage);
    }
    
    /**
     * Test Login with Password link functionality
     * Verifies that the Login with Password button is visible and clickable
     */
    @Test(priority = 3, description = "Test Login with Password link visibility and functionality")
    public void testLoginWithPasswordLink() {
        // Verify Login with Password button is visible
        Assert.assertTrue(grcPage.isLoginWithPasswordButtonVisible(), 
            "Login with Password button should be visible on the page");
        
        // Click Login with Password button
        grcPage.clickLoginWithPassword();
        
        // Wait for any page changes
        page.waitForTimeout(1500);
        
        // Verify the button was clickable (no exception thrown)
        // Additional assertions could be added here based on expected behavior
        // such as navigation to a different page or modal appearance
    }
    
    /**
     * Test page elements are properly loaded
     * Verifies that all essential page elements are present and functional
     */
    @Test(priority = 4, description = "Test that all essential page elements are loaded correctly")
    public void testPageElementsLoaded() {
        // Verify page title is correct
        Assert.assertEquals(grcPage.getPageTitle(), "Log into your account", 
            "Page title should be 'Log into your account'");
        
        // Verify email field is visible and interactable
        Assert.assertTrue(page.locator("#login-id").isVisible(), 
            "Email/phone input field should be visible");
        
        // Verify Get OTP button is present
        Assert.assertTrue(grcPage.isGetOTPButtonEnabled(), 
            "Get OTP button should be present and enabled by default");
        
        // Verify Login with Password button is present
        Assert.assertTrue(grcPage.isLoginWithPasswordButtonVisible(), 
            "Login with Password button should be visible");
    }
    
    /**
     * Test entering invalid email format
     * Verifies validation for invalid email formats
     */
    @Test(priority = 5, description = "Test entering invalid email format")
    public void testInvalidEmailFormat() {
        // Enter invalid email format
        grcPage.enterEmailOrPhone("invalid-email");
        
        // Verify the text is entered
        Assert.assertEquals(grcPage.getEmailFieldValue(), "invalid-email", 
            "Invalid email text should be entered in the field");
        
        // Click Get OTP button
        grcPage.clickGetOTP();
        
        // Wait for potential validation
        page.waitForTimeout(1000);
        
        // The behavior for invalid email might vary - this test documents the current behavior
        // Additional assertions can be added based on actual application behavior
    }
    
    /**
     * Test entering valid email address
     * Verifies that valid email addresses are accepted
     */
    @Test(priority = 6, description = "Test entering valid email address")
    public void testValidEmailAddress() {
        // Enter valid email address
        grcPage.enterEmailOrPhone("test@example.com");
        
        // Verify email is entered correctly
        Assert.assertEquals(grcPage.getEmailFieldValue(), "test@example.com", 
            "Valid email should be entered correctly");
        
        // Verify Get OTP button is enabled
        Assert.assertTrue(grcPage.isGetOTPButtonEnabled(), 
            "Get OTP button should be enabled for valid email");
        
        // Click Get OTP button
        grcPage.clickGetOTP();
        
        // Wait for response
        page.waitForTimeout(1500);
        
        // Verify no validation errors for valid email
        Assert.assertFalse(grcPage.isValidationMessageVisible(), 
            "No validation error should be shown for valid email address");
    }
}