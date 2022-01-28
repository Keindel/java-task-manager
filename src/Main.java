import tasks.*;

public class Main {
    public static void main(String[] args) {
        // Тестирование приложения
        Manager manager = new Manager();
        // 2 задачи для теста
        Task testTask1 = new Task("", "");
        Task testTask2 = new Task("", "");
        manager.makeAnyTask(testTask1);
        manager.makeAnyTask(testTask2);
        System.out.println(manager.getAnyTaskById(1));
        System.out.println(manager.getAnyTaskById(2));
        // 1ый эпик для теста
        Task testTask3 = new Task("", "");
        EpicTask testEpicTask3 = new EpicTask(testTask3);
        manager.makeAnyTask(testEpicTask3);
        System.out.println(manager.getAnyTaskById(3));
        // 2 подзадачи для 1го эпика
        Task testTask4 = new Task("", "");
        Task testTask5 = new Task("", "");
        SubTask subTask4 = new SubTask(testTask4, 3);
        SubTask subTask5 = new SubTask(testTask5, 3);
        manager.makeAnyTask(subTask4);
        manager.makeAnyTask(subTask5);
        System.out.println(manager.getAnyTaskById(4));
        System.out.println(manager.getAnyTaskById(5));
        System.out.println(manager.getAnyTaskById(3));
        // 2ой эпик для теста
        Task testTask6 = new Task("", "");
        EpicTask testEpicTask6 = new EpicTask(testTask6);
        manager.makeAnyTask(testEpicTask6);
        System.out.println(manager.getAnyTaskById(6));
        // 1 подзадача для 2го эпика
        Task testTask7 = new Task("", "");
        SubTask subTask7 = new SubTask(testTask7, 6);
        manager.makeAnyTask(subTask7);
        System.out.println(manager.getAnyTaskById(7));
        System.out.println(manager.getAnyTaskById(6));

        System.out.println("Список эпиков: " + manager.getEpicTasks());
        System.out.println("Список обычных задач: " + manager.getRegularTasks());
        System.out.println("Список подзадач: " + manager.getSubTasks());

        // Обновление статусов обычных задач
        Task testTask11 = new Task("", "", 1, "DONE");
        Task testTask12 = new Task("", "", 2, "IN_PROGRESS");
        manager.updateAnyTask(testTask11);
        manager.updateAnyTask(testTask12);
        System.out.println("Задача с id=1 " + manager.getAnyTaskById(1));
        System.out.println("Задача с id=2 " + manager.getAnyTaskById(2));

        // Обновление статусов подзадач 1го эпика
        Task testTask14 = new Task("", "", 4, "IN_PROGRESS");
        SubTask subTask14 = new SubTask(testTask14, 3);
        Task testTask15 = new Task("", "", 5, "DONE");
        SubTask subTask15 = new SubTask(testTask15, 3);
        manager.updateAnyTask(subTask14);
        manager.updateAnyTask(subTask15);
        System.out.println("Подзадача с id=4 " + manager.getAnyTaskById(4));
        System.out.println("Подзадача с id=5 " + manager.getAnyTaskById(5));
        System.out.println("Эпик с id=3 " + manager.getAnyTaskById(3));

        // Обновление статуса подзадачи 2го эпика
        Task testTask17 = new Task("", "", 7, "DONE");
        SubTask subTask17 = new SubTask(testTask17, 6);
        manager.updateAnyTask(subTask17);
        System.out.println("Подзадача с id=7 " + manager.getAnyTaskById(7));
        System.out.println("Эпик с id=6 " + manager.getAnyTaskById(6));

        // Удаление задачи
        manager.deleteAnyTaskById(2);
        System.out.println("Список обычных задач после удаления: " + manager.getRegularTasks());

        // Удаление эпика
        manager.deleteAnyTaskById(3);
        System.out.println("Список эпиков после удаления: " + manager.getEpicTasks());
        System.out.println("Список подзадач после удаления эпика: " + manager.getSubTasks());

        // Обновление SubTask с несуществующим epicId
        Task testTask27 = new Task("", "", 7, "IN_PROGRESS");
        SubTask subTask27 = new SubTask(testTask27, 16);
        manager.updateAnyTask(subTask27);
    }
}
