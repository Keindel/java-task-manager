import tasks.Task;

import java.util.Collection;

public interface HistoryManager {

    void add(Task task);
    // Метод просмотра истории задач - возвращать последние 10 просмотренных задач
    Collection<Task> getHistory();

    void remove(int id);
}
