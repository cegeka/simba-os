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
package org.simbasecurity.core.spring;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.simbasecurity.core.locator.GlobalContext;
import org.simbasecurity.core.locator.SpringAwareLocator;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

public class SimbaContextLoaderListener extends ContextLoaderListener {

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        super.contextDestroyed(event);
        GlobalContext.destroy();
    }

    @Override
    protected void customizeContext(ServletContext servletContext,
                                    ConfigurableWebApplicationContext applicationContext) {
        SpringAwareLocator.contextConfigLocation = servletContext.getInitParameter(LOCATOR_FACTORY_SELECTOR_PARAM);
    }

}
