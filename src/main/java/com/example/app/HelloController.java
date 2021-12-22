package com.example.app;

import com.example.app.db.*;
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

    private static final String NUMBER = "Номер";
    private static final String MONTH = "Месяц";
    private static final String YEAR = "Год";
    private static final String DAY = "День";
    private static final String QUARTER = "Квартал";
    private static final String HUMAN_NAME = "Имя";
    private static final String NAME = "Название";
    private static final String PRICE = "Цена";
    private static final String ID = "Идентификатор";

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

    private TableView<?> tableView;
    private final ServiceRepository serviceRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final SpecialistRepository specialistRepository;
    private final IssueRepository issueRepository;

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
        this.issueRepository = new IssueRepository(properties);
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
                createAddServiceButton();
                createAddCarButton();
                createShowAllIssuesButton();
                createAddClientButton();
            }
            case CLIENT -> createGetPriceButton(user.username());
            case SPECIALIST -> {
                createGetCarsButton();
                createGetCarInfoButton();
                createCompleteIssueButton();
                createShowMyIssuesButton();
            }
        }
    }

    private void createAddClientButton() {
        var buttonAddClient = new Button("Добавить нового клиента");
        buttonAddClient.setOnAction(ignore -> {
            Dialog<Map<String, String>> dialog = getDialogWithTextFields(List.of(
                    HUMAN_NAME,
                    "Логин",
                    "Пароль",
                    "Номер телефона",
                    "Автомобильный номер",
                    "Банковские реквизиты"
            ));
            Optional<Map<String, String>> result = dialog.showAndWait();

            var isAdded = false;
            if (result.isPresent()) {
                var map = result.get();
                removeCurrentTableView();
                try {
                    isAdded = userRepository.addNewClient(
                            map.get(HUMAN_NAME),
                            map.get("Логин"),
                            map.get("Пароль"),
                            map.get("Номер телефона"),
                            map.get("Автомобильный номер"),
                            map.get("Банковские реквизиты")
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (!isAdded) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Произошла ошибка, изменения отменены");
                alert.show();
            }
        });
        listView.getItems().add(buttonAddClient);
    }

    private void createShowAllIssuesButton() {
        var buttonShowAllIssuesButton = new Button("Выдать информацию о заказах");
        buttonShowAllIssuesButton.setOnAction(ignore -> {
            removeCurrentTableView();
            switchTableToAllIssues(issueRepository.getIssues(), vBox);
        });
        listView.getItems().add(buttonShowAllIssuesButton);
    }

    private void switchTableToAllIssues(List<Issue> issues, Pane parent) {
        final TableView<Issue> table = getNewTableView();

        TableColumn<Issue, Integer> idColumn = new TableColumn<>(ID);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        table.getColumns().add(idColumn);

        TableColumn<Issue, String> numberColumn = new TableColumn<>(NUMBER);
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("carRegNumber"));
        table.getColumns().add(numberColumn);

        TableColumn<Issue, String> clientColumn = new TableColumn<>("Имя клиента");
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        table.getColumns().add(clientColumn);

        TableColumn<Issue, String> specialistColumn = new TableColumn<>("Имя специалиста");
        specialistColumn.setCellValueFactory(new PropertyValueFactory<>("specialistName"));
        table.getColumns().add(specialistColumn);

        TableColumn<Issue, String> serviceColumn = new TableColumn<>("Название услуги");
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        table.getColumns().add(serviceColumn);

        TableColumn<Issue, String> completionDate = new TableColumn<>("Дата завершения");
        completionDate.setCellValueFactory(new PropertyValueFactory<>("completionDate"));
        table.getColumns().add(completionDate);

        issues.forEach(x -> table.getItems().add(x));
        parent.getChildren().add(table);
    }

    private void createShowMyIssuesButton() {
        var buttonShowMyIssuesButton = new Button("Выдать информацию о моих заказах");
        buttonShowMyIssuesButton.setOnAction(ignore -> {
            removeCurrentTableView();
            switchTableToMyIssues(issueRepository.getIssues(textUsername.getText()), vBox);
        });
        listView.getItems().add(buttonShowMyIssuesButton);
    }

    private void switchTableToMyIssues(List<MyIssue> issues, Pane parent) {
        final TableView<MyIssue> table = getNewTableView();

        TableColumn<MyIssue, Integer> idColumn = new TableColumn<>(ID);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        table.getColumns().add(idColumn);

        TableColumn<MyIssue, String> numberColumn = new TableColumn<>(NUMBER);
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("carRegNumber"));
        table.getColumns().add(numberColumn);

        TableColumn<MyIssue, String> completionDate = new TableColumn<>("Дата завершения");
        completionDate.setCellValueFactory(new PropertyValueFactory<>("completionDate"));
        table.getColumns().add(completionDate);

        issues.forEach(x -> table.getItems().add(x));
        parent.getChildren().add(table);
    }

    private void createCompleteIssueButton() {
        final var buttonCompleteIssue = new Button("Завершить заказ");
        buttonCompleteIssue.setOnAction(ignore -> {
            Dialog<Map<String, String>> dialog = getDialogWithTextFields(List.of(ID));
            Optional<Map<String, String>> result = dialog.showAndWait();

            var isUpdated = false;
            if (result.isPresent()) {
                var map = result.get();
                removeCurrentTableView();
                isUpdated = issueRepository.complete(Integer.parseInt(map.get(ID)), textUsername.getText());
            }
            if (!isUpdated) {
                final var alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Произошла ошибка. Вероятно, это не ваш заказ, или идентификатор указан неверно");
                alert.show();
            }
        });
        listView.getItems().add(buttonCompleteIssue);
    }

    private void createAddServiceButton() {
        final var buttonAddService = new Button("Добавить новую услугу");
        buttonAddService.setOnAction(ignore -> {
            Dialog<Map<String, String>> dialog = getDialogWithTextFields(List.of(NAME, PRICE));
            Optional<Map<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                var map = result.get();
                removeCurrentTableView();
                serviceRepository.addService(map.get(NAME), Double.parseDouble(map.get(PRICE)));
            }
        });
        listView.getItems().add(buttonAddService);
    }

    private void createAddCarButton() {
        final var buttonAddService = new Button("Добавить новую машину");
        buttonAddService.setOnAction(ignore -> {
            Dialog<Map<String, String>> dialog = getDialogWithTextFields(List.of(NUMBER, ID));
            Optional<Map<String, String>> result = dialog.showAndWait();

            var isAdded = false;
            if (result.isPresent()) {
                var map = result.get();
                removeCurrentTableView();
                try {
                    isAdded = carRepository.addCar(map.get(NUMBER), Integer.parseInt(map.get(ID)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (!isAdded) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Произошла ошибка, возможно клиента с таким идентификатором нет");
                alert.show();
            }
        });
        listView.getItems().add(buttonAddService);
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
            Dialog<Map<String, String>> dialog = getDialogWithTextFields(List.of(HUMAN_NAME, YEAR, QUARTER));
            Optional<Map<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                var map = result.get();
                removeCurrentTableView();
                switchTableToSpecialistReports(specialistRepository.getSpecialistQuarterReport(map.get(HUMAN_NAME),
                        Integer.parseInt(map.get(YEAR)), Integer.parseInt(map.get(QUARTER))), vBox);
            }
        });
        listView.getItems().add(buttonGetSpecialistDayReport);
    }

    private Dialog<Map<String, String>> getDialogWithTextFields(Collection<String> names) {
        var dialog = new Dialog<Map<String, String>>();
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        var fieldsMap = new HashMap<String, TextField>();
        var pane = new GridPane();
        int count = 0;
        for (String s : names) {
            var field = new TextField();
            field.setPromptText(s);
            pane.add(field, 0, count);
            fieldsMap.put(s, field);
            count++;
        }

        dialog.getDialogPane().setContent(pane);
        dialog.setResultConverter(dialogButton -> {
            if (ButtonType.OK.equals(dialogButton)) {
                var map = new HashMap<String, String>();
                fieldsMap.forEach((name, field) -> map.put(name, field.getText()));
                return map;
            }
            return null;
        });
        return dialog;
    }

    private void createGetSpecialistDayReportButton() {
        final var buttonGetSpecialistDayReport = new Button("Выдать информацию о работе специалиста за день");
        buttonGetSpecialistDayReport.setOnAction(ignore -> {
            Dialog<Map<String, String>> dialog = getDialogWithTextFields(List.of(HUMAN_NAME, YEAR, MONTH, DAY));
            Optional<Map<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                var map = result.get();
                removeCurrentTableView();
                switchTableToSpecialistReports(specialistRepository.getSpecialistDayReport(map.get(HUMAN_NAME),
                        Integer.parseInt(map.get(YEAR)), Integer.parseInt(map.get(MONTH)),
                        Integer.parseInt(map.get(DAY))), vBox);
            }
        });
        listView.getItems().add(buttonGetSpecialistDayReport);
    }

    private void createGetSpecialistMonthReportButton() {
        final var buttonGetSpecialistMonthReport = new Button("Выдать информацию о работе специалиста за месяц");
        buttonGetSpecialistMonthReport.setOnAction(ignore -> {
            Dialog<Map<String, String>> dialog = getDialogWithTextFields(List.of(HUMAN_NAME, YEAR, MONTH));
            Optional<Map<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                var map = result.get();
                removeCurrentTableView();
                switchTableToSpecialistReports(specialistRepository.getSpecialistMonthReport(map.get(HUMAN_NAME),
                        Integer.parseInt(map.get(YEAR)), Integer.parseInt(map.get(MONTH))), vBox);
            }
        });
        listView.getItems().add(buttonGetSpecialistMonthReport);
    }

    private void createGetSpecialistYearReportButton() {
        final var buttonGetSpecialistYearReport = new Button("Выдать информацию о работе специалиста за год");
        buttonGetSpecialistYearReport.setOnAction(ignore -> {
            Dialog<Map<String, String>> dialog = getDialogWithTextFields(List.of(HUMAN_NAME, YEAR));
            Optional<Map<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                var map = result.get();
                removeCurrentTableView();
                switchTableToSpecialistReports(specialistRepository.getSpecialistYearReport(map.get(HUMAN_NAME),
                        Integer.parseInt(map.get(YEAR))), vBox);
            }
        });
        listView.getItems().add(buttonGetSpecialistYearReport);
    }

    private void createGetCarInfoButton() {
        final var buttonGetCarInfo = new Button("Выдать информацию об услугах, оказываемых машине");
        buttonGetCarInfo.setOnAction(ignore -> {
            Dialog<Map<String, String>> dialog = getDialogWithTextFields(List.of(NUMBER));
            Optional<Map<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                var map = result.get();
                var list = serviceRepository.getServicesOfCar(map.get(NUMBER));
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

        TableColumn<Report, String> serviceNameColumn = new TableColumn<>(NAME);
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        table.getColumns().add(serviceNameColumn);

        TableColumn<Report, Double> priceColumn = new TableColumn<>(PRICE);
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

        TableColumn<CarService, String> specialistNameColumn = new TableColumn<>(HUMAN_NAME);
        specialistNameColumn.setCellValueFactory(new PropertyValueFactory<>("specialistName"));
        table.getColumns().add(specialistNameColumn);

        TableColumn<CarService, String> serviceNameColumn = new TableColumn<>(NAME);
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        table.getColumns().add(serviceNameColumn);

        TableColumn<CarService, Double> priceColumn = new TableColumn<>(PRICE);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        table.getColumns().add(priceColumn);

        carServices.forEach(x -> table.getItems().add(x));
        parent.getChildren().add(table);
    }

    private void switchTableToServices(Collection<Service> services, Pane parent) {
        final TableView<Service> table = getNewTableView();

        TableColumn<Service, Integer> idColumn = new TableColumn<>(ID);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        table.getColumns().add(idColumn);

        TableColumn<Service, String> nameColumn = new TableColumn<>(NAME);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.getColumns().add(nameColumn);

        TableColumn<Service, Double> priceColumn = new TableColumn<>(PRICE);
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

        TableColumn<Car, String> numberColumn = new TableColumn<>(NUMBER);
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("carRegNumber"));
        table.getColumns().add(numberColumn);

        TableColumn<Car, String> nameColumn = new TableColumn<>("Владелец");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        table.getColumns().add(nameColumn);

        services.forEach(x -> table.getItems().add(x));
        parent.getChildren().add(table);
    }
}
