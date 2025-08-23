package com.example.demo.register_entity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


@RestController
@CrossOrigin(origins = "*")
public class Register {

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @PostMapping("/register")

    public String registerUser(@RequestBody Users user) throws SQLException {

        String url = "jdbc:sqlite:user.datas.db";
        String comand = "select password,username,email from users where username=?";

        try (
                Connection conn = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = conn.prepareStatement(comand);) {

            preparedStatement.setString(1, user.getUsername());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return "Hiba ez a felhasználó név már foglalt!";
            }
        }

        user.setPassword(encoder.encode(user.getPassword()));
        String insertsql = "insert into users(username,email,password,registered_at) values(?,?,?,?)";


        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement ps = connection.prepareStatement(insertsql)
        ) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());

            LocalDateTime now=LocalDateTime.now();
            DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatted_date=now.format(formatter);
            
            ps.setString(4, formatted_date);
            ps.executeUpdate();
        }


        return "Sikeres regisztráció: " + user.getUsername();


    }


}



