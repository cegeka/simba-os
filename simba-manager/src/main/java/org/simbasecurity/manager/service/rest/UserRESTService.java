package org.simbasecurity.manager.service.rest;

import org.simbasecurity.api.service.thrift.UserService;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.manager.service.rest.assembler.DTOAssembler;
import org.simbasecurity.manager.service.rest.dto.GroupDTO;
import org.simbasecurity.manager.service.rest.dto.PolicyDTO;
import org.simbasecurity.manager.service.rest.dto.RoleDTO;
import org.simbasecurity.manager.service.rest.dto.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.simbasecurity.common.request.RequestConstants.SIMBA_SSO_TOKEN;
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
    public Collection<UserDTO> findAll(@CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return DTOAssembler.list($(() -> cl(ssoToken).findAll()));
    }

    @RequestMapping("search")
    @ResponseBody
    public Collection<UserDTO> search(@RequestBody String searchText,
                                      @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return DTOAssembler.list($(() -> cl(ssoToken).search(searchText)));
    }

    @RequestMapping("findByRole")
    @ResponseBody
    public Collection<UserDTO> find(@RequestBody RoleDTO role,
                                    @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return DTOAssembler.list($(() -> cl(ssoToken).findByRole(DTOAssembler.assemble(role))));
    }

    @RequestMapping("findRoles")
    @ResponseBody
    public Collection<RoleDTO> findRoles(@RequestBody UserDTO user,
                                         @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return DTOAssembler.list($(() -> cl(ssoToken).findRoles(DTOAssembler.assemble(user))));
    }

    @RequestMapping("findRolesNotLinked")
    @ResponseBody
    public Collection<RoleDTO> findRolesNotLinked(@RequestBody UserDTO user,
                                                  @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return DTOAssembler.list($(() -> cl(ssoToken).findRolesNotLinked(DTOAssembler.assemble(user))));
    }

    @RequestMapping("removeRole")
    @ResponseBody
    public void removeRole(@JsonBody("user") UserDTO user, @JsonBody("role") RoleDTO role,
                           @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        $(() -> cl(ssoToken).removeRole(DTOAssembler.assemble(user), DTOAssembler.assemble(role)));
    }

    @RequestMapping("addRoles")
    @ResponseBody
    public void addRoles(@JsonBody("user") UserDTO user, @JsonBody("roles") Set<RoleDTO> roles,
                         @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        $(() -> cl(ssoToken).addRoles(DTOAssembler.assemble(user), DTOAssembler.assemble(roles, Collectors.toSet())));
    }

    @RequestMapping("findPolicies")
    @ResponseBody
    public Collection<PolicyDTO> findPolicies(@RequestBody UserDTO user,
                                              @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return DTOAssembler.list($(() -> cl(ssoToken).findPolicies(DTOAssembler.assemble(user))));
    }

    @RequestMapping("findGroups")
    @ResponseBody
    public Collection<GroupDTO> findGroups(@RequestBody UserDTO user,
                                           @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return DTOAssembler.list($(() -> cl(ssoToken).findGroups(DTOAssembler.assemble(user))));
    }

    @RequestMapping("resetPassword")
    @ResponseBody
    public UserDTO resetPassword(@RequestBody UserDTO user, HttpServletResponse response,
                                 @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        try {
            return DTOAssembler.assemble($(() -> cl(ssoToken).resetPassword(DTOAssembler.assemble(user))));
        } catch (Exception ex) {
            sendError(UNABLE_TO_RESET_PASSWORD_ERROR_CODE, response,
                    "Something went wrong while resetting the password of user '" + user.getUserName() + "'. Message: "
                    + ex.getMessage());
        }
        return null;
    }

    @RequestMapping("createWithRoles")
    @ResponseBody
    public UserDTO create(@JsonBody("user") UserDTO user, @JsonBody("roleNames") List<String> roleNames,
                          @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return DTOAssembler.assemble($(() -> cl(ssoToken).createWithRoles(DTOAssembler.assemble(user), roleNames)));
    }

    @RequestMapping("create")
    @ResponseBody
    public UserDTO create(@RequestBody UserDTO user,
                          @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return DTOAssembler.assemble($(() -> cl(ssoToken).create(DTOAssembler.assemble(user))));
    }

    @RequestMapping("createAsClone")
    @ResponseBody
    public UserDTO create(@JsonBody("user") UserDTO user, @JsonBody("userName") String userName,
                          @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return DTOAssembler.assemble($(() -> cl(ssoToken).cloneUser(DTOAssembler.assemble(user), userName)));
    }

    @RequestMapping("createRestUser")
    @ResponseBody
    public String createRestUser(@JsonBody("userName") String username,
                                 @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return $(() -> cl(ssoToken).createRestUser(username));
    }

    @RequestMapping("update")
    @ResponseBody
    public UserDTO update(@RequestBody UserDTO user,
                          @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return DTOAssembler.assemble($(() -> cl(ssoToken).update(DTOAssembler.assemble(user))));
    }

    @RequestMapping("refresh")
    @ResponseBody
    public UserDTO refresh(@RequestBody UserDTO user,
                           @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return DTOAssembler.assemble($(() -> cl(ssoToken).refresh(DTOAssembler.assemble(user))));
    }

}
