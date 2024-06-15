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
package com.pkrete.xrde2e.client.util;

import com.pkrete.xrde2e.client.member.E2EProducerMember;

import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.message.ServiceRequest;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for ApplicationHelper utility class.
 *
 * @author Petteri Kivimäki
 */
public class ApplicationHelperTest {

    /**
     * Test valid consumer.
     */
    @Test
    public void testExtractConsumer0() {
        ConsumerMember consumer = ApplicationHelper.extractConsumer("FI-PILOT.GOV.1019125-0.DemoClient");
        assertEquals(false, consumer == null);
        assertEquals("FI-PILOT.GOV.1019125-0.DemoClient", consumer.toString());
    }

    /**
     * Test invalid consumer - only 3 blocks.
     */
    public void testExtractConsumer1() {
        ConsumerMember consumer = ApplicationHelper.extractConsumer("FI-PILOT.GOV.1019125-0");
        assertEquals(true, consumer == null);
    }

    /**
     * Test invalid consumer - 5 blocks.
     */
    public void testExtractConsumer2() {
        ConsumerMember consumer = ApplicationHelper.extractConsumer("FI-PILOT.GOV.1019125-0.DemoClient.com");
        assertEquals(true, consumer == null);
    }

    /**
     * Valid subsystem and server.
     */
    public void testExtractTarget0() {
        Properties props = new Properties();
        props.put("0." + Constants.PROPERTIES_SUBSYSTEM, "FI-PILOT.GOV.1019125-0.TestService");
        props.put("0." + Constants.PROPERTIES_SERVER, "FI-PILOT.COM.2229125-0.orgsecser01t");
        props.put("0." + Constants.PROPERTIES_LABEL, "Server 01");
        ConsumerMember consumer = ApplicationHelper.extractConsumer("FI-PILOT.MUN.9879125-0.E2EClient");
        List<ServiceRequest> targets = ApplicationHelper.extractTargets(props, consumer);
        // Compare results
        ServiceRequest request = targets.get(0);
        assertEquals(false, request == null);
        assertEquals(false, request.getProducer() == null);
        assertEquals(false, request.getSecurityServer() == null);
        // Check producer
        assertEquals("FI-PILOT.GOV.1019125-0.TestService.listMethods", request.getProducer().toString());
        assertEquals("FI-PILOT", request.getProducer().getXRoadInstance());
        assertEquals("GOV", request.getProducer().getMemberClass());
        assertEquals("1019125-0", request.getProducer().getMemberCode());
        assertEquals("TestService", request.getProducer().getSubsystemCode());
        assertEquals("Server 01", ((E2EProducerMember) request.getProducer()).getLabel());
        // Check security server
        assertEquals("FI-PILOT.COM.2229125-0.orgsecser01t", request.getSecurityServer().toString());
        assertEquals("FI-PILOT", request.getSecurityServer().getXRoadInstance());
        assertEquals("COM", request.getSecurityServer().getMemberClass());
        assertEquals("2229125-0", request.getSecurityServer().getMemberCode());
        assertEquals("orgsecser01t", request.getSecurityServer().getServerCode());
    }

    /**
     * Valid subsystem and server, but server has dots in its name. No label.
     */
    public void testExtractTarget1() {
        Properties props = new Properties();
        props.put("0." + Constants.PROPERTIES_SUBSYSTEM, "FI-DEV.GOV.2229125-9.Demo2Service");
        props.put("0." + Constants.PROPERTIES_SERVER, "FI-DEV.COM.2229125-1.orgsecser01.qa.com");
        ConsumerMember consumer = ApplicationHelper.extractConsumer("FI-DEV.MUN.9879125-0.Client");
        List<ServiceRequest> targets = ApplicationHelper.extractTargets(props, consumer);
        // Compare results
        ServiceRequest request = targets.get(0);
        assertEquals(false, request == null);
        assertEquals(false, request.getProducer() == null);
        assertEquals(false, request.getSecurityServer() == null);
        // Check producer
        assertEquals("FI-DEV.GOV.2229125-9.Demo2Service.listMethods", request.getProducer().toString());
        assertEquals("FI-DEV", request.getProducer().getXRoadInstance());
        assertEquals("GOV", request.getProducer().getMemberClass());
        assertEquals("2229125-9", request.getProducer().getMemberCode());
        assertEquals("Demo2Service", request.getProducer().getSubsystemCode());
        assertEquals("", ((E2EProducerMember) request.getProducer()).getLabel());
        // Check security server
        assertEquals("FI-DEV.COM.2229125-1.orgsecser01.qa.com", request.getSecurityServer().toString());
        assertEquals("FI-DEV", request.getSecurityServer().getXRoadInstance());
        assertEquals("COM", request.getSecurityServer().getMemberClass());
        assertEquals("2229125-1", request.getSecurityServer().getMemberCode());
        assertEquals("orgsecser01.qa.com", request.getSecurityServer().getServerCode());

    }

    /**
     * Invalid subsystem - only 3 blocks.
     */
    public void testExtractTarget2() {
        Properties props = new Properties();
        props.put("0." + Constants.PROPERTIES_SUBSYSTEM, "FI.GOV.2229125-0");
        props.put("0." + Constants.PROPERTIES_SERVER, "FI.COM.2229125-0.orgsecser01.qa.com");
        ConsumerMember consumer = ApplicationHelper.extractConsumer("FI.MUN.9879125-0.E2EClient");
        List<ServiceRequest> targets = ApplicationHelper.extractTargets(props, consumer);
        // Compare results
        assertEquals(true, targets.isEmpty());
    }

    /**
     * Invalid subsystem - 5 blocks.
     */
    public void testExtractTarget3() {
        Properties props = new Properties();
        props.put("0." + Constants.PROPERTIES_SUBSYSTEM, "FI-PILOT.GOV.2229125-8.TestService.getData");
        props.put("0." + Constants.PROPERTIES_SERVER, "FI-PILOT.COM.2229125-2.orgsecser01.qa.com");
        ConsumerMember consumer = ApplicationHelper.extractConsumer("FI-PILOT.MUN.9879125-7.E2EClient");
        List<ServiceRequest> targets = ApplicationHelper.extractTargets(props, consumer);
        // Compare results
        assertEquals(true, targets.isEmpty());
    }

    /**
     * Invalid server - 3 blocks.
     */
    public void testExtractTarget4() {
        Properties props = new Properties();
        props.put("0." + Constants.PROPERTIES_SUBSYSTEM, "FI-TEST.GOV.2229125-7.TestService");
        props.put("0." + Constants.PROPERTIES_SERVER, "FI-TEST.COM.2229125-3");
        ConsumerMember consumer = ApplicationHelper.extractConsumer("FI-TEST.MUN.9879125-4.E2EClient");
        List<ServiceRequest> targets = ApplicationHelper.extractTargets(props, consumer);
        // Compare results
        assertEquals(true, targets.isEmpty());
    }
}
