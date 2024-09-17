package org.example;

public class Order {
    private String orderType;
    private String userName;
    private int platinum;
    private int modRank;

    public Order(String orderType, String userName, int platinum, int modRank) {
        this.orderType = orderType;
        this.userName = userName;
        this.platinum = platinum;
        this.modRank = modRank;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getUserName() {
        return userName;
    }

    public int getPlatinum() {
        return platinum;
    }

    public int getModRank() {
        return modRank;
    }
}
