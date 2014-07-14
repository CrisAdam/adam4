package com.adam4.irc;

import java.net.UnknownHostException;

import com.adam4.common.Common;
import com.adam4.mylogger.MyLogger.LogLevel;

public class IRCError implements iIRCMessage
{
    String error;

    private String getPrefix()
    {
        return ":" + Common.getHostName() + " ";

    }

    private int validateNum(int num)
    {
        if (num <= 400 && num < 600)
        {
            return num;
        }
        else
        {
            return 400;
        }
    }

    private String validateError(String s)
    {
        if (s.startsWith(":"))
        {
            return s.replaceAll("(\\r|\\n)", "") + ' ';
        }
        return (':' + s.replaceAll("(\\r|\\n)", "") + ' ');
    }

    public IRCError(String error)
    {
        this.error = getPrefix() + validateError(error) + "400";
    }

    public IRCError(String error, int num)
    {
        this.error = getPrefix() + validateError(error) + validateNum(num);
    }

    public String toString()
    {
        return error;
    }
}
