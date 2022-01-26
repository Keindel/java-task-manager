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
}
