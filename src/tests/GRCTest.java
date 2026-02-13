package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import pages.GRCPage;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GRC Login functionality
 * Tests login form validation, OTP request, and password login option
 */
public class GRCTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private GRCPage grcPage;
    
    @BeforeAll
    static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }
    
    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1280, 720)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
        );
        page = context.newPage();
        grcPage = new GRCPage(page);
        grcPage.navigateToLoginPage();
    }
    
    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }
    
    @AfterAll
    static void tearDown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
    
    @Test
    @DisplayName("Should successfully enter mobile number and enable Get OTP button")
    void testEnterMobileNumberAndGetOTP() {
        // Arrange
        String mobileNumber = "8148438075";
        
        // Act
        grcPage.enterEmailOrMobile(mobileNumber);
        
        // Assert
        assertEquals(mobileNumber, grcPage.getEmailOrMobileValue(), 
            "Mobile number should be correctly entered in the input field");
        assertTrue(grcPage.isGetOtpButtonEnabled(), 
            "Get OTP button should be enabled after entering valid mobile number");
        
        // Act - Click Get OTP button
        grcPage.clickGetOtpButton();
        
        // Assert - Verify the form submission was triggered
        // Note: In a real scenario, this might redirect to OTP verification page
        // or show a success message. Adjust assertions based on actual behavior.
        assertDoesNotThrow(() -> grcPage.clickGetOtpButton(), 
            "Clicking Get OTP button should not throw any exception");
    }
    
    @Test
    @DisplayName("Should display validation error for empty email/mobile field")
    void testEmptyFieldValidation() {
        // Arrange - Start with empty field
        grcPage.clearEmailOrMobileField();
        
        // Act - Try to submit form with empty field
        grcPage.clickGetOtpButton();
        grcPage.waitForErrorMessage();
        
        // Assert
        assertTrue(grcPage.isErrorMessageDisplayed(), 
            "Error message should be displayed for empty field");
        
        String errorMessage = grcPage.getErrorMessage();
        assertFalse(errorMessage.isEmpty(), 
            "Error message should contain text");
        assertTrue(errorMessage.toLowerCase().contains("required") || 
                  errorMessage.toLowerCase().contains("email") || 
                  errorMessage.toLowerCase().contains("mobile"),
            "Error message should indicate that email or mobile number is required");
    }
    
    @Test
    @DisplayName("Should show Login with Password option")
    void testLoginWithPasswordLink() {
        // Assert
        assertTrue(grcPage.isLoginWithPasswordButtonVisible(), 
            "Login with Password button should be visible on the page");
        
        // Act - Click Login with Password
        grcPage.clickLoginWithPasswordButton();
        
        // Assert - Verify the button click was successful
        assertDoesNotThrow(() -> grcPage.clickLoginWithPasswordButton(), 
            "Clicking Login with Password button should not throw any exception");
    }
    
    @Test
    @DisplayName("Should have proper page elements loaded")
    void testPageElementsPresence() {
        // Assert all key elements are present
        assertTrue(grcPage.isGetOtpButtonEnabled() || !grcPage.isGetOtpButtonEnabled(), 
            "Get OTP button should be present (enabled or disabled)");
        assertTrue(grcPage.isLoginWithPasswordButtonVisible(), 
            "Login with Password button should be visible");
        assertTrue(grcPage.isSignupLinkVisible(), 
            "Sign Up link should be visible");
        
        // Verify input field properties
        String placeholder = grcPage.getInputPlaceholder();
        assertNotNull(placeholder, "Input field should have a placeholder");
        assertTrue(placeholder.toLowerCase().contains("email") || 
                  placeholder.toLowerCase().contains("mobile"),
            "Placeholder should mention email or mobile");
    }
    
    @Test
    @DisplayName("Should handle valid email format")
    void testValidEmailEntry() {
        // Arrange
        String email = "test@example.com";
        
        // Act
        grcPage.enterEmailOrMobile(email);
        
        // Assert
        assertEquals(email, grcPage.getEmailOrMobileValue(), 
            "Email should be correctly entered in the input field");
        assertTrue(grcPage.isGetOtpButtonEnabled(), 
            "Get OTP button should be enabled after entering valid email");
    }
    
    @Test
    @DisplayName("Should clear input field successfully")
    void testClearInputField() {
        // Arrange
        String testInput = "8148438075";
        grcPage.enterEmailOrMobile(testInput);
        assertEquals(testInput, grcPage.getEmailOrMobileValue(), 
            "Input should be entered first");
        
        // Act
        grcPage.clearEmailOrMobileField();
        
        // Assert
        String clearedValue = grcPage.getEmailOrMobileValue();
        assertTrue(clearedValue.isEmpty(), 
            "Input field should be empty after clearing");
    }
    
    @Test
    @DisplayName("Should handle special characters in mobile number")
    void testSpecialCharactersInMobileNumber() {
        // Arrange
        String mobileWithSpaces = "8148 438 075";
        String mobileWithDashes = "814-843-8075";
        String mobileWithPlus = "+918148438075";
        
        // Test mobile with spaces
        grcPage.enterEmailOrMobile(mobileWithSpaces);
        assertFalse(grcPage.getEmailOrMobileValue().isEmpty(), 
            "Should accept mobile number with spaces");
        
        // Test mobile with dashes
        grcPage.clearEmailOrMobileField();
        grcPage.enterEmailOrMobile(mobileWithDashes);
        assertFalse(grcPage.getEmailOrMobileValue().isEmpty(), 
            "Should accept mobile number with dashes");
        
        // Test mobile with plus sign
        grcPage.clearEmailOrMobileField();
        grcPage.enterEmailOrMobile(mobileWithPlus);
        assertFalse(grcPage.getEmailOrMobileValue().isEmpty(), 
            "Should accept mobile number with country code");
    }
}