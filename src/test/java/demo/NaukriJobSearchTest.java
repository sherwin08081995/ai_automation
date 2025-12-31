package demo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NaukriJobSearchTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private NaukriSearchTest home;

    private Workbook workbook;
    private Sheet sheet;
    private int rowNum = 1;

    // 🔹 ALL SEARCH TERMS
    private static final List<String> SEARCH_TERMS = List.of(
            "Senior Legal Officer",
            "Sr. Legal Officer",
            "GM – Legal",
            "DGM – Legal",
            "AGM – Legal",
            "Manager – Legal",
            "Sr. Manager – Legal",
            "Head – Legal",
            "AVP – Legal",
            "VP – Legal",
            "Legal Manager – Corporate",
            "Corporate Advocate",
            "Corporate Legal Counsel",
            "In-House Counsel",
            "Legal Executive",
            "Compliance Officer",
            "Senior Compliance Officer",
            "Legal Counsel – Fintech / BFSI",
            "Contract Manager – Legal",
            "Legal Drafting Specialist",
            "IT Legal Counsel",
            "Data Protection Officer",
            "Legal Counsel – Regulatory & Compliance"
    );

    // 🔹 Deduplication (same job appearing in multiple searches)
    private final Set<String> seenJobs = new HashSet<>();

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        if (headless) options.addArguments("--headless=new");

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        home = new NaukriSearchTest(driver);

        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Legal Jobs");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Search Term");
        header.createCell(1).setCellValue("Job Title");
        header.createCell(2).setCellValue("Company");
        header.createCell(3).setCellValue("Experience");
        header.createCell(4).setCellValue("Location");
        header.createCell(5).setCellValue("Job URL");
        header.createCell(6).setCellValue("Scraped At");
    }

    @Test
    public void scrapeAllLegalRoles() throws Exception {

        for (String term : SEARCH_TERMS) {

            System.out.println("\n🔍 Searching: " + term);

            home.open();
            home.enterSkillAndSelect(term);
            home.clickSearch();

            List<WebElement> jobs = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector(".srp-jobtuple-wrapper, article.jobTuple")
                    )
            );

            for (WebElement job : jobs) {

                String title;
                try {
                    title = job.findElement(
                            By.cssSelector(".title, .jobTuple-title a")
                    ).getText().trim();
                } catch (Exception e) {
                    continue;
                }

                // ✅ NO "legal" FILTER ANYMORE

                String company = getText(
                        job,
                        "a.comp-name, .companyInfo, .jobTuple-companyName"
                );

                String exp = getText(job, ".expwdth, .experience");
                String loc = getText(job, ".locWdth, .location");

                String url = "";
                try {
                    url = job.findElement(
                            By.cssSelector(".title a, .jobTuple-title a")
                    ).getAttribute("href");
                } catch (Exception ignored) {}

                // dedupe across searches
                String key = title + company + loc;
                if (!seenJobs.add(key)) continue;

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(term);
                row.createCell(1).setCellValue(title);
                row.createCell(2).setCellValue(company);
                row.createCell(3).setCellValue(exp);
                row.createCell(4).setCellValue(loc);
                row.createCell(5).setCellValue(url);
                row.createCell(6).setCellValue(
                        LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        )
                );

                System.out.println("✔ Saved: " + title + " | " + company);
            }
        }

        for (int i = 0; i <= 6; i++) sheet.autoSizeColumn(i);
        saveExcel();
    }

    private String getText(WebElement parent, String css) {
        try {
            return parent.findElement(By.cssSelector(css)).getText().trim();
        } catch (Exception e) {
            return "N/A";
        }
    }

    private void saveExcel() throws Exception {
        Path outDir = Paths.get("target");
        Files.createDirectories(outDir);

        String fileName =
                "naukri_multi_role_results_" +
                        LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                        ) +
                        ".xlsx";

        Path file = outDir.resolve(fileName);

        try (FileOutputStream fos = new FileOutputStream(file.toFile())) {
            workbook.write(fos);
        }

        System.out.println("\n📁 Excel saved at: " + file.toAbsolutePath());
    }

    @AfterClass
    public void tearDown() throws Exception {
        if (driver != null) driver.quit();
        if (workbook != null) workbook.close();
    }
}
