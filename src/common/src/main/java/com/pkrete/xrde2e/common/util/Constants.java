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
package com.pkrete.xrde2e.common.util;

/**
 * This class defines all the constants used in this application.
 *
 * @author Petteri Kivimäki
 */
public class Constants {

    /**
     * Database, table and column names
     */
    public static final String DB_NAME = "xrde2emonitoring";
    public static final String TABLE_CURRENT_STATE = "current_state";
    public static final String TABLE_HISTORICAL_STATE = "historical_state";
    public static final String COLUMN_PRODUCER_MEMBER = "producerMember";
    public static final String COLUMN_SECURITY_SERVER = "securityServer";
    public static final String COLUMN_REQUEST_ID = "requestId";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_FAULT_CODE = "faultCode";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_BEGIN = "begin";
    public static final String COLUMN_END = "end";
    public static final String COLUMN_CREATED_DATE = "createdDate";
}
