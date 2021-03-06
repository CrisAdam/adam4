package com.adam4.common;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.adam4.mylogger.MyLogger.LogLevel;

public class BlockOnRunFile {
    private final Path watchedFile;

    public BlockOnRunFile(String runFilePath)
    {
        watchedFile = Paths.get(runFilePath).toAbsolutePath();
        try 
        {
			Files.deleteIfExists(watchedFile);
	        // create entire directory tree, if possible, to create our watch file
	        // in.
	        Files.createDirectories(watchedFile.getParent());
	        Files.createFile(watchedFile);
	        Common.log.logMessage("Created watch file at " + watchedFile.toString(), LogLevel.INFO);
		} 
        catch (IOException e) 
        {
        	Common.log.logMessage("Unable to create watch file" + watchedFile.toString(), LogLevel.ERROR);
		}


    }

    public void end() throws IOException {
        Files.deleteIfExists(watchedFile);
    }

    public void block() {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            final WatchKey key = watchedFile.getParent().register(watcher,
                    StandardWatchEventKinds.ENTRY_DELETE);

            // stall until the game is supposed to end
            // reset key to allow new events to be detected
            while (key.reset()) {

                // wait for a file to be deleted (or an overflow....)
                if (key != watcher.take()) {
                    throw new IllegalStateException(
                            "Only our key is registered, only it should be taken");
                }

                // now, we know something has changed in the directory, all we
                // care about though, is if our file exists.
                if (!Files.exists(watchedFile)) {
                    return;
                }

            }
        } catch (IOException e1) {
            Common.log.logMessage(e1, LogLevel.ERROR);
        } catch (InterruptedException e) {
            // propagate an interrupt... we can't handle it here.....
            // just let the file be removed, and we die....
            Thread.currentThread().interrupt();
            Common.log.logMessage(e, LogLevel.WARN);
        } finally {
            try {
                Files.deleteIfExists(watchedFile);
            } catch (IOException e) {
                // unable to delete the sentry file.....
                Common.log.logMessage(e, LogLevel.WARN);
            }
        }
    }
}