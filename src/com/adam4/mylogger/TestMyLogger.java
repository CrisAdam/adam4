package com.adam4.mylogger;

import com.adam4.common.Common;

public class TestMyLogger
{
    public static void main(String args[]) throws InterruptedException
    {
        System.out.println(Common.getTime());
        Common.log.setApplication(Thread.currentThread().getStackTrace()[1].getClassName());
        // MyLogger.addLogWriter(new ConsoleLogWriter());
        Common.log.addLogWriter(new FileLogWriter("TestErrorLog.txt"));
        if (args.length != 0)
        {
            // log.addLogWriter(new DatabaseLogWriter(args[0]));
        }

        Common.log.LogMessage(Thread.currentThread().getName(), "Test log message", MyLogger.LogLevel.DEBUG);

        Common.log.close();
    }
}
