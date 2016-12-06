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
package com.pkrete.xrde2e.common.event;

import com.pkrete.xrde2e.common.storage.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for monitoring E2E event queue and saving the
 * objects added to the queue to the storage. After the object has been added,
 * it's removed from the queue.
 *
 * Saving operation is run in a JDK Timer based thread, that is different from
 * the main thread. This implementation makes the actual storage operation
 * invisible for the monitoring operation, as it runs in the background in
 * another thread. The thread is started when the application starts, and it's
 * making blocking method calls, which means that it keeps on running until the
 * shutdown.
 *
 * @author Petteri Kivimäki
 */
public class E2EEventQueueProcessor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(E2EEventQueueProcessor.class);
    private final E2EEventQueue queue;
    private final StorageManager storageManager;

    /**
     * Constructs and initializes a new E2EEventQueueProcessor object.
     * @param storageManager storage manager is responsible for storing 
     * E2E events to the storage
     */
    public E2EEventQueueProcessor(StorageManager storageManager) {
        this.queue = E2EEventQueue.getInstance();
        this.storageManager = storageManager;
        LOGGER.info("E2EEventQueueProcessor initiated.");
    }

    /**
     * Monitors the E2E event queue and saves event to the storage when one
     * becomes available.
     */
    @Override
    public void run() {
        LOGGER.info("E2EEventQueueProcessor started.");
        E2EEvent event;
        while ((event = queue.take()) != null && !Thread.currentThread().isInterrupted()) {
            LOGGER.debug("New event received: \"{}\"", event.toString());
            this.storageManager.add(event);
        }
    }
}
