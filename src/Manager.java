import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private static int nextId = 0;
    // Static для вызова без экземпляра
    static HashMap<Integer, Task> tasks = new HashMap<>();
    static HashMap<Integer, SubTask> subTasks = new HashMap<>();
    static HashMap<Integer, EpicTask> epicTasks = new HashMap<>();

/*
    // static Scanner для вызова в методах без обращения к экземпляру
    static Scanner scanner = new Scanner(System.in);
    // Метод для выбора пользователем типа задач к дальнейшей работе
    public static int chooseType() {
        System.out.println("Приложение Менеджер Задач приветствует Вас!");
        System.out.println("Какой тип задач Вас сейчас интересует?");
        System.out.println("1 - Обычные задачи");
        System.out.println("2 - Подзадачи");
        System.out.println("3 - Эпики");
        System.out.println("100 - Выйти из приложения");
        int taskType = scanner.nextInt();
        switch (taskType) {
            case 1:
                System.out.println("Вы выбрали 1 - Обычные задачи");
                return taskType;
            case 2:
                System.out.println("Вы выбрали 2 - Подзадачи");
                return taskType;
            case 3:
                System.out.println("Вы выбрали 3 - Эпики");
                return taskType;
            case 0:
                System.out.println("Вы выбрали 0 - Выйти из приложения.\nЗавершаем работу приложения");
                return taskType;
            default:
                System.out.println("Выбран несуществующий тип");
                return chooseType();
        }
    }

    // Метод для выбора пользователем действия с задачами
    public static int chooseAction(int taskType) {
        System.out.println("Выберите действие:");
        System.out.println("1 - Получение списка всех задач.");
        System.out.println("2 - Удаление всех задач");
        System.out.println("3 - Получение по идентификатору.");
        System.out.println("4 - Создание задачи.");
        System.out.println("5 - Обновление задачи");
        System.out.println("6 - Удаление по идентификатору");
        // В зависимости от типа задачи
        if (taskType == 3) {
            System.out.println("7 - Получение списка всех подзадач определённого эпика.");
        }
        System.out.println("0 - Вернуться на уровень выше");
        int action = scanner.nextInt();
        switch (action) {
            case 1:
                System.out.println("Вы выбрали 1 - Получение списка всех задач.");
                return action;
            case 2:
                System.out.println("Вы выбрали 2 - Удаление всех задач");
                return action;
            case 3:
                System.out.println("Вы выбрали 3 - Получение по идентификатору.");
                return action;
            case 4:
                System.out.println("Вы выбрали 4 - Создание задачи.");
                return action;
            case 5:
                System.out.println("Вы выбрали 5 - Обновление задачи");
                return action;
            case 6:
                System.out.println("Вы выбрали 6 - Удаление по идентификатору");
                return action;
            case 7:
                if (taskType == 3) {
                    System.out.println("Вы выбрали 7 - Получение списка всех подзадач определённого эпика.");
                    return action;
                }
            case 0:
                System.out.println("Вы выбрали 0 - Вернуться на уровень выше");
                return action;
            default:
                System.out.println("Недопустимый ввод.");
                return chooseAction(taskType);
        }
    }
*/

    /*
    Метод для создания-сохранения задачи в зависимости от класса переданного объекта (от типа задачи)
    */
    public static void makeTask(Object obj) {
        if (obj == null) {
            System.out.println("Передана пустая ссылка, не содержащая объекта");
            return;
        }
        String taskType = String.valueOf(obj.getClass());
        switch (taskType) {
            case "Task":
                Task task = (Task) obj;
                tasks.put(task.getId(), task);
                nextId++;
                break;
            case "SubTask":
                SubTask subTask = (SubTask) obj;
                int inEpicID = subTask.getInEpicId();
                if (epicTasks.containsKey(inEpicID)) {
                    /* В этом блоке в случае наличия Epic с id соответствующим inEpicID, указанным в этой SubTask,
                    заполняется хэш-таблица subTasks */
                    int subTaskId = subTask.getId();
                    subTasks.put(subTaskId, subTask);
                    /*
                    // Заполняем id SubTask в список, чтобы Epic знал о своих SubTask'ах
                    EpicTask parentEpic = epicTasks.get(inEpicID);
                    parentEpic.putSubTaskId(subTaskId);
                    */

                    // Будем заполнять сразу ссылки на SubTask'и в список Epic'а
                    EpicTask parentEpic = epicTasks.get(inEpicID);
                    parentEpic.putSubTask(subTask);
                    // Обновление статуса Epic'a, т.к. он в зависимости от статусов SubTask'ов
                    parentEpic.updateEpicStatus();
                    nextId++;
                } else {
                    System.out.println("Нет эпика с id, соответствующим данной подзадаче");
                }
                break;
            case "Epic":
                EpicTask epicTask = (EpicTask) obj;
                epicTasks.put(epicTask.getId(), epicTask);
                nextId++;
                break;
            default:
                System.out.println("Передан объект недопустимого класса");
        }
    }

    // Метод для обновления вызывает метод создания, т.к. по ТЗ обновление реализуется как новая запись поверх старой
    public static void updateTask(Object obj) {
        makeTask(obj);
        // Уменьшаем nextId, т.к. он был увеличен при вызове метода создания
        nextId--;
    }

    // Метод удаления задачи по идентификатору
    public static void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            int inEpicId = subTask.getInEpicId();
            // Удаляем ссылку на subTask из списка Epic'a
            epicTasks.get(inEpicId).removeSubTaskFromEpic(subTask);
            // Удаляем subTask из HashMap
            subTasks.remove(id);
        } else if (epicTasks.containsKey(id)) {
            // При удалении Epic'a удаляем все входящие в него SubTask
            // Сначала получаем список SubTask'ов Epic'a
            ArrayList<SubTask> subList = getSubTasksFromEpic(id);
            // Удаляем ссылки на SubTask'и Epic'a в HashMap'е SubTask'ов
            for (SubTask subTask : subList) {
                int subTaskId = subTask.getId();
                subTasks.remove(subTaskId);
            }
            // Удаляем ссылки в списке подзадач Epic'a
            epicTasks.get(id).clearEpicSublist();
            // Удаляем сам Epic из HashMap;
            epicTasks.remove(id);
        } else {
            System.out.println("Задачи с таким id не существует");
        }
    }


    // Метод получения задачи по идентификатору
    public static Object getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        } else if (epicTasks.containsKey(id)) {
            return epicTasks.get(id);
        } else {
            System.out.println("Задачи с таким id не существует");
            return null;
        }
    }

    // Метод удаления всех задач обычного типа
    public static void deleteAllTasks() {
        tasks.clear();
        System.out.println("Все задачи обычного типа удалены");
    }

    // Метод удаления всех подзадач
    public static void deleteAllSubTasks() {
        subTasks.clear();
        System.out.println("Все подзадачи удалены");
    }

    // Метод удаления всех эпиков
    public static void deleteAllEpicTasks() {
        epicTasks.clear();
        System.out.println("Все эпики удалены");
    }

    // Метод для получения списка задач обычного типа
    public static ArrayList<Task> getTasks() {
        ArrayList<Task> tasksListed = new ArrayList<>();
        for (int id : tasks.keySet()) {
            tasksListed.add(tasks.get(id));
        }
        System.out.println("Список задач обычного типа: " + tasksListed);
        return tasksListed;
    }

    // Метод для получения списка подзадач
    public static ArrayList<SubTask> getSubTasks() {
        ArrayList<SubTask> subTasksListed = new ArrayList<>();
        for (int id : subTasks.keySet()) {
            subTasksListed.add(subTasks.get(id));
        }
        System.out.println("Список подзадач: " + subTasksListed);
        return subTasksListed;
    }

    // Метод для получения списка эпиков
    public static ArrayList<EpicTask> getEpicTasks() {
        ArrayList<EpicTask> epicTasksListed = new ArrayList<>();
        for (int id : epicTasks.keySet()) {
            epicTasksListed.add(epicTasks.get(id));
        }
        System.out.println("Список эпиков: " + epicTasksListed);
        return epicTasksListed;
    }

    // Метод получения списка всех подзадач определённого эпика
    public static ArrayList<SubTask> getSubTasksFromEpic(int epicId) {
        ArrayList<SubTask> subTasks = epicTasks.get(epicId).getThisEpicSubTasks();
        return subTasks;
    }

    /*
    // Метод получения списка всех подзадач определённого эпика по его id
    public static ArrayList<SubTask> getSubTasksFromEpic(int epicId) {
        ArrayList<Integer> subTasksIds = epicTasks.get(epicId).getSubTasksIds();
        for (int subTaskId : subTasksIds) {
            ArrayList<SubTask> subTasksFromEpic = new ArrayList<>();
            SubTask subTask = (SubTask) getTaskById(subTaskId);
            subTasksFromEpic.add();
        }
             ){

        }
    }
    */

    /*
    Метод получения списка всех подзадач определённого эпика по его id
    (формирование списка, если бы эпик не знал свои подзадачи заранее)
     */
    /*
    public static ArrayList<SubTask> getSubTasksFromEpic(int epicId) {
        ArrayList<SubTask> subTasksFromEpic = new ArrayList<>();
        if (epicTasks.containsKey(epicId)){
            for (int subTaskId : subTasks.keySet()) {
                int inEpicId = subTasks.get(subTaskId).getInEpicId();
                if (inEpicId == epicId) {
                    SubTask subTask = subTasks.get(subTaskId);
                    subTasksFromEpic.add(subTask);
                }
            }
        } else {
            System.out.println("Эпик с таким id не существует");
        }
        System.out.println("Список подзадач в эпике с id=" + epicId + " : " + subTasksFromEpic);
        return subTasksFromEpic;
    }*/
}
