package tasks;

import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<String> subtasks;

    public EpicTask(String name, String description, int id) {
        super(name, description, id);
        this.subtasks = new ArrayList<>();
    }

    public ArrayList<String> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<String> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public String toString() {
        return "tasks.EpicTask{" +
                "subtasks=" + subtasks.toString() +
                '}';
    }
}
