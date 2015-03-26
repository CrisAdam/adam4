package com.adam4.SFA;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.adam4.common.Common;
import com.adam4.irc.ParsedMessage;
import com.adam4.mylogger.MyLogger;

public class Network
{

    private static AtomicBoolean acceptingNewClients;

    private static ClientListener clientListener;
    // private static AdminListener adminListener;
    private static Thread clientListenerThread;

    // private static Thread adminListenerThread;

    Network()
    {
    	acceptingNewClients = new AtomicBoolean(true);
        clientListener = new ClientListener();
        clientListenerThread = new Thread(clientListener);
        clientListenerThread.start();
        
        // adminListener = new AdminListener();
    }

    void acceptNewClients()
    {
        acceptingNewClients.set(true);
    }

    void stopAcceptingNewClients()
    {
        acceptingNewClients.set(false);
    }

    class ClientListener implements Runnable
    {

        ServerSocket serverSocket = null;

        ClientListener()
        {
            
        }

        @Override
        public void run()
        {
        	Thread.currentThread().setName("Network Client Main");
        	try
            {
                serverSocket = new ServerSocket(SFAServer.clientPort);
            }
            catch (Exception e)
            {
                Common.log.logMessage("unable to open server socket, ending " + e, MyLogger.LogLevel.ERROR);
                SFAServer.endServer();
                return;
            }
        	Common.log.logMessage("Network started", MyLogger.LogLevel.DEBUG);
        	acceptNewClients();
            while (acceptingNewClients.get())
            {
                try
                {
                	serverSocket.setSoTimeout(1000);
                	//serverSocket.setSoTimeout(SFAServer.ENDCHECKFREQUENCY);
                    Socket clientSocket = serverSocket.accept();
                    if (acceptingNewClients.get())
                    {
                    	 Common.log.logMessage("new client connected from " + clientSocket.getInetAddress(), MyLogger.LogLevel.INFO);
                    	 System.out.println("new client connected from " + clientSocket.getInetAddress());
                    	new Thread(new ClientHandler(clientSocket)).start();
                    }
                    else
                    {
                    	clientSocket.close();
                    }
                }
                catch (SocketTimeoutException e)
                {
                    ;
                    // do nothing; this is an exit due to SoTimeout such that it
                    // can check if it should still be listening or not
                }
                catch (IOException e)
                {
                    Common.log.logMessage(e, MyLogger.LogLevel.INFO);
                }
            }
            Common.log.logMessage("no longer accepting connections", MyLogger.LogLevel.INFO);
        }

    }

    class AdminListener implements Runnable
    {

        ServerSocket serverSocket = null;

        @Override
        public void run()
        {
            try
            {
                serverSocket = new ServerSocket();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            while (acceptingNewClients.get())
            {
                try
                {
                    serverSocket.setSoTimeout(SFAServer.ENDCHECKFREQUENCY);
                    Socket clientSocket = serverSocket.accept();
                    if (acceptingNewClients.get())
                    {
                        new ClientHandler(clientSocket);
                    }
                    else
                    {

                    }
                }
                catch (SocketException e)
                {
                    ;
                    // do nothing; this is an exit due to SoTimeout such that it
                    // can check that if it should still be listening or not
                }
                catch (IOException e)
                {
                    Common.log.logMessage(e, MyLogger.LogLevel.INFO);
                }
            }
        }

    }

    

}
