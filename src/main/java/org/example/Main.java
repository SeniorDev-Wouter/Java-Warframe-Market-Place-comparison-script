package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        DifferenceFinder finder = new DifferenceFinder();
        while (true) {
            finder.findBiggestDifference("50", "prime"); // Check 50 random items each time
            finder.checkActiveOrders();
            Thread.sleep(3000);
        }
    }
}
