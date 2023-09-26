package com.chocksaway.reactorflow.entities;

import java.io.Serializable;

public record Image(int id, String name) implements Serializable {
    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}