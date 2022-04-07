import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTasksManager>{


    @BeforeEach
    @Override
    public void initializeManager() {
        manager = new FileBackedTasksManager(Managers.getDefaultHistory(), Path.of("txt.txt"));
    }

    @Test
    public void shouldSave(){
        manager.save();
    }
}
