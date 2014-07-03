package com.adam4.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

import com.adam4.mylogger.MyLogger;
import com.adam4.mylogger.MyLogger.LogLevel;

public class DatabaseConnectionPool
{
	private int maxIdle = 3;
	private Queue<Connection> connections;
	private DatabaseConnectionInfo[] connectionInfo;
	
	public DatabaseConnectionPool(DatabaseConnectionInfo[] connectionInfo, int maxIdle) throws Exception
	{
		this.connectionInfo = connectionInfo;
		connections = new LinkedList<>();
		connections.add(createConnection());
		this.maxIdle = maxIdle;
	}
	
	public DatabaseConnectionPool(String parms, int maxIdle) throws Exception
	{
		this.connectionInfo = new DatabaseConnectionInfo[1];
		String[] params = parms.split(";");
        if (params.length != 3)
        {
        	//MyLogger.LogMessage(Thread.currentThread().getName(), "Invalid DB arguments " + params.length, LogLevel.ERROR);
        	return;
        }
		connectionInfo[0] = new DatabaseConnectionInfo(params[0], params[1], params[2]);
		connections = new LinkedList<>();
		connections.add(createConnection());
		this.maxIdle = maxIdle;
	}

	public DatabaseConnectionPool(DatabaseConnectionInfo dbConInfo, int maxIdle) 
	{
		this.connectionInfo = new DatabaseConnectionInfo[1];
		connectionInfo[0] = new DatabaseConnectionInfo(dbConInfo.url, dbConInfo.user, dbConInfo.password);
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
	
	private void findBestConnection()
	{
		//Process proc = Runtime.getRuntime().exec("ping " + ip);
	}
	
	private Connection createConnection()
	{
		Connection con = null;
		// todo: randomize or something to avoid making connections to the
		for (DatabaseConnectionInfo conInfo : connectionInfo)
		{
			try
	        {
				con = DriverManager.getConnection(conInfo.url, conInfo.user, conInfo.password);
			}
			 catch (SQLException e)
	        {
	        //	MyLogger.LogMessage(Thread.currentThread(), "Connection pool was unable to create connection to " + conInfo.url, LogLevel.INFO, e);
	        }
			if (con != null)
			{
				break;
			}
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
     //   	MyLogger.LogMessage(Thread.currentThread(), "ConnectionPool was not cleanly closed", LogLevel.ERROR);
            close();
        }
        finally
        {
            super.finalize();
        }
    }

	private void close() throws SQLException
	{
		for (Connection c : connections)
		{
			c.commit();
			c.close();
		}
		
	}
	

	
}
