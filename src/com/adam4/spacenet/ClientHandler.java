package com.adam4.spacenet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.adam4.common.Common;
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

    ClientHandler(Socket s)
    {
        this.s = s;
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
            Common.log.LogMessage(e, MyLogger.LogLevel.ERROR);
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
                message = "error";  // not sure if needed
                try
                {
                    s.close();
                }
                catch (IOException e1)
                {
                    Common.log.LogMessage(e1, MyLogger.LogLevel.ERROR);
                }
                Common.log.LogMessage(e, MyLogger.LogLevel.ERROR);
            }
            try
            {
                handleRequest(IRC.parseLine(message));
            }
            catch (UnknownHostException e)
            {
                Common.log.LogMessage(e, MyLogger.LogLevel.ERROR);
            }
        }
    }

    void handleRequest(ParsedMessage parsed) throws UnknownHostException
    {
        switch (parsed.command)
        {
        case ("AWAY"):
            away(parsed);
            break;
        case ("HELP"):
            help(parsed);
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
            motd(parsed);
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

    private void away(ParsedMessage parsed)
    {
        away = !away;
        // TODO notify all who are talking directly with this person

    }

    private void help(ParsedMessage parsed)
    {
        BufferedReader br;
        try
        {
            br = new BufferedReader(new FileReader(System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + "resources" + FileSystems.getDefault().getSeparator() + "spacenetHelp.txt"));
            String line = null;
            while ((line = br.readLine()) != null)
            {
                System.out.println(line);
            }
            br.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        sendIRCMessage("help");
    }

    private void info(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

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

    private void time(ParsedMessage parsed)
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
            }
        }
        catch (SQLException e)
        {
            Common.log.LogMessage(e, LogLevel.ERROR);
        }
        ;
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

    private void summon(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void stats(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void silence(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void quit(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void privmsg(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void pong(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void ping(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void part(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void notice(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void names(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void motd(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void mode(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void pass(ParsedMessage parsed)
    {
        try
        {
            hashedPassword = Common.hashPassword(parsed.args[0]);
        }
        catch (NoSuchAlgorithmException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void whois(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void who(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void watch(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void wallops(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void version(ParsedMessage parsed)
    {
        // TODO Auto-generated method stub

    }

    private void nick(ParsedMessage parsed)
    {
        hashedPassword = parsed.args[0];
    }

    public void sendIRCMessage(String message)
    {
        try
        {
            sendIRCMessage(new GenericIRCMessage(message));
        }
        catch (UnknownHostException e)
        {
            Common.log.LogMessage(e, MyLogger.LogLevel.ERROR);
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
            Common.log.LogMessage(e, MyLogger.LogLevel.ERROR);
        }
        catch (IOException e)
        {
            Common.log.LogMessage(e, MyLogger.LogLevel.WARN);
        }
    }
}
