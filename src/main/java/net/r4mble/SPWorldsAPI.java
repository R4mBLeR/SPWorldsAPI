package net.r4mble;

import com.google.gson.Gson;
import net.r4mble.types.BaseCard;
import net.r4mble.types.Player;
import net.r4mble.types.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

import static net.r4mble.Constants.BASE_URL;

public class SPWorldsAPI {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    private final String id;
    private final String token;

    public SPWorldsAPI(String id, String token){
        this.id = id;
        this.token = token;
    }

    public int getAuthStatus() {
        HttpResponse<String> response = makeRequest("card");
        return response != null ? response.statusCode() : 503;
    }

    public static int getAuthStatus(String id, String token) {
        HttpResponse<String> response = makeRequest("card", id, token);
        return response != null ? response.statusCode() : 503;
    }

    public Player getPlayerByDiscordId(String discordId, String id, String token){
        HttpResponse<String> response = makeRequest("users/" + discordId);
        return gson.fromJson(response.body(), Player.class);
    }

    public BaseCard[] getCardsByPlayerName(String playerName){
        HttpResponse<String> response = makeRequest("accounts/"+ playerName +"/cards");
        return gson.fromJson(response.body(), BaseCard[].class);
    }

    public User getUser(){
        HttpResponse<String> response = makeRequest("accounts/me");
        return gson.fromJson(response.body(), User.class);
    }

    public HttpResponse<String> makeRequest(String endpoint){
        return makeRequest(endpoint, id, token);
    }

    public static HttpResponse<String> makeRequest(String endpoint, String id, String token){
        HttpRequest request =  HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", getAuthorizationHeader(id, token))
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return null;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public static String getAuthorizationHeader(String id, String token) {
        return "Bearer " + Base64.getEncoder().encodeToString((id + ":" + token).getBytes());
    }
}
