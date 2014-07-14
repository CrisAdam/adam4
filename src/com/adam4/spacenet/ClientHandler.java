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
import com.adam4.irc.GenericIRCMessage;
import com.adam4.irc.IRC;
import com.adam4.irc.IRCError;
import com.adam4.irc.iIRCMessage;
import com.adam4.irc.ParsedMessage;
import com.adam4.mylogger.MyLogger;
import com.adam4.mylogger.MyLogger.LogLevel;

public class ClientHandler implements Runnable
{
    private boolean isLoggedIn = false;
    private Socket s;
    private String hashedPassword;
    private String nickName;
    private String userName;
    private boolean invisible;
    private boolean away;
    private int privilegeLevel;

    private ArrayList<String> channels;
    private ArrayList<String> groups;

    private int sendLevel;
    private LinkedList<Date> timeRecieved; // used for anti-spam/flood

    ClientHandler(Socket s)
    {
        channels = new ArrayList<>();
        groups = new ArrayList<>();
        this.s = s;
        timeRecieved = new LinkedList<Date>();
        privilegeLevel = 0;
        sendLevel = 10000; // 10 messages in 10 seconds average
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
                handleRequest(IRC.parseLine(message));
            }
            catch (UnknownHostException e)
            {
                Common.log.logMessage(e, MyLogger.LogLevel.ERROR);
            }
        }
        isLoggedIn = false;
        SpaceNetServer.disconnect(this);
    }

    void handleRequest(ParsedMessage parsed) throws UnknownHostException
    {
        preventSpam();
        switch (parsed.command)
        {
        case ("AWAY"):
            away(parsed);
            break;
        case ("HELP"):
            help();
            break;
        case ("INFO"):
            info(parsed);
            break;
        case ("INVITE"):
            invite(parsed);
            break;
        case ("ISON"):
            ison(parsed);
            break;
        case ("JOIN"):
            join(parsed);
            break;
        case ("KICK"):
            kick(parsed);
            break;
        case ("KILL"):
            kill(parsed);
            break;
        case ("KNOCK"):
            knock(parsed);
            break;
        case ("LIST"):
            list(parsed);
            break;
        case ("MODE"):
            mode(parsed);
            break;
        case ("MOTD"):
            motd();
            break;
        case ("NAMES"):
            names(parsed);
            break;
        case ("NICK"):
            nick(parsed);
            break;
        case ("NOTICE"):
            notice(parsed);
            break;
        case ("PART"):
            part(parsed);
            break;
        case ("PASS"):
            pass(parsed);
            break;
        case ("PING"):
            ping(parsed);
            break;
        case ("PONG"):
            pong(parsed);
            break;
        case ("PRIVMSG"):
            privmsg(parsed);
            break;
        case ("QUIT"):
            quit(parsed);
            break;
        case ("SILENCE"):
            silence(parsed);
            break;
        case ("STATS"):
            stats(parsed);
            break;
        case ("SUMMON"):
            summon(parsed);
            break;
        case ("TIME"):
            time(parsed);
            break;
        case ("USER"):
            user(parsed);
            break;
        case ("USERHOST"):
            userhost(parsed);
            break;
        case ("USERIP"):
            userip(parsed);
            break;
        case ("USERS"):
            users(parsed);
            break;
        case ("VERSION"):
            version(parsed);
            break;
        case ("WALLOPS"):
            wallops(parsed);
            break;
        case ("WATCH"): // freind
            watch(parsed);
            break;
        case ("WHO"):
            who(parsed);
            break;
        case ("WHOIS"):
            whois(parsed);
            break;
        default:
            sendIRCMessage(new IRCError("Unsupported command " + parsed.command));
            System.out.println("Error");
            break;
        }
    } // end handleRequest

    private void preventSpam()
    {
        timeRecieved.add(new Date());
        if (timeRecieved.peek().getTime() - timeRecieved.getLast().getTime() > sendLevel)
        {
            while (timeRecieved.size() > 10)
            {
                timeRecieved.pop();
            }
        }
        else if (timeRecieved.peek().getTime() - timeRecieved.getLast().getTime() < (sendLevel * 1.2))
        {
            try
            {
                sendIRCMessage(new IRCError("Flood error: you may only send an average of " + (sendLevel / 10000) + " messages per second, pausing", 540));
                Thread.sleep(sendLevel / 1000);
            }
            catch (InterruptedException e)
            {
                Common.log.logMessage(e, LogLevel.INFO);
            }
        }
        else
        {
            sendIRCMessage(new IRCError("Flood warning: you may only send an average of " + (sendLevel / 1000) + " messages per second", 540));
        }
    }

    private void away(ParsedMessage parsed)
    {
        away = !away;
        SpaceNetServer.away(this, away);
    }

    private void help()
    {
        sendIRCMessage(Common.replaceNewLines(Common.readResourceFile("spacenetHelp.txt")));
    }

    private void info(ParsedMessage parsed)
    {
        sendIRCMessage(SpaceNetServer.version + " " + Common.getHostName(), 371);
    }

    private void ison(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
    }

    private void invite(ParsedMessage parsed)

    {
        // TODO Auto-generated method stub

    }

    private void join(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void kick(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void kill(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void knock(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void list(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void user(ParsedMessage parsed)
    {
        // connect
        userName = parsed.trailing.replace(":", "");
        String login = "SELECT ACCESSLEVEL, CLIENTNAME, HASHEDPASSWORD, LASTLOGINDATE FROM AUTH.CLIENT WHERE CLIENTNAME=\"" + userName + "\";";
        ResultSet result = SpaceNetServer.clientDatabaseManager.getData(login);
        try
        {
            if (!result.first())
            {
                // user does not exist, insert into database
                String create = "INSERT INTO AUTH.CLIENT (ACCESSLEVEL, CLIENTNAME, HASHEDPASSWORD, LASTLOGINDATE) VALUES ( /* ACCESSLEVEL */ /* CLIENTNAME */, /* HASHEDPASSWORD */, /* LASTLOGINDATE */ );";
                SQLRequest insert = new SQLRequest(create);
                isLoggedIn = SpaceNetServer.clientDatabaseManager.writeData(insert);
                if (isLoggedIn)
                {
                    sendIRCMessage("Welcome to the SpaceNet IRC" + userName, 001);
                    motd();
                }
            }
            else if (result.getString("HASHED_PASSWORD").equals(hashedPassword))
            {
                motd();
                isLoggedIn = true;
            }
            else
            {
                sendIRCMessage(new IRCError("Password incorrect", 464));
            }
        }
        catch (SQLException e)
        {
            isLoggedIn = false;
            Common.log.logMessage(e, LogLevel.ERROR);
        }

        if (parsed.args[0].startsWith("8"))
        {
            invisible = true;
        }
        else
        {
            invisible = false;
        }
    }

    private void userhost(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void userip(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void users(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void privmsg(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void pong(ParsedMessage parsed)
    {
        // TODO check that it matches a requested ping, otherwise disregard

    }

    private void ping(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void mode(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void motd()
    {
        String motd = Common.readResourceFile("motd.txt");
        if (motd.equals("motd.txt file not found"))
        {
            sendIRCMessage(new IRCError("MOTD file is missing", 422));
        }
        else
        {
            sendIRCMessage(motd);
        }

    }

    private void names(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        // http://tools.ietf.org/html/rfc2812#section-3.2.5

    }

    private void nick(ParsedMessage parsed)
    {
        if (Common.isGoodUserName(parsed.args[0]))
        {
            nickName = parsed.args[0];
        }
        else
        {
            sendIRCMessage("invalid nickname", 432);
        }
    }

    private void notice(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void pass(ParsedMessage parsed)
    {
        hashedPassword = Common.hashPassword(parsed.args[0]);
    }

    private void part(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void quit(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void silence(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void stats(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void summon(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void time(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void who(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void whois(ParsedMessage parsed)
    {
        // TODO : need sample message
    }

    private void watch(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void wallops(ParsedMessage parsed)
    {
        SpaceNetServer.wallops(userName, parsed.trailing);
    }

    private void version(ParsedMessage parsed)
    {
        sendIRCMessage("SpaceNet Server v" + SpaceNetServer.version, 351);
    }

    public void sendIRCMessage(String message)
    {
        try
        {
            sendIRCMessage(new GenericIRCMessage(message));
        }
        catch (UnknownHostException e)
        {
            Common.log.logMessage(e, MyLogger.LogLevel.ERROR);
        }
    }

    public void sendIRCMessage(String message, int number)
    {
        try
        {
            sendIRCMessage(new GenericIRCMessage(message, number));
        }
        catch (UnknownHostException e)
        {
            Common.log.logMessage(e, MyLogger.LogLevel.ERROR);
        }
    }

    public void sendIRCMessage(iIRCMessage m)
    {
        try
        {
            s.getOutputStream().write((m.toString().getBytes(Common.ENCODING)));
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
}
