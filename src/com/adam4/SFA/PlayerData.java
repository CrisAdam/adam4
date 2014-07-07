package com.adam4.SFA;

import java.util.Date;

public class PlayerData
{
    int userID;
    String userName;
    String hashedPassword;
    int Rating;
    Date JoinDate;
    Date LastLogin;
    int wins;
    int losses;
    int disconnects;
    int kills;
    int deaths;
    int teamKills;
    int teamDeaths;
    int asteroidCrashes;
    Date lastUpdate;

    PlayerData(String name)
    {
        userName = name;
    }
}
