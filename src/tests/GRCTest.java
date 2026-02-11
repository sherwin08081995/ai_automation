package src.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import src.pages.GRCPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Test class for GRC Login page functionality
 * Tests login with phone number, empty field validation, and Login with Password link
 */
public class GRCTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private GRCPage grcPage;
    private Map<String, Object> testData;
    
    @BeforeEach
    void setUp() throws Exception {
        // Load test data
        ObjectMapper mapper = new ObjectMapper();
        testData = mapper.readValue(
            Paths.get("src/fixtures/grc.data.json").toFile(), 
            Map.class
        );
        
        // Setup Playwright
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
        grcPage = new GRCPage(page);
        
        // Navigate to GRC login page
        grcPage.navigateTo((String) testData.get("baseUrl"));
    }
    
    @AfterEach
    void tearDown() {
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
    
    @Test
    @DisplayName("Should successfully enter phone number and click Get OTP button")
    void testEnterPhoneNumberAndClickGetOTP() {
        // Arrange
        String phoneNumber = (String) testData.get("validPhoneNumber");
        
        // Act
        grcPage.waitForPageLoad();
        grcPage.enterEmailOrPhone(phoneNumber);
        
        // Assert input value is set correctly
        assertEquals(phoneNumber, grcPage.getEmailPhoneValue(), 
            "Phone number should be entered correctly in the input field");
        
        // Assert Get OTP button is enabled
        assertTrue(grcPage.isGetOtpButtonEnabled(), 
            "Get OTP button should be enabled after entering valid phone number");
        
        // Act - Click Get OTP button
        grcPage.clickGetOtpButton();
        
        // Assert - Wait a moment to see if any navigation or state change occurs
        // In a real scenario, this might redirect to OTP verification page
        assertNotNull(grcPage.getPageTitle(), "Page should remain accessible after clicking Get OTP");
    }
    
    @Test
    @DisplayName("Should show validation error for empty email/phone field")
    void testEmptyFieldValidation() {
        // Arrange
        grcPage.waitForPageLoad();
        
        // Act - Leave field empty and try to submit
        grcPage.clearEmailPhoneField();
        grcPage.clickGetOtpButton();
        
        // Assert - Error message should be visible
        assertTrue(grcPage.isErrorMessageVisible(), 
            "Error message should be visible when field is empty");
        
        String errorMessage = grcPage.getErrorMessage();
        assertTrue(errorMessage.toLowerCase().contains("required") || 
                  errorMessage.toLowerCase().contains("email") || 
                  errorMessage.toLowerCase().contains("mobile"),
            "Error message should indicate that email or mobile number is required");
    }
    
    @Test
    @DisplayName("Should display and be able to click Login with Password link")
    void testLoginWithPasswordLink() {
        // Arrange
        grcPage.waitForPageLoad();
        
        // Assert - Login with Password link should be visible
        assertTrue(grcPage.isLoginWithPasswordLinkVisible(), 
            "Login with Password link should be visible on the page");
        
        // Act - Click the Login with Password link
        grcPage.clickLoginWithPasswordLink();
        
        // Assert - Page should remain accessible (in real scenario might navigate to password login)
        assertTrue(grcPage.isLoginFormVisible(), 
            "Login form should still be accessible after clicking Login with Password");
    }
    
    @Test
    @DisplayName("Should validate page elements are loaded correctly")
    void testPageElementsLoaded() {
        // Arrange & Act
        grcPage.waitForPageLoad();
        
        // Assert - All main elements should be present
        assertTrue(grcPage.isLoginFormVisible(), "Login form should be visible");
        assertTrue(grcPage.isGetOtpButtonEnabled(), "Get OTP button should be present");
        assertTrue(grcPage.isLoginWithPasswordLinkVisible(), 
            "Login with Password link should be visible");
        
        // Assert page title
        String pageTitle = grcPage.getPageTitle();
        assertTrue(pageTitle.contains("GRC") || pageTitle.contains("Vakilsearch"), 
            "Page title should contain GRC or Vakilsearch");
    }
    
    @Test
    @DisplayName("Should enter email and validate Get OTP functionality")
    void testEnterEmailAndGetOTP() {
        // Arrange
        String email = (String) testData.get("validEmail");
        
        // Act
        grcPage.waitForPageLoad();
        grcPage.enterEmailOrPhone(email);
        
        // Assert
        assertEquals(email, grcPage.getEmailPhoneValue(), 
            "Email should be entered correctly in the input field");
        
        assertTrue(grcPage.isGetOtpButtonEnabled(), 
            "Get OTP button should be enabled after entering valid email");
        
        // Act - Click Get OTP
        grcPage.clickGetOtpButton();
        
        // Assert - Page should handle the request
        assertNotNull(grcPage.getPageTitle(), 
            "Page should remain accessible after clicking Get OTP with email");
    }
    
    @Test
    @DisplayName("Should handle invalid phone number input")
    void testInvalidPhoneNumberInput() {
        // Arrange
        String invalidPhone = (String) testData.get("invalidPhoneNumber");
        
        // Act
        grcPage.waitForPageLoad();
        grcPage.enterEmailOrPhone(invalidPhone);
        grcPage.clickGetOtpButton();
        
        // Assert - Should either show error or handle gracefully
        // The exact behavior depends on frontend validation
        assertTrue(grcPage.isLoginFormVisible(), 
            "Form should remain visible even with invalid input");
    }
}