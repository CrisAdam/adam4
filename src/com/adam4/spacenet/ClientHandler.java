package com.adam4.spacenet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import com.adam4.common.Common;
import com.adam4.mylogger.MyLogger;
import com.adam4.mylogger.MyLogger.LogLevel;

public class ClientHandler implements Runnable
{
    private boolean isLoggedIn = false;
    private Socket s;

    ClientHandler(Socket s)
    {
        this.s = s;
    }

    @Override
    public void run()
    {
        BufferedReader input = null; // not sure if I need the buffering, but
        // having the getLine() is nice
        String message = "";

        try
        {
            input = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8.newDecoder()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
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
                message = "error";
                try
                {
                    s.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                Common.log.LogMessage(e, MyLogger.LogLevel.ERROR);
            }
            
            parseLine(message);
        }
    }

    private void parseLine(String line)
    {
        // RFC1459    http://tools.ietf.org/html/rfc1459.html#section-2
        // RFC2812    http://tools.ietf.org/html/rfc2812
        
            String prefix = "";
            
            String parameters = "";
            // check for prefix and remove if exists
            if (line.startsWith(":"))
            {
                String[] tokens = line.split(" ", 2);
                prefix = line.substring(0, line.indexOf(" "));
                line = line.substring(line.indexOf(" "), line.length());
            }
            String command = line.substring(0, line.indexOf(" "));
            line = line.substring(line.indexOf(" "), line.length());
            
            
            
            String[] tokens1 = line.split(" ", 2);
            line = tokens1.length > 1 ? tokens1[1] : "";
            String[] tokens2 = line.split("(^| )\\:", 2);
            String trailing = null;
            line = tokens2[0];
            if (tokens2.length > 1)
                trailing = tokens2[1];
            ArrayList<String> argumentList = new ArrayList<String>();
            if (!line.equals(""))
                argumentList.addAll(Arrays.asList(line.split(" ")));
            if (trailing != null)
                argumentList.add(trailing);
            String[] arguments = argumentList.toArray(new String[0]);
            
            ISpaceNetCommand spaceNetCommand = getCommand();
            spaceNetCommand.process(prefix, command, parameters);
        
    }  // end parse line

    private ISpaceNetCommand getCommand()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
