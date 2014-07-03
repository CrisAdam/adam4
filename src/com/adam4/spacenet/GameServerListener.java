package com.adam4.spacenet;

import java.net.ServerSocket;

import com.adam4.SFA.Server;
import com.adam4.mylogger.MyLogger;


/**
 * This allows game servers to connect
 * so that the spacenet can send players to them
 */
public class GameServerListener implements Runnable
{
	
	private Thread gameListenerThread;
	private ServerSocket serverSocket;
	
	GameServerListener() 
	{
        try
        {
            serverSocket = new ServerSocket();
            gameListenerThread = new Thread(this);
    		gameListenerThread.start();
        }
        catch (Exception e)
        {
        	SpaceNet.log.LogMessage(Thread.currentThread(), "Unable to open gameServer Listener socket", MyLogger.LogLevel.ERROR , e);
        }
	}
	
	private boolean authenticateGameServer()
	{
		return false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
