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
package org.simbasecurity.core.service.thrift;

import org.simbasecurity.api.service.thrift.*;
import org.simbasecurity.common.util.StringUtil;
import org.simbasecurity.common.util.ThriftDate;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.*;
import org.simbasecurity.core.domain.condition.TimeCondition;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.domain.user.EmailFactory;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@Service
@SuppressWarnings("unchecked")
public class ThriftAssembler {

    private CoreConfigurationService coreConfigurationService;
    private EmailFactory emailFactory;

    @Autowired
    public ThriftAssembler(CoreConfigurationService coreConfigurationService, EmailFactory emailFactory) {
        this.coreConfigurationService = coreConfigurationService;
        this.emailFactory = emailFactory;
    }

    private Map<Class<?>, Function<?, ?>> classMappers = new HashMap<>();

    {
        classMappers.put(User.class, in -> this.assemble((User) in));
        classMappers.put(TUser.class, in -> this.assemble((TUser) in));
        classMappers.put(Session.class, in -> this.assemble((Session) in));
        classMappers.put(Role.class, in -> this.assemble((Role) in));
        classMappers.put(TRole.class, in -> this.assemble((TRole) in));
        classMappers.put(Policy.class, in -> this.assemble((Policy) in));
        classMappers.put(TPolicy.class, in -> this.assemble((TPolicy) in));
        classMappers.put(Group.class, in -> this.assemble((Group) in));
        classMappers.put(TGroup.class, in -> this.assemble((TGroup) in));
        classMappers.put(Condition.class, in -> this.assemble((Condition) in));
        classMappers.put(TCondition.class, in -> this.assemble((TCondition) in));
        classMappers.put(Rule.class, in -> this.assemble((Rule) in));
    }

    public <I, O> List<O> list(Collection<I> input) {
        return assemble(input, Collectors.toList());
    }

    public <I, O> Set<O> set(Collection<I> input) {
        return assemble(input, Collectors.toSet());
    }

    public <I, O, C extends Collection<O>> C assemble(Collection<I> input, Collector<O, ?, C> collector) {
        return input.stream()
                    .map(i -> (O) assemble(i))
                    .collect(collector);
    }

    private <R, I> R assemble(I in) {
        Optional<Function<I, R>> function = classMappers.entrySet()
                                                                  .stream()
                                                                  .filter(e -> e.getKey()
                                                                                .isAssignableFrom(in.getClass()))
                                                                  .map(e -> (Function<I, R>) e.getValue())
                                                                  .findFirst();
        if (function.isPresent()) {
            return function.get().apply(in);
        } else {
            throw new IllegalArgumentException("No thrift assembler found for type: " + in.getClass());
        }

    }

    public TUser assemble(User user) {
        return new TUser(
                user.getId(),
                user.getVersion(),
                user.getUserName(),
                user.getName(),
                user.getFirstName(),
                ThriftDate.format(user.getInactiveDate()),
                user.getStatus() == null ? null : user.getStatus().name(),
                user.getSuccessURL(),
                user.getLanguage() == null ? null : user.getLanguage().name(),
                user.isChangePasswordOnNextLogon(),
                user.isChangePasswordOnNextLogon(),
                (user.getEmail() != null) ? user.getEmail().asString() : null
        );
    }

    public User assemble(TUser tUser) {
        return UserEntity.user(
                tUser.getUserName(),
                tUser.getFirstName(),
                tUser.getName(),
                tUser.getSuccessURL(),
                tUser.getLanguage() == null ? null : Language.valueOf(tUser.getLanguage()),
                tUser.getStatus() == null ? null : Status.valueOf(tUser.getStatus()),
                tUser.isMustChangePassword(),
                tUser.isPasswordChangeRequired(),
                assemble(tUser.getEmail())
        );
    }

    private EmailAddress assemble(String email) {
        boolean required = coreConfigurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED);

        if(required || !StringUtil.isEmpty(email)) {
            return emailFactory.email(email);
        }
        return null;
    }

    public TSession assemble(Session session) {
        return new TSession(
                session.getSSOToken().getToken(),
                session.getUser().getUserName(),
                session.getClientIpAddress(),
                session.getCreationTime(),
                session.getLastAccessTime()
        );
    }

    public TRole assemble(Role role) {
        return new TRole(role.getId(), role.getVersion(), role.getName());
    }

    public Role assemble(TRole tRole) {
        return new RoleEntity(tRole.getName());
    }

    public TPolicy assemble(Policy policy) {
        return new TPolicy(policy.getId(), policy.getVersion(), policy.getName());
    }

    public Policy assemble(TPolicy tPolicy) {
        return new PolicyEntity(tPolicy.getName());
    }

    public TGroup assemble(Group group) {
        return new TGroup(group.getId(), group.getVersion(), group.getName(), group.getCN());
    }

    public Group assemble(TGroup tGroup) {
        return new GroupEntity(tGroup.getName(), tGroup.getCn());
    }

    public TCondition assemble(Condition condition) {
        if (condition instanceof TimeCondition) {
            TimeCondition timeCondition = (TimeCondition) condition;
            return new TCondition(timeCondition.getId(), timeCondition.getVersion(), timeCondition.getName(),
                                  TConditionType.TIME, timeCondition.getStartCondition(), timeCondition.getEndCondition());
        }
        throw new IllegalArgumentException("Unknown type " + condition.getClass());
    }

    public Condition assemble(TCondition tCondition) {
        if (tCondition.getType() == TConditionType.TIME) {
            TimeCondition timeCondition = new TimeCondition(tCondition.getStartExpression(),
                                                            tCondition.getEndExpression());
            timeCondition.setName(tCondition.getName());

            return timeCondition;
        }

        throw new IllegalArgumentException("Unknown type " + tCondition.getType());
    }

    public TRule assemble(Rule rule) {
        return new TRule(rule.getId(), rule.getVersion(), rule.getName(), rule.getResourceName());
    }

}
