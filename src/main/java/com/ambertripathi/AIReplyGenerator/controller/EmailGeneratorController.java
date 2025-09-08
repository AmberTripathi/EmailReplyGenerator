package com.ambertripathi.AIReplyGenerator.controller;

import com.ambertripathi.AIReplyGenerator.entity.EmailRequest;
import com.ambertripathi.AIReplyGenerator.service.EmailGeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
public class EmailGeneratorController {

    private final EmailGeneratorService emailGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest) {
        String response = emailGeneratorService.generateReply(emailRequest);
        return ResponseEntity.ok(response);
    }
}
