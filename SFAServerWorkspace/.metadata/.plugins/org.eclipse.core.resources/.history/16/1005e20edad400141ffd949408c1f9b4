package com.adam4.dbconnection;

public class DatabaseConnectionInfo
{
    public String url, user, password;
    public int connections;
    
    public DatabaseConnectionInfo(String url, String user, String password)
    {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public DatabaseConnectionInfo(String string)
    {
        String[] params = string.split(";");
        url = params[0];
        user = params[1];
        password = params[2];
    }
}
