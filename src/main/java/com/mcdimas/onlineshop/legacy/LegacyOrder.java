package com.mcdimas.onlineshop.legacy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class LegacyOrder {
    String id;
    String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    String status = "Accepted";
    int progress = 0;
    int distance;
    BigDecimal total;
    List<LegacyProduct> items = new ArrayList<>();

    LegacyOrder(String id, int distance, BigDecimal total) {
        this.id = id;
        this.distance = distance;
        this.total = total;
    }

    void refresh() {
        progress = Math.min(100, progress + 25);
        if (progress >= 100) {
            status = "Delivered";
        } else if (progress >= 60) {
            status = "Shipping";
        } else if (progress >= 25) {
            status = "Packing";
        }
    }
}
