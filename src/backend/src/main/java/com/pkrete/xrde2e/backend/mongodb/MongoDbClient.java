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

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pkrete.xrde2e.common.event.E2EEvent;
import com.pkrete.xrde2e.common.storage.StorageClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            MongoDatabase db = mongoClient.getDatabase("xrde2emonitoring");
            MongoCollection table = db.getCollection("current_state");
            MongoCursor<Document> cursor = table.find().sort(new Document("securityServer", 1)).iterator();
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
            return null;
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
            LOGGER.info(securityServer);
            List<E2EEvent> results = new ArrayList<>();
            MongoDatabase db = mongoClient.getDatabase("xrde2emonitoring");
            MongoCollection table = db.getCollection("historical_state");
            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("securityServer", securityServer);
            MongoCursor<Document> cursor;
            if (limit == 0) {
                cursor = table.find(whereQuery).sort(new Document("begin", -1)).iterator();
            } else {
                cursor = table.find(whereQuery).sort(new Document("begin", -1)).limit(limit).iterator();
            }
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
            return null;
        }
    }

    /**
     * Converts the given document to an E2EEvent object.
     *
     * @param document Document object to be converted
     * @return E2EEvent object
     */
    private E2EEvent documentToE2EEvent(Document document) {
        String producerMember = document.getString("producerMember");
        String securityServer = document.getString("securityServer");
        String requestId = document.getString("requestId");
        boolean status = document.getBoolean("status");
        String faultCode = document.getString("faultCode");
        long duration = document.getLong("duration");
        Date begin = document.getDate("begin");
        Date end = document.getDate("end");
        return new E2EEvent(producerMember, securityServer, requestId, status, faultCode, duration, begin, end);
    }
}
