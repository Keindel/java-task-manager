import tasks.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path taskManagerDataFile;
    
    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
        taskManagerDataFile = null;
    }
    
    public FileBackedTasksManager(HistoryManager historyManager, Path taskManagerDataFile) {
        super(historyManager);
        this.taskManagerDataFile = taskManagerDataFile;
    }

    public void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(taskManagerDataFile.toString()))) {
            bufferedWriter.write("id,type,name,status,description,startTime,duration,endTime,epic");
            List<Task> allTasks = new ArrayList<>(getRegularTasks());
            allTasks.addAll(getEpicTasks());
            allTasks.addAll(getSubTasks());
            for (Task task : allTasks) {
                bufferedWriter.newLine();
                bufferedWriter.write(task.toString());
            }
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write(InMemoryHistoryManager.toStringOfIds(this.getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public static FileBackedTasksManager loadFromFile(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        HistoryManager historyManager = new InMemoryHistoryManager();
        FileBackedTasksManager fileBackedTasksManager
                = new FileBackedTasksManager(historyManager, path);

        if (lines.size() == 0) return new FileBackedTasksManager(historyManager, path);

        lines.remove(0);
        List<Integer> history = InMemoryHistoryManager
                .fromString(lines.get(lines.size()-1));
        lines.remove(lines.size()-1);
        lines.removeIf(String::isBlank);

        for (String line : lines) {
            fileBackedTasksManager.loadTask(taskFromString(line));
        }
        for (Integer id : history) {
            fileBackedTasksManager.loadTaskById(id);
        }
        return fileBackedTasksManager;
    }

    public static Task taskFromString(String value) {
        String[] taskFields = value.split(",");
        if (taskFields.length < 2) {
            throw new IllegalStateException("Unexpected value: " + value);
        }
        TaskTypes type = TaskTypes.valueOf(taskFields[1]);

        switch (type) {
            case TASK:
                return Task.fromString(value);
            case EPIC:
                return EpicTask.fromString(value);
            case SUBTASK:
                return SubTask.fromString(value);
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    private void loadTask(Task task) {
        super.makeTask(task);
    }

    private void loadTaskById(int id) {
        super.getSavedTaskByIdAndAffectHistory(id);
    }

    @Override
    public void makeTask(Task task) {
        super.makeTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllRegularTasks() {
        super.deleteAllRegularTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpicTasks() {
        super.deleteAllEpicTasks();
        save();
    }

    @Override
    public Task getSavedTaskByIdAndAffectHistory(int id) {
        Task task = super.getSavedTaskByIdAndAffectHistory(id);
        save();
        return task;
    }

    public static void main(String[] args) throws IOException {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Path path = Path.of("taskManagerData.csv");
        FileBackedTasksManager fileBackedTasksManager
                = new FileBackedTasksManager(historyManager, path);
        // 2 задачи для теста
        Task testTask1 = new Task("name1", "descr1");
        Task testTask2 = new Task("name2", "descr2");
        fileBackedTasksManager.makeTask(testTask1);
        fileBackedTasksManager.makeTask(testTask2);
//        System.out.println(fileBackedTasksManager.getTaskById(1));
//        System.out.println(fileBackedTasksManager.getTaskById(2));
        // 1ый эпик для теста
        Task testTask3 = new Task("name3", "descr3");
        EpicTask testEpicTask3 = new EpicTask(testTask3);
        fileBackedTasksManager.makeTask(testEpicTask3);
//        System.out.println(fileBackedTasksManager.getTaskById(3));
        // 2 подзадачи для 1го эпика
        Task testTask4 = new Task("name4", "descr4");
        Task testTask5 = new Task("name5", "descr5");
        SubTask subTask4 = new SubTask(testTask4, 3);
        SubTask subTask5 = new SubTask(testTask5, 3);
        fileBackedTasksManager.makeTask(subTask4);
        fileBackedTasksManager.makeTask(subTask5);
//        System.out.println(fileBackedTasksManager.getTaskById(4));
//        System.out.println(fileBackedTasksManager.getTaskById(5));
//        System.out.println(fileBackedTasksManager.getTaskById(3));
        // 2ой эпик для теста
        Task testTask6 = new Task("name6", "descr6");
        EpicTask testEpicTask6 = new EpicTask(testTask6);
        fileBackedTasksManager.makeTask(testEpicTask6);
//        System.out.println(fileBackedTasksManager.getTaskById(6));
        // 1 подзадача для 2го эпика
        Task testTask7 = new Task("name7", "descr7");
        SubTask subTask7 = new SubTask(testTask7, 6);
        fileBackedTasksManager.makeTask(subTask7);
//        System.out.println(fileBackedTasksManager.getTaskById(7));
//        System.out.println(fileBackedTasksManager.getTaskById(6));

        System.out.println("Список эпиков: " + fileBackedTasksManager.getEpicTasks());
        System.out.println("Список обычных задач: " + fileBackedTasksManager.getRegularTasks());
        System.out.println("Список подзадач: " + fileBackedTasksManager.getSubTasks());

        /*
        Просмотр истории
        */
        System.out.println("\nИстория запросов: " + fileBackedTasksManager.getHistoryManager());
        // Краткий вид истории, по id
        for (Task task : fileBackedTasksManager.getHistoryManager().getHistory()) {
            System.out.println("Краткий вид истории, по id: " + task.getId());
        }

        /*
        2ой менеджер из файла
        */
        FileBackedTasksManager managerFromFile = FileBackedTasksManager.loadFromFile(path);
        System.out.println("\nСписок эпиков: " + managerFromFile.getEpicTasks());
        System.out.println("Список обычных задач: " + managerFromFile.getRegularTasks());
        System.out.println("Список подзадач: " + managerFromFile.getSubTasks());
        System.out.println();
        System.out.println("История запросов: " + managerFromFile.getHistoryManager());
        // Краткий вид истории, по id
            System.out.println("Краткий вид истории, по id: " + InMemoryHistoryManager.toStringOfIds(managerFromFile.getHistoryManager()));
    }
}
