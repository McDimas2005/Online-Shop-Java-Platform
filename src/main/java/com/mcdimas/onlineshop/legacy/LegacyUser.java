package com.mcdimas.onlineshop.legacy;

import java.util.ArrayList;
import java.util.List;

class LegacyUser {
    String id;
    String name;
    String email;
    String address;
    int distance;
    List<LegacyProduct> cart = new ArrayList<>();
    List<LegacyOrder> orders = new ArrayList<>();

    LegacyUser(String id, String name, String email, String address, int distance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.distance = distance;
    }

    boolean admin() {
        return id.startsWith("AD");
    }
}
