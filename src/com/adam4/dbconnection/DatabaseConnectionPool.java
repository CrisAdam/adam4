package com.adam4.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

import com.adam4.common.Common;
import com.adam4.mylogger.MyLogger.LogLevel;

public class DatabaseConnectionPool
{
    private boolean isConnected;
    private int maxIdle = 3;
    private Queue<Connection> connections;
    private DatabaseConnectionInfo connectionInfo;

    /*
     * creates connections to a single database
     */
    public DatabaseConnectionPool(DatabaseConnectionInfo dbConInfo, int maxIdle)
    {
        this.connectionInfo = dbConInfo;
        connections = new LinkedList<>();
        connections.add(createConnection());
        this.maxIdle = maxIdle;
    }

    public Connection getConnection()
    {
        if (connections.isEmpty())
        {
            return createConnection();
        }
        return connections.poll();
    }

    private Connection createConnection()
    {
        Connection con = null;
        try
        {
            con = DriverManager.getConnection(connectionInfo.url, connectionInfo.user, connectionInfo.password);
            isConnected = true;
        }
        catch (SQLException e)
        {
            isConnected = false;
            // MyLogger.LogMessage(Thread.currentThread(),
            // "Connection pool was unable to create connection to " +
            // conInfo.url, LogLevel.INFO, e);
        }
        return con;
    }

    public void returnConnection(Connection c)
    {
        try
        {
            if (c.isClosed())
            {
                return;
            }
            else
            {

                if (connections.size() > maxIdle)
                {
                    c.close();
                }
                else
                {
                    connections.add(c);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    protected void finalize() throws Throwable
    {
        try
        {
            Common.log.logMessage("ConnectionPool was not cleanly closed", LogLevel.ERROR);
            close();
        }
        finally
        {
            super.finalize();
        }
    }

    void close() throws SQLException
    {
        for (Connection c : connections)
        {
            c.commit();
            c.close();
        }

    }

    public boolean isConnected()
    {
        return isConnected;
    }

}
