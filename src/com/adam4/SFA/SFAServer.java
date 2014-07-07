package com.adam4.SFA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.nio.file.StandardWatchEventKinds;

import com.adam4.common.Common;
import com.adam4.dbconnectionmanager.DatabaseConnectionManager;
import com.adam4.dbconnectionmanager.DatabaseConnectionPool;
import com.adam4.mylogger.ConsoleLogWriter;
import com.adam4.mylogger.DatabaseLogWriter;
import com.adam4.mylogger.FileLogWriter;
import com.adam4.mylogger.MyLogger;
import com.adam4.mylogger.MyLogger.LogLevel;

public class SFAServer
{

    // final variables
    public final static int clientPort = 9995;
    public final static int adminPort = 9996;
    public final static int ENDCHECKFREQUENCY = 1000;
    public final static int quadCapacity = 4;
    public final static Double rootQuadTreeSize = 10000.0;
    public final static Double timeConstant = 0.000000001;
    public final static String version = "1.0.0";

    // class variables
    static DatabaseConnectionPool clientDatabasePool;

    // private variables
    private static File run;
    private static ConcurrentLinkedQueue<Game> games = new ConcurrentLinkedQueue<Game>();
    private static ConcurrentLinkedQueue<Client> clients = new ConcurrentLinkedQueue<Client>();
    private static Network network = new Network();
    private static final int maxIdleClientConnections = 3;
    private static String runFilePath = System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + "run";
    private static boolean running = true;
    private static ConcurrentLinkedQueue<iClientDataResource> clientDataResources = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) throws Exception
    {
        Common.log.setApplication(Thread.currentThread().getStackTrace()[1].getClassName());
        if (!handleCLI(args))
        {
            // do not run program if it is given invalid arguments
            return;
        }

        Common.log.LogMessage(Thread.currentThread(), "System started", LogLevel.DEBUG);

        // the run file is so that the server can (softly) terminate the program
        // by deleting it
        makeRunFile(runFilePath);
        watchRunFile();

        endServer();
        Common.log.close();

    }

    public void joinGame(ConcurrentLinkedQueue<Client> playersJoining)
    {
        for (Game g : games)
        {
            if (!g.getStarted() && (g.getOpenSpots > playersJoining.size()))
            {
                clients.removeAll(clients);
                g.addClients(clients);
                return;
            }
        }
        // no games with open slots exist, create new game
        createGame(playersJoining);

    }

    public void createGame(ConcurrentLinkedQueue<Client> clients)
    {
        Game temp = new Game();
        temp.addClients(clients);
        clients.removeAll(clients);
    }

    private static void watchRunFile()
    {
        try
        {
            Path path = FileSystems.getDefault().getPath(runFilePath.substring(0, runFilePath.lastIndexOf(FileSystems.getDefault().getSeparator())));
            System.out.println(runFilePath.substring(0, runFilePath.lastIndexOf(FileSystems.getDefault().getSeparator())));
            WatchService watcher = FileSystems.getDefault().newWatchService();
            WatchKey key;
            key = path.register(watcher, StandardWatchEventKinds.ENTRY_DELETE);
            // stall until the game is supposed to end
            // reset key to allow new events to be detected
            while (key.reset())
            {
                try
                {
                    for (WatchEvent<?> event : key.pollEvents())
                    {
                        WatchEvent.Kind<?> kind = event.kind();
                        if (kind == StandardWatchEventKinds.OVERFLOW)
                        {
                            Common.log.LogMessage(Thread.currentThread(), "File watcher overflow", LogLevel.INFO);
                            if (!run.exists())
                            {
                                running = false;
                            }
                            break;
                        }
                        if (kind == StandardWatchEventKinds.ENTRY_DELETE)
                        {
                            @SuppressWarnings("unchecked")
                            WatchEvent<Path> ev = (WatchEvent<Path>) event;
                            Path filename = ev.context();
                            if (filename.toAbsolutePath().toString().equals(runFilePath))
                            {
                                Common.log.LogMessage(Thread.currentThread(), "Run file has been deleted", LogLevel.INFO);
                                running = false;
                                break;
                            }
                        }

                    }
                    if (!running)
                    {
                        watcher.close();
                        break;
                    }
                }
                catch (Exception e)
                {
                    Common.log.LogMessage(Thread.currentThread(), "File watcher error", LogLevel.INFO, e);
                    continue;
                }
            }// end while loop
        }
        catch (IOException e1)
        {
            Common.log.LogMessage(Thread.currentThread(), "File watcher error", LogLevel.ERROR, e1);
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
                // Common.log.addLogWriter(new DatabaseLogWriter(new
                // DatabaseConnectionManager(new DatabaseConnectionPool(new
                // DatabaseConnectionInfo(args[++i].split(";")[0], "", ""),
                // maxIdleClientConnections)));
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
                // clientDataResources.add(new ClientDatabaseResource(new
                // DatabaseConnectionPool(new
                // DatabaseConnectionInfo(args[++i].split(";")[0], "", ""),
                // maxIdleClientConnections)));
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

    }

    private static void printUsage() throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + "resources" + FileSystems.getDefault().getSeparator() + "usage.txt"));
        String line = null;
        while ((line = br.readLine()) != null)
        {
            System.out.println(line);
        }
        br.close();
    }

    private static void makeRunFile(String runFilePath)
    {

        run = new File(runFilePath);
        try
        {
            run.createNewFile();
        }
        catch (IOException e)
        {
            Common.log.LogMessage(Thread.currentThread().getName(), "unable to create run file", MyLogger.LogLevel.ERROR, e);
        }
    }

    static void addPlayer(Client client)
    {
        clients.add(client);
    }

    static void removeClient(Client client)
    {
        clients.remove(client);
    }

    private static void endServer()
    {
        network.stopAcceptingNewClients();
        for (Game g : games)
        {
            g.endGame();
        }
        for (Client c : clients)
        {
            c.disconnect("Server is going down");
        }
    }

    public static ConcurrentLinkedQueue<iClientDataResource> getClientDataResources()
    {
        return clientDataResources;
    }

    public static void addClient(Client c)
    {
        // TODO Auto-generated method stub

    }

}
