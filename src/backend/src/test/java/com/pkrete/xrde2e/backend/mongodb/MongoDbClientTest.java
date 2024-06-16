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
package com.pkrete.xrde2e.backend.mongodb;

import com.pkrete.xrde2e.common.event.E2EEvent;
import com.pkrete.xrde2e.common.util.Constants;

import org.niis.xrd4j.common.util.MessageHelper;

import org.bson.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for MongoDbClient class.
 *
 * @author Petteri Kivimäki
 */
public class MongoDbClientTest {

    /**
     * Test conversion from Document to E2EEvent.
     *
     * @throws java.text.ParseException
     */
    public void testDocumentToE2EEvent0() throws ParseException {
        // Init values
        String requestId = MessageHelper.generateId();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        // Create a Document
        Document document = new Document();
        document.put(Constants.COLUMN_LABEL, "Text label");
        document.put(Constants.COLUMN_PRODUCER_MEMBER, "FI-PILOT.GOV.1019125-0.TestService");
        document.put(Constants.COLUMN_SECURITY_SERVER, "FI-PILOT.COM.2229125-0.orgsecser01t");
        document.put(Constants.COLUMN_REQUEST_ID, requestId);
        document.put(Constants.COLUMN_STATUS, true);
        document.put(Constants.COLUMN_FAULT_CODE, "faultCode");
        document.put(Constants.COLUMN_FAULT_STRING, "faultString");
        document.put(Constants.COLUMN_DURATION, (long)567);
        document.put(Constants.COLUMN_BEGIN, sdf.parse("18.12.2016 08:57:30.326"));
        document.put(Constants.COLUMN_END, sdf.parse("18.12.2016 08:57:30.893"));
        document.put(Constants.COLUMN_CREATED_DATE, sdf.parse("18.12.2016 08:57:30.895"));
        MongoDbClient client = new MongoDbClient();
        // Convert the Document to E2EEvent
        E2EEvent event = client.documentToE2EEvent(document);
        // Compare values
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
