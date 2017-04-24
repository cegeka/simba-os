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

package org.simbasecurity.core.audit;

public enum AuditLogLevel {
	OFF(Integer.MAX_VALUE), INFO(1000), TRACE(500), ALL(Integer.MIN_VALUE);

    private final int value;

    private AuditLogLevel(int value) {
        this.value = value;
    }

    public boolean isLoggable(AuditLogLevel level) {
        return value != OFF.value && level.value >= value;
    }
}
