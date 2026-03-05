package com.yourcompany.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import com.yourcompany.hooks.Hooks;

/**
 * TestNG Cucumber runner class
 */
@CucumberOptions(
    features = "src/test/resources",
    glue = {"com.yourcompany.stepdefinitions", "com.yourcompany.hooks"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports",
        "json:target/cucumber-reports/Cucumber.json",
        "junit:target/cucumber-reports/Cucumber.xml"
    },
    monochrome = true,
    dryRun = false
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }

    @AfterClass
    public void tearDownClass() {
        Hooks.closeDriver();
    }
}