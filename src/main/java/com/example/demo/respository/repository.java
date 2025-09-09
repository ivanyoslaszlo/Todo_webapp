package com.example.demo.respository;

import com.example.demo.entities.Users;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Repository
public class repository {


    public boolean check_username(String username) {
        String url = "jdbc:sqlite:user.datas.db";
        String comand = "select password,username,email from users where username=?";

        try (
                Connection conn = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = conn.prepareStatement(comand);) {

            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();
        } catch (
                SQLException e) {
            System.out.println(e);
            return false;
        }

    }

    public void save(Users user) {

        String url = "jdbc:sqlite:user.datas.db";
        String insertsql = "insert into users(username,email,password,registered_at) values(?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement ps = connection.prepareStatement(insertsql)
        ) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatted_date = now.format(formatter);

            ps.setString(4, formatted_date);
            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println(e);
            throw new RuntimeException(e);
        }


    }

}

