package com.adam4.mylogger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.adam4.mylogger.MyLogger.LogLevel;

public class DatabaseLogWriter implements iLogWriter
{
	private ConcurrentLinkedQueue<Error> errors;
	private Writer writer;
	private Thread writerThread;
	
	public DatabaseLogWriter(String connectionInfoDB)
	{
		errors = new ConcurrentLinkedQueue<>();
		writer = new Writer(errors, connectionInfoDB);
		writerThread = new Thread(writer);
		writerThread.start();
	}
	
	class Writer implements Runnable
	{
		private String[] params;
		private Connection con;
		private boolean done;
		
		Writer(ConcurrentLinkedQueue<Error> errors, String connectionInfoDB)
		{
			params = connectionInfoDB.split(";");
	        if (params.length != 3)
	        {
	        //	log.LogMessage(Thread.currentThread().getName(), "Invalid DB arguments " + params.length, LogLevel.ERROR);
	        	done = true;
	        }
	        else
	        {
	        	done = false;
	        }
	        
	        if (!connect())
	        {
	        	done = true;
	        }
		}
		
		private boolean connect()
		{
			try
	        {
	            con = DriverManager.getConnection(params[0], params[1], params[2]);
	            java.sql.Statement count = con.createStatement();
	            ResultSet result = count.executeQuery("SELECT COUNT(*) FROM `SFASchema`.`ErrorLoggingTable`;");
	            StringBuilder sb = new StringBuilder();
	            while (result.next())
	            {
	            	sb.append(result.getObject(1));
	            }
	    //        MyLogger.LogMessage(Thread.currentThread().getName(), "Logger was able to connect to database, " + sb.toString() + " Rows in Error DB" , LogLevel.DEBUG);
	            return true;
	        }
	        catch (Exception e)
	        {
	      //  	MyLogger.LogMessage(Thread.currentThread().getName(), "Logger was unable to connect to database", LogLevel.ERROR, e);
	        }
			return false;
		}
		
		private boolean writeError(Error error)
		{
			 try
	         {
	         	// INSERT INTO `SFASchema`.`ErrorLoggingTable` (`Time`, `ThreadName`, `Error`, `Level`, `StackTrace`) VALUES ('2014-06-25T17:29', 'Main', 'Test', 'DEBUG', 'sun.nio.fs.UnixException.translateToIOException(UnixException.java:88)');
	         	
	         	String SQLquery = " insert into `SFASchema`.`ErrorLoggingTable` (`Time`, `ThreadName`, `Error`, `Level`, `StackTrace`) values (?, ?, ?, ?, ?)";
	         	PreparedStatement preparedStmt = con.prepareStatement(SQLquery);
	         	// convert java.util.Date to java.sql.Date (double to long)
	         	preparedStmt.setDate(1, new Date(error.date.getTime()));
	         	preparedStmt.setString(2, error.thread);
	         	preparedStmt.setString(3, error.message);
	         	preparedStmt.setString(4, error.level.toString());
	         	preparedStmt.setString(5, error.trace);
	         	
	         	preparedStmt.execute();
	         	
	         	if (!con.getAutoCommit())
	         	{
	         		con.commit();
	         	}
	         	return true;
	         }
	         catch (SQLException e)
	         {
	      //  	 MyLogger.LogMessage(Thread.currentThread(), "Logger was unable to write to DB", LogLevel.INFO, e);
	        	 return false;
	         }
		}

		@Override
		public void run()
		{
			while(!done)
			{
				try
				{
					synchronized(writerThread)
					{
						while(!errors.isEmpty())
						{
							writeError(errors.poll());
						}
						writerThread.wait();
					}
				}
				catch(InterruptedException e)
				{
					
				}
			}
			
		}
		
	} // end Writer class
	
	
	
	public void close()
	{
		
	}

	public boolean addError(Error error)
	{
		// notify
		return errors.add(error);
	}


	
	
	
	protected void finalize() throws Throwable
    {
        try
        {
      //  	MyLogger.LogMessage(Thread.currentThread(), "DatabaseLogger was not cleanly closed", LogLevel.ERROR);
            close();
        }
        finally
        {
            super.finalize();
        }
    }

	@Override
	public void reconnect(ReAdd reAdd)
	{
		// todo: reconnect to DB and then call:
		reAdd.add(this);
	}
}
