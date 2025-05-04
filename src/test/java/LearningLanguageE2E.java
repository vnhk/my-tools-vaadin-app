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
public class LearningLanguageE2E extends BaseTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserToUserRelationRepository userToUserRelationRepository;

    private static void AddNewItem(ChromeDriver driver, String testText, String testTranslation, String testExamples, String testExamplesTranslation) throws InterruptedException {
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

    @BeforeEach
    public void setup() {
        super.setup(userRepository, userToUserRelationRepository);
    }

    private static Integer GetFlashcardsLeftAmount(WebDriverWait webDriverWait) {
        WebElement flashcardLeftInfo = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(., 'Flashcards left:')]")));
        return Integer.parseInt(flashcardLeftInfo.getText().split("Flashcards left: ")[1]);
    }

    @Test
    @Order(1)
    public void testSimpleAddRecords() throws InterruptedException {
        ChromeDriver driver = Config.getDriver();
        try {
            super.Login(driver);
            super.GoToApp(driver, "Learning Language");
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.urlToBe(baseUrl + "/learning-english-app/home"));
            Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
            Assertions.assertEquals(0, itemsInTable);
            AddNewItem(driver, "Test Text 0", "Test Translation 0", "Test Examples 0", "Test Examples Translation 0");
            AddNewItem(driver, "Test Text 1", "Test Translation 1", "Test Examples 1", "Test Examples Translation 1");
            AddNewItem(driver, "Test Text 2", "Test Translation 2", "Test Examples 2", "Test Examples Translation 2");

            Thread.sleep(1500);

            itemsInTable = BervanTableCommon.GetItemsInTable(driver);
            Assertions.assertEquals(3, itemsInTable);
        } finally {
            driver.quit();
        }

    }

    @Test
    @Order(2)
    public void testRecordActiveAndInactive() throws InterruptedException {
        ChromeDriver driver = Config.getDriver();
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            super.Login(driver);
            super.GoToApp(driver, "Learning Language");
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.urlToBe(baseUrl + "/learning-english-app/home"));
            Thread.sleep(1500);
            Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
            Assertions.assertEquals(3, itemsInTable);

            GoToAnotherViewInApp(driver, "Flashcards");
            Integer flashcardsLeftAmount = GetFlashcardsLeftAmount(webDriverWait);
            Assertions.assertEquals(3, flashcardsLeftAmount);

            GoToAnotherViewInApp(driver, "Home");
            BervanTableCommon.AssertColumnValueAsStr(driver, 8, 0, 10, "true");
            BervanTableCommon.AssertColumnValueAsStr(driver, 8, 1, 10, "true");
            BervanTableCommon.AssertColumnValueAsStr(driver, 8, 2, 10, "true");

            BervanTableCommon.clickCheckboxSelectAll(driver);
            BervanTableCommon.ClickCheckboxSelectByRow(driver, 0);
            DeactivateSelected(webDriverWait, driver);

            BervanTableCommon.AssertColumnValueAsStr(driver, 8, 0, 10, "true");
            BervanTableCommon.AssertColumnValueAsStr(driver, 8, 1, 10, "false");
            BervanTableCommon.AssertColumnValueAsStr(driver, 8, 2, 10, "false");

            GoToAnotherViewInApp(driver, "Flashcards");
            flashcardsLeftAmount = GetFlashcardsLeftAmount(webDriverWait);
            Assertions.assertEquals(1, flashcardsLeftAmount);

            GoToAnotherViewInApp(driver, "Home");
            BervanTableCommon.ClickCheckboxSelectByRow(driver, 0);
            DeactivateSelected(webDriverWait, driver);

            GoToAnotherViewInApp(driver, "Flashcards");
            flashcardsLeftAmount = GetFlashcardsLeftAmount(webDriverWait);
            Assertions.assertEquals(0, flashcardsLeftAmount);

            GoToAnotherViewInApp(driver, "Home");
            BervanTableCommon.ClickCheckboxSelectByRow(driver, 0);
            BervanTableCommon.ClickCheckboxSelectByRow(driver, 1);
            BervanTableCommon.ClickCheckboxSelectByRow(driver, 2);
            ActivateSelected(webDriverWait, driver);

            GoToAnotherViewInApp(driver, "Flashcards");
            flashcardsLeftAmount = GetFlashcardsLeftAmount(webDriverWait);
            Assertions.assertEquals(3, flashcardsLeftAmount);
        } finally {
            driver.quit();
        }
    }

    @Test
    @Order(4)
    public void testDeleteRecords() throws InterruptedException {
        ChromeDriver driver = Config.getDriver();
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            super.Login(driver);
            super.GoToApp(driver, "Learning Language");
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.urlToBe(baseUrl + "/learning-english-app/home"));
            Thread.sleep(1500);
            Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
            Assertions.assertEquals(3, itemsInTable);

            BervanTableCommon.ClickCheckboxSelectByRow(driver, 0);
            BervanTableCommon.ClickCheckboxSelectByRow(driver, 2);
            BervanTableCommon.DeleteSelected(webDriverWait, driver);

            itemsInTable = BervanTableCommon.GetItemsInTable(driver);
            Assertions.assertEquals(1, itemsInTable);

            Thread.sleep(3000);

            Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver,
                    1, 0, 10, "Test Text 1"));

            BervanTableCommon.DeleteItemByColumnClick(driver, 1, 0, 10);
            itemsInTable = BervanTableCommon.GetItemsInTable(driver);
            Assertions.assertEquals(0, itemsInTable);
        } finally {
            driver.quit();
        }

    }

    private void DeactivateSelected(WebDriverWait webDriverWait, ChromeDriver driver) throws InterruptedException {
        WebElement deActivateButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//vaadin-button[contains(.,'Deactivate')]")));

        deActivateButton.click();
        Thread.sleep(400);
        BervanTableCommon.ConfirmYesConfirmVaadinDialog(driver);
        Thread.sleep(400);
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