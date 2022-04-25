import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gsonAdapters.DurationAdapter;
import gsonAdapters.LocalDateTimeAdapter;
import tasks.Task;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HTTPTaskManager(HistoryManager historyManager, String url) {
        super(historyManager);
        this.kvTaskClient = new KVTaskClient(url);
    }

    public KVTaskClient getKvTaskClient() {
        return kvTaskClient;
    }

    @Override
    public void save() {
        kvTaskClient.put("tasks", gson.toJson(getPrioritizedTasks()));
    }
    /*
     * Конструктор HTTPTaskManager должен будет вместо имени файла принимать URL к серверу KVServer.
     *
     *  Также HTTPTaskManager создаёт KVTaskClient, из которого можно получить исходное состояние менеджера.
     *
     * Вам нужно заменить вызовы сохранения состояния в файлах на вызов клиента.

     * В конце обновите статический метод getDefault() в утилитарном классе Managers, чтобы он возвращал HTTPTaskManager.
     * */
}
