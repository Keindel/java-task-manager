package tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class EpicTask extends Task {
    private ArrayList<SubTask> subtasks;

    // Конструктор эпиков
    public EpicTask(Task task) {
        super(task.name, task.description, task.id);
        subtasks = new ArrayList<>();
        // Обновление статуса эпика
        updateEpicStatus();
    }

    // Метод для добавления ссылок на SubTask'и в поле Epic'а со списком
    public void putSubTask(SubTask subTask) {
        subtasks.add(subTask);
        // Обновление статуса эпика
        updateEpicStatus();
    }

    // Getter списка SubTask'ов в Epic'е
    public ArrayList<SubTask> getThisEpicSubTasks() {
        return subtasks;
    }

    // Метод очистки списка ссылок на SubTask'и в Epic'е
    public void clearEpicSublist() {
        subtasks.clear();
        // Обновление статуса эпика
        updateEpicStatus();
    }

    // Метод удаления SubTask'и из Epic'a
    public void removeSubTaskFromEpic(SubTask subTask) {
        subtasks.remove(subTask);
        // Обновление статуса эпика
        updateEpicStatus();
    }

    // Метод обновления статуса Epic'a
    public void updateEpicStatus() {
        if (subtasks.size() == 0) {
            status = Status.NEW;
        } else {
            HashSet<Status> uniqueStatuses = new HashSet();
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

    @Override
    public String toString() {
        return "EpicTask{" +
                "subtasks=" + subtasks +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
