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
package com.pkrete.xrde2e.client.member;

import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.member.ProducerMember;

/**
 * This class extends the ProducerMember class and adds a new label instance
 * variable to it.
 *
 * @author Petteri Kivimäki
 */
public class E2EProducerMember extends ProducerMember {

    private String label;

    public E2EProducerMember(String xRoadInstance, String memberClass, String memberCode, String subsystemCode, String serviceCode, String label)
            throws XRd4JException {
        super(xRoadInstance, memberClass, memberCode, subsystemCode, serviceCode);
        this.label = label;
    }

    /**
     * Returns the label of this E2EProducerMember.
     *
     * @return the label this E2EProducerMember
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label of this E2EProducerMember.
     *
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
