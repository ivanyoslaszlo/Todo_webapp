package com.example.demo.controllers;

import com.example.demo.respository.repository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@RestController
public class NoteController {

    private final repository userRespository;

    public NoteController(repository userRespository) {
        this.userRespository = userRespository;
    }


    @PostMapping("/note")
    public String createNote(@RequestBody String szoveg, HttpSession session) {

        System.out.println("Beérkezett szöveg: " + szoveg);
        String username = (String) session.getAttribute("user");

        if (username == null) {
            return "Be kell jelentkezni!";
        }

        userRespository.createNote(username, szoveg);
        return "Jegyzet sikeresen elküldve";
    }

    @GetMapping("/note")
    public Object getNotes(HttpSession session) {
        String username = (String) session.getAttribute("user");

        if (username == null) {
            return "Hozzáférés megtagadva!";
        }
        return userRespository.getNotes(username);
    }

    @DeleteMapping("/note")
    public String deleteNotes(@RequestBody List<String> contents, HttpSession session) {

        String username = (String) session.getAttribute("user");

        if (userRespository.deleteNotes(contents, username)) {
            return "Sikeres törlés";
        } else {
            return "Sikertelen törlés";
        }


    }

}
