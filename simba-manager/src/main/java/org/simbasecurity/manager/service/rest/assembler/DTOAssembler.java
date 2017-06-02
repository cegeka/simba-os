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
}
