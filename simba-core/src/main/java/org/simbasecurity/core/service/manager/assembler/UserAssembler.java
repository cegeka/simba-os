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
package org.simbasecurity.core.service.manager.assembler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.UserEntity;
import org.simbasecurity.core.service.manager.dto.UserDTO;

public class UserAssembler {
    private UserAssembler() {
        // utility class should not be instantiated
    }

    public static Set<User> assemble(Collection<UserDTO> userDTOs) {
        Set<User> users = new HashSet<User>(userDTOs.size());

        for (UserDTO userDTO : userDTOs) {
            users.add(assemble(userDTO));
        }

        return users;
    }

    public static UserEntity assemble(UserDTO userDTO) {
        return new UserEntity(userDTO.getUserName(), userDTO.getFirstName(), userDTO.getName(),
                userDTO.getSuccessURL(), userDTO.getLanguage(), userDTO.getStatus(),
                userDTO.isChangePasswordOnNextLogon(), userDTO.isPasswordChangeRequired());
    }
}
