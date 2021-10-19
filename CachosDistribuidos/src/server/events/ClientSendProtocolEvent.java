/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.events;

import java.util.EventObject;
import server.net.Client;

/**
 *
 * @author Paul
 */
public class ClientSendProtocolEvent extends EventObject {

    private Client sender;

    public ClientSendProtocolEvent(Client sender, Object source) {
        super(source);
        this.sender = sender;
    }

    public Client getSender() {
        return sender;
    }

    public void setSender(Client sender) {
        this.sender = sender;
    }
    
    
    
}   
