import com.bervan.common.user.User;
import com.bervan.common.user.UserRepository;
import com.bervan.common.user.UserToUserRelation;
import com.bervan.common.user.UserToUserRelationRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.testcontainers.containers.RabbitMQContainer;

import static java.time.Duration.ofSeconds;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

public class BaseTest {
    protected UserRepository userRepository;
    protected UserToUserRelationRepository userToUserRelationRepository;
    protected RabbitMQContainer rabbitMQContainer;
    protected String baseUrl = "http://localhost:9091";

    public BaseTest() {

    }

    public void setup(UserRepository userRepository, UserToUserRelationRepository userToUserRelationRepository) {
        this.userRepository = userRepository;
        this.userToUserRelationRepository = userToUserRelationRepository;
        rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.11")
                .withExposedPorts(5672);
        rabbitMQContainer.start();
        System.setProperty("spring.rabbitmq.username", rabbitMQContainer.getAdminUsername());
        System.setProperty("spring.rabbitmq.password", rabbitMQContainer.getAdminPassword());
        System.setProperty("spring.rabbitmq.host", rabbitMQContainer.getHost());
        System.setProperty("spring.rabbitmq.port", rabbitMQContainer.getAmqpPort().toString());
    }

    public void Login(ChromeDriver driver) {
        createTestUser();
        driver.get(baseUrl + "/login");

        new WebDriverWait(driver, ofSeconds(10), ofSeconds(1))
                .until(titleIs("Login"));

        var button = driver.findElement(By.xpath("//vaadin-button[contains(.,'Log in')]"));
        var username = driver.findElement(By.xpath("//vaadin-text-field[@id='vaadinLoginUsername']"));
        var password = driver.findElement(By.xpath("//vaadin-password-field[@id='vaadinLoginPassword']"));
        username.sendKeys("testUser");
        password.sendKeys("testUser!2#4%6");
        button.click();

        driver.get(baseUrl + "/settings");
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