package src.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import src.pages.GRCPage;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GRC Login page functionality
 * Tests phone number login, validation, and navigation elements
 */
public class GRCTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private GRCPage grcPage;
    
    private static final String BASE_URL = "https://grc.vakilsearch.com/grc/auth/signin";
    private static final String TEST_PHONE_NUMBER = "8148438075";
    
    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(false)
            .setSlowMo(1000));
    }
    
    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
    
    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1280, 720));
        page = context.newPage();
        grcPage = new GRCPage(page);
        grcPage.navigateToLoginPage(BASE_URL);
    }
    
    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }
    
    @Test
    @DisplayName("Should successfully enter phone number and click Get OTP")
    void testEnterPhoneNumberAndClickGetOTP() {
        // Wait for page to load completely
        grcPage.waitForPageLoad();
        
        // Enter phone number
        grcPage.enterPhoneOrEmail(TEST_PHONE_NUMBER);
        
        // Verify phone number is entered correctly
        assertEquals(TEST_PHONE_NUMBER, grcPage.getInputValue(), 
            "Phone number should be entered correctly in the input field");
        
        // Verify Get OTP button is enabled and visible
        assertTrue(grcPage.isGetOtpButtonEnabled(), 
            "Get OTP button should be visible and enabled");
        
        // Click Get OTP button
        grcPage.clickGetOtpButton();
        
        // Add a small wait to allow any immediate UI changes
        page.waitForTimeout(1000);
        
        // Verify that the form submission was attempted (no error should appear for valid phone number)
        assertFalse(grcPage.isErrorMessageVisible(), 
            "No validation error should appear for valid phone number");
    }
    
    @Test
    @DisplayName("Should show validation error when Get OTP is clicked with empty field")
    void testEmptyFieldValidation() {
        // Wait for page to load completely
        grcPage.waitForPageLoad();
        
        // Ensure input field is empty
        grcPage.enterPhoneOrEmail("");
        
        // Click Get OTP without entering any data
        grcPage.submitEmptyForm();
        
        // Verify error message appears
        assertTrue(grcPage.isErrorMessageVisible(), 
            "Validation error message should appear when field is empty");
        
        // Verify error message content
        String errorText = grcPage.getErrorMessage();
        assertTrue(errorText.contains("Email or Mobile number is required"), 
            "Error message should indicate that field is required");
        
        // Verify input field has error styling
        assertTrue(grcPage.hasInputErrorStyling(), 
            "Input field should have error styling (red border) when validation fails");
    }
    
    @Test
    @DisplayName("Should display and allow clicking Login with Password button")
    void testLoginWithPasswordButton() {
        // Wait for page to load completely
        grcPage.waitForPageLoad();
        
        // Verify Login with Password button is visible
        assertTrue(grcPage.isLoginWithPasswordButtonVisible(), 
            "Login with Password button should be visible on the page");
        
        // Click Login with Password button
        grcPage.clickLoginWithPasswordButton();
        
        // Add a small wait to allow any navigation or UI changes
        page.waitForTimeout(1000);
        
        // The button should remain clickable (no errors should occur)
        assertTrue(grcPage.isLoginWithPasswordButtonVisible(), 
            "Login with Password button should remain visible after clicking");
    }
    
    @Test
    @DisplayName("Should validate input field placeholder and initial state")
    void testInputFieldInitialState() {
        // Wait for page to load completely
        grcPage.waitForPageLoad();
        
        // Verify input field is initially empty
        assertEquals("", grcPage.getInputValue(), 
            "Input field should be empty initially");
        
        // Verify input field has proper placeholder (checking via space since placeholder might be handled differently)
        String placeholder = grcPage.getInputPlaceholder();
        assertEquals(" ", placeholder, 
            "Input field should have proper placeholder setup");
        
        // Verify Get OTP button is initially enabled
        assertTrue(grcPage.isGetOtpButtonEnabled(), 
            "Get OTP button should be enabled initially");
    }
    
    @Test
    @DisplayName("Should verify page navigation elements are present")
    void testPageNavigationElements() {
        // Wait for page to load completely
        grcPage.waitForPageLoad();
        
        // Verify Sign Up link is present and visible
        assertTrue(grcPage.isSignUpLinkVisible(), 
            "Sign Up link should be visible for users who don't have an account");
        
        // Verify both login options are available
        assertTrue(grcPage.isGetOtpButtonEnabled(), 
            "Get OTP login option should be available");
        
        assertTrue(grcPage.isLoginWithPasswordButtonVisible(), 
            "Login with Password option should be available");
    }
    
    @Test
    @DisplayName("Should handle different input formats correctly")
    void testDifferentInputFormats() {
        // Wait for page to load completely
        grcPage.waitForPageLoad();
        
        // Test with email format
        String testEmail = "test@example.com";
        grcPage.enterPhoneOrEmail(testEmail);
        assertEquals(testEmail, grcPage.getInputValue(), 
            "Email should be entered correctly");
        
        // Clear and test with different phone number
        String anotherPhoneNumber = "9876543210";
        grcPage.enterPhoneOrEmail(anotherPhoneNumber);
        assertEquals(anotherPhoneNumber, grcPage.getInputValue(), 
            "Different phone number should be entered correctly");
        
        // Clear and test with the original test phone number
        grcPage.enterPhoneOrEmail(TEST_PHONE_NUMBER);
        assertEquals(TEST_PHONE_NUMBER, grcPage.getInputValue(), 
            "Original test phone number should be entered correctly");
        
        // Verify Get OTP button remains functional with different inputs
        assertTrue(grcPage.isGetOtpButtonEnabled(), 
            "Get OTP button should remain enabled with valid input");
    }
}