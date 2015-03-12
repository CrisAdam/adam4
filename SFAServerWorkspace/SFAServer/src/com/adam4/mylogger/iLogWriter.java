package com.adam4.mylogger;

public interface iLogWriter
{
    public void close();

    boolean addError(Error error);

    public void reconnect(ReAdd reAdd);
}
