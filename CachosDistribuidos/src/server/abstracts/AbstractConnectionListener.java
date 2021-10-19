/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.abstracts;

import java.util.ArrayList;
import server.events.ClientConnectionEvent;
import server.listeners.ClientConnectionListener;

/**
 *
 * @author Paul
 */
public abstract class AbstractConnectionListener {

    protected ArrayList<ClientConnectionListener> connectionListeners;

    public void addConnectionListener(ClientConnectionListener clientConnectionListener) {
        connectionListeners.add(clientConnectionListener);
    }

    public void removeConnectionListener(ClientConnectionListener clientConnectionListener) {
        connectionListeners.remove(clientConnectionListener);
    }

    public void notifyOnClientConnect(Object dispatcher, ClientConnectionEvent clientConnectionEvent) {
        connectionListeners.forEach((mClientConnectionListener) -> {
            mClientConnectionListener.onClientConnect(dispatcher, clientConnectionEvent);
        });
    }

    public void notifyOnClientDisconnect(Object dispatcher, ClientConnectionEvent clientConnectionEvent) {
        connectionListeners.forEach((mClientConnectionListener) -> {
            mClientConnectionListener.onClientDisconnect(dispatcher, clientConnectionEvent);
        });
    }
     public void notifyOnClientReconnect(Object dispatcher, ClientConnectionEvent clientConnectionEvent) {
        connectionListeners.forEach((mClientConnectionListener) -> {
            mClientConnectionListener.onClientReconnect(dispatcher, clientConnectionEvent);
        });
    }
}
