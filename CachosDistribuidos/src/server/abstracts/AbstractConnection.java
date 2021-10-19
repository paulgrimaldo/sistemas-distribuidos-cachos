/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.abstracts;

import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Paul
 */
public abstract class AbstractConnection implements Serializable {

    protected int port;
    protected String ip;

    public abstract void connect() throws IOException;
    public abstract void disconnect() throws IOException;

   
}
