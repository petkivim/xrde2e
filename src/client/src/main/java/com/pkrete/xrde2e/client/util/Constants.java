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

/**
 * This class defines all the constants used in this application.
 *
 * @author Petteri Kivimäki
 */
public class Constants {

    /**
     * Configuration file names
     */
    public static final String PROPERTIES_FILE = "xrde2e.properties";
    public static final String LOG4J_SETTINGS_FILE = "log4j.xml";
    /**
     * Parameter names
     */
    public static final String PROPERTIES_DIR_PARAM_NAME = "propertiesDirectory";
    public static final String PROPERTIES_LOG4J_DIR_PARAM_NAME = "log4jConfDirectory";
    /**
     * Properties names
     */
    public static final String PROPERTIES_PROXY = "proxy";
    public static final String PROPERTIES_INTERVAL = "interval";
    public static final String PROPERTIES_CONSUMER = "consumer";
    public static final String PROPERTIES_SUBSYSTEM = "subsystem";
    public static final String PROPERTIES_LABEL = "label";
    public static final String PROPERTIES_SERVER = "server";
    public static final String PROPERTIES_DB_HOST = "db.host";
    public static final String PROPERTIES_DB_PORT = "db.port";
    public static final String PROPERTIES_DB_CONNECTION_STRING = "db.connectionString";
    public static final String PROPERTIES_DELETE_OLDER_THAN = "deleteOlderThan";
    public static final String PROPERTIES_DELETE_OLDER_THAN_INTERVAL = "deleteOlderThanInterval";
    public static final String PROPERTIES_THREAD_INTERVAL = "threadInterval";
}
