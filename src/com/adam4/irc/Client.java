package com.adam4.irc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

import com.adam4.common.Common;
import com.adam4.dbconnection.SQLRequest;
import com.adam4.mylogger.MyLogger;
import com.adam4.mylogger.MyLogger.LogLevel;
import com.adam4.spacenet.ClientHandler;
import com.adam4.spacenet.SpaceNetServer;

public class Client
{
    public enum Status
    {
        AVAILABLE, AWAY, INVISIBLE, BUSY, QUIT;
    }

    private boolean isLoggedIn = false;
    private Socket s;
    private String hashedPassword;
    private String nickName;
    private String userName;
    private Status status;

    private int sendLevel;
    private LinkedList<Date> timeRecieved; // used for anti-spam/flood

    private int privilegeLevel;

    // privilegeLevel: 0: unregistered/unlogged in/banned
    // 100: restricted user: can listen, but not talk, cannot be invisible
    // 200: logged in / not banned, can chat/etc.
    // 300: privileged user: can private message others
    // 400: helper user: can move people into/out of channels
    // 500: channel operator - can set properties/restrictions on channels
    // 600: moderator: can request user IP addresses, ban/unban from server, view invisible users
    // 800: super moderator: can promote/demote moderators, see server loads, and move users/groups to different servers
    // 1000: admin: can do everything, including shutdown/reboot of servers

    Client(ClientHandler handler)
    {
        timeRecieved = new LinkedList<Date>();
        sendLevel = 10000; // 10 messages in 10 seconds average
    }

