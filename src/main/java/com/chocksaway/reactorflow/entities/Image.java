package com.chocksaway.reactorflow.entities;

public record Image(int id, String name) {
    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}