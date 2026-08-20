package com.example.demo.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.service.JwtService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {

        return jwtService.generateToken(username);
    }

    @GetMapping("/test")
    public String test() {
        return "AuthController is working!";
    }
}