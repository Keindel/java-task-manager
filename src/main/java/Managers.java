import java.nio.file.Path;

public final class Managers {

    public static TaskManager getDefault(HistoryManager historyManager, String url) {
        return new HTTPTaskManager(historyManager, url);
//        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getDefaultHistory() {
            return new InMemoryHistoryManager();
    }
}
