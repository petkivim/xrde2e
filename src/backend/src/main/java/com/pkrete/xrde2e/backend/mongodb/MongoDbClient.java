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

import com.pkrete.xrde2e.common.event.E2EEvent;
import com.pkrete.xrde2e.common.storage.StorageClient;
import com.pkrete.xrde2e.common.util.Constants;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class implements all the database operations needed by the API layer.
 *
 * @author Petteri Kivimäki
 */
@Service("mongoDbClient")
public class MongoDbClient implements StorageClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDbClient.class);
    @Autowired
    private MongoClient mongoClient;

    /**
     * Returns all the current state entries from the database.
     *
     * @return list of E2EEvents containing the latest state of the monitored
     * security servers
     */
    @Override
    public List<E2EEvent> getAllCurrent() {
        try {
            List<E2EEvent> results = new ArrayList<>();
            MongoDatabase db = mongoClient.getDatabase(Constants.DB_NAME);
            MongoCollection table = db.getCollection(Constants.TABLE_CURRENT_STATE);
            MongoCursor<Document> cursor = table.find().sort(new Document(Constants.COLUMN_SECURITY_SERVER, 1)).iterator();
            try {
                while (cursor.hasNext()) {
                    results.add(this.documentToE2EEvent(cursor.next()));
                }
                return results;
            } finally {
                cursor.close();
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return new ArrayList<>();
        }
    }

    /**
     * Returns all the E2EEvents related to the specified security server. If
     * limit is defined, only the given number of events is returned. The events
     * are ordered in ascending order by the event begin timestamp.
     *
     * @param securityServer security server code
     * @param limit how many events is returned. All the events are returned if
     * limit is 0.
     * @return list of E2EEvents related to the given security server
     */
    @Override
    public List<E2EEvent> getHistorical(String securityServer, int limit) {
        try {
            LOGGER.info("Fetch historical data for target \"{}\". Limit is set to {}.", securityServer, limit);
            List<E2EEvent> results = new ArrayList<>();
            MongoDatabase db = mongoClient.getDatabase(Constants.DB_NAME);
            MongoCollection table = db.getCollection(Constants.TABLE_HISTORICAL_STATE);
            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put(Constants.COLUMN_SECURITY_SERVER, securityServer);
            MongoCursor<Document> cursor;
            if (limit == 0) {
                cursor = table.find(whereQuery).sort(new Document(Constants.COLUMN_BEGIN, -1)).iterator();
            } else {
                cursor = table.find(whereQuery).sort(new Document(Constants.COLUMN_BEGIN, -1)).limit(limit).iterator();
            }
            try {
                while (cursor.hasNext()) {
                    results.add(this.documentToE2EEvent(cursor.next()));
                }
                LOGGER.info("Found {} historical monitoring events.", results.size());
                return results;
            } finally {
                cursor.close();
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return new ArrayList<>();
        }
    }

    /**
     * Converts the given document to an E2EEvent object.
     *
     * @param document Document object to be converted
     * @return E2EEvent object
     */
    protected E2EEvent documentToE2EEvent(Document document) {
        String label = document.getString(Constants.COLUMN_LABEL);
        String producerMember = document.getString(Constants.COLUMN_PRODUCER_MEMBER);
        String securityServer = document.getString(Constants.COLUMN_SECURITY_SERVER);
        String requestId = document.getString(Constants.COLUMN_REQUEST_ID);
        boolean status = document.getBoolean(Constants.COLUMN_STATUS);
        String faultCode = document.getString(Constants.COLUMN_FAULT_CODE);
        String faultString = document.getString(Constants.COLUMN_FAULT_STRING);
        long duration = document.getLong(Constants.COLUMN_DURATION);
        Date begin = document.getDate(Constants.COLUMN_BEGIN);
        Date end = document.getDate(Constants.COLUMN_END);
        Date createdDate = document.getDate(Constants.COLUMN_CREATED_DATE);
        return new E2EEvent.E2EEventBuilder()
                .label(label)
                .producerMember(producerMember)
                .securityServer(securityServer)
                .requestId(requestId)
                .status(status)
                .faultCode(faultCode)
                .faultString(faultString)
                .duration(duration)
                .begin(begin)
                .end(end)
                .createdDate(createdDate)
                .build();
    }
}
