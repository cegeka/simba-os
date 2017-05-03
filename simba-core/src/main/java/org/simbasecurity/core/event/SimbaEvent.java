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
package org.simbasecurity.core.event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SimbaEvent implements Serializable {

    private static final long serialVersionUID = -4779128738914446249L;

    private final SimbaEventType eventType;

    private Map<String, Object> data;

    public SimbaEvent(SimbaEventType eventType, String key,
                      Object value) {
        this.eventType = eventType;
        data = new HashMap<String, Object>();
        data.put(key, value);
    }

    public SimbaEvent(SimbaEventType eventType, String value) {
        this(eventType, eventType.name(), value);
    }

    public SimbaEventType getEventType() {
        return eventType;
    }

    public Object getValue(String key) {
        return data.get(key);
    }

    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    public Set<String> getKeys() {
        return data.keySet();
    }

//	@SuppressWarnings("unchecked")
//	public <T> T getParameterOldValue() {
//		return (T) value;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public <T> T getParameterNewValue() {
//		return (T) parameterNewValue;
//	}

}
