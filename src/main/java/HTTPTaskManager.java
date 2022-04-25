import java.nio.file.Path;

public class HTTPTaskManager extends FileBackedTasksManager {

    public HTTPTaskManager(HistoryManager historyManager, Path taskManagerDataFile) {
        super(historyManager, taskManagerDataFile);
    }
}
