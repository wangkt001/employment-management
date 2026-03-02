package com.employment.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @PostMapping
    public String testPost(@RequestBody TestRequest request) {
        System.out.println("收到测试请求: " + request.toString());
        return "测试成功";
    }

    public static class TestRequest {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "TestRequest{" +
                    "message='" + message + '\'' +
                    '}';
        }
    }
}