import tasks.*;

public class Main {
    public static void main(String[] args) {
        // Тестирование приложения
        // 2 задачи для теста
        Task testTask1 = new Task("", "");
        Task testTask2 = new Task("", "");
        Manager.makeAnyTask(testTask1);
        Manager.makeAnyTask(testTask2);
        System.out.println(Manager.getAnyTaskById(1));
        System.out.println(Manager.getAnyTaskById(2));
        // 1ый эпик для теста
        Task testTask3 = new Task("", "");
        EpicTask testEpicTask3 = new EpicTask(testTask3);
        Manager.makeAnyTask(testEpicTask3);
        System.out.println(Manager.getAnyTaskById(3));
        // 2 подзадачи для 1го эпика
        Task testTask4 = new Task("", "");
        Task testTask5 = new Task("", "");
        SubTask subTask4 = new SubTask(testTask4, 3);
        SubTask subTask5 = new SubTask(testTask5, 3);
        Manager.makeAnyTask(subTask4);
        Manager.makeAnyTask(subTask5);
        System.out.println(Manager.getAnyTaskById(4));
        System.out.println(Manager.getAnyTaskById(5));
        System.out.println(Manager.getAnyTaskById(3));
        // 2ой эпик для теста
        Task testTask6 = new Task("", "");
        EpicTask testEpicTask6 = new EpicTask(testTask6);
        Manager.makeAnyTask(testEpicTask6);
        System.out.println(Manager.getAnyTaskById(6));
        // 1 подзадача для 2го эпика
        Task testTask7 = new Task("", "");
        SubTask subTask7 = new SubTask(testTask7, 6);
        Manager.makeAnyTask(subTask7);
        System.out.println(Manager.getAnyTaskById(7));
        System.out.println(Manager.getAnyTaskById(6));

        System.out.println("Список эпиков: " + Manager.getEpicTasks());
        System.out.println("Список обычных задач: " + Manager.getRegularTasks());
        System.out.println("Список подзадач: " + Manager.getSubTasks());

        // Обновление статусов обычных задач
        Task testTask11 = new Task("", "", 1, "DONE");
        Task testTask12 = new Task("", "", 2, "IN_PROGRESS");
        Manager.updateAnyTask(testTask11);
        Manager.updateAnyTask(testTask12);
        System.out.println("Задача с id=1 " + Manager.getAnyTaskById(1));
        System.out.println("Задача с id=2 " + Manager.getAnyTaskById(2));

        // Обновление статусов подзадач 1го эпика
        Task testTask14 = new Task("", "", 4, "IN_PROGRESS");
        SubTask subTask14 = new SubTask(testTask14, 3);
        Task testTask15 = new Task("", "", 5, "DONE");
        SubTask subTask15 = new SubTask(testTask15, 3);
        Manager.updateAnyTask(subTask14);
        Manager.updateAnyTask(subTask15);
        System.out.println("Подзадача с id=4 " + Manager.getAnyTaskById(4));
        System.out.println("Подзадача с id=5 " + Manager.getAnyTaskById(5));
        System.out.println("Эпик с id=3 " + Manager.getAnyTaskById(3));

        // Обновление статуса подзадачи 2го эпика
        Task testTask17 = new Task("", "", 7, "DONE");
        SubTask subTask17 = new SubTask(testTask17, 6);
        Manager.updateAnyTask(subTask17);
        System.out.println("Подзадача с id=7 " + Manager.getAnyTaskById(7));
        System.out.println("Эпик с id=6 " + Manager.getAnyTaskById(6));

        // Удаление задачи
        Manager.deleteAnyTaskById(2);
        System.out.println("Список обычных задач после удаления: " + Manager.getRegularTasks());

        // Удаление эпика
        Manager.deleteAnyTaskById(3);
        System.out.println("Список эпиков после удаления: " + Manager.getEpicTasks());
        System.out.println("Список подзадач после удаления эпика: " + Manager.getSubTasks());

        // Обновление SubTask с несуществующим epicId
        Task testTask27 = new Task("", "", 7, "IN_PROGRESS");
        SubTask subTask27 = new SubTask(testTask27, 16);
        Manager.updateAnyTask(subTask27);
    }
}
