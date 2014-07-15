package com.adam4.dbconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;

import com.adam4.common.Common;
import com.adam4.common.SeparatedURL;
import com.adam4.mylogger.MyLogger.LogLevel;

public class DatabaseConnectionManager
{
    private LinkedList<DatabaseConnectionManagerConnection> managerPool;
    private DatabaseConnectionPool localConPool;
    private Thread pendingQueueThread;
    private Thread confirmedQueueThread;
    private LinkedList<String> pendingQueue; // temporary holding place for
                                             // requests to be saved until
                                             // the majority has saved them
    private LinkedList<String> confirmedQueue;// temporary holding place for
                                              // requests that the majority
                                              // has decided to commit

    public DatabaseConnectionManager(DatabaseConnectionPool localDB, LinkedList<SeparatedURL> managerPoolURLs)
    {
        localConPool = localDB;
        managerPool = new LinkedList<>();
        for (SeparatedURL url : managerPoolURLs)
        {
            managerPool.add(new DatabaseConnectionManagerConnection(url));
            pendingQueueThread = new Thread(pendingQueueThread());
            pendingQueueThread.start();
            confirmedQueueThread = new Thread(confirmedQueue());
            confirmedQueueThread.start();

        }

    }

    public ResultSet executeSQL(String sql)
    {
        return null;
    }

    private Runnable pendingQueueThread()
    {
        for (int i = 0; i < 100; i++)
        {
            System.out.println("pending + " + i);
        }

        return null;
    }

    private Runnable confirmedQueue()
    {
        for (int i = 0; i < 100; i++)
        {
            System.out.println("confirmed + " + i);
        }
        return null;
    }

    public boolean isOnline()
    {
        if (!localConPool.isConnected())
        {
            return false;
        }
        int temp = 0;
        for (DatabaseConnectionManagerConnection con : managerPool)
        {
            if (con.isConnected())
            {
                ++temp;
            }
        }
        return ((managerPool.size() < ((temp * 2) + 1)));
    }

    public ResultSet getData(String query)
    {
        if (!localConPool.isConnected())
        {
            return null;
        }
        Connection con = localConPool.getConnection();
        try
        {
            Statement st = con.createStatement();
            return st.executeQuery(query);
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    public Boolean writeData(SQLRequest request)
    {
        if (!isOnline())
        {
            return false;
        }

        if (managerPool.size() == 0)
        {
            return writeLocalData(request);
        }
        writeDistributedData(request);
        return false;
    }

    private boolean writeDistributedData(SQLRequest request)
    {

        Common.log.logMessage("distributed writes not yet implemented", LogLevel.ERROR);
        return false;
        /*
         * todo: finish these
         * 
         * boolean[] succeeded = new boolean[managerPool.size()]; Arrays.fill(succeeded, false);
         * 
         * for (int i = 0; i < succeeded.length; i++) { if (managerPool.get(i).isConnected()) { succeeded[i] = managerPool.get(i).attemptWrite(request); } }
         * 
         * boolean localStatus = writeLocalData(request);
         * 
         * for (DatabaseConnectionManagerConnection con : managerPool) { if (con.isConnected()) { con.attemptWrite(request); } } Common.log.LogMessage(Thread.currentThread(), "distributed writes not yet implemented", LogLevel.ERROR);
         * 
         * return false;
         */
    }

    private boolean attemptWriteLocal(SQLRequest request)
    {
        return false;
    }

    private Boolean writeLocalData(SQLRequest request)
    {
        if (!localConPool.isConnected())
        {
            return false;
        }
        Connection con = localConPool.getConnection();
        try
        {
            PreparedStatement pst = con.prepareCall(request.statement);
            request.prepare(pst);
            return pst.execute();
        }
        catch (SQLException e)
        {
            Common.log.logMessage(e, LogLevel.ERROR);
            return false;
        }
    }

    public void close()
    {
        try
        {
            localConPool.close();
        }
        catch (SQLException e)
        {
            Common.log.logMessage(e, LogLevel.ERROR);
        }
    }

}
