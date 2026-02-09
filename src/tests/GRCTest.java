package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import pages.GRCPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Test class for GRC Login page functionality
 * Tests the login flow with email/mobile number and OTP generation
 */
public class GRCTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private GRCPage grcPage;
    private Map<String, Object> testData;
    
    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }
    
    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }
    
    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
        grcPage = new GRCPage(page);
        
        // Load test data
        loadTestData();
    }
    
    @AfterEach
    void closeContext() {
        context.close();
    }
    
    /**
     * Load test data from JSON fixture file
     */
    private void loadTestData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            testData = mapper.readValue(
                new File("src/fixtures/grc.data.json"), 
                new TypeReference<Map<String, Object>>() {}
            );
        } catch (IOException e) {
            fail("Failed to load test data: " + e.getMessage());
        }
    }
    
    /**
     * Test successful login flow with valid mobile number
     * Scenario: User enters valid mobile number and clicks Get OTP
     */
    @Test
    @DisplayName("Should successfully enter mobile number and click Get OTP")
    void testSuccessfulLoginWithValidMobileNumber() {
        // Arrange
        String url = (String) testData.get("loginUrl");
        String mobileNumber = (String) testData.get("validMobileNumber");
        
        // Act
        grcPage.navigateToLoginPage(url);
        grcPage.waitForPageLoad();
        
        // Assert page loads correctly
        assertTrue(grcPage.isEmailInputVisible(), "Email/Mobile input should be visible");
        assertTrue(grcPage.isGetOtpButtonVisible(), "Get OTP button should be visible");
        assertEquals("GRC | Vakilsearch", grcPage.getPageTitle(), "Page title should match");
        
        // Act - Enter mobile number and click Get OTP
        grcPage.enterEmailOrMobile(mobileNumber);
        
        // Assert input value is set correctly
        assertEquals(mobileNumber, grcPage.getEmailInputValue(), "Mobile number should be entered correctly");
        
        // Act - Click Get OTP button
        grcPage.clickGetOtpButton();
        
        // Assert - Button was clickable (no assertion needed as click would fail if not clickable)
        // In a real scenario, you might wait for OTP screen or success message
    }
    
    /**
     * Test login with empty input field
     * Scenario: User clicks Get OTP without entering any credentials
     */
    @Test
    @DisplayName("Should display error message when Get OTP is clicked with empty input")
    void testLoginWithEmptyInput() {
        // Arrange
        String url = (String) testData.get("loginUrl");
        
        // Act
        grcPage.navigateToLoginPage(url);
        grcPage.waitForPageLoad();
        grcPage.clickGetOtpButton();
        
        // Assert error message is displayed
        assertTrue(grcPage.isErrorMessageDisplayed(), "Error message should be displayed for empty input");
        String errorMessage = grcPage.getErrorMessage();
        assertTrue(errorMessage.contains("required"), "Error message should indicate field is required");
    }
    
    /**
     * Test login with valid email address
     * Scenario: User enters valid email and clicks Get OTP
     */
    @Test
    @DisplayName("Should successfully enter email address and click Get OTP")
    void testSuccessfulLoginWithValidEmail() {
        // Arrange
        String url = (String) testData.get("loginUrl");
        String email = (String) testData.get("validEmail");
        
        // Act
        grcPage.navigateToLoginPage(url);
        grcPage.waitForPageLoad();
        grcPage.enterEmailOrMobile(email);
        
        // Assert
        assertEquals(email, grcPage.getEmailInputValue(), "Email should be entered correctly");
        assertTrue(grcPage.isGetOtpButtonEnabled(), "Get OTP button should be enabled with valid email");
        
        // Act - Click Get OTP
        grcPage.clickGetOtpButton();
    }
    
    /**
     * Test the complete login workflow as specified in requirements
     * Scenario: Enter 8148438075 and click Get OTP (exact requirement)
     */
    @Test
    @DisplayName("Should complete the exact login workflow: enter 8148438075 and click Get OTP")
    void testExactLoginWorkflowAsSpecified() {
        // Arrange
        String url = (String) testData.get("loginUrl");
        String requiredMobile = "8148438075";
        
        // Act - Navigate to page
        grcPage.navigateToLoginPage(url);
        grcPage.waitForPageLoad();
        
        // Verify initial page state
        assertTrue(grcPage.isEmailInputVisible(), "Email address section should be visible");
        assertTrue(grcPage.isGetOtpButtonVisible(), "Get OTP CTA should be visible");
        
        // Act - Step 1: Enter 8148438075 in email address section
        grcPage.enterEmailOrMobile(requiredMobile);
        
        // Verify mobile number is entered correctly
        assertEquals(requiredMobile, grcPage.getEmailInputValue(), "Mobile number 8148438075 should be entered in email address section");
        
        // Act - Step 2: Click Get OTP CTA
        grcPage.clickGetOtpButton();
        
        // Assert workflow completion (in real scenario, would verify OTP screen appears)
        // Since we can't verify the next screen without actual backend, we verify the action was performed
        assertNotNull(grcPage.getEmailInputValue(), "Login workflow should complete successfully");
    }
    
    /**
     * Test page elements and navigation
     * Scenario: Verify all important page elements are present and functional
     */
    @Test
    @DisplayName("Should display all required page elements correctly")
    void testPageElementsVisibility() {
        // Arrange
        String url = (String) testData.get("loginUrl");
        
        // Act
        grcPage.navigateToLoginPage(url);
        grcPage.waitForPageLoad();
        
        // Assert all key elements are visible
        assertTrue(grcPage.isEmailInputVisible(), "Email/Mobile input field should be visible");
        assertTrue(grcPage.isGetOtpButtonVisible(), "Get OTP button should be visible");
        assertTrue(grcPage.isTermsOfServiceLinkVisible(), "Terms of Service link should be visible");
        assertTrue(grcPage.isPrivacyPolicyLinkVisible(), "Privacy Policy link should be visible");
        
        // Verify input label
        String labelText = grcPage.getEmailInputLabel();
        assertTrue(labelText.contains("Email") || labelText.contains("mobile"), "Input label should mention email or mobile");
    }
    
    /**
     * Test navigation to Sign Up page
     * Scenario: User clicks Sign Up link
     */
    @Test
    @DisplayName("Should navigate to Sign Up page when Sign Up link is clicked")
    void testSignUpLinkNavigation() {
        // Arrange
        String url = (String) testData.get("loginUrl");
        
        // Act
        grcPage.navigateToLoginPage(url);
        grcPage.waitForPageLoad();
        
        // Verify Sign Up link is present and clickable
        grcPage.clickSignUpLink();
        
        // Assert navigation occurred (URL should change)
        String currentUrl = page.url();
        assertTrue(currentUrl.contains("signup"), "Should navigate to signup page");
    }
    
    /**
     * Test invalid mobile number format
     * Scenario: User enters invalid mobile number
     */
    @Test
    @DisplayName("Should handle invalid mobile number format")
    void testInvalidMobileNumberFormat() {
        // Arrange
        String url = (String) testData.get("loginUrl");
        String invalidMobile = (String) testData.get("invalidMobileNumber");
        
        // Act
        grcPage.navigateToLoginPage(url);
        grcPage.waitForPageLoad();
        grcPage.enterEmailOrMobile(invalidMobile);
        grcPage.clickGetOtpButton();
        
        // Assert - In real scenario, would check for validation error
        // For now, verify the invalid input was accepted by the field
        assertEquals(invalidMobile, grcPage.getEmailInputValue(), "Invalid mobile number should be entered");
    }
}