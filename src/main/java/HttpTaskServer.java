import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import com.google.gson.Gson;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

public class HttpTaskServer {
    private static final int PORT = 8081;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static Gson gson = new Gson();

    private static HistoryManager historyManager = Managers.getDefaultHistory();
    //    private static TaskManager taskManager = Managers.getDefault(historyManager);
    private static TaskManager taskManager
            = new FileBackedTasksManager(historyManager, Path.of("taskManagerData.csv"));
    private static HttpServer httpServer;

    static {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TasksHandler());

//            startTaskServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        startTaskServer();
    }

    private static void startTaskServer() {
        httpServer.start();
        System.out.println("Server started on " + PORT);
    }

    private static void stopTaskServer() {
        httpServer.stop(0);
        System.out.println("Server stopped on " + PORT);
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
//            String[] splittedPath = path.split("/");

            int id = 0;
            if (uriQuery != null && !uriQuery.isBlank()) {
                endpoint = endpoint + "?" + Integer.parseInt(uriQuery.split("=")[0]);
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

            response = handleEndpoint(response, endpoint, id, requestBody);
            giveResponse(httpExchange, response, statusCode);
//  GET /tasks/task/                    Collection<Task> getRegularTasks();
//  GET /tasks/task/?id=                Task getSavedTaskByIdAndAffectHistory(int id);
//  POST/tasks/task/ Body:{task ..}     void makeTask(Task task); void updateTask(Task task);
//
//  DELETE/tasks/task/?id=              void deleteTaskById(int id);
//  DELETE/tasks/task                   void deleteAllRegularTasks();
//
//  GET /tasks/subtask/                 Collection<SubTask> getSubTasks();
//  DELETE/tasks/subtask                void deleteAllSubTasks();
//
//  GET /tasks/epic/                    Collection<EpicTask> getEpicTasks();
//  DELETE/tasks/epic                   void deleteAllEpicTasks();
//
//  GET /tasks/subtask/epic/?id=        Collection<SubTask> getSubTasksFromEpic(int epicId);
//  GET /tasks/history
//  GET /tasks/                         Set<Task> getPrioritizedTasks();



/*
* Сначала добавьте в проект библиотеку Gson для работы с JSON. Далее создайте класс HttpTaskServer,
*  который будет слушать порт 8080 и принимать запросы. Добавьте в него реализациюBackedFileTaskManager,
*  которую можно получить из утилитного класса Managers. После этого можно
*  реализовать маппинг запросов на методы интерфейса TaskManager.
*
API должен работать так, чтобы все запросы по пути /tasks/<ресурсы> приходили в интерфейс TaskManager.
*  Путь для обычных задач — /tasks/task, для подзадач — /tasks/subtask, для эпиков — /tasks/epic.
*  Получить все задачи сразу можно будет по пути /tasks/, а получить историю задач по пути /tasks/history.

* Для получения данных должны быть GET-запросы. Для создания и изменения — POST-запросы.
*  Для удаления — DELETE-запросы. Задачи передаются в теле запроса в формате JSON.
*  Идентификатор (id) задачи следует передавать параметром запроса (через вопросительный знак).

* В результате для каждого метода интерфейса TaskManager должен быть создан отдельный эндпоинт,
*  который можно будет вызвать по HTTP.
*
*
* */
        }

        private String handleEndpoint(String response, String endpoint, int id, String requestBody) {
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
                    response = gson.toJson(historyManager.getHistory());
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
            }
            return response;
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
