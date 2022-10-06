import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client;
    private String API_KEY;
    private String url;
    private static final Gson gson = new Gson();

    public KVTaskClient(String url) {
        this.client = HttpClient.newHttpClient();
        this.url = url;
        receiveAPI_KEY(URI.create(url + "/register"));
    }

    private void receiveAPI_KEY(URI uriRegister) {
        HttpRequest registerRequest = HttpRequest.newBuilder()
                .uri(uriRegister)
                .GET()
                .build();
        try {
            HttpResponse<String> httpResponse = client.send(registerRequest
                    , HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() == 200) {
                API_KEY = gson.fromJson(httpResponse.body(), String.class);
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: "
                        + httpResponse.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса KVTaskClient.receiveAPI_KEY() возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public void put(String key, String json) {
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "/save/" + key + "?API_KEY=" + API_KEY))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            final HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String load(String key) {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "/load/" + key + "?API_KEY=" + API_KEY))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        String stringResponse = "";
        try {
            final HttpResponse<String> httpResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() == 200) {
                stringResponse = httpResponse.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + httpResponse.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return stringResponse;
    }
}
