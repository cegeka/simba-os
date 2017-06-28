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
package org.simbasecurity.manager.service.rest;

import org.simbasecurity.api.service.thrift.GroupService;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.manager.service.rest.dto.GroupDTO;
import org.simbasecurity.manager.service.rest.dto.RoleDTO;
import org.simbasecurity.manager.service.rest.dto.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.List;

import static org.simbasecurity.common.request.RequestConstants.SIMBA_SSO_TOKEN;
import static org.simbasecurity.manager.service.rest.assembler.DTOAssembler.assemble;
import static org.simbasecurity.manager.service.rest.assembler.DTOAssembler.list;

@Controller
@RequestMapping("group")
public class GroupRESTService extends BaseRESTService<GroupService.Client> {

    public GroupRESTService() {
        super(new GroupService.Client.Factory(), SimbaConfiguration.getGroupServiceURL());
    }

    @RequestMapping("findAll")
    @ResponseBody
    public Collection<GroupDTO> findAll(@CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return list($(() -> cl(ssoToken).findAll()));
    }

    @RequestMapping("findRoles")
    @ResponseBody
    public Collection<RoleDTO> findRoles(@RequestBody GroupDTO group,
                                         @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return list($(() -> cl(ssoToken).findRoles(assemble(group))));
    }

    @RequestMapping("findRolesNotLinked")
    @ResponseBody
    public Collection<RoleDTO> findRolesNotLinked(@RequestBody GroupDTO group,
                                                  @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return list($(() -> cl(ssoToken).findRolesNotLinked(assemble(group))));
    }

    @RequestMapping("findUsers")
    @ResponseBody
    public Collection<UserDTO> findUsers(@RequestBody GroupDTO group,
                                         @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return list($(() -> cl(ssoToken).findUsers(assemble(group))));
    }

    @RequestMapping("addRole")
    @ResponseBody
    public void addRole(@JsonBody("group") GroupDTO group, @JsonBody("role") RoleDTO role,
                        @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        $(() -> cl(ssoToken).addRole(assemble(group), assemble(role)));
    }

    @RequestMapping("addRoles")
    @ResponseBody
    public void addRoles(@JsonBody("group") GroupDTO group, @JsonBody("roles") List<RoleDTO> roles,
                         @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        $(() -> cl(ssoToken).addRoles(assemble(group), list(roles)));
    }

    @RequestMapping("removeRole")
    @ResponseBody
    public void removeRole(@JsonBody("group") GroupDTO group, @JsonBody("role") RoleDTO role,
                           @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        $(() -> cl(ssoToken).removeRole(assemble(group), assemble(role)));
    }

    @RequestMapping("refresh")
    @ResponseBody
    public GroupDTO refresh(@RequestBody GroupDTO group,
                            @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return assemble($(() -> cl(ssoToken).refresh(assemble(group))));
    }

}
