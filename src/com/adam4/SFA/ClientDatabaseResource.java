package com.adam4.SFA;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.adam4.common.Common;
import com.adam4.dbconnection.DatabaseConnectionPool;
import com.adam4.mylogger.MyLogger;
import com.adam4.mylogger.MyLogger.LogLevel;

public class ClientDatabaseResource implements iClientDataResource
{
    DatabaseConnectionPool databaseConnections;

    public ClientDatabaseResource(DatabaseConnectionPool connectionPool)
    {
        databaseConnections = connectionPool;
    }

    @Override
    public boolean playerExists(String playerName)
    {
        try
        {
            Connection con = databaseConnections.getConnection();
            java.sql.Statement existsQuery;
            existsQuery = con.createStatement();

            ResultSet existsResult = existsQuery.executeQuery("SELECT COUNT(*) from SFASchema.UsersTable where UserName=\"" + playerName + "\";");
            return (existsResult.getInt(1) == 1);

        }
        catch (SQLException e)
        {
            Common.log.LogMessage(e, LogLevel.ERROR);
            return false;
        }
    }

    @Override
    public PlayerData getPlayerData(String playerName)
    {
        return null;
    }

    @Override
    public void setPlayerData(PlayerData playerData)
    {
        try
        {
            Connection con = databaseConnections.getConnection();
            java.sql.Statement count;

            count = con.createStatement();
            count.executeQuery("SELECT * from SFASchema.UsersTable where UserName=\"" + "" + "\";");
        }
        catch (SQLException e)
        {
            Common.log.LogMessage(e, LogLevel.ERROR);
        }

    }

    @Override
    public ShipData getShipData(String playerName, String ShipName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setShipData(ShipData shipData)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public AbilityData getAbilityData(int shipID, String abilityName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAbilityData(AbilityData abilityData)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean shipExists(String playerName, String ShipName)
    {
        try
        {
            Connection con = databaseConnections.getConnection();
            java.sql.Statement existsQuery;
            existsQuery = con.createStatement();

            ResultSet existsResult = existsQuery.executeQuery("SELECT COUNT(*) from SFASchema.UsersTable where UserName=\"" + playerName + "\";");
            return (existsResult.getInt(1) == 1);

        }
        catch (SQLException e)
        {
            Common.log.LogMessage(e, LogLevel.ERROR);
            return false;
        }
    }

    @Override
    public boolean abilityExists(int shipID, String abilityName)
    {
        try
        {
            Connection con = databaseConnections.getConnection();
            java.sql.Statement existsQuery;
            existsQuery = con.createStatement();

            ResultSet existsResult = existsQuery.executeQuery("SELECT COUNT(*) from SFASchema.AbilityTable where ShipID=\"" + shipID + "\";");
            return (existsResult.getInt(1) == 1);

        }
        catch (SQLException e)
        {
            Common.log.LogMessage(e, LogLevel.ERROR);
            return false;
        }
    }

}
