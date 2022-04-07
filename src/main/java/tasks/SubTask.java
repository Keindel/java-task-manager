package tasks;

public class SubTask extends Task {
    // Поле с id эпика, в который вложена subtask
    private int inEpicId;

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
        int epicId = Integer.parseInt(taskFields[5]);

        return new SubTask(new Task(name, description, id, status), epicId);
    }

    @Override
    public String toString() {
        return String.join(","
                , String.valueOf(id)
                , TaskTypes.SUBTASK.toString()
                , name
                , status.toString()
                , description
                , String.valueOf(inEpicId));
    }
}
