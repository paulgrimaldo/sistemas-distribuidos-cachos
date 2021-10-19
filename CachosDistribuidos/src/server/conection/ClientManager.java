/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.conection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.abstracts.AbstractConnectionListener;
import server.net.Client;
import server.events.ClientConnectionEvent;
import server.events.ClientSendProtocolEvent;
import server.listeners.ClientSendProtocolListener;

/**
 *
 * @author Paul
 */
public class ClientManager extends AbstractConnectionListener {

    private final ArrayList<ClientSendProtocolListener> protocolListeners;
    private final HashMap<Client, Thread> connectedClients;
    private final HashMap<Client, ClientDisconnectionTaskManager> disconnectedClients;

    public ClientManager() {
        protocolListeners = new ArrayList<>(10);
        connectionListeners = new ArrayList<>(10);
        disconnectedClients = new HashMap<>(10);
        connectedClients = new HashMap<>(10);
    }

    public synchronized void destroyClient(Client client) {
        connectedClients.remove(client).stop();
    }

    public void addNewClient(Client client) {
        if (clientExists(client)) {
            destroyClient(client);
            removeFromDisconnected(client);
            notifyOnClientReconnect(this, new ClientConnectionEvent(client, ClientConnectionEvent.ConnectionType.RECONNECTION));
        }

        Thread clientThread = new Thread() {
            @Override
            public void run() {
                boolean connectionOk = true;
                try {
                    client.createStreams();
                } catch (IOException ex) {
                    Logger.getLogger(ClientManager.class.getName()).log(Level.SEVERE, "Error creating streams", ex);
                }

                while (connectionOk) {
                    try {
                        String message = client.readMessage();
                        System.out.println(message);
                        notifyOnNewProtocol(new ClientSendProtocolEvent(client, message));
                    } catch (IOException ex) {
                        connectionOk = false;
                        notifyOnClientDisconnect(this, new ClientConnectionEvent(client, ClientConnectionEvent.ConnectionType.DISCONNECTION));
                    } catch (Exception ex) {
                        Logger.getLogger(ClientManager.class.getName()).log(Level.WARNING, null, ex);
                    }
                }

            }
        };

        System.out.println("Client " + client.getId() + " thread is created and running");
        connectedClients.put(client, clientThread);
        clientThread.start();
    }

    public ArrayList<Client> getClientsByID(ArrayList<String> clientsId) {
        ArrayList<Client> result = new ArrayList<>();

        clientsId.forEach((clientId) -> {
            connectedClients.keySet().stream().filter((connectedClient) -> (connectedClient.getId().equals(String.valueOf(clientId)))).forEachOrdered((connectedClient) -> {
                result.add(connectedClient);
            });
        });
        return result;
    }

    public void sendMessageToClients(ArrayList<Client> clients, String message) {
        try {
            for (Client client : clients) {
                client.writeMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se pudo enviar el mensaje");
        }
    }

    public boolean clientExists(Client client) {
        return connectedClients.keySet().contains(client);
    }

    public void setTimerForClientDisconnected(Client client) {
        ClientDisconnectionTaskManager clientDisconnectionTaskManager = new ClientDisconnectionTaskManager();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                notifyOnClientDisconnect(this, new ClientConnectionEvent(client, ClientConnectionEvent.ConnectionType.DESTROY));
                destroyClient(client);
                removeFromDisconnected(client);
            }
        };

        long delay = 10000;
        clientDisconnectionTaskManager.setTask(task);
        clientDisconnectionTaskManager.executeTask(delay);
        disconnectedClients.put(client, clientDisconnectionTaskManager);
    }

    public synchronized void removeFromDisconnected(Client client) {
        disconnectedClients.remove(client).cancel();
    }

    public void close() {
        try {
            for (Map.Entry<Client, Thread> entry : connectedClients.entrySet()) {
                Client key = entry.getKey();
                Thread value = entry.getValue();
                value.stop();
                destroyClient(key);
            }

            for (Map.Entry<Client, ClientDisconnectionTaskManager> entry : disconnectedClients.entrySet()) {
                Client client = entry.getKey();
                ClientDisconnectionTaskManager manager = entry.getValue();
                manager.cancel();
                removeFromDisconnected(client);

            }

        } catch (Exception e) {
            System.out.println("Eliminado");
        }

    }

    public void addProtocolListener(ClientSendProtocolListener listener) {
        protocolListeners.add(listener);
    }

    public void notifyOnNewProtocol(ClientSendProtocolEvent event) {
        for (int i = 0; i < protocolListeners.size(); i++) {
            protocolListeners.get(i).onReceiveProtocol(event);
        }
    }

}
