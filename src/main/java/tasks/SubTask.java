package tasks;

public class SubTask extends Task {
    // Поле с id эпика, в который вложена subtask
    private int inEpicId;
    private final TaskTypes taskType = TaskTypes.SUBTASK;

    // Конструктор подзадачи содержит дополнительный параметр - inEpicId
    public SubTask(Task task, int epicId) {
        super(task.name, task.description, task.id, task.status);
        this.inEpicId = epicId;
    }

    // Getter поля inEpicId
    public int getInEpicId() {
        return inEpicId;
    }

    public static SubTask fromString(String value) {
        String[] taskFields = value.split(",");
        int id = Integer.parseInt(taskFields[0]);
        String name = taskFields[2];
        Status status = Status.valueOf(taskFields[3]);
        String description = taskFields[4];
        String startTime = taskFields[5];
        long duration = Long.parseLong(taskFields[6]);

        int epicId = Integer.parseInt(taskFields[taskFields.length-1]);

        return new SubTask(new Task(
                new Task(name, description, id, status)
                , startTime, duration)
                , epicId);
    }

    @Override
    public String toString() {
        return String.join(super.toString()
                , String.valueOf(inEpicId));
    }
}
