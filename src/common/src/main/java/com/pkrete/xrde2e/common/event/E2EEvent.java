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
package com.pkrete.xrde2e.common.event;

import java.io.Serializable;
import java.util.Date;

/**
 * This class represents X-Road E2E event and holds information related to a
 * single event.
 *
 * @author Petteri Kivimäki
 */
public final class E2EEvent implements Serializable {

    private String label;
    private String producerMember;
    private String securityServer;
    private String requestId;
    private boolean status;
    private String faultCode;
    private String faultString;
    private long duration;
    private Date begin;
    private Date end;
    private Date createdDate;

    private E2EEvent(E2EEventBuilder builder) {
        this.label = builder.label;
        this.producerMember = builder.producerMember;
        this.securityServer = builder.securityServer;
        this.requestId = builder.requestId;
        this.status = builder.status;
        this.faultCode = builder.faultCode;
        this.faultString = builder.faultString;
        this.duration = builder.duration;
        this.begin = builder.begin;
        this.end = builder.end;
        this.createdDate = builder.createdDate;
    }

    /**
     * Returns the label of this event.
     *
     * @return the label this event
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label of this event.
     *
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the identifier of the producer member as a String.
     *
     * @return identifier of the producer member as a String
     */
    public String getProducerMember() {
        return producerMember;
    }

    /**
     * Sets the identifier of the producer member as a String.
     *
     * @param producerMember the producerMember to set
     */
    public void setProducerMember(String producerMember) {
        this.producerMember = producerMember;
    }

    /**
     * Returns the identifier of the security server as a String.
     *
     * @return identifier of the security server as a String
     */
    public String getSecurityServer() {
        return securityServer;
    }

    /**
     * Sets the identifier of the security server as a String.
     *
     * @param securityServer the securityServer to set
     */
    public void setSecurityServer(String securityServer) {
        this.securityServer = securityServer;
    }

    /**
     * Returns the identifier of the event.
     *
     * @return unique event identifier
     */
    public String getRequestId() {
        return this.requestId;
    }

    /**
     * Sets the identifier of the event.
     *
     * @param requestId the requestId to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Returns true if and only if the status is OK, and otherwise false.
     *
     * @return true if and only if the status is OK, and otherwise false
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Sets the status that tells if the result of the E2EEvent was OK or not.
     * True if and only if the status is OK, and otherwise false.
     *
     * @param status the status to set
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Returns the fault code returned by the service provider security server
     * if the status is not OK.
     *
     * @return fault code returned by the service provider security server
     */
    public String getFaultCode() {
        return faultCode;
    }

    /**
     * Sets the fault code returned by the service provider security server if
     * the status is not OK.
     *
     * @param faultCode the faultCode to set
     */
    public void setFaultCode(String faultCode) {
        this.faultCode = faultCode;
    }

    /**
     * Returns the fault string returned by the service provider security server
     * if the status is not OK.
     *
     * @return fault string returned by the service provider security server
     */
    public String getFaultString() {
        return faultString;
    }

    /**
     * Sets the fault string returned by the service provider security server if
     * the status is not OK.
     *
     * @param faultString the faultString to set
     */
    public void setFaultString(String faultString) {
        this.faultString = faultString;
    }

    /**
     * Duration of the event in milli seconds.
     *
     * @return the duration of the event in milli seconds
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Sets the duration of the event in milli seconds
     *
     * @param duration the duration to set
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Returns the begin time of the event.
     *
     * @return the begin time of the event
     */
    public Date getBegin() {
        return begin;
    }

    /**
     * Sets the begin time of the event.
     *
     * @param begin the begin to set
     */
    public void setBegin(Date begin) {
        this.begin = begin;
    }

    /**
     * Returns the end time of the event.
     *
     * @return the end time of the event
     */
    public Date getEnd() {
        return end;
    }

    /**
     * Sets the end time of the event.
     *
     * @param end the end to set
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * Returns the creation time of the event.
     *
     * @return the creation time of the event
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the creation time of the event.
     *
     * @param createdDate the creation time to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    /**
     * Returns a String presentation of this E2EEvent object.
     *
     * @return String presentation of this E2EEvent object
     */
    public String toString() {
        StringBuilder builder = new StringBuilder(this.producerMember).append("::");
        builder.append(this.securityServer).append("::");
        builder.append(this.requestId);
        return builder.toString();
    }

    @Override
    /**
     * Indicates whether some other object is "equal to" this E2EEvent.
     *
     * @param o the reference object with which to compare
     * @return true only if the specified object is also an E2EEvent and it has
     * the same id as this E2EEvent
     */
    public boolean equals(Object o) {
        return (o instanceof E2EEvent && this.requestId.equals(((E2EEvent) o).getRequestId()));
    }

    @Override
    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    public int hashCode() {
        return this.requestId.hashCode();
    }

    /**
     * Static nested inner builder class for E2EEvents.
     */
    public static class E2EEventBuilder {

        private String label;
        private String producerMember;
        private String securityServer;
        private String requestId;
        private boolean status;
        private String faultCode;
        private String faultString;
        private long duration;
        private Date begin;
        private Date end;
        private Date createdDate;

        public E2EEventBuilder label(final String label) {
            this.label = label;
            return this;
        }

        public E2EEventBuilder producerMember(final String producerMember) {
            this.producerMember = producerMember;
            return this;
        }

        public E2EEventBuilder securityServer(final String securityServer) {
            this.securityServer = securityServer;
            return this;
        }

        public E2EEventBuilder requestId(final String requestId) {
            this.requestId = requestId;
            return this;
        }

        public E2EEventBuilder status(final boolean status) {
            this.status = status;
            return this;
        }

        public E2EEventBuilder faultCode(final String faultCode) {
            this.faultCode = faultCode;
            return this;
        }

        public E2EEventBuilder faultString(final String faultString) {
            this.faultString = faultString;
            return this;
        }

        public E2EEventBuilder duration(final long duration) {
            this.duration = duration;
            return this;
        }

        public E2EEventBuilder begin(final Date begin) {
            this.begin = begin;
            return this;
        }

        public E2EEventBuilder end(final Date end) {
            this.end = end;
            return this;
        }

        public E2EEventBuilder createdDate(final Date createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public E2EEvent build() {
            return new E2EEvent(this);
        }
    }
}
