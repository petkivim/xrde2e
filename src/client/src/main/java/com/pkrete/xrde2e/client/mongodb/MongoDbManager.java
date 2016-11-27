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

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.pkrete.common.event.E2EEvent;
import com.pkrete.common.storage.StorageManager;
import java.util.Date;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements operations for adding, updating and deleting data
 * to/from MongoDb.
 *
 * @author Petteri Kivimäki
 */
public class MongoDbManager extends AbstractMongoDbClient implements StorageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDbManager.class);

    /**
     * Constructs and initializes a new MongoDbManager object.
     *
     * @param host database host
     * @param port database port
     */
    public MongoDbManager(String host, int port) {
        super.connect(host, port);
    }

    @Override
    /**
     * Adds a new E2EEvent to the database.
     *
     * @param event E2EEvent to be added to the database
     * @return true or false
     */
    public boolean add(E2EEvent event) {
        if (!this.insert("xrde2emonitoring", "historical_state", event)) {
            return false;
        }
        if (!this.update("xrde2emonitoring", "current_state", event)) {
            return false;
        }
        return true;
    }

    /**
     * Inserts a new E2EEvent to the database.
     *
     * @param database database name
     * @param collection collection name
     * @param event E2EEvent to be added to the database
     * @return true if and only the event was successfully added, otherwise
     * false
     */
    protected boolean insert(String database, String collection, E2EEvent event) {
        try {
            MongoDatabase db = mongoClient.getDatabase(database);
            MongoCollection table = db.getCollection(collection);
            Document document = new Document();
            document.put("producerMember", event.getProducerMember());
            document.put("securityServer", event.getSecurityServer());
            document.put("requestId", event.getRequestId());
            document.put("status", event.isStatus());
            document.put("faultCode", event.getFaultCode());
            document.put("duration", event.getDuration());
            document.put("begin", event.getBegin());
            document.put("end", event.getEnd());
            document.put("createdDate", new Date());
            table.insertOne(document);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return false;
        }
        return true;

    }

    /**
     * Deletes an event with the same security server code from the database.
     *
     * @param database database name
     * @param collection collection name
     * @param event E2EEvent to be deleted from the database
     * @return true if and only the event was successfully deleted, otherwise
     * false
     */
    protected boolean delete(String database, String collection, E2EEvent event) {
        try {
            MongoDatabase db = mongoClient.getDatabase(database);
            MongoCollection table = db.getCollection(collection);
            Document document = new Document();
            document.put("securityServer", event.getSecurityServer());
            table.deleteOne(document);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    /**
     * Updates the given E2EEvent to the database based on the security server
     * code. If an item with the given security server code does not exist,
     * it is created.
     *
     * @param database database name
     * @param collection collection name
     * @param event E2EEvent to be updated to the database
     * @return true if and only the event was successfully updated, otherwise
     * false
     */
    protected boolean update(String database, String collection, E2EEvent event) {
        try {
            MongoDatabase db = mongoClient.getDatabase(database);
            MongoCollection table = db.getCollection(collection);
            Document document = new Document();
            document.put("producerMember", event.getProducerMember());
            document.put("securityServer", event.getSecurityServer());
            document.put("requestId", event.getRequestId());
            document.put("status", event.isStatus());
            document.put("faultCode", event.getFaultCode());
            document.put("duration", event.getDuration());
            document.put("begin", event.getBegin());
            document.put("end", event.getEnd());
            document.put("createdDate", new Date());
            BasicDBObject query = new BasicDBObject();
            Bson newDocument = new Document("$set", document);
            query.append("securityServer", event.getSecurityServer());
            table.updateOne(query, newDocument, (new UpdateOptions()).upsert(true));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }
}