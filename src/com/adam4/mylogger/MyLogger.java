package com.adam4.mylogger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.adam4.common.Common;

public class MyLogger
{

    // todo: set parameters to discard certain LogLevel (I.E. debug) per
    // individual iLogWriter

    private Sorter sorter;
    private String server, application;

    public MyLogger()
    {
        try
        {
            this.server = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
            this.server = "UnknownServer";
            LogMessage(e, LogLevel.ERROR);
        }
        this.application = "unset";
        sorter = new Sorter();
    }

    public boolean addLogWriter(iLogWriter logWriter)
    {
        return sorter.addLogWriter(logWriter);
    }

    /**
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable
    {
        try
        {
            LogMessage("Logger was not cleanly closed", LogLevel.ERROR);
            close();
        }
        finally
        {
            super.finalize();
        }
    }

    public void close()
    {
        sorter.close();
    }

    public enum LogLevel
    {
        DEBUG, INFO, WARN, ERROR, OFF;
    }

    public void LogMessage( Exception e, LogLevel level)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getClass().toString());
        sb.append(": ");
        sb.append(e.getMessage());
        for (StackTraceElement element : e.getStackTrace())
        {
            sb.append(" -> ");
            sb.append(element.toString());
        }
        LogMessage(sb.toString(), level);
    }

    public void LogMessage(String message, LogLevel level)
    {
        sorter.addError(new Error(server, application, Thread.currentThread().getName(), message, level, Common.getTime()));
    }

    public void setApplication(String server)
    {
        this.server = server;
    }

    public void setServer(String application)
    {
        this.application = application;
    }
}

class ReAdd
{
    private ConcurrentLinkedQueue<iLogWriter> listToAddTo;

    ReAdd(ConcurrentLinkedQueue<iLogWriter> list)
    {
        listToAddTo = list;
    }

    public void add(iLogWriter logWriter)
    {
        if (listToAddTo != null)
        {
            listToAddTo.add(logWriter);
        }
    }
}

class Sorter
{
    private ConcurrentLinkedQueue<iLogWriter> distributionList;

    Sorter()
    {
        distributionList = new ConcurrentLinkedQueue<>();
    }

    public void close()
    {
        for (iLogWriter writer : distributionList)
        {
            writer.close();
        }
    }

    public boolean addLogWriter(iLogWriter logWriter)
    {
        return distributionList.add(logWriter);
    }

    public boolean addError(Error e)
    {
        boolean logged = false;
        for (iLogWriter writer : distributionList)
        {
            if (writer.addError(e))
            {
                logged = true;
            }
            else
            {
                distributionList.remove(writer);
                writer.reconnect(new ReAdd(distributionList));
            }
        }
        return logged;
    }

}
