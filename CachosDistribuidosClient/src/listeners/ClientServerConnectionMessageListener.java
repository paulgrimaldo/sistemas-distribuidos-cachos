/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import events.ClientServerConnectionMessageEvent;

/**
 *
 * @author Paul
 */
public interface ClientServerConnectionMessageListener {

    public void onNewMessage(ClientServerConnectionMessageEvent event);
}
