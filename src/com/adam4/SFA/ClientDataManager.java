package com.adam4.SFA;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientDataManager
{

	private PlayerData playerData;
	//ConcurrentLinkedQueue<iClientDataResource> dataSources = Server.getClientDataResources();
	
	iClientDataResource dataSource;
	
	public ClientDataManager(String name)
	{
		if (dataSource.playerExists(name))
		{
			playerData = dataSource.getPlayerData(name);
		}
	}

	public PlayerData getPlayerData(String name)
	{
		return playerData;
	}

	public void setPlayerData(PlayerData stats)
	{
		playerData = stats;
		// TODO update database with new info asynchronously 
	}
	
}
