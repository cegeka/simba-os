/*
 * Copyright 2011 Simba Open Source
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
 */
package org.simbasecurity.core.locator;

import java.util.Arrays;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.context.support.AbstractApplicationContext;

public class SpringAwareLocator implements Locator {

    public static String contextConfigLocation;

    private AbstractApplicationContext context;

    public SpringAwareLocator() {
    }

    public SpringAwareLocator(String contextConfigLocation) {
        SpringAwareLocator.contextConfigLocation = contextConfigLocation;
    }

    @Override
    public <B> B locate(String name, Class<B> type) {
        return (B) getApplicationContext().getBean(name, type);
    }

    private ApplicationContext getApplicationContext() {
        if (context == null) {
            BeanFactoryLocator locator = ContextSingletonBeanFactoryLocator.getInstance(contextConfigLocation);
            BeanFactoryReference reference = locator.useBeanFactory("simbaContext");
            context = (AbstractApplicationContext) reference.getFactory();
        }
        return context;
    }

    @Override
    public <B> B locate(Class<B> type) {
        String[] names = getApplicationContext().getBeanNamesForType(type);

        if (names.length != 1) {
            throw new NoSuchBeanDefinitionException(type, String.format("found {0} definitions instead of 1: {1}",
                    names.length, Arrays.asList(names)));
        }

        return locate(names[0], type);
    }

    @Override
    public Object locate(String naam) {
        Object bean = getApplicationContext().getBean(naam);

        if (bean == null) {
            throw new NoSuchBeanDefinitionException(naam, String.format("found null for: {0}", naam));
        }

        return bean;
    }

    @Override
    public boolean locatable(String name) {
        return getApplicationContext().containsBean(name);
    }

    @Override
    public void destroy() {
        if (context != null) {
            context.destroy();
        }
    }
}