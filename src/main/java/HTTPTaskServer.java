import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import gsonAdapters.DurationAdapter;
import gsonAdapters.LocalDateTimeAdapter;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

public class HTTPTaskServer {
    private static final int PORT = 8081;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    private static HistoryManager historyManager = Managers.getDefaultHistory();
    private static TaskManager taskManager = Managers.getDefault(historyManager, "http://localhost:" + KVServer.PORT);
    private static HttpServer httpServer;

    public static TaskManager getTaskManager() {
        return taskManager;
    }

    static {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TasksHandler());

        } catch (IOException e) {
            throw new RuntimeException();
        }
    }


    public static void main(String[] args) throws IOException {
        startTaskServer();
        taskManager = FileBackedTasksManager.loadFromFile(Path.of("taskManagerData.csv"));
        FileBackedTasksManager fileBackedTasksManager = (FileBackedTasksManager) taskManager;
        historyManager = fileBackedTasksManager.getHistoryManager();
    }

    public static void startTaskServer() {
        httpServer.start();
        System.out.println("TaskServer started on " + PORT);
    }

    public static void stopTaskServer() {
        httpServer.stop(0);
        System.out.println("TaskServer stopped on " + PORT);
    }

    static class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "";
            int statusCode = 501;

            String requestMethod = httpExchange.getRequestMethod();
            URI requestUri = httpExchange.getRequestURI();
            String path = requestUri.getPath();
            String uriQuery = requestUri.getQuery();

            String endpoint = requestMethod + path;

            int id = 0;
            if (uriQuery != null && !uriQuery.isBlank()) {
                endpoint = endpoint + "?" + uriQuery.split("=")[0];
                id = Integer.parseInt(uriQuery.split("=")[1]);
            }
            if (endpoint.endsWith("/")) {
                endpoint = endpoint.substring(0, endpoint.length() - 1);
            }

            InputStream inputStream;
            String requestBody = "";
            if (requestMethod.equals("POST")) {
                inputStream = httpExchange.getRequestBody();
                requestBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            }
            statusCode = defineStatusCode(requestMethod);

            switch (endpoint) {
                case "GET/tasks/task":
                    response = gson.toJson(taskManager.getRegularTasks());
                    break;
                case "GET/tasks/task/?id":
                    response = gson.toJson(taskManager.getSavedTaskByIdAndAffectHistory(id));
                    break;
                case "GET/tasks/epic":
                    response = gson.toJson(taskManager.getEpicTasks());
                    break;
                case "GET/tasks/subtask":
                    response = gson.toJson(taskManager.getSubTasks());
                    break;
                case "GET/tasks":
                    response = gson.toJson(taskManager.getPrioritizedTasks());
                    break;
                case "GET/tasks/history":
                    response = gson.toJson(InMemoryHistoryManager.toStringOfIds(historyManager));
                    break;
                case "GET/tasks/subtask/epic/?id":
                    response = gson.toJson(taskManager.getSubTasksFromEpic(id));
                    break;

                case "POST/tasks/task":
                    JsonElement jsonElement = JsonParser.parseString(requestBody);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    String taskType = jsonObject.get("taskType").getAsString().toLowerCase();
                    switch (taskType) {
                        case "task":
                            taskManager.updateTask(gson.fromJson(requestBody, Task.class));
                            break;
                        case "epic":
                            taskManager.updateTask(gson.fromJson(requestBody, EpicTask.class));
                            break;
                        case "subtask":
                            taskManager.updateTask(gson.fromJson(requestBody, SubTask.class));
                            break;
                        default:
                            throw new IllegalArgumentException("taskType is not defined");
                    }
                    break;

                case "DELETE/tasks/task/?id":
                    taskManager.deleteTaskById(id);
                    break;
                case "DELETE/tasks/task":
                    taskManager.deleteAllRegularTasks();
                    break;
                case "DELETE/tasks/epic":
                    taskManager.deleteAllEpicTasks();
                    break;
                case "DELETE/tasks/subtask":
                    taskManager.deleteAllSubTasks();
                    break;
                default:
                    statusCode = 400;
            }

            giveResponse(httpExchange, response, statusCode);
        }

        private int defineStatusCode(String requestMethod) {
            int statusCode;
            switch (requestMethod) {
                case "GET":
                    statusCode = 200;
                    break;
                case "POST":
                    statusCode = 201;
                    break;
                case "DELETE":
                    statusCode = 204;
                    break;
                default:
                    statusCode = 400;
            }
            return statusCode;
        }

        private void giveResponse(HttpExchange httpExchange, String response, int statusCode) throws IOException {
            httpExchange.sendResponseHeaders(statusCode, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
