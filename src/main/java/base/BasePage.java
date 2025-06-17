package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import utils.LoggerUtils;
import utils.ReusableHelperMethods;
import utils.WaitUtils;
import org.apache.logging.log4j.Logger;

/**
 * BasePage is an abstract foundation for all page objects.
 * It initializes commonly used utilities like WebDriver, wait handling, helper methods, and logging.
 * All other page classes should extend this class to inherit shared behavior.
 *
 * @author Sherwin
 * @since 09-06-2025
 */

public class BasePage {
    protected WebDriver driver;
    protected WaitUtils wait;
    protected ReusableHelperMethods helpers;
    protected Logger logger;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WaitUtils(driver);
        helpers = new ReusableHelperMethods();
        logger = LoggerUtils.getLogger(getClass());
    }
}

