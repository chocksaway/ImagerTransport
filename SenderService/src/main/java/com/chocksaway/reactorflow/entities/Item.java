package com.chocksaway.reactorflow.entities;

import java.io.Serializable;

public record Item(int id, String name) implements Serializable {
}