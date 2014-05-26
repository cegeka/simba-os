package org.simbasecurity.core.service.thrift;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.FrameworkServlet;

public class SpringTServlet<I> extends FrameworkServlet {

    private TProcessor processor;

    private final Class<? extends TBaseProcessor<I>> processorClass;
    private final String processorBean;

    private final TProtocolFactory inProtocolFactory;

    private final TProtocolFactory outProtocolFactory;

    private final Collection<Map.Entry<String, String>> customHeaders;

    protected SpringTServlet(Class<? extends TBaseProcessor<I>> processorClass, String processorBean, TProtocolFactory inProtocolFactory, TProtocolFactory outProtocolFactory) {
        this.processorClass = processorClass;
        this.processorBean = processorBean;
        this.inProtocolFactory = inProtocolFactory;
        this.outProtocolFactory = outProtocolFactory;

        this.customHeaders = new ArrayList<Map.Entry<String, String>>();
    }

    protected SpringTServlet(Class<? extends TBaseProcessor<I>> processorClass, String processorBean, TProtocolFactory protocolFactory) {
        this(processorClass, processorBean, protocolFactory, protocolFactory);
    }

    private TProcessor getProcessor() {
        if (processor == null) {
            try {
                Constructor<? extends TBaseProcessor<I>> constructor = findConstructor();
                processor = constructor.newInstance(getWebApplicationContext().getBean(processorBean));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return processor;
    }

    private Constructor<? extends TBaseProcessor<I>> findConstructor() throws NoSuchMethodException {
        Class<?> type = ClassUtils.getUserClass(getWebApplicationContext().getType(processorBean));
        Constructor<? extends TBaseProcessor<I>> constructor = null;
        try {
            constructor = processorClass.getConstructor(type);
        } catch (NoSuchMethodException ignore) {
        }
        if (constructor == null) {
            for (Class<?> aClass : type.getInterfaces()) {
                try {
                    constructor = processorClass.getConstructor(aClass);
                    break;
                } catch (NoSuchMethodException ignore) {
                }
            }
        }
        if (constructor == null) {
            throw new IllegalStateException("Can't locate correct constructor on " + processorClass.getName());
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

            if (null != this.customHeaders) {
                for (Map.Entry<String, String> header : this.customHeaders) {
                    response.addHeader(header.getKey(), header.getValue());
                }
            }
            InputStream in = request.getInputStream();
            OutputStream out = response.getOutputStream();

            TTransport transport = new TIOStreamTransport(in, out);
            inTransport = transport;
            outTransport = transport;

            TProtocol inProtocol = inProtocolFactory.getProtocol(inTransport);
            TProtocol outProtocol = outProtocolFactory.getProtocol(outTransport);

            getProcessor().process(inProtocol, outProtocol);
            out.flush();
        } catch (TException te) {
            throw new ServletException(te);
        }
    }

    public void addCustomHeader(final String key, final String value) {
        this.customHeaders.add(new Map.Entry<String, String>() {
            public String getKey() {
                return key;
            }

            public String getValue() {
                return value;
            }

            public String setValue(String value) {
                return null;
            }
        });
    }

    public void setCustomHeaders(Collection<Map.Entry<String, String>> headers) {
        this.customHeaders.clear();
        this.customHeaders.addAll(headers);
    }
}
