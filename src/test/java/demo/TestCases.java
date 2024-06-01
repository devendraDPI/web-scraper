package demo;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import demo.utils.ActionsWrapper;
import demo.utils.WSUtils;

public class TestCases {
    WebDriver driver;

    @BeforeClass
    public void createDriver() {
        WSUtils.logStatus("createDriver", "Creating driver");

        // Setting up ChromeOptions with desired arguments
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");

        // Creating a Chrome WebDriver instance with the specified options
        driver = new ChromeDriver(options);

        // Implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        WSUtils.logStatus("createDriver", "Done driver creation");
    }

    @AfterClass
    public void quitDriver() {
        WSUtils.logStatus("quitDriver", "Quitting driver");
        // Quit the WebDriver instance
        if (driver != null) {
            driver.quit();
        }
        WSUtils.logStatus("quitDriver", "Done driver quit");
    }

    @BeforeMethod
    public void driverGet() {
        // Navigate to https://www.scrapethissite.com/pages
        String url = "https://www.scrapethissite.com/pages";
        WSUtils.driverGet(driver, url);
    }

    @Test(priority = 1, enabled = true, description = "Scrape and store hockey team data with Win % < 40% from multiple pages")
    public void TestCase001() {
        WSUtils.logStatus("TC001", "Start", "Scrape and store hockey team data with Win % < 40% from multiple pages");

        // Click on "Hockey Teams: Forms, Searching and Pagination"
        ActionsWrapper.clickAW(driver, By.xpath("//a[contains(text(), 'Hockey Teams')]"));

        // Iterate through the table and collect the Team Name, Year and Win % for the teams with Win % less than 40% (0.40) and iterate through 4 pages
        ArrayList<HashMap<String, Object>> data = WSUtils.getDataWithWinPercentage(driver, 4, "<", 40);

        // Convert the ArrayList<HashMap> object to a JSON file named "hockey-team-data.json"
        String outputDirectory = "output";
        String fileName = "hockey-team-data.json";
        WSUtils.dataToJson(data, outputDirectory, fileName);

        // Check if the JSON file exists
        File file = new File(outputDirectory + File.separator + fileName);

        // Assert if file exists
        Assert.assertTrue(file.exists(), String.format("'%s' file does not exist", fileName));
        WSUtils.logStatus("Step", String.format("'%s' file exists", fileName));

        // Assert if file is not empty
        Assert.assertTrue(file.length() > 0, String.format("'%s' file is empty", fileName));
        WSUtils.logStatus("Step", String.format("'%s' file is not empty", fileName));

        WSUtils.logStatus("TC001", "End", "Scrape and store hockey team data with Win % < 40% from multiple pages");
    }

    @Test(priority = 1, enabled = true, description = "Scrape and store top 5 Oscar winning film data from each year")
    public void TestCase002() {
        WSUtils.logStatus("TC002", "Start", "Scrape and store top 5 Oscar winning film data from each year");

        // Click on "Oscar Winning Films: AJAX and Javascript"
        ActionsWrapper.clickAW(driver, By.xpath("//a[contains(text(), 'Oscar Winning Films')]"));

        // Click on each year present on the screen and find the top 5 movies on the list
        ArrayList<HashMap<String, Object>> data = WSUtils.getTopNOscarOfEachYear(driver, 5);

        // Convert the ArrayList<HashMap> object to a JSON file named "oscar-winner-data.json"
        String outputDirectory = "output";
        String fileName = "oscar-winner-data.json";
        WSUtils.dataToJson(data, outputDirectory, fileName);

        // Check if the JSON file exists
        File file = new File(outputDirectory + File.separator + fileName);

        // Assert if file exists
        Assert.assertTrue(file.exists(), String.format("'%s' file does not exist", fileName));
        WSUtils.logStatus("Step", String.format("'%s' file exists", fileName));

        // Assert if file is not empty
        Assert.assertTrue(file.length() > 0, String.format("'%s' file is empty", fileName));
        WSUtils.logStatus("Step", String.format("'%s' file is not empty", fileName));

        WSUtils.logStatus("TC002", "End", "Scrape and store top 5 Oscar winning film data from each year");
    }
}
