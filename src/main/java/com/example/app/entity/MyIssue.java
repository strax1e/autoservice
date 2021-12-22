package com.example.app.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class MyIssue {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty carRegNumber;
    private final SimpleStringProperty completionDate;

    public MyIssue(int id, String carRegNumber, String completionDate) {
        this.id = new SimpleIntegerProperty(id);
        this.carRegNumber = new SimpleStringProperty(carRegNumber);
        this.completionDate = new SimpleStringProperty(completionDate);
    }

    public int getId() {
        return id.get();
    }

    public String getCarRegNumber() {
        return carRegNumber.get();
    }

    public String getCompletionDate() {
        return completionDate.get();
    }
}
