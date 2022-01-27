import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    // Поля static для удобства вызова без экземпляра
    private static int nextId = 1;
    static HashMap<Integer, Task> tasks = new HashMap<>();
    static HashMap<Integer, SubTask> subTasks = new HashMap<>();
    static HashMap<Integer, EpicTask> epicTasks = new HashMap<>();

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
                if (task.getId() == 0){
                    task.setId(nextId);
                }
                tasks.put(task.getId(), task);
                nextId++;
                break;
            case "SubTask":
                SubTask subTask = (SubTask) obj;
                if (subTask.getId() == 0){
                    subTask.setId(nextId);
                }
                int inEpicID = subTask.getInEpicId();
                if (epicTasks.containsKey(inEpicID)) {
                    // todo Здесь в случае обновления через этот метод создания будет проблема
                    //  со списком subTask в Epic'e. Перед добавлением обновленной subTask в список Epic'a
                    //  надо удалить старую subTask с тем же id
                    /* В этом блоке в случае наличия Epic с id соответствующим inEpicID, указанным в этой SubTask,
                    заполняется хэш-таблица subTasks и список subTask'ов в Epic'е */
                    int subTaskId = subTask.getId();
                    subTasks.put(subTaskId, subTask);
                    // Будем заполнять сразу ссылки (а не id) на SubTask'и в список Epic'а
                    EpicTask parentEpic = epicTasks.get(inEpicID);
                    parentEpic.putSubTask(subTask);
                    nextId++;
                } else {
                    // todo Здесь в случае обновления через этот метод создания будет проблема с nextId
                    System.out.println("Нет эпика с id, соответствующим данной подзадаче");
                }
                break;
            case "Epic":
                EpicTask epicTask = (EpicTask) obj;
                if (epicTask.getId() == 0){
                    epicTask.setId(nextId);
                }
                epicTasks.put(epicTask.getId(), epicTask);
                nextId++;
                break;
            default:
                System.out.println("Передан объект недопустимого класса");
        }
    }

    /*
    Метод для обновления задачи любого типа вызывает метод создания,
    т.к. по ТЗ обновление реализуется как новая запись поверх старой
     */
    public static void updateAnyTask(Object obj) {
        makeTask(obj);
        // Уменьшаем nextId, т.к. он был увеличен при вызове метода создания
        nextId--;
    }

    // Метод удаления задачи любого типа по идентификатору
    public static void deleteAnyTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            int inEpicId = subTask.getInEpicId();
            // Удаляем ссылку на subTask из списка Epic'a
            epicTasks.get(inEpicId).removeSubTaskFromEpic(subTask);
            // Удаляем ссылку на subTask из HashMap
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
            // Удаляем ссылки на SubTask'и в списке подзадач Epic'a
            subList.clear(); //epicTasks.get(id).clearEpicSublist();
            // Удаляем сам Epic из HashMap;
            epicTasks.remove(id);
        } else {
            System.out.println("Задачи с таким id не существует");
        }
    }

    // Метод получения задачи любого типа по идентификатору
    public static Object getAnyTaskById(int id) {
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
    public static void deleteAllRegularTasks() {
        tasks.clear();
        System.out.println("Все задачи обычного типа удалены");
    }

    // Метод удаления всех подзадач
    public static void deleteAllSubTasks() {
        // Удаляем ссылки из HashMap
        subTasks.clear();
        // Удаляем ссылки из Epic'ов
        for (EpicTask epicTask : epicTasks.values()) {
            epicTask.clearEpicSublist();
        }
        System.out.println("Все подзадачи удалены");
    }

    // Метод удаления всех эпиков
    public static void deleteAllEpicTasks() {
        // Сначала удаляем все подзадачи эпиков
        deleteAllSubTasks();
        // Теперь удаляем сами Epic'и
        epicTasks.clear();
        System.out.println("Все эпики удалены");
    }

    // Метод для получения списка задач обычного типа
    public static ArrayList<Task> getRegularTasks() {
        ArrayList<Task> tasksListed = new ArrayList<>();
        for (Task task : tasks.values()) {
            tasksListed.add(task);
        }
        System.out.println("Список задач обычного типа: " + tasksListed);
        return tasksListed;
    }

    // Метод для получения списка подзадач
    public static ArrayList<SubTask> getSubTasks() {
        ArrayList<SubTask> subTasksListed = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            subTasksListed.add(subTask);
        }
        System.out.println("Список подзадач: " + subTasksListed);
        return subTasksListed;
    }

    // Метод для получения списка эпиков
    public static ArrayList<EpicTask> getEpicTasks() {
        ArrayList<EpicTask> epicTasksListed = new ArrayList<>();
        for (EpicTask epicTask : epicTasks.values()) {
            epicTasksListed.add(epicTask);
        }
        System.out.println("Список эпиков: " + epicTasksListed);
        return epicTasksListed;
    }

    // Метод получения списка всех подзадач определённого эпика
    public static ArrayList<SubTask> getSubTasksFromEpic(int epicId) {
        ArrayList<SubTask> subTasks = epicTasks.get(epicId).getThisEpicSubTasks();
        return subTasks;
    }
}
