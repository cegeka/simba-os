package org.simbasecurity.core.service.thrift;

import org.simbasecurity.api.service.thrift.*;
import org.simbasecurity.common.util.ThriftDate;
import org.simbasecurity.core.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("unchecked")
public class ThriftAssembler {

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
                user.isChangePasswordOnNextLogon()
        );
    }

    public User assemble(TUser tUser) {
        return new UserEntity(
                tUser.getUserName(),
                tUser.getFirstName(),
                tUser.getName(),
                tUser.getSuccessURL(),
                tUser.getLanguage() == null ? null : Language.valueOf(tUser.getLanguage()),
                tUser.getStatus() == null ? null : Status.valueOf(tUser.getStatus()),
                tUser.isMustChangePassword(),
                tUser.isPasswordChangeRequired()
        );
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
}
