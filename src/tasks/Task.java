package tasks;

import java.util.Objects;

public class Task {
    // Поля задачи protected для инкапсуляции
    protected String name;
    protected String description;
    protected int id;
    // status может принимать значения NEW, IN_PROGRESS, DONE
    protected Status status;

    // Конструктор общий для новых задач, без id ()
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.id = 0;
    }

    // Конструктор общий, без статуса (т.к. статус Epic'a определяется по входящим SubTask'ам)
    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    // Конструктор задачи со статусом
    public Task(String name, String description, int id, Status status) {
        this(name, description, id);
        this.status = status;
    }

    public static Task fromString(String value) {
        String[] taskFields = value.split(",");
        int id = Integer.parseInt(taskFields[0]);
        String name = taskFields[2];
        Status status = Status.valueOf(taskFields[3]);
        String description = taskFields[4];

        return new Task(name, description, id, status);
    }


    @Override
    public String toString() {
        return String.join(","
                , String.valueOf(id)
                , TaskTypes.TASK.toString()
                , name
                , status.toString()
                , description);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

