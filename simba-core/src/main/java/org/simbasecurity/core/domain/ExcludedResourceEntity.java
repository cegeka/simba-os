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
package org.simbasecurity.core.domain;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.*;

@Entity
@Table(name = "SIMBA_EXCLUDED_RESOURCE")
public class ExcludedResourceEntity extends AbstractVersionedEntity implements ExcludedResource {

    private static final long serialVersionUID = 1579583130782665494L;

    @Id
    @GeneratedValue(generator = "simbaSequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "simbaSequence", sequenceName = "SEQ_SIMBA_EXCLUDED_RESOURCE")
    protected long id = 0;
    
    @Column(unique = true)
    private String pattern;

    @Column(name = "LOGGING_EXCLUDED")
    private boolean loggingExcluded;

    public ExcludedResourceEntity() {

    }

    public ExcludedResourceEntity(String pattern, boolean loggingExcluded) {
        this.pattern = pattern;
        this.loggingExcluded = loggingExcluded;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public boolean matches(String resource) {
        return FilenameUtils.wildcardMatch(resource, pattern);
    }

    @Override
    public boolean loggingExcluded() {
        return loggingExcluded;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ExcludedResourceEntity)) {
            return false;
        }
        ExcludedResourceEntity er = (ExcludedResourceEntity) o;
        return new EqualsBuilder()
            .append(id, er.id)
            .append(pattern, er.pattern)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
            append(id).
            append(pattern).
            toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", id).append("pattern", pattern).toString();
    }

}
