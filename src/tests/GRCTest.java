package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import pages.GRCPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;

/**
 * Test class for GRC Login Page functionality
 * Tests login scenarios including OTP generation, validation, and navigation
 */
class GRCTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private GRCPage grcPage;
    private JsonNode testData;
    
    @BeforeAll
    static void setUpAll() {
        // Any global setup can go here
    }
    
    @BeforeEach
    void setUp() throws IOException {
        // Initialize Playwright
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1280, 720));
        page = context.newPage();
        grcPage = new GRCPage(page);
        
        // Load test data
        ObjectMapper mapper = new ObjectMapper();
        testData = mapper.readTree(new File("src/fixtures/grc.data.json"));
    }
    
    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
    
    /**
     * Test successful mobile number entry and Get OTP button click
     */
    @Test
    @DisplayName("Should successfully enter mobile number and click Get OTP")
    void testEnterMobileNumberAndClickGetOTP() {
        // Arrange
        String mobileNumber = testData.get("validMobileNumber").asText();
        String expectedUrl = testData.get("loginUrl").asText();
        
        // Act
        grcPage.navigateToLoginPage(expectedUrl);
        grcPage.waitForPageLoad();
        grcPage.enterEmailOrMobile(mobileNumber);
        
        // Assert
        assertEquals(mobileNumber, grcPage.getEmailFieldValue(), "Mobile number should be entered correctly");
        assertTrue(grcPage.isGetOtpButtonEnabled(), "Get OTP button should be enabled");
        
        // Act - Click Get OTP
        grcPage.clickGetOtpButton();
        
        // Assert - Verify page behavior after clicking Get OTP
        // Note: In real scenario, this might navigate to OTP verification page or show success message
        assertDoesNotThrow(() -> grcPage.clickGetOtpButton(), "Should be able to click Get OTP button without errors");
    }
    
    /**
     * Test empty field validation when trying to get OTP
     */
    @Test
    @DisplayName("Should show validation error for empty email field")
    void testEmptyFieldValidation() {
        // Arrange
        String expectedUrl = testData.get("loginUrl").asText();
        String expectedErrorMessage = testData.get("validationMessages").get("emptyField").asText();
        
        // Act
        grcPage.navigateToLoginPage(expectedUrl);
        grcPage.waitForPageLoad();
        grcPage.clearEmailField();
        grcPage.clickGetOtpButton();
        
        // Assert
        assertTrue(grcPage.isErrorMessageDisplayed(), "Error message should be displayed for empty field");
        assertEquals(expectedErrorMessage, grcPage.getErrorMessageText(), "Should display correct validation message");
        assertTrue(grcPage.hasEmailFieldErrorStyling(), "Email field should have error styling");
    }
    
    /**
     * Test Login with Password link functionality
     */
    @Test
    @DisplayName("Should display and be clickable Login with Password link")
    void testLoginWithPasswordLink() {
        // Arrange
        String expectedUrl = testData.get("loginUrl").asText();
        
        // Act
        grcPage.navigateToLoginPage(expectedUrl);
        grcPage.waitForPageLoad();
        
        // Assert
        assertTrue(grcPage.isLoginWithPasswordLinkVisible(), "Login with Password link should be visible");
        
        // Act - Click Login with Password link
        assertDoesNotThrow(() -> grcPage.clickLoginWithPasswordLink(), "Should be able to click Login with Password link");
    }
    
    /**
     * Test page load and essential elements visibility
     */
    @Test
    @DisplayName("Should load page with all essential elements visible")
    void testPageLoadAndElementsVisibility() {
        // Arrange
        String expectedUrl = testData.get("loginUrl").asText();
        String expectedTitle = testData.get("expectedPageTitle").asText();
        
        // Act
        grcPage.navigateToLoginPage(expectedUrl);
        grcPage.waitForPageLoad();
        
        // Assert
        assertEquals(expectedTitle, grcPage.getPageTitle(), "Page title should match expected");
        assertTrue(grcPage.isGetOtpButtonEnabled(), "Get OTP button should be visible and enabled");
        assertTrue(grcPage.isLoginWithPasswordLinkVisible(), "Login with Password link should be visible");
        assertTrue(grcPage.isSignUpLinkVisible(), "Sign Up link should be visible");
    }
    
    /**
     * Test entering valid email address
     */
    @Test
    @DisplayName("Should successfully enter valid email address")
    void testEnterValidEmailAddress() {
        // Arrange
        String validEmail = testData.get("validEmail").asText();
        String expectedUrl = testData.get("loginUrl").asText();
        
        // Act
        grcPage.navigateToLoginPage(expectedUrl);
        grcPage.waitForPageLoad();
        grcPage.enterEmailOrMobile(validEmail);
        
        // Assert
        assertEquals(validEmail, grcPage.getEmailFieldValue(), "Email should be entered correctly");
        assertTrue(grcPage.isGetOtpButtonEnabled(), "Get OTP button should remain enabled for valid email");
    }
    
    /**
     * Test form interaction workflow - complete user journey
     */
    @Test
    @DisplayName("Should handle complete login workflow with mobile number")
    void testCompleteLoginWorkflow() {
        // Arrange
        String mobileNumber = testData.get("validMobileNumber").asText();
        String expectedUrl = testData.get("loginUrl").asText();
        
        // Act & Assert - Step 1: Navigate to page
        grcPage.navigateToLoginPage(expectedUrl);
        grcPage.waitForPageLoad();
        assertNotNull(grcPage.getPageTitle(), "Page should load successfully");
        
        // Act & Assert - Step 2: Enter mobile number
        grcPage.enterEmailOrMobile(mobileNumber);
        assertEquals(mobileNumber, grcPage.getEmailFieldValue(), "Mobile number should be entered");
        
        // Act & Assert - Step 3: Verify Get OTP is ready
        assertTrue(grcPage.isGetOtpButtonEnabled(), "Get OTP button should be ready to click");
        
        // Act & Assert - Step 4: Click Get OTP
        assertDoesNotThrow(() -> grcPage.clickGetOtpButton(), "Should successfully click Get OTP");
    }
    
    /**
     * Test field clearing and re-entering data
     */
    @Test
    @DisplayName("Should handle field clearing and data re-entry")
    void testFieldClearingAndReEntry() {
        // Arrange
        String firstEntry = testData.get("validEmail").asText();
        String secondEntry = testData.get("validMobileNumber").asText();
        String expectedUrl = testData.get("loginUrl").asText();
        
        // Act
        grcPage.navigateToLoginPage(expectedUrl);
        grcPage.waitForPageLoad();
        
        // Enter first value
        grcPage.enterEmailOrMobile(firstEntry);
        assertEquals(firstEntry, grcPage.getEmailFieldValue(), "First entry should be correct");
        
        // Clear and enter second value
        grcPage.clearEmailField();
        grcPage.enterEmailOrMobile(secondEntry);
        
        // Assert
        assertEquals(secondEntry, grcPage.getEmailFieldValue(), "Second entry should replace first entry");
        assertNotEquals(firstEntry, grcPage.getEmailFieldValue(), "First entry should be cleared");
    }
}