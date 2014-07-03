package com.adam4.SFA;

public interface iClientDataResource
{
	boolean playerExists(String playerName);
	PlayerData getPlayerData(String playerName);
	void setPlayerData(PlayerData playerData);
	
	boolean shipExists(String userID, String shipName);
	ShipData getShipData(String userID, String shipName);
	void setShipData(ShipData shipData);
	
	boolean abilityExists(int shipID, String abilityName);
	AbilityData getAbilityData(int shipID, String abilityName);
	void setAbilityData(AbilityData abilityData);
	
	
}


