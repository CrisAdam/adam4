

package com.adam4.mylogger;

import com.adam4.common.Common;

public class TestMyLogger
{
    public static void main(String args[]) throws InterruptedException
    {
    	System.out.println(Common.getTime());
    	MyLogger log = new MyLogger("MyServer", "Logger test");
    	//MyLogger.addLogWriter(new ConsoleLogWriter());
    	log.addLogWriter(new FileLogWriter("TestErrorLog.txt"));
    	if (args.length != 0)
    	{
    		log.addLogWriter(new DatabaseLogWriter(args[0]));
    	}
    	
    	log.LogMessage(Thread.currentThread().getName(), "Test log message", MyLogger.LogLevel.DEBUG);
    	
    	log.close();
    }
}
