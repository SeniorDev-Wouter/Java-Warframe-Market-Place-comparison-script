package org.example;

public class Difference {
    private int difference;
    private Item item;
    private Order highestBuyOrder;
    private Order lowestSellOrder;
    private int rank;

    public Difference(int difference, Item item, Order highestBuyOrder, Order lowestSellOrder, int rank) {
        this.difference = difference;
        this.item = item;
        this.highestBuyOrder = highestBuyOrder;
        this.lowestSellOrder = lowestSellOrder;
        this.rank = rank;
    }

    public int getDifference() {
        return difference;
    }

    public Item getItem() {
        return item;
    }

    public Order getHighestBuyOrder() {
        return highestBuyOrder;
    }

    public Order getLowestSellOrder() {
        return lowestSellOrder;
    }

    public int getRank() {
        return rank;
    }
}
