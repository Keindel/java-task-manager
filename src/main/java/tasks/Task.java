package tasks;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    // Поля задачи protected для инкапсуляции
    protected String name;
    protected String description;
    protected int id;
    // status может принимать значения NEW, IN_PROGRESS, DONE
    protected Status status;
    protected Duration duration;
    protected LocalDate startTime;
    private final TaskTypes taskType = TaskTypes.TASK;

    // Базовый конструктор для новых задач, без id ()
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.id = 0;
        this.duration = Duration.ofDays(1);
        this.startTime = LocalDate.now();
    }

    // Конструктор с id
    public Task(String name, String description, int id) {
        this(name, description);
        this.id = id;
    }

    // Конструктор задачи с id и статусом
    public Task(String name, String description, int id, Status status) {
        this(name, description, id);
        this.status = status;
    }

    // Конструктор задачи с временем старта и длительностью
    public Task(Task task, String startTime, long durationInDays) {
        this.duration = Duration.ofDays(durationInDays);
        this.startTime = LocalDate.from(getDateTimeFormatter().parse(startTime));
    }

    public static Task fromString(String value) {
        String[] taskFields = value.split(",");
        int id = Integer.parseInt(taskFields[0]);
        String name = taskFields[2];
        Status status = Status.valueOf(taskFields[3]);
        String description = taskFields[4];
        if (taskFields[5].isBlank()) {
            return new Task(name, description, id, status);
        }
        String startTime = taskFields[5];
        long duration = Long.parseLong(taskFields[6]);

        return new Task(new Task(name, description, id, status)
                , startTime, duration);
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy/MM/dd");
    }

    public LocalDate getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return String.join(","
                , String.valueOf(id)
                , taskType.toString()
                , name
                , status.toString()
                , description
                , startTime.format(getDateTimeFormatter())
                , String.valueOf(duration.toDays())
                , getEndTime().format(getDateTimeFormatter()));
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
