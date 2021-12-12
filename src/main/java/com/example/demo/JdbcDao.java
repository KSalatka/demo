package com.example.demo;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcDao {

    // Replace below database url, username and password with your actual database credentials
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/javafx_registration";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = "L@bas123";

    public boolean validateUsername(String emailId) throws SQLException {
        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

             // Step 2:Create a statement using connection object
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM registration WHERE email_id = ?")) {
            preparedStatement.setString(1, emailId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            // print SQL exception information
            printSQLException(e);
        }
        return false;
    }

    public boolean validate(String emailId, String password) throws SQLException {

        // Step 1: Establishing a Connection and
        // try-with-resource statement will auto close the connection.
        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

             // Step 2:Create a statement using connection object
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM registration WHERE email_id = ? and password = ?")) {
            preparedStatement.setString(1, emailId);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }

    public void validateSignup(String emailId, String password) throws SQLException {
        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `javafx_registration`.`registration` (`email_id`, `password`) VALUES (?, ?)")) {
            preparedStatement.setString(1, emailId);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
        }
    }

    public static void insertNote(int userId, String text, Date date) throws SQLException {
        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `javafx_registration`.`notes` (`userId`, `note_date`, `note_text`) VALUES (?, ?, ?)")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, date);
            preparedStatement.setString(3, text);
            preparedStatement.executeUpdate();
        }
    }

    public static void updateNote(int userId, String text, Date date) throws SQLException {
        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `javafx_registration`.`notes` SET `note_text` = ? WHERE (userId = ? and note_date = ?)")) {
            preparedStatement.setInt(2, userId);
            preparedStatement.setDate(3, date);
            preparedStatement.setString(1, text);
            preparedStatement.executeUpdate();
        }
    }

    public boolean validateInsertNote(int userId, Date date) throws SQLException {
        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

             // Step 2:Create a statement using connection object
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM notes WHERE userId = ? and note_date = ?")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            // print SQL exception information
            printSQLException(e);
        }
        return false;
    }

    public User initUser(String emailId) {
        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

             // Step 2:Create a statement using connection object
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT userId, email_id FROM registration WHERE email_id = ?")) {
            preparedStatement.setString(1, emailId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = new User(resultSet.getInt(1), resultSet.getString(2));
                return user;
            }

        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;
    }

    public Map<LocalDate, String> loadData(int userId) throws SQLException {
        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT note_date, note_text FROM notes WHERE userId = ?")) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            Map<LocalDate, String> data = new HashMap<>();
            while (resultSet.next()) {
                LocalDate date = convertToLocalDateViaSqlDate(resultSet.getDate(1));
                String text = resultSet.getString(2);
                data.put(date, text);
            }
            return data;
        }
    }

    public static void deleteUser(int userId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)){
             PreparedStatement deleteNotes = connection.prepareStatement("DELETE FROM notes where userId = ?");
            PreparedStatement deleteRegistration=connection.prepareStatement("DELETE from registration where userId = ?");
            deleteNotes.setInt(1, userId);
            deleteNotes.executeUpdate();
            deleteRegistration.setInt(1, userId);
            deleteRegistration.executeUpdate();
        }
    }



    public LocalDate convertToLocalDateViaSqlDate(Date dateToConvert) {
        return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }

}

