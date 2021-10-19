/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.conection;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Paul
 */
public class ClientDisconnectionTaskManager extends Timer {

    public TimerTask task;

    public TimerTask getTask() {
        return task;
    }

    public void setTask(TimerTask task) {
        this.task = task;
    }

    public void executeTask(long delay) {
        schedule(task, delay);
    }

    @Override
    public void cancel() {
        task.cancel();
        super.cancel(); //To change body of generated methods, choose Tools | Templates.
    }

}
