package org.example.templatebackend.chat.controller;

import org.example.templatebackend.chat.service.ChatService;
import org.example.templatebackend.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public ResponseEntity<?> getChat(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestParam("chat-id") String chatId) {
        try {
            System.out.println("User " + authenticatedUser.id() + " is trying to access chat " + chatId);
            return ResponseEntity.ok().body(chatService.getChat(Integer.parseInt(chatId), authenticatedUser.id()));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
