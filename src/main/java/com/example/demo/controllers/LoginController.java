package com.example.demo.controllers;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Controller
public class LoginController {

    String url = "jdbc:sqlite:user.datas.db";

    public void last_logged_in(Connection connection, String username) {

        String insert_last_loggin = "update users set last_login=? where username=?";

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatedDate = now.format(formatter);

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(insert_last_loggin)
        ) {
            preparedStatement.setString(1, formatedDate);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }


    }


    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @PostMapping("/login")
    public String logincontroller(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) throws FileNotFoundException {

        String comand = "select password,username,email from users where username=?";

        String inputusername = username;


        try (
                Connection connection = DriverManager.getConnection(url);

                PreparedStatement preparedStatement = connection.prepareStatement(comand)

        ) {

            preparedStatement.setString(1, inputusername);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {

                String dbusername = rs.getString("username");
                String dbpassword = rs.getString("password");


                if (encoder.matches(password, dbpassword)) {

                    session.setAttribute("user", username);
                    Object userobj = session.getAttribute("user");
                    System.out.println("Bejelentkezett: " + userobj);
                    if (userobj != null) {
                        last_logged_in(connection,userobj.toString());
                        return "redirect:/todo";


                    } else {
                        return "loginpage";
                    }


                } else {
                    model.addAttribute("errorMessage", "Hibás jelszó!");
                    return "loginpage";
                }
            }


        } catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException(e);

        }


        return "loginpage";
    }


}
