public abstract class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    // Метод должен возвращать объект InMemoryHistoryManager
    public static HistoryManager getDefaultHistory(TaskManager taskManager) throws IllegalArgumentException {
        if (taskManager instanceof InMemoryTaskManager) {
            InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) taskManager;
            return inMemoryTaskManager.getInMemoryHistoryManager();
        } else throw new IllegalArgumentException();
    }
}
