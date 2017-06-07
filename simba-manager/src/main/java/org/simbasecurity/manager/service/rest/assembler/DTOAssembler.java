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
package org.simbasecurity.manager.service.rest.assembler;

import org.simbasecurity.api.service.thrift.*;
import org.simbasecurity.manager.service.rest.dto.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class DTOAssembler {
    private static final Map<Class<?>, Function<?, ?>> classMappers = new HashMap<>();
    static {
        classMappers.put(UserDTO.class, in -> DTOAssembler.assemble((UserDTO) in));
        classMappers.put(TUser.class, in -> DTOAssembler.assemble((TUser) in));
        classMappers.put(SessionDTO.class, in -> DTOAssembler.assemble((SessionDTO) in));
        classMappers.put(TSession.class, in -> DTOAssembler.assemble((TSession) in));
        classMappers.put(RoleDTO.class, in -> DTOAssembler.assemble((RoleDTO) in));
        classMappers.put(TRole.class, in -> DTOAssembler.assemble((TRole) in));
        classMappers.put(PolicyDTO.class, in -> DTOAssembler.assemble((PolicyDTO) in));
        classMappers.put(TPolicy.class, in -> DTOAssembler.assemble((TPolicy) in));
        classMappers.put(GroupDTO.class, in -> DTOAssembler.assemble((GroupDTO) in));
        classMappers.put(TGroup.class, in -> DTOAssembler.assemble((TGroup) in));
        classMappers.put(TCondition.class, in -> DTOAssembler.assemble((TCondition) in));
        classMappers.put(TimeConditionDTO.class, in -> DTOAssembler.assemble((TimeConditionDTO) in));
        classMappers.put(RuleDTO.class, in -> DTOAssembler.assemble((RuleDTO) in));
        classMappers.put(TRule.class, in -> DTOAssembler.assemble((TRule) in));
    }

    public static <I, O> List<O> list(Collection<I> input) {
        return assemble(input, Collectors.toList());
    }

    public static <I, O> Set<O> set(Collection<I> input) {
        return assemble(input, Collectors.toSet());
    }

    public static <I, O, C extends Collection<O>> C assemble(Collection<I> input, Collector<O, ?, C> collector) {
        return input.stream()
                    .map(i -> (O) assemble(i))
                    .collect(collector);
    }

    private static <R, I> R assemble(I in) {
        Function<I, R> function = (Function<I, R>) classMappers.get(in.getClass());
        if (function == null) {
            throw new IllegalArgumentException("No DTO assembler found for type: " + in.getClass());
        }

        return function.apply(in);
    }

    public static TUser assemble(UserDTO user) {
        return new TUser(
                user.getId(),
                user.getVersion(),
                user.getUserName(),
                user.getName(),
                user.getFirstName(),
                user.getInactiveDate(),
                user.getStatus(),
                user.getSuccessURL(),
                user.getLanguage(),
                user.isChangePasswordOnNextLogon(),
                user.isPasswordChangeRequired()
        );
    }

    public static UserDTO assemble(TUser tUser) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(tUser.getId());
        userDTO.setVersion(tUser.getVersion());
        userDTO.setUserName(tUser.getUserName());
        userDTO.setName(tUser.getName());
        userDTO.setFirstName(tUser.getFirstName());
        userDTO.setInactiveDate(tUser.getInactiveDate());
        userDTO.setStatus(tUser.getStatus());
        userDTO.setSuccessURL(tUser.getSuccessURL());
        userDTO.setLanguage(tUser.getLanguage());
        userDTO.setChangePasswordOnNextLogon(tUser.isPasswordChangeRequired());
        return userDTO;
    }

    public static TSession assemble(SessionDTO session) {
        return new TSession(
                session.getSsoToken(),
                session.getUserName(),
                session.getClientIpAddress(),
                session.getCreationTime(),
                session.getLastAccessTime()
        );
    }

    public static SessionDTO assemble(TSession session) {
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setUserName(session.getUserName());
        sessionDTO.setClientIpAddress(session.getClientIpAddress());
        sessionDTO.setCreationTime(session.getCreationTime());
        sessionDTO.setLastAccessTime(session.getLastAccessTime());
        sessionDTO.setSsoToken(session.getSsoToken());
        return sessionDTO;
    }

    public static TRole assemble(RoleDTO role) {
        return new TRole(role.getId(), role.getVersion(), role.getName());
    }

    public static RoleDTO assemble(TRole tRole) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(tRole.getId());
        roleDTO.setVersion(tRole.getVersion());
        roleDTO.setName(tRole.getName());
        return roleDTO;
    }

    public static TPolicy assemble(PolicyDTO policy) {
        return new TPolicy(policy.getId(), policy.getVersion(), policy.getName());
    }

    public static PolicyDTO assemble(TPolicy tPolicy) {
        PolicyDTO policyDTO = new PolicyDTO();
        policyDTO.setId(tPolicy.getId());
        policyDTO.setVersion(tPolicy.getVersion());
        policyDTO.setName(tPolicy.getName());
        return policyDTO;
    }

    public static TGroup assemble(GroupDTO group) {
        return new TGroup(group.getId(), group.getVersion(), group.getName(), group.getCn());
    }

    public static GroupDTO assemble(TGroup tGroup) {
        final GroupDTO groupDTO = new GroupDTO();
        groupDTO.setId(tGroup.getId());
        groupDTO.setVersion(tGroup.getVersion());
        groupDTO.setName(tGroup.getName());
        groupDTO.setCn(tGroup.getCn());
        return groupDTO;
    }

    public static TCondition assemble(ConditionDTO condition) {
        if (condition instanceof TimeConditionDTO) {
            return assemble((TimeConditionDTO) condition);
        }
        throw new IllegalArgumentException("Unknow type: " + condition.getClass());
    }

    public static TCondition assemble(TimeConditionDTO timeCondition) {
        return new TCondition(timeCondition.getId(), timeCondition.getVersion(), timeCondition.getName(),
                              TConditionType.TIME, timeCondition.getStartCondition(), timeCondition.getEndCondition());
    }

    public static TimeConditionDTO assemble(TCondition condition) {
        final TimeConditionDTO conditionDTO = new TimeConditionDTO();
        conditionDTO.setId(condition.getId());
        conditionDTO.setVersion(condition.getVersion());
        conditionDTO.setName(condition.getName());
        conditionDTO.setStartCondition(condition.getStartExpression());
        conditionDTO.setEndCondition(condition.getEndExpression());
        return conditionDTO;
    }

    public static TRule assemble(RuleDTO rule) {
        return new TRule(rule.getId(), rule.getVersion(), rule.getName(), rule.getResourceName());
    }

    public static RuleDTO assemble(TRule tRule) {
        RuleDTO ruleDTO = new RuleDTO();
        ruleDTO.setId(tRule.getId());
        ruleDTO.setVersion(tRule.getVersion());
        ruleDTO.setName(tRule.getName());
        ruleDTO.setResourceName(tRule.getResourceName());
        return ruleDTO;
    }

}
