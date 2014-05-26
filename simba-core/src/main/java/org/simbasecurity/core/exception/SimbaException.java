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
package org.simbasecurity.core.exception;


public class SimbaException extends RuntimeException {
    private static final long serialVersionUID = -1935511566359965499L;

    private final SimbaMessageKey code;

    /**
     * @param code the error code; should never be <tt>null</tt>
     */
    public SimbaException(SimbaMessageKey code) {
        super(code.toString());
        this.code = code;
    }

    /**
     * @param code the error code; should never be <tt>null</tt>
     */
    public SimbaException(SimbaMessageKey code, Throwable cause) {
        super(code.toString(), cause);
        this.code = code;
    }

    /**
     * @param code    the error code; should never be <tt>null</tt>
     * @param message an message containing more explanation for the error
     */
    public SimbaException(SimbaMessageKey code, String message) {
        super(message);

        if (code == null) {
            throw new NullPointerException("code should never be null");
        }

        this.code = code;
    }

    /**
     * @return the error code
     */
    public SimbaMessageKey getMessageKey() {
        return code;
    }
}


