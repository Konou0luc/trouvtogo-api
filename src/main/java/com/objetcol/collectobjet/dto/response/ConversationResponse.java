package com.objetcol.collectobjet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private Long id;
    private Long itemId;
    private ObjetResponse item;
    private Long otherUserId;
    private UserPublicResponse otherUser;
    private LastMessageResponse lastMessage;
    private int unreadCount;
    private int matchScore;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LastMessageResponse {
        private String content;
        private LocalDateTime createdAt;
        private boolean isFromMe;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPublicResponse {
        private Long id;
        private String name;
        private String avatarUrl;
    }
}
