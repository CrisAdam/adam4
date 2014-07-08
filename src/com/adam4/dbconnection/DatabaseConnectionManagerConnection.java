package com.adam4.dbconnection;

import java.io.IOException;
import java.net.Socket;

import com.adam4.common.SeparatedURL;

public class DatabaseConnectionManagerConnection
{
    private boolean isConnected;
    Socket s;

    DatabaseConnectionManagerConnection(SeparatedURL url)
    {
        setConnected(false);
        try
        {
            s = new Socket(url.url, url.port);
            setConnected(true);
        }
        catch (IOException e)
        {
            setConnected(false);
            e.printStackTrace();
        }
    }

    public boolean isConnected()
    {
        return isConnected;
    }

    public void setConnected(boolean isConnected)
    {
        this.isConnected = isConnected;
    }

    public boolean attemptWrite(SQLRequest request)
    {
        // TODO Auto-generated method stub
        return false;
    }
}
