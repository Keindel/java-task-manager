import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private static HistoryManager historyManager;

    @BeforeEach
    public void beforeEach(){
        historyManager = new InMemoryHistoryManager();
    }

    private Collection<Task> populateHistory(){
        Task testTask1 = new Task("name1", "descr1", 1);
        Task testTask2 = new Task("name2", "descr2", 2);
        Collection<Task> taskCollection = new ArrayList<>();
        taskCollection.add(testTask1);
        taskCollection.add(testTask2);
        // 1ый эпик для теста
        EpicTask testEpicTask3 = new EpicTask(new Task("name3", "descr3", 3));
        taskCollection.add(testEpicTask3);
        // 2 подзадачи для 1го эпика
        SubTask subTask4 = new SubTask(new Task("name4", "descr4", 4), 3);
        SubTask subTask5 = new SubTask(new Task("name5", "descr5", 5), 3);
        taskCollection.add(subTask4);
        taskCollection.add(subTask5);
        // 2ой эпик для теста
        EpicTask testEpicTask6 = new EpicTask(new Task("name6", "descr6", 6));
        taskCollection.add(testEpicTask6);
        // 1 подзадача для 2го эпика
        SubTask subTask7 = new SubTask(new Task("name7", "descr7", 7), 6);
        taskCollection.add(subTask7);

        taskCollection.forEach(task -> historyManager.add(task));
        return taskCollection;
    }

    @Test
    void shouldAddAndGetHistoryAndRemove() {
        //Empty case
        assertTrue(historyManager.getHistory().isEmpty());
        Collection<Task> expectedCollection = populateHistory();
        assertEquals(expectedCollection, historyManager.getHistory());
        //Duplication
        populateHistory();
        assertEquals(expectedCollection, historyManager.getHistory());
        //Deletion from the beginning
        historyManager.remove(1);
        expectedCollection.remove(new Task("name1", "descr1", 1));
        assertEquals(expectedCollection, historyManager.getHistory());
        //Delete Epic from the middle 3, 4, 5
        historyManager.remove(4);
        expectedCollection.remove(new SubTask(new Task("name4", "descr4", 4), 3));
        assertEquals(expectedCollection, historyManager.getHistory());
        //Deletion from the end
        historyManager.remove(7);
        expectedCollection.remove(new SubTask(new Task("name7", "descr7", 7), 6));
        assertEquals(expectedCollection, historyManager.getHistory());
    }
}