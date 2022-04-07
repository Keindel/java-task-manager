import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTasksManager>{
    private static Path path = Path.of("taskManagerDataTest.csv");

    @BeforeEach
    @Override
    public void initializeManager() {
        manager = new FileBackedTasksManager(Managers.getDefaultHistory(), path);
    }

    @AfterEach
    public void clearFileAfterEach() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.toString()))) {
            bufferedWriter.write("");
            //bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populate() {
        Task testTask1 = new Task("name1", "descr1");
        Task testTask2 = new Task("name2", "descr2");
        manager.makeTask(testTask1);
        manager.makeTask(testTask2);
        // 1ый эпик для теста
        EpicTask testEpicTask3 = new EpicTask(new Task("name3", "descr3"));
        manager.makeTask(testEpicTask3);
        // 2 подзадачи для 1го эпика
        SubTask subTask4 = new SubTask(new Task("name4", "descr4"), 3);
        SubTask subTask5 = new SubTask(new Task("name5", "descr5"), 3);
        manager.makeTask(subTask4);
        manager.makeTask(subTask5);
        // 2ой эпик для теста
        EpicTask testEpicTask6 = new EpicTask(new Task("name6", "descr6"));
        manager.makeTask(testEpicTask6);
        // 1 подзадача для 2го эпика
        SubTask subTask7 = new SubTask(new Task("name7", "descr7"), 6);
        manager.makeTask(subTask7);
    }

    @Test
    public void test101_shouldSaveToFileAndThrowManagerSaveException() throws IOException {
        //Empty case
        FileBackedTasksManager managerFromFile = FileBackedTasksManager.loadFromFile(path);
        checkManagerFromFile(managerFromFile);
        //Populated case
        populate();
        //No history case
        managerFromFile = FileBackedTasksManager.loadFromFile(path);
        checkManagerFromFile(managerFromFile);
        // 2 задачи для теста
        assertNotNull(manager.getTaskById(1));
        assertNotNull(manager.getTaskById(2));
        // 1ый эпик для теста
        assertNotNull(manager.getTaskById(3));
        // Epic without subtasks case
        managerFromFile = FileBackedTasksManager.loadFromFile(path);
        checkManagerFromFile(managerFromFile);
        // 2 подзадачи для 1го эпика
        assertNotNull(manager.getTaskById(4));
        assertNotNull(manager.getTaskById(5));
        assertNotNull(manager.getTaskById(3));
        // 2ой эпик для теста
        assertNotNull(manager.getTaskById(6));
        // 1 подзадача для 2го эпика
        assertNotNull(manager.getTaskById(7));
        assertNotNull(manager.getTaskById(6));

        // 2ой менеджер из файла
        managerFromFile = FileBackedTasksManager.loadFromFile(path);
        checkManagerFromFile(managerFromFile);

        //Exception case
        Path emptyPath = Path.of("");
        manager = new FileBackedTasksManager(Managers.getDefaultHistory(), emptyPath);
        assertThrows(
                ManagerSaveException.class,
                () -> manager.makeTask(new Task("abc", "bcd"))
        );
    }

    private void checkManagerFromFile(FileBackedTasksManager managerFromFile) {
        assertEquals(manager.getEpicTasks(), managerFromFile.getEpicTasks());
        assertEquals(manager.getRegularTasks(), managerFromFile.getRegularTasks());
        assertEquals(manager.getSubTasks(), managerFromFile.getSubTasks());
        assertEquals(manager.getHistoryManager().getHistory()
                , managerFromFile.getHistoryManager().getHistory());
        assertEquals(InMemoryHistoryManager.toStringOfIds(manager.getHistoryManager())
                , InMemoryHistoryManager.toStringOfIds(managerFromFile.getHistoryManager()));
    }

    @Test
    public void test102_shouldLoadFromFileWhenPathExist() throws IOException {
        //Populated case is being checked in test101_shouldSaveToFile()
        //This is empty case
        FileBackedTasksManager managerFromFile = FileBackedTasksManager.loadFromFile(path);
        assertTrue(managerFromFile.getEpicTasks().isEmpty());
        assertTrue(managerFromFile.getRegularTasks().isEmpty());
        assertTrue(managerFromFile.getSubTasks().isEmpty());
        // Краткий вид истории, по id
        assertTrue(InMemoryHistoryManager.toStringOfIds(managerFromFile.getHistoryManager()).isEmpty());
    }

    @Test
    public void test103_shouldThrowIOExceptionWhenLoadNotExistingPath() {
        Path wrongPath = Path.of("WRONG_PATH");

        assertThrows(
                IOException.class,
                () -> FileBackedTasksManager.loadFromFile(wrongPath)
        );
    }

    @Test
    public void test104_shouldGetTaskFromStringAndThrowIllegalStateException() {
        Task task1 = new Task("name1", "descr1");
        Task task1FromString = FileBackedTasksManager.taskFromString(task1.toString());
        assertEquals(task1, task1FromString);

        Task epic1 = new EpicTask(new Task("name1", "descr1"));
        Task epic1FromString = FileBackedTasksManager.taskFromString(epic1.toString());
        assertEquals(epic1, epic1FromString);

        Task subTask1 = new SubTask(new Task("name1", "descr1"), 1);
        Task subTask1FromString = FileBackedTasksManager.taskFromString(subTask1.toString());
        assertEquals(subTask1, subTask1FromString);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> FileBackedTasksManager.taskFromString("wrong string")
        );
        assertTrue(ex.getMessage().startsWith("Unexpected value: "));
    }

    /*
    * Дополнительно для FileBackedTasksManager — проверка работы по сохранению и восстановлению состояния. Граничные условия:
a. Пустой список задач.
b. Эпик без подзадач.
c. Пустой список истории.*/
}
