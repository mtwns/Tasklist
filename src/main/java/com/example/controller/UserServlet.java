package com.example.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.example.dao.UserDAO;
import com.example.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    public void init() {
        Connection connection = (Connection) getServletContext().getAttribute("dbConnection");
        userDAO = new UserDAO(connection);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Manejar las solicitudes POST
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Acción predeterminada
        }

        switch (action) {
            case "show":
                showUser(request, response);
                break;
            case "delete":
                deleteUser(request, response);
                break;
            default:
                listUsers(request, response);
                break;
        }
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<User> users = userDAO.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("user-list.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error al obtener la lista de usuarios", e);
        }
    }

    private void showUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        try {
            User user = userDAO.getUserByUsername(username);
            request.setAttribute("user", user);
            request.getRequestDispatcher("user-detail.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error al obtener el usuario", e);
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        try {
            userDAO.deleteUser(userId);
            response.sendRedirect("UserServlet?action=list");
        } catch (SQLException e) {
            throw new ServletException("Error al eliminar el usuario", e);
        }
    }
}
