import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.Collection;

public interface TaskManager {

    /*
        Метод для создания-сохранения задачи в зависимости от класса переданного объекта (от типа задачи)
        */
    void makeAnyTask(Object obj);

    /*
        Метод для обновления задачи любого типа вызывает метод создания,
        т.к. по ТЗ обновление реализуется как новая запись поверх старой
         */
    void updateAnyTask(Object obj);

    // Метод удаления задачи любого типа по идентификатору
    void deleteAnyTaskById(int id);

    // Метод получения задачи любого типа по идентификатору
    Task getAnyTaskById(int id);

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
}
