package controllers;

import models.ChatMessage;
import models.ChatUser;
import models.TestUser;
import play.*;
import play.libs.Comet;
import play.mvc.*;

import play.twirl.api.Html;
import views.html.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableFuture;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("It's working!!", "http://i.giphy.com/KFfqWaxf8L3eE.gif"));
    }

    public Result secret() {
        return ok(index.render("wow such secret very hidden", "http://i.imgur.com/PLOEqVE.jpg"));
    }

    public Result addUser(String mail, String name, String password) {
        TestUser u = new TestUser(mail, name, password);
        u.save();
        return ok();
    }

    public Result sendMessage(String message) {
        String a = request().remoteAddress();
        ChatUser u;
        List<ChatUser> us = ChatUser.find.where()
                .ilike("identification", a)
                .findList();
        if (us.size() == 0) {
            u = new ChatUser(a);
            u.save();
        } else {
            u = us.get(0);
        }

        ChatMessage m = new ChatMessage(u, message);
        m.save();

        return renderMessages();
    }

    public Result changeName(String name) {
        String n = "";

        String a = request().remoteAddress();
        ChatUser u;
        List<ChatUser> us = ChatUser.find.where()
                .ilike("identification", a)
                .findList();
        if (us.size() == 0) {
            u = new ChatUser(a);
            u.save();
        } else {
            u = us.get(0);
        }

        u.name = name;
        u.save();

        return renderMessages();
    }

    public Result renderMessages() {
        String s = "";
        List<ChatMessage> ms = ChatMessage.find.all();

        for(ChatMessage m : ms) {
            s += m.sender.name + ": " + m.message + "<br>";
        }
        return ok(messages.render("Chat", s));
    }

    Comet comet = null;
    ArrayList<ChatConnection> connections = new ArrayList<ChatConnection>();

    public Result cometChat() {
        return ok(cometMessages.render("CometChat"));
    }

    public Result cometCheck() {
        return ok(messages.render("Connections", "" + connections.size()));
    }

    public Result getComet() {
        ChatConnection c = new ChatConnection(request().remoteAddress());

        Runnable r = new Runnable() {
            public void run() {
                connections.remove(c);
            }
        };
        Logger.debug("cometAdded");
        c.activate(r);

        connections.add(c);

        return ok(c.comet);
    }

    public Result cometSendMessage(String msg) {
        ChatUser u = ChatUser.getUser(request().remoteAddress());
        ChatMessage m = new ChatMessage(u, msg);
        m.save();

        for(ChatConnection c : connections) {
            c.send(m);
        }

        return ok();
    }

}