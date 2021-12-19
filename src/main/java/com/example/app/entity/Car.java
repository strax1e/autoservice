package com.example.app.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public final class Car {

    private final SimpleStringProperty carRegNumber;
    private final SimpleIntegerProperty clientId;
    private final SimpleStringProperty clientName;

    public Car(String carRegNumber, int clientId, String clientName) {
        this.carRegNumber = new SimpleStringProperty(carRegNumber);
        this.clientId = new SimpleIntegerProperty(clientId);
        this.clientName = new SimpleStringProperty(clientName);
    }

    public String getCarRegNumber() {
        return carRegNumber.get();
    }

    public int getClientId() {
        return clientId.get();
    }

    public String getClientName() {
        return clientName.get();
    }
}
