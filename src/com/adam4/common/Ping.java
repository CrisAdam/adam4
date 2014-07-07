package com.adam4.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Ping
{
    int ping(String host)
    {
        ProcessBuilder ps;
        if (System.getProperty("os.name").equals("Linux"))
        {
            ps = new ProcessBuilder("ping", "-c 1 " + host);
        }
        else if (System.getProperty("os.name").contains("Windows"))
        {
            ps = new ProcessBuilder("ping", "-n 1 " + host);
        }
        else
        {
            return Integer.MAX_VALUE;
        }
        try
        {
            Process pr = ps.start();
            BufferedReader output = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String temp = " ";
            while (temp != "")
            {
                temp = output.readLine();
                if (temp.equals("Request timed out.") || temp.equals("100% packet loss"))
                { // windows version linux version
                    return Integer.MAX_VALUE;
                }
                else if (temp.contains("Minimum"))
                {
                    // windows version
                    return Integer.getInteger((temp.split("=")[3].replaceAll("ms", "").trim()));
                }
                else if (temp.contains("rtt min/avg/max/mdev = "))
                {
                    // linux version
                    return Integer.getInteger((temp.split("/")[4].replaceAll("ms", "").trim()));
                }
            }
        }
        catch (IOException e)
        {
            // large number if unable to connect
            return Integer.MAX_VALUE;
        }
        return Integer.MAX_VALUE;
    }
}
