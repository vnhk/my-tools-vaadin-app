import com.bervan.common.user.User;
import com.bervan.common.user.UserRepository;
import com.bervan.common.user.UserToUserRelationRepository;
import com.bervan.shstat.entity.scrap.ScrapAudit;
import com.bervan.shstat.queue.AddProductsQueue;
import com.bervan.shstat.repository.ProductConfigRepository;
import com.bervan.shstat.service.ProductService;
import com.bervan.shstat.service.ScrapAuditService;
import com.bervan.toolsapp.Application;
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
import java.time.LocalDate;
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
    @Autowired
    ScrapAuditService scrapAuditService;
    @Autowired
    ProductConfigRepository productConfigRepository;
    int TOTAL_COLUMNS_SHOP_CONFIG = 3;
    int TOTAL_COLUMNS_PRODUCT_CONFIG = 7;
    private ChromeDriver driver;
    private WebDriverWait webDriverWait;
    private Optional<User> commonUser;
    @Autowired
    private AddProductsQueue addProductsQueue;

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
    public void testSimpleAddShopConfigRecords() throws InterruptedException {
        super.GoToAnotherViewInApp(driver, "Shop Config");
        Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(0, itemsInTable);
        AddNewItemShopConfig(driver, "Apple shop", "https://www.appleshop.com");
        Thread.sleep(1500);

        itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(1, itemsInTable);

        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 1, 0, TOTAL_COLUMNS_SHOP_CONFIG, "Apple shop"));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 2, 0, TOTAL_COLUMNS_SHOP_CONFIG, "https://www.appleshop.com"));
    }

    @Test
    @Order(2)
    public void tesShopConfigRecordsEdit() throws InterruptedException {
        super.GoToAnotherViewInApp(driver, "Shop Config");
        Thread.sleep(1500);
        Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(1, itemsInTable);

        BervanTableCommon.EditTextInColumn(driver, 1, 0, TOTAL_COLUMNS_SHOP_CONFIG, "Apple Shop");
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 1, 0, TOTAL_COLUMNS_SHOP_CONFIG, "Apple Shop"));
    }

    @Test
    @Order(3)
    public void testSimpleAddProductConfigRecords() throws InterruptedException {
        super.GoToAnotherViewInApp(driver, "Product Config");
        ChangeShopName("Apple Shop");
        Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(0, itemsInTable);
        AddNewItemProductConfig(driver, "Apple Shop", "Smartwatches", "/smartwatches/apple/", "15:00", "Apple Devices", "Smartwatches");
        Thread.sleep(1500);

        itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(1, itemsInTable);

        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 1, 0, TOTAL_COLUMNS_PRODUCT_CONFIG, "Smartwatches"));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 2, 0, TOTAL_COLUMNS_PRODUCT_CONFIG, "/smartwatches/apple/"));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 5, 0, TOTAL_COLUMNS_PRODUCT_CONFIG, "15:00"));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 6, 0, TOTAL_COLUMNS_PRODUCT_CONFIG, "[Apple Devices, Smartwatches]"));

        AddNewItemProductConfig(driver, "Apple Shop", "MacBook Air M1", "/notebooks/apple/mac-air-m1-1", "2:00", "Apple Devices", "Notebooks");
        Thread.sleep(1500);

        itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(2, itemsInTable);

        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 1, 1, TOTAL_COLUMNS_PRODUCT_CONFIG, "MacBook Air M1"));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 2, 1, TOTAL_COLUMNS_PRODUCT_CONFIG, "/notebooks/apple/mac-air-m1-1"));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 5, 1, TOTAL_COLUMNS_PRODUCT_CONFIG, "02:00"));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 6, 1, TOTAL_COLUMNS_PRODUCT_CONFIG, "[Apple Devices, Notebooks]"));
    }

    @Test
    @Order(4)
    public void testScrapAuditForProducts() throws InterruptedException {
        ScrapAudit scrapAudit = new ScrapAudit();
        LocalDate localDate = LocalDate.now();
        scrapAudit.setDate(localDate);
        scrapAudit.setProductConfig(productConfigRepository.findAll().get(0));
        scrapAudit.addOwner(commonUser.get());
        scrapAuditService.save(scrapAudit);

        super.GoToAnotherViewInApp(driver, "Scrap Audit");

        Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(1, itemsInTable);

        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 1, 0, 4, localDate.format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 2, 0, 4, "Smartwatches - Apple Shop (15:00)"));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 3, 0, 4, "0"));
    }

    @Test
    @Order(10)
    public void testPriceDetailsForProduct() throws InterruptedException {
        super.GoToAnotherViewInApp(driver, "Search");
        List<Map<String, Object>> products = new ArrayList<>();

        LocalDateTime localDateTime = LocalDateTime.of(2025, 5, 9, 17, 0, 0);

        addProductsQueue.addProductsByPartitions(List.of(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime, 1999, "Smartwatches", "/smartwatches/apple/")), addProducts);

        localDateTime = LocalDateTime.of(2025, 5, 9, 18, 0, 0);
        addProductsQueue.addProductsByPartitions(List.of(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime, 2500, "Smartwatches", "/smartwatches/apple/")), addProducts);


        localDateTime = LocalDateTime.of(2025, 5, 9, 19, 0, 0);
        addProductsQueue.addProductsByPartitions(List.of(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime, 2100, "Smartwatches", "/smartwatches/apple/")), addProducts);

        localDateTime = LocalDateTime.of(2025, 5, 9, 20, 0, 0);
        addProductsQueue.addProductsByPartitions(List.of(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime, 1250, "Smartwatches", "/smartwatches/apple/")), addProducts);


        //
        ClickButtonByText(driver, "Search");
        Thread.sleep(1500);

        ClickAnchorByText(driver, "Apple Watch 5");
        Thread.sleep(1500);

        Assertions.assertEquals(3, CountElementsByClass(driver, "previous-price"));

        List<WebElement> elements = driver.findElements(By.xpath("//vaadin-vertical-layout//h4"));

        Assertions.assertEquals("Min: 1250.00 zł (09-05-2025 20:00:00)", elements.get(0).getText());
        Assertions.assertEquals("Avg: 2199.6666666666665 zł", elements.get(1).getText());
        Assertions.assertEquals("Max: 2500.00 zł (09-05-2025 18:00:00)", elements.get(2).getText());

        super.GoToAnotherViewInApp(driver, "Scrap Audit");

        Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(1, itemsInTable);

        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 1, 0, 4, LocalDate.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 2, 0, 4, "Smartwatches - Apple Shop (15:00)"));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 3, 0, 4, "4"));

    }

    @Order(12)
    public void testPriceWasNotAdded() throws InterruptedException {
        List<Map<String, Object>> products = new ArrayList<>();

        LocalDateTime localDateTime = LocalDateTime.of(2025, 5, 9, 21, 0, 0);
        products.add(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime, 1250, "Smartwatches", "/smartwatches/apple/"));
        //price is the same as before and date is different
        productService.addProductsAsync(products, addProductsContext);

        localDateTime = LocalDateTime.of(2025, 5, 10, 22, 0, 0);
        products.add(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime, 1250, "Smartwatches", "/smartwatches/apple/"));
        //price is the same as before and date is different
        productService.addProductsAsync(products, addProductsContext);

        //
        ClickButtonByText(driver, "Search");
        Thread.sleep(1500);

        ClickAnchorByText(driver, "Apple Watch 5");
        Thread.sleep(1500);

        Assertions.assertEquals(3, CountElementsByClass(driver, "previous-price"));

        super.GoToAnotherViewInApp(driver, "Scrap Audit");

        Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(1, itemsInTable);

        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 1, 0, 4, LocalDate.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 2, 0, 4, "Smartwatches - Apple Shop (15:00)"));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 3, 0, 4, "6"));
    }

    @Test
    @Order(13)
    public void testBestOffersProduct() throws InterruptedException {
        //need to add price for today, to be added to actual products and then best offer can be created and fetched

        LocalDateTime localDateTime = LocalDateTime.now().minusHours(5);
        productService.addProductsAsync(Collections.singletonList(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime, 1209, "Smartwatches", "/smartwatches/apple/")), addProductsContext);


        //-1 should be skipped and not affect a price
        productService.addProductsAsync(Collections.singletonList(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime.plusSeconds(50), -1, "Smartwatches", "/smartwatches/apple/")), addProductsContext);

        //-1 null/empty be skipped and not affect a price
        productService.addProductsAsync(Collections.singletonList(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime.plusSeconds(50), null, "Smartwatches", "/smartwatches/apple/")), addProductsContext);

        //-1 null/empty be skipped and not affect a price
        productService.addProductsAsync(Collections.singletonList(getProductMap("Apple Watch 5", "Apple Shop", new String[]{"Apple", "Smartwatch"},
                "https://www.appleshop.com/smartwatches/apple/smartwatch-apple-watch-5-black",
                "https://www.apple.com/newsroom/images/product/watch/standard/Apple_watch_series_5-gold-aluminum-case-pomegranate-band-and-space-gray-aluminum-case-pine-green-band-091019_big.jpg.large_2x.jpg",
                localDateTime.plusSeconds(50), "", "Smartwatches", "/smartwatches/apple/")), addProductsContext);

        productService.createBestOffers();
        super.GoToAnotherViewInApp(driver, "Best Offers");

        TypeTextToVaadinText(driver, "Product Name:", "watch 5");

        ClickButtonByText(driver, "Search");
        Thread.sleep(1500);

        ClickAnchorByText(driver, "Apple Watch 5");
        Thread.sleep(1500);

        Assertions.assertEquals(4, CountElementsByClass(driver, "previous-price"));

        List<WebElement> elements = driver.findElements(By.xpath("//vaadin-vertical-layout//h4"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", elements.get(0));

        Assertions.assertTrue(elements.get(0).getText().contains("Min: 1209.00 zł"));
        Assertions.assertEquals("Avg: 1811.6 zł", elements.get(1).getText());
        Assertions.assertEquals("Max: 2500.00 zł (09-05-2025 18:00:00)", elements.get(2).getText());

        super.GoToAnotherViewInApp(driver, "Scrap Audit");

        Integer itemsInTable = BervanTableCommon.GetItemsInTable(driver);
        Assertions.assertEquals(1, itemsInTable);

        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 1, 0, 4, LocalDate.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 2, 0, 4, "Smartwatches - Apple Shop (15:00)"));
        Assertions.assertTrue(BervanTableCommon.EqualsColumnValueAsStr(driver, 3, 0, 4, "8"));

    }

    private Map<String, Object> getProductMap(String offerName, String shop, String[] categories, String offerUrl,
                                              String image, LocalDateTime localDateTime, Object price, String productListName,
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

    private void AddNewItemShopConfig(ChromeDriver driver, String shop, String baseUrl) throws InterruptedException {
        Thread.sleep(500);
        BervanTableCommon.openAddItemModal(driver);
        WebElement element = driver.findElement(By.xpath("//vaadin-text-area[label[text()='Shop Name']]//textarea"));
        element.sendKeys(shop);
        element = driver.findElement(By.xpath("//vaadin-text-area[label[text()='Base Url']]//textarea"));
        element.sendKeys(baseUrl);

        WebElement button = driver.findElement(By.xpath("//vaadin-button[contains(.,'Save')]"));
        button.click();
    }


    private void AddNewItemProductConfig(ChromeDriver driver, String shopName, String productName, String productUrl, String time, String... categories) throws InterruptedException {
        Thread.sleep(500);
        BervanTableCommon.openAddItemModal(driver);
        WebElement element = driver.findElement(By.xpath("//vaadin-text-area[label[text()='Name']]//textarea"));
        element.sendKeys(productName);
        element = driver.findElement(By.xpath("//vaadin-text-area[label[text()='Url']]//textarea"));
        element.sendKeys(productUrl);

        BervanTableCommon.SetTimePickerValue(driver, "Select Time", time);
        BervanTableCommon.SelectDropdownValue(driver, "Shop", shopName);
        BervanTableCommon.SelectMultiSelectComboBoxValues(driver, "Categories", Arrays.stream(categories).toList(), true);

        WebElement button = driver.findElement(By.xpath("//vaadin-button[contains(.,'Save')]"));
        button.click();
    }


    private void ChangeShopName(String shopName) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "document.querySelector('vaadin-combo-box').shadowRoot.querySelector('[part=\"toggle-button\"]').click();"
        );
        Thread.sleep(1500);

        WebElement overlay = driver.findElement(By.tagName("vaadin-combo-box-overlay"));

        List<WebElement> items = overlay.findElements(By.cssSelector("vaadin-combo-box-item"));
        for (WebElement item : items) {
            if (item.getText().equals(shopName)) {
                item.click();
                break;
            }
        }

        Thread.sleep(1500);
    }

}