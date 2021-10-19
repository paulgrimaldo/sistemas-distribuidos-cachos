/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.events;

import java.util.EventObject;

/**
 *
 * @author Paul
 */
public class ClientConnectionEvent extends EventObject {

    public enum ConnectionType {
        CONNECTION,
        DISCONNECTION,
        RECONNECTION,
        DESTROY
    }

    private ConnectionType state;

    public ClientConnectionEvent(Object source, ConnectionType state) {
        super(source);
        this.state = state;
    }

    public ConnectionType getState() {
        return state;
    }
    
    
}
