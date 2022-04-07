import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{


    @BeforeEach
    @Override
    public void initializeManager() {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @Test
    public void shouldGetHistoryManager(){
        assertNotNull(manager.getHistoryManager());
        assertEquals(InMemoryHistoryManager.class, manager.getHistoryManager().getClass());
    }
}
