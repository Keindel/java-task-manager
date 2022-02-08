import tasks.Task;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
public class InMemoryHistoryManager implements HistoryManager{
    private List<Task> history;

    public InMemoryHistoryManager(){
        this.history = new LinkedList<>();
    }

    // Метод для удаления из истории - вызывается при удалении задач
    @Override
    public void remove(Task task){
        history.remove(task);
    }

    @Override
    public void add(Task task) {
        if (history.size() >= 10) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public Collection<Task> getHistory() {
        return history;
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "history=" + history +
                '}';
    }
}