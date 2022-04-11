package tasks;

import java.time.*;
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
    protected LocalDateTime startTime;
    private final TaskTypes taskType = TaskTypes.TASK;

    public static final int MINUTES_DISCRETIZATION = 15;

    // Базовый конструктор для новых задач, без id ()
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.id = 0;
        this.duration = Duration.ZERO;
        this.startTime = LocalDateTime.MAX;
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
    public Task(String name, String description, int id, Status status, String startTime, long durationInMinutes) {
        this(name, description, id, status);
        if (durationInMinutes < 0) throw new IllegalArgumentException("duration must not be negative");
        this.duration = Duration.ofMinutes(roundUpByDiscretizator(durationInMinutes));
        this.startTime = LocalDateTime.from(getDateTimeFormatter().parse(startTime))
                .withMinute(0)
                .plusMinutes(roundUpByDiscretizator(LocalTime
                        .from(getDateTimeFormatter().parse(startTime)).getMinute()));
    }

    private long roundUpByDiscretizator(long minutes) {
        if (minutes % MINUTES_DISCRETIZATION > 0) {
            minutes = (minutes / MINUTES_DISCRETIZATION + 1) * (MINUTES_DISCRETIZATION);
        }
        return minutes;
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

        return new Task(name, description, id, status
                , startTime, duration);
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public long getStartInMinutes() {
        return startTime.toEpochSecond(ZoneOffset.UTC) / 60;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    public long getEndInMinutes() {
        return getEndTime().toEpochSecond(ZoneOffset.UTC) / 60;
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
                , String.valueOf(duration.toMinutes())
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
