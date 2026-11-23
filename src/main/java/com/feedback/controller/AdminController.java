package com.feedback.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.feedback.entity.Feedback;
import com.feedback.entity.User;
import com.feedback.repository.FeedbackRepository;
import com.feedback.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {

        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        List<Feedback> feedbackList = feedbackRepository.findAll();

        model.addAttribute("feedbackList", feedbackList);
        model.addAttribute("feedback", new Feedback());
        model.addAttribute("users", userRepository.findAll());

        return "admin";
    }

    @PostMapping("/admin/add")
    public String addFeedback(@RequestParam String message,@RequestParam Long userId,HttpSession session) {

        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        User user=userRepository.findById(userId).orElse(null);

        if (user!=null) {

            Feedback feedback = new Feedback();

            feedback.setMessage(message);
            feedback.setUser(user);
            feedback.setCreatedAt(LocalDateTime.now());

            feedbackRepository.save(feedback);
        }

        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/admin/edit/{id}")
    public String editFeedback(@PathVariable Long id,HttpSession session,Model model) {

        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        Feedback feedback=feedbackRepository.findById(id).orElse(null);

        if (feedback==null) {
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("feedback",feedback);
        return "admin-edit";
    }

    @PostMapping("/admin/update")
    public String updateFeedback(@RequestParam Long id, @RequestParam String message,HttpSession session) {

        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        Feedback feedback = feedbackRepository.findById(id).orElse(null);

        if (feedback != null) {

            feedback.setMessage(message);

            feedbackRepository.save(feedback);
        }

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteFeedback(@PathVariable Long id,
                                 HttpSession session) {

        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        feedbackRepository.deleteById(id);

        return "redirect:/admin/dashboard";
    }
}