import jakarta.validation.constraints.NotEmpty;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static java.time.Duration.ofSeconds;

public class BervanTableCommon {
    public static Integer getItemsInTable(ChromeDriver driver) {
        var pageInfo = driver.findElement(By.xpath("//span[@class='table-pageable-details']"));
        return Integer.parseInt(pageInfo.getText().split(",")[0].replace("Items: ", "").trim());
    }

    public static void openAddItemModal(ChromeDriver driver) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//vaadin-button[.//vaadin-icon[@icon='vaadin:plus']]")));

        driver.executeScript("arguments[0].scrollIntoView(true);", button);
        Thread.sleep(500);

        button.click();

        new WebDriverWait(driver, ofSeconds(10), ofSeconds(1))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//vaadin-button[.//vaadin-icon[@icon='vaadin:close']]")));
    }

    public static void clickCheckboxSelectAll(ChromeDriver driver) throws InterruptedException {
        WebElement webElement = driver.findElements(By.xpath("(//vaadin-grid-cell-content//vaadin-checkbox)")).get(0);
        driver.executeScript("arguments[0].scrollIntoView(true);", webElement);
        Thread.sleep(250);

        webElement.click();
    }

    public static boolean assertColumnValueAsStr(ChromeDriver driver, int colIndex, int rowIndex, int totalColumns, @NotEmpty String expected) throws InterruptedException {
        driver.executeScript("document.querySelector('vaadin-grid').scrollToIndex(arguments[0])", rowIndex);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int index = rowIndex * totalColumns + colIndex;
        List<WebElement> allCells = driver.findElements(By.cssSelector("vaadin-grid .bervan-cell-component"));

        if (index >= allCells.size()) {
            throw new RuntimeException("Cell index out of bounds");
        }

        return expected.equals(allCells.get(index).getText());
    }

    public static void clickCheckboxSelectByRow(ChromeDriver driver, int rowIndex) throws InterruptedException {
        WebElement webElement = driver.findElements(By.xpath("(//vaadin-grid-cell-content//vaadin-checkbox)")).get(rowIndex + 1);
        driver.executeScript("arguments[0].scrollIntoView(true);", webElement);
        Thread.sleep(250);

        webElement.click();
    }
}