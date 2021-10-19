/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.abstracts.AbstractSocketWriter;

/**
 *
 * @author Paul
 */
public class Client implements Serializable, AbstractSocketWriter {

    private int port;
    private String ip;
    private String connected_at;
    private transient Socket socket;
    private  String id;
    private transient DataInputStream inputStream;
    private transient DataOutputStream outputStream;

    public Client(String ip, int port, String id) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        this.connected_at = "";
    }

    public String getConnectedAt() {
        return connected_at;
    }

    public void setConnectedAt(String connected_at) {
        this.connected_at = connected_at;
    }

    public Socket getSocket() {
        return socket;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getConnected_at() {
        return connected_at;
    }

    public void setConnected_at(String connected_at) {
        this.connected_at = connected_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSocket(Socket clientSocket) {
        this.socket = clientSocket;
    }

    @Override
    public String toString() {
        return "Client:{port:" + port + ", ip:" + ip + ",id:" + id + "}";
    }

    @Override
    public void createStreams() throws IOException {
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void writeMessage(String message) throws IOException {
        final Timer timer = new Timer("Timer");
        long delay = 5L;

        final TimerTask task;
        task = new TimerTask() {
            @Override
            public void run() {
                try {
                    outputStream.writeUTF(message);
                    this.cancel();
                    timer.cancel();
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        timer.schedule(task, delay);
    }

    @Override
    public String readMessage() throws IOException {
        return inputStream.readUTF();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.port;
        hash = 17 * hash + Objects.hashCode(this.ip);
        hash = 17 * hash + Objects.hashCode(this.connected_at);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Client other = (Client) obj;
        if (this.port != other.port) {
            return false;
        }
        if (!Objects.equals(this.ip, other.ip)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
}
