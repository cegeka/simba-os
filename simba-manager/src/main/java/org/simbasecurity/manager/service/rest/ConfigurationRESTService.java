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

import org.simbasecurity.api.service.thrift.ConfigurationService;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.manager.service.rest.dto.ConfigurationParameterDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.simbasecurity.common.request.RequestConstants.SIMBA_SSO_TOKEN;

@Controller
@RequestMapping("configuration")
public class ConfigurationRESTService extends BaseRESTService<ConfigurationService.Client> {

    public ConfigurationRESTService() {
        super(new ConfigurationService.Client.Factory(), SimbaConfiguration.getConfigurationServiceURL());
    }

    @RequestMapping("findUniqueParameters")
    @ResponseBody
    public Collection<ConfigurationParameterDTO> findUniqueParameters(@CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        List<String> parameterNames = $(() -> cl(ssoToken).getUniqueParameters());

        return parameterNames.stream().map(n -> new ConfigurationParameterDTO(n, getValue(n, ssoToken))).collect(Collectors.toList());
    }

    @RequestMapping("findListParameters")
    @ResponseBody
    public Collection<ConfigurationParameterDTO> findListParameters(@CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        List<String> parameterNames = $(() -> cl(ssoToken).getListParameters());
        return parameterNames.stream()
                             .map(n -> new ConfigurationParameterDTO(n, $(() -> cl(ssoToken).getListValue(n))))
                             .collect(Collectors.toList());
    }

    @RequestMapping("getValue")
    @ResponseBody
    public String getValue(@RequestBody String parameter,
                           @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return $(() -> cl(ssoToken).getValue(parameter));
    }

    @RequestMapping("getListValue")
    @ResponseBody
    public List<String> getListValue(@RequestBody String parameter,
                                     @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return $(() -> cl(ssoToken).getListValue(parameter));
    }

    @RequestMapping("changeParameter")
    @ResponseBody
    public void changeParameter(@JsonBody("name") String name, @JsonBody("value") String value,
                                @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        $(() -> cl(ssoToken).changeParameter(name, value));
    }

    @RequestMapping("changeListParameter")
    @ResponseBody
    public void changeListParameter(@JsonBody("name") String name, @JsonBody("value") List<String> values,
                                    @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        $(() -> cl(ssoToken).changeListParameter(name, values));
    }
}
