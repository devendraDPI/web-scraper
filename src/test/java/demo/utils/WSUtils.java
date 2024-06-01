package demo.utils;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class WSUtils {
    /**
     * Logs a status message with the specified method name and message to the console.
     *
     * @param methodName The name of the method associated with the log message.
     * @param message The message to be logged.
     */
    public static void logStatus(String methodName, String message) {
        System.out.println(String.format("%s | %s | %s", getDateTime("yyyy-MM-dd HH:mm:ss"), methodName, message));
    }

    /**
     * Logs a status message with the specified test case ID, test step, and test message to the console.
     *
     * @param testCaseID The ID of the test case associated with the log message.
     * @param testStep The step of the test case associated with the log message.
     * @param testMessage The message related to the test step.
     */
    public static void logStatus(String testCaseID, String testStep, String testMessage) {
        System.out.println(String.format("\n%s | %s | %s | %s\n", getDateTime("yyyy-MM-dd HH:mm:ss"), testCaseID, testStep, testMessage));
    }

    /**
     * Retrieves the current date and time in the specified format pattern.
     *
     * @param formatPattern The pattern used to format the date and time (e.g., "yyyy-MM-dd HH:mm:ss").
     * @return A string representing the current date and time formatted according to the specified pattern.
     */
    public static String getDateTime(String formatPattern) {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern(formatPattern));
    }

    /**
     * Navigates to the specified URL using the provided WebDriver and waits for the page to load completely.
     *
     * @param driver The WebDriver instance to use for navigation.
     * @param url The URL to navigate to.
     */
    public static void driverGet(WebDriver driver, String url) {
        try {
            logStatus("driverGet", "Navigate to '"+ url +"' and wait for the page to load completely");

            // Navigate to the URL
            driver.get(url);

            // Wait for the page to load completely
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState == 'complete'"));
        } catch (Exception e) {
            logStatus("driverGet", "Exception\n" + e.getMessage());
        }
    }

    /**
     * Retrieves data from a website based on win percentage criteria.
     *
     * @param driver The WebDriver instance to use for navigation.
     * @param pages The number of pages to scrape.
     * @param operator The comparison operator for win percentage (e.g., "<", "<=", "=", ">=", ">").
     * @param percentage The win percentage threshold.
     * @return A list of hashmaps containing scraped data.
     */
    public static ArrayList<HashMap<String, Object>> getDataWithWinPercentage(WebDriver driver, int pages, String operator, double percentage) {
        logStatus("getDataWithWinPercentage", String.format("Getting data with win percentage %s %.2f%%", operator, percentage));

        // Initialize list to store scraped data
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        try {
            // Loop through specified number of pages
            for (int page=1; page<=pages; page++) {
                // Navigate to the page
                driverGet(driver, "https://www.scrapethissite.com/pages/forms/?page_num=" + page);

                // Find the table containing the data
                WebElement table = driver.findElement(By.xpath("//table[contains(@class, 'table')]"));

                // Find rows matching win percentage criteria
                List<WebElement> tableRow = table.findElements(By.xpath(".//td[contains(@class, 'pct')][text() "+ operator +" "+ (percentage/100) +"]/parent::tr"));

                // Iterate through each matching row
                for (WebElement cell : tableRow) {
                    // Extract data from table cells
                    String epochTime = String.valueOf(getEpochTime());
                    String teamName = cell.findElement(By.xpath("./td[contains(@class, 'name')]")).getText();
                    String year = cell.findElement(By.xpath("./td[contains(@class, 'year')]")).getText();
                    String winPercentage = cell.findElement(By.xpath("./td[contains(@class, 'pct')]")).getText();

                    // Create hashmap to store data for current row
                    HashMap<String, Object> hm = new HashMap<>();
                    hm.put("epochTime", epochTime);
                    hm.put("teamName", teamName);
                    hm.put("year", year);
                    hm.put("winPercentage", winPercentage);

                    // Add hashmap to list of data
                    data.add(hm);
                }
            }
        } catch (Exception e) {
            logStatus("getDataWithWinPercentage", "An error occurred while scraping data\n" + e.getMessage());
        }
        return data;
    }

    /**
     * Converts an ArrayList of HashMaps to JSON format and stores it in a file.
     *
     * @param data The ArrayList of HashMaps to be converted.
     * @param outputDirectory The directory where the JSON file will be stored.
     * @param fileName The name of the JSON file.
     */
    public static void dataToJson(ArrayList<HashMap<String, Object>> data, String outputDirectory, String fileName) {
        try {
            logStatus("dataToJson", "Converting and storing ArrayList to JSON");

            // Create ObjectMapper instance
            ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.INDENT_OUTPUT);

            // Create output directory if it doesn't exist
            File outputDir = new File(outputDirectory);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // Create output file
            File outputFile = new File(outputDir, fileName);

            // Write data to JSON file
            om.writeValue(outputFile, data);
        } catch (Exception e) {
            logStatus("dataToJson", "An error occurred while converting and storing ArrayList to JSON\n" + e.getMessage());
        }
    }

    /**
     * Get the current epoch time in seconds.
     *
     * @return The current epoch time in seconds.
     */
    private static long getEpochTime() {
        return System.currentTimeMillis()/1000;
    }

    /**
     * Retrieves top N Oscar movie data for each year from a webpage.
     *
     * @param driver The WebDriver instance to use for navigation.
     * @param topOscar The number of top Oscars to retrieve for each year.
     * @return A list of hashmaps containing the scraped data.
     */
    public static ArrayList<HashMap<String, Object>> getTopNOscarOfEachYear(WebDriver driver, int topOscar) {
        logStatus("getTopNOscarOfEachYear", String.format("Getting data with top %s oscars", topOscar));

        // Initialize list to store scraped data
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        try {
            // Find elements containing year information
            List<WebElement> years = driver.findElements(By.xpath("//a[contains(@class, 'year-link')]"));

            // Set implicit wait time
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1));

            // Iterate through each year element
            for (WebElement year : years) {
                // Click on the year element
                year.click();
                FluentWait<WebDriver> fWait = new FluentWait<WebDriver>(driver)
                                            .withTimeout((Duration.ofSeconds(10)))
                                            .pollingEvery(Duration.ofMillis(250));
                fWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table")));

                // Find table containing data
                WebElement table = driver.findElement(By.xpath("//table[contains(@class, 'table')]"));

                // Find top N table rows for each year
                List<WebElement> tableRows = table.findElements(By.xpath(".//tr[contains(@class, 'film')][position() <= "+ topOscar +"]"));

                // Iterate through each table row
                for (WebElement cell : tableRows) {
                    // Extract data from table cells
                    String epochTime = String.valueOf(getEpochTime());
                    String yr = year.getText();
                    String title = cell.findElement(By.xpath("./td[contains(@class, 'title')]")).getText();
                    String nomination = cell.findElement(By.xpath("./td[contains(@class, 'nomination')]")).getText();
                    String awards = cell.findElement(By.xpath("./td[contains(@class, 'awards')]")).getText();
                    Boolean isWinner = false;
                    try {
                        isWinner = cell.findElement(By.xpath("./td[contains(@class, 'best-picture')]/i")).isDisplayed();
                    } catch (Exception e) {}

                    // Create hashmap to store data for current row
                    HashMap<String, Object> hm = new HashMap<>();
                    hm.put("epochTime", epochTime);
                    hm.put("year", yr);
                    hm.put("title", title);
                    hm.put("nomination", nomination);
                    hm.put("awards", awards);
                    hm.put("isWinner", isWinner);

                    // Add hashmap to list of data
                    data.add(hm);
                }
            }

            // Reset implicit wait time
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        } catch (Exception e) {
            logStatus("getTopNOscarOfEachYear", "An error occurred while scraping data\n" + e.getMessage());
        }
        return data;
    }
}
