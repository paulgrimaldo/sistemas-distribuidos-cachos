/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import org.json.JSONException;
import server.abstracts.AbstractConnectionMessageable;
import server.application.ProtocolProcessor;
import server.conection.ClientManager;
import server.conection.ConnectionCoordinator;
import server.events.ClientConnectionEvent;
import server.events.ClientConnectionEvent.ConnectionType;
import server.events.ClientSendProtocolEvent;
import server.events.ClientServerConnectionMessageEvent;
import server.listeners.ClientConnectionListener;
import server.listeners.ClientSendProtocolListener;

/**
 *
 * @author Paul
 */
public class Server extends AbstractConnectionMessageable implements ClientConnectionListener, ClientSendProtocolListener {

    private ServerSocket serverSocket;
    private ConnectionCoordinator connectionCoordinator;
    private final ClientManager clientManager;
    private ProtocolProcessor protocolProcessor;

    public Server(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.protocolProcessor = new ProtocolProcessor();
        this.clientManager = new ClientManager();
        this.clientManager.addConnectionListener((ClientConnectionListener) this);
        this.messageListeners = new ArrayList<>();
    }

    @Override
    public void connect() throws IOException {
        System.out.println("Server initialized");
        InetAddress inetAddress = InetAddress.getByName(ip);
        serverSocket = new ServerSocket(port, 50, inetAddress);
        connectionCoordinator = new ConnectionCoordinator(serverSocket);
        connectionCoordinator.addConnectionListener(this);
        connectionCoordinator.init();
        clientManager.addProtocolListener(this);
    }

    @Override
    public void disconnect() throws IOException {
        System.out.println("Server closing");
        clientManager.close();
        connectionCoordinator.close();
        serverSocket.close();
    }

    @Override
    public void onClientConnect(Object dispatcher, ClientConnectionEvent evt) {
        Client client = (Client) evt.getSource();
        String message = "User " + client.getId() + " connected: ";
        System.out.println(message);
        notifyOnSimpleMessage(new ClientServerConnectionMessageEvent(message));
        clientManager.addNewClient(client);

    }

    @Override
    public void onClientDisconnect(Object dispatcher, ClientConnectionEvent evt) {
        Client disconnectedClient = (Client) evt.getSource();
        String message = "";
        switch (evt.getState()) {
            case DISCONNECTION: {
                message = "User " + disconnectedClient.getId() + " disconnected: ";
                System.out.println(message);
                clientManager.setTimerForClientDisconnected(disconnectedClient);
                break;
            }
            case DESTROY: {
                message = "Closing session of user " + disconnectedClient.getId();
                System.out.println(message);
                break;
            }
        }

        notifyOnSimpleMessage(new ClientServerConnectionMessageEvent(message));
    }

    @Override
    public void onClientReconnect(Object dispatcher, ClientConnectionEvent evt) {
        Client reconnectedClient = (Client) evt.getSource();
        String message = "User " + reconnectedClient.getId() + " reconnected: ";
        System.out.println(message);
        notifyOnSimpleMessage(new ClientServerConnectionMessageEvent(message));
    }

    @Override
    public void onReceiveProtocol(ClientSendProtocolEvent evt) {
        //parsear el mensaje 
        String message = (String) evt.getSource();
        if (message.equals("PING")) {
            Client client = evt.getSender();
            notifyOnSimpleMessage(new ClientServerConnectionMessageEvent("Client " + client.getId() + " " + message + " me"));
        } else {
            try {
                protocolProcessor.setContent((String) evt.getSource());
                protocolProcessor.process();
                if (protocolProcessor.getCode() == 1) {//send message
                    ArrayList<String> clientsId = protocolProcessor.getData();
                    ArrayList<Client> clients = clientManager.getClientsByID(clientsId);
                    clientManager.sendMessageToClients(clients, protocolProcessor.getMessage());
                }
            } catch (JSONException e) {
                System.out.println("Server@onReceiveProtocol Error: Is not a json");
                notifyOnSimpleMessage(new ClientServerConnectionMessageEvent(message));
            } catch (Exception e) {
                System.out.println("Server@onReceiveProtocol Error: " + e.getMessage());
                notifyOnSimpleMessage(new ClientServerConnectionMessageEvent(message));
            }
        }

    }

}
