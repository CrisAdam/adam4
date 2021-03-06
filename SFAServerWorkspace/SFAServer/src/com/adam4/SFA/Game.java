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
    public int getOpenSpots;
	public int gameID;

    public Game()
    {
        started = false;  // determines if in lobby state or not
        clients = new LinkedList<ClientHandler>();
        updatesPerSecond = 66; // starting with an optimistic number

    }

    boolean getStarted()
    {
        return started;
    }
    
    private void start()
    {
    	started = true;
    	physics = new Physics(this);
        physicsThread = new Thread(physics);
        physicsThread.start();
    }

    void moveObjects(long dTime)
    {
        // map.moveObjects(dTime * Server.timeConstant);
    }

    boolean getRunning()
    {
        return started;
    }

    public void addClients(ConcurrentLinkedQueue<ClientHandler> newClients)
    {
        for (ClientHandler c : newClients)
        {
            clients.add(c);
        }
    }

	public void endGame() 
	{
		started = false;
	}

}
