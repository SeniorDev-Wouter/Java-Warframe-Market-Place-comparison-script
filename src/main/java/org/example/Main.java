package org.example;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        WarframeMarketAPI api = new WarframeMarketAPI();

        System.out.print("Enter the string that the item name should contain: ");
        String filterString = scanner.nextLine();

        System.out.println("Select the number of items to process:");
        System.out.println("1. All items");
        System.out.println("2. Specify the number of items");
        String choice = scanner.nextLine();

        int numItems = 0;
        if (choice.equals("2")) {
            System.out.print("Enter the number of items to process: ");
            numItems = Integer.parseInt(scanner.nextLine());
        }

        findBiggestDifference(api, numItems, filterString);
    }

    public static void findBiggestDifference(WarframeMarketAPI api, int numItems, String filterString) throws IOException {
        List<Item> items = api.getItems();
        if (items.isEmpty()) {
            return;
        }

        List<Item> filteredItems = items.stream()
                .filter(item -> item.getItemName().toLowerCase().contains(filterString.toLowerCase()))
                .collect(Collectors.toList());

        List<Item> selectedItems;
        if (numItems == 0) {
            selectedItems = filteredItems;
        } else {
            Collections.shuffle(filteredItems);
            selectedItems = filteredItems.subList(0, Math.min(numItems, filteredItems.size()));
        }

        System.out.println("Processing " + selectedItems.size() + " items out of " + filteredItems.size() + " filtered items.");

        List<Difference> differences = new ArrayList<>();

        for (Item item : selectedItems) {
            List<Order> orders = api.getOrders(item.getUrlName());

            Map<Integer, List<Order>> ordersByRank = orders.stream()
                    .collect(Collectors.groupingBy(Order::getModRank));

            for (Map.Entry<Integer, List<Order>> entry : ordersByRank.entrySet()) {
                int rank = entry.getKey();
                List<Order> rankOrders = entry.getValue();

                List<Order> buyOrders = rankOrders.stream()
                        .filter(order -> order.getOrderType().equals("buy"))
                        .collect(Collectors.toList());

                List<Order> sellOrders = rankOrders.stream()
                        .filter(order -> order.getOrderType().equals("sell"))
                        .collect(Collectors.toList());

                if (buyOrders.isEmpty() || sellOrders.isEmpty()) {
                    continue;
                }

                Order highestBuyOrder = Collections.max(buyOrders, Comparator.comparingInt(Order::getPlatinum));
                Order lowestSellOrder = Collections.min(sellOrders, Comparator.comparingInt(Order::getPlatinum));

                int difference = highestBuyOrder.getPlatinum() - lowestSellOrder.getPlatinum();
                differences.add(new Difference(difference, item, highestBuyOrder, lowestSellOrder, rank));
            }

            // Introduce a delay between requests
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        differences.sort(Comparator.comparingInt(Difference::getDifference).reversed());
        List<Difference> top10 = differences.stream().limit(10).collect(Collectors.toList());

        for (Difference diff : top10) {
            System.out.println("Item: " + diff.getItem().getItemName() + " (Rank: " + diff.getRank() + ")");
            System.out.println("Highest buy order: " + diff.getHighestBuyOrder().getPlatinum() + " platinum by " + diff.getHighestBuyOrder().getUserName());
            System.out.println("Lowest sell order: " + diff.getLowestSellOrder().getPlatinum() + " platinum by " + diff.getLowestSellOrder().getUserName());
            System.out.println("Difference: " + diff.getDifference() + " platinum\n");
        }
    }
}
