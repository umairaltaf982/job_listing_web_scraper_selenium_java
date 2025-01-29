package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.FileWriter;
import java.time.Duration;
import java.util.List;

public class WebScraper {
    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            driver.get("https://www.indeed.com/jobs?q=software+developer");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("job_seen_beacon")));

            List<WebElement> jobElements = driver.findElements(By.className("job_seen_beacon"));

            try (FileWriter fileWriter = new FileWriter("jobs.csv");
                 CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader("Job Title", "Company", "Link"))) {

                for (WebElement job : jobElements) {
                    try {
                        WebElement titleElement = job.findElement(By.cssSelector("h2 a"));
                        String jobTitle = titleElement.getText();
                        String jobLink = titleElement.getAttribute("href");

                        WebElement companyElement = job.findElement(By.cssSelector(".css-1h7lukg.eu4oa1w0"));
                        String companyName = companyElement.getText();

                        System.out.println("Job: " + jobTitle + " | Company: " + companyName);
                        csvPrinter.printRecord(jobTitle, companyName, jobLink);
                    } catch (Exception e) {
                        System.out.println("Error extracting job details ---> " + e.getMessage());
                    }
                }
            }
            System.out.println("✅ Job data saved in jobs.csv");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
