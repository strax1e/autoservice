package com.example.app.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Issue {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty carRegNumber;
    private final SimpleStringProperty clientName;
    private final SimpleStringProperty specialistName;
    private final SimpleStringProperty serviceName;
    private final SimpleStringProperty completionDate;

    public Issue(int id, String carRegNumber, String clientName, String specialistName, String serviceName,
                 String completionDate) {
        this.id = new SimpleIntegerProperty(id);
        this.carRegNumber = new SimpleStringProperty(carRegNumber);
        this.clientName = new SimpleStringProperty(clientName);
        this.specialistName = new SimpleStringProperty(specialistName);
        this.serviceName = new SimpleStringProperty(serviceName);
        this.completionDate = new SimpleStringProperty(completionDate);
    }

    public int getId() {
        return id.get();
    }

    public String getCarRegNumber() {
        return carRegNumber.get();
    }

    public String getClientName() {
        return clientName.get();
    }

    public String getSpecialistName() {
        return specialistName.get();
    }

    public String getServiceName() {
        return serviceName.get();
    }

    public String getCompletionDate() {
        return completionDate.get();
    }
}
