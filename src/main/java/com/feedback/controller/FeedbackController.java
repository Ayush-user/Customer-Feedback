package com.feedback.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.feedback.entity.Feedback;
import com.feedback.entity.User;
import com.feedback.repository.FeedbackRepository;
import com.feedback.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/feedback")
    public String feedbackPage(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        List<Feedback> feedbackList =
                feedbackRepository.findByUserId(userId);

        model.addAttribute("feedbackList", feedbackList);
        model.addAttribute("feedback", new Feedback());

        return "feedback";
    }
    
    
    @PostMapping("/feedback/save")
    public String saveFeedback(@ModelAttribute Feedback feedback, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        feedback.setUser(user);
        feedback.setCreatedAt(LocalDateTime.now());

        feedbackRepository.save(feedback);

        return "redirect:/feedback";
    }

    @GetMapping("/feedback/edit/{id}")
    public String editFeedback(@PathVariable Long id, HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        Feedback feedback=feedbackRepository.findById(id).orElse(null);

        if (feedback==null || !feedback.getUser().getId().equals(userId)) {
            return "redirect:/feedback";
        }

        model.addAttribute("feedback", feedback);
        model.addAttribute("feedbackList",feedbackRepository.findByUserId(userId));
        return "feedback";
    }


    @PostMapping("/feedback/update")
    public String updateFeedback(@ModelAttribute Feedback feedback,HttpSession session) {

        Long userId=(Long) session.getAttribute("userId");

        if (userId==null) {
            return "redirect:/login";
        }

        Feedback existing=feedbackRepository.findById(feedback.getId()).orElse(null);

        if (existing==null ||
            !existing.getUser().getId().equals(userId)) {
            return "redirect:/feedback";
        }

        existing.setMessage(feedback.getMessage());

        feedbackRepository.save(existing);

        return "redirect:/feedback";
    }
}