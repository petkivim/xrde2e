/*
 * The MIT License
 *
 * Copyright 2016 Petteri Kivimäki
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.pkrete.xrde2e.client.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.event.ServerHeartbeatFailedEvent;
import com.mongodb.event.ServerHeartbeatStartedEvent;
import com.mongodb.event.ServerHeartbeatSucceededEvent;
import com.mongodb.event.ServerMonitorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstract class can be used as a base class for MongoDb clients.
 *
 * @author Petteri Kivimäki
 */
public abstract class AbstractMongoDbClient implements ServerMonitorListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMongoDbClient.class);
    protected MongoClient mongoClient;
    protected boolean serverStatus;
    protected boolean pingStarted;

    /**
     * Opens a connection to the given host and port. Returns true if and only
     * if the connection is opened, otherwise false.
     *
     * @param host MongoDb host
     * @param port MongoDb port
     * @return true if and only if the connection is opened, otherwise false
     */
    protected boolean connect(String host, int port) {
        try {
            MongoClientOptions clientOptions = new MongoClientOptions.Builder()
                    .addServerMonitorListener(this)
                    .writeConcern(WriteConcern.JOURNALED)
                    .build();
            this.mongoClient = new MongoClient(new ServerAddress(host, port), clientOptions);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    /**
     * Closes the connection to the MongoDB host.
     *
     * @return true if and only if the connection was successfully closed,
     * otherwise false
     */
    protected boolean close() {
        try {
            this.mongoClient.close();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    @Override
    public void serverHearbeatStarted(ServerHeartbeatStartedEvent serverHeartbeatStartedEvent) {
        LOGGER.trace("Heartbeat started.");
        this.pingStarted = true;
    }

    @Override
    public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent serverHeartbeatSucceededEvent) {
        LOGGER.trace("Heartbeat succeeded.");
        this.serverStatus = true;
    }

    @Override
    public void serverHeartbeatFailed(ServerHeartbeatFailedEvent serverHeartbeatFailedEvent) {
        LOGGER.trace("Heartbeat failed.");
        this.serverStatus = false;
    }
}
