package com.example.demo.controllers;

import com.example.demo.entities.Users;
import com.example.demo.respository.repository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RestController

public class Register {


    private final repository userRespository;

    public Register(repository userRespository) {
        this.userRespository = userRespository;
    }

    @PostMapping("/register")

    public String registerUser(@RequestBody Users user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (user.getUsername() == null || user.getUsername().trim().isEmpty() ||
                user.getEmail() == null || user.getEmail().trim().isEmpty() ||
                user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return "Hiba: minden mező kitöltése kötelező!";
        }

        if (userRespository.check_username(user.getUsername())) {
            return "Hiba foglalt felhasználó";
        }


        user.setPassword(encoder.encode(user.getPassword()));
        userRespository.save(user);



        return "Sikeres regisztráció!: " + user.getUsername();

    }
}



