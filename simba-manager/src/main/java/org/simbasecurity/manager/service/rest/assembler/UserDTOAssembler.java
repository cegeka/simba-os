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

import org.simbasecurity.api.service.thrift.UserR;
import org.simbasecurity.manager.service.rest.dto.UserDTO;

import java.util.Collection;
import java.util.stream.Collectors;

public final class UserDTOAssembler {
    public static Collection<UserDTO> assemble(final Collection<UserR> users) {
        return users.stream()
                    .map(UserDTOAssembler::assemble)
                    .collect(Collectors.toList());
    }

    public static UserDTO assemble(final UserR user) {
        final UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setVersion(user.getVersion());
        userDTO.setUserName(user.getUserName());
        userDTO.setName(user.getName());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setInactiveDate(user.getInactiveDate());
        userDTO.setStatus(user.getStatus());
        userDTO.setSuccessURL(user.getSuccessURL());
        userDTO.setLanguage(user.getLanguage());
        userDTO.setChangePasswordOnNextLogon(user.isPasswordChangeRequired());
        return userDTO;
    }
}
