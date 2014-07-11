package com.adam4.irc;

import java.net.UnknownHostException;

import com.adam4.common.Common;

public class GenericIRCMessage implements iIRCMessage
{
    String msg;

    private String getPrefix() throws UnknownHostException
    {
        return ":" + Common.getHostName() + " ";
    }

    private String validate(String s)
    {
        if (s.startsWith(":"))
        {
            return s.replaceAll("(\\r|\\n)", "") + ' ';
        }
        return (':' + s.replaceAll("(\\r|\\n)", "") + ' ');
    }

    public GenericIRCMessage(String message) throws UnknownHostException
    {
        this.msg = getPrefix() + validate(message);
    }

    public String toString()
    {
        return msg;
    }
}
