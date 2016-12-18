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

import com.pkrete.xrd4j.common.util.MessageHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import junit.framework.TestCase;

/**
 * Test cases for E2EEvent class.
 *
 * @author Petteri Kivimäki
 */
public class E2EEventTest extends TestCase {

    /**
     * Test E2EEventBuilder.
     *
     * @throws java.text.ParseException
     */
    public void testE2EEventBuilder0() throws ParseException {
        String requestId = MessageHelper.generateId();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        // Create new E2EEvent
        E2EEvent event = new E2EEvent.E2EEventBuilder()
                .label("Text label")
                .producerMember("FI-PILOT.GOV.1019125-0.TestService")
                .securityServer("FI-PILOT.COM.2229125-0.orgsecser01t")
                .requestId(requestId)
                .status(true)
                .faultCode("faultCode")
                .faultString("faultString")
                .duration(567)
                .begin(sdf.parse("18.12.2016 08:57:30.326"))
                .end(sdf.parse("18.12.2016 08:57:30.893"))
                .createdDate(sdf.parse("18.12.2016 08:57:30.895"))
                .build();
        // Compare
        assertEquals("Text label", event.getLabel());
        assertEquals("FI-PILOT.GOV.1019125-0.TestService", event.getProducerMember());
        assertEquals("FI-PILOT.COM.2229125-0.orgsecser01t", event.getSecurityServer());
        assertEquals(requestId, event.getRequestId());
        assertEquals(true, event.isStatus());
        assertEquals("faultCode", event.getFaultCode());
        assertEquals("faultString", event.getFaultString());
        assertEquals(567, event.getDuration());
        assertEquals(sdf.parse("18.12.2016 08:57:30.326"), event.getBegin());
        assertEquals(sdf.parse("18.12.2016 08:57:30.893"), event.getEnd());
        assertEquals(sdf.parse("18.12.2016 08:57:30.895"), event.getCreatedDate());
    }
}
