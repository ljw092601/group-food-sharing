// src/main/java/com/database/group_food/controller/PageController.java
package com.database.group_food.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // RestController 아님! HTML을 반환하는 Controller
public class PageController {

    // 메인 페이지 (지도 화면)
    @GetMapping("/")
    public String home() {
        return "index"; // templates/index.html을 찾아감
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // 회원가입 페이지
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    // 글쓰기 페이지
    @GetMapping("/post/write")
    public String writePost() {
        return "write-post";
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String myPage() {
        return "my-page";
    }
}