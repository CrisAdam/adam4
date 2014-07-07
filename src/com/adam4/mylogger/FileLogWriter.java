package com.adam4.mylogger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.adam4.mylogger.MyLogger.LogLevel;

public class FileLogWriter implements iLogWriter, Runnable
{
    private ConcurrentLinkedQueue<Error> errors;
    private boolean done;
    private PrintWriter fileWriter;
    private Thread writerThread;

    public FileLogWriter(String filePath)
    {
        errors = new ConcurrentLinkedQueue<>();
        done = false;
        Path loggingFilePath = Paths.get(filePath);
        try
        {

            Files.createDirectories(loggingFilePath.getParent());
            Files.createFile(loggingFilePath);
            loggingFilePath.toFile().createNewFile();
        }
        catch (FileAlreadyExistsException e)
        {
            // MyLogger.LogMessage(Thread.currentThread(),
            // "Log target file already exists, appending", LogLevel.DEBUG, e);
        }
        catch (IOException e)
        {
            done = true;
            // MyLogger.LogMessage(Thread.currentThread(),
            // "Log target file error", LogLevel.DEBUG, e);
        }
        try
        {
            fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(loggingFilePath.toString(), true)));
        }
        catch (Exception e)
        {
            close();
            done = true;
            // MyLogger.LogMessage(Thread.currentThread(),
            // "unable to initialize file writer", LogLevel.ERROR, e);
        }
        writerThread = new Thread(this);
        writerThread.start();
    }

    public void close()
    {

        synchronized (writerThread)
        {
            writerThread.notify();
        }
        done = true;
        fileWriter.close();
    }

    public boolean addError(Error error)
    {
        if (done)
        {
            return false;
        }
        boolean result = errors.add(error);
        synchronized (writerThread)
        {
            writerThread.notify();
        }
        return result;
    }

    private void writeError(Error error)
    {
        fileWriter.println(error);
    }

    protected void finalize() throws Throwable
    {
        try
        {
            // MyLogger.LogMessage(Thread.currentThread().getName(),
            // "FileLogger was not cleanly closed", LogLevel.ERROR);
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
        // TODO Auto-generated method stub

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
                    fileWriter.flush();
                    writerThread.wait();
                }
            }
            catch (InterruptedException e)
            {

            }
        }
    }

}
