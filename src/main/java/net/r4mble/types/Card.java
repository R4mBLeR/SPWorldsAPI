package net.r4mble.types;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;

import static net.r4mble.Constants.BASE_URL;

public class Card extends BaseCard {
    private final String token;

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public Card(String id, String token) {
        this.id = id;
        this.token = token;
        BaseCard[] cards = getUser().getCards();
        for (BaseCard card : cards) {
            if (Objects.equals(card.id, this.id)) {
                this.name = card.name;
                this.color = card.color;
                break;
            }
        }
    }

    public int getAuthStatus() {
        HttpRequest  request =  HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "card"))
                .header("Authorization", getAuthorizationHeader())
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e){
            return 503;
        }
        return response.statusCode();
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

    public BaseCard[] getCardsByUsername(String username){
        HttpResponse<String> response = makeRequest("accounts/"+ username +"/cards");
        return gson.fromJson(response.body(), BaseCard[].class);
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
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
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
        return java.util.Base64.getEncoder().encodeToString((id + ":" + token).getBytes());
    }



}
