/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.abstracts;

import java.io.IOException;

/**
 *
 * @author Paul
 */
public interface AbstractSocketWriter {

    public void createStreams()throws IOException;

    public void writeMessage(String message) throws IOException;

    public String readMessage() throws IOException;

}
