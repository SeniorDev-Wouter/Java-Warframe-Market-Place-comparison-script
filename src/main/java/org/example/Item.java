package org.example;

public class Item {
    private String itemName;
    private String urlName;

    public Item(String itemName, String urlName) {
        this.itemName = itemName;
        this.urlName = urlName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getUrlName() {
        return urlName;
    }
}

