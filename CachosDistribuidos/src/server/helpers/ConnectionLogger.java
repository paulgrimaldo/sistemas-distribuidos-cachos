/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.helpers;

import javax.swing.text.JTextComponent;

/**
 *
 * @author Paul
 */
public class ConnectionLogger {

    private JTextComponent txt;
    private static ConnectionLogger instance;

    private ConnectionLogger() {
    }

    public static ConnectionLogger getInstace() {
        if (instance == null) {
            instance = new ConnectionLogger();
        }
        return instance;
    }

    public void setComponent(JTextComponent component) {
        txt = component;
    }

    public void log(String message) {
        if (txt != null) {
            txt.setText(txt.getText() + "\n" + message);
        }
    }
}
