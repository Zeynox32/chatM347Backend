package org.example.templatebackend.chat.controller;

import org.example.templatebackend.chat.dto.AddChatDTO;
import org.example.templatebackend.chat.dto.SendMessageDTO;
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
            return ResponseEntity.ok().body(chatService.getChat(chatId, authenticatedUser.id()));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all-metadata")
    public ResponseEntity<?> getAllChatMetadata(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
        try {
            return ResponseEntity.ok().body(chatService.getAllMetadata(authenticatedUser.id()));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> addChat(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestBody AddChatDTO newChat) {
        try {
            System.out.println("ooooooooooooooooooooooooooooooooooooooooooooooooooooo");

            ResponseEntity<?> xy = ResponseEntity.ok().body(chatService.addChat(newChat, authenticatedUser.id()));

            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

            return xy;

        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestBody SendMessageDTO message) {
        try {
            return ResponseEntity.ok().body(chatService.sendMessage(message, authenticatedUser.id()));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
