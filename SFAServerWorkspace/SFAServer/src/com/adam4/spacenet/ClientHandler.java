package com.adam4.spacenet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import com.adam4.common.Common;
import com.adam4.dbconnection.SQLRequest;
import com.adam4.irc.Channel;
import com.adam4.irc.Client;
import com.adam4.irc.IRC;
import com.adam4.irc.ParsedMessage;
import com.adam4.mylogger.MyLogger;
import com.adam4.mylogger.MyLogger.LogLevel;

public class ClientHandler implements Runnable
{
    private Client client;

    private Socket s;

    ClientHandler(Socket s)
    {
        this.s = s;
        client = new Client(this);
    }

    @Override
    public void run()
    {
        // not sure if I need the buffering, but having the getLine() is nice
        BufferedReader input = null;
        String message = "";
        try
        {
            input = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8.newDecoder()));
        }
        catch (IOException e)
        {
            Common.log.logMessage(e, MyLogger.LogLevel.ERROR);
        }
        while (!s.isClosed())
        {
            try
            {
                message = input.readLine();
                if (message.isEmpty())
                {
                    // according to RFC1459 2.3.1, ignore empty messages
                    continue;
                }
            }
            catch (IOException e)
            {
                message = "error"; // not sure if needed
                try
                {
                    s.close();
                }
                catch (IOException e1)
                {
                    Common.log.logMessage(e1, MyLogger.LogLevel.ERROR);
                }
                Common.log.logMessage(e, MyLogger.LogLevel.ERROR);
            }
            try
            {
                client.handleRequest(IRC.parseLine(message));
            }
            catch (UnknownHostException e)
            {
                Common.log.logMessage(e, MyLogger.LogLevel.ERROR);
            }
        }
        client.disconnect();
    }

}
