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
package org.simbasecurity.core.service.config;

import org.apache.thrift.TException;
import org.simbasecurity.core.config.ConfigurationParameter;
import org.simbasecurity.core.config.ConfigurationStore;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.config.StoreType;
import org.simbasecurity.core.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.simbasecurity.core.event.SimbaEventType.CONFIG_PARAM_CHANGED;

@Transactional
@Service("configurationService")
public class ConfigurationServiceImpl implements CoreConfigurationService, SimbaEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(CoreConfigurationService.class);

    @Qualifier("storeTypes")
    @Autowired
    private EnumMap<StoreType, ConfigurationStore> stores;
    @Autowired private EventService eventService;

    @Qualifier("ConfigurationParameterClassesList")
    @Autowired
    private List<Class<? extends ConfigurationParameter>> configurationParameterClasses;

    private List<ConfigurationParameter> configurationParameters;
    private List<String> configurationParameterNames;

    private Map<ConfigurationParameter, Object> configurationCache = new HashMap<>();

    private static final Predicate<ConfigurationParameter> isUnique = ConfigurationParameter::isUnique;

    @PostConstruct
    public void resolveConfigurationParameters() {
        configurationParameters = configurationParameterClasses.stream()
                                                               .flatMap(
                                                                   ConfigurationServiceImpl::resolveConfigurationParameters)
                                                               .collect(Collectors.toList());
        configurationParameterNames = configurationParameters.stream()
                                                             .map(ConfigurationParameter::getName)
                                                             .collect(Collectors.toList());
    }

    private static Stream<ConfigurationParameter> resolveConfigurationParameters(
        Class<? extends ConfigurationParameter> aClass) {
        if (!aClass.isEnum()) {
            throw new BeanCreationException(
                "Could not resolve configuration parameters. " + aClass + " should be an enum");
        }
        return Arrays.stream(aClass.getEnumConstants());
    }

    @Override
    public String getValue(String parameterName) throws TException {
        return findConfigurationParameter(parameterName).map(p -> p.convertToString(getValue(p)))
                                                        .orElse(null);
    }

    @Override
    public List<String> getListValue(String parameterName) throws TException {
        return findConfigurationParameter(parameterName).map(p -> ((List<?>) getValue(p)).stream()
                                                                                         .map(p::convertToString)
                                                                                         .collect(Collectors.toList()))
                                                        .orElse(null);
    }

    @Override
    public void changeParameter(String parameterName, String value) throws TException {
        findConfigurationParameter(parameterName).ifPresent(p -> changeParameter(p, p.convertToType(value)));
    }

    @Override
    public void changeListParameter(String parameterName, List<String> values) throws TException {
        findConfigurationParameter(parameterName).ifPresent(
            p -> changeParameter(p, values.stream().map(p::convertToType).collect(Collectors.toList())));
    }

    @Override
    public List<String> getUniqueParameters() throws TException {
        return configurationParameters.stream()
                                      .filter(isUnique)
                                      .map(ConfigurationParameter::getName)
                                      .collect(Collectors.toList());
    }

    @Override
    public List<String> getListParameters() throws TException {
        return configurationParameters.stream()
                                      .filter(isUnique.negate())
                                      .map(ConfigurationParameter::getName)
                                      .collect(Collectors.toList());
    }

    @Override
    public List<String> getConfigurationParameters() throws TException {
        return configurationParameterNames;
    }

    private Optional<ConfigurationParameter> findConfigurationParameter(String name) {
        return configurationParameters.stream()
                                      .filter(p -> p.getName().equals(name))
                                      .findFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(ConfigurationParameter parameter) {
        if (configurationCache.containsKey(parameter)) {
            Object value = configurationCache.get(parameter);
            LOG.debug("Read parameter '{}' from cache. Result: {}", parameter, value);
            return (T) value;
        }
        ConfigurationStore store = stores.get(parameter.getStoreType());

        T value;

        if (parameter.isUnique()) {
            value = parameter.convertToType(store.getValue(parameter));
        } else {
            List<String> valueList = store.getValueList(parameter);

            List<T> resultList = new ArrayList<>(valueList.size());

            for (String string : valueList) {
                resultList.add(parameter.convertToType(string));
            }

            value = (T) resultList;
        }

        configurationCache.put(parameter, value);

        LOG.debug("Read parameter '{}' from store. Result: {}", parameter, value);

        return value;
    }

    @Override
    public <T> void changeParameter(ConfigurationParameter parameter, T value) {
        T oldValue = changeValue(parameter, value);
        eventService.publish(SimbaEventType.CONFIG_PARAM_CHANGED, parameter.getName(), oldValue, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T changeValue(ConfigurationParameter parameter, T value) {
        configurationCache.remove(parameter);

        ConfigurationStore store = stores.get(parameter.getStoreType());

        T oldValue;
        if (parameter.isUnique()) {
            String newValue = parameter.convertToString(value);
            String oldValueString = store.setValue(parameter, newValue);
            oldValue = parameter.convertToType(oldValueString);
        } else {
            List<T> list = (List<T>) value;
            List<String> valueList = new ArrayList<>(list.size());

            for (T t : list) {
                valueList.add(parameter.convertToString(t));
            }

            List<String> oldValueList = store.setValueList(parameter, valueList);

            List<T> resultList = new ArrayList<>(oldValueList.size());

            for (String string : valueList) {
                resultList.add(parameter.convertToType(string));
            }

            oldValue = (T) resultList;
        }

        LOG.debug("Changed parameter '{}' from '{}' to '{}'", parameter, oldValue, value);

        return oldValue;
    }

    @Override
    public void eventOccured(SimbaEvent event) {
        if (CONFIG_PARAM_CHANGED.equals(event.getEventType())) {
            for (String key : event.getKeys()) {
                Pair pair = (Pair) event.getValue(key);
                changeValue(SimbaConfigurationParameter.valueOf(key), pair.getNewValue());
            }
        }
    }

    @Override
    public EnumSet<SimbaEventType> getTypesOfInterest() {
        return EnumSet.of(SimbaEventType.CONFIG_PARAM_CHANGED);
    }

    void setStores(EnumMap<StoreType, ConfigurationStore> stores) {
        this.stores = stores;
    }
}
