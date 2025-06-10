package pages;

import base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * @author Sherwin
 * @since 09-06-2025
 */

public class LoginPage extends BasePage {
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "(//input[@id='mobile'])[2]")
    private WebElement userName;

    @FindBy(xpath = "(//p[normalize-space()='Send OTP'])[2]")
    private WebElement otpBtn;

    @FindBy(xpath = "//p[text()='Enter OTP']/following-sibling::div//input[@inputmode='numeric' and @maxlength='1']")
    private List<WebElement> otpInputs;

    @FindBy(xpath = "(//p[normalize-space()='Your business compliances, simplified and sorted.'])[2]")
    private WebElement loginSubtitleText;

    public void enterEmail(String emailInput) {
        wait.waitForElementToBeClickable(userName).sendKeys(emailInput);
    }

    public void clickOtpButton() {
        wait.waitForElementToBeClickable(otpBtn).click();
    }

    public void enterOtp(String otp) {
        int startIndex = 6;

        // Safety check to avoid IndexOutOfBoundsException
        if (otpInputs.size() < startIndex + otp.length()) {
            throw new RuntimeException("Not enough OTP input boxes. Found: " + otpInputs.size() + ", Required: " + (startIndex + otp.length()));
        }

        for (int i = 0; i < otp.length(); i++) {
            WebElement inputBox = wait.waitForElementToBeClickable(otpInputs.get(startIndex + i));
            inputBox.clear();
            inputBox.sendKeys(String.valueOf(otp.charAt(i)));
        }
    }


    public boolean isLoginSubtitleCorrect() {
        String expected = "Your business compliances, simplified and sorted.";
        String actual = wait.waitForVisibility(loginSubtitleText).getText().trim();
        return expected.equals(actual);
    }


}

