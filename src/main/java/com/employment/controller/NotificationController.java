package com.employment.controller;

import com.employment.entity.Notification;
import com.employment.entity.User;
import com.employment.repository.NotificationRepository;
import com.employment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NotificationController {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/notifications")
    public String notifications(Model model, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        model.addAttribute("notifications", notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()));
        model.addAttribute("unreadCount", notificationRepository.countByUserIdAndIsReadFalse(user.getId()));
        return "notifications";
    }
    
    @PostMapping("/notification/read")
    public String markAsRead(@RequestParam Long id, Authentication authentication) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null) {
            User user = userRepository.findByUsername(authentication.getName());
            if (notification.getUser().getId().equals(user.getId())) {
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        }
        return "redirect:/notifications";
    }
    
    @PostMapping("/notification/read-all")
    public String markAllAsRead(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        var notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        }
        return "redirect:/notifications";
    }
}