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
package org.simbasecurity.manager.service.rest;

import org.simbasecurity.api.service.thrift.CacheService;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("cache")
public class CacheRESTService extends BaseRESTService<CacheService.Client> {

    public CacheRESTService() {
        super(new CacheService.Client.Factory(), SimbaConfiguration.getCacheServiceURL());
    }

    @RequestMapping("refresh")
    @ResponseBody
    public void refreshCache() {
        $(() -> cl().refreshCacheIfEnabled());
    }

    @RequestMapping("enable")
    @ResponseBody
    public void enableCache() {
        $(() -> cl().setCacheEnabled(true));
    }

    @RequestMapping("isEnabled")
    @ResponseBody
    public boolean isCacheEnabled() {
        return $(() -> cl().isCacheEnabled());
    }

    @RequestMapping("disable")
    @ResponseBody
    public void disableCache() {
        $(() -> cl().setCacheEnabled(false));
    }
}
