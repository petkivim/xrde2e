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
package com.pkrete.xrde2e.common.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for removing old events from the storage.
 *
 * @author Petteri Kivimäki
 */
public class StorageCleaner implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageCleaner.class);
    private final StorageManager storageManager;
    private final int deleteOlderThan;
    private final int deleteOlderThanInterval;

    public StorageCleaner(StorageManager storageManager, int deleteOlderThan, int deleteOlderThanInterval) {
        this.storageManager = storageManager;
        this.deleteOlderThan = deleteOlderThan;
        this.deleteOlderThanInterval = deleteOlderThanInterval;
        LOGGER.info("StorageCleaner initiated.");
    }

    @Override
    public void run() {
        LOGGER.info("StorageCleaner started.");
        while (this.deleteOlderThanInterval > 0 && !Thread.currentThread().isInterrupted()) {
            this.storageManager.deleteOlderThan(this.deleteOlderThan);
            try {
                LOGGER.debug("StorageCleaner sleeping {} ms.", this.deleteOlderThanInterval);
                Thread.sleep(this.deleteOlderThanInterval);
            } catch (InterruptedException ex) {
                LOGGER.error(ex.getMessage(), ex);
                Thread.currentThread().interrupt();
            }
        }
        LOGGER.info("StorageCleaner quitted.");
    }

}
