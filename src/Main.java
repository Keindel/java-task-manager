import tasks.*;

public class Main {
    public static void main(String[] args) {
        // Тестирование приложения
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        // 2 задачи для теста
        Task testTask1 = new Task("", "");
        Task testTask2 = new Task("", "");
        inMemoryTaskManager.makeAnyTask(testTask1);
        inMemoryTaskManager.makeAnyTask(testTask2);
        System.out.println(inMemoryTaskManager.getAnyTaskById(1));
        System.out.println(inMemoryTaskManager.getAnyTaskById(2));
        // 1ый эпик для теста
        Task testTask3 = new Task("", "");
        EpicTask testEpicTask3 = new EpicTask(testTask3);
        inMemoryTaskManager.makeAnyTask(testEpicTask3);
        System.out.println(inMemoryTaskManager.getAnyTaskById(3));
        // 2 подзадачи для 1го эпика
        Task testTask4 = new Task("", "");
        Task testTask5 = new Task("", "");
        SubTask subTask4 = new SubTask(testTask4, 3);
        SubTask subTask5 = new SubTask(testTask5, 3);
        inMemoryTaskManager.makeAnyTask(subTask4);
        inMemoryTaskManager.makeAnyTask(subTask5);
        System.out.println(inMemoryTaskManager.getAnyTaskById(4));
        System.out.println(inMemoryTaskManager.getAnyTaskById(5));
        System.out.println(inMemoryTaskManager.getAnyTaskById(3));
        // 2ой эпик для теста
        Task testTask6 = new Task("", "");
        EpicTask testEpicTask6 = new EpicTask(testTask6);
        inMemoryTaskManager.makeAnyTask(testEpicTask6);
        System.out.println(inMemoryTaskManager.getAnyTaskById(6));
        // 1 подзадача для 2го эпика
        Task testTask7 = new Task("", "");
        SubTask subTask7 = new SubTask(testTask7, 6);
        inMemoryTaskManager.makeAnyTask(subTask7);
        System.out.println(inMemoryTaskManager.getAnyTaskById(7));
        System.out.println(inMemoryTaskManager.getAnyTaskById(6));

        System.out.println("Список эпиков: " + inMemoryTaskManager.getEpicTasks());
        System.out.println("Список обычных задач: " + inMemoryTaskManager.getRegularTasks());
        System.out.println("Список подзадач: " + inMemoryTaskManager.getSubTasks());

        // Обновление статусов обычных задач
        Task testTask11 = new Task("", "", 1, Status.DONE);
        Task testTask12 = new Task("", "", 2, Status.IN_PROGRESS);
        inMemoryTaskManager.updateAnyTask(testTask11);
        inMemoryTaskManager.updateAnyTask(testTask12);
        System.out.println("Задача с id=1 " + inMemoryTaskManager.getAnyTaskById(1));
        System.out.println("Задача с id=2 " + inMemoryTaskManager.getAnyTaskById(2));

        // Обновление статусов подзадач 1го эпика
        Task testTask14 = new Task("", "", 4, Status.IN_PROGRESS);
        SubTask subTask14 = new SubTask(testTask14, 3);
        Task testTask15 = new Task("", "", 5, Status.DONE);
        SubTask subTask15 = new SubTask(testTask15, 3);
        inMemoryTaskManager.updateAnyTask(subTask14);
        inMemoryTaskManager.updateAnyTask(subTask15);
        System.out.println("Подзадача с id=4 " + inMemoryTaskManager.getAnyTaskById(4));
        System.out.println("Подзадача с id=5 " + inMemoryTaskManager.getAnyTaskById(5));
        System.out.println("Эпик с id=3 " + inMemoryTaskManager.getAnyTaskById(3));

        // Обновление статуса подзадачи 2го эпика
        Task testTask17 = new Task("", "", 7, Status.DONE);
        SubTask subTask17 = new SubTask(testTask17, 6);
        inMemoryTaskManager.updateAnyTask(subTask17);
        System.out.println("Подзадача с id=7 " + inMemoryTaskManager.getAnyTaskById(7));
        System.out.println("Эпик с id=6 " + inMemoryTaskManager.getAnyTaskById(6));

        // Удаление задачи
        inMemoryTaskManager.deleteAnyTaskById(2);
        System.out.println("Список обычных задач после удаления: " + inMemoryTaskManager.getRegularTasks());

        // Удаление эпика
        inMemoryTaskManager.deleteAnyTaskById(3);
        System.out.println("Список эпиков после удаления: " + inMemoryTaskManager.getEpicTasks());
        System.out.println("Список подзадач после удаления эпика: " + inMemoryTaskManager.getSubTasks());

        // Обновление SubTask с несуществующим epicId
        Task testTask27 = new Task("", "", 7, Status.IN_PROGRESS);
        SubTask subTask27 = new SubTask(testTask27, 16);
        inMemoryTaskManager.updateAnyTask(subTask27);

        // Просмотр истории
        System.out.println("История запросов: " + inMemoryTaskManager.getHistory());
        // Краткий вид истории, по id
        for (Task task : inMemoryTaskManager.getHistory()) {
            System.out.println("Краткий вид истории, по id: " + task.getId());
        }
    }
}
