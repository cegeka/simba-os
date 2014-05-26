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
package org.simbasecurity.core.service.manager;

import org.simbasecurity.core.config.ConfigurationParameter;
import org.simbasecurity.core.config.ConfigurationService;
import org.simbasecurity.core.service.manager.dto.ConfigurationParameterDTO;
import org.simbasecurity.core.service.manager.web.JsonBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Transactional
@Controller
@RequestMapping("configuration")
public class ConfigurationManagerService {

    @Autowired
    private ConfigurationService configurationService;

    @RequestMapping("findUniqueParameters")
    @ResponseBody
    public Collection<ConfigurationParameterDTO> findAll() {
        Collection<ConfigurationParameterDTO> configurationParameterDTOs = new ArrayList<ConfigurationParameterDTO>();

        ConfigurationParameter[] configurationParameters = ConfigurationParameter.values();

        for (ConfigurationParameter parameter : configurationParameters) {
            if(parameter.isUnique()) {
                ConfigurationParameterDTO parameterDTO = new ConfigurationParameterDTO(parameter, configurationService.getValue(parameter));
                configurationParameterDTOs.add(parameterDTO);
            }
        }

        return configurationParameterDTOs;
    }

    @RequestMapping("findListParameters")
    @ResponseBody
    public Collection<ConfigurationParameterDTO> findListParameters() {
        Collection<ConfigurationParameterDTO> configurationParameterDTOs = new ArrayList<ConfigurationParameterDTO>();

        ConfigurationParameter[] configurationParameters = ConfigurationParameter.values();

        for (ConfigurationParameter parameter : configurationParameters) {
            if(!parameter.isUnique()) {
                ConfigurationParameterDTO parameterDTO = new ConfigurationParameterDTO(parameter, configurationService.getValue(parameter));
                configurationParameterDTOs.add(parameterDTO);
            }
        }

        return configurationParameterDTOs;
    }

    public <T> T getValue(ConfigurationParameter parameter) {
        return configurationService.getValue(parameter);
    }

    @RequestMapping("getValue")
    @ResponseBody
    public <T> T getValue(@RequestBody String parameter) {
        return getValue(ConfigurationParameter.valueOf(parameter));
    }

    @RequestMapping("changeParameter")
    @ResponseBody
    public void changeParameter(@JsonBody("name") String name, @JsonBody("value") String value) {
        ConfigurationParameter parameter = ConfigurationParameter.valueOf(name);
        configurationService.changeParameter(parameter, parameter.convertToType(value));
    }

    @RequestMapping("changeListParameter")
    @ResponseBody
    public void changeListParameter(@JsonBody("name") String name, @JsonBody("value") List<String> values) {
        ConfigurationParameter parameter = ConfigurationParameter.valueOf(name);
        ArrayList result = new ArrayList();
        for (String value : values) {
            result.add(parameter.convertToType(value));
        }
        configurationService.changeParameter(parameter, result);
    }
}
