package com.adam4.SFA;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
    	acceptingNewClients = new AtomicBoolean();
        clientListener = new ClientListener();
        clientListenerThread = new Thread(clientListener);
        clientListenerThread.start();
        Common.log.logMessage("Network started", MyLogger.LogLevel.DEBUG);
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
            try
            {
                serverSocket = new ServerSocket(SFAServer.clientPort);
            }
            catch (Exception e)
            {
                Common.log.logMessage(e, MyLogger.LogLevel.ERROR);
                SFAServer.endServer();
            }
        }

        @Override
        public void run()
        {
            while (acceptingNewClients.get())
            {
                try
                {
                    serverSocket.setSoTimeout(SFAServer.ENDCHECKFREQUENCY);
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
                catch (SocketException e)
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

    public static void sendError(Socket s, String error)
    {

        if (error.isEmpty())
        {
            error = "empty error";
        }
        if (!error.substring(0, 1).equals("e" + Common.SEPARATOR))
        {
            error = "e" + Common.SEPARATOR + error;
        }
        if (error.charAt(error.length() - 1) != '\n')
        {
            error += '\n';
        }
        try
        {
            s.getOutputStream().write((error.getBytes(Common.ENCODING)));
        }
        catch (UnsupportedEncodingException e)
        {
            Common.log.logMessage(e, MyLogger.LogLevel.ERROR);
        }
        catch (IOException e)
        {
            Common.log.logMessage(e, MyLogger.LogLevel.WARN);
        }
    }
    
    public static void sendMessage(Socket s, ParsedMessage message)
    {
        try
        {
            s.getOutputStream().write((message.toString().getBytes(Common.ENCODING)));
        }
        catch (UnsupportedEncodingException e)
        {
            Common.log.logMessage(e, MyLogger.LogLevel.ERROR);
        }
        catch (IOException e)
        {
            Common.log.logMessage(e, MyLogger.LogLevel.WARN);
        }
    }

	public static void sendMOTD(Socket clientSocket) 
	{
		String MOTD = "Hello and welcome to the SFA Server!";
		sendMessage(clientSocket, new ParsedMessage("MOTD", MOTD));
	}

}
