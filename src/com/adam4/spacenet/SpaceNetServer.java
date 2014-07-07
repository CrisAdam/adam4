package com.adam4.spacenet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.LinkedList;
import com.adam4.common.Common;
import com.adam4.common.SeparatedURL;
import com.adam4.dbconnectionmanager.DatabaseConnectionInfo;
import com.adam4.dbconnectionmanager.DatabaseConnectionManager;
import com.adam4.dbconnectionmanager.DatabaseConnectionPool;
import com.adam4.mylogger.ConsoleLogWriter;
import com.adam4.mylogger.DatabaseLogWriter;
import com.adam4.mylogger.FileLogWriter;

public class SpaceNetServer
{

    private static final int maxIdleClientConnections = 3;
    public static final int ENDCHECKFREQUENCY = 100; // polling frequency in ms
                                                     // for network to close
                                                     // socket

    // class variables
    static DatabaseConnectionManager clientDatabaseManager;
    static DatabaseConnectionManager serverDatabaseManager;

    public static void main(String[] args) throws Exception
    {
        Common.log.setApplication("SpaceNet");
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
            case "-sdb":
            case "-serverdatabase":
                serverDatabaseManager = new DatabaseConnectionManager(new DatabaseConnectionPool(new DatabaseConnectionInfo(args[++i]), maxIdleClientConnections), new LinkedList<SeparatedURL>());
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
