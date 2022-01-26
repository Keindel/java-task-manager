package tasks;

import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<SubTask> subtasks;
    private String status;

    // Конструктор эпиков
    public EpicTask(Task task) {
        super(task.name, task.description, task.id);
        this.subtasks = new ArrayList<>();
    }

    // Метод для добавления ссылок на SubTask'и в поле Epic'а со списком
    public void putSubTask(SubTask subTask) {
        this.subtasks.add(subTask);
    }

    // Getter списка SubTask'ов в Epic'е
    public ArrayList<SubTask> getThisEpicSubTasks() {
        return this.subtasks;
    }

    // Метод очистки списка ссылок на SubTask'и в Epic'е
    public void clearEpicSublist(){
        this.subtasks.clear();
    }

    // Метод удаления SubTask'и из Epic'a
    public void removeSubTaskFromEpic(SubTask subTask){
        this.subtasks.remove(subTask);
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
                        break;
                    case "DONE":
                        quantityOfDone++;
                        break;
                    default:
                        status = "IN_PROGRESS";
                        return;
                }
                if (quantityOfNew == subtasks.size()){
                    status = "NEW";
                } else if (quantityOfDone == subtasks.size()){
                    status = "DONE";
                }
            }
        }
    }

    /*
    // Getter списка id SubTask'ов в Epic'е
    public ArrayList<Integer> getSubTasksIds(){
        return subtasks;
    }*/
    }
