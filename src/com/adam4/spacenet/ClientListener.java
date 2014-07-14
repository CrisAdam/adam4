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
    private Reciever reciever;

    /*
     * this will allow clients to connect to the auth server
     */
    ClientListener()
    {
        try
        {
            serverSocket = new ServerSocket();
            acceptingNewClients = true;
            reciever = new Reciever(serverSocket);
            new Thread(reciever).start();
        }
        catch (IOException e)
        {
            acceptingNewClients = false;
            Common.log.LogMessage(e, LogLevel.ERROR);
        }

    }

    private class Reciever implements Runnable
    {
        ServerSocket socket;

        Reciever(ServerSocket socket)
        {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            while (!socket.isClosed())
            {
                try
                {
                    serverSocket.setSoTimeout(SpaceNetServer.ENDCHECKFREQUENCY);
                    Socket clientSocket = serverSocket.accept();
                    if (acceptingNewClients)
                    {
                        new Thread(new ClientHandler(clientSocket)).start();
                    }
                    else
                    {
                        Common.log.LogMessage("Not accepting new connections " + clientSocket.getLocalAddress(), LogLevel.WARN);
                        clientSocket.close();
                    }
                }
                catch (SocketException e)
                {
                    ;
                    // do nothing; this is an exit due to SoTimeout such that it can check that if it should still be listening or not
                }
                catch (IOException e)
                {
                    Common.log.LogMessage(e, LogLevel.INFO);
                }
            }
        } // end run
    }

    public void close()
    {
        acceptingNewClients = false;
    }

}
