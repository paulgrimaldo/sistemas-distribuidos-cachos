/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.abstracts;

import java.util.ArrayList;
import server.events.ClientServerConnectionMessageEvent;
import server.listeners.ClientServerConnectionMessageListener;

/**
 *
 * @author Paul
 */
public abstract class AbstractConnectionMessageable extends AbstractConnection {

    protected ArrayList<ClientServerConnectionMessageListener> messageListeners;
    public void addSimpleMessageListener(ClientServerConnectionMessageListener listener) {
        messageListeners.add(listener);
    }
    public void removeFromMessageListener(ClientServerConnectionMessageListener listener) {
        messageListeners.remove(listener);
    }
    public void notifyOnSimpleMessage(ClientServerConnectionMessageEvent evt) {
        messageListeners.forEach((simpleMessageListener) -> {
            simpleMessageListener.onNewMessage(evt);
        });
    }
}
