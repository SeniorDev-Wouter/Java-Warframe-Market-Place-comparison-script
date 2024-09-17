package org.example;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class WarframeMarket {
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public static List<JsonObject> getItems() throws IOException {
        String url = "https://api.warframe.market/v1/items";
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Failed to fetch items. HTTP Status code: " + response.code());
                return Collections.emptyList();
            }
            JsonObject data = gson.fromJson(response.body().string(), JsonObject.class);
            JsonArray items = data.getAsJsonObject("payload").getAsJsonArray("items");
            return gson.fromJson(items, new TypeToken<List<JsonObject>>() {}.getType());
        }
    }

    public static List<JsonObject> getOrders(String itemUrlName) throws IOException {
        String url = "https://api.warframe.market/v1/items/" + itemUrlName + "/orders";
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Failed to fetch orders for " + itemUrlName + ". HTTP Status code: " + response.code());
                return Collections.emptyList();
            }
            JsonObject data = gson.fromJson(response.body().string(), JsonObject.class);
            JsonArray orders = data.getAsJsonObject("payload").getAsJsonArray("orders");
            return gson.fromJson(orders, new TypeToken<List<JsonObject>>() {}.getType());
        }
    }
}
