package com.example.app.entity;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Report {

    private final SimpleStringProperty serviceName;
    private final SimpleDoubleProperty price;
    private final SimpleStringProperty carRegNumber;
    private final SimpleStringProperty completionDate;

    public Report(String serviceName, double price, String carRegNumber, String completionDate) {
        this.carRegNumber = new SimpleStringProperty(carRegNumber);
        this.completionDate = new SimpleStringProperty(completionDate);
        this.price = new SimpleDoubleProperty(price);
        this.serviceName = new SimpleStringProperty(serviceName);
    }

    public String getServiceName() {
        return serviceName.get();
    }

    public double getPrice() {
        return price.get();
    }

    public String getCarRegNumber() {
        return carRegNumber.get();
    }

    public String getCompletionDate() {
        return completionDate.get();
    }

    @Override
    public String toString() {
        return "Report{" +
                "serviceName=" + serviceName +
                ", price=" + price +
                ", carRegNumber=" + carRegNumber +
                ", completionDate=" + completionDate +
                '}';
    }
}
