import com.bervan.logging.JsonLogger;
import jakarta.validation.constraints.NotEmpty;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.time.Duration.ofSeconds;

public class BervanTableCommon {
    private static final JsonLogger log = JsonLogger.getLogger(BervanTableCommon.class, "test");

    public static Integer GetItemsInTable(ChromeDriver driver) {
        var pageInfo = driver.findElement(By.xpath("//span[@class='table-pageable-details']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", pageInfo);
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

    public static boolean EqualsColumnValueAsStr(ChromeDriver driver, int colIndex, int rowIndex, int totalColumns, @NotEmpty String expected) throws InterruptedException {
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


    public static boolean ContainsColumnValueAsStr(ChromeDriver driver, int colIndex, int rowIndex, int totalColumns, @NotEmpty String expected) throws InterruptedException {
        driver.executeScript("document.querySelector('vaadin-grid').scrollToIndex(arguments[0])", rowIndex);
        Thread.sleep(100);

        int index = rowIndex * totalColumns + colIndex;
        List<WebElement> allCells = driver.findElements(By.cssSelector("vaadin-grid .bervan-cell-component"));

        if (index >= allCells.size()) {
            throw new RuntimeException("Cell index out of bounds");
        }

        String text = allCells.get(index).getText();
        log.info("Text in column: {} (colI:{}, rowI:{})", text, colIndex, rowIndex);
        return text.contains(expected);
    }

    public static WebElement ClickOnGridColumn(ChromeDriver driver, int colIndex, int rowIndex, int totalColumns) throws InterruptedException {
        driver.executeScript("document.querySelector('vaadin-grid').scrollToIndex(arguments[0])", rowIndex);
        Thread.sleep(500);
        int index = rowIndex * totalColumns + colIndex;
        List<WebElement> allCells = driver.findElements(By.cssSelector("vaadin-grid .bervan-cell-component"));

        if (index >= allCells.size()) {
            throw new RuntimeException("Cell index out of bounds");
        }

        allCells.get(index).click();
        WebDriverWait webDriverWait = new WebDriverWait(driver, ofSeconds(10), ofSeconds(1));
        return webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//vaadin-dialog-overlay")));
    }

    public static void ClickOnVaadinLinkIcon(ChromeDriver driver) throws InterruptedException {
        WebDriverWait webDriverWait = new WebDriverWait(driver, ofSeconds(10), ofSeconds(1));
        WebElement until = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//vaadin-icon[@icon='vaadin:link']")));

        until.click();
        Thread.sleep(500);
    }

    public static void ClickSortableColumn(ChromeDriver driver, String columnText) throws InterruptedException {
        Thread.sleep(500);

        WebElement sortableCell = driver.findElement(By.xpath("//vaadin-grid-sorter[contains(.,'" + columnText + "')]"));
        sortableCell.click();
    }

    public static void EditTextInColumn(ChromeDriver driver, int colIndex, int rowIndex, int totalColumns, String newText) throws InterruptedException {
        Thread.sleep(1000);
        WebElement dialog = ClickOnGridColumn(driver, colIndex, rowIndex, totalColumns);
        WebElement textArea = dialog.findElement(By.xpath("//vaadin-text-area"));
        Thread.sleep(1000);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", textArea);
        Thread.sleep(1000);
        textArea.sendKeys(newText);
        Thread.sleep(1000);
        WebElement saveButton = dialog.findElement(By.xpath("//vaadin-button[contains(., 'Save')]"));
        saveButton.click();
        Thread.sleep(3000);
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

    public static void ClickOnRefreshTableButton(ChromeDriver driver) throws InterruptedException {
        WebElement refreshTableButton = new WebDriverWait(driver, ofSeconds(10), ofSeconds(1))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//vaadin-button[.//vaadin-icon[@icon='vaadin:refresh']]")));
        refreshTableButton.click();
        Thread.sleep(1500);
    }


    public static void OpenTableFilters(ChromeDriver driver) throws InterruptedException {
        WebElement filterButton = new WebDriverWait(driver, ofSeconds(10), ofSeconds(1)).until(ExpectedConditions
                .elementToBeClickable(By.xpath("//vaadin-icon[@icon='vaadin:filter']")));

        filterButton.click();
        Thread.sleep(500);
    }

    public static void SetTimePickerValue(ChromeDriver driver, String label, String time) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement timePicker = driver.findElement(By.xpath("//vaadin-time-picker[label[text()='" + label + "']]"));

        js.executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'));", timePicker, time);
    }

    public static void SelectMultiSelectComboBoxValues(ChromeDriver driver, String labelText, List<String> valuesToSelect, boolean addIfNotExists) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement labelElement = driver.findElement(By.xpath("//label[text()='" + labelText + "']"));
        String inputId = labelElement.getAttribute("for");

        WebElement multiComboBox = driver.findElement(By.xpath("//vaadin-multi-select-combo-box[input[@id='" + inputId + "']]"));

        WebElement input = multiComboBox.findElement(By.tagName("input"));

        for (String value : valuesToSelect) {
            Thread.sleep(800);
            js.executeScript("arguments[0].shadowRoot.querySelector('[part=\"toggle-button\"]').click();", multiComboBox);
            Thread.sleep(800);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            List<WebElement> items = new ArrayList<>();
            try {
                WebElement overlay = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("vaadin-multi-select-combo-box-overlay")));
                items = overlay.findElements(By.cssSelector("vaadin-multi-select-combo-box-item"));
            } catch (Exception e) {
                //no elements in dropdown!
            }

            boolean found = false;
            for (WebElement item : items) {
                if (item.getText().equals(value)) {
                    item.click();
                    found = true;
                    break;
                }
            }


            if (!found && addIfNotExists) {
                input.sendKeys(value);
                input.sendKeys(Keys.ENTER);
                Thread.sleep(500);
            }

            Thread.sleep(500);
        }

        js.executeScript("arguments[0].shadowRoot.querySelector('[part=\"toggle-button\"]').click();", multiComboBox);

        Thread.sleep(500);
    }

    public static void SelectDropdownValue(ChromeDriver driver, String labelText, String valueToSelect) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement labelElement = driver.findElement(By.xpath("//label[text()='" + labelText + "']"));

        String inputId = labelElement.getAttribute("for");

        WebElement comboBox = driver.findElement(By.xpath("//vaadin-combo-box[input[@id='" + inputId + "']]"));

        js.executeScript("arguments[0].scrollIntoView(true);", comboBox);

        js.executeScript("arguments[0].shadowRoot.querySelector('[part=\"toggle-button\"]').click();", comboBox);
        Thread.sleep(1000);

        WebElement overlay = driver.findElement(By.tagName("vaadin-combo-box-overlay"));
        List<WebElement> items = overlay.findElements(By.cssSelector("vaadin-combo-box-item"));

        for (WebElement item : items) {
            if (item.getText().equals(valueToSelect)) {
                item.click();
                break;
            }
        }

        Thread.sleep(1000);
    }
}