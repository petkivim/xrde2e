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
package com.pkrete.xrde2e.backend.mongodb;

import com.pkrete.xrd4j.common.util.MessageHelper;
import com.pkrete.xrde2e.common.event.E2EEvent;
import com.pkrete.xrde2e.common.util.Constants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.bson.Document;

/**
 * Test cases for MongoDbClient class.
 *
 * @author Petteri Kivimäki
 */
public class MongoDbClientTest extends TestCase {

    /**
     * Test conversion from Document to E2EEvent.
     *
     * @throws java.text.ParseException
     */
    public void testDocumentToE2EEvent0() throws ParseException {
        // Init values
        String label = "Text label";
        String producerMember = "FI-PILOT.GOV.1019125-0.TestService";
        String securityServer = "FI-PILOT.COM.2229125-0.orgsecser01t";
        String requestId = MessageHelper.generateId();
        boolean status = true;
        String faultCode = "faultCode";
        String faultString = "faultString";
        long duration = 567;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        Date begin = sdf.parse("18.12.2016 08:57:30.326");
        Date end = sdf.parse("18.12.2016 08:57:30.893");
        Date createdDate = sdf.parse("18.12.2016 08:57:30.895");
        // Create a Document
        Document document = new Document();
        document.put(Constants.COLUMN_LABEL, label);
        document.put(Constants.COLUMN_PRODUCER_MEMBER, producerMember);
        document.put(Constants.COLUMN_SECURITY_SERVER, securityServer);
        document.put(Constants.COLUMN_REQUEST_ID, requestId);
        document.put(Constants.COLUMN_STATUS, status);
        document.put(Constants.COLUMN_FAULT_CODE, faultCode);
        document.put(Constants.COLUMN_FAULT_STRING, faultString);
        document.put(Constants.COLUMN_DURATION, duration);
        document.put(Constants.COLUMN_BEGIN, begin);
        document.put(Constants.COLUMN_END, end);
        document.put(Constants.COLUMN_CREATED_DATE, createdDate);
        MongoDbClient client = new MongoDbClient();
        // Convert the Document to E2EEvent
        E2EEvent event = client.documentToE2EEvent(document);
        // Compare values
        assertEquals(label, event.getLabel());
        assertEquals(producerMember, event.getProducerMember());
        assertEquals(securityServer, event.getSecurityServer());
        assertEquals(requestId, event.getRequestId());
        assertEquals(status, event.isStatus());
        assertEquals(faultCode, event.getFaultCode());
        assertEquals(faultString, event.getFaultString());
        assertEquals(duration, event.getDuration());
        assertEquals(begin, event.getBegin());
        assertEquals(end, event.getEnd());
        assertEquals(createdDate, event.getCreatedDate());
    }
}
