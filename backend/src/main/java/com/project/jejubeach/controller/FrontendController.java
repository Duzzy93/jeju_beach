package com.project.jejubeach.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    /**
     * Vue.js SPA의 모든 라우트를 처리
     * 클라이언트 사이드 라우팅을 위해 index.html을 반환
     */
    @GetMapping(value = {
        "/",
        "/login",
        "/home",
        "/beach-management",
        "/beach-detail/**",
        "/beach-crowd",
        "/beach-crowd/**",
        "/chatbot",
        "/admin",
        "/ai-model-status"
    })
    public String serveFrontend() {
        return "forward:/index.html";
    }
}
