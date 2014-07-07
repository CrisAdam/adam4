package com.adam4.SFA;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.adam4.common.Common;
import com.adam4.mylogger.MyLogger;

public class Network
{

    private static boolean acceptingNewClients;

    private static ClientListener clientListener;
    // private static AdminListener adminListener;
    private static Thread clientListenerThread;

    // private static Thread adminListenerThread;

    Network()
    {
        clientListener = new ClientListener();
        clientListenerThread = new Thread(clientListener);
        clientListenerThread.start();
        // adminListener = new AdminListener();
    }

    void acceptNewClients()
    {
        acceptingNewClients = true;
    }

    void stopAcceptingNewClients()
    {
        acceptingNewClients = false;
    }

    class ClientListener implements Runnable
    {

        ServerSocket serverSocket = null;

        ClientListener()
        {
            try
            {
                serverSocket = new ServerSocket();
            }
            catch (Exception e)
            {
                Common.log.LogMessage(Thread.currentThread(), "Unable to start ClientListener socket", MyLogger.LogLevel.ERROR, e);
            }
        }

        @Override
        public void run()
        {
            while (acceptingNewClients)
            {
                try
                {
                    serverSocket.setSoTimeout(SFAServer.ENDCHECKFREQUENCY);
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
                    // do nothing; this is an exit due to SoTimeout such that it
                    // can check that if it should still be listening or not
                }
                catch (IOException e)
                {
                    Common.log.LogMessage(Thread.currentThread(), "Network error", MyLogger.LogLevel.INFO, e);
                }
            }
        }

    }

    class AdminListener implements Runnable
    {

        ServerSocket serverSocket = null;

        @Override
        @SuppressWarnings("empty-statement")
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
            while (acceptingNewClients)
            {
                try
                {
                    serverSocket.setSoTimeout(SFAServer.ENDCHECKFREQUENCY);
                    Socket clientSocket = serverSocket.accept();
                    if (acceptingNewClients)
                    {
                        new Client(clientSocket);
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
                    Common.log.LogMessage(Thread.currentThread().getName(), "Network error", MyLogger.LogLevel.INFO, e);
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
            Common.log.LogMessage(Thread.currentThread(), "Encoding error", MyLogger.LogLevel.ERROR, e);
        }
        catch (IOException e)
        {
            Common.log.LogMessage(Thread.currentThread(), "Error sending error", MyLogger.LogLevel.INFO, e);
        }
    }

}
