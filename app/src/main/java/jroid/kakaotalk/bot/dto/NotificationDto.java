package jroid.kakaotalk.bot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class NotificationDto {
    
    private String room;
    private String sender;
    private String message;
    private String image;
    private boolean isGroupChat;
    
}
