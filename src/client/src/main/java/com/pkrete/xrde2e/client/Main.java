
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

import com.pkrete.xrd4j.common.member.ConsumerMember;
import com.pkrete.xrd4j.common.message.ServiceRequest;
import com.pkrete.xrd4j.common.util.MessageHelper;
import com.pkrete.xrd4j.common.util.PropertiesUtil;
import com.pkrete.xrde2e.client.thread.E2EWorker;
import com.pkrete.xrde2e.client.util.ApplicationHelper;
import com.pkrete.xrde2e.client.util.Constants;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public Main() {
        ApplicationHelper.configureLog4j();
    }

    public static void main(String[] args) {
        new Main().start(args);
    }

    public void start(String[] args) {
        LOGGER.debug("Starting to initialize XRdE2E Client.");
        LOGGER.debug("Reading XRdE2E properties");
        String propertiesDirectoryParameter = System.getProperty(Constants.PROPERTIES_DIR_PARAM_NAME);
        if (propertiesDirectoryParameter != null) {
            this.settings = PropertiesUtil.getInstance().load(propertiesDirectoryParameter + Constants.PROPERTIES_FILE, false);
        } else {
            this.settings = PropertiesUtil.getInstance().load("/" + Constants.PROPERTIES_FILE);
        }
        LOGGER.debug("Setting XRdE2E properties.");
        String url = settings.getProperty(Constants.PROPERTIES_PROXY);
        int interval = MessageHelper.strToInt(settings.getProperty(Constants.PROPERTIES_INTERVAL));
        int threadPoolSize = MessageHelper.strToInt(settings.getProperty(Constants.PROPERTIES_THREAD_POOL_SIZE));

        LOGGER.info("\"{}\" : \"{}\"", Constants.PROPERTIES_PROXY, url);
        LOGGER.info("\"{}\" : \"{}\"", Constants.PROPERTIES_INTERVAL, interval);
        LOGGER.info("\"{}\" : \"{}\"", Constants.PROPERTIES_THREAD_POOL_SIZE, threadPoolSize);

        this.consumer = ApplicationHelper.extractConsumer(settings.getProperty(Constants.PROPERTIES_CONSUMER));
        this.targets = ApplicationHelper.extractTargets(settings, this.consumer);

        LOGGER.debug("Setting XRdE2E properties done.");

        LOGGER.info("Start processing.");
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
