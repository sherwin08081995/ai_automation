package com.yourcompany.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources",
    glue = {"com.yourcompany.stepdefinitions", "com.yourcompany.hooks"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber-html-report",
        "json:target/cucumber-reports/cucumber.json",
        "junit:target/cucumber-reports/cucumber.xml"
    },
    monochrome = true,
    dryRun = false,
    tags = "@smoke or @regression"
)
public class TestRunner extends AbstractTestNGCucumberTests {
    
}