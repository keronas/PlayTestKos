package models;

import javax.persistence.*;
import com.avaje.ebean.*;

@Entity
public class TestUser extends Model {
    @Id
    public String email;
    public String name;
    public String password;

    public TestUser(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public static Finder<String, TestUser> find = new Finder<>(TestUser.class);
}
