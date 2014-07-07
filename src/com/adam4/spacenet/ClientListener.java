package com.adam4.spacenet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.adam4.common.Common;
import com.adam4.mylogger.MyLogger.LogLevel;

public class ClientListener
{
    private ServerSocket serverSocket;
    private boolean acceptingNewClients;

    /*
     * this will allow clients to connect to the auth server
     */
    ClientListener()
    {
        try
        {
            serverSocket = new ServerSocket();
            acceptingNewClients = true;
        }
        catch (IOException e)
        {
            acceptingNewClients = false;
            Common.log.LogMessage(Thread.currentThread(), "unable to open serverSocket", LogLevel.ERROR, e);
        }

    }

    private Runnable run()
    {
        while (acceptingNewClients)
        {
            try
            {
                serverSocket.setSoTimeout(SpaceNetServer.ENDCHECKFREQUENCY);
                Socket clientSocket = serverSocket.accept();
                if (acceptingNewClients)
                {

                }
                else
                {

                }
            }
            catch (SocketException e)
            {
                ;
                // do nothing; this is an exit due to SoTimeout such that it can
                // check that if it should still be listening or not
            }
            catch (IOException e)
            {
                Common.log.LogMessage(Thread.currentThread(), "Network error", LogLevel.INFO, e);
            }
        }
        return null;
    }

    public void close()
    {
        acceptingNewClients = false;
    }

}
