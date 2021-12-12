package com.example.app.entity;

import javafx.beans.property.SimpleIntegerProperty;

public final class Car {

    private final SimpleIntegerProperty number;
    private final SimpleIntegerProperty owner;

    public Car(int number, int owner) {
        this.number = new SimpleIntegerProperty(number);
        this.owner = new SimpleIntegerProperty(owner);
    }

    public int getNumber() {
        return number.get();
    }

    public int getOwner() {
        return owner.get();
    }
}
