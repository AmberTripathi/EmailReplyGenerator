package com.ambertripathi.AIReplyGenerator.service;

import com.ambertripathi.AIReplyGenerator.entity.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGeneratorService {
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApikey;

    private final WebClient webClientBuilder;

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder.build();
    }

    public String generateReply(EmailRequest emailRequest) {
        //prompt build
        String prompt = buildPrompt(emailRequest);
        //Craft the request
        Map<String, Object> requestBody = Map.of("contents", Map.of("Parts", Map.of("text", prompt)));
        //Do request and get response
        String response = webClientBuilder.post()
                .uri(geminiApiUrl + geminiApikey)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        //extract response and return
        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text").asText();
        }catch (Exception e) {
            return "Error" + e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content. Please don't generate subject line ");
        if (emailRequest.getTone() != null && emailRequest.getTone().isEmpty()) {
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone");
        }
        prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}
