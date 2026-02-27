package com.yourcompany.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestNG runner for Cucumber BDD tests.
 * Configures feature files location and step definitions package.
 */
@CucumberOptions(
    features = "src/test/resources",
    glue = {
        "com.yourcompany.stepdefinitions",
        "com.yourcompany.hooks"
    },
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
    
    /**
     * Enable parallel execution of scenarios
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}