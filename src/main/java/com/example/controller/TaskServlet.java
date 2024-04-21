package com.example.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.example.dao.TaskDAO;
import com.example.model.Task;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/TaskServlet")
public class TaskServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TaskDAO taskDAO;

    public void init() {
        Connection connection = (Connection) getServletContext().getAttribute("dbConnection");
        taskDAO = new TaskDAO(connection);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action) {
            case "create":
                createTask(request, response);
                break;
            case "update":
                updateTask(request, response);
                break;
            case "delete":
                deleteTask(request, response);
                break;
            default:
                response.sendRedirect("TaskServlet?action=list");
                break;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action) {
            case "list":
                listTasks(request, response);
                break;
            case "show":
                showTask(request, response);
                break;
            default:
                response.sendRedirect("TaskServlet?action=list");
                break;
        }
    }

    private void listTasks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Task> tasks = taskDAO.getTasksByUserId(1); // Obtener tareas para el usuario actual (por ejemplo, ID 1)
            request.setAttribute("tasks", tasks);
            request.getRequestDispatcher("task-list.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error al obtener la lista de tareas", e);
        }
    }

    private void showTask(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int taskId = Integer.parseInt(request.getParameter("taskId"));
        try {
            Task task = taskDAO.getTaskById(taskId);
            request.setAttribute("task", task);
            request.getRequestDispatcher("task-detail.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error al obtener la tarea", e);
        }
    }

    private void createTask(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Task task = createTaskFromRequest(request);
            taskDAO.createTask(task);
            response.sendRedirect("TaskServlet?action=list");
        } catch (SQLException | ParseException | IllegalArgumentException e) {
            throw new ServletException("Error al crear la tarea", e);
        }
    }

    private void updateTask(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Task task = createTaskFromRequest(request);
            taskDAO.updateTask(task);
            response.sendRedirect("TaskServlet?action=list");
        } catch (SQLException | ParseException | IllegalArgumentException e) {
            throw new ServletException("Error al actualizar la tarea", e);
        }
    }

    private void deleteTask(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int taskId = Integer.parseInt(request.getParameter("taskId"));
        try {
            taskDAO.deleteTask(taskId);
            response.sendRedirect("TaskServlet?action=list");
        } catch (SQLException e) {
            throw new ServletException("Error al eliminar la tarea", e);
        }
    }

    private Task createTaskFromRequest(HttpServletRequest request) throws ParseException {
        Task task = new Task();
        task.setUserId(1); // Asumiendo que el ID del usuario actual es siempre 1, puedes cambiar esto según tu lógica de autenticación de usuario
        task.setTitle(request.getParameter("title"));
        task.setDescription(request.getParameter("description"));

        // Verificar si el parámetro "dueDate" no es nulo ni vacío antes de intentar convertirlo a Date
        String dueDateStr = request.getParameter("dueDate");
        if (dueDateStr != null && !dueDateStr.isEmpty()) {
            task.setDueDate(new SimpleDateFormat("yyyy-MM-dd").parse(dueDateStr));
        } else {
            // Manejar el caso en que el parámetro "dueDate" no esté presente en la solicitud
            throw new IllegalArgumentException("Due date is missing or empty");
        }

        task.setStatus(request.getParameter("status"));
        return task;
    }
}
