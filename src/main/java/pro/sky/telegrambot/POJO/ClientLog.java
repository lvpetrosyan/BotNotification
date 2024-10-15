package pro.sky.telegrambot.POJO;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "client_log")
public class ClientLog {

    @GeneratedValue
    @Id
    @NonNull
    private int id;

    @Column(name = "client_id")
    @NonNull
    private int clientId;

    @Column(name = "chat_id")
    @NonNull
    private int chatId;

    public ClientLog (int id, int clientId, int chatId){
        this.id=id;
        this.clientId=clientId;
        this.chatId=chatId;
    }

    public ClientLog() {

    }
}
