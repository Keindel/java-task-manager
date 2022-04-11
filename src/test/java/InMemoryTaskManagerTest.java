import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

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

    @Test
    public void shouldGoOverExistingIdsForIncomingTasksWithoutIds(){
        assertEquals(0, manager.getRegularTasks().size());
        manager.makeTask(new Task("abc", "bcd", 2));
        assertEquals(1, manager.getRegularTasks().size());
        manager.makeTask(new Task("n1", "d1"));
        assertEquals(2, manager.getRegularTasks().size());
        manager.makeTask(new Task("n2", "d2"));
        assertEquals(3, manager.getRegularTasks().size());
        manager.makeTask(new Task("n3", "d3"));
        assertEquals(4, manager.getRegularTasks().size());
    }
}
