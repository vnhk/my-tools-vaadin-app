import com.bervan.common.user.UserRepository;
import com.bervan.common.user.UserToUserRelationRepository;
import com.bervan.shstat.ProductService;
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
public class ProjectMgmtE2ETest extends BaseTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserToUserRelationRepository userToUserRelationRepository;
    @Autowired
    ProductService productService;
    int TOTAL_COLUMNS = 7;
    private ChromeDriver driver;
    private WebDriverWait webDriverWait;

    @Test
    @Order(0)
    public void setup() throws InterruptedException {
        super.setup(userRepository, userToUserRelationRepository);

        driver = Config.getDriver();
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(5));

        super.Login(driver);
        super.GoToApp(driver, "Project Management");
    }

    @Test
    @Order(1)
    public void testSimpleAddProject() throws InterruptedException {
        super.GoToAnotherViewInApp(driver, "Projects");
        Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(0, itemsInTable);
        AddNewProjectItem(driver, "Project A", "PR-A", "This is description for a new project!\n Very important project.");
        Thread.sleep(1500);

        itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(1, itemsInTable);

        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 1, 0, TOTAL_COLUMNS, "Project A"));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 2, 0, TOTAL_COLUMNS, "PR-A"));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 3, 0, TOTAL_COLUMNS, "Open"));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 4, 0, TOTAL_COLUMNS, "Medium"));

        //check description
    }

    @Test
    @Order(2)
    public void testSimpleAddTaskToProject() throws InterruptedException {
        super.GoToAnotherViewInApp(driver, "Projects");
        Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(1, itemsInTable);

        BervanTableCommon.ClickOnVaadinLinkIcon(driver);
        Thread.sleep(500);

        AddNewTaskItem(driver, "Test Task 1", "Task description 1");
        itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(1, itemsInTable);
    }


    @Test
    @Order(100)
    public void teardown() throws InterruptedException {
        if (driver != null) {
            driver.quit();
        }
    }

    private void AddNewProjectItem(ChromeDriver driver, String name, String number, String description) throws InterruptedException {
        Thread.sleep(500);
        BervanTableCommon.openAddItemModal(driver);
        WebElement element = driver.findElement(By.xpath("//vaadin-text-area[label[text()='Name']]//textarea"));
        element.sendKeys(name);
        element = driver.findElement(By.xpath("//vaadin-text-area[label[text()='Number']]//textarea"));
        element.sendKeys(number);

        element = driver.findElement(By.className("ql-editor"));
        driver.executeScript("arguments[0].scrollIntoView(true);", element);
        element.sendKeys(description);

        WebElement button = driver.findElement(By.xpath("//vaadin-button[contains(.,'Save')]"));
        button.click();
        Thread.sleep(1500);
    }

    private void AddNewTaskItem(ChromeDriver driver, String name, String description) throws InterruptedException {
        Thread.sleep(500);
        BervanTableCommon.openAddItemModal(driver);
        WebElement element = driver.findElement(By.xpath("//vaadin-text-area[label[text()='Name']]//textarea"));
        element.sendKeys(name);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        element = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@contenteditable='true']")));
        Thread.sleep(500);
        driver.executeScript("arguments[0].scrollIntoView(true);", element);
        element.sendKeys(description);

        Thread.sleep(500);
        WebElement button = driver.findElement(By.xpath("//vaadin-button[contains(.,'Save')]"));
        button.click();
        Thread.sleep(1500);
    }
}