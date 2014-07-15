package com.adam4.irc;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Channel
{
    // channel names must start with a #
    private String channelName;
    private int maxNumClients = 10;
    private ConcurrentLinkedQueue<Client> clients;

    public Channel(String name, Collection<Client> clients)
    {
        this.clients = new ConcurrentLinkedQueue();
        this.clients.addAll(clients);
        channelName = name;
    }

    public Channel(String name, Client client)
    {
        this.clients = new ConcurrentLinkedQueue();
        this.clients.add(client);
        channelName = name;
    }

    public Channel(String name)
    {
        this.clients = new ConcurrentLinkedQueue();
        channelName = name;
    }

    public String getName()
    {
        return channelName;
    }

    public boolean addClient()
    {
        return false;
    }

    public void recieveMessage(String message)
    {

    }

}
