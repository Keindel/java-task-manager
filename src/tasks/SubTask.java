package tasks;

import java.util.Objects;

public class SubTask extends Task {
    // Поле с id эпика, в который вложена subtask
    private int inEpicId;

    public int getInEpicId() {
        return inEpicId;
    }

    public void setInEpicId(int inEpicId) {
        this.inEpicId = inEpicId;
    }

    // Конструктор подзадачи содержит допонительный параметр - inEpicId
    public SubTask(Task task, int epicId) {
        super(task.name, task.description, task.id, task.status);
        this.inEpicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subtask = (SubTask) o;
        return Objects.equals(inEpicId, subtask.inEpicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), inEpicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicName='" + inEpicId + '\'' +
                '}';
    }
}
