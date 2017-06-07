package org.simbasecurity.core.service;

import org.owasp.esapi.errors.ValidationException;
import org.simbasecurity.api.service.thrift.RoleService;
import org.simbasecurity.api.service.thrift.TPolicy;
import org.simbasecurity.api.service.thrift.TRole;
import org.simbasecurity.api.service.thrift.TUser;
import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.RoleEntity;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.PolicyRepository;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.service.filter.EntityFilterService;
import org.simbasecurity.core.service.thrift.ThriftAssembler;
import org.simbasecurity.core.service.validation.DTOValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service("roleService")
public class RoleServiceImpl implements RoleService.Iface {

    private final RoleRepository roleRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final EntityFilterService filterService;
    private final ThriftAssembler assembler;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, PolicyRepository policyRepository,
                           UserRepository userRepository, EntityFilterService filterService,
                           ThriftAssembler assembler) {
        this.roleRepository = roleRepository;
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
        this.filterService = filterService;
        this.assembler = assembler;
    }

    public List<TRole> findAll() {
        return assembler.list(filterService.filterRoles(roleRepository.findAll()));
    }

    public List<TPolicy> findPolicies(TRole role) {
        return assembler.list(
                filterService.filterPolicies(policyRepository.findForRole(roleRepository.lookUp(role.getId()))));
    }

    public List<TPolicy> findPoliciesNotLinked(TRole role) {
        return assembler.list(
                filterService.filterPolicies(policyRepository.findNotLinked(roleRepository.lookUp(role.getId()))));
    }

    public List<TUser> findUsers(TRole role) {
        return assembler.list(
                filterService.filterUsers(userRepository.findForRole(roleRepository.lookUp(role.getId()))));
    }

    public List<TUser> findUsersNotLinked(TRole role) {
        return assembler.list(
                filterService.filterUsers(userRepository.findNotLinked(roleRepository.lookUp(role.getId()))));
    }

    public void addPolicy(TRole role, TPolicy policy) {
        final Role attachedRole = roleRepository.refreshWithOptimisticLocking(role.getId(), role.getVersion());
        final Policy attachedPolicy =
                policyRepository.refreshWithOptimisticLocking(policy.getId(), policy.getVersion());

        attachedRole.addPolicy(attachedPolicy);

        roleRepository.persist(attachedRole);
    }

    public void addPolicies(TRole role, List<TPolicy> policies) {
        Role attachedRole = roleRepository.refreshWithOptimisticLocking(role.getId(), role.getVersion());
        Set<Policy> attachedPolicies = policies.stream()
                                               .map(p -> policyRepository.refreshWithOptimisticLocking(p.getId(), p.getVersion()))
                                               .collect(Collectors.toSet());

        attachedRole.addPolicies(attachedPolicies);
        roleRepository.persist(attachedRole);
    }

    public void removePolicy(TRole role, TPolicy policy) {
        Role attachedRole = roleRepository.refreshWithOptimisticLocking(role.getId(), role.getVersion());
        Policy attachedPolicy = policyRepository.refreshWithOptimisticLocking(policy.getId(), policy.getVersion());

        attachedRole.removePolicy(attachedPolicy);

        roleRepository.persist(attachedRole);
    }

    public void removeUser(TUser user, TRole role) {
        Role attachedRole = roleRepository.refreshWithOptimisticLocking(role.getId(), role.getVersion());
        User attachedUser = userRepository.refreshWithOptimisticLocking(user.getId(), user.getVersion());
        attachedRole.removeUser(attachedUser);

        roleRepository.persist(attachedRole);
    }

    public void addUsers(TRole role, List<TUser> users) {
        Role attachedRole = roleRepository.refreshWithOptimisticLocking(role.getId(), role.getVersion());
        Set<User> attachedUsers = users.stream()
                                       .map(p -> userRepository.refreshWithOptimisticLocking(p.getId(), p.getVersion()))
                                       .collect(Collectors.toSet());

        attachedRole.addUsers(attachedUsers);

        roleRepository.persist(attachedRole);
    }

    public TRole refresh(TRole role) {
        return assembler.assemble(roleRepository.lookUp(role.getId()));
    }

    public TRole createRole(String roleName) {
        try {
            DTOValidator.assertValidString("createRole", roleName);
            if (roleRepository.findByName(roleName) != null) {
                throw new IllegalArgumentException("Role with name " + roleName + " already exists");
            }
            Role newRole = new RoleEntity(roleName);
            roleRepository.persist(newRole);
            return assembler.assemble(newRole);
        } catch (ValidationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void deleteRole(TRole role) {
        Role roleToDelete = roleRepository.lookUp(role.getId());
        roleRepository.remove(roleToDelete);
    }
}
