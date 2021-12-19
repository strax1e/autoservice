package com.example.app.entity;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class CarService {
    private final SimpleStringProperty specialistName;
    private final SimpleStringProperty serviceName;
    private final SimpleDoubleProperty price;

    public CarService(String specialistName, String serviceName, double price) {
        this.specialistName = new SimpleStringProperty(specialistName);
        this.serviceName = new SimpleStringProperty(serviceName);
        this.price = new SimpleDoubleProperty(price);
    }

    public String getSpecialistName() {
        return specialistName.get();
    }

    public String getServiceName() {
        return serviceName.get();
    }

    public double getPrice() {
        return price.get();
    }
}
