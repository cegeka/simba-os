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

import org.simbasecurity.api.service.thrift.RoleService;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.manager.service.rest.dto.PolicyDTO;
import org.simbasecurity.manager.service.rest.dto.RoleDTO;
import org.simbasecurity.manager.service.rest.dto.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.xml.bind.ValidationException;
import java.util.Collection;
import java.util.List;

import static org.simbasecurity.manager.service.rest.assembler.DTOAssembler.assemble;
import static org.simbasecurity.manager.service.rest.assembler.DTOAssembler.list;

@Controller
@RequestMapping("role")
public class RoleRESTService extends BaseRESTService<RoleService.Client> {

    public RoleRESTService() {
        super(new RoleService.Client.Factory(), SimbaConfiguration.getRoleServiceURL());
    }

    @RequestMapping("findAll")
    @ResponseBody
    public Collection<RoleDTO> findAll() {
        return list($(() -> cl().findAll()));
    }

    @RequestMapping("findPolicies")
    @ResponseBody
    public Collection<PolicyDTO> findPolicies(@RequestBody RoleDTO role) {
        return list($(() -> cl().findPolicies(assemble(role))));
    }

    @RequestMapping("findPoliciesNotLinked")
    @ResponseBody
    public Collection<PolicyDTO> findPoliciesNotLinked(@RequestBody RoleDTO role) {
        return list($(() -> cl().findPoliciesNotLinked(assemble(role))));
    }

    @RequestMapping("findUsers")
    @ResponseBody
    public Collection<UserDTO> findUsers(@RequestBody RoleDTO role) {
        return list($(() -> cl().findUsers(assemble(role))));
    }

    @RequestMapping("findUsersNotLinked")
    @ResponseBody
    public Collection<UserDTO> findUsersNotLinked(@RequestBody RoleDTO role) {
        return list($(() -> cl().findUsersNotLinked(assemble(role))));
    }

    @RequestMapping("addPolicy")
    @ResponseBody
    public void addPolicy(@JsonBody("role") RoleDTO role, @JsonBody("policy") PolicyDTO policy) {
        $(() -> cl().addPolicy(assemble(role), assemble(policy)));
    }

    @RequestMapping("addPolicies")
    @ResponseBody
    public void addPolicies(@JsonBody("role") RoleDTO role, @JsonBody("policies") List<PolicyDTO> policies) {
        $(() -> cl().addPolicies(assemble(role), list(policies)));
    }

    @RequestMapping("removePolicy")
    @ResponseBody
    public void removePolicy(@JsonBody("role") RoleDTO role, @JsonBody("policy") PolicyDTO policy) {
        $(() -> cl().removePolicy(assemble(role), assemble(policy)));
    }

    @RequestMapping("removeUser")
    @ResponseBody
    public void removeUser(@JsonBody("user") UserDTO user, @JsonBody("role") RoleDTO role) {
        $(() -> cl().removeUser(assemble(user), assemble(role)));
    }

    @RequestMapping("addUsers")
    @ResponseBody
    public void addUsers(@JsonBody("role") RoleDTO role, @JsonBody("users") List<UserDTO> users) {
        $(() -> cl().addUsers(assemble(role), list(users)));
    }

    @RequestMapping("refresh")
    @ResponseBody
    public RoleDTO refresh(@RequestBody RoleDTO role) {
        return assemble($(() -> cl().refresh(assemble(role))));
    }

    @RequestMapping("createRole")
    @ResponseBody
    public RoleDTO createRole(@JsonBody("roleName") String roleName) throws ValidationException {
        return assemble($(() -> cl().createRole(roleName)));
    }

    @RequestMapping("deleteRole")
    public void deleteRole(@JsonBody("role") RoleDTO role) throws ValidationException {
        $(() -> cl().deleteRole(assemble(role)));
    }
}
