/*
 * Copyright 2013-2017 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.simbasecurity.core.service.thrift;

import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService;
import org.simbasecurity.api.service.thrift.AuthorizationService;
import org.simbasecurity.api.service.thrift.CacheService;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.FrameworkServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ThriftServlet extends FrameworkServlet {

    private final TProtocolFactory protocolFactory = new TJSONProtocol.Factory();

    private static final Map<String, Class<? extends TBaseProcessor>> processorClassMap = new HashMap<>();

    static {
        processorClassMap.put("authorizationService", AuthorizationService.Processor.class);
        processorClassMap.put("authenticationFilterService", AuthenticationFilterService.Processor.class);
        processorClassMap.put("cacheService", CacheService.Processor.class);
    }

    private final Map<String, TProcessor> processorMap = new HashMap<>();

    public ThriftServlet() {
    }

    @SuppressWarnings("unchecked")
    private <I> Class<? extends TBaseProcessor<I>> getProcessorClass(String serviceName) {
        return (Class<? extends TBaseProcessor<I>>) processorClassMap.get(serviceName);
    }

    private TProcessor getProcessor(String serviceName) {
        TProcessor tProcessor = processorMap.get(serviceName);
        if (tProcessor == null) {
            try {
                Constructor<? extends TBaseProcessor<?>> constructor = findConstructor(serviceName);
                tProcessor = constructor.newInstance(getWebApplicationContext().getBean(serviceName));
                processorMap.put(serviceName, tProcessor);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return tProcessor;
    }

    private Constructor<? extends TBaseProcessor<?>> findConstructor(String serviceName) throws NoSuchMethodException {
        Class<?> type = ClassUtils.getUserClass(getWebApplicationContext().getType(serviceName));
        Constructor<? extends TBaseProcessor<?>> constructor = null;
        try {
            constructor = getProcessorClass(serviceName).getConstructor(type);
        } catch (NoSuchMethodException ignore) {
        }
        if (constructor == null) {
            for (Class<?> aClass : type.getInterfaces()) {
                try {
                    constructor = getProcessorClass(serviceName).getConstructor(aClass);
                    break;
                } catch (NoSuchMethodException ignore) {
                }
            }
        }
        if (constructor == null) {
            throw new IllegalStateException("Can't locate correct constructor on " + getProcessorClass(serviceName).getName());
        }
        return constructor;
    }

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        TTransport inTransport;
        TTransport outTransport;

        try {
            response.setContentType("application/x-thrift");

            InputStream in = request.getInputStream();
            OutputStream out = response.getOutputStream();

            TTransport transport = new TIOStreamTransport(in, out);
            inTransport = transport;
            outTransport = transport;

            TProtocol inProtocol = protocolFactory.getProtocol(inTransport);
            TProtocol outProtocol = protocolFactory.getProtocol(outTransport);

            getProcessor(getRequestedServiceName(request)).process(inProtocol, outProtocol);
            out.flush();
        } catch (TException te) {
            throw new ServletException(te);
        }
    }

    private String getRequestedServiceName(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.substring(requestURI.lastIndexOf('/') + 1);
    }
}
