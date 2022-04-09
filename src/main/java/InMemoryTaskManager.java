import tasks.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static tasks.Task.MINUTES_DISCRETIZATION;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private Map<Integer, Task> tasks;
    private Map<Integer, SubTask> subTasks;
    private Map<Integer, EpicTask> epicTasks;
    // Объявляем переменную менеджера историй
    private HistoryManager historyManager;
    private Set<Task> tasksAndSubtasksPrioritizedSet;
    private Set<Long> scheduleSet;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        this.historyManager = historyManager;
        // Если все-таки понадобится, чтобы задачи могли начинаться в одно и то же время
        // добавлен 2ой компаратор. Иначе treeSet считает их идентичными.
        this.tasksAndSubtasksPrioritizedSet = new TreeSet<>(Comparator
                .comparing(Task::getStartTime)
                .thenComparing(Task::getId));
        this.scheduleSet = new HashSet<>();
    }

    // Метод получения объекта менеджера истории
    public HistoryManager getHistoryManager(){
        return historyManager;
    }

    /*
        Метод для создания-сохранения задачи в зависимости от класса переданного объекта (от типа задачи)
        */
    @Override
    public void makeTask(Task task) {
        if (task == null) {
            System.out.println("Передана пустая ссылка, не содержащая объекта");
            return;
        }
        String taskType = String.valueOf(task.getClass());
        switch (taskType) {
            case "class tasks.Task":
                saveTask(task);
                break;
            case "class tasks.SubTask":
                saveSubTask((SubTask) task);
                break;
            case "class tasks.EpicTask":
                saveEpicTask((EpicTask) task);
                break;
            default:
                System.out.println("Передан объект недопустимого класса");
        }
    }

    private void saveEpicTask(EpicTask epicTask) {
        if (epicTask.getId() <= 0) {
            epicTask.setId(nextId);
            nextId++;
        }
        epicTasks.put(epicTask.getId(), epicTask);
        incrementNextIdWhileOccupied();
    }

    private void saveSubTask(SubTask subTask) {
        int inEpicID = subTask.getInEpicId();
        /*
        Если такой id уже есть - перезапись (обновление) существующей задачи. В HashMap будет перезапись,
        а из списка Epic'a SubTask надо именно удалить, а потом добавить снова.
        */
        if (subTask.getId() <= 0) {
            subTask.setId(nextId);
            nextId++;
            incrementNextIdWhileOccupied();
        } else {
            if (epicTasks.containsKey(inEpicID)) {
                removeOldSubTaskFromEpic(subTask, inEpicID);
            } else {
                System.out.println("Нет эпика с таким id, на который ссылается данная подзадача");
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
            tasksAndSubtasksPrioritizedSet.add(subTask);
            // Будем заполнять сразу ссылки (а не id) на SubTask'и в список Epic'а
            EpicTask parentEpic = epicTasks.get(inEpicID);
            parentEpic.putSubTask(subTask);
            incrementNextIdWhileOccupied();
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
        if (task.getId() <= 0) {
            task.setId(nextId);
            nextId++;
        }
        tasks.put(task.getId(), task);
        tasksAndSubtasksPrioritizedSet.add(task);
        incrementNextIdWhileOccupied();
    }

    private void incrementNextIdWhileOccupied() {
        while (tasks.containsKey(nextId)
        || subTasks.containsKey(nextId)
        || epicTasks.containsKey(nextId)) {
            nextId++;
        }
    }

    //todo
    private boolean checkTimeInSchedule() {

        return true;
    }

    private boolean reserveTimeInSchedule(Task task) {
        if (task.getStartTime().isEqual(LocalDateTime.MAX)) {
            return true;
        }
        for (long i = task.getStartInMinutes(); i < task.getEndInMinutes(); i += MINUTES_DISCRETIZATION) {
            if (scheduleSet.contains(i)) {
                return false;
            }
        }
        for (long i = task.getStartInMinutes(); i < task.getEndInMinutes(); i += MINUTES_DISCRETIZATION) {
            scheduleSet.add(i);
        }
        return true;
    }

    private void freeTimeInSchedule(Task task) {
        if (task.getStartTime().isEqual(LocalDateTime.MAX)) {
            return;
        }
        for (long i = task.getStartInMinutes(); i < task.getEndInMinutes(); i += MINUTES_DISCRETIZATION) {
            scheduleSet.remove(i);
        }
    }

    public Set<Task> getPrioritizedTasks(){
        return tasksAndSubtasksPrioritizedSet;
    }

    /*
    Метод для обновления задачи любого типа вызывает метод создания,
    т.к. по ТЗ обновление реализуется как новая запись поверх старой
     */
    @Override
    public void updateTask(Task task) {
        makeTask(task);
    }

    // Метод удаления задачи любого типа по идентификатору
    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            tasksAndSubtasksPrioritizedSet.remove(tasks.remove(id));
        } else if (subTasks.containsKey(id)) {
            historyManager.remove(id);
            SubTask subTask = subTasks.get(id);
            int inEpicId = subTask.getInEpicId();
            // Удаляем ссылку на subTask из списка Epic'a
            epicTasks.get(inEpicId).removeSubTaskFromEpic(subTask);
            // Удаляем ссылку на subTask из HashMap
            tasksAndSubtasksPrioritizedSet.remove(subTasks.remove(id));
        } else if (epicTasks.containsKey(id)) {
            // При удалении Epic'a удаляем все входящие в него SubTask
            // Сначала получаем список SubTask'ов Epic'a
            Collection<SubTask> subList = getSubTasksFromEpic(id);
            // Удаляем ссылки на SubTask'и Epic'a в HashMap'е SubTask'ов
            for (SubTask subTask : subList) {
                historyManager.remove(subTask.getId());
                int subTaskId = subTask.getId();
                tasksAndSubtasksPrioritizedSet.remove(subTasks.remove(subTaskId));
            }
            // Удаляем ссылки на SubTask'и в списке подзадач Epic'a
            subList.clear(); //epicTasks.get(id).clearEpicSublist();
            // Удаляем сам Epic из HashMap;
            historyManager.remove(id);
            tasksAndSubtasksPrioritizedSet.remove(epicTasks.remove(id));
        } else {
            System.out.println("Задачи с таким id не существует");
        }
    }

    // Метод получения задачи любого типа по идентификатору
    @Override
    public Task getTaskById(int id) {
        // Для упрощения и читабельности кода сначала общая проверка
        if (!tasks.containsKey(id) && !subTasks.containsKey(id) && !epicTasks.containsKey(id)) {
            System.out.println("Задачи с таким id не существует");
            return null;
        }
        // Следующий блок выполняется, если id существует. Поэтому можно корректировать размер списка history
        // Далее основные блоки
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);
            return task;
        } else if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            historyManager.add(subTask);
            return subTask;
        } else if (epicTasks.containsKey(id)) {
            EpicTask epicTask = epicTasks.get(id);
            historyManager.add(epicTask);
            return epicTasks.get(id);
        }
        return null;
    }

    // Метод удаления всех задач обычного типа
    @Override
    public void deleteAllRegularTasks() {
        for (Task task: tasks.values()) {
            historyManager.remove(task.getId());
            tasksAndSubtasksPrioritizedSet.remove(task);
        }
        tasks.clear();
        System.out.println("Все задачи обычного типа удалены");
    }

    // Метод удаления всех подзадач
    @Override
    public void deleteAllSubTasks() {
        for (SubTask subTask: subTasks.values()) {
            historyManager.remove(subTask.getId());
            tasksAndSubtasksPrioritizedSet.remove(subTask);
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
            historyManager.remove(epicTask.getId());
            tasksAndSubtasksPrioritizedSet.remove(epicTask);
        }
        // Сначала удаляем все подзадачи эпиков
        deleteAllSubTasks();
        // Теперь удаляем сами Epic'и
        epicTasks.clear();
        System.out.println("Все эпики удалены");
    }

    // Метод для получения списка задач обычного типа
    @Override
    public Collection<Task> getRegularTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Метод для получения списка подзадач
    @Override
    public Collection<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    // Метод для получения списка эпиков
    @Override
    public Collection<EpicTask> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    // Метод получения списка всех подзадач определённого эпика
    @Override
    public Collection<SubTask> getSubTasksFromEpic(int epicId) {
        return epicTasks.get(epicId).getThisEpicSubTasks();
    }
}
