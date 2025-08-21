package net.r4mble.types;

import com.google.gson.*;
import net.r4mble.SPWorldsAPI;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
            if (Objects.equals(card.getId(), this.id)) {
                this.name = card.getName();
                this.color = card.getColor();
                break;
            }
        }
    }

    public String getToken() {
        return token;
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

    public User getUser(){
        HttpResponse<String> response = makeRequest("accounts/me");
        return gson.fromJson(response.body(), User.class);
    }

    public HttpResponse<String> makeTransfer(String number, int amount, String comment) {
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
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    public HttpResponse<String> makeTransfer(String number, int amount) {
        return makeTransfer(number,amount, " ");
    }

    public Payment createPayment(Item[] items, String redirectUrl, String webhookUrl, String data) {
        JsonObject requestBody = new JsonObject();
        JsonArray itemsArray = new JsonArray();
        for (Item item : items) {
            itemsArray.add(gson.toJsonTree(item));
        }
        requestBody.add("items", itemsArray);
        requestBody.addProperty("redirectUrl", redirectUrl);
        requestBody.addProperty("webhookUrl", webhookUrl);
        requestBody.addProperty("data", data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "payments"))
                .header("Authorization", getAuthorizationHeader())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return gson.fromJson(response.body(), Payment.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse<String> makeRequest(String endpoint){
        return SPWorldsAPI.makeRequest(endpoint, id, token);
    }

    private String getAuthorizationHeader() {
        return SPWorldsAPI.getAuthorizationHeader(id, token);
    }
}
