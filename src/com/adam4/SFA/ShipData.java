package com.adam4.SFA;

import java.util.Date;

public class ShipData
{
	int userID;
	String shipName;
	int wins;
	int losses;
	int disconnects;
	int kills;
	int deaths;
	int teamKills;
	int teamDeaths;
	int asteroidCrashes;
	Date lastUpdate;
	String version;

	ShipData(int userID, String shipName)
	{
		this.userID = userID;
		this.shipName = shipName;
	}
}
