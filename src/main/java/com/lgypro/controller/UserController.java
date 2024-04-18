package com.lgypro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    SessionRegistry sessionRegistry;

    @GetMapping("/listUsers")
    @ResponseBody
    public List<Object> list() {
        return sessionRegistry.getAllPrincipals();
    }

    @GetMapping("/kick")
    public String kickUser(String username) {
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();

        for (Object principal : allPrincipals) {
            List<SessionInformation> allSessions = sessionRegistry.getAllSessions(principal, false);

            User user = (User) principal;

            if (user.getUsername().equals(username)) {
                allSessions.forEach(SessionInformation::expireNow);//将所有已经登录的session会话都给失效
            }
        }
        return "redirect:/index";
    }

    @GetMapping("/listAllSessions")
    @ResponseBody
    public List<SessionInformation> listAllSessions(@RequestParam(value = "username", defaultValue = "") String username) {
        User target = null;
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof User user) {
                if (user.getUsername().equals(username)) {
                    target = user;
                }
            }
        }
        if (target != null) {
            return sessionRegistry.getAllSessions(target, true);
        }
        return Collections.emptyList();
    }
}
