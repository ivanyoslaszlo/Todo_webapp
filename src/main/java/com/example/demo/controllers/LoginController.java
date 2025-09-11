package com.example.demo.controllers;

import com.example.demo.entities.Users;
import com.example.demo.respository.repository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;

@Controller
public class LoginController {


    private final repository userRespository;

    public LoginController(repository userRespository) {
        this.userRespository = userRespository;
    }

    @PostMapping("/login")
    public String logincontroller(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) throws FileNotFoundException {

        Users user = userRespository.findByUsername(username);

        if (user != null) {
            if (userRespository.check_password(password, user.getPassword())) {

                session.setAttribute("user", user.getUsername());
                session.setAttribute("login_time", LocalDateTime.now().withNano(0));

                System.out.println("Bejelentkezett: " + user.getUsername() + " " + session.getAttribute("login_time"));

                userRespository.updateLastLogin(user.getUsername());

                return "redirect:/todo";

            } else {
                model.addAttribute("errorMessage", "Hibás jelszó!");
                return "loginpage";
            }
        }

        model.addAttribute("errorMessage", "Nincs ilyen felhasználó!");
        return "loginpage";
    }
}
