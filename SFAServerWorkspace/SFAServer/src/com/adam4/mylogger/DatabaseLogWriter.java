package com.adam4.mylogger;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.adam4.common.Common;
import com.adam4.dbconnection.DatabaseConnectionManager;
import com.adam4.dbconnection.SQLRequest;
import com.adam4.mylogger.MyLogger.LogLevel;

public class DatabaseLogWriter implements iLogWriter
{
    private ConcurrentLinkedQueue<Error> errors;
    private Writer writer;
    private Thread writerThread;

    public DatabaseLogWriter(DatabaseConnectionManager dbConManager)
    {
        errors = new ConcurrentLinkedQueue<>();
        writer = new Writer(errors, dbConManager);
        writerThread = new Thread(writer);
        writerThread.start();
    }

    class Writer implements Runnable
    {
        private boolean done;
        private DatabaseConnectionManager dbConManager;

        Writer(ConcurrentLinkedQueue<Error> errors, DatabaseConnectionManager dbConManager)
        {
            this.dbConManager = dbConManager;
            if (!dbConManager.isOnline())
            {
                done = true;
            }
        }

        private boolean writeError(Error error)
        {
            SQLRequest req = new SQLRequest("INSERT INTO `SFASchema`.`ErrorLoggingTable` (`Time`, `ThreadName`, `Error`, `Level`) VALUES (?,?,?,?,?,?)");
            req.add(error.time);
            req.add(error.server);
            req.add(error.application);
            req.add(error.thread);
            req.add(error.message);
            req.add(error.level.toString());

            return dbConManager.writeData(req);
        }

        @Override
        public void run()
        {
            while (!done)
            {
                try
                {
                    synchronized (writerThread)
                    {
                        while (!errors.isEmpty())
                        {
                            writeError(errors.poll());
                        }
                        writerThread.wait();
                    }
                }
                catch (InterruptedException e)
                {

                }
            }

        }

        public void close()
        {
            dbConManager.close();
        }

    } // end Writer class

    public void close()
    {
        writer.close();
    }

    public boolean addError(Error error)
    {
        // notify
        return errors.add(error);
    }

    protected void finalize() throws Throwable
    {
        try
        {
            Common.log.logMessage("DatabaseLogger was not cleanly closed", LogLevel.ERROR);
            close();
        }
        finally
        {
            super.finalize();
        }
    }

    @Override
    public void reconnect(ReAdd reAdd)
    {
        // todo: reconnect to DB and then call:
        reAdd.add(this);
    }
}
