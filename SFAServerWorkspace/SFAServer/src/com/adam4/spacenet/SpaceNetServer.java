package com.adam4.spacenet;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.adam4.common.BlockOnRunFile;
import com.adam4.common.Common;
import com.adam4.common.SeparatedURL;
import com.adam4.dbconnection.DatabaseConnectionInfo;
import com.adam4.dbconnection.DatabaseConnectionManager;
import com.adam4.dbconnection.DatabaseConnectionPool;
import com.adam4.irc.Channel;
import com.adam4.irc.Client;
import com.adam4.mylogger.ConsoleLogWriter;
import com.adam4.mylogger.DatabaseLogWriter;
import com.adam4.mylogger.FileLogWriter;

public class SpaceNetServer
{
    // constants
    public final static String version = "1.0";
    private static final int maxIdleClientConnections = 3;
    public static final int ENDCHECKFREQUENCY = 5000; // polling frequency in ms for network to close socket

    // class variables
    private static DatabaseConnectionManager clientDatabaseManager; // used for player name/password lookups

    // private variables
    private static String runFilePath = System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + "SpaceNetServer.run";
    private static ClientListener clientListener;
  //  private static ServerListener serverListener;
    private static ConcurrentLinkedQueue<ClientHandler> connectedClients;
 //   private static ConcurrentLinkedQueue<ServerHandler> connectedServers;
    private static boolean isMaster = false;
    private static boolean isConnectedToMajority = false;
    private static ConcurrentLinkedQueue<Channel> channels;

    public static void main(String[] args) throws Exception
    {
        Common.log.setApplication("SpaceNet");
        if (!handleCLI(args))
        {
            // do not run program if it is given invalid arguments
            return;
        }
        connectedClients = new ConcurrentLinkedQueue();

        BlockOnRunFile block = new BlockOnRunFile(runFilePath);
        block.block();

        Common.log.close();

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
                Common.log.addLogWriter(new ConsoleLogWriter());
                break;
            case "-ldb":
            case "-loggingdatabase":
                Common.log.addLogWriter(new DatabaseLogWriter(new DatabaseConnectionManager(new DatabaseConnectionPool(new DatabaseConnectionInfo(args[++i]), maxIdleClientConnections), new LinkedList<SeparatedURL>())));
                break;
            case "-lf":
            case "-loggingfile":
                String logFilePath = args[++i];
                if (!new File(logFilePath).isAbsolute())
                {
                    logFilePath = System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + logFilePath;
                }
                Common.log.addLogWriter(new FileLogWriter(logFilePath));
                break;
            case "-cdb":
            case "-clientdatabase":
                clientDatabaseManager = new DatabaseConnectionManager(new DatabaseConnectionPool(new DatabaseConnectionInfo(args[++i]), maxIdleClientConnections), new LinkedList<SeparatedURL>());
                break;
            case "-r":
            case "-run":
                String runFilePath = args[++i];
                if (!new File(runFilePath).isAbsolute())
                {
                    runFilePath = System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + runFilePath;
                }
                break;
            case "-h":
            case "-help":
            default:
                System.out.println(Common.readResourceFile("spacenetUsage.txt"));
                return false;
            }
        }
        if (clientDatabaseManager == null)
        {
            System.out.println("Client database connection required");
            return false;
        }
        return true;
    } // end CLI processing

    public static void statusChange(Client client, String trailing)
    {
        // for ();
        // TODO for each person who is friends with caller, and for each channel the caller is in, update display status

    }

    public static void disconnect(Client client)
    {
        // TODO Auto-generated method stub

    }

    public static void wallops(String sourceUser, String message)
    {
        // TODO for each operator, send then the message

    }

    public static DatabaseConnectionManager getClientDatabaseManager()
    {
        return clientDatabaseManager;
    }

}
