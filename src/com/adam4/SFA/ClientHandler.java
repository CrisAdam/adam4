package com.adam4.SFA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.adam4.common.Common;
import com.adam4.mylogger.MyLogger;
import com.adam4.mylogger.MyLogger.LogLevel;

public class ClientHandler implements Runnable
{
    Socket clientSocket;
    Client c;

    ClientHandler(Socket connection, Client client)
    {
        clientSocket = connection;
        c = client;
    }

    private enum MESSAGETYPE
    {

        CONNECT(1), DISCONNECT(2), UPDATEPASSWORD(3), SELECTSHIP(4), SENDINPUT(5);

        private final int index;

        MESSAGETYPE(int type)
        {
            index = type;
        }

        public int type()
        {
            return index;
        }
    }

    public void run()
    {

        BufferedReader input = null; // not sure if I need the buffering, but
                                     // having the getLine() is nice
        String message = "";

        try
        {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8.newDecoder()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        while (!clientSocket.isClosed())
        {
            try
            {
                message = input.readLine();
                if (message.isEmpty())
                {
                    continue;
                }
            }
            catch (IOException e)
            {
                message = "error";
                try
                {
                    clientSocket.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                Common.log.LogMessage(e, MyLogger.LogLevel.ERROR);
            }

            char switchChar = message.charAt(0);
            switch (switchChar)
            {
            // CONNECT(c), DISCONNECT(d), UPDATEPASSWORD(u), SELECTSHIP(s),
            // SENDINPUT(i);
            case 'c':
            {
                connect(message);
                continue;
            }
            default:
            {
                System.out.println("unsupported message type" + message.charAt(0));
                break;
            }
            }
        }
    }

    private void connect(String message)
    {
        String[] params = message.split(Common.SEPARATOR);
        // 1 = name, 2 = password, 3 = clientType, 4 = clientVersion
        String playerName = params[1];
        String password = params[2] + playerName;
        String version = params[4];
        if (!Common.isGoodUserName(playerName))
        {
            System.out.println(message);
            System.out.println(params[0] + "  " + params[1] + "  " + params[3]);
            Network.sendError(clientSocket, "bad user name: " + playerName);
            Common.log.LogMessage("bad user name: " + playerName, LogLevel.INFO);
            return;
        }
    }

    public void disconnect()
    {
        try
        {
            clientSocket.close();
        }
        catch (IOException e)
        {
            Common.log.LogMessage(e, MyLogger.LogLevel.INFO);
        }
    }

    private void recieveInput(ArrayList<String> input)
    {
        //

    }

}
