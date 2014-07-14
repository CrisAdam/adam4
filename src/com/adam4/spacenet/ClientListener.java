package com.adam4.spacenet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLServerSocket;

import com.adam4.common.Common;
import com.adam4.mylogger.MyLogger.LogLevel;

public class ClientListener
{
    private ServerSocket serverSocket;
    private SSLServerSocket sslServerSocket;
    private boolean acceptingNewClients;
    private Reciever reciever;
    private SSLReciever sslReciever;

    /*
     * this will allow clients to connect to the auth server
     */
    ClientListener()
    {
        acceptingNewClients = true;
        try
        {
            serverSocket = new ServerSocket(6667);
            reciever = new Reciever(serverSocket);
            new Thread(reciever).start();

            ServerSocketFactory sslserversocketfactory = SSLServerSocketFactory.getDefault();
            sslServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(6697);
            reciever = new Reciever(serverSocket);
            new Thread(reciever).start();
        }
        catch (IOException e)
        {
            acceptingNewClients = false;
            Common.log.logMessage(e, LogLevel.ERROR);
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
                        Common.log.logMessage("Not accepting new connections " + clientSocket.getLocalAddress(), LogLevel.WARN);
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
                    Common.log.logMessage(e, LogLevel.INFO);
                }
            }
        } // end run
    }

    private class SSLReciever implements Runnable
    {
        ServerSocket socket;

        SSLReciever(ServerSocket socket)
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
                        Common.log.logMessage("Not accepting new connections " + clientSocket.getLocalAddress(), LogLevel.WARN);
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
                    Common.log.logMessage(e, LogLevel.INFO);
                }
            }
        } // end run
    }

    public void close()
    {
        acceptingNewClients = false;
    }

}
