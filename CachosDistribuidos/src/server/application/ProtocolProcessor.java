/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.application;

import java.util.ArrayList;

/**
 *
 * @author Paul
 */
public class ProtocolProcessor {

    private Protocol protocol;

    public ProtocolProcessor() {
    }

    public void setContent(String protocol) {
        this.protocol = new Protocol(protocol);
    }

    public ProtocolProcessor(String protocol) {
        this.protocol = new Protocol(protocol);
    }

    public void process() {
        protocol.process();
    }

    public int getCode() {
        return protocol.getCode();
    }

    public ArrayList<String> getData() {
        return protocol.getData();
    }

    public String getMessage() {
        return protocol.getMessage();
    }
}
