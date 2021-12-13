package com.example.app;

import com.example.app.db.CarRepository;
import com.example.app.db.ServiceRepository;
import com.example.app.db.UserRepository;
import com.example.app.entity.Car;
import com.example.app.entity.Service;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

public class HelloController {

    @FXML
    private TextField textUsername;

    @FXML
    private PasswordField textPassword;

    @FXML
    private ListView<Button> listView;

    private TableView tableView;

    @FXML
    private Button button;

    @FXML
    private VBox vBox;

    private final ServiceRepository serviceRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public HelloController() {
        final var properties = new Properties();
        try (final var in = HelloController.class.getResourceAsStream("db.properties")) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.serviceRepository = new ServiceRepository(properties);
        this.carRepository = new CarRepository(properties);
        this.userRepository = new UserRepository(properties);
    }

    @FXML
    protected void onHelloButtonClick() {
        button.setDisable(true);
        textUsername.setDisable(true);
        textPassword.setDisable(true);

        final var username = textUsername.getText();
        final var password = textPassword.getText();

        final var authorizer = new Authorizer(userRepository);
        final var user = authorizer.auth(username, password);

        System.out.println(user.isPresent() ? "ok" : "not ok");

        final var buttonGetServices = new Button("Вывести список услуг");
        buttonGetServices.setOnAction(ignore -> switchTableToServices(serviceRepository.getServices(), vBox));
        listView.getItems().add(buttonGetServices);

        final var buttonGetCars = new Button("Вывести список автомобилей");
        buttonGetCars.setOnAction(ignore -> switchTableToCars(carRepository.getCars(), vBox));
        listView.getItems().add(buttonGetCars);

        final var buttonGetCarInfo = new Button("Выдать информацию о машине");
//        buttonGetServices.setOnAction(ignore -> switchTableToServices(serviceRepository.getServices(), vBox));
        listView.getItems().add(buttonGetCarInfo);

        final var buttonGetWorkerInfo = new Button("Выдать информацию о работе специалиста за отчетный период");
//        buttonGetCars.setOnAction(ignore -> switchTableToCars(carRepository.getCars(), vBox));
        listView.getItems().add(buttonGetWorkerInfo);

        final var buttonGetPrice = new Button("Вывести стоимость услуг для клиента");
//        buttonGetServices.setOnAction(ignore -> switchTableToServices(serviceRepository.getServices(), vBox));
        listView.getItems().add(buttonGetPrice);
    }

    private void switchTableToServices(Collection<Service> services, Pane parent) {
        final TableView<Service> table = getNewTableView();

        TableColumn<Service, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        table.getColumns().add(idColumn);

        TableColumn<Service, String> nameColumn = new TableColumn<>("Название");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.getColumns().add(nameColumn);

        TableColumn<Service, Double> priceColumn = new TableColumn<>("Цена");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        table.getColumns().add(priceColumn);

        services.forEach(x -> table.getItems().add(x));
        parent.getChildren().add(table);
    }

    private <T> TableView<T> getNewTableView() {
        vBox.getChildren().remove(this.tableView);
        final TableView<T> newTableView = new TableView<>();
        this.tableView = newTableView;
        return newTableView;
    }

    private void switchTableToCars(Collection<Car> services, Pane parent) {
        final TableView<Car> table = getNewTableView();

        TableColumn<Car, Integer> numberColumn = new TableColumn<>("Номер");
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        table.getColumns().add(numberColumn);

        TableColumn<Car, String> nameColumn = new TableColumn<>("Владелец");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        table.getColumns().add(nameColumn);

        services.forEach(x -> table.getItems().add(x));
        parent.getChildren().add(table);
    }
}
