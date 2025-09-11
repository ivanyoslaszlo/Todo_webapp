package com.example.demo.respository;

import com.example.demo.entities.Users;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
public class repository {

    private final String url = "jdbc:sqlite:user.datas.db";
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    public boolean check_username(String username) {
        String sql = "SELECT username FROM users WHERE username=?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public void save(Users user) {
        String insertsql = "INSERT INTO users(username,email,password,registered_at) VALUES(?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement ps = connection.prepareStatement(insertsql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ps.setString(4, now.format(formatter));

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    public boolean check_password(String rawPassword, String encoded_password) {
        return encoder.matches(rawPassword, encoded_password);
    }

    public Users findByUsername(String username) {
        String sql = "SELECT username, email, password FROM users WHERE username=?";
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Users user = new Users();
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                return user;
            }

        } catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        return null;
    }

    public void updateLastLogin(String username) {
        String sql = "UPDATE users SET last_login=? WHERE username=?";
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, now.format(formatter));
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    public void createNote(String username, String content) {
        String sql = "INSERT INTO notes (username, content) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, content);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Hiba a jegyzet mentése közben: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public List<String> getNotes(String username) {

        List<String> notes = new ArrayList<>();

        String url = "jdbc:sqlite:user.datas.db";
        String sql = "SELECT content FROM notes WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                notes.add(rs.getString("content"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return notes;

    }


    public boolean deleteNotes(List<String> contents,String username) {
        String url = "jdbc:sqlite:user.datas.db";
        String sql = "DELETE FROM notes WHERE username = ? AND content = ?";

        int totalDeleted = 0;


        try (Connection connection = DriverManager.getConnection(url)) {
            for (String content : contents) {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, username);
                    ps.setString(2, content);
                    totalDeleted += ps.executeUpdate();
                }
            }
            if (totalDeleted > 0) {
                System.out.println("Sikeresen törölve: " + totalDeleted);
                return true;
            } else {
                System.out.println("Nem történt törlés");
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
