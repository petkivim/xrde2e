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
package com.pkrete.xrde2e.backend.api.v1;

import com.pkrete.xrde2e.common.event.E2EEvent;
import com.pkrete.xrde2e.common.storage.StorageClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
/**
 * This class implements a REST API for accessing to E2E monitoring data.
 *
 * @author Petteri Kivimäki
 */
@RestController
public class E2EEventsController {

    @Autowired
    private StorageClient storageClient;

    @RequestMapping("/")
    public String index() {
        return "";
    }

    @RequestMapping(method = GET, path = "/api/v1/current", produces = "application/json")
    public List<E2EEvent> current() {
        return this.storageClient.getAllCurrent();
    }

    @RequestMapping(method = GET, value = "/api/v1/history/{securityServer:.+}", produces = "application/json")
    public List<E2EEvent> history(@PathVariable String securityServer, @RequestParam(value = "limit", defaultValue = "0") int limit) {
        return this.storageClient.getHistorical(securityServer, limit);
    }
}
