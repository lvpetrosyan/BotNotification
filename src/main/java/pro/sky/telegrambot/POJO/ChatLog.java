package pro.sky.telegrambot.POJO;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "message_log")
public class ChatLog {

    @GeneratedValue
    @Id
    @NonNull
    private int id;

    @Column(name = "chat_id")
    @NonNull
    private int chatId;

    @Column(name = "text_message")
    private String textMessage;

    @Column(name = "date")
    private LocalDateTime dateMessage;

public ChatLog(int id, int chatId, String textMessage, LocalDateTime dateMessage){
    this.chatId=chatId;
    this.textMessage=textMessage;
    this.dateMessage=dateMessage;
    this.id=id;
}

    public ChatLog() {

    }


}