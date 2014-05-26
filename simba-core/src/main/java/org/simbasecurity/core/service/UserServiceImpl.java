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
package org.simbasecurity.core.service;

import static org.simbasecurity.core.exception.SimbaMessageKey.*;

import java.util.List;

import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.exception.SimbaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired private Audit audit;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;

    @Autowired private AuditLogEventFactory eventFactory;

    @Override
    public User create(User user, List<String> roleNames) {
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                throw new IllegalArgumentException("Role name " + roleName + " doesn't exist");
            }
            user.addRole(role);
        }

        if (userRepository.findByName(user.getUserName()) != null) {
            throw new SimbaException(USER_ALREADY_EXISTS, user.getUserName());
        }

        User newUser = userRepository.persist(user);

        audit.log(eventFactory.createEventForSession(user.getUserName(), null, "", "User created"));
        return newUser;
    }

}