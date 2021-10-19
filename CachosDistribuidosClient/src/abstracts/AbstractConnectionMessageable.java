/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Paul
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abstracts;

import java.util.ArrayList;
import events.ClientServerConnectionMessageEvent;
import listeners.ClientServerConnectionMessageListener;

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
