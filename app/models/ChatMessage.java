package models;

import javax.persistence.*;
import com.avaje.ebean.*;

@Entity
public class ChatMessage extends Model {
    @Id
    public Integer id;
    public String message;
    @ManyToOne
    public ChatUser sender;

    public ChatMessage(ChatUser sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public static Finder<Integer, ChatMessage> find = new Finder<>(ChatMessage.class);
}

