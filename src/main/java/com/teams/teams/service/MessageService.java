package com.teams.teams.service;

import com.teams.teams.domain.Message;
import com.teams.teams.dto.MessageDto;
import com.teams.teams.dto.CreateMessageRequest;
import com.teams.teams.dto.UpdateMessageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {
    MessageDto createMessage(CreateMessageRequest request, Long senderId);
    MessageDto getMessageById(Long id);
    MessageDto updateMessage(Long id, UpdateMessageRequest request, Long userId);
    void deleteMessage(Long id, Long userId);
    Page<MessageDto> getChannelMessages(Long channelId, Pageable pageable);
    Page<MessageDto> getConversationMessages(Long conversationId, Pageable pageable);
    Page<MessageDto> getMessageReplies(Long messageId, Pageable pageable);
    Page<MessageDto> searchMessages(String query, Pageable pageable);
    MessageDto toMessageDto(Message message);
}

