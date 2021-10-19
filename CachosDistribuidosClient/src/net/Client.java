/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import abstracts.AbstractConnectionMessageable;
import abstracts.AbstractSocketWriter;
import events.ClientServerConnectionMessageEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author Paul
 */
public class Client extends AbstractConnectionMessageable implements Serializable {

    private transient Socket clientSocket;
    private String name;
    private String id;
    private String createdAt;
    private transient ClientServerConnection clientServerConnectionThread;

    public String getName() {
        return name;
    }

    public Client(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.createdAt = "";
        this.messageListeners = new ArrayList<>();
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return clientSocket;
    }

    public void setSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    private void generateId() {
        if (id == null) {
            int randomId = (new Random()).nextInt(99999);
            String timeId = (new SimpleDateFormat("HH:mmm:ss")).format(new Date());
            this.id = String.valueOf(Math.abs(randomId * Objects.hashCode(String.valueOf(randomId).concat(timeId))));// read the id
        }
    }

    @Override
    public void connect() throws IOException {
        System.out.println("Client " + name + " trying to connect");

        clientSocket = new Socket(ip, port);

        generateId();

        System.out.println("Sendig id:" + id + " to the server");

        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        out.writeUTF(this.id);
        out.flush();

        clientServerConnectionThread = new ClientServerConnection(clientSocket);
        clientServerConnectionThread.start();
    }

    @Override
    public void disconnect() throws IOException {
        System.out.println("Client " + name + " trying to disconnect");

        if (clientSocket != null) {
            clientSocket.close();
        }
        if (clientServerConnectionThread != null) {
            clientServerConnectionThread.end();
        }
    }

    public void sendMessage(String message) throws IOException {
        clientServerConnectionThread.writeMessage(message);
    }

    @Override
    public String toString() {
        return "Client:{port:" + port + ", ip:" + ip + ", name:" + name + ",id:" + id + "}";
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.name);
        hash = 17 * hash + Objects.hashCode(this.id);
        hash = 17 * hash + Objects.hashCode(this.createdAt);
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
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    private final class ClientServerConnection extends Thread implements AbstractSocketWriter {

        private final Socket connectionSocket;
        private DataOutputStream outputStream;
        private DataInputStream inputStream;
        boolean connectionIsFine = true;

        private Timer tryToReconnectTask;
        private Timer pingTask;

        public ClientServerConnection(Socket socket) throws IOException {
            this.connectionSocket = socket;
            createStreams();
            tryToReconnectTask = new Timer("ReconnectTime");
            pingTask = new Timer("PingTimer");
        }

        @Override
        public void run() {
            makePing();
            while (connectionIsFine) {
                System.out.println("Tying to read again, Main Thread");
                try {
                    String serverMessage = readMessage();
                    System.out.println(serverMessage);
                    notifyOnSimpleMessage(new ClientServerConnectionMessageEvent(serverMessage));
                } catch (IOException ex) {
                    ///Cliente perdio la conexion al servidor
                    //stop the loop
                    connectionIsFine = false;
                    //cancel the ping
                    pingTask.cancel();
                    //report the error
                    String message = "Se perdio la conexion al servidor";
                    notifyOnSimpleMessage(new ClientServerConnectionMessageEvent(message));
                    System.out.println("ClientServerConnection@run:" + message);
                    // try to reconnect
                    tryToReconnect();
                }
            }
        }

        private void makePing() {
            long delay = 5L;
            long period = 2000L;

            final TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {

                        System.out.println("ClientServerConnection@makePing: Haciendo PING");
                        outputStream.writeUTF("PING");
                    } catch (IOException e) {
                        ///Log the error
                        System.out.println("ClientServerConnection@makePing: Error haciendo ping" + e.getMessage());
                        //stop the main loop
                        connectionIsFine = false;
                        //cancel the task
                        cancel();
                        // cancel the ping timer
                        pingTask.cancel();
                        //report the error
                        String message = "Perdi la conexion al servidor";
                        notifyOnSimpleMessage(new ClientServerConnectionMessageEvent(message));
                        tryToReconnect();
                    }
                }
            };

            pingTask.scheduleAtFixedRate(task, delay, period);
        }

        private void tryToReconnect() {
            long delay = 5L;
            long period = 2000L;

            final TimerTask task;
            task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        connect();
                        cancel();
                        ClientServerConnection.this.stop();
                    } catch (IOException e) {
                        String message = "Aun no se pudo conectar al servidor";
                        notifyOnSimpleMessage(new ClientServerConnectionMessageEvent(message));
                        System.out.println("ClientServerConnection@tryToReconnect:" + message);
                    }
                }
            };

            tryToReconnectTask.scheduleAtFixedRate(task, delay, period);
        }

        @Override
        public void createStreams() throws IOException {
            inputStream = new DataInputStream(connectionSocket.getInputStream());
            outputStream = new DataOutputStream(connectionSocket.getOutputStream());
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
                        cancel();
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

        private void end() {
            connectionIsFine = false;
            tryToReconnectTask.cancel();
            pingTask.cancel();
            stop();
        }

    }

}
