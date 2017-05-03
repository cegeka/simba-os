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
package org.simbasecurity.core.domain;

/**
 * A URLRule is a specific {@link org.simbasecurity.core.domain.Rule} where the resource is a URL.
 *
 * @since 1.0
 */
public interface URLRule extends Rule {

    /**
     * @return whether getting the URL is allowed.
     */
    boolean isGetAllowed();

    /**
     * @param allowed sets whether getting the URL is allowed
     */
    void setGetAllowed(boolean allowed);

    /**
     * @return whether posting to the URL is allowed.
     */
    boolean isPostAllowed();

    /**
     * @param allowed sets whether posting to the URL is allowed
     */
    void setPostAllowed(boolean allowed);

    /**
     * @return whether putting on the URL is allowed.
     */
    boolean isPutAllowed();

    /**
     * @param allowed sets whether putting on the URL is allowed
     */
    void setPutAllowed(boolean allowed);

    /**
     * @return whether deleting from the URL is allowed.
     */
    boolean isDeleteAllowed();

    /**
     * @param allowed sets whether deleting from the URL is allowed
     */
    void setDeleteAllowed(boolean allowed);

    /**
     * Check if operation is allowed
     *
     * @param operation the operation to be checked
     * @return <code>true</code> if operation is allowed; <code>false</code> if operation is not allowed
     */
    boolean isAllowed(URLOperationType operation);


}