package net.r4mble;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.r4mble.types.Card;
import net.r4mble.types.Player;
import net.r4mble.types.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.Objects;

import static net.r4mble.Constants.BASE_URL;

public class SPWorldsClient extends Card {
    private final String token;

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public SPWorldsClient(String id, String token) {
        this.id = id;
        this.token = token;
        Card[] cards = getUser().getCards();
        for (Card card : cards) {
            if (Objects.equals(card.getId(), this.id)) {
                this.name = card.getName();
                this.color = card.getColor();
                break;
            }
        }
    }

    public int getAuthStatus() {
        HttpResponse<String> response = makeRequest("card");
        return response != null ? response.statusCode() : 503;
    }

    public int getBalance() {
        HttpResponse<String> response = makeRequest("card");
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        return json.get("balance").getAsInt();
    }

    public String getWebhook() {
        HttpResponse<String> response = makeRequest("card");
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        if(json.get("webhook").isJsonNull()){
            return  null;
        } else {
            return json.get("webhook").getAsString();
        }
    }

    public Player getPlayerByDiscordId(String discordId){
        HttpResponse<String> response = makeRequest("users/" + discordId);
        return gson.fromJson(response.body(), Player.class);
    }

    public Card[] getCardsByPlayerName(String playerName){
        HttpResponse<String> response = makeRequest("accounts/"+ playerName +"/cards");
        return gson.fromJson(response.body(), Card[].class);
    }

    public User getUser(){
        HttpResponse<String> response = makeRequest("accounts/me");
        return gson.fromJson(response.body(), User.class);
    }

    public boolean createTransfer(String number, int amount, String comment) {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("receiver", number);
            requestBody.addProperty("amount", amount);
            requestBody.addProperty("comment", comment);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "transactions"))
                    .header("Authorization", getAuthorizationHeader())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return true;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public boolean createTransfer(String number, int amount) {
        return createTransfer(number,amount, " ");
    }

    private HttpResponse<String> makeRequest(String endpoint){
        HttpRequest  request =  HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", getAuthorizationHeader())
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

    private String getAuthorizationHeader() {
        return "Bearer " + this.getBase64Key();
    }

    private String getBase64Key() {
        return Base64.getEncoder().encodeToString((id + ":" + token).getBytes());
    }

}
