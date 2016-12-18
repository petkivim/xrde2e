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
import com.mongodb.client.result.DeleteResult;
import com.pkrete.xrde2e.common.event.E2EEvent;
import com.pkrete.xrde2e.common.storage.StorageManager;
import com.pkrete.xrde2e.common.util.Constants;
import java.util.Calendar;
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
     */
    protected MongoDbManager() {

    }

    /**
     * Constructs and initializes a new MongoDbManager object.
     *
     * @param connectionString connection string that describes the hosts to be
     * used and options
     */
    public MongoDbManager(String connectionString) {
        super.connect(connectionString);
        // Remove all the entries from the current_state collection
        this.deleteAll(Constants.DB_NAME, Constants.TABLE_CURRENT_STATE);
    }

    /**
     * Constructs and initializes a new MongoDbManager object.
     *
     * @param host database host
     * @param port database port
     */
    public MongoDbManager(String host, int port) {
        super.connect(host, port);
        // Remove all the entries from the current_state collection
        this.deleteAll(Constants.DB_NAME, Constants.TABLE_CURRENT_STATE);
    }

    @Override
    /**
     * Adds a new E2EEvent to the database.
     *
     * @param event E2EEvent to be added to the database
     * @return true or false
     */
    public boolean add(E2EEvent event) {
        if (!this.insert(Constants.DB_NAME, Constants.TABLE_HISTORICAL_STATE, event)) {
            return false;
        }
        if (!this.update(Constants.DB_NAME, Constants.TABLE_CURRENT_STATE, event)) {
            return false;
        }
        return true;
    }

    /**
     * Deletes all the entries older than the given days from the historical
     * state collection.
     *
     * @param days number of days
     * @return true if and only if the entries were deleted successfully,
     * otherwise false
     */
    @Override
    public boolean deleteOlderThan(int days) {
        try {
            // Days must be negative
            int dayCount = days > 0 ? days * -1 : days;
            // Create a calendar object with today date.
            Calendar calendar = Calendar.getInstance();
            // Move calendar backwards according to the given day count
            calendar.add(Calendar.DATE, dayCount);
            LOGGER.info("Delete documents older than \"{}\" from \"{}\" collection.", calendar.getTime(), Constants.TABLE_HISTORICAL_STATE);
            MongoDatabase db = mongoClient.getDatabase(Constants.DB_NAME);
            MongoCollection table = db.getCollection(Constants.TABLE_HISTORICAL_STATE);
            Document document = new Document();
            document.put("$lt", calendar.getTime());
            Bson query = new Document(Constants.COLUMN_CREATED_DATE, document);
            DeleteResult deleteResult = table.deleteMany(query);
            LOGGER.info("Deleted {} documents from \"{}\" collection.", deleteResult.getDeletedCount(), Constants.TABLE_HISTORICAL_STATE);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
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
            Document document = this.eventToDocument(event);
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
            document.put(Constants.COLUMN_SECURITY_SERVER, event.getSecurityServer());
            table.deleteOne(document);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    /**
     * Updates the given E2EEvent to the database based on the security server
     * code. If an item with the given security server code does not exist, it
     * is created.
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
            Document document = this.eventToDocument(event);
            BasicDBObject query = new BasicDBObject();
            Bson newDocument = new Document("$set", document);
            query.append(Constants.COLUMN_SECURITY_SERVER, event.getSecurityServer());
            table.updateOne(query, newDocument, (new UpdateOptions()).upsert(true));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    /**
     * Deletes all the entries from the given collection.
     *
     * @param database database name
     * @param collection collection name
     * @return true if and only if all the entries were successfully deleted,
     * otherwise false
     */
    protected boolean deleteAll(String database, String collection) {
        try {
            MongoDatabase db = mongoClient.getDatabase(database);
            MongoCollection table = db.getCollection(collection);
            table.deleteMany(new Document());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return false;
        }
        return true;

    }

    /**
     * Converts the given E2EEvent to a corresponding Document
     *
     * @param event E2EEvent to be converted
     * @return Document representing the given E2EEvent
     */
    protected Document eventToDocument(E2EEvent event) {
        Document document = new Document();
        document.put(Constants.COLUMN_LABEL, event.getLabel());
        document.put(Constants.COLUMN_PRODUCER_MEMBER, event.getProducerMember());
        document.put(Constants.COLUMN_SECURITY_SERVER, event.getSecurityServer());
        document.put(Constants.COLUMN_REQUEST_ID, event.getRequestId());
        document.put(Constants.COLUMN_STATUS, event.isStatus());
        document.put(Constants.COLUMN_FAULT_CODE, event.getFaultCode());
        document.put(Constants.COLUMN_FAULT_STRING, event.getFaultString());
        document.put(Constants.COLUMN_DURATION, event.getDuration());
        document.put(Constants.COLUMN_BEGIN, event.getBegin());
        document.put(Constants.COLUMN_END, event.getEnd());
        document.put(Constants.COLUMN_CREATED_DATE, new Date());
        return document;
    }
}
