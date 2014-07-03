package com.adam4.SFA;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.adam4.mylogger.MyLogger.LogLevel;
import com.adam4.mylogger.MyLogger;

class InputCommands
{
	Point screenCenter;
	Point targetLocation;  // mouse position
	char[] changedKeys;
}

public class Client
{
	private PlayerData stats;
	private ClientHandler clientHandler;
	private ClientDataManager clientDataManager;
	Socket connection;
	
	ConcurrentLinkedQueue<InputCommands> inputCommands;

	Client(Socket connection)
	{
		clientHandler = new ClientHandler(connection, this);
	}
	
	boolean logIn(String name, String hashedPassword)
	{
		if (clientDataManager == null)
		{
			clientDataManager = new ClientDataManager(name);
		}
		stats = clientDataManager.getPlayerData(name);
		if (stats.hashedPassword.length() < 20)
		{
			stats.hashedPassword = hashedPassword;
			clientDataManager.setPlayerData(stats);
		}
		return (hashedPassword.equals(stats.hashedPassword));
	}

	void disconnect(String reason)
	{
		try
		{
			Network.sendError(connection, reason);
			connection.close();
		}
		catch (IOException e)
		{
			Server.log.LogMessage(Thread.currentThread(),  "Client unable to disconnect", LogLevel.WARN, e);
		}
	}

	public Player getPlayer()
	{
		// TODO Auto-generated method stub
		return null;
	}

} // end Player
