import java.util.Objects;

public class SubTask extends Task {
    private String epicName;

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public SubTask(String name, String description, int id, String status, String epicName) {
        super(name, description, id, status);
        this.epicName = epicName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subtask = (SubTask) o;
        return Objects.equals(epicName, subtask.epicName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicName);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicName='" + epicName + '\'' +
                '}';
    }
}
