package com.adam4.spacenet;

import java.net.ServerSocket;

import com.adam4.SFA.SFAServer;
import com.adam4.common.Common;
import com.adam4.mylogger.MyLogger;

/**
 * This allows game servers to connect so that the spacenet can send players to them
 */
public class GameServerListener implements Runnable
{

    private Thread gameListenerThread;
    private ServerSocket serverSocket;

    GameServerListener()
    {
        try
        {
            serverSocket = new ServerSocket();
            gameListenerThread = new Thread(this);
            gameListenerThread.start();
        }
        catch (Exception e)
        {
            Common.log.LogMessage(e, MyLogger.LogLevel.ERROR);
        }
    }

    private boolean authenticateGameServer()
    {
        return false;
    }

    @Override
    public void run()
    {
        // TODO Auto-generated method stub

    }
}
