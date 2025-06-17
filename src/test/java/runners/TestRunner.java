package runners;

import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.AbstractTestNGCucumberTests;


@CucumberOptions(
        features = {
                "src/test/resources",
        },
        glue = {"stepDefinitions", "hooks"},
        plugin = {"pretty",
                "html:target/cucumber-reports/cucumber-html-report.html",
                "json:target/cucumber-reports/Cucumber.json"
        },
        monochrome = true
)

public class TestRunner extends AbstractTestNGCucumberTests {
}
