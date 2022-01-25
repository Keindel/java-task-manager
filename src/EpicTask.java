import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<String> subtasks;

    public EpicTask(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }
}
