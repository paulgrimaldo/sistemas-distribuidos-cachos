/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.listeners;

import server.events.ClientConnectionEvent;

/**
 *
 * @author Paul
 */
public interface ClientConnectionListener {

    public void onClientConnect(Object dispatcher,ClientConnectionEvent evt);
    public void onClientDisconnect(Object dispatcher,ClientConnectionEvent evt);
    public void onClientReconnect(Object dispatcher,ClientConnectionEvent evt);
    
}