    public void handleRequest(ParsedMessage parsed) throws UnknownHostException
    {
        preventSpam();
        System.out.println(parsed);
        switch (parsed.command)
        {
        case ("AWAY"):
            away(parsed);
            break;
        case ("CHAT"):
            chat();
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
        case ("WATCH"):
            watch(parsed);
            break;
        case ("WHO"):
            who(parsed);
            break;
        case ("WHOIS"):
            whois(parsed);
            break;
        default:
            sendIRCMessage(new ParsedMessage("NOTICE", "Unsupported command " + parsed.command));
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
                sendIRCMessage(new ParsedMessage("NOTICE", "Flood error: you may only send an average of " + (sendLevel / 10000) + " messages per second, pausing"));
                Thread.sleep(sendLevel / 1000);
            }
            catch (InterruptedException e)
            {
                Common.log.logMessage(e, LogLevel.INFO);
            }
        }
        else
        {
            sendIRCMessage(new ParsedMessage("NOTICE", "Flood warning: you may only send an average of " + (sendLevel / 10000) + " messages per second"));
        }
    }

    private void away(ParsedMessage parsed)
    {
        status = Status.AWAY;
        SpaceNetServer.statusChange(this, parsed.trailing);
    }
    
    private void chat()
    {
        // this starts a p2p chat session, disconnected from the server
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));
    }

    private void help()
    {
        sendIRCMessage(new ParsedMessage("705", "Reading help file:"));
        Scanner s = new Scanner(Common.readResourceFile("spacenetHelp.txt"));
        while (s.hasNextLine())
        {
            sendIRCMessage(new ParsedMessage("706", s.nextLine()));
        }
        s.close();
        sendIRCMessage(new ParsedMessage("707", Common.replaceNewLines(Common.readResourceFile("spacenetHelp.txt"))));
    }

    private void info(ParsedMessage parsed)
    {
        sendIRCMessage(new ParsedMessage("371", SpaceNetServer.version + " " + Common.getHostName()));
    }

    private void ison(ParsedMessage parsed)
    {
        // TODO: finish this
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));
    }

    private void invite(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));

    }

    private void join(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));

    }

    private void kick(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));

    }

    private void kill(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));

    }

    private void knock(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));

    }

    private void list(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));

    }

    private void privmsg(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));

    }

    private void pong(ParsedMessage parsed)
    {
        // TODO check that it matches a requested ping, otherwise disregard
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));
    }

    private void ping(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));

    }

    private void mode(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));

    }

    private void motd()
    {
        String motd = Common.readResourceFile("motd.txt");
        if (motd.equals("motd.txt file not found"))
        {
            sendIRCMessage(new ParsedMessage("422", "MOTD file is missing"));
        }
        else
        {
            sendIRCMessage(new ParsedMessage("372", motd));
        }

    }

    private void names(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        // http://tools.ietf.org/html/rfc2812#section-3.2.5
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));

    }

    private void nick(ParsedMessage parsed)
    {
        if (Common.isGoodUserName(parsed.args[0]))
        {
            nickName = parsed.args[0];
        }
        else
        {
            sendIRCMessage(new ParsedMessage("432", "invalid nickname"));
        }
    }

    private void notice(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));

    }

    private void pass(ParsedMessage parsed)
    {
        hashedPassword = Common.hashPassword(parsed.args[0]);
    }

    private void part(ParsedMessage parsed)
    {
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));
        // TODO Auto-generated method stub

    }

    private void quit(ParsedMessage parsed)
    {
        status = Status.QUIT;
        SpaceNetServer.statusChange(this, parsed.trailing);
        SpaceNetServer.disconnect(this);
        sendIRCMessage(new ParsedMessage("ERROR", "Quit: " + parsed.trailing));
        try
        {
            s.close();
        }
        catch (IOException e)
        {
            Common.log.logMessage(e,  LogLevel.WARN);
        }
        finally
        {
            privilegeLevel = 0;
        }
        

    }

    private void silence(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));
    }

    private void stats(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));
    }

    private void summon(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));
    }

    private void time(ParsedMessage parsed)
    {
        sendIRCMessage(new ParsedMessage("391", "Server Time: " + Common.getTime()));
    }

    private void user(ParsedMessage parsed)
    {
        // connect
        userName = parsed.trailing;
        String login = "SELECT ACCESSLEVEL, CLIENTNAME, HASHEDPASSWORD, LASTLOGINDATE FROM AUTH.CLIENT WHERE CLIENTNAME=\"" + userName + "\";";
        ResultSet result = SpaceNetServer.getClientDatabaseManager().getData(login);
        try
        {
            if (!result.first())
            {
                // user does not exist, insert into database
                String create = "INSERT INTO AUTH.CLIENT (ACCESSLEVEL, CLIENTNAME, HASHEDPASSWORD, LASTLOGINDATE) VALUES ( /* ACCESSLEVEL */ /* CLIENTNAME */, /* HASHEDPASSWORD */, /* LASTLOGINDATE */ );";
                SQLRequest insert = new SQLRequest(create);
                isLoggedIn = SpaceNetServer.getClientDatabaseManager().writeData(insert);
                if (isLoggedIn)
                {
                    sendIRCMessage(new ParsedMessage("001", "Welcome to the SpaceNet IRC " + userName));
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
                sendIRCMessage(new ParsedMessage("464", "Password incorrect"));
            }
        }
        catch (SQLException e)
        {
            isLoggedIn = false;
            Common.log.logMessage(e, LogLevel.ERROR);
        }

        if (parsed.args[0].startsWith("8") && privilegeLevel > 100)
        {
            status = Status.INVISIBLE;
        }
        else
        {
            status = Status.AVAILABLE;
        }
    }

    private void userhost(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));
    }

    private void userip(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));
    }

    private void users(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));
    }

    private void version(ParsedMessage parsed)
    {
        ParsedMessage versionMessage = new ParsedMessage(Common.getHostName(), "351", null, "SpaceNet Server v" + SpaceNetServer.version);
        sendIRCMessage(versionMessage);
    }

    private void wallops(ParsedMessage parsed)
    {
        SpaceNetServer.wallops(userName, parsed.trailing);
    }

    private void watch(ParsedMessage parsed)
    {
        // freind request
        
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));
    }

    private void who(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));
    }

    private void whois(ParsedMessage parsed)
    {
        // TODO : need sample message
        sendIRCMessage(new ParsedMessage("NOTICE", "This is a wishlist item that has not yet been implemented"));
    }

    public void sendIRCMessage(ParsedMessage m)
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

    public void disconnect()
    {
        // TODO Auto-generated method stub

    }

}
