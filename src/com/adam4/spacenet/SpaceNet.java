package com.adam4.spacenet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;

import com.adam4.SFA.ClientDatabaseResource;
import com.adam4.common.DatabaseConnectionPool;
import com.adam4.mylogger.ConsoleLogWriter;
import com.adam4.mylogger.DatabaseLogWriter;
import com.adam4.mylogger.FileLogWriter;
import com.adam4.mylogger.MyLogger;

public class SpaceNet 
{

	private static final int maxIdleClientConnections = 3;
	 //class variables
	static DatabaseConnectionPool clientDatabasePool;
	static DatabaseConnectionPool gameServerDatabasePool;
	static MyLogger log = new MyLogger("Server", "SpaceNet");
	
	public static void main (String[] args) throws Exception
	{
	   	if (!handleCLI(args))
    	{
    		// do not run program if it is given invalid arguments
    		return;
    	}
	}
	
	
	
	private static boolean handleCLI(String[] args) throws Exception
	{
    	// return false and end program if invalid input is provided
    	for (int i = 0; i < args.length; ++i)
    	{
    		switch (args[i].toLowerCase())
    		{
    		case "-ltc":
    		case "-c":
    		case "-console":
    			log.addLogWriter(new ConsoleLogWriter());
    			break;
    		case "-ldb":
    		case "-loggingdatabase":
    			log.addLogWriter(new DatabaseLogWriter(args[++i]));
    			break;
    		case "-lf":
    		case "-loggingfile":
    			String logFilePath = args[++i];
    			if(!new File(logFilePath).isAbsolute())
    			{
    				logFilePath = System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + logFilePath;
    			}
    			log.addLogWriter(new FileLogWriter(logFilePath));
    			break;
    		case "-cdb":
    		case "-clientdatabase":
    			clientDatabasePool = new DatabaseConnectionPool(args[++i], maxIdleClientConnections);
    			break;
    		case "-sdb":
    		case "-serverdatabase":
    			gameServerDatabasePool = new DatabaseConnectionPool(args[++i], maxIdleClientConnections);
    			break;
    		case "-r":
    		case "-run":
    			String runFilePath = args[++i];
    			if(!new File(runFilePath).isAbsolute())
    			{
    				runFilePath = System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + runFilePath;
    			}
    			break;
    		case "-h" :
    		case "-help":
    		default: 
    			printUsage();
    			return false;
    		}
    	}
		return true;
	} // end CLI processing
	
	private static void printUsage() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + "resources" + FileSystems.getDefault().getSeparator() + "spacenetUsage.txt"));
		 String line = null;
		 while ((line = br.readLine()) != null) 
		 {
		   System.out.println(line);
		 }
		 br.close();
	}
}
