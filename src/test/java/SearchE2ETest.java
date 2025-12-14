import com.bervan.common.search.SearchQueryOption;
import com.bervan.common.search.SearchRequest;
import com.bervan.common.search.SearchRequestQueryTranslator;
import com.bervan.common.search.SearchService;
import com.bervan.common.search.model.SearchResponse;
import com.bervan.common.user.User;
import com.bervan.common.user.UserRepository;
import com.bervan.common.user.UserToUserRelationRepository;
import com.bervan.logging.LogEntity;
import com.bervan.logging.LogService;
import com.bervan.toolsapp.Application;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = Application.class)
@TestPropertySource("classpath:application-it.properties")
@ActiveProfiles("it")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchE2ETest extends BaseTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserToUserRelationRepository userToUserRelationRepository;
    @Autowired
    LogService logService;
    List<LogEntity> toBeRemoved = new ArrayList<>();

    @Autowired
    SearchService searchService;

    private Optional<User> commonUser;

    private static @NotNull LogEntity getLogEntity(Optional<User> commonUser, String applicationName, String logLevel, String exampleLogMessage, LocalDateTime timestamp, String className, int lineNumber, String exampleMethodName) {
        LogEntity entity = new LogEntity();
        entity.setApplicationName(applicationName);
        entity.setLogLevel(logLevel);
        entity.setMessage(exampleLogMessage);
        entity.setTimestamp(timestamp);
        entity.setClassName(className);
        entity.setLineNumber(lineNumber);
        entity.setMethodName(exampleMethodName);

        return entity;
    }

    @Test
    @Order(0)
    public void setup() throws InterruptedException {
        super.setup(userRepository, userToUserRelationRepository);
        commonUser = userRepository.findByUsername("COMMON_USER");
        LocalDateTime now = LocalDateTime.now();
        now = now.minusMinutes(40);

        LogEntity entity = getLogEntity(commonUser, "test-app-1", "INFO", "Example Log Message", now.plusSeconds(3), "LogsE2ETest", 50, "exampleMethodName3");
        toBeRemoved.add(entity);
        logService.save(entity);

        entity = getLogEntity(commonUser, "test-app-1", "ERROR", "Example ERROR Message", now.plusSeconds(2), "LogsE2ETest", 54, "exampleMethodName2");
        toBeRemoved.add(entity);
        logService.save(entity);

        entity = getLogEntity(commonUser, "test-app-1", "DEBUG", "Example DEBUG Message", now.plusSeconds(1), "LogsE2ETest", 56, "exampleMethodName1");
        toBeRemoved.add(entity);
        logService.save(entity);

        //app 2
        entity = getLogEntity(commonUser, "test-app-2", "INFO", "Example Log Message", now.plusSeconds(4), "LogsE2ETest", 50, "exampleMethodName40");
        toBeRemoved.add(entity);
        logService.save(entity);

        entity = getLogEntity(commonUser, "test-app-2", "ERROR", "Example ERROR Message", now.plusSeconds(3), "LogsE2ETest", 54, "exampleMethodName30");
        toBeRemoved.add(entity);
        logService.save(entity);

        entity = getLogEntity(commonUser, "test-app-2", "DEBUG", "Example DEBUG Message", now.plusSeconds(2), "LogsE2ETest", 56, "exampleMethodName20");
        toBeRemoved.add(entity);
        logService.save(entity);

        entity = getLogEntity(commonUser, "test-app-2", "DEBUG", "Example DEBUG Message", now.plusSeconds(1), "LogsE2ETest", 56, "exampleMethodName10");
        toBeRemoved.add(entity);
        logService.save(entity);

        now = LocalDateTime.now();
        //create log for 2h in past

        entity = getLogEntity(commonUser, "test-app-2", "WARN", "Log happened 2h earlier!", now.minusHours(2), "LogsE2ETest", 56, "exampleMethodName10");
        toBeRemoved.add(entity);
        logService.save(entity);
    }

    @Test
    @Order(100)
    public void teardown() throws InterruptedException {
        for (LogEntity logEntity : toBeRemoved) {
            logService.delete(logEntity);
        }
    }

    @Test
    @Order(1)
    public void testSimpleLogsLoading() throws InterruptedException {
        SearchQueryOption options = new SearchQueryOption();
        options.setEntityToFind(LogEntity.class);
        SearchRequest request = SearchRequestQueryTranslator.translateQuery(
                "logLevel = 'ERROR' | (methodName = 'exampleMethodName10' & logLevel != 'WARN')", LogEntity.class);
        SearchResponse<LogEntity> search = searchService.search(request, options);

        List<LogEntity> resultList = search.getResultList();
        assertEquals("ERROR", resultList.get(0).getLogLevel());
        assertEquals("ERROR", resultList.get(1).getLogLevel());
        assertEquals("DEBUG", resultList.get(2).getLogLevel());

        assertEquals("exampleMethodName2", resultList.get(0).getMethodName());
        assertEquals("exampleMethodName30", resultList.get(1).getMethodName());
        assertEquals("exampleMethodName10", resultList.get(2).getMethodName());

        assertTrue(resultList.get(0).getFullLog().contains("LogsE2ETest:exampleMethodName2:54 - Example ERROR Message"));
        assertTrue(resultList.get(1).getFullLog().contains("LogsE2ETest:exampleMethodName30:54 - Example ERROR Message"));
        assertTrue(resultList.get(2).getFullLog().contains("LogsE2ETest:exampleMethodName10:56 - Example DEBUG Message"));
    }
}