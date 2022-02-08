import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, SubTask> subTasks;
    private HashMap<Integer, EpicTask> epicTasks;
    // Объявляем переменную менеджера историй
    private InMemoryHistoryManager inMemoryHistoryManager;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        this.inMemoryHistoryManager = new InMemoryHistoryManager();
    }

    // Метод получения объекта менеджера истории
    public InMemoryHistoryManager getInMemoryHistoryManager(){
        return inMemoryHistoryManager;
    }

    /*
        Метод для создания-сохранения задачи в зависимости от класса переданного объекта (от типа задачи)
        */
    @Override
    public void makeAnyTask(Object obj) {
        if (obj == null) {
            System.out.println("Передана пустая ссылка, не содержащая объекта");
            return;
        }
        String taskType = String.valueOf(obj.getClass());
        switch (taskType) {
            case "class tasks.Task":
                saveTask((Task) obj);
                break;
            case "class tasks.SubTask":
                saveSubTask((SubTask) obj);
                break;
            case "class tasks.EpicTask":
                saveEpicTask((EpicTask) obj);
                break;
            default:
                System.out.println("Передан объект недопустимого класса");
        }
    }

    private void saveEpicTask(EpicTask epicTask) {
        if (epicTask.getId() == 0) {
            epicTask.setId(nextId);
        }
        epicTasks.put(epicTask.getId(), epicTask);
        nextId++;
    }

    private void saveSubTask(SubTask subTask) {
        int inEpicID = subTask.getInEpicId();
        /*
        Если id != 0, то это перезапись (обновление) существующей задачи. В HashMap будет перезапись,
        а из списка Epic'a SubTask надо именно удалить, а потом добавить снова
        */
        if (subTask.getId() == 0) {
            subTask.setId(nextId);
        } else {
            if (epicTasks.containsKey(inEpicID)) {
                removeOldSubTaskFromEpic(subTask, inEpicID);
            } else {
                System.out.println("Нет эпика с таким id, на который ссылается данная подзадача");
                /*
                Увеличиваем nextId, т.к. этот блок работает только после вызова
                метода updateAnyTask(), в котором предусмотрена команда nextId--
                */
                nextId++;
                return;
            }
        }
        /*
        В случае наличия Epic с id соответствующим inEpicID, указанным в этой SubTask,
        заполняется хэш-таблица subTasks и список subTask'ов в Epic'е
        */
        if (epicTasks.containsKey(inEpicID)) {
            int subTaskId = subTask.getId();
            subTasks.put(subTaskId, subTask);
            // Будем заполнять сразу ссылки (а не id) на SubTask'и в список Epic'а
            EpicTask parentEpic = epicTasks.get(inEpicID);
            parentEpic.putSubTask(subTask);
            nextId++;
        } else {
            System.out.println("Нет эпика с таким id, на который ссылается данная подзадача");
        }
    }

    private void removeOldSubTaskFromEpic(SubTask subTask, int inEpicID) {
        int subTaskId = subTask.getId();
        EpicTask parentEpic = epicTasks.get(inEpicID);
        for (SubTask subTaskSameId : parentEpic.getThisEpicSubTasks()) {
            if (subTaskSameId.getId() == subTaskId) {
                parentEpic.removeSubTaskFromEpic(subTaskSameId);
                break;
            }
        }
    }

    private void saveTask(Task task) {
        if (task.getId() == 0) {
            task.setId(nextId);
        }
        tasks.put(task.getId(), task);
        nextId++;
    }

    /*
    Метод для обновления задачи любого типа вызывает метод создания,
    т.к. по ТЗ обновление реализуется как новая запись поверх старой
     */
    @Override
    public void updateAnyTask(Object obj) {
        makeAnyTask(obj);
        // Уменьшаем nextId, т.к. он был увеличен при вызове метода создания
        nextId--;
    }

    // Метод удаления задачи любого типа по идентификатору
    @Override
    public void deleteAnyTaskById(int id) {
        if (tasks.containsKey(id)) {
            inMemoryHistoryManager.remove(tasks.get(id));
            tasks.remove(id);
        } else if (subTasks.containsKey(id)) {
            inMemoryHistoryManager.remove(subTasks.get(id));
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
                inMemoryHistoryManager.remove(subTask);
                int subTaskId = subTask.getId();
                subTasks.remove(subTaskId);
            }
            // Удаляем ссылки на SubTask'и в списке подзадач Epic'a
            subList.clear(); //epicTasks.get(id).clearEpicSublist();
            // Удаляем сам Epic из HashMap;
            inMemoryHistoryManager.remove(epicTasks.get(id));
            epicTasks.remove(id);
        } else {
            System.out.println("Задачи с таким id не существует");
        }
    }

    // Метод получения задачи любого типа по идентификатору
    @Override
    public Task getAnyTaskById(int id) {
        // Для упрощения и читабельности кода сначала общая проверка
        if (!tasks.containsKey(id) && !subTasks.containsKey(id) && !epicTasks.containsKey(id)) {
            System.out.println("Задачи с таким id не существует");
            return null;
        }
        // Следующий блок выполняется, если id существует. Поэтому можно корректировать размер списка history
        // Далее основные блоки
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            inMemoryHistoryManager.add(task);
            return task;
        } else if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            inMemoryHistoryManager.add(subTask);
            return subTask;
        } else if (epicTasks.containsKey(id)) {
            EpicTask epicTask = epicTasks.get(id);
            inMemoryHistoryManager.add(epicTask);
            return epicTasks.get(id);
        }
        return null;
    }

    // Метод удаления всех задач обычного типа
    @Override
    public void deleteAllRegularTasks() {
        for (Task task: tasks.values()) {
            inMemoryHistoryManager.remove(task);
        }
        tasks.clear();
        System.out.println("Все задачи обычного типа удалены");
    }

    // Метод удаления всех подзадач
    @Override
    public void deleteAllSubTasks() {
        for (SubTask subTask: subTasks.values()) {
            inMemoryHistoryManager.remove(subTask);
        }
        // Удаляем ссылки из HashMap
        subTasks.clear();
        // Удаляем ссылки из Epic'ов
        for (EpicTask epicTask : epicTasks.values()) {
            epicTask.clearEpicSublist();
        }
        System.out.println("Все подзадачи удалены");
    }

    // Метод удаления всех эпиков
    @Override
    public void deleteAllEpicTasks() {
        for (EpicTask epicTask: epicTasks.values()) {
            inMemoryHistoryManager.remove(epicTask);
        }
        // Сначала удаляем все подзадачи эпиков
        deleteAllSubTasks();
        // Теперь удаляем сами Epic'и
        epicTasks.clear();
        System.out.println("Все эпики удалены");
    }

    // Метод для получения списка задач обычного типа
    @Override
    public ArrayList<Task> getRegularTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Метод для получения списка подзадач
    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    // Метод для получения списка эпиков
    @Override
    public ArrayList<EpicTask> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    // Метод получения списка всех подзадач определённого эпика
    @Override
    public ArrayList<SubTask> getSubTasksFromEpic(int epicId) {
        return epicTasks.get(epicId).getThisEpicSubTasks();
    }
}
