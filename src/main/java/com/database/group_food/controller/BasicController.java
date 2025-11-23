// src/main/java/com/database/group_food/controller/BasicController.java
package com.database.group_food.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class BasicController {


    /**
     * 헬스 체크 (/api/health)
     * AWS나 클라우드 배포 시 로드밸런서가 서버 상태를 체크하는 용도
     */
    @GetMapping("/api/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("SERVER IS RUNNING OK");
    }
}