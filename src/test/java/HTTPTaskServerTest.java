import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gsonAdapters.DurationAdapter;
import gsonAdapters.LocalDateTimeAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HTTPTaskServerTest {
    private KVServer kvServer;
    private HTTPTaskServer httpTaskServer;
    private TaskManager manager;

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();


    private HttpClient client;


    @BeforeEach
    public void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HTTPTaskServer();
        HTTPTaskServer.startTaskServer();
        manager = HTTPTaskServer.getTaskManager();

        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void afterEach() {
        kvServer.stop();
        HTTPTaskServer.stopTaskServer();
    }

    @Test
    public void shouldHandleRequests() throws IOException, InterruptedException {
        Task task1 = new Task("name1", "descr1", 1, Status.NEW, "22.04.22 09:14", 17);
        Task task2 = new Task("name2", "descr2", 2, Status.NEW, "22.04.22 11:14", 34);
        EpicTask epicTask3 = new EpicTask(new Task("name3", "descr3", 3));
        SubTask subTask4 = new SubTask(new Task("name4", "descr4", 4, Status.NEW, "22.04.22 13:14", 47), 3);
        SubTask subTask5 = new SubTask(new Task("name5", "descr5", 5, Status.NEW, "22.04.22 15:14", 71), 3);
        EpicTask epicTask6 = new EpicTask(new Task("name6", "descr6", 6));
        SubTask subTask7 = new SubTask(new Task("name7", "descr7", 7, Status.NEW, "22.05.22 06:14", 189), 6);

        List<Task> allTasks = List.of(task1, task2, epicTask3, subTask4, subTask5, epicTask6, subTask7);

        for (Task task : allTasks) {
            HttpResponse<String> httpResponse = client.send(getPOSTRequest(task)
                    , HttpResponse.BodyHandlers.ofString());
            assertEquals(201, httpResponse.statusCode());
        }

        // GET id requests
        String pathAndQuery = "/tasks/task/?id=1";
        HttpResponse<String> httpResponse = client.send(getGETRequest(pathAndQuery)
                , HttpResponse.BodyHandlers.ofString());
        assertEquals(200, httpResponse.statusCode());
        assertEquals(gson.toJson(manager.getSavedTaskByIdAndAffectHistory(1)), httpResponse.body());
        pathAndQuery = "/tasks/task/?id=6";
        httpResponse = client.send(getGETRequest(pathAndQuery)
                , HttpResponse.BodyHandlers.ofString());
        assertEquals(200, httpResponse.statusCode());
        assertEquals(gson.toJson(manager.getSavedTaskByIdAndAffectHistory(6)), httpResponse.body());
        pathAndQuery = "/tasks/task/?id=7";
        httpResponse = client.send(getGETRequest(pathAndQuery)
                , HttpResponse.BodyHandlers.ofString());
        assertEquals(200, httpResponse.statusCode());
        assertEquals(gson.toJson(manager.getSavedTaskByIdAndAffectHistory(7)), httpResponse.body());

        // GET regularTasks
        pathAndQuery = "/tasks/task/";
        httpResponse = client.send(getGETRequest(pathAndQuery)
                , HttpResponse.BodyHandlers.ofString());
        assertEquals(200, httpResponse.statusCode());
        assertEquals(gson.toJson(manager.getRegularTasks()), httpResponse.body());

        // GET epicTasks
        pathAndQuery = "/tasks/epic/";
        httpResponse = client.send(getGETRequest(pathAndQuery)
                , HttpResponse.BodyHandlers.ofString());
        assertEquals(200, httpResponse.statusCode());
        assertEquals(gson.toJson(manager.getEpicTasks()), httpResponse.body());

        // GET subtasks
        pathAndQuery = "/tasks/subtask/";
        httpResponse = client.send(getGETRequest(pathAndQuery)
                , HttpResponse.BodyHandlers.ofString());
        assertEquals(200, httpResponse.statusCode());
        assertEquals(gson.toJson(manager.getSubTasks()), httpResponse.body());

        // GET prioritizedTasks
        pathAndQuery = "/tasks/";
        httpResponse = client.send(getGETRequest(pathAndQuery)
                , HttpResponse.BodyHandlers.ofString());
        assertEquals(200, httpResponse.statusCode());
        assertEquals(gson.toJson(manager.getPrioritizedTasks()), httpResponse.body());

        // GET history
        pathAndQuery = "/tasks/history";
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) manager;
        httpResponse = client.send(getGETRequest(pathAndQuery)
                , HttpResponse.BodyHandlers.ofString());
        assertEquals(200, httpResponse.statusCode());
        assertEquals(gson.toJson(InMemoryHistoryManager
                .toStringOfIds(inMemoryTaskManager.getHistoryManager())), httpResponse.body());

        // GET subtasksFromEpic
        pathAndQuery = "/tasks/subtask/epic/?id=3";
        httpResponse = client.send(getGETRequest(pathAndQuery)
                , HttpResponse.BodyHandlers.ofString());
        assertEquals(200, httpResponse.statusCode());

        // DELETE taskById
        pathAndQuery = "/tasks/task/?id=3";
        httpResponse = client.send(getDELETERequest(pathAndQuery)
                , HttpResponse.BodyHandlers.ofString());
        assertEquals(204, httpResponse.statusCode());
        assertNull(manager.getSavedTaskByIdAndAffectHistory(3));

        // DELETE allRegularTasks
        pathAndQuery = "/tasks/task";
        httpResponse = client.send(getDELETERequest(pathAndQuery)
                , HttpResponse.BodyHandlers.ofString());
        assertEquals(204, httpResponse.statusCode());
        assertEquals(manager.getRegularTasks(), Collections.EMPTY_LIST);

        // DELETE allEpics
        pathAndQuery = "/tasks/epic";
        httpResponse = client.send(getDELETERequest(pathAndQuery)
                , HttpResponse.BodyHandlers.ofString());
        assertEquals(204, httpResponse.statusCode());
        assertEquals(manager.getEpicTasks(), Collections.EMPTY_LIST);

        // DELETE allSubtasks
        pathAndQuery = "/tasks/subtask/";
        httpResponse = client.send(getDELETERequest(pathAndQuery)
                , HttpResponse.BodyHandlers.ofString());
        assertEquals(204, httpResponse.statusCode());
        assertEquals(manager.getSubTasks(), Collections.EMPTY_LIST);
    }

    private HttpRequest getDELETERequest(String pathAndQuery) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081" + pathAndQuery))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
    }

    private HttpRequest getGETRequest(String pathAndQuery) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081" + pathAndQuery))
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }

    private <T extends Task> HttpRequest getPOSTRequest(T task) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
    }
}

/*
*               case "GET/tasks/task":
                case "GET/tasks/task/?id":
                case "GET/tasks/epic":
                case "GET/tasks/subtask":
                case "GET/tasks":
                case "GET/tasks/history":
                case "GET/tasks/subtask/epic/?id":
                case "POST/tasks/task":
                    switch (taskType) {
                        case "task":
                        case "epic":
                        case "subtask":
                        default:
                            throw new IllegalArgumentException("taskType is not defined");
                case "DELETE/tasks/task/?id":
                case "DELETE/tasks/task":
                case "DELETE/tasks/epic":
                case "DELETE/tasks/subtask":
                default:
                    statusCode = 400;
* */

/*
* Напишите тесты для каждого эндпоинта HTTPTaskManager.
*  Чтобы каждый раз не добавлять запуск сервера, можно реализовать в классах с тестами отдельный метод.
*  Пометьте его аннотацией @BeforeAll — если предполагается запуск сервера для всех тестов
*  или аннотацией @BeforeEach — если для каждого теста требуется отдельный запуск.
*
*
* Подсказка: как остановить KVServer
Если запускать новый сервер перед каждым тестом на том же порту, то потребуется остановить предыдущий.
*  Для этого реализуйте метод stop() в KVServer. Его вызов поместите в отдельный метод в тестах.
*  Пометьте его аннотацией @AfterEach.
* */