package com.adam4.irc;

public class ParsedMessage
{
    public String prefix;
    public String trailing;
    public String command;
    public String[] args;

    public ParsedMessage(String prefix, String command, String[] args, String trailing)
    {
        this.prefix = prefix;
        this.trailing = trailing;
        this.command = command;
        this.args = args;
    }

    public String toString()
    {
        String argString = "";
        for (String s : args)
        {
            argString += s + "   ";
        }
        return "Prefix: " + prefix + " command: " + command + " args: " + argString + " trailing: " + trailing;
    }
}
