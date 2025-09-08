package com.example.demo.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Controller
public class HomePageController {

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @GetMapping("/loginpage")
    public String showLoginPage() {
        return "loginpage";
    }

    @PostMapping("/kilepes")
    public String logout(HttpSession session) {

        LocalDateTime belepes_ideje = (LocalDateTime) session.getAttribute("login_time");
        LocalDateTime kilepes_ideje = LocalDateTime.now().withNano(0);
        Duration munkamenet = Duration.between(belepes_ideje, kilepes_ideje);
        long hour = munkamenet.toHours();
        long minutes = munkamenet.toMinutes() % 60;
        long seconds = munkamenet.toSeconds() % 60;

        System.out.println(session.getAttribute("user") + " Kilépett: " + kilepes_ideje + " || " + "Munkamenet hossza: " + hour + " óra " + minutes + " perc " + seconds + " másodperc");
        session.invalidate();
        return "loginpage";
    }

    @GetMapping("/todo")
    public String showTodoPage(HttpSession session) throws SQLException {

        String username = (String) session.getAttribute("user");


        if (username == null) {
            return "loginpage";
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:user.datas.db");
             PreparedStatement ps = conn.prepareStatement("SELECT role FROM users WHERE username = ?")) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {

                String role = rs.getString("role");

                if ("ADMIN".equals(role)) {
                    return "AdminCenter";

                } else {
                    return "todo";
                }


            } else {
                return "loginpage";
            }
        }

    }
}
