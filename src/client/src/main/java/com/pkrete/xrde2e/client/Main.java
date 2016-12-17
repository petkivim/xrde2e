
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

import com.pkrete.xrde2e.common.event.E2EEventQueueProcessor;
import com.pkrete.xrde2e.client.mongodb.MongoDbManager;
import com.pkrete.xrde2e.common.storage.StorageManager;
import com.pkrete.xrd4j.common.member.ConsumerMember;
import com.pkrete.xrd4j.common.message.ServiceRequest;
import com.pkrete.xrd4j.common.util.MessageHelper;
import com.pkrete.xrd4j.common.util.PropertiesUtil;
import com.pkrete.xrde2e.client.thread.E2EWorker;
import com.pkrete.xrde2e.client.util.ApplicationHelper;
import com.pkrete.xrde2e.client.util.Constants;
import com.pkrete.xrde2e.common.storage.StorageCleaner;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Main class for the application.
 *
 * @author Petteri Kivimäki
 */
public class Main {

    private Properties settings;
    private List<ServiceRequest> targets;
    private ConsumerMember consumer;
    private final int millisecondsToHours = 3600000;
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public Main() {
        ApplicationHelper.configureLog4j();
    }

    public static void main(String[] args) {
        new Main().start(args);
    }

    public void start(String[] args) {
        LOGGER.info("Starting to initialize XRdE2E Client.");
        LOGGER.info("Reading XRdE2E properties");
        String propertiesDirectoryParameter = System.getProperty(Constants.PROPERTIES_DIR_PARAM_NAME);
        if (propertiesDirectoryParameter != null) {
            this.settings = PropertiesUtil.getInstance().load(propertiesDirectoryParameter + Constants.PROPERTIES_FILE, false);
            LOGGER.debug("Reading XRdE2E properties from \"{}\".", propertiesDirectoryParameter + Constants.PROPERTIES_FILE);
        } else {
            this.settings = PropertiesUtil.getInstance().load("/" + Constants.PROPERTIES_FILE);
            LOGGER.debug("Reading XRdE2E properties from \"{}\".", "/" + Constants.PROPERTIES_FILE);
        }
        LOGGER.debug("Setting XRdE2E properties.");
        String url = settings.getProperty(Constants.PROPERTIES_PROXY);
        int interval = MessageHelper.strToInt(settings.getProperty(Constants.PROPERTIES_INTERVAL));
        int deleteOlderThan = MessageHelper.strToInt(settings.getProperty(Constants.PROPERTIES_DELETE_OLDER_THAN));
        int deleteOlderThanInterval = this.millisecondsToHours * MessageHelper.strToInt(settings.getProperty(Constants.PROPERTIES_DELETE_OLDER_THAN_INTERVAL));
        String dbHost = settings.getProperty(Constants.PROPERTIES_DB_HOST);
        int dbPort = MessageHelper.strToInt(settings.getProperty(Constants.PROPERTIES_DB_PORT));
        String dbConnectionString = settings.getProperty(Constants.PROPERTIES_DB_CONNECTION_STRING);

        LOGGER.info("\"{}\" : \"{}\"", Constants.PROPERTIES_PROXY, url);
        LOGGER.info("\"{}\" : \"{}\"", Constants.PROPERTIES_INTERVAL, interval);
        LOGGER.info("\"{}\" : \"{}\"", Constants.PROPERTIES_DELETE_OLDER_THAN, deleteOlderThan);
        LOGGER.info("\"{}\" : \"{}\"", Constants.PROPERTIES_DELETE_OLDER_THAN_INTERVAL, deleteOlderThanInterval);
        LOGGER.info("\"{}\" : \"{}\"", Constants.PROPERTIES_DB_HOST, dbHost);
        LOGGER.info("\"{}\" : \"{}\"", Constants.PROPERTIES_DB_PORT, dbPort);

        this.consumer = ApplicationHelper.extractConsumer(settings.getProperty(Constants.PROPERTIES_CONSUMER));
        this.targets = ApplicationHelper.extractTargets(settings, this.consumer);
        int threadPoolSize = this.targets.size();
        LOGGER.debug("Setting XRdE2E properties done.");
        LOGGER.info("{} monitoring targets loaded.", threadPoolSize);

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
        StorageCleaner storateCleaner = new StorageCleaner(storageManager, deleteOlderThan, deleteOlderThanInterval);
        // Create new thread for storage cleaner
        Thread storateCleanerThread = new Thread(storateCleaner);
        // Start storage cleaner
        storateCleanerThread.start();

        // Create executor for monitoring threads
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        for (int i = 0; i < this.targets.size(); i++) {
            LOGGER.debug("Starting thread #{}.", i);
            Runnable worker = new E2EWorker(url, interval, this.targets.get(i));
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

    }
}
