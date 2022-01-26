package tasks;

public class Task {
    // Поля задачи package-private для инкапсуляции
    String name;
    String description;
    int id;
    // status может принимать значения NEW, IN_PROGRESS, DONE
    String status;

    // Конструктор общий
    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    // Конструктор задачи со статусом
    public Task(String name, String description, int id, String status) {
        this(name, description, id);
        this.status = status;
    }
    @Override
    public String toString() {
        return "tasks.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }
}

