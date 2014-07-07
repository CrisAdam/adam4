package com.adam4.SFA;

import java.util.Date;

public class AbilityData
{
    int abilityID;
    String abilityName;
    int shipID;
    int wins;
    int losses;
    int disconnects;
    int kills;
    int deaths;
    int teamKills;
    int teamDeaths;
    int asteroidCrashes;
    Date lastUpdate;

    AbilityData(int shipID, String abilityName)
    {
        this.shipID = shipID;
        this.abilityName = abilityName;
    }
}
