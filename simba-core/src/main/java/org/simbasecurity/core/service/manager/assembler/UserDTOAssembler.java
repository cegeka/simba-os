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

import org.simbasecurity.core.domain.AbstractVersionedEntity;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.service.manager.dto.UserDTO;

import java.util.ArrayList;
import java.util.Collection;

import static org.simbasecurity.core.service.manager.assembler.VersionedDTOAssemblerUtil.applyVersionAndId;

public final class UserDTOAssembler {
    public static Collection<UserDTO> assemble(final Collection<User> users) {
        final Collection<UserDTO> userDTOs = new ArrayList<UserDTO>(users.size());
        for (final User user : users) {
            userDTOs.add(assemble(user));
        }
        return userDTOs;
    }

    public static UserDTO assemble(final User user) {
        final UserDTO userDTO = new UserDTO();
        applyVersionAndId((AbstractVersionedEntity) user, userDTO);
        userDTO.setUserName(user.getUserName());
        userDTO.setName(user.getName());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setInactiveDate(user.getInactiveDate());
        userDTO.setStatus(user.getStatus());
        userDTO.setSuccessURL(user.getSuccessURL());
        userDTO.setLanguage(user.getLanguage());
        userDTO.setChangePasswordOnNextLogon(user.isChangePasswordOnNextLogon());
//        DTOValidator.encodeForHTML(userDTO);
        return userDTO;
    }
}
