package org.example;

import java.util.Objects;

public class Difference {
    int difference;
    String item;
    String buyer;
    String seller;
    int rank;

    public Difference(int difference, String item, String buyer, String seller, int rank) {
        this.difference = difference;
        this.item = item;
        this.buyer = buyer;
        this.seller = seller;
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Difference that = (Difference) o;
        return difference == that.difference &&
                rank == that.rank &&
                Objects.equals(item, that.item) &&
                Objects.equals(buyer, that.buyer) &&
                Objects.equals(seller, that.seller);
    }

    @Override
    public int hashCode() {
        return Objects.hash(difference, item, buyer, seller, rank);
    }
}
