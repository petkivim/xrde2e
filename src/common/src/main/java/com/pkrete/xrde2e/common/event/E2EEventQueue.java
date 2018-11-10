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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class presents a queue of E2EEvent objects that are waiting to be stored
 * in the storage. Every E2E operation generates a new event that is stored to
 * the storage.
 *
 * This queue is accessed via BlockingQueue interface and the queue
 * implementation uses LinkedBlockingQueue class. BlockingQueue implementation
 * is thread-safe. All queuing methods achieve their effects atomically using
 * internal locks or other forms of concurrency control.
 *
 * This class implements Singleton design pattern, which means that only one
 * object is created runtime, and it's referenced by all the other objects.
 * Because of this, thread safe implementation is essential.
 */
public final class E2EEventQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(E2EEventQueue.class);
    private final BlockingQueue<E2EEvent> queue;
    private static E2EEventQueue ref;

    /**
     * The class implements Singleton design pattern, so constructor must be
     * defined as private.
     */
    private E2EEventQueue() {
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Returns the EventQueue Singleton object. If the object doesn't exist yet,
     * it's created.
     *
     * @return EventQueue Singleton object
     */
    public static E2EEventQueue getInstance() {
        if (ref == null) {
            ref = new E2EEventQueue();
        }
        return ref;
    }

    /**
     * Inserts a new E2E event to the queue, waiting if necessary for space to
     * become available.
     *
     * @param event the event to be inserted
     */
    public void put(E2EEvent event) {
        try {
            queue.put(event);
        } catch (InterruptedException iex) {
            LOGGER.error(iex.getMessage(), iex);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Retrieves and removes the head of this queue, waiting if necessary until
     * an element becomes available.
     *
     * @return head of the queue
     */
    public E2EEvent take() {
        try {
            return queue.take();
        } catch (InterruptedException iex) {
            LOGGER.error(iex.getMessage(), iex);
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * Returns true if this queue contains no elements.
     *
     * @return true if this queue contains no elements
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
