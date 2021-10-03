package com.liner.ragebot.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class AsyncFileAppender {
    private static final int THREADS = (int) Math.round(Runtime.getRuntime().availableProcessors() * .75);
    private final ExecutorService threadPool = Executors.newFixedThreadPool(THREADS);
    private final Path path;
    private AsynchronousFileChannel log;
    private final AtomicLong position = new AtomicLong(0);

    public AsyncFileAppender(String path) {
        this.path = Paths.get(path);
    }

    protected void append(String string) {
        AsynchronousFileChannel log = log();
        ByteBuffer buffer = ByteBuffer.allocateDirect(string.length());
        buffer.put(string.getBytes());
        buffer.flip();
        long position = this.position.getAndAdd(string.length());
        log.write(buffer, position);
    }

    public void close() {
        if (null != log) {
            try {
                log.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private AsynchronousFileChannel log() {
        if (null == this.log) {
            Set<OpenOption> openOptions = new HashSet<>();
            openOptions.add(StandardOpenOption.CREATE);
            openOptions.add(StandardOpenOption.WRITE);

            try {
                this.log = AsynchronousFileChannel.open(this.path, openOptions, threadPool);
                this.position.set(this.log.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return log;
    }

}