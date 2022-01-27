package tasks;

import java.util.ArrayList;

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
            status = "NEW";
        } else {
            int quantityOfNew = 0;
            int quantityOfDone = 0;
            for (SubTask subTask : subtasks) {
                switch (subTask.getStatus()) {
                    case "NEW":
                        quantityOfNew++;
                        if (quantityOfDone > 0) {
                            status = "IN_PROGRESS";
                            return;
                        }
                        break;
                    case "DONE":
                        quantityOfDone++;
                        if (quantityOfNew > 0) {
                            status = "IN_PROGRESS";
                            return;
                        }
                        break;
                    default:
                        status = "IN_PROGRESS";
                        return;
                }
            }
            if (quantityOfNew == subtasks.size()) {
                status = "NEW";
            } else {
                status = "DONE";
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
