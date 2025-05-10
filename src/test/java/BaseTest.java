import com.bervan.common.user.User;
import com.bervan.common.user.UserRepository;
import com.bervan.common.user.UserToUserRelation;
import com.bervan.common.user.UserToUserRelationRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static java.time.Duration.ofSeconds;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

public class BaseTest {
    protected static UserRepository userRepository;
    protected static UserToUserRelationRepository userToUserRelationRepository;
    protected static RabbitMQContainer rabbitMQContainer;
    protected static MariaDBContainer mariaDBContainer;
    protected static String baseUrl = "http://localhost:9091";

    public BaseTest() {

    }

    static {
        rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.11")
                .withExposedPorts(5672);
        rabbitMQContainer.start();
        System.setProperty("spring.rabbitmq.username", rabbitMQContainer.getAdminUsername());
        System.setProperty("spring.rabbitmq.password", rabbitMQContainer.getAdminPassword());
        System.setProperty("spring.rabbitmq.host", rabbitMQContainer.getHost());
        System.setProperty("spring.rabbitmq.port", rabbitMQContainer.getAmqpPort().toString());

        mariaDBContainer = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"))
                .withDatabaseName("my_tools_db")
                .withUsername("my_tools_db_user")
                .withPassword("my_tools_db_password");
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        mariaDBContainer.start();
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
    }

    public void setup(UserRepository userRepository, UserToUserRelationRepository userToUserRelationRepository) {
        this.userRepository = userRepository;
        this.userToUserRelationRepository = userToUserRelationRepository;
    }

    public void Login(ChromeDriver driver) throws InterruptedException {
        createTestUser();
        driver.get(baseUrl + "/login");

        new WebDriverWait(driver, ofSeconds(10), ofSeconds(1))
                .until(titleIs("Login"));

        var button = driver.findElement(By.xpath("//vaadin-button[contains(.,'Log in')]"));
        var username = driver.findElement(By.xpath("//vaadin-text-field[@id='vaadinLoginUsername']"));
        var password = driver.findElement(By.xpath("//vaadin-password-field[@id='vaadinLoginPassword']"));
        username.sendKeys("testUser");
        password.sendKeys("testUser!2#4%6");
        Thread.sleep(500);
        button.click();
        Thread.sleep(1000);
        driver.get(baseUrl + "/settings");
    }

    public void TypeTextToVaadinText(ChromeDriver driver, String label, String text) throws InterruptedException {
        WebElement element = driver.findElement(By.xpath("//vaadin-text-field[label[contains(.,'" + label + "')]]"));
        Thread.sleep(250);
        element.sendKeys(text);
        Thread.sleep(500);
    }

    protected void createTestUser() {
        if (userRepository.findByUsername("testUser").isEmpty()) {
            User testUser = new User();
            testUser.setUsername("testUser");
            testUser.setPassword(new BCryptPasswordEncoder().encode("testUser!2#4%6"));
            testUser.setRole("ROLE_USER");
            userRepository.save(testUser);

            UserToUserRelation userRelation = new UserToUserRelation();
            userRelation.setChild(testUser);
            userRelation.setParent(testUser);
            userRelation.addOwner(testUser);
            userToUserRelationRepository.save(userRelation);
        }
    }

    protected void ClickButtonByText(ChromeDriver driver, String text) throws InterruptedException {
        WebElement button = new WebDriverWait(driver, ofSeconds(10), ofSeconds(1))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//vaadin-vertical-layout[@class='bervan-page']//vaadin-button[contains(.,'" + text + "')]")));

        button.click();
        Thread.sleep(500);
    }

    protected void ClickAnchorByText(ChromeDriver driver, String text) throws InterruptedException {
        WebElement button = new WebDriverWait(driver, ofSeconds(10), ofSeconds(1))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(.,'" + text + "')]")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
        Thread.sleep(300);

        button.click();
        Thread.sleep(500);
    }

    protected int CountElementsByClass(ChromeDriver driver, String className) throws InterruptedException {
        List<WebElement> elements = driver.findElements(By.className(className));
        return elements.size();
    }

    protected void ClickButtonByIcon(ChromeDriver driver, String icon) throws InterruptedException {
        WebElement button = new WebDriverWait(driver, ofSeconds(10), ofSeconds(1))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//vaadin-vertical-layout[@class='bervan-page']//vaadin-button[@icon='" + icon + "']")));

        button.click();
        Thread.sleep(500);
    }

    protected void GoToApp(ChromeDriver driver, String subPageText) {
        WebElement drawerToggle = new WebDriverWait(driver, ofSeconds(10), ofSeconds(1))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//vaadin-drawer-toggle")));

        drawerToggle.click();

        new WebDriverWait(driver, ofSeconds(10), ofSeconds(1))
                .until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[@class='menu-item-link' and contains(., '" + subPageText + "')]")
                ));

        var menuItem = driver.findElement(By.xpath("//a[@class='menu-item-link' and contains(., '" + subPageText + "')]"));
        menuItem.click();
    }

    protected void GoToAnotherViewInApp(ChromeDriver driver, String subPageButtonText) throws InterruptedException {
        WebElement navigationButtons = new WebDriverWait(driver, ofSeconds(10), ofSeconds(1))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//navigation-buttons")));

        WebElement button = navigationButtons.findElement(By.xpath("//vaadin-button[contains(.,'" + subPageButtonText + "')]"));
        button.click();
        Thread.sleep(400);
    }
}