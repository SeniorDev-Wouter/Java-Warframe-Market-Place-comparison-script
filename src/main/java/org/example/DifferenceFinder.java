package org.example;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.tongfei.progressbar.ProgressBar;

public class DifferenceFinder {
    private static final String FILE_PATH = "differences.txt";
    private static final Gson gson = new Gson();

    public void findBiggestDifference(String numItems, String filterString) throws IOException, InterruptedException {
        List<JsonObject> items = WarframeMarket.getItems();
        if (items.isEmpty()) {
            return;
        }

        List<JsonObject> filteredItems = new ArrayList<>();
        for (JsonObject item : items) {
            if (item.get("item_name").getAsString().toLowerCase().contains(filterString.toLowerCase())) {
                filteredItems.add(item);
            }
        }

        List<JsonObject> selectedItems;
        if (numItems.equals("all")) {
            selectedItems = filteredItems;
        } else {
            int num = Integer.parseInt(numItems);
            Collections.shuffle(filteredItems);
            selectedItems = filteredItems.subList(0, Math.min(num, filteredItems.size()));
        }

        System.out.println("Processing " + selectedItems.size() + " items out of " + filteredItems.size() + " filtered items.");

        List<Difference> differences = new ArrayList<>();

        try (ProgressBar pb = new ProgressBar("Processing items", selectedItems.size())) {
            for (JsonObject item : selectedItems) {
                String itemUrlName = item.get("url_name").getAsString();
                List<JsonObject> orders = WarframeMarket.getOrders(itemUrlName);

                Map<Integer, RankOrders> ordersByRank = new HashMap<>();
                for (JsonObject order : orders) {
                    int rank = order.has("mod_rank") ? order.get("mod_rank").getAsInt() : 0;
                    ordersByRank.putIfAbsent(rank, new RankOrders());
                    if (order.get("order_type").getAsString().equals("buy") && order.getAsJsonObject("user").get("status").getAsString().equals("ingame")) {
                        ordersByRank.get(rank).buy.add(order);
                    } else if (order.get("order_type").getAsString().equals("sell") && order.getAsJsonObject("user").get("status").getAsString().equals("ingame")) {
                        ordersByRank.get(rank).sell.add(order);
                    }
                }

                for (Map.Entry<Integer, RankOrders> entry : ordersByRank.entrySet()) {
                    int rank = entry.getKey();
                    RankOrders rankOrders = entry.getValue();
                    if (rankOrders.buy.isEmpty() || rankOrders.sell.isEmpty()) {
                        continue;
                    }

                    JsonObject highestBuyOrder = Collections.max(rankOrders.buy, Comparator.comparing(o -> o.get("platinum").getAsInt()));
                    JsonObject lowestSellOrder = Collections.min(rankOrders.sell, Comparator.comparing(o -> o.get("platinum").getAsInt()));

                    int difference = highestBuyOrder.get("platinum").getAsInt() - lowestSellOrder.get("platinum").getAsInt();
                    differences.add(new Difference(difference, item.get("item_name").getAsString(), highestBuyOrder.getAsJsonObject("user").get("ingame_name").getAsString(), lowestSellOrder.getAsJsonObject("user").get("ingame_name").getAsString(), rank));
                }

                TimeUnit.MILLISECONDS.sleep(250);
                pb.step();
            }
        }

        List<Difference> currentDifferences = readDifferencesFromFile();
        differences.addAll(currentDifferences);

        differences.sort(Comparator.comparingInt(d -> -d.difference));
        List<Difference> top10 = differences.subList(0, Math.min(10, differences.size()));

        writeDifferencesToFile(top10);

        for (Difference diff : top10) {
            System.out.println("Item: " + diff.item + " (Rank: " + diff.rank + ")");
            System.out.println("Highest buy order by: " + diff.buyer);
            System.out.println("Lowest sell order by: " + diff.seller);
            System.out.println("Difference: " + diff.difference + " platinum\n");
        }
    }

    public void checkActiveOrders() throws IOException {
        System.out.println("checking active orders up to dateness");
        List<Difference> currentDifferences = readDifferencesFromFile();
        List<Difference> activeDifferences = new ArrayList<>();
        try (ProgressBar pb = new ProgressBar("Checking orders", currentDifferences.size())) {
            for (Difference diff : currentDifferences) {
                List<JsonObject> orders = WarframeMarket.getOrders(diff.item.replaceAll("\\s", "_").toLowerCase().replaceAll("&", "and"));
                boolean isActive = false;

                for (JsonObject order : orders) {
                    String buyer = order.getAsJsonObject("user").get("ingame_name").getAsString();
                    String seller = order.getAsJsonObject("user").get("ingame_name").getAsString();
                    if (buyer.equals(diff.buyer) || seller.equals(diff.seller)) {
                        isActive = true;
                        break;
                    }
                }

                if (isActive) {
                    activeDifferences.add(diff);
                }
                pb.step();
            }
        }

        writeDifferencesToFile(activeDifferences);
    }

    private List<Difference> readDifferencesFromFile() {
        List<Difference> differences = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                differences.add(gson.fromJson(line, Difference.class));
            }
        } catch (IOException e) {
            System.out.println("Failed to read differences from file: " + e.getMessage());
        }
        return differences;
    }

    private void writeDifferencesToFile(List<Difference> differences) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Difference diff : differences) {
                writer.write(gson.toJson(diff));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Failed to write differences to file: " + e.getMessage());
        }
    }
}
