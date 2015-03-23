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

    public BlockOnRunFile(String runFilePath) throws IOException {
        watchedFile = Paths.get(runFilePath).toAbsolutePath();
        Files.deleteIfExists(watchedFile);
        // create entire directory tree, if possible, to create our watch file
        // in.
        Files.createDirectories(watchedFile.getParent());
        Files.createFile(watchedFile);

    }

    public void end() throws IOException {
        Files.deleteIfExists(watchedFile);
    }

<<<<<<< HEAD
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
=======
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
            	
         //   	key = watcher.take();
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
>>>>>>> afdf2a7ff41d1078a219d5a14cbb5b9ae6366d33
                }

                // now, we know something has changed in the directory, all we
                // care about though, is if our file exists.
                if (!Files.exists(watchedFile)) {
                    return;
                }
<<<<<<< HEAD

            }
        } catch (IOException e1) {
=======
            }// end while loop
        }
        catch (IOException e1)
        {
>>>>>>> afdf2a7ff41d1078a219d5a14cbb5b9ae6366d33
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
