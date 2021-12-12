package com.example.demo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ApplicationController {
    @FXML
    private DatePicker picker;
    @FXML
    private TextArea notes;
    @FXML
    private Button deleteButton;
    @FXML
    private Button logoutButton;

    User user = LoginController.user;
    private Map<LocalDate, String> data = new HashMap<>();

    public void initialize() throws SQLException {
        load();
        if(picker != null) {
            picker.valueProperty().addListener((o, oldDate, date) -> {
                notes.setText(data.getOrDefault(date, ""));
            });
            picker.setValue(LocalDate.now());
        }
    }


    public void updateNotes(ActionEvent event) throws SQLException {

        int id =user.getUserId();
        String text = notes.getText();
        Date date = convertToDateViaSqlDate(picker.getValue());
        JdbcDao jdbcDao = new JdbcDao();
        if(jdbcDao.validateInsertNote(id, date)) {
            data.put(picker.getValue(), notes.getText());
            jdbcDao.updateNote(id, text, date);
        }
        else{
            data.put(picker.getValue(), notes.getText());
            jdbcDao.insertNote(id, text, date);
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Notes were updated");
        alert.setTitle("Success");
        alert.setHeaderText(" ");
        alert.show();


    }

    public Date convertToDateViaSqlDate(LocalDate dateToConvert) {
        return java.sql.Date.valueOf(dateToConvert);
    }

    public void load() throws SQLException {
        JdbcDao jdbcDao = new JdbcDao();
        int id = user.getUserId();
        data = jdbcDao.loadData(id);
    }
    public void deleteUser() throws SQLException, IOException {
        JdbcDao jdbcDao = new JdbcDao();
        int id = user.getUserId();
        jdbcDao.deleteUser(id);
        Scene scene = deleteButton.getScene();
        Window window = scene.getWindow();
        Stage stage = (Stage) window;
        Parent root = FXMLLoader.load(getClass().getResource("login_form.fxml"));
        stage.setTitle("User Login");
        stage.setScene(new Scene(root, 600, 500));
    }

    public void logout() throws IOException {
        Scene scene = deleteButton.getScene();
        Window window = scene.getWindow();
        Stage stage = (Stage) window;
        Parent root = FXMLLoader.load(getClass().getResource("login_form.fxml"));
        stage.setTitle("User Login");
        stage.setScene(new Scene(root, 600, 500));
    }


}





