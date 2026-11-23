package com.feedback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.feedback.entity.User;
import com.feedback.service.AuthService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, @RequestParam String confirmPassword,Model model) {

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        String result = authService.register(user);

        if (!result.equals("Registration successful")) {
            model.addAttribute("error", result);
            return "register";
        }

        return "redirect:/login";
    }

  
    @PostMapping("/login")
    public String login(@RequestParam String username,@RequestParam String password,HttpSession session,Model model) {

        User user = authService.login(username, password);

        if (user == null) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        if (user.getRole().equals("ADMIN")) {
            return "redirect:/admin/dashboard";
        }

        return "redirect:/feedback";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}