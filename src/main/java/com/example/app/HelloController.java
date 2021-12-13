package com.example.app;

import com.example.app.db.CarRepository;
import com.example.app.db.ServiceRepository;
import com.example.app.db.UserRepository;
import com.example.app.entity.Car;
import com.example.app.entity.Service;
import com.example.app.entity.User;
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

    @FXML
    private Button loginButton;

    @FXML
    private VBox vBox;

    private TableView tableView;

    private final ServiceRepository serviceRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public HelloController() {
        final var properties = new Properties();
        try (final var in = HelloController.class.getResourceAsStream("db.properties")) {
            properties.load(in);
        } catch (IOException e) {
            final var alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Ошибка загрузки БД");
            alert.show();

            setEnableAuthorizingBlock(false);
        }

        this.serviceRepository = new ServiceRepository(properties);
        this.carRepository = new CarRepository(properties);
        this.userRepository = new UserRepository(properties);
    }

    @FXML
    protected void onLoginButtonClick() {
        setEnableAuthorizingBlock(false);

        final var username = textUsername.getText();
        final var password = textPassword.getText();

        final var authorizer = new Authorizer(userRepository);
        final var userOpt = authorizer.auth(username, password);

        if (userOpt.isEmpty()) {
            setEnableAuthorizingBlock(true);
            alertInvalidCredentials();
            return;
        }

        removeAuthorizingBlock();
        createActionButtons(userOpt.get());
    }

    private void createActionButtons(User user) {
        createGetServicesButton();
        switch (user.role()) {
            case ADMIN -> {
                createGetCarsButton();
                createGetCarInfoButton();
                createGetWorkerInfo();
            }
            case CLIENT -> {
                createGetPriceButton(user.username());
            }
            case WORKER -> {
                createGetCarsButton();
                createGetCarInfoButton();
            }
        }
    }

    private void alertInvalidCredentials() {
        final var alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText("Неверный логин или пароль");
        alert.show();
    }

    private void createGetWorkerInfo() {
        final var buttonGetWorkerInfo = new Button("Выдать информацию о работе специалиста за отчетный период");
        // TODO: buttonGetWorkerInfo.setOnAction()
        listView.getItems().add(buttonGetWorkerInfo);
    }

    private void createGetCarInfoButton() {
        final var buttonGetCarInfo = new Button("Выдать информацию о машине");
        // TODO: buttonGetCarInfo.setOnAction()
        listView.getItems().add(buttonGetCarInfo);
    }

    private void createGetCarsButton() {
        final var buttonGetCars = new Button("Вывести список автомобилей");
        buttonGetCars.setOnAction(ignore -> switchTableToCars(carRepository.getCars(), vBox));
        listView.getItems().add(buttonGetCars);
    }

    private void createGetServicesButton() {
        final var buttonGetServices = new Button("Вывести список услуг");
        buttonGetServices.setOnAction(ignore -> switchTableToServices(serviceRepository.getServices(), vBox));
        listView.getItems().add(buttonGetServices);
    }

    private void createGetPriceButton(String username) {
        final var buttonGetPrice = new Button("Вывести стоимость услуг для клиента");
        buttonGetPrice.setOnAction(ignore -> {
            final var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Сумма к оплате: " + serviceRepository.calculateCostByClient(username).orElse(Double.NaN));
            alert.show();
        });
        listView.getItems().add(buttonGetPrice);
    }

    private void setEnableAuthorizingBlock(boolean enable) {
        loginButton.setDisable(!enable);
        textUsername.setDisable(!enable);
        textPassword.setDisable(!enable);
    }

    private void removeAuthorizingBlock() {
        vBox.getChildren().remove(textUsername);
        vBox.getChildren().remove(textPassword);
        vBox.getChildren().remove(loginButton);
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
