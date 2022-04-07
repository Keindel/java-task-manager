import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{


    @BeforeEach
    @Override
    public void initializeManager() {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }


}
