import com.bervan.common.user.User;
import com.bervan.common.user.UserRepository;
import com.bervan.common.user.UserToUserRelationRepository;
import com.bervan.shstat.ProductService;
import com.bervan.toolsapp.Application;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = Application.class)
@TestPropertySource("classpath:application-it.properties")
@ActiveProfiles("it")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductsE2ETest extends BaseTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserToUserRelationRepository userToUserRelationRepository;
    @Autowired
    ProductService productService;
    private ChromeDriver driver;
    private WebDriverWait webDriverWait;
    private Optional<User> commonUser;

    @Test
    @Order(0)
    public void setup() throws InterruptedException {
        super.setup(userRepository, userToUserRelationRepository);
        commonUser = userRepository.findByUsername("COMMON_USER");

        driver = Config.getDriver();
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(5));

        super.Login(driver);
        super.GoToApp(driver, "Shopping");
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
    public void testPriceDetailsForProduct() throws InterruptedException {
        super.GoToAnotherViewInApp(driver, "Search");
        List<Map<String, Object>> products = new ArrayList<>();

        LocalDateTime localDateTime = LocalDateTime.of(2025, 5, 9, 17, 0, 0);

        //prepare data with different prices
        products.add(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime, 1999, "Smartwatches", "/smartwatches/apple/"));

        localDateTime = LocalDateTime.of(2025, 5, 9, 18, 0, 0);
        products.add(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime, 2500, "Smartwatches", "/smartwatches/apple/"));


        localDateTime = LocalDateTime.of(2025, 5, 9, 19, 0, 0);
        products.add(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime, 2100, "Smartwatches", "/smartwatches/apple/"));

        localDateTime = LocalDateTime.of(2025, 5, 9, 20, 0, 0);
        products.add(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime, 1250, "Smartwatches", "/smartwatches/apple/"));

        productService.addProducts(products);

        //
        ClickButtonByText(driver, "Search");
        Thread.sleep(1500);

        ClickAnchorByText(driver, "Apple Watch 5");
        Thread.sleep(1500);

        Assertions.assertEquals(3, CountElementsByClass(driver, "previous-price"));

        List<WebElement> elements = driver.findElements(By.xpath("//vaadin-vertical-layout//h4"));

        Assertions.assertEquals("Min: 1250.00 zł (09-05-2025 20:00:00)", elements.get(0).getText());
        Assertions.assertEquals("Avg: 1962.25 zł", elements.get(1).getText());
        Assertions.assertEquals("Max: 2500.00 zł (09-05-2025 18:00:00)", elements.get(2).getText());
    }

    @Order(2)
    public void testPriceWasNotAdded() throws InterruptedException {
        List<Map<String, Object>> products = new ArrayList<>();

        LocalDateTime localDateTime = LocalDateTime.of(2025, 5, 9, 21, 0, 0);
        products.add(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime, 1250, "Smartwatches", "/smartwatches/apple/"));
        //price is the same as before and date is different
        productService.addProducts(products);

        localDateTime = LocalDateTime.of(2025, 5, 10, 22, 0, 0);
        products.add(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime, 1250, "Smartwatches", "/smartwatches/apple/"));
        //price is the same as before and date is different
        productService.addProducts(products);

        //
        ClickButtonByText(driver, "Search");
        Thread.sleep(1500);

        ClickAnchorByText(driver, "Apple Watch 5");
        Thread.sleep(1500);

        Assertions.assertEquals(3, CountElementsByClass(driver, "previous-price"));
    }

    @Test
    @Order(3)
    public void testBestOffersProduct() throws InterruptedException {
        productService.createLowerThanAVGForLastXMonths();
        super.GoToAnotherViewInApp(driver, "Best Offers");

        TypeTextToVaadinText(driver, "Product Name:", "watch 5");

        ClickButtonByText(driver, "Search");
        Thread.sleep(1500);

        ClickAnchorByText(driver, "Apple Watch 5");
        Thread.sleep(1500);

        Assertions.assertEquals(3, CountElementsByClass(driver, "previous-price"));

        List<WebElement> elements = driver.findElements(By.xpath("//vaadin-vertical-layout//h4"));

        Assertions.assertEquals("Min: 1250.00 zł (09-05-2025 20:00:00)", elements.get(0).getText());
        Assertions.assertEquals("Avg: 1962.25 zł", elements.get(1).getText());
        Assertions.assertEquals("Max: 2500.00 zł (09-05-2025 18:00:00)", elements.get(2).getText());
    }


    private Map<String, Object> getProductMap(String offerName, String shop, String[] categories, String offerUrl,
                                              String image, LocalDateTime localDateTime, int price, String productListName,
                                              String productListUrl) {
        ZoneId zone = ZoneId.systemDefault();
        long timestamp = localDateTime.atZone(zone).toInstant().toEpochMilli();

        Map<String, Object> productMap = new HashMap<>();
        productMap.put("Name", offerName);
        productMap.put("Shop", shop);
        productMap.put("Categories", Arrays.stream(categories).toList());
        productMap.put("Offer Url", offerUrl);
        productMap.put("Image", image);
        productMap.put("Date", timestamp);
        productMap.put("Formatted Date", localDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        productMap.put("Price", price);
        productMap.put("Product List Name", productListName);
        productMap.put("Product List Url", productListUrl);

        return productMap;
    }
}