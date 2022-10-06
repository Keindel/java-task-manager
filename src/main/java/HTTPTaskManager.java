import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gsonAdapters.DurationAdapter;
import gsonAdapters.LocalDateTimeAdapter;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HTTPTaskManager(HistoryManager historyManager, String url) {
        super(historyManager);
        this.kvTaskClient = new KVTaskClient(url);
    }

    @Override
    public void save() {
        List<Task> allTasks = new ArrayList<>(getRegularTasks());
        allTasks.addAll(getEpicTasks());
        allTasks.addAll(getSubTasks());
        kvTaskClient.put("tasks", gson.toJson(allTasks));
        kvTaskClient.put("history", gson.toJson(InMemoryHistoryManager
                .toStringOfIds(this.getHistoryManager())));
    }
}
