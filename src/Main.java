import tasks.*;

public class Main {
    public static void main(String[] args) {
        // Тестирование приложения
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault(historyManager);
        // 2 задачи для теста
        Task testTask1 = new Task("", "");
        Task testTask2 = new Task("", "");
        taskManager.makeAnyTask(testTask1);
        taskManager.makeAnyTask(testTask2);
        System.out.println(taskManager.getAnyTaskById(1));
        System.out.println(taskManager.getAnyTaskById(2));
        // 1ый эпик для теста
        Task testTask3 = new Task("", "");
        EpicTask testEpicTask3 = new EpicTask(testTask3);
        taskManager.makeAnyTask(testEpicTask3);
        System.out.println(taskManager.getAnyTaskById(3));
        // 2 подзадачи для 1го эпика
        Task testTask4 = new Task("", "");
        Task testTask5 = new Task("", "");
        SubTask subTask4 = new SubTask(testTask4, 3);
        SubTask subTask5 = new SubTask(testTask5, 3);
        taskManager.makeAnyTask(subTask4);
        taskManager.makeAnyTask(subTask5);
        System.out.println(taskManager.getAnyTaskById(4));
        System.out.println(taskManager.getAnyTaskById(5));
        System.out.println(taskManager.getAnyTaskById(3));
        // 2ой эпик для теста
        Task testTask6 = new Task("", "");
        EpicTask testEpicTask6 = new EpicTask(testTask6);
        taskManager.makeAnyTask(testEpicTask6);
        System.out.println(taskManager.getAnyTaskById(6));
        // 1 подзадача для 2го эпика
        Task testTask7 = new Task("", "");
        SubTask subTask7 = new SubTask(testTask7, 6);
        taskManager.makeAnyTask(subTask7);
        System.out.println(taskManager.getAnyTaskById(7));
        System.out.println(taskManager.getAnyTaskById(6));

        System.out.println("Список эпиков: " + taskManager.getEpicTasks());
        System.out.println("Список обычных задач: " + taskManager.getRegularTasks());
        System.out.println("Список подзадач: " + taskManager.getSubTasks());

        /*
        Просмотр истории
        */
        System.out.println();
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) taskManager;
        System.out.println("История запросов: " + inMemoryTaskManager.getHistoryManager());
        // Краткий вид истории, по id
        for (Task task : inMemoryTaskManager.getHistoryManager().getHistory()) {
            System.out.println("Краткий вид истории, по id: " + task.getId());
        }

        // Обновление статусов обычных задач
        Task testTask11 = new Task("", "", 1, Status.DONE);
        Task testTask12 = new Task("", "", 2, Status.IN_PROGRESS);
        taskManager.updateAnyTask(testTask11);
        taskManager.updateAnyTask(testTask12);
        System.out.println("Задача с id=1 " + taskManager.getAnyTaskById(1));
        System.out.println("Задача с id=2 " + taskManager.getAnyTaskById(2));

        /*
        Просмотр истории
        */
        System.out.println();
        System.out.println("История запросов: " + inMemoryTaskManager.getHistoryManager());
        // Краткий вид истории, по id
        for (Task task : inMemoryTaskManager.getHistoryManager().getHistory()) {
            System.out.println("Краткий вид истории, по id: " + task.getId());
        }

        // Обновление статусов подзадач 1го эпика
        Task testTask14 = new Task("", "", 4, Status.IN_PROGRESS);
        SubTask subTask14 = new SubTask(testTask14, 3);
        Task testTask15 = new Task("", "", 5, Status.DONE);
        SubTask subTask15 = new SubTask(testTask15, 3);
        taskManager.updateAnyTask(subTask14);
        taskManager.updateAnyTask(subTask15);
        System.out.println("Подзадача с id=4 " + taskManager.getAnyTaskById(4));
        System.out.println("Подзадача с id=5 " + taskManager.getAnyTaskById(5));
        System.out.println("Эпик с id=3 " + taskManager.getAnyTaskById(3));

        /*
        Просмотр истории
        */
        System.out.println();
        System.out.println("История запросов: " + inMemoryTaskManager.getHistoryManager());
        // Краткий вид истории, по id
        for (Task task : inMemoryTaskManager.getHistoryManager().getHistory()) {
            System.out.println("Краткий вид истории, по id: " + task.getId());
        }

        // Обновление статуса подзадачи 2го эпика
        Task testTask17 = new Task("", "", 7, Status.DONE);
        SubTask subTask17 = new SubTask(testTask17, 6);
        taskManager.updateAnyTask(subTask17);
        System.out.println("Подзадача с id=7 " + taskManager.getAnyTaskById(7));
        System.out.println("Эпик с id=6 " + taskManager.getAnyTaskById(6));

        // Удаление задачи
        taskManager.deleteAnyTaskById(2);
        System.out.println("Список обычных задач после удаления: " + taskManager.getRegularTasks());

        // Удаление эпика
        taskManager.deleteAnyTaskById(3);
        System.out.println("Список эпиков после удаления: " + taskManager.getEpicTasks());
        System.out.println("Список подзадач после удаления эпика: " + taskManager.getSubTasks());

        // Обновление SubTask с несуществующим epicId
        Task testTask27 = new Task("", "", 7, Status.IN_PROGRESS);
        SubTask subTask27 = new SubTask(testTask27, 16);
        taskManager.updateAnyTask(subTask27);

        /*
        Просмотр истории
        */
        System.out.println();
        System.out.println("История запросов: " + inMemoryTaskManager.getHistoryManager());
        // Краткий вид истории, по id
        for (Task task : inMemoryTaskManager.getHistoryManager().getHistory()) {
            System.out.println("Краткий вид истории, по id: " + task.getId());
        }
    }
}
