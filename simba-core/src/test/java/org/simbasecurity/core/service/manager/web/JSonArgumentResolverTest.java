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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.core.service.manager.dto.RoleDTO;
import org.simbasecurity.core.service.manager.dto.UserDTO;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class JSonArgumentResolverTest {

    private JSonArgumentResolver resolver = new JSonArgumentResolver();

    @Test
    public void testResolveArgument_SimpleString() throws Exception {
        Object result = resolver.resolveArgument(methodParameter(String.class, null), null, nativeWebRequest("\"This is a test\""), null);

        assertThat(result, instanceOf(String.class));
        assertEquals("This is a test", result);
    }

    @Test
    public void testResolveArgument_ObjectString() throws Exception {
        Object result = resolver.resolveArgument(methodParameter(String.class, null), null, nativeWebRequest("{\"test\":\"This is a test\"}"), null);

        assertThat(result, instanceOf(String.class));
        assertEquals("This is a test", result);
    }

    @Test
    public void testResolveArgument_List() throws Exception {
        String jsonBody = "{\"test\":[{\"testVar\":\"1\"},{\"testVar\":\"2\"}]}";
        Object result = resolver.resolveArgument(methodParameter(List.class, TestObject.class), null, nativeWebRequest(jsonBody), null);

        assertThat(result, instanceOf(List.class));

        List resultList = (List) result;
        assertTrue(resultList.contains(new TestObject("1")));
        assertTrue(resultList.contains(new TestObject("2")));
    }

    @SuppressWarnings( "unused" )
    public static class TestObject {
        private String testVar;
        public TestObject(String testVar){this.testVar = testVar;}

        public TestObject() {
        }

        public void setTestVar(String t) {
            this.testVar = t;
        }
        public String getTestVar() {
            return testVar;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TestObject that = (TestObject) o;

            return testVar.equals(that.testVar);
        }

        @Override
        public int hashCode() {
            return testVar.hashCode();
        }
    }

    @Test
    public void testResolveArgument_Set() throws Exception {
        Object result = resolver.resolveArgument(methodParameter(Set.class, String.class), null, nativeWebRequest("{\"test\": [\"1\", \"2\"]}"), null);

        assertThat(result, instanceOf(Set.class));

        Set resultList = (Set) result;
        assertTrue(resultList.contains("1"));
        assertTrue(resultList.contains("2"));
    }

    @Test
    public void testResolveArgument_SortedSet() throws Exception {
        Object result = resolver.resolveArgument(methodParameter(SortedSet.class, String.class), null, nativeWebRequest("{\"test\": [\"1\", \"2\"]}"), null);

        assertThat(result, instanceOf(SortedSet.class));

        SortedSet resultList = (SortedSet) result;
        assertTrue(resultList.contains("1"));
        assertTrue(resultList.contains("2"));
    }

    private MethodParameter methodParameter(Class type, final Class genericCollectionType) {
        MethodParameter parameter = mock(MethodParameter.class);
        when(parameter.getParameterAnnotation(JsonBody.class)).thenReturn(new JsonBody() {

            @Override
            public String value() {
                return "test";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return JsonBody.class;
            }
        });
        when(parameter.getParameterType()).thenReturn(type);
        when(parameter.getGenericParameterType()).thenReturn(new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { genericCollectionType };
            }

            @Override
            public Type getRawType() {
                return genericCollectionType;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        });
        return parameter;
    }

    private NativeWebRequest nativeWebRequest(String jsonBody) {
        NativeWebRequest request = mock(NativeWebRequest.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(request.getNativeRequest(HttpServletRequest.class)).thenReturn(servletRequest);
        when(servletRequest.getAttribute(JSonArgumentResolver.JSON_REQUEST_BODY)).thenReturn(jsonBody);
        return request;
    }

    @Test
    public void testComplexMessage_DoesNotCrash() throws Exception {
        String message = "{\"id\":4,\"version\":0,\"name\":\"admin\"}";
        resolver.resolveArgument(methodParameter(RoleDTO.class, null), null, nativeWebRequest(message), null);

        String message2 = "[{\"id\":6,\"version\":1,\"userName\":\"groupie\",\"name\":\"Groupie\",\"firstName\":\"Groupie\",\"inactiveDate\":null,\"status\":\"ACTIVE\",\"successURL\":null,\"language\":\"nl_NL\",\"passwordChangeRequired\":false,\"changePasswordOnNextLogon\":false}]";
        resolver.resolveArgument(methodParameter(List.class, UserDTO.class), null, nativeWebRequest(message2), null);
    }
}
