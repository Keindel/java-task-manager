import tasks.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    // Метод просмотра истории задач - возвращать последние 10 просмотренных задач
    List<Task> getHistory();
}
