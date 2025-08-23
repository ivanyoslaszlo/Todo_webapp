package com.example.demo.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class NoteConroller {


    @PostMapping("/note")
    public String createNote(@RequestBody String szoveg, HttpSession session) {
        System.out.println("Beérkezett szöveg: " + szoveg);

        String url = "jdbc:sqlite:user.datas.db";
        String comand = "insert into notes (username,content)  values (?,?)";
        String username = (String) session.getAttribute("user");
        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = connection.prepareStatement(comand)


        ) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, szoveg);
            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                  System.out.println("sikeres beszurás");
            } else {
                System.out.println("hiba a beszurás közben");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return "A jegyzet sikeresen elküldve!";
    }


    @GetMapping("/note")
    public List<String> getNotes(HttpSession session) {

        String username = (String) session.getAttribute("user");
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

    @DeleteMapping("/note")
    public String deleteNotes(@RequestBody List<String> contents, HttpSession session) {
        String username = (String) session.getAttribute("user");
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
                System.out.println("Sikeresen törölve: "+totalDeleted);
            } else {
                System.out.println("Nem történt törlés");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
