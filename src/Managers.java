public abstract class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
