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
package com.pkrete.xrde2e.client.mongodb;

import com.pkrete.xrde2e.common.event.E2EEvent;
import com.pkrete.xrde2e.common.util.Constants;

import org.niis.xrd4j.common.util.MessageHelper;

import junit.framework.TestCase;
import org.bson.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static junit.framework.Assert.assertEquals;

/**
 * Test cases for MongoDbManager class.
 *
 * @author Petteri Kivimäki
 */
public class MongoDbManagerTest extends TestCase {

    /**
     * Test conversion from E2EEvent to Document.
     *
     * @throws java.text.ParseException
     */
    public void testDocumentToE2EEvent0() throws ParseException {
        // Init values
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
                .build();
        MongoDbManager manager = new MongoDbManager();
        // Convert the Document to E2EEvent
        Document document = manager.eventToDocument(event);
        // Compare values
        assertEquals("Text label", document.getString(Constants.COLUMN_LABEL));
        assertEquals("FI-PILOT.GOV.1019125-0.TestService", document.getString(Constants.COLUMN_PRODUCER_MEMBER));
        assertEquals("FI-PILOT.COM.2229125-0.orgsecser01t", document.getString(Constants.COLUMN_SECURITY_SERVER));
        assertEquals(requestId, document.getString(Constants.COLUMN_REQUEST_ID));
        boolean docStatus = document.getBoolean(Constants.COLUMN_STATUS);
        assertEquals(true, docStatus);
        assertEquals("faultCode", document.getString(Constants.COLUMN_FAULT_CODE));
        assertEquals("faultString", document.getString(Constants.COLUMN_FAULT_STRING));
        long docDuration = document.getLong(Constants.COLUMN_DURATION);
        assertEquals(567, docDuration);
        assertEquals(sdf.parse("18.12.2016 08:57:30.326"), document.getDate(Constants.COLUMN_BEGIN));
        assertEquals(sdf.parse("18.12.2016 08:57:30.893"), document.getDate(Constants.COLUMN_END));
        // CreatedDate is assigned during conversion so we dont't know its value
    }
}
