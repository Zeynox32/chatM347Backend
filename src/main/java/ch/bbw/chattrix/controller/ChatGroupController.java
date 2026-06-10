package ch.bbw.chattrix.controller;

import ch.bbw.chattrix.dto.chatgroup.ChatGroupRequest;
import ch.bbw.chattrix.dto.message.MessageRequest;
import ch.bbw.chattrix.service.ChatGroupService;
import ch.bbw.chattrix.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat-group")
public class ChatGroupController {

    private final ChatGroupService chatGroupService;

    public ChatGroupController(ChatGroupService chatGroupService) {
        this.chatGroupService = chatGroupService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getChatGroup(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @PathVariable("id") String chatId) {
        try {
            return ResponseEntity.ok().body(chatGroupService.getChatGroup(chatId, authenticatedUser.id()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/metadata")
    public ResponseEntity<?> getAllChatGroupMetadata(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        try {
            return ResponseEntity.ok().body(chatGroupService.getAllMetadata(authenticatedUser.id()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createChatGroup(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestBody ChatGroupRequest newChatGroup) {
        try {
            return ResponseEntity.ok().body(chatGroupService.createChatGroup(newChatGroup, authenticatedUser.id()));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{chatGroupId}")
    public ResponseEntity<?> deleteChatGroup(
            @PathVariable String chatGroupId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        try {
            chatGroupService.deleteChatGroup(
                    chatGroupId,
                    authenticatedUser.id());

            return ResponseEntity.ok().build();

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }


    // TODO: Separate Controller
    @PostMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestBody MessageRequest message) {
        try {
            return ResponseEntity.ok().body(chatGroupService.sendMessage(message, authenticatedUser.id()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
