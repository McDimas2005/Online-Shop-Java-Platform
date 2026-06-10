package com.mcdimas.onlineshop.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppErrorController implements ErrorController {
    @GetMapping("/403")
    public String forbidden(Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("title", "Access denied");
        model.addAttribute("message", "You are signed in, but your account does not have access to this area.");
        return "error-friendly";
    }

    @GetMapping("/404")
    public String notFound(Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("title", "Page not found");
        model.addAttribute("message", "The page you requested does not exist.");
        return "error-friendly";
    }

    @GetMapping("/500")
    public String serverError(Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("title", "Something went wrong");
        model.addAttribute("message", "The application hit an unexpected error. Try returning to the dashboard.");
        return "error-friendly";
    }

    @GetMapping("/error")
    public String error(HttpServletRequest request, Model model) {
        Object statusValue = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = statusValue == null ? 500 : Integer.parseInt(statusValue.toString());
        if (status == HttpStatus.FORBIDDEN.value()) {
            return forbidden(model);
        }
        if (status == HttpStatus.NOT_FOUND.value()) {
            return notFound(model);
        }
        return serverError(model);
    }
}
