package org.example;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class WarframeMarketAPI {

    public List<Item> getItems() throws IOException {
        String urlString = "https://api.warframe.market/v1/items";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            System.out.println("Failed to fetch items. HTTP Status code: " + conn.getResponseCode());
            return new ArrayList<>();
        }

        Scanner scanner = new Scanner(url.openStream());
        StringBuilder inline = new StringBuilder();
        while (scanner.hasNext()) {
            inline.append(scanner.nextLine());
        }
        scanner.close();

        JSONObject data = new JSONObject(inline.toString());
        JSONArray itemsArray = data.getJSONObject("payload").getJSONArray("items");
        List<Item> items = new ArrayList<>();

        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemObj = itemsArray.getJSONObject(i);
            items.add(new Item(itemObj.getString("item_name"), itemObj.getString("url_name")));
        }

        return items;
    }

    public List<Order> getOrders(String itemUrlName) throws IOException {
        String urlString = "https://api.warframe.market/v1/items/" + itemUrlName + "/orders";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            System.out.println("Failed to fetch orders for " + itemUrlName + ". HTTP Status code: " + conn.getResponseCode());
            return new ArrayList<>();
        }

        Scanner scanner = new Scanner(url.openStream());
        StringBuilder inline = new StringBuilder();
        while (scanner.hasNext()) {
            inline.append(scanner.nextLine());
        }
        scanner.close();

        JSONObject data = new JSONObject(inline.toString());
        JSONArray ordersArray = data.getJSONObject("payload").getJSONArray("orders");
        List<Order> orders = new ArrayList<>();

        for (int i = 0; i < ordersArray.length(); i++) {
            JSONObject orderObj = ordersArray.getJSONObject(i);
            orders.add(new Order(
                    orderObj.getString("order_type"),
                    orderObj.getJSONObject("user").getString("ingame_name"),
                    orderObj.getInt("platinum"),
                    orderObj.getInt("mod_rank")
            ));
        }

        return orders;
    }
}
