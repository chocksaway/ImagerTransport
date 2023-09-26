package com.chocksaway.receiverservice.entities;

import java.io.Serializable;

public record Image(int id, String name) implements Serializable {

    public String getName() {
        return this.name;
    }
}