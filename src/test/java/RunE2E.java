import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        LogsE2ETest.class,
        ProductsE2ETest.class,
        ProjectMgmtE2ETest.class,
        LearningLanguageE2ETest.class
})
public class RunE2E extends BaseTest {


}