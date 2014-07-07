package com.adam4.mylogger;

import java.util.Date;

import com.adam4.common.Common;
import com.adam4.mylogger.MyLogger.LogLevel;

public class Error
{

    String server;
    String application;
    String thread;
    String message;
    LogLevel level;
    String trace;
    Date date;

    Error(String server, String application, String thread, String message, LogLevel level, String trace, Date date)
    {
        this.server = server;
        this.application = application;
        this.thread = thread;
        this.message = message;
        this.level = level;
        this.trace = trace;
        this.date = date;
    }

    @Override
    public String toString()
    {
        return Common.dateFormat.format(date) + "  " + thread + "  " + message + "  " + level + "  " + trace;
    }

}
