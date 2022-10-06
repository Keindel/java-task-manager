public final class Managers {

    public static TaskManager getDefault(HistoryManager historyManager, String url) {
        return new HTTPTaskManager(historyManager, url);
    }

    public static HistoryManager getDefaultHistory() {
            return new InMemoryHistoryManager();
    }
}
