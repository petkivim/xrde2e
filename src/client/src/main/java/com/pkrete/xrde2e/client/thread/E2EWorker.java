/*
 * The MIT License
 *
 * Copyright 2016- Petteri Kivimäki
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
package com.pkrete.xrde2e.client.thread;

import com.pkrete.xrde2e.client.member.E2EProducerMember;
import com.pkrete.xrde2e.common.event.E2EEvent;
import com.pkrete.xrde2e.common.event.E2EEventQueue;

import org.niis.xrd4j.client.SOAPClient;
import org.niis.xrd4j.client.SOAPClientImpl;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.message.ServiceResponse;
import org.niis.xrd4j.common.util.MessageHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * This class is responsible for calling security server's listMethods service
 * and storing the response in the storage.
 *
 * @author Petteri Kivimäki
 */
public class E2EWorker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Runnable.class);
    private final String url;
    private final int interval;
    private final ServiceRequest request;
    private static int count = 0;
    private final int number;

    /**
     * Constructs and initializes a new E2EWorker object.
     *
     * @param url
     * @param interval
     * @param request
     */
    public E2EWorker(String url, int interval, ServiceRequest request) {
        this.url = url;
        this.interval = interval;
        this.request = request;
        this.number = count;
        count++;
    }

    @Override
    public void run() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Thread #{} starting to monitor security server \"{}\".", this.number, request.getSecurityServer());
        }
        // Init variables for counting requests and time
        int requestCount = 0;
        String label = ((E2EProducerMember) this.request.getProducer()).getLabel();
        // Keep on sending messages forever
        while (!Thread.currentThread().isInterrupted()) {
            // Init variables for logging
            long throughput = 0;
            boolean status = false;
            String faultCode = "";
            String faultString = "";
            Date begin = null;
            Date end = null;
            // Get unique ID for the message
            String reqId = MessageHelper.generateId();
            try {
                // Set message ID
                request.setId(reqId);
                LOGGER.debug("Thread #{} sending message #{}, ID : \"{}\".", this.number, requestCount, reqId);
                long msgStartTime = System.currentTimeMillis();
                begin = new Date();
                // Create new client for sending the message
                SOAPClient client = new SOAPClientImpl();
                // Send the ServiceRequest, result is returned as ServiceResponse object
                ServiceResponse<String, String> serviceResponse = client.listMethods(request, url);
                // Calculate message throughput time
                throughput = System.currentTimeMillis() - msgStartTime;
                end = new Date();
                // Check SOAP response for SOAP Fault
                if (serviceResponse.hasError()) {
                    status = false;
                    faultCode = serviceResponse.getErrorMessage().getFaultCode();
                    faultString = serviceResponse.getErrorMessage().getFaultString();
                    LOGGER.error("Thread #{} received response containing SOAP Fault for message #{}, ID : \"{}\".", this.number, requestCount, reqId);
                    LOGGER.error("Fault code : \"{}\".", serviceResponse.getErrorMessage().getFaultCode());
                } else {
                    status = true;
                    LOGGER.debug("Thread #{} received response for message #{}, ID : \"{}\".", this.number, requestCount, reqId);
                }
                LOGGER.info("Server \"{}\" status: {}. Request \"{}\" duration {}ms. Fault code: \"{}\"",
                        request.getSecurityServer().getServerCode(), status, reqId, throughput, faultCode);
            } catch (Exception ex) {
                LOGGER.error("Thread #{} sending message #{} failed, ID : \"{}\".", this.number, requestCount, reqId);
                LOGGER.error(ex.getMessage(), ex);
            }
            // Create new E2EEvent for the storage and put it in the queue
            E2EEvent event = new E2EEvent.E2EEventBuilder()
                    .label(label)
                    .producerMember(request.getProducer().toString())
                    .securityServer(request.getSecurityServer().toString())
                    .requestId(reqId)
                    .status(status)
                    .faultCode(faultCode)
                    .faultString(faultString)
                    .duration(throughput)
                    .begin(begin)
                    .end(end)
                    .build();
            E2EEventQueue.getInstance().put(event);
            // Sleep...
            if (this.interval > 0) {
                try {
                    LOGGER.debug("Thread #{} sleeping {} ms.", this.number, this.interval);
                    Thread.sleep(this.interval);
                } catch (InterruptedException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    Thread.currentThread().interrupt();
                }
            }
            // Update request counter
            requestCount++;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Thread #{} quitting to monitor security server \"{}\".", this.number, request.getSecurityServer());
        }
    }
}
