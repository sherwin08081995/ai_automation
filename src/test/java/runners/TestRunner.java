package runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = { "src/test/resources/login.feature" },
        glue = {"stepDefinitions", "hooks"},
        plugin = {"pretty",
                "html:target/cucumber-reports/cucumber-html-report.html",
                "json:target/cucumber-reports/Cucumber.json"
        },
        monochrome = true
)

public class TestRunner extends AbstractTestNGCucumberTests {
}
