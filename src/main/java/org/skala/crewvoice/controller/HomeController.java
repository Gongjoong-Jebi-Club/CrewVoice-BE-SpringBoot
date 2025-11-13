package org.skala.crewvoice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 루트 페이지 확인용 텍스트
@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "CrewVoice Server - Running on Port 8080";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}