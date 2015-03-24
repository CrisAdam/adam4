package com.adam4.SFA;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Game
{
    public LinkedList<ClientHandler> clients;
    double updatesPerSecond;
    private boolean started;
    private Thread physicsThread;
    private Physics physics;
    private boolean paused;
    public int getOpenSpots;
	public int gameID;

    public Game()
    {
        started = false;
        paused = true;
        clients = new LinkedList<ClientHandler>();
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
        for (ClientHandler c : clients)
        {
            SFAServer.addPlayer(c);
        }
    }

    boolean getRunning()
    {
        return !paused;
    }

    public void addClients(ConcurrentLinkedQueue<ClientHandler> newClients)
    {
        for (ClientHandler c : newClients)
        {
            clients.add(c);
        }
    }

}
