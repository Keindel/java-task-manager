import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    @BeforeEach
    public abstract void initializeManager();

    @Test
    public void test1_shouldMakeRegularTaskWhenEmptyList() {
        assertTrue(manager.getRegularTasks().isEmpty());
        Task task = new Task("name", "decrip", 3);
        manager.makeTask(task);
        assertTrue(manager.getRegularTasks().contains(task));
    }

    @Test
    public void test2_shouldMakeRegularTaskWhenNotEmptyList() {
        Task task = new Task("name", "decrip", 3);
        manager.makeTask(task);
        assertTrue(manager.getRegularTasks().contains(task));

        Task task2 = new Task("name2", "decrip2", 4);
        manager.makeTask(task2);
        assertTrue(manager.getRegularTasks().contains(task));
        assertTrue(manager.getRegularTasks().contains(task2));
    }

    @Test
    public void test3_shouldMakeRegularTaskWhenNoId() {
        assertTrue(manager.getRegularTasks().isEmpty());
        Task task = new Task("name", "decrip");
        manager.makeTask(task);
        assertTrue(manager.getRegularTasks().contains(task));
        assertTrue(task.getId() > 0);
    }

    @Test
    public void test4_shouldMakeEpicTaskWhenEmptyList() {
        assertTrue(manager.getEpicTasks().isEmpty());
        Task task = new EpicTask(new Task("name", "decrip", 3));
        manager.makeTask(task);
        assertTrue(manager.getEpicTasks().contains(task));
    }

    @Test
    public void test5_shouldMakeEpicTaskWhenNotEmptyList() {
        Task task = new EpicTask(new Task("name", "decrip", 3));
        manager.makeTask(task);
        Task task2 = new EpicTask(new Task("name2", "decrip2", 4));
        manager.makeTask(task2);
        assertTrue(manager.getEpicTasks().contains(task));
        assertTrue(manager.getEpicTasks().contains(task2));
    }

    @Test
    public void test6_shouldMakeEpicTaskWhenNoId() {
        assertTrue(manager.getEpicTasks().isEmpty());
        Task task = new EpicTask(new Task("name", "decrip"));
        manager.makeTask(task);
        assertTrue(manager.getEpicTasks().contains(task));
        assertTrue(task.getId() > 0);
    }

    @Test
    public void test7_shouldNotMakeSubTaskWhenNoEpic() {
        assertTrue(manager.getEpicTasks().isEmpty());
        Task task = new SubTask(new Task("name", "decrip", 3), 1);
        manager.makeTask(task);
        assertFalse(manager.getSubTasks().contains(task));
    }

    @Test
    public void test8_shouldMakeSubTaskWhenEmptyList() {
        assertTrue(manager.getSubTasks().isEmpty());
        Task epic = new EpicTask(new Task("name", "decrip", 1));
        manager.makeTask(epic);
        Task subTask = new SubTask(new Task("name", "decrip", 3), 1);
        manager.makeTask(subTask);
        assertTrue(manager.getSubTasks().contains(subTask));
    }

    @Test
    public void test9_shouldMakeSubTaskWhenNotEmptyList() {
        Task epic = new EpicTask(new Task("name", "decrip", 1));
        manager.makeTask(epic);
        Task subTask = new SubTask(new Task("name", "decrip", 3), 1);
        Task subTask2 = new SubTask(new Task("name", "decrip", 4), 1);
        manager.makeTask(subTask);
        manager.makeTask(subTask2);
        assertTrue(manager.getSubTasks().contains(subTask));
        assertTrue(manager.getSubTasks().contains(subTask2));
    }

    @Test
    public void test10_shouldMakeSubTaskWhenNoId() {
        assertTrue(manager.getSubTasks().isEmpty());
        Task epic = new EpicTask(new Task("name", "decrip", 1));
        manager.makeTask(epic);
        Task subTask = new SubTask(new Task("name", "decrip"), 1);
        manager.makeTask(subTask);
        assertTrue(manager.getSubTasks().contains(subTask));
        assertTrue(subTask.getId() > 0);
    }

    @Test
    public void test11_shouldUpdateTaskWhenIdIsOccupied() {
        Task task = new Task("name", "decrip", 1);
        manager.makeTask(task);
        Task taskUpdated = new Task("nameUpd", "decripUpd", 1);
        manager.makeTask(taskUpdated);
        assertEquals(taskUpdated, manager.getTaskById(1));
    }

    @Test
    public void test12_shouldDeleteTaskByIdWhenPresent() {
        Task task = new Task("name", "decrip", 1);
        manager.makeTask(task);
        assertTrue(manager.getRegularTasks().contains(task));
        manager.deleteTaskById(task.getId());
        assertFalse(manager.getRegularTasks().contains(task));
    }

    @Test
    public void test13_shouldGetTaskByIdWhenPresent() {
        Task task = new Task("name", "decrip", 1);
        manager.makeTask(task);
        assertEquals(task, manager.getTaskById(1));
    }

    @Test
    public void test14_shouldDeleteAllRegularTasks() {
        Task task = new Task("name", "decrip", 1);
        manager.makeTask(task);
        assertTrue(manager.getRegularTasks().size() > 0);
        manager.deleteAllRegularTasks();
        assertEquals(0, manager.getRegularTasks().size());
    }

    @Test
    public void test15_shouldDeleteAllSubTasks() {
        Task epic = new EpicTask(new Task("name", "decrip", 2));
        Task subTask = new SubTask(new Task("name", "decrip", 3), 2);
        manager.makeTask(epic);
        manager.makeTask(subTask);
        assertTrue(manager.getSubTasks().size() > 0);
        manager.deleteAllSubTasks();
        assertEquals(0, manager.getSubTasks().size());
    }

    @Test
    public void test16_shouldDeleteAllEpicTasks() {
        Task epic = new EpicTask(new Task("name", "decrip", 2));
        Task subTask = new SubTask(new Task("name", "decrip", 3), 2);
        manager.makeTask(epic);
        manager.makeTask(subTask);
        assertTrue(manager.getEpicTasks().size() > 0);
        assertTrue(manager.getSubTasks().size() > 0);
        manager.deleteAllEpicTasks();
        assertEquals(0, manager.getEpicTasks().size());
        assertEquals(0, manager.getSubTasks().size());
    }

    @Test
    public void test17_shouldGetRegularTasks() {
        Task task1 = new Task("name", "decrip", 1);
        Task task2 = new Task("name", "decrip", 2);
        Task task3 = new Task("name", "decrip", 3);

        assertEquals(0, manager.getRegularTasks().size());
        manager.makeTask(task1);
        assertEquals(1, manager.getRegularTasks().size());
        assertTrue(manager.getRegularTasks().contains(task1));

        manager.makeTask(task2);
        assertEquals(2, manager.getRegularTasks().size());
        assertTrue(manager.getRegularTasks().contains(task1));
        assertTrue(manager.getRegularTasks().contains(task2));

        manager.makeTask(task3);
        assertEquals(3, manager.getRegularTasks().size());
        assertTrue(manager.getRegularTasks().contains(task1));
        assertTrue(manager.getRegularTasks().contains(task2));
        assertTrue(manager.getRegularTasks().contains(task3));
    }

    @Test
    public void test18_shouldGetSubTasks() {
        Task epic1 = new EpicTask(new Task("name", "decrip", 1));

        assertEquals(0, manager.getEpicTasks().size());
        manager.makeTask(epic1);
        assertEquals(1, manager.getEpicTasks().size());
        assertTrue(manager.getEpicTasks().contains(epic1));

        Task subTask1 = new SubTask(new Task("name", "decrip", 2), 1);
        Task subTask2 = new SubTask(new Task("name", "decrip", 3), 1);
        Task subTask3 = new SubTask(new Task("name", "decrip", 4), 1);

        assertEquals(0, manager.getSubTasks().size());
        manager.makeTask(subTask1);
        assertEquals(1, manager.getSubTasks().size());
        assertTrue(manager.getSubTasks().contains(subTask1));

        manager.makeTask(subTask2);
        assertEquals(2, manager.getSubTasks().size());
        assertTrue(manager.getSubTasks().contains(subTask1));
        assertTrue(manager.getSubTasks().contains(subTask2));

        manager.makeTask(subTask3);
        assertEquals(3, manager.getSubTasks().size());
        assertTrue(manager.getSubTasks().contains(subTask1));
        assertTrue(manager.getSubTasks().contains(subTask2));
        assertTrue(manager.getSubTasks().contains(subTask3));
    }

    @Test
    public void test19_shouldGetEpicTasks() {
        Task epic1 = new EpicTask(new Task("name", "decrip", 2));
        Task epic2 = new EpicTask(new Task("name", "decrip", 3));
        Task epic3 = new EpicTask(new Task("name", "decrip", 4));

        assertEquals(0, manager.getEpicTasks().size());
        manager.makeTask(epic1);
        assertEquals(1, manager.getEpicTasks().size());
        assertTrue(manager.getEpicTasks().contains(epic1));

        manager.makeTask(epic2);
        assertEquals(2, manager.getEpicTasks().size());
        assertTrue(manager.getEpicTasks().contains(epic1));
        assertTrue(manager.getEpicTasks().contains(epic2));

        manager.makeTask(epic3);
        assertEquals(3, manager.getEpicTasks().size());
        assertTrue(manager.getEpicTasks().contains(epic1));
        assertTrue(manager.getEpicTasks().contains(epic2));
        assertTrue(manager.getEpicTasks().contains(epic3));
    }

    @Test
    public void test20_shouldGetSubTasksFromEpic() {
        Task epic1 = new EpicTask(new Task("name", "decrip", 1));

        assertEquals(0, manager.getEpicTasks().size());
        manager.makeTask(epic1);
        assertEquals(1, manager.getEpicTasks().size());
        assertTrue(manager.getEpicTasks().contains(epic1));

        Task subTask1 = new SubTask(new Task("name", "decrip", 2), 1);
        Task subTask2 = new SubTask(new Task("name", "decrip", 3), 1);
        Task subTask3 = new SubTask(new Task("name", "decrip", 4), 1);

        assertEquals(0, manager.getSubTasksFromEpic(1).size());
        manager.makeTask(subTask1);
        assertEquals(1, manager.getSubTasksFromEpic(1).size());
        assertTrue(manager.getSubTasksFromEpic(1).contains(subTask1));

        manager.makeTask(subTask2);
        assertEquals(2, manager.getSubTasksFromEpic(1).size());
        assertTrue(manager.getSubTasksFromEpic(1).contains(subTask1));
        assertTrue(manager.getSubTasksFromEpic(1).contains(subTask2));

        manager.makeTask(subTask3);
        assertEquals(3, manager.getSubTasksFromEpic(1).size());
        assertTrue(manager.getSubTasksFromEpic(1).contains(subTask1));
        assertTrue(manager.getSubTasksFromEpic(1).contains(subTask2));
        assertTrue(manager.getSubTasksFromEpic(1).contains(subTask3));
    }
}
