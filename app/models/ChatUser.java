package models;

import javax.persistence.*;
import com.avaje.ebean.*;

import java.util.List;

@Entity
public class ChatUser extends Model {
    @Id
    public Integer id;
    public String identification;
    public String name;

    public ChatUser(String identification) {
        this.identification = identification;
        name = "user" + ChatUser.find.findRowCount();
    }

    public static Finder<Integer, ChatUser> find = new Finder<>(ChatUser.class);

    public static ChatUser getUser(String ident){
        ChatUser u;
        List<ChatUser> us = find.where()
                .ilike("identification", ident)
                .findList();
        if (us.size() == 0) {
            u = new ChatUser(ident);
            u.save();
        } else {
            u = us.get(0);
        }
        return u;
    }
}
