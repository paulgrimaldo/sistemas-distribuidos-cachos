/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.conection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.abstracts.AbstractConnectionListener;
import server.events.ClientConnectionEvent;
import server.net.Client;
import server.abstracts.AbstractSocketWriter;

/**
 *
 * @author Paul
 */
public class ConnectionCoordinator extends AbstractConnectionListener implements Runnable, AbstractSocketWriter {

    private boolean connectionState;
    private final Thread listenConnectionsThread;
    private final ServerSocket serverSocket;
    private Socket serviceSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ConnectionCoordinator(ServerSocket serverSocket) {
        connectionListeners = new ArrayList<>(20);
        listenConnectionsThread = new Thread(this);
        this.serverSocket = serverSocket;
        connectionState = true;

    }

    public void init() {
        listenConnectionsThread.start();
    }

    @Override
    public void run() {
        while (connectionState) {
            try {
                //Accept the client
                serviceSocket = serverSocket.accept();
                createStreams();
                //create the client
                String clientId = readMessage();
                System.out.println("Client id is " + clientId);
                Client client = new Client(serviceSocket.getInetAddress().getHostName(), serverSocket.getLocalPort(), clientId);
                client.setSocket(serviceSocket);
                //if success send the event to the server
                notifyOnClientConnect(this, new ClientConnectionEvent(client, ClientConnectionEvent.ConnectionType.CONNECTION));
            } catch (IOException ex) {
                invalidate();
            } catch (Exception ex) {
                Logger.getLogger(ConnectionCoordinator.class.getName()).log(Level.SEVERE, "Server can't handle connection with client", ex);
            }
        }

    }

    private void invalidate() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (serviceSocket != null && !serviceSocket.isClosed()) {
                serviceSocket.close();
            }
        } catch (IOException e) {
            Logger.getLogger(ConnectionCoordinator.class.getName()).log(Level.SEVERE, "Can't invalidate socket and stream", e);
        }

    }

    @Override
    public void writeMessage(String message) throws IOException {
        outputStream.writeUTF(message);
    }

    @Override
    public String readMessage() throws IOException {
        return inputStream.readUTF();
    }

    public void close() {
        connectionState = false;
        invalidate();
        listenConnectionsThread.stop();
    }

    @Override
    public void createStreams() throws IOException {
        inputStream = new DataInputStream(serviceSocket.getInputStream());
        outputStream = new DataOutputStream(serviceSocket.getOutputStream());
    }

}
