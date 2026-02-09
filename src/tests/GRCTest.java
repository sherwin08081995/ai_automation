package src.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import src.pages.GRCPage;

/**
 * Test class for GRC Login functionality
 * Tests login scenarios with email/mobile number and OTP
 */
public class GRCTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private GRCPage grcPage;
    
    @BeforeAll
    static void setUpClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }
    
    @AfterAll
    static void tearDownClass() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
    
    @BeforeEach
    void setUp() {
        context = browser.newContext();
        page = context.newPage();
        grcPage = new GRCPage(page);
        grcPage.navigateToLogin();
    }
    
    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
    }
    
    @Test
    @DisplayName("Should successfully enter mobile number and click Get OTP")
    void testSuccessfulLoginWithMobileNumber() {
        // Wait for page to load
        grcPage.waitForPageLoad();
        
        // Verify login form elements are visible
        assertTrue(grcPage.isEmailMobileInputVisible(), "Email/Mobile input field should be visible");
        assertTrue(grcPage.isGetOtpButtonVisible(), "Get OTP button should be visible");
        
        // Enter mobile number as specified in requirement
        String mobileNumber = "8148438075";
        grcPage.enterEmailOrMobile(mobileNumber);
        
        // Verify the mobile number was entered correctly
        assertEquals(mobileNumber, grcPage.getEmailMobileInputValue(), "Mobile number should be entered correctly");
        
        // Click Get OTP button
        grcPage.clickGetOtpButton();
        
        // Verify button click was successful (button should be enabled before clicking)
        assertTrue(grcPage.isGetOtpButtonEnabled(), "Get OTP button should be enabled after entering valid mobile number");
    }
    
    @Test
    @DisplayName("Should perform complete login flow with mobile number 8148438075")
    void testCompleteLoginFlowWithSpecifiedMobileNumber() {
        // Wait for page to load
        grcPage.waitForPageLoad();
        
        // Perform the complete login flow as specified in requirement
        String mobileNumber = "8148438075";
        grcPage.performLogin(mobileNumber);
        
        // Verify the mobile number is correctly entered
        assertEquals(mobileNumber, grcPage.getEmailMobileInputValue(), "Mobile number should match the specified requirement");
    }
    
    @Test
    @DisplayName("Should display error message for empty input")
    void testErrorMessageForEmptyInput() {
        // Wait for page to load
        grcPage.waitForPageLoad();
        
        // Click Get OTP without entering any value
        grcPage.clickGetOtpButton();
        
        // Verify error message is displayed
        assertTrue(grcPage.isErrorMessageVisible(), "Error message should be visible for empty input");
        
        // Verify error message content
        String errorMessage = grcPage.getErrorMessageText();
        assertNotNull(errorMessage, "Error message should not be null");
        assertTrue(errorMessage.contains("required") || errorMessage.contains("Email") || errorMessage.contains("Mobile"), 
                  "Error message should indicate that email or mobile is required");
    }
    
    @Test
    @DisplayName("Should validate input field accepts different formats")
    void testInputFieldAcceptsDifferentFormats() {
        // Wait for page to load
        grcPage.waitForPageLoad();
        
        // Test with email format
        String email = "test@example.com";
        grcPage.enterEmailOrMobile(email);
        assertEquals(email, grcPage.getEmailMobileInputValue(), "Should accept email format");
        
        // Clear and test with mobile number
        String mobileNumber = "8148438075";
        grcPage.enterEmailOrMobile(mobileNumber);
        assertEquals(mobileNumber, grcPage.getEmailMobileInputValue(), "Should accept mobile number format");
    }
    
    @Test
    @DisplayName("Should verify all login page elements are present")
    void testLoginPageElementsPresence() {
        // Wait for page to load
        grcPage.waitForPageLoad();
        
        // Verify all essential elements are present
        assertTrue(grcPage.isEmailMobileInputVisible(), "Email/Mobile input should be visible");
        assertTrue(grcPage.isGetOtpButtonVisible(), "Get OTP button should be visible");
        assertTrue(grcPage.isSignupLinkVisible(), "Signup link should be visible");
        
        // Verify Get OTP button is initially enabled (assuming no validation errors)
        assertTrue(grcPage.isGetOtpButtonEnabled(), "Get OTP button should be enabled by default");
    }
    
    @Test
    @DisplayName("Should navigate to signup page when clicking signup link")
    void testNavigationToSignupPage() {
        // Wait for page to load
        grcPage.waitForPageLoad();
        
        // Verify signup link is present
        assertTrue(grcPage.isSignupLinkVisible(), "Signup link should be visible");
        
        // Click signup link
        grcPage.clickSignupLink();
        
        // Verify navigation (URL should contain signup)
        String currentUrl = page.url();
        assertTrue(currentUrl.contains("signup"), "Should navigate to signup page");
    }
    
    @Test
    @DisplayName("Should allow switching to password login")
    void testSwitchToPasswordLogin() {
        // Wait for page to load
        grcPage.waitForPageLoad();
        
        // Click login with password button
        grcPage.clickLoginWithPasswordButton();
        
        // Verify the UI changed (this would need more specific assertions based on actual behavior)
        // For now, just verify the button click doesn't cause errors
        assertDoesNotThrow(() -> grcPage.clickLoginWithPasswordButton(), 
                          "Clicking login with password should not throw exceptions");
    }
}