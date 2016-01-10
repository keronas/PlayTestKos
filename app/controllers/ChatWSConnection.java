package controllers;

import models.ChatMessage;
import models.ChatUser;
import play.Logger;
import play.mvc.WebSocket;
import play.mvc.WebSocket.*;

import java.util.List;

public class ChatWSConnection {
    public WebSocket<String> socket;
    private Out<String> out = null;
    private ChatUser user;

    public ChatWSConnection(String identification) {
        user = ChatUser.getUser(identification);
    }

    public void activate(Runnable disconnect) {
        socket = WebSocket.whenReady((in, out) -> {
            in.onClose(() -> {
                Logger.debug("WebSocket Closed");
                disconnect.run();
            });
            out.write("connected to chat <br>");
            this.out = out;

            List<ChatMessage> ms = ChatMessage.find
                    .findList();
            for (ChatMessage m : ms) {
                send(m);
            }
        });
    }

    public void send(ChatMessage m ) {
        out.write(m.sender.name + ": " + m.message + "<br>");
    }

}
