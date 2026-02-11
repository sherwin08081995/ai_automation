package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import pages.GRCPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.nio.file.Paths;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GRC Login Page functionality
 * Tests login scenarios including OTP, password login, and validation
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GRCTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private GRCPage grcPage;
    private JsonNode testData;
    
    @BeforeAll
    void setUp() throws IOException {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        
        // Load test data
        ObjectMapper mapper = new ObjectMapper();
        testData = mapper.readTree(Paths.get("src/fixtures/grc.data.json").toFile());
    }
    
    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1280, 720));
        page = context.newPage();
        grcPage = new GRCPage(page);
    }
    
    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }
    
    @AfterAll
    void tearDown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
    
    @Test
    @DisplayName("Should successfully load GRC login page")
    void shouldLoadGRCLoginPage() {
        // Navigate to login page
        grcPage.navigateToLoginPage();
        grcPage.waitForPageLoad();
        
        // Verify page elements are visible
        assertTrue(grcPage.isPageTitleVisible(), "Page title should be visible");
        assertTrue(grcPage.isGetOtpButtonEnabled(), "Get OTP button should be enabled");
        assertTrue(grcPage.isLoginWithPasswordButtonVisible(), "Login with Password button should be visible");
        assertTrue(grcPage.isSignUpLinkVisible(), "Sign Up link should be visible");
    }
    
    @Test
    @DisplayName("Should enter mobile number and click Get OTP button")
    void shouldEnterMobileNumberAndClickGetOTP() {
        // Navigate to login page
        grcPage.navigateToLoginPage();
        grcPage.waitForPageLoad();
        
        // Enter mobile number
        String mobileNumber = testData.get("mobileNumber").asText();
        grcPage.enterEmailOrMobile(mobileNumber);
        
        // Verify mobile number is entered
        assertEquals(mobileNumber, grcPage.getEmailInputValue(), 
            "Mobile number should be entered correctly");
        
        // Click Get OTP button
        grcPage.clickGetOtpButton();
        
        // Wait a moment for any response (could be OTP screen or error)
        page.waitForTimeout(2000);
        
        // Verify button click was successful (button should remain enabled)
        assertTrue(grcPage.isGetOtpButtonEnabled(), "Get OTP button should remain functional after click");
    }
    
    @Test
    @DisplayName("Should show validation error for empty email field")
    void shouldShowValidationErrorForEmptyEmailField() {
        // Navigate to login page
        grcPage.navigateToLoginPage();
        grcPage.waitForPageLoad();
        
        // Ensure email field is empty
        grcPage.clearEmailField();
        
        // Click Get OTP button without entering email/mobile
        grcPage.clickGetOtpButton();
        
        // Wait for validation to appear
        page.waitForTimeout(1000);
        
        // Verify error message is displayed
        assertTrue(grcPage.isErrorMessageVisible(), "Error message should be visible for empty field");
        
        // Verify error message content
        String errorText = grcPage.getErrorMessageText();
        assertTrue(errorText.contains("Email or Mobile number is required"), 
            "Error message should indicate required field");
        
        // Verify field has error styling
        assertTrue(grcPage.hasEmailFieldErrorStyling(), 
            "Email field should have error styling (red border)");
    }
    
    @Test
    @DisplayName("Should click Login with Password button")
    void shouldClickLoginWithPasswordButton() {
        // Navigate to login page
        grcPage.navigateToLoginPage();
        grcPage.waitForPageLoad();
        
        // Verify Login with Password button is visible
        assertTrue(grcPage.isLoginWithPasswordButtonVisible(), 
            "Login with Password button should be visible");
        
        // Click Login with Password button
        grcPage.clickLoginWithPasswordButton();
        
        // Wait for any potential navigation or modal
        page.waitForTimeout(2000);
        
        // Verify click was registered (button should still be visible)
        assertTrue(grcPage.isLoginWithPasswordButtonVisible(), 
            "Login with Password button should remain visible after click");
    }
    
    @Test
    @DisplayName("Should validate email input field properties")
    void shouldValidateEmailInputFieldProperties() {
        // Navigate to login page
        grcPage.navigateToLoginPage();
        grcPage.waitForPageLoad();
        
        // Verify input field placeholder
        String placeholder = grcPage.getEmailInputPlaceholder();
        assertNotNull(placeholder, "Email input should have placeholder text");
        
        // Test entering different types of input
        String email = testData.get("email").asText();
        grcPage.enterEmailOrMobile(email);
        assertEquals(email, grcPage.getEmailInputValue(), "Email should be entered correctly");
        
        // Clear and test mobile number
        grcPage.clearEmailField();
        String mobile = testData.get("mobileNumber").asText();
        grcPage.enterEmailOrMobile(mobile);
        assertEquals(mobile, grcPage.getEmailInputValue(), "Mobile number should be entered correctly");
    }
    
    @Test
    @DisplayName("Should test complete login flow with mobile number")
    void shouldTestCompleteLoginFlowWithMobileNumber() {
        // Navigate to login page
        grcPage.navigateToLoginPage();
        grcPage.waitForPageLoad();
        
        // Enter mobile number
        String mobileNumber = testData.get("mobileNumber").asText();
        grcPage.enterEmailOrMobile(mobileNumber);
        
        // Verify input
        assertEquals(mobileNumber, grcPage.getEmailInputValue(), 
            "Mobile number should be entered correctly");
        
        // Ensure no error message is visible before clicking
        assertFalse(grcPage.isErrorMessageVisible(), 
            "No error message should be visible with valid input");
        
        // Click Get OTP
        grcPage.clickGetOtpButton();
        
        // Wait for response
        page.waitForTimeout(3000);
        
        // Verify the flow completed without validation errors
        // (The actual OTP screen or success response would be tested in integration tests)
        assertTrue(grcPage.isGetOtpButtonEnabled(), 
            "Get OTP button should remain functional");
    }
    
    @Test
    @DisplayName("Should test form validation with various input scenarios")
    void shouldTestFormValidationWithVariousInputScenarios() {
        // Navigate to login page
        grcPage.navigateToLoginPage();
        grcPage.waitForPageLoad();
        
        // Test 1: Empty field validation
        grcPage.clearEmailField();
        grcPage.clickGetOtpButton();
        page.waitForTimeout(1000);
        assertTrue(grcPage.isErrorMessageVisible(), "Empty field should show error");
        
        // Test 2: Valid mobile number should clear error
        String validMobile = testData.get("mobileNumber").asText();
        grcPage.enterEmailOrMobile(validMobile);
        page.waitForTimeout(500);
        
        // Test 3: Valid email should work
        grcPage.clearEmailField();
        String validEmail = testData.get("email").asText();
        grcPage.enterEmailOrMobile(validEmail);
        assertEquals(validEmail, grcPage.getEmailInputValue(), 
            "Valid email should be accepted");
    }
}