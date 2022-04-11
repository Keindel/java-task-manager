import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.Collection;
import java.util.Set;

public interface TaskManager {

    /*
        Метод для создания-сохранения задачи в зависимости от класса переданного объекта (от типа задачи)
        */
    void makeTask(Task task);

    /*
        Метод для обновления задачи любого типа вызывает метод создания,
        т.к. по ТЗ обновление реализуется как новая запись поверх старой
         */
    void updateTask(Task task);

    // Метод удаления задачи любого типа по идентификатору
    void deleteTaskById(int id);

    // Метод получения задачи любого типа по идентификатору
    Task getSavedTaskByIdAndAffectHistory(int id);

    // Метод удаления всех задач обычного типа
    void deleteAllRegularTasks();

    // Метод удаления всех подзадач
    void deleteAllSubTasks();

    // Метод удаления всех эпиков
    void deleteAllEpicTasks();

    // Метод для получения списка задач обычного типа
    Collection<Task> getRegularTasks();

    // Метод для получения списка подзадач
    Collection<SubTask> getSubTasks();

    // Метод для получения списка эпиков
    Collection<EpicTask> getEpicTasks();

    // Метод получения списка всех подзадач определённого эпика
    Collection<SubTask> getSubTasksFromEpic(int epicId);

    Set<Task> getPrioritizedTasks();


}
