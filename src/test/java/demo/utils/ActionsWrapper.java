package demo.utils;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ActionsWrapper {
    /**
     * Clicks on the web element located by the given locator using the provided WebDriver instance.
     * Waits for the element to be visible before clicking.
     *
     * @param driver The WebDriver instance to use for locating the element and clicking.
     * @param locator The locator strategy used to find the web element.
     */
    public static void clickAW(WebDriver driver, By locator) {
        try {
            WSUtils.logStatus("clickAW", "Clicking");

            // Setting up WebDriverWait with a timeout of 10 seconds
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Waiting for the element to be visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

            // Locating the web element
            WebElement element = driver.findElement(locator);

            // Clicking the element
            element.click();
        } catch (Exception e) {
            WSUtils.logStatus("clickAW", "Exception while clicking\n" + e.getMessage());
        }
    }
}
