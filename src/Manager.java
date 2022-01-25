import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Manager {
    private int nextId = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Task> subTasks = new HashMap<>();
    HashMap<Integer, Task> epics = new HashMap<>();
    static Scanner scanner = new Scanner(System.in);

    public static int сhooseType() {
        System.out.println("Приложение Менеджер Задач приветствует Вас!");
        System.out.println("Какой тип задач Вас сейчас интересует?");
        System.out.println("1 - Обычные задачи");
        System.out.println("2 - Подзадачи");
        System.out.println("3 - Эпики");
        int command = scanner.nextInt();
        switch (command) {
            case 1:
                System.out.println("Вы выбрали обычные задачи");
                return command;
            case 2:
                System.out.println("Вы выбрали подзадачи");
                return command;
            case 3:
                System.out.println("Вы выбрали эпики");
                return command;
            default:
                System.out.println("Выбран несуществующий тип");
        }

        // Метод для создания-сохранения задачи
        public static void makeTask(){
            // В зависимости от типа задачи

            Task task = new
        }

        // Метод для получения списка задач
        public static ArrayList<Task> getTasks {
            ArrayList<Task> tasks = new ArrayList<>();
            for (task :
                 ) {

            }
            return tasks;
        }
        System.out.println("1 - Получение списка всех задач.");
        System.out.println("2 - Удаление всех задач");
        System.out.println("3 - Получение по идентификатору.");
        System.out.println("4 - Создание задачи.");
        System.out.println("5 - Обновление задачи");
        System.out.println("6 - Удаление по идентификатору");

        //
        System.out.println("Получение списка всех подзадач определённого эпика.");
    }

    public static void
}
