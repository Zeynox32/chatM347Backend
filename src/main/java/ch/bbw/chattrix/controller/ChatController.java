package ch.bbw.chattrix.controller;

import ch.bbw.chattrix.dto.chat.ChatGroupRequest;
import ch.bbw.chattrix.dto.chat.ChatRequest;
import ch.bbw.chattrix.service.ChatService;
import ch.bbw.chattrix.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "${FRONTEND_URL}", allowCredentials = "true")
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
        try {
            return ResponseEntity.ok().body(chatService.getAllMetadata(authenticatedUser.id()));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> addChat(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestBody ChatGroupRequest newChat) {
        try {
            ResponseEntity<?> xy = ResponseEntity.ok().body(chatService.addChat(newChat, authenticatedUser.id()));
            return xy;

        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestBody ChatRequest message) {
        try {
            return ResponseEntity.ok().body(chatService.sendMessage(message, authenticatedUser.id()));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
