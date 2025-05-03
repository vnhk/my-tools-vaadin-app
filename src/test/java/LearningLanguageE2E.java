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

    @Test
    @Order(1)
    public void testSimpleAddRecords() throws InterruptedException {
        ChromeDriver driver = Config.getDriver();
        try {
            super.login(driver);
            super.goToApp(driver, "Learning Language");
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.urlToBe(baseUrl + "/learning-english-app/home"));
            Integer itemsInTable = BervanTableCommon.getItemsInTable(driver);
            Assertions.assertEquals(0, itemsInTable);
            AddNewItem(driver, "Test Text 0", "Test Translation 0", "Test Examples 0", "Test Examples Translation 0");
            AddNewItem(driver, "Test Text 1", "Test Translation 1", "Test Examples 1", "Test Examples Translation 1");
            AddNewItem(driver, "Test Text 2", "Test Translation 2", "Test Examples 2", "Test Examples Translation 2");

            Thread.sleep(1500);

            itemsInTable = BervanTableCommon.getItemsInTable(driver);
            Assertions.assertEquals(3, itemsInTable);
        } finally {
            driver.quit();
        }

    }

    @Test
    @Order(2)
    public void testRecordActiveAndInactive() throws InterruptedException {
        ChromeDriver driver = Config.getDriver();
        try {
            super.login(driver);
            super.goToApp(driver, "Learning Language");
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.urlToBe(baseUrl + "/learning-english-app/home"));
            Thread.sleep(1500);
            Integer itemsInTable = BervanTableCommon.getItemsInTable(driver);
            Assertions.assertEquals(3, itemsInTable);

            BervanTableCommon.assertColumnValueAsStr(driver, 8, 0, 10, "true");
            BervanTableCommon.assertColumnValueAsStr(driver, 8, 1, 10, "true");
            BervanTableCommon.assertColumnValueAsStr(driver, 8, 2, 10, "true");

            BervanTableCommon.clickCheckboxSelectAll(driver);
            BervanTableCommon.clickCheckboxSelectByRow(driver, 0);

            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement deActivateButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//vaadin-button[contains(.,'Deactivate')]")));

            deActivateButton.click();
            Thread.sleep(400);
            confirmYesConfirmVaadinDialog(driver);

            Thread.sleep(400);

            BervanTableCommon.assertColumnValueAsStr(driver, 8, 0, 10, "true");
            BervanTableCommon.assertColumnValueAsStr(driver, 8, 1, 10, "false");
            BervanTableCommon.assertColumnValueAsStr(driver, 8, 2, 10, "false");
        } finally {
            driver.quit();
        }

    }

}