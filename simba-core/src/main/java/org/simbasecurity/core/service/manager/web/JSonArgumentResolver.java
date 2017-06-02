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

package org.simbasecurity.core.service.manager.web;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.Charset;
import java.util.*;

public class JSonArgumentResolver implements HandlerMethodArgumentResolver {
    static final String JSON_REQUEST_BODY = "JSON_REQUEST_BODY";

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(JsonBody.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {
        String body = getRequestBody(nativeWebRequest);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(body);
        JsonNode objectNode = rootNode.get(methodParameter.getParameterAnnotation(JsonBody.class).value());
        if (objectNode == null) {
            objectNode = rootNode;
        }
        if (Collection.class.isAssignableFrom(methodParameter.getParameterType())) {
            return readAsCollection(objectMapper, objectNode, methodParameter);
        } else {
            return readAsObject(objectMapper, objectNode, methodParameter);
        }
    }

    private Object readAsObject(ObjectMapper objectMapper, JsonNode objectNode, MethodParameter methodParameter) throws Exception {
        return objectMapper.readValue(objectNode, methodParameter.getParameterType());
    }

    private Collection<?> readAsCollection(ObjectMapper objectMapper, JsonNode objectNode, MethodParameter methodParameter) throws Exception {
        Collection<Object> collection = (Collection<Object>) createCollection(methodParameter.getParameterType());
        ParameterizedType type = (ParameterizedType) methodParameter.getGenericParameterType();
        Class<?> actualType = (Class<?>) type.getActualTypeArguments()[0];
        for (JsonNode jsonNode : objectNode) {
            collection.add(objectMapper.readValue(jsonNode, actualType));
        }
        return collection;
    }

    private Collection<?> createCollection(Class<?> type) {
        if (SortedSet.class.isAssignableFrom(type)) {
            return new TreeSet<Object>();
        } else if (Set.class.isAssignableFrom(type)) {
            return new LinkedHashSet<Object>();
        } else if (List.class.isAssignableFrom(type)) {
            return new ArrayList<Object>();
        }
        throw new IllegalArgumentException("No implementation defined for " + type.getName());
    }

    private String getRequestBody(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String jsonBody = (String) servletRequest.getAttribute(JSON_REQUEST_BODY);
        if (jsonBody == null) {
            try {
                String body = IOUtils.toString(servletRequest.getInputStream(), Charset.defaultCharset());
                servletRequest.setAttribute(JSON_REQUEST_BODY, body);
                return body;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return jsonBody;
    }
}
