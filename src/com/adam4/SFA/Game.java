package com.adam4.SFA;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Game
{
    private LinkedList<Client> clients;
    double updatesPerSecond;
    private boolean started;
    private Thread physicsThread;
    private Physics physics;
    private boolean paused;
    public int getOpenSpots;

    public Game()
    {
        started = false;
        paused = true;
        clients = new LinkedList<Client>();
        updatesPerSecond = 66; // starting with an optimistic number

        physics = new Physics(this);
        physicsThread = new Thread(physics);
        physicsThread.start();
    }

    boolean getStarted()
    {
        return started;
    }

    void moveObjects(long dTime)
    {
        // map.moveObjects(dTime * Server.timeConstant);
    }

    void endGame()
    {
        for (Client c : clients)
        {
            SFAServer.addClient(c);
        }
    }

    boolean getRunning()
    {
        return !paused;
    }

    public void addClients(ConcurrentLinkedQueue<Client> newClients)
    {
        for (Client c : newClients)
        {
            clients.add(c);
        }
    }

}
