import com.bervan.common.user.User;
import com.bervan.common.user.UserRepository;
import com.bervan.common.user.UserToUserRelationRepository;
import com.bervan.logging.LogEntity;
import com.bervan.logging.LogService;
import com.bervan.toolsapp.Application;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = Application.class)
@TestPropertySource("classpath:application-it.properties")
@ActiveProfiles("it")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LogsE2ETest extends BaseTest {

    private final int TOTAL_COLUMNS = 2;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserToUserRelationRepository userToUserRelationRepository;
    @Autowired
    LogService logService;
    private ChromeDriver driver;
    private WebDriverWait webDriverWait;

    @Test
    @Order(0)
    public void setup() throws InterruptedException {
        super.setup(userRepository, userToUserRelationRepository);
        Optional<User> commonUser = userRepository.findByUsername("COMMON_USER");
        LogEntity entity = getLogEntity(commonUser, "test-app-1", "INFO", "Example Log Message", LocalDateTime.of(2025, 5, 5, 10, 23), "LogsE2ETest", 50, "exampleMethodName3");
        logService.save(entity);

        entity = getLogEntity(commonUser, "test-app-1", "ERROR", "Example ERROR Message", LocalDateTime.of(2025, 5, 5, 10, 22), "LogsE2ETest", 54, "exampleMethodName2");
        logService.save(entity);

        entity = getLogEntity(commonUser, "test-app-1", "DEBUG", "Example DEBUG Message", LocalDateTime.of(2025, 5, 5, 10, 21), "LogsE2ETest", 56, "exampleMethodName1");
        logService.save(entity);

        //app 2
        entity = getLogEntity(commonUser, "test-app-2", "INFO", "Example Log Message", LocalDateTime.of(2025, 5, 5, 10, 24), "LogsE2ETest", 50, "exampleMethodName40");
        logService.save(entity);

        entity = getLogEntity(commonUser, "test-app-2", "ERROR", "Example ERROR Message", LocalDateTime.of(2025, 5, 5, 10, 23), "LogsE2ETest", 54, "exampleMethodName30");
        logService.save(entity);

        entity = getLogEntity(commonUser, "test-app-2", "DEBUG", "Example DEBUG Message", LocalDateTime.of(2025, 5, 5, 10, 22), "LogsE2ETest", 56, "exampleMethodName20");
        logService.save(entity);

        entity = getLogEntity(commonUser, "test-app-2", "DEBUG", "Example DEBUG Message", LocalDateTime.of(2025, 5, 5, 10, 21), "LogsE2ETest", 56, "exampleMethodName10");
        logService.save(entity);


        driver = Config.getDriver();
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(5));

        super.Login(driver);
        super.GoToApp(driver, "Logs");
    }

    private static @NotNull LogEntity getLogEntity(Optional<User> commonUser, String applicationName, String logLevel, String exampleLogMessage, LocalDateTime timestamp, String className, int lineNumber, String exampleMethodName) {
        LogEntity entity = new LogEntity();
        entity.setApplicationName(applicationName);
        entity.setLogLevel(logLevel);
        entity.setMessage(exampleLogMessage);
        entity.setTimestamp(timestamp);
        entity.setClassName(className);
        entity.setLineNumber(lineNumber);
        entity.setMethodName(exampleMethodName);

        entity.addOwner(commonUser.get());
        return entity;
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
    public void testSimpleLogsLoading() throws InterruptedException {
        super.GoToAnotherViewInApp(driver, "Logs");
        Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(3, itemsInTable);

        //assure logs are correct and in correct order by timestamp!
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 0, 0, TOTAL_COLUMNS, "DEBUG"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 1, 0, TOTAL_COLUMNS, "2025-05-05 10-21-00:000 LogsE2ETest:exampleMethodName1:56 - Example DEBUG Message"));

        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 0, 1, TOTAL_COLUMNS, "ERROR"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 1, 1, TOTAL_COLUMNS, "2025-05-05 10-22-00:000 LogsE2ETest:exampleMethodName2:54 - Example ERROR Message"));

        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 0, 2, TOTAL_COLUMNS, "INFO"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 1, 2, TOTAL_COLUMNS, "2025-05-05 10-23-00:000 LogsE2ETest:exampleMethodName3:50 - Example Log Message"));
    }

    @Test
    @Order(2)
    public void testChangeLogsAppLoading() throws InterruptedException {
        super.GoToAnotherViewInApp(driver, "Logs");

        ChangeAppName("test-app-2");

        Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(4, itemsInTable);

        //assure logs are correct and in correct order by timestamp!
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 0, 0, TOTAL_COLUMNS, "DEBUG"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 1, 0, TOTAL_COLUMNS, "2025-05-05 10-21-00:000 LogsE2ETest:exampleMethodName10:56 - Example DEBUG Message"));

        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 0, 1, TOTAL_COLUMNS, "DEBUG"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 1, 1, TOTAL_COLUMNS, "2025-05-05 10-22-00:000 LogsE2ETest:exampleMethodName20:56 - Example DEBUG Message"));

        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 0, 2, TOTAL_COLUMNS, "ERROR"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 1, 2, TOTAL_COLUMNS, "2025-05-05 10-23-00:000 LogsE2ETest:exampleMethodName30:54 - Example ERROR Message"));

        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 0, 3, TOTAL_COLUMNS, "INFO"));
        Assertions.assertTrue(BervanTableCommon.AssertColumnValueAsStr(driver, 1, 3, TOTAL_COLUMNS, "2025-05-05 10-24-00:000 LogsE2ETest:exampleMethodName40:50 - Example Log Message"));
    }

    private void ChangeAppName(String appName) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "document.querySelector('vaadin-combo-box').shadowRoot.querySelector('[part=\"toggle-button\"]').click();"
        );
        Thread.sleep(1500);

        WebElement overlay = driver.findElement(By.tagName("vaadin-combo-box-overlay"));

        List<WebElement> items = overlay.findElements(By.cssSelector("vaadin-combo-box-item"));
        for (WebElement item : items) {
            if (item.getText().equals(appName)) {
                item.click();
                break;
            }
        }

        Thread.sleep(1500);
    }
}