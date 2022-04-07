package tasks;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EpicTaskTest {
    private static EpicTask epicTask;

    @BeforeAll
    public static void beforeAll() {
        epicTask = new EpicTask(new Task("epic name", "epic descr", 1));
    }

    @BeforeEach
    public void beforeEach() {
        //epicTask = new EpicTask(new Task("epic name", "epic descr", 1));
    }

    @DisplayName("GIVEN Epic "
            + "WHEN put list of subTasks "
            + "THEN return epic status based on subtasks statuses")
    @MethodSource("test1MethodSource")
    @ParameterizedTest(name = "{index} epic with status = {1}")
    void test1_shouldUpdateEpicStatusFromSubtasksStatuses(List<SubTask> subTasks, Status status) {
        //Given
        epicTask = new EpicTask(new Task("epic name", "epic descr", 1));
        //When
        for (SubTask subTask : subTasks) {
            epicTask.putSubTask(subTask);
        }
        //Then
        assertEquals(status, epicTask.getStatus());
    }

    private Stream<Arguments> test1MethodSource() {
        return Stream.of(
                Arguments.of(List.of(), Status.NEW)
                , Arguments.of(generateSubTasksListWithStatus(Status.NEW), Status.NEW)
                , Arguments.of(generateSubTasksListWithStatus(Status.DONE), Status.DONE)
                , Arguments.of(List.of(new SubTask(new Task("name11", "descrip11", 0, Status.NEW), epicTask.id)
                                , new SubTask(new Task("name12", "descrip12", 0, Status.DONE), epicTask.id))
                        , Status.IN_PROGRESS)
                , Arguments.of(generateSubTasksListWithStatus(Status.IN_PROGRESS), Status.IN_PROGRESS));
    }

    private List<SubTask> generateSubTasksListWithStatus(Status status) {
        List<SubTask> subTasks = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            subTasks.add(new SubTask(new Task("name" + i, "descrip" + i
                    , epicTask.id + i, status), epicTask.id));
        }
        return subTasks;
    }
}