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
package com.pkrete.xrde2e.client;


import com.pkrete.xrde2e.client.mongodb.MongoDbManager;
import com.pkrete.xrde2e.client.thread.E2EWorker;
import com.pkrete.xrde2e.client.util.ApplicationHelper;
import com.pkrete.xrde2e.client.util.Constants;
import com.pkrete.xrde2e.common.event.E2EEventQueueProcessor;
import com.pkrete.xrde2e.common.storage.StorageCleaner;
import com.pkrete.xrde2e.common.storage.StorageManager;

import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.util.MessageHelper;
import org.niis.xrd4j.common.util.PropertiesUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main class for the application.
 *
 * @author Petteri Kivimäki
 */
public class Main {

    private static final String PROPS_LOG_PATTERN = "\"{}\" : \"{}\"";
    private static final int MILLISECONDS_TO_HOURS = 3600000;
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public Main() {
        ApplicationHelper.configureLog4j();
    }

    public static void main(String[] args) {
        new Main().start();
    }

    /**
     * Starts XRdE2E client.
     */
    public void start() {
        // Init local variables
        Properties settings;
        List<ServiceRequest> targets;
        ConsumerMember consumer;

        LOGGER.info("Starting to initialize XRdE2E Client.");
        LOGGER.info("Reading XRdE2E properties");
        String propertiesDirectoryParameter = System.getProperty(Constants.PROPERTIES_DIR_PARAM_NAME);
        if (propertiesDirectoryParameter != null) {
            settings = PropertiesUtil.getInstance().load(propertiesDirectoryParameter + Constants.PROPERTIES_FILE, false);
            LOGGER.debug("Reading XRdE2E properties from \"{}{}\".", propertiesDirectoryParameter, Constants.PROPERTIES_FILE);
        } else {
            settings = PropertiesUtil.getInstance().load("/" + Constants.PROPERTIES_FILE);
            LOGGER.debug("Reading XRdE2E properties from \"{}\".", "/" + Constants.PROPERTIES_FILE);
        }
        LOGGER.debug("Setting XRdE2E properties.");
        String url = settings.getProperty(Constants.PROPERTIES_PROXY);
        int interval = MessageHelper.strToInt(settings.getProperty(Constants.PROPERTIES_INTERVAL));
        int deleteOlderThan = MessageHelper.strToInt(settings.getProperty(Constants.PROPERTIES_DELETE_OLDER_THAN));
        int deleteOlderThanFromCurrent = MessageHelper.strToInt(settings.getProperty(Constants.PROPERTIES_DELETE_OLDER_THAN_CURRENT));
        int deleteOlderThanInterval = MILLISECONDS_TO_HOURS * MessageHelper.strToInt(settings.getProperty(Constants.PROPERTIES_DELETE_OLDER_THAN_INTERVAL));
        int threadInterval = MessageHelper.strToInt(settings.getProperty(Constants.PROPERTIES_THREAD_INTERVAL));
        String dbHost = settings.getProperty(Constants.PROPERTIES_DB_HOST);
        int dbPort = MessageHelper.strToInt(settings.getProperty(Constants.PROPERTIES_DB_PORT));
        String dbConnectionString = settings.getProperty(Constants.PROPERTIES_DB_CONNECTION_STRING);

        LOGGER.info(PROPS_LOG_PATTERN, Constants.PROPERTIES_PROXY, url);
        LOGGER.info(PROPS_LOG_PATTERN, Constants.PROPERTIES_INTERVAL, interval);
        LOGGER.info(PROPS_LOG_PATTERN, Constants.PROPERTIES_DELETE_OLDER_THAN, deleteOlderThan);
        LOGGER.info(PROPS_LOG_PATTERN, Constants.PROPERTIES_DELETE_OLDER_THAN_CURRENT, deleteOlderThanFromCurrent);
        LOGGER.info(PROPS_LOG_PATTERN, Constants.PROPERTIES_DELETE_OLDER_THAN_INTERVAL, deleteOlderThanInterval);
        LOGGER.info(PROPS_LOG_PATTERN, Constants.PROPERTIES_THREAD_INTERVAL, threadInterval);
        LOGGER.info(PROPS_LOG_PATTERN, Constants.PROPERTIES_DB_HOST, dbHost);
        LOGGER.info(PROPS_LOG_PATTERN, Constants.PROPERTIES_DB_PORT, dbPort);

        consumer = ApplicationHelper.extractConsumer(settings.getProperty(Constants.PROPERTIES_CONSUMER));
        targets = ApplicationHelper.extractTargets(settings, consumer);
        int threadPoolSize = targets.size();
        LOGGER.debug("Setting XRdE2E properties done.");
        LOGGER.info("{} monitoring targets loaded.", threadPoolSize);

        // If no targets were loaded there's nothing to do
        if (threadPoolSize == 0) {
            LOGGER.info("No monitoring targets loaded. Nothing to do here. Exit.");
            return;
        }

        LOGGER.info("Start processing.");
        // Create new storage manager
        StorageManager storageManager;
        // Check if connection string has been defined
        if (dbConnectionString != null && !dbConnectionString.isEmpty()) {
            LOGGER.debug("Use {} for database connection.", Constants.PROPERTIES_DB_CONNECTION_STRING);
            storageManager = new MongoDbManager(dbConnectionString);
        } else {
            storageManager = new MongoDbManager(dbHost, dbPort);
            LOGGER.debug("Use {} and {} for database connection.", Constants.PROPERTIES_DB_HOST, Constants.PROPERTIES_DB_PORT);
        }
        // Initialize event queue processor
        E2EEventQueueProcessor eventQueueProcessor = new E2EEventQueueProcessor(storageManager);
        // Create new thread for event processing
        Thread eventQueueProcessorThread = new Thread(eventQueueProcessor);
        // Start event processor
        eventQueueProcessorThread.start();
        // Initialize storage cleaner
        StorageCleaner storateCleaner = new StorageCleaner(storageManager, deleteOlderThan, deleteOlderThanInterval, deleteOlderThanFromCurrent);
        // Create new thread for storage cleaner
        Thread storateCleanerThread = new Thread(storateCleaner);
        // Start storage cleaner
        storateCleanerThread.start();

        // Create executor for monitoring threads
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        for (int i = 0; i < targets.size(); i++) {
            LOGGER.debug("Starting thread #{}.", i);
            Runnable worker = new E2EWorker(url, interval, targets.get(i));
            executor.execute(worker);
            try {
                // Wait a bit before starting a new thread. All the threads
                // are sending SOAP requests to the same security server
                // which is why a large number of threads cannot be started
                // simultaneously.
                LOGGER.debug("Main thread sleeping {} ms.", threadInterval);
                Thread.sleep(threadInterval);
            } catch (InterruptedException ex) {
                LOGGER.error(ex.getMessage(), ex);
                Thread.currentThread().interrupt();
            }
        }
        // The shutdown() method doesn’t cause an immediate destruction
        // of the ExecutorService. It will make the ExecutorService stop
        // accepting new tasks and shut down after all running threads
        // finish their current work.
        executor.shutdown();

        try {
            // Blocks until all tasks have completed execution after a shutdown
            // request, or the timeout occurs, or the current thread is
            // interrupted, whichever happens first. Returns true if this
            // executor is terminated and false if the timeout elapsed
            // before termination.
            while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                // Wait for executor to be terminated
                LOGGER.trace("Waiting for ExecutorService to be terminated.");
            }
        } catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        }

        // Interupt eventQueueProcessor and storateCleaner
        eventQueueProcessorThread.interrupt();
        storateCleanerThread.interrupt();

        try {
            storateCleanerThread.join();
            eventQueueProcessorThread.join();
        } catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        }
        LOGGER.info("Exit.");
    }
}
