package com.example.demo.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController 
@RequestMapping("/vrajesh")
public class MyController {

    @GetMapping("/public")
    public String publicMethod() {
        return new String("Vrajesh");
    }

    @GetMapping("/private")
    public String privateMethod() {
        return new String("Vaghasiya");
    }
}
