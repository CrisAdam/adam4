package com.adam4.irc;

public class IRC
{
    public static ParsedMessage parseLine(String line)
    {
        // RFC1459 http://tools.ietf.org/html/rfc1459.html#section-2
        // RFC2812 http://tools.ietf.org/html/rfc2812

        // prefix is optional
        // prefix is defined as existing if the message starts with :
        // prefix ends after the first space
        // prefix is used to define the source machine
        String prefix = "";
        // check for prefix and remove if exists

        if (line.startsWith(":"))
        {
            prefix = line.substring(0, line.indexOf(" "));
            line = line.substring(line.indexOf(" "), line.length()).trim();
        }
        // trailing is optional
        // trailing is defined as existing if there is a : that is not at the beginning of the message
        // trailing is between the first non-starting : and the end of line
        String trailing = "";
        // check for trailing and remove if exists
        if (line.indexOf(':') != -1)
        {
            trailing = line.substring(line.indexOf(':'), line.length());
            line = line.substring(0, line.indexOf(':'));
        }
        // command is required, it is defined as the first word after any prefix and trailing is removed
        // a word in this context is defined as a segment of a space-delimited string
        // any additional non-trailing words after the command are parameters
        // the command and parameters may not contain : or space, however the trailing may contain them
        // parameters are optional
        String[] temp = line.split(" ");
        String[] args = new String[(temp.length - 1)];
        if (temp.length > 1)
        {
            System.arraycopy(temp, 1, args, 0, args.length);
        }
        else
        {
            args = new String[0];
        }
        return new ParsedMessage(prefix, temp[0], args, trailing);

    } // end parse line

}
