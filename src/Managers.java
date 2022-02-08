public abstract class Managers {

    public static TaskManager getDefault(HistoryManager historyManager) {
        return new InMemoryTaskManager((InMemoryHistoryManager) historyManager);
    }

    public static HistoryManager getDefaultHistory() {
            return new InMemoryHistoryManager();
    }
}
