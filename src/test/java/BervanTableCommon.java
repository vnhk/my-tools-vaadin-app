import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static java.time.Duration.ofSeconds;

@Slf4j
public class BervanTableCommon {
    public static Integer GetItemsInTable(ChromeDriver driver) {
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

    public static boolean AssertColumnValueAsStr(ChromeDriver driver, int colIndex, int rowIndex, int totalColumns, @NotEmpty String expected) throws InterruptedException {
        driver.executeScript("document.querySelector('vaadin-grid').scrollToIndex(arguments[0])", rowIndex);
        Thread.sleep(100);

        int index = rowIndex * totalColumns + colIndex;
        List<WebElement> allCells = driver.findElements(By.cssSelector("vaadin-grid .bervan-cell-component"));

        if (index >= allCells.size()) {
            throw new RuntimeException("Cell index out of bounds");
        }

        String text = allCells.get(index).getText();
        log.info("Text in column: {} (colI:{}, rowI:{})", text, colIndex, rowIndex);
        return expected.equals(text);
    }

    public static void ClickOnGridColumn(ChromeDriver driver, int colIndex, int rowIndex, int totalColumns) throws InterruptedException {
        driver.executeScript("document.querySelector('vaadin-grid').scrollToIndex(arguments[0])", rowIndex);
        Thread.sleep(500);
        int index = rowIndex * totalColumns + colIndex;
        List<WebElement> allCells = driver.findElements(By.cssSelector("vaadin-grid .bervan-cell-component"));

        if (index >= allCells.size()) {
            throw new RuntimeException("Cell index out of bounds");
        }

        allCells.get(index).click();
        WebDriverWait webDriverWait = new WebDriverWait(driver, ofSeconds(10), ofSeconds(1));
        WebElement vaadinDialog = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//vaadin-dialog-overlay")));
    }

    public static void DeleteItemByColumnClick(ChromeDriver driver, int colIndex, int rowIndex, int totalColumns) throws InterruptedException {
        ClickOnGridColumn(driver, colIndex, rowIndex, totalColumns);
        WebElement deleteButton = driver.findElement(By.xpath("//vaadin-button[contains(., 'Delete Item')]"));
        deleteButton.click();
        Thread.sleep(1500);
    }

    public static void ClickCheckboxSelectByRow(ChromeDriver driver, int rowIndex) throws InterruptedException {
        WebElement webElement = driver.findElements(By.xpath("(//vaadin-grid-cell-content//vaadin-checkbox)")).get(rowIndex + 1);
        driver.executeScript("arguments[0].scrollIntoView(true);", webElement);
        Thread.sleep(250);

        webElement.click();
    }

    public static void DeleteSelected(WebDriverWait webDriverWait, ChromeDriver driver) throws InterruptedException {
        WebElement delete = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//vaadin-button[contains(.,'Delete')]")));

        delete.click();
        Thread.sleep(400);
        ConfirmYesConfirmVaadinDialog(driver);
        Thread.sleep(1500);
    }

    public static void ConfirmYesConfirmVaadinDialog(ChromeDriver driver) throws InterruptedException {
        WebElement confirmDialog = new WebDriverWait(driver, ofSeconds(10), ofSeconds(1)).until(ExpectedConditions.elementToBeClickable(By.xpath("//vaadin-confirm-dialog-overlay")));

        WebElement confirmButton = confirmDialog.findElement(By.xpath("//vaadin-button[@slot='confirm-button']"));
        Thread.sleep(500);
        confirmButton.click();
    }
}