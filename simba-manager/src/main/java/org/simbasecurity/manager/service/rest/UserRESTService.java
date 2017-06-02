package org.simbasecurity.manager.service.rest;

import org.simbasecurity.api.service.thrift.UserService;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.manager.service.rest.assembler.DTOAssembler;
import org.simbasecurity.manager.service.rest.dto.GroupDTO;
import org.simbasecurity.manager.service.rest.dto.PolicyDTO;
import org.simbasecurity.manager.service.rest.dto.RoleDTO;
import org.simbasecurity.manager.service.rest.dto.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.simbasecurity.manager.service.rest.util.ErrorSender.UNABLE_TO_RESET_PASSWORD_ERROR_CODE;
import static org.simbasecurity.manager.service.rest.util.ErrorSender.sendError;

@Controller
@RequestMapping("user")
public class UserRESTService extends BaseRESTService<UserService.Client> {

    public UserRESTService() {
        super(new UserService.Client.Factory(), SimbaConfiguration.getUserServiceURL());
    }

    @RequestMapping("findAll")
    @ResponseBody
    public Collection<UserDTO> findAll() {
        return DTOAssembler.list($(() -> cl().findAll()));
    }

    @RequestMapping("search")
    @ResponseBody
    public Collection<UserDTO> search(@RequestBody String searchText) {
        return DTOAssembler.list($(() -> cl().search(searchText)));
    }

    @RequestMapping("findByRole")
    @ResponseBody
    public Collection<UserDTO> find(@RequestBody RoleDTO role) {
        return DTOAssembler.list($(() -> cl().findByRole(DTOAssembler.assemble(role))));
    }

    @RequestMapping("findRoles")
    @ResponseBody
    public Collection<RoleDTO> findRoles(@RequestBody UserDTO user) {
        return DTOAssembler.list($(() -> cl().findRoles(DTOAssembler.assemble(user))));
    }

    @RequestMapping("findRolesNotLinked")
    @ResponseBody
    public Collection<RoleDTO> findRolesNotLinked(@RequestBody UserDTO user) {
        return DTOAssembler.list($(() -> cl().findRolesNotLinked(DTOAssembler.assemble(user))));
    }

    @RequestMapping("removeRole")
    @ResponseBody
    public void removeRole(@JsonBody("user") UserDTO user, @JsonBody("role") RoleDTO role) {
        $(() -> cl().removeRole(DTOAssembler.assemble(user), DTOAssembler.assemble(role)));
    }

    @RequestMapping("addRoles")
    @ResponseBody
    public void addRoles(@JsonBody("user") UserDTO user, @JsonBody("roles") Set<RoleDTO> roles) {
        $(() -> cl().addRoles(DTOAssembler.assemble(user), DTOAssembler.assemble(roles, Collectors.toSet())));
    }

    @RequestMapping("findPolicies")
    @ResponseBody
    public Collection<PolicyDTO> findPolicies(@RequestBody UserDTO user) {
        return DTOAssembler.list($(() -> cl().findPolicies(DTOAssembler.assemble(user))));
    }

    @RequestMapping("findGroups")
    @ResponseBody
    public Collection<GroupDTO> findGroups(@RequestBody UserDTO user) {
        return DTOAssembler.list($(() -> cl().findGroups(DTOAssembler.assemble(user))));
    }

    @RequestMapping("resetPassword")
    @ResponseBody
    public UserDTO resetPassword(@RequestBody UserDTO user, HttpServletResponse response) {
        try {
            return DTOAssembler.assemble($(() -> cl().resetPassword(DTOAssembler.assemble(user))));
        } catch (Exception ex) {
            sendError(UNABLE_TO_RESET_PASSWORD_ERROR_CODE, response,
                    "Something went wrong while resetting the password of user '" + user.getUserName() + "'. Message: "
                    + ex.getMessage());
        }
        return null;
    }

    @RequestMapping("createWithRoles")
    @ResponseBody
    public UserDTO create(@JsonBody("user") UserDTO user, @JsonBody("roleNames") List<String> roleNames) {
        return DTOAssembler.assemble($(() -> cl().createWithRoles(DTOAssembler.assemble(user), roleNames)));
    }

    @RequestMapping("create")
    @ResponseBody
    public UserDTO create(@RequestBody UserDTO user) {
        return DTOAssembler.assemble($(() -> cl().create(DTOAssembler.assemble(user))));
    }

    @RequestMapping("createAsClone")
    @ResponseBody
    public UserDTO create(@JsonBody("user") UserDTO user, @JsonBody("userName") String userName) {
        return DTOAssembler.assemble($(() -> cl().cloneUser(DTOAssembler.assemble(user), userName)));
    }

    @RequestMapping("createRestUser")
    @ResponseBody
    public String createRestUser(@JsonBody("userName") String username) {
        return $(() -> cl().createRestUser(username));
    }

    @RequestMapping("update")
    @ResponseBody
    public UserDTO update(@RequestBody UserDTO user) {
        return DTOAssembler.assemble($(() -> cl().update(DTOAssembler.assemble(user))));
    }

    @RequestMapping("refresh")
    @ResponseBody
    public UserDTO refresh(@RequestBody UserDTO user) {
        return DTOAssembler.assemble($(() -> cl().refresh(DTOAssembler.assemble(user))));
    }

}
