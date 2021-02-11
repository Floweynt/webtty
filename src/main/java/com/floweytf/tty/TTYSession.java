package com.floweytf.tty;

import com.floweytf.betterlogger.BetterLogger;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.eclipse.jetty.websocket.api.Session;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TTYSession {
    private final Thread runner;
    private final FileChannel fileChannel;
    private BetterLogger logger;
    private final Session websock;
    private final String tty;
    public TTYSession(String tty, Session session) throws SessionException {
        runner = new Thread(this::poll);
        runner.setName("session-" + runner.getId());
        try {
            logger = new BetterLogger(Main.logger);
            logger.loggerName += "/" + runner.getName() + "-init";

            Path file = Paths.get(tty);
            logger.info("opening tty: "  + tty);
            fileChannel = FileChannel.open(file);
            logger.info("tty has been opened successfully!");
            this.websock = session;
            this.tty = tty;
            runner.start();
        }
        catch (IOException e) {
            logger.error("Failed to open tty!", (Throwable)e);
            throw new SessionException("Failed to open tty!");
        }

        logger.loggerName = Main.logger.loggerName + "/" + runner.getName();
    }

    @ApiStatus.Internal
    public void poll() {
        logger.info("starting poll loop");
        try {
            ByteBuffer buf = ByteBuffer.allocate(64);
            while (true) {
                /*int size = fileChannel.read(buf);
                if(size != 0)
                    websock.getRemote().sendString(StandardCharsets.UTF_8.decode(buf).toString());*/
            }
        }
        /*catch (IOException e) {
            websock.close(500, e.getMessage());
        }*/
        catch (Exception e) {
            // we chill
        }
        logger.info("Poll loop ended");
    }

    public void write(@NotNull String s) throws SessionException {
        try {
            ByteBuffer buf = ByteBuffer.wrap(s.getBytes());
            while(buf.hasRemaining())
                fileChannel.write(buf);
        }
        catch (IOException e) {
            throw new SessionException("Underlying IO failed!" + e.getMessage());
        }
    }

    public String getTTY() {
        return tty;
    }

    public void finalize() {
        close();
    }

    public void close() {
        runner.interrupt();
        try {
            fileChannel.close();
        }
        catch (IOException e) {
            // ignore
        }

        // wait for it to actually close
        while(runner.isAlive());
    }
}