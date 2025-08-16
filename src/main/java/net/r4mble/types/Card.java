package net.r4mble.types;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static net.r4mble.Constants.BASE_URL;

public class Card {
    private final String id;
    private final String token;
    private String name;

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public Card(String id, String token) {
        this.id = id;
        this.token = token;
    }

    public Card(String id, String token, String name) {
        this.id = id;
        this.token = token;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "card"))
                .header("Authorization", getAuthorizationHeader())
                .timeout(Duration.ofSeconds(5))
                .build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            return -1;
        }
        if (response.statusCode() != 200) {
            return -1;
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        return json.get("balance").getAsInt();
    }

    public int getAuthStatus() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "card"))
                .header("Authorization", getAuthorizationHeader())
                .timeout(Duration.ofSeconds(5))
                .build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            return 503;
        }
        return response.statusCode();
    }

    private String getAuthorizationHeader() {
        return "Bearer " + this.getBase64Key();
    }

    private String getBase64Key() {
        return java.util.Base64.getEncoder().encodeToString((id + ":" + token).getBytes());
    }
}
