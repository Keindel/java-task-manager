package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class EpicTask extends Task {
    private ArrayList<SubTask> subtasks;
    private LocalDateTime endTime;
    private final TaskTypes taskType = TaskTypes.EPIC;

    // Конструктор эпиков
    public EpicTask(Task task) {
        super(task.name, task.description, task.id);
        subtasks = new ArrayList<>();
        // Обновление статуса эпика
        updateEpic();
    }

    // Метод для добавления ссылок на SubTask'и в поле Epic'а со списком
    public void putSubTask(SubTask subTask) {
        subtasks.add(subTask);
        // Обновление статуса эпика
        updateEpic();
    }

    // Getter списка SubTask'ов в Epic'е
    public ArrayList<SubTask> getThisEpicSubTasks() {
        return subtasks;
    }

    // Метод очистки списка ссылок на SubTask'и в Epic'е
    public void clearEpicSublist() {
        subtasks.clear();
        // Обновление статуса эпика
        updateEpic();
    }

    // Метод удаления SubTask'и из Epic'a
    public void removeSubTaskFromEpic(SubTask subTask) {
        subtasks.remove(subTask);
        // Обновление статуса эпика
        updateEpic();
    }

    // Метод обновления статуса Epic'a
    private void updateEpic() {
        updateStatus();
        updateTimeAndDuration();
    }

    private void updateStatus() {
        if (subtasks.size() == 0) {
            status = Status.NEW;
        } else {
            HashSet<Status> uniqueStatuses = new HashSet<>();
            for (SubTask subTask : subtasks) {
                uniqueStatuses.add(subTask.getStatus());
            }
            if (uniqueStatuses.size() == 1) {
                Iterator<Status> i = uniqueStatuses.iterator();
                status = i.next();
            } else {
                status = Status.IN_PROGRESS;
            }
        }
    }

    private void updateTimeAndDuration() {
        if (subtasks.size() == 0) {
            startTime = LocalDateTime.MAX;
            duration = Duration.ZERO;
            endTime = startTime.plusDays(duration.toDays());
        } else {
            startTime = subtasks.stream()
                    .map(x -> x.startTime)
                    .min(LocalDateTime::compareTo)
                    .get();
            endTime = subtasks.stream()
                    .map(Task::getEndTime)
                    .max(LocalDateTime::compareTo)
                    .get();
            duration = Duration.ofMinutes(subtasks.stream()
                    .map(x -> x.duration.toMinutes())
                    .reduce(Long::sum)
                    .get());
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
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

    public static Task fromString(String value) {
        String[] taskFields = value.split(",");
        int id = Integer.parseInt(taskFields[0]);
        String name = taskFields[2];
        Status status = Status.valueOf(taskFields[3]);
        String description = taskFields[4];

        return new EpicTask(new Task(name, description, id, status));
    }
}
