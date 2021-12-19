package com.example.app;

import com.example.app.db.CarRepository;
import com.example.app.db.ServiceRepository;
import com.example.app.db.SpecialistRepository;
import com.example.app.db.UserRepository;
import com.example.app.entity.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.*;

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
    private final SpecialistRepository specialistRepository;

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
        this.specialistRepository = new SpecialistRepository(properties);
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
                createGetSpecialistReportButtons();
            }
            case CLIENT -> createGetPriceButton(user.username());
            case SPECIALIST -> {
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

    private void createGetSpecialistReportButtons() {
        createGetSpecialistYearReportButton();
        createGetSpecialistMonthReportButton();
        createGetSpecialistDayReportButton();
        createGetSpecialistQuarterReportButton();
    }

    private void createGetSpecialistQuarterReportButton() {
        final var buttonGetSpecialistDayReport = new Button("Выдать информацию о работе специалиста за квартал");
        buttonGetSpecialistDayReport.setOnAction(ignore -> {
            var dialog = new Dialog<Map<String, String>>();
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

            var gridPane = new GridPane();
            var name = new TextField();
            name.setPromptText("Имя");
            var year = new TextField();
            year.setPromptText("Год");
            var quarter = new TextField();
            quarter.setPromptText("Квартал");

            gridPane.add(name, 0, 0);
            gridPane.add(year, 0, 1);
            gridPane.add(quarter, 0, 2);

            dialog.getDialogPane().setContent(gridPane);
            dialog.setResultConverter(dialogButton -> {
                if (ButtonType.OK.equals(dialogButton)) {
                    var map = new HashMap<String, String>();
                    map.put("name", name.getText());
                    map.put("year", year.getText());
                    map.put("quarter", quarter.getText());
                    return map;
                }
                return null;
            });
            Optional<Map<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                var map = result.get();
                removeCurrentTableView();
                switchTableToSpecialistReports(specialistRepository.getSpecialistQuarterReport(map.get("name"),
                        Integer.parseInt(map.get("year")), Integer.parseInt(map.get("quarter"))), vBox);
            }
        });
        listView.getItems().add(buttonGetSpecialistDayReport);
    }

    private void createGetSpecialistDayReportButton() {
        final var buttonGetSpecialistDayReport = new Button("Выдать информацию о работе специалиста за день");
        buttonGetSpecialistDayReport.setOnAction(ignore -> {
            var dialog = new Dialog<Map<String, String>>();
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

            var gridPane = new GridPane();
            var name = new TextField();
            name.setPromptText("Имя");
            var year = new TextField();
            year.setPromptText("Год");
            var month = new TextField();
            month.setPromptText("Месяц");
            var day = new TextField();
            day.setPromptText("День");

            gridPane.add(name, 0, 0);
            gridPane.add(year, 0, 1);
            gridPane.add(month, 0, 2);
            gridPane.add(day, 0, 3);

            dialog.getDialogPane().setContent(gridPane);
            dialog.setResultConverter(dialogButton -> {
                if (ButtonType.OK.equals(dialogButton)) {
                    var map = new HashMap<String, String>();
                    map.put("name", name.getText());
                    map.put("year", year.getText());
                    map.put("month", month.getText());
                    map.put("day", day.getText());
                    return map;
                }
                return null;
            });
            Optional<Map<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                var map = result.get();
                removeCurrentTableView();
                switchTableToSpecialistReports(specialistRepository.getSpecialistDayReport(map.get("name"),
                        Integer.parseInt(map.get("year")), Integer.parseInt(map.get("month")),
                        Integer.parseInt(map.get("day"))), vBox);
            }
        });
        listView.getItems().add(buttonGetSpecialistDayReport);
    }

    private void createGetSpecialistMonthReportButton() {
        final var buttonGetSpecialistMonthReport = new Button("Выдать информацию о работе специалиста за месяц");
        buttonGetSpecialistMonthReport.setOnAction(ignore -> {
            var dialog = new Dialog<Map<String, String>>();
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

            var gridPane = new GridPane();
            var name = new TextField();
            name.setPromptText("Имя");
            var year = new TextField();
            year.setPromptText("Год");
            var month = new TextField();
            month.setPromptText("Месяц");

            gridPane.add(name, 0, 0);
            gridPane.add(year, 0, 1);
            gridPane.add(month, 0, 2);

            dialog.getDialogPane().setContent(gridPane);
            dialog.setResultConverter(dialogButton -> {
                if (ButtonType.OK.equals(dialogButton)) {
                    var map = new HashMap<String, String>();
                    map.put("name", name.getText());
                    map.put("year", year.getText());
                    map.put("month", month.getText());
                    return map;
                }
                return null;
            });
            Optional<Map<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                var map = result.get();
                removeCurrentTableView();
                switchTableToSpecialistReports(specialistRepository.getSpecialistMonthReport(map.get("name"),
                        Integer.parseInt(map.get("year")), Integer.parseInt(map.get("month"))), vBox);
            }
        });
        listView.getItems().add(buttonGetSpecialistMonthReport);
    }

    private void createGetSpecialistYearReportButton() {
        final var buttonGetSpecialistYearReport = new Button("Выдать информацию о работе специалиста за год");
        buttonGetSpecialistYearReport.setOnAction(ignore -> {
            var dialog = new Dialog<Map<String, String>>();
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

            var gridPane = new GridPane();
            var name = new TextField();
            name.setPromptText("Имя");
            var year = new TextField();
            year.setPromptText("Год");

            gridPane.add(name, 0, 0);
            gridPane.add(year, 0, 1);

            dialog.getDialogPane().setContent(gridPane);
            dialog.setResultConverter(dialogButton -> {
                if (ButtonType.OK.equals(dialogButton)) {
                    var map = new HashMap<String, String>();
                    map.put("name", name.getText());
                    map.put("year", year.getText());
                    return map;
                }
                return null;
            });
            Optional<Map<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                var map = result.get();
                removeCurrentTableView();
                switchTableToSpecialistReports(specialistRepository.getSpecialistYearReport(map.get("name"),
                        Integer.parseInt(map.get("year"))), vBox);
            }
        });
        listView.getItems().add(buttonGetSpecialistYearReport);
    }

    private void createGetCarInfoButton() {
        final var buttonGetCarInfo = new Button("Выдать информацию об услугах, оказываемых машине");
        buttonGetCarInfo.setOnAction(ignore -> {
            final var dialog = new TextInputDialog();
            dialog.setHeaderText("Enter number of car:");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                var list = serviceRepository.getServicesOfCar(result.get());
                if (list.isEmpty()) {
                    final var alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("По заданной машине нет услуг");
                    alert.show();
                }
                removeCurrentTableView();
                switchTableToCarServices(list, vBox);
            }
        });
        listView.getItems().add(buttonGetCarInfo);
    }

    private void createGetCarsButton() {
        final var buttonGetCars = new Button("Вывести список автомобилей");
        buttonGetCars.setOnAction(ignore -> {
            removeCurrentTableView();
            switchTableToCars(carRepository.getCars(), vBox);
        });
        listView.getItems().add(buttonGetCars);
    }

    private void createGetServicesButton() {
        final var buttonGetServices = new Button("Вывести список услуг");
        buttonGetServices.setOnAction(ignore -> {
            removeCurrentTableView();
            switchTableToServices(serviceRepository.getServices(), vBox);
        });
        listView.getItems().add(buttonGetServices);
    }

    private void createGetPriceButton(String username) {
        final var buttonGetPrice = new Button("Вывести стоимость услуг для клиента");
        buttonGetPrice.setOnAction(ignore -> {
            final var alert = new Alert(Alert.AlertType.INFORMATION);
            removeCurrentTableView();
            alert.setContentText("Сумма к оплате: " + serviceRepository.calculateCostByClient(username)
                    .orElse(Double.NaN));
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

    private void switchTableToSpecialistReports(Collection<Report> reports, Pane parent) {
        final TableView<Report> table = getNewTableView();

        TableColumn<Report, String> serviceNameColumn = new TableColumn<>("Название услуги");
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        table.getColumns().add(serviceNameColumn);

        TableColumn<Report, Double> priceColumn = new TableColumn<>("Цена");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        table.getColumns().add(priceColumn);

        TableColumn<Report, String> carRegNumberColumn = new TableColumn<>("Номер автомобиля");
        carRegNumberColumn.setCellValueFactory(new PropertyValueFactory<>("carRegNumber"));
        table.getColumns().add(carRegNumberColumn);

        TableColumn<Report, String> completionDateColumn = new TableColumn<>("Дата окончания");
        completionDateColumn.setCellValueFactory(new PropertyValueFactory<>("completionDate"));
        table.getColumns().add(completionDateColumn);

        reports.forEach(x -> table.getItems().add(x));
        parent.getChildren().add(table);
    }

    private void switchTableToCarServices(Collection<CarService> carServices, Pane parent) {
        final TableView<CarService> table = getNewTableView();

        TableColumn<CarService, String> specialistNameColumn = new TableColumn<>("Имя специалиста");
        specialistNameColumn.setCellValueFactory(new PropertyValueFactory<>("specialistName"));
        table.getColumns().add(specialistNameColumn);

        TableColumn<CarService, String> serviceNameColumn = new TableColumn<>("Название услуги");
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        table.getColumns().add(serviceNameColumn);

        TableColumn<CarService, Double> priceColumn = new TableColumn<>("Цена");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        table.getColumns().add(priceColumn);

        carServices.forEach(x -> table.getItems().add(x));
        parent.getChildren().add(table);
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
        final TableView<T> newTableView = new TableView<>();
        this.tableView = newTableView;
        return newTableView;
    }

    private void removeCurrentTableView() {
        vBox.getChildren().remove(this.tableView);
    }

    private void switchTableToCars(Collection<Car> services, Pane parent) {
        final TableView<Car> table = getNewTableView();

        TableColumn<Car, String> numberColumn = new TableColumn<>("Номер");
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("carRegNumber"));
        table.getColumns().add(numberColumn);

        TableColumn<Car, String> nameColumn = new TableColumn<>("Владелец");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        table.getColumns().add(nameColumn);

        services.forEach(x -> table.getItems().add(x));
        parent.getChildren().add(table);
    }
}
