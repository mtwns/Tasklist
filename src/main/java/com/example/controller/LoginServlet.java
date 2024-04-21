package com.example.controller;

import com.example.dao.UserDAO;
import com.example.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    public void init() {
        try {
            Connection connection = (Connection) getServletContext().getAttribute("dbConnection");
            userDAO = new UserDAO(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = userDAO.getUserByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                // Usuario y contraseña válidos, redirigir al menú principal
                String A = request.getContextPath() + "/view/main_menu.jsp";
                response.sendRedirect(request.getContextPath() + "main_menu.jsp");
            } else {
                // Credenciales incorrectas, redirigir al formulario de inicio de sesión con un mensaje de error
                request.setAttribute("errorMessage", "Usuario o contraseña incorrectos");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Error al autenticar al usuario", e);
        }
    }
}
