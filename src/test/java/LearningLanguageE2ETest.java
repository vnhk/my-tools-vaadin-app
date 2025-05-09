import com.bervan.common.user.UserRepository;
import com.bervan.common.user.UserToUserRelationRepository;
import com.bervan.toolsapp.Application;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = Application.class)
@TestPropertySource("classpath:application-it.properties")
@ActiveProfiles("it")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LearningLanguageE2ETest extends BaseTest {

    private final int TOTAL_COLUMNS = 10;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserToUserRelationRepository userToUserRelationRepository;
    private ChromeDriver driver;
    private WebDriverWait webDriverWait;

    private static void AddNewItem(ChromeDriver driver, String testText, String testTranslation, String testExamples, String testExamplesTranslation) throws InterruptedException {
        Thread.sleep(500);
        BervanTableCommon.openAddItemModal(driver);
        WebElement element = driver.findElement(By.xpath("//vaadin-text-area[label[text()='Text']]//textarea"));
        element.sendKeys(testText);
        element = driver.findElement(By.xpath("//vaadin-text-area[label[text()='Translation']]//textarea"));
        element.sendKeys(testTranslation);

        element = driver.findElement(By.xpath("//vaadin-text-area[label[text()='Examples']]//textarea"));
        driver.executeScript("arguments[0].scrollIntoView(true);", element);
        element.sendKeys(testExamples);

        element = driver.findElements(By.xpath("//vaadin-text-area[label[text()='Translation']]//textarea")).get(1);
        driver.executeScript("arguments[0].scrollIntoView(true);", element);
        element.sendKeys(testExamplesTranslation);

        WebElement button = driver.findElement(By.xpath("//vaadin-button[contains(.,'Save')]"));
        button.click();
    }

    private static Integer GetFlashcardsLeftAmount(WebDriverWait webDriverWait) {
        WebElement flashcardLeftInfo = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(., 'Flashcards left:')]")));
        return Integer.parseInt(flashcardLeftInfo.getText().split("Flashcards left: ")[1]);
    }

    @Test
    @Order(0)
    public void setup() throws InterruptedException {
        super.setup(userRepository, userToUserRelationRepository);
        driver = Config.getDriver();
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(5));

        super.Login(driver);
        super.GoToApp(driver, "Learning Language");
    }

    @Test
    @Order(100)
    public void teardown() throws InterruptedException {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    public void testSimpleAddRecords() throws InterruptedException {
        super.GoToAnotherViewInApp(driver, "Home");
        Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(0, itemsInTable);
        AddNewItem(driver, "Text 0", "Test Translation 0", "Test Examples 0", "Test Examples Translation 0");
        AddNewItem(driver, "Text 1", "Test Translation 1", "Test Examples 1", "Test Examples Translation 1");
        AddNewItem(driver, "Text 2", "Test Translation 2", "Test Examples 2", "Test Examples Translation 2");
        Thread.sleep(1500);

        itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(3, itemsInTable);

        BervanTableCommon.ClickSortableColumn(driver, "Text");

        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 1, 0, TOTAL_COLUMNS, "Text 0"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 2, 0, TOTAL_COLUMNS, "N/A"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 3, 0, TOTAL_COLUMNS, "Test Translation 0"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 4, 0, TOTAL_COLUMNS, "Test Examples 0"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 5, 0, TOTAL_COLUMNS, "Test Examples Translation 0"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 8, 0, TOTAL_COLUMNS, "true"));

        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 1, 1, TOTAL_COLUMNS, "Text 1"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 2, 1, TOTAL_COLUMNS, "N/A"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 3, 1, TOTAL_COLUMNS, "Test Translation 1"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 4, 1, TOTAL_COLUMNS, "Test Examples 1"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 5, 1, TOTAL_COLUMNS, "Test Examples Translation 1"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 8, 1, TOTAL_COLUMNS, "true"));

        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 1, 2, TOTAL_COLUMNS, "Text 2"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 2, 2, TOTAL_COLUMNS, "N/A"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 3, 2, TOTAL_COLUMNS, "Test Translation 2"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 4, 2, TOTAL_COLUMNS, "Test Examples 2"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 5, 2, TOTAL_COLUMNS, "Test Examples Translation 2"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 8, 2, TOTAL_COLUMNS, "true"));
    }

    @Test
    @Order(2)
    public void testRecordsEdit() throws InterruptedException {
        super.GoToAnotherViewInApp(driver, "Home");
        Thread.sleep(1500);
        Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(3, itemsInTable);

        BervanTableCommon.EditTextInColumn(driver, 1, 0, TOTAL_COLUMNS, "New Test Text 0");
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 1, 0, TOTAL_COLUMNS, "New Test Text 0"));

        BervanTableCommon.EditTextInColumn(driver, 3, 0, TOTAL_COLUMNS, "New Test Translation 0");
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 3, 0, TOTAL_COLUMNS, "New Test Translation 0"));

        BervanTableCommon.EditTextInColumn(driver, 4, 0, TOTAL_COLUMNS, "New Test Examples 0");
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 4, 0, TOTAL_COLUMNS, "New Test Examples 0"));

        BervanTableCommon.EditTextInColumn(driver, 5, 0, TOTAL_COLUMNS, "New Test Examples Translation 0");
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 5, 0, TOTAL_COLUMNS, "New Test Examples Translation 0"));

    }

    @Test
    @Order(3)
    public void testRecordActiveAndInactive() throws InterruptedException {
        super.GoToAnotherViewInApp(driver, "Home");
        Thread.sleep(1500);
        Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(3, itemsInTable);

        GoToAnotherViewInApp(driver, "Flashcards");
        Integer flashcardsLeftAmount = GetFlashcardsLeftAmount(webDriverWait);
        Assertions.assertEquals(3, flashcardsLeftAmount);

        GoToAnotherViewInApp(driver, "Home");
        BervanTableCommon.ClickSortableColumn(driver, "Text");

        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 8, 0, TOTAL_COLUMNS, "true"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 8, 1, TOTAL_COLUMNS, "true"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 8, 2, TOTAL_COLUMNS, "true"));

        BervanTableCommon.clickCheckboxSelectAll(driver);
        BervanTableCommon.ClickCheckboxSelectByRow(driver, 0);
        DeactivateSelected(webDriverWait, driver);

        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 8, 0, TOTAL_COLUMNS, "true"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 8, 1, TOTAL_COLUMNS, "false"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 8, 2, TOTAL_COLUMNS, "false"));

        GoToAnotherViewInApp(driver, "Flashcards");
        flashcardsLeftAmount = GetFlashcardsLeftAmount(webDriverWait);
        Assertions.assertEquals(1, flashcardsLeftAmount);

        GoToAnotherViewInApp(driver, "Home");
        BervanTableCommon.ClickSortableColumn(driver, "Text");

        Thread.sleep(500);

        BervanTableCommon.ClickCheckboxSelectByRow(driver, 0);
        DeactivateSelected(webDriverWait, driver);

        GoToAnotherViewInApp(driver, "Flashcards");
        flashcardsLeftAmount = GetFlashcardsLeftAmount(webDriverWait);
        Assertions.assertEquals(0, flashcardsLeftAmount);

        GoToAnotherViewInApp(driver, "Home");
        BervanTableCommon.ClickSortableColumn(driver, "Text");

        Thread.sleep(500);

        BervanTableCommon.ClickCheckboxSelectByRow(driver, 0);
        BervanTableCommon.ClickCheckboxSelectByRow(driver, 1);
        BervanTableCommon.ClickCheckboxSelectByRow(driver, 2);
        ActivateSelected(webDriverWait, driver);

        GoToAnotherViewInApp(driver, "Flashcards");
        flashcardsLeftAmount = GetFlashcardsLeftAmount(webDriverWait);
        Assertions.assertEquals(3, flashcardsLeftAmount);
    }

    @Test
    @Order(4)
    public void testDeleteRecords() throws InterruptedException {
        super.GoToAnotherViewInApp(driver, "Home");
        BervanTableCommon.ClickSortableColumn(driver, "Text");

        Thread.sleep(500);

        Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(3, itemsInTable);

        BervanTableCommon.ClickCheckboxSelectByRow(driver, 0);
        BervanTableCommon.ClickCheckboxSelectByRow(driver, 2);
        BervanTableCommon.DeleteSelected(webDriverWait, driver);

        itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(1, itemsInTable);

        Thread.sleep(5000);

        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver,
                1, 0, TOTAL_COLUMNS, "Text 1"));

        BervanTableCommon.DeleteItemByColumnClick(driver, 1, 0, TOTAL_COLUMNS);
        itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(0, itemsInTable);
    }

    private void DeactivateSelected(WebDriverWait webDriverWait, ChromeDriver driver) throws InterruptedException {
        WebElement deActivateButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//vaadin-button[contains(.,'Deactivate')]")));

        deActivateButton.click();
        Thread.sleep(1500);
        BervanTableCommon.ConfirmYesConfirmVaadinDialog(driver);
        Thread.sleep(1500);
    }

    private void ActivateSelected(WebDriverWait webDriverWait, ChromeDriver driver) throws InterruptedException {
        WebElement activateButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//vaadin-button[contains(.,'Activate')]")));

        activateButton.click();
        Thread.sleep(400);
        BervanTableCommon.ConfirmYesConfirmVaadinDialog(driver);
        Thread.sleep(400);
    }

}