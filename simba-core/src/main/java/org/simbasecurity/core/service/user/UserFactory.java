package org.simbasecurity.core.service.user;

import org.simbasecurity.core.audit.ManagementAudit;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.Language;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.UserEntity;
import org.simbasecurity.core.domain.generator.PasswordGenerator;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.simbasecurity.core.service.communication.reset.password.NewUser;
import org.simbasecurity.core.service.communication.reset.password.ResetPasswordService;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.simbasecurity.common.util.StringUtil.join;
import static org.simbasecurity.core.exception.SimbaMessageKey.USER_ALREADY_EXISTS;
import static org.simbasecurity.core.exception.SimbaMessageKey.USER_ALREADY_EXISTS_WITH_EMAIL;

@Service
@Transactional
public class UserFactory {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ManagementAudit managementAudit;
    @Autowired
    private PasswordGenerator passwordGenerator;
    @Autowired
    private ResetPasswordService resetPasswordService;
    @Autowired
    private NewUser resetPasswordForNewUser;

    private CoreConfigurationService configurationService;

    @Autowired
    public void setConfigurationService(CoreConfigurationService configurationService) {
        this.configurationService = configurationService;
    }


    public User create(User user) {
        User newUser = createUser(user);

        managementAudit.log("User ''{0}'' created", user.getUserName());

        return newUser;
    }

    public User createWithRoles(User user, List<String> roleNames) {
        User newUser = createUser(user);
        roleNames.stream()
                .map(n -> roleRepository.findByName(n))
                .filter(Objects::nonNull)
                .forEach(newUser::addRole);

        managementAudit.log("User ''{0}'' created with roles ''{1}''", newUser.getUserName(), join(roleNames, r -> r));

        return newUser;
    }

    public User cloneUser(User user, String clonedUsername) {
        User newUser = createUser(user);
        newUser.addRoles(userRepository.findByName(clonedUsername).getRoles());

        managementAudit.log("User ''{0}'' created as clone of ''{1}''", newUser.getUserName(), clonedUsername);

        return newUser;
    }

    private User createUser(User user) {
        validateUniqueUsername(user);
        validateUniqueEmail(user);
        validateRequiredEmail(user);

        User persist = userRepository.persist(user);

        if (!user.getEmail().isEmpty()) {
            resetPasswordService.sendResetPasswordMessageTo(user, resetPasswordForNewUser);
        }
        return persist;
    }

    public User createEIDUserWithRoles(User user, List<String> roleNames) {
        validateUniqueUsername(user);
        User newUser = userRepository.persist(user);
        roleNames.stream()
                .map(n -> roleRepository.findByName(n))
                .filter(Objects::nonNull)
                .forEach(newUser::addRole);

        managementAudit.log("User ''{0}'' created with roles ''{1}''", newUser.getUserName(), join(roleNames, r -> r));

        return newUser;
    }

     private void validateUniqueEmail(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new SimbaException(USER_ALREADY_EXISTS_WITH_EMAIL, String.format("User already exists with email: %s", user.getEmail()));
        }
    }

    private void validateRequiredEmail(User user) {
        if (configurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)) {
            if (user.getEmail().isEmpty()) {
                throw new SimbaException(SimbaMessageKey.EMAIL_ADDRESS_REQUIRED);
            }
        }
    }

    private void validateUniqueUsername(User user) {
        if (userRepository.findByName(user.getUserName()) != null) {
            throw new SimbaException(USER_ALREADY_EXISTS, String.format("User already exists with username: %s", user.getUserName()));
        }
    }


    public String createRestUser(String username) {
        UserEntity temporaryUser = UserEntity.restUser(username, Language.nl_NL);
        User newUser = createRestUser(temporaryUser);

        User attachedUser = userRepository.persist(newUser);
        String password = passwordGenerator.generatePassword();
        attachedUser.changePassword(password, password);

        managementAudit.log("REST User ''{0}'' created", username);

        return password;
    }

    private User createRestUser(User user) {
        if (userRepository.findByName(user.getUserName()) != null) {
            throw new SimbaException(USER_ALREADY_EXISTS, user.getUserName());
        }

        return userRepository.persist(user);
    }
}
