package com.example.dao;

import com.example.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private Connection connection;

    // Constructor
    public TaskDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para obtener todas las tareas de un usuario
    public List<Task> getTasksByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM Tasks WHERE user_id = ?";
        List<Task> tasks = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(new Task(
                            resultSet.getInt("task_id"),
                            resultSet.getInt("user_id"),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            resultSet.getDate("due_date"),
                            resultSet.getString("status")
                    ));
                }
            }
        }
        return tasks;
    }

    // Método para obtener una tarea por su ID
    public Task getTaskById(int taskId) throws SQLException {
        String query = "SELECT * FROM Tasks WHERE task_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, taskId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Task(
                            resultSet.getInt("task_id"),
                            resultSet.getInt("user_id"),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            resultSet.getDate("due_date"),
                            resultSet.getString("status")
                    );
                }
            }
        }
        return null;
    }

    // Método para insertar una nueva tarea en la base de datos
    public void createTask(Task task) throws SQLException {
        String query = "INSERT INTO Tasks (user_id, title, description, due_date, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, task.getUserId());
            statement.setString(2, task.getTitle());
            statement.setString(3, task.getDescription());
            statement.setDate(4, new java.sql.Date(task.getDueDate().getTime()));
            statement.setString(5, task.getStatus());
            statement.executeUpdate();
        }
    }

    // Método para actualizar los datos de una tarea en la base de datos
    public void updateTask(Task task) throws SQLException {
        String query = "UPDATE Tasks SET user_id = ?, title = ?, description = ?, due_date = ?, status = ? WHERE task_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, task.getUserId());
            statement.setString(2, task.getTitle());
            statement.setString(3, task.getDescription());
            statement.setDate(4, new java.sql.Date(task.getDueDate().getTime()));
            statement.setString(5, task.getStatus());
            statement.setInt(6, task.getTaskId());
            statement.executeUpdate();
        }
    }

    // Método para eliminar una tarea de la base de datos por su ID
    public void deleteTask(int taskId) throws SQLException {
        String query = "DELETE FROM Tasks WHERE task_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, taskId);
            statement.executeUpdate();
        }
    }
}
