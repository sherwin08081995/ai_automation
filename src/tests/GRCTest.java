package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import pages.GRCPage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GRC Login Page functionality
 * Tests login with OTP, validation, and password login options
 */
public class GRCTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private GRCPage grcPage;
    private JsonNode testData;
    
    @BeforeAll
    static void setUpClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(false)
            .setSlowMo(1000));
    }
    
    @BeforeEach
    void setUp() throws IOException {
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
    
    @Test
    @DisplayName("Should load GRC login page successfully")
    void testPageLoad() {
        // Navigate to GRC login page
        grcPage.navigateToLoginPage();
        
        // Verify page elements are visible
        assertTrue(grcPage.isPageTitleDisplayed(), "Page title should be displayed");
        assertEquals("Log into your account", grcPage.getPageTitle(), "Page title should match expected text");
        assertTrue(grcPage.isGetOTPButtonEnabled(), "Get OTP button should be enabled");
        assertTrue(grcPage.isLoginWithPasswordLinkVisible(), "Login with Password link should be visible");
        assertTrue(grcPage.isSignUpLinkVisible(), "Sign Up link should be visible");
    }
    
    @Test
    @DisplayName("Should successfully enter mobile number and click Get OTP")
    void testEnterMobileNumberAndGetOTP() {
        String mobileNumber = testData.get("validMobileNumber").asText();
        
        // Navigate to login page
        grcPage.navigateToLoginPage();
        
        // Enter mobile number
        grcPage.enterEmailOrMobile(mobileNumber);
        
        // Verify mobile number is entered correctly
        assertEquals(mobileNumber, grcPage.getEmailFieldValue(), "Mobile number should be entered correctly");
        
        // Click Get OTP button
        grcPage.clickGetOTP();
        
        // Wait a moment for any potential page changes
        page.waitForTimeout(2000);
        
        // Verify the action was performed (button should still be visible as we're not actually getting OTP)
        assertTrue(grcPage.isGetOTPButtonEnabled(), "Get OTP button should remain accessible");
    }
    
    @Test
    @DisplayName("Should display validation error for empty email field")
    void testEmptyFieldValidation() {
        // Navigate to login page
        grcPage.navigateToLoginPage();
        
        // Ensure field is empty
        grcPage.clearEmailField();
        
        // Click Get OTP without entering any data
        grcPage.clickGetOTP();
        
        // Wait for validation message to appear
        page.waitForTimeout(1000);
        
        // Verify validation error is displayed
        assertTrue(grcPage.isValidationErrorDisplayed(), "Validation error should be displayed for empty field");
        
        String expectedError = testData.get("validationMessages").get("required").asText();
        assertEquals(expectedError, grcPage.getValidationErrorText(), "Validation error message should match expected text");
    }
    
    @Test
    @DisplayName("Should show Login with Password option")
    void testLoginWithPasswordLink() {
        // Navigate to login page
        grcPage.navigateToLoginPage();
        
        // Verify Login with Password link is visible and clickable
        assertTrue(grcPage.isLoginWithPasswordLinkVisible(), "Login with Password link should be visible");
        
        // Click Login with Password link
        grcPage.clickLoginWithPassword();
        
        // Wait for any potential page changes
        page.waitForTimeout(2000);
        
        // Verify the link is still accessible (as it might trigger a modal or form change)
        assertTrue(grcPage.isLoginWithPasswordLinkVisible(), "Login with Password link should remain accessible");
    }
    
    @Test
    @DisplayName("Should handle different types of input in email field")
    void testVariousInputTypes() {
        String[] testInputs = {
            testData.get("validMobileNumber").asText(),
            testData.get("validEmail").asText(),
            testData.get("invalidInput").asText()
        };
        
        grcPage.navigateToLoginPage();
        
        for (String input : testInputs) {
            // Clear field and enter new input
            grcPage.clearEmailField();
            grcPage.enterEmailOrMobile(input);
            
            // Verify input is entered correctly
            assertEquals(input, grcPage.getEmailFieldValue(), "Input should be entered correctly: " + input);
            
            // Verify Get OTP button remains enabled for all inputs
            assertTrue(grcPage.isGetOTPButtonEnabled(), "Get OTP button should be enabled for input: " + input);
        }
    }
    
    @Test
    @DisplayName("Should maintain page state after multiple interactions")
    void testPageStateConsistency() {
        String mobileNumber = testData.get("validMobileNumber").asText();
        
        grcPage.navigateToLoginPage();
        
        // Perform multiple interactions
        grcPage.enterEmailOrMobile(mobileNumber);
        grcPage.clickLoginWithPassword();
        page.waitForTimeout(1000);
        
        // Verify page elements remain accessible
        assertTrue(grcPage.isPageTitleDisplayed(), "Page title should remain visible");
        assertTrue(grcPage.isGetOTPButtonEnabled(), "Get OTP button should remain enabled");
        assertTrue(grcPage.isLoginWithPasswordLinkVisible(), "Login with Password link should remain visible");
        
        // Verify input value is preserved
        assertEquals(mobileNumber, grcPage.getEmailFieldValue(), "Input value should be preserved");
    }
}