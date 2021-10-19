/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.listeners;

import server.events.ClientTimeToWaitFinishEvent;

/**
 *
 * @author Paul
 */
public interface ClientTimeToWaitFinishListener {

    public void onTimeToWaitFinish(ClientTimeToWaitFinishEvent evt);
}
