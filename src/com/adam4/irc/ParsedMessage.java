package com.adam4.irc;

import com.adam4.common.Common;

public class ParsedMessage
{
    public String prefix;
    public String trailing;
    public String command;
    public String[] args;

    public ParsedMessage(String prefix, String command, String[] args, String trailing)
    {
        this.prefix = prefix;
        this.trailing = Common.removeNewLine(trailing);
        this.command = command;
        this.args = args;
    }

    public ParsedMessage(String command, String[] args, String trailing)
    {

        this.prefix = Common.getHostName();
        this.trailing = Common.removeNewLine(trailing);
        this.command = command;
        this.args = args;
    }

    public ParsedMessage(String command, String trailing)
    {

        this.prefix = Common.getHostName();
        this.trailing = trailing;
        this.command = command;
    }

    public ParsedMessage(String command, String[] args)
    {

        this.prefix = Common.getHostName();
        this.command = command;
        this.args = args;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if (!prefix.equals(""))
        {
            sb.append(Common.prefixColon(prefix));
        }
        sb.append(' ');
        sb.append(command);
        if (args != null)
        {
            sb.append(' ');
            for (String s : args)
            {
                sb.append(s);
                sb.append(' ');
            }
        }
        if (!trailing.equals(""))
        {
            sb.append(' ');
            sb.append(Common.prefixColon(trailing));
        }
        return sb.toString();
    }

}
