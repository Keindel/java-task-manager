import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int nextId = 1;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();
    HashMap<Integer, EpicTask> epicTasks = new HashMap<>();

    /*
    Метод для создания-сохранения задачи в зависимости от класса переданного объекта (от типа задачи)
    */
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
    public void updateAnyTask(Object obj) {
        makeAnyTask(obj);
        // Уменьшаем nextId, т.к. он был увеличен при вызове метода создания
        nextId--;
    }

    // Метод удаления задачи любого типа по идентификатору
    public void deleteAnyTaskById(int id) {
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
    public Task getAnyTaskById(int id) {
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
    public void deleteAllRegularTasks() {
        tasks.clear();
        System.out.println("Все задачи обычного типа удалены");
    }

    // Метод удаления всех подзадач
    public void deleteAllSubTasks() {
        // Удаляем ссылки из HashMap
        subTasks.clear();
        // Удаляем ссылки из Epic'ов
        for (EpicTask epicTask : epicTasks.values()) {
            epicTask.clearEpicSublist();
        }
        System.out.println("Все подзадачи удалены");
    }

    // Метод удаления всех эпиков
    public void deleteAllEpicTasks() {
        // Сначала удаляем все подзадачи эпиков
        deleteAllSubTasks();
        // Теперь удаляем сами Epic'и
        epicTasks.clear();
        System.out.println("Все эпики удалены");
    }

    // Метод для получения списка задач обычного типа
    public ArrayList<Task> getRegularTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Метод для получения списка подзадач
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    // Метод для получения списка эпиков
    public ArrayList<EpicTask> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    // Метод получения списка всех подзадач определённого эпика
    public ArrayList<SubTask> getSubTasksFromEpic(int epicId) {
        return epicTasks.get(epicId).getThisEpicSubTasks();
    }
}
