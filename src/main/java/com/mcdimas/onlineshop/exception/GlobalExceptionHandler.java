package com.mcdimas.onlineshop.exception;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public Object notFound(NotFoundException ex, HttpServletRequest request, Model model) {
        if (isApi(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
        return friendly(model, 404, "Page not found", ex.getMessage());
    }

    @ExceptionHandler(AppException.class)
    public Object app(AppException ex, HttpServletRequest request, Model model) {
        if (isApi(request)) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
        return friendly(model, 400, "Request could not be completed", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validation(MethodArgumentNotValidException ex, HttpServletRequest request, Model model) {
        if (isApi(request)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Validation failed."));
        }
        return friendly(model, 400, "Validation failed", "Please check the submitted values and try again.");
    }

    private boolean isApi(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return uri.startsWith("/api/") || (accept != null && accept.contains("application/json"));
    }

    private String friendly(Model model, int status, String title, String message) {
        model.addAttribute("status", status);
        model.addAttribute("title", title);
        model.addAttribute("message", message);
        return "error-friendly";
    }
}
