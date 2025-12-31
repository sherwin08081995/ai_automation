package demo;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class NaukriSearchTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private By skillInput =
            By.xpath("//input[@placeholder='Enter skills / designations / companies']");

    private By searchBtn = By.xpath("//button[text()='Search']");

    public NaukriSearchTest(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void open() {
        driver.get("https://www.naukri.com");

        String main = driver.getWindowHandle();
        for (String h : driver.getWindowHandles()) {
            if (!h.equals(main)) {
                driver.switchTo().window(h);
                driver.close();
            }
        }
        driver.switchTo().window(main);
    }

    public void enterSkillAndSelect(String skill) {
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(skillInput)
        );
        input.clear();
        input.sendKeys(skill);
        input.sendKeys(Keys.ENTER);
    }

    public void clickSearch() {
        try {
            WebElement btn = wait.until(
                    ExpectedConditions.elementToBeClickable(searchBtn)
            );
            btn.click();
        } catch (Exception ignored) {}
    }
}
