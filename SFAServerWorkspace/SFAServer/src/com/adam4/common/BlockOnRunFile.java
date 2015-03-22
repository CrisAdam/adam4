package com.adam4.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.adam4.mylogger.MyLogger.LogLevel;

public class BlockOnRunFile
{
    private File watchedFile;
    private Path path;
    private WatchService watcher;

    public void end()
    {
        watchedFile.delete();
    }

    public BlockOnRunFile(String runFilePath) throws IOException
    {
        watcher = FileSystems.getDefault().newWatchService();
        path = FileSystems.getDefault().getPath(runFilePath.substring(0, runFilePath.lastIndexOf(FileSystems.getDefault().getSeparator())));
        watchedFile = new File(runFilePath);
        watchedFile.createNewFile();
    }

    public void block()
    {
        try
        {
            WatchKey key;
            key = path.register(watcher, StandardWatchEventKinds.ENTRY_DELETE);
            // stall until the game is supposed to end
            // reset key to allow new events to be detected
            while (key.reset())
            {
            	Common.log.logMessage("Log file key reset loop", LogLevel.DEBUG);
                try
                {
                    for (WatchEvent<?> event : key.pollEvents())
                    {
                        WatchEvent.Kind<?> kind = event.kind();
                        if (kind == StandardWatchEventKinds.OVERFLOW)
                        {
                            Common.log.logMessage("File watcher overflow", LogLevel.INFO);
                            if (!watchedFile.exists())
                            {
                            	Common.log.logMessage("File watcher file exists loop", LogLevel.INFO);
                                // do nothing
                                ;
                            }
                            break;
                        }
                        if (kind == StandardWatchEventKinds.ENTRY_DELETE)
                        {
                            @SuppressWarnings("unchecked")
                            WatchEvent<Path> ev = (WatchEvent<Path>) event;
                            Path filename = ev.context();
                            if (filename.toAbsolutePath().toString().equals(watchedFile.getAbsolutePath().toString()))
                            {
                                watcher.close();
                                Common.log.logMessage("watchedFile file has been deleted", LogLevel.INFO);
                                break;
                            }
                        }
                    }
                    Thread.sleep(1000);
                }
                catch (Exception e)
                {
                    watcher.close();
                    Common.log.logMessage(e, LogLevel.INFO);
                    continue;
                }
            }// end while loop
        }
        catch (IOException e1)
        {
            Common.log.logMessage(e1, LogLevel.ERROR);
        }
    }
}
