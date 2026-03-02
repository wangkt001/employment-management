package com.employment.controller;

import com.employment.entity.User;
import com.employment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("forward:/index.html");
    }

    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("forward:/index.html");
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            Model model) {

        if (userRepository.existsByUsername(username)) {
            model.addAttribute("error", "用户名已存在");
            return "redirect:/register?error=用户名已存在";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);

        userRepository.save(user);

        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard() {
        return new ModelAndView("forward:/index.html");
    }

    @GetMapping("/")
    public ModelAndView home() {
        return new ModelAndView("forward:/index.html");
    }

    @GetMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("forward:/index.html");
    }

    @GetMapping("/admin/**")
    public ModelAndView admin() {
        return new ModelAndView("forward:/index.html");
    }

    @GetMapping("/student/**")
    public ModelAndView student() {
        return new ModelAndView("forward:/index.html");
    }

    @GetMapping("/company/**")
    public ModelAndView company() {
        return new ModelAndView("forward:/index.html");
    }
}