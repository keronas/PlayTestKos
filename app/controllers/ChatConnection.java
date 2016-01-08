package controllers;

import models.ChatMessage;
import models.ChatUser;
import play.Logger;
import play.libs.Comet;
import play.libs.F;

import java.util.List;

public class ChatConnection {
    public Comet comet;
    private ChatUser user;

    public ChatConnection(String identification) {

        user = ChatUser.getUser(identification);


    }

    public void activate(Runnable disconnect) {
        comet = new Comet("document.write") {
            public void onConnected() {
                List<ChatMessage> ms = ChatMessage.find
                        /*.where()
                        .ne("id", user.id)*/
                        .findList();
                for (ChatMessage m : ms) {
                    send(m);
                }

                comet.onDisconnected(new F.Callback0() {
                    public void invoke() throws Throwable {
                        Logger.debug("cometClosed");
                        disconnect.run();
                    }
                });


            }

            public void onDisconnected(F.Callback0 callback0) {
                Logger.debug(callback0.toString());
                super.onDisconnected(callback0);
            }
        };




    }

    public void send(ChatMessage m ) {
        comet.sendMessage(m.sender.name + ": " + m.message + "<br>");
    }

}
