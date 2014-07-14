package com.adam4.mylogger;

import java.sql.Timestamp;

import com.adam4.common.Common;
import com.adam4.mylogger.MyLogger.LogLevel;

public class Error
{

    String server;
    String application;
    String thread;
    String message;
    LogLevel level;
    Timestamp time;

    Error(String server, String application, String thread, String message, LogLevel level, Timestamp time)
    {
        this.server = server;
        this.application = application;
        this.thread = thread;
        this.message = message;
        this.level = level;
        this.time = time;
    }

    @Override
    public String toString()
    {
        return Common.dateFormat.format(time) + "  " + thread + "  " + message + "  " + level;
    }

}
