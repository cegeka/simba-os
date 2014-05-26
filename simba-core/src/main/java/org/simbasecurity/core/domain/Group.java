package org.simbasecurity.core.domain;

import java.util.Collection;

public interface Group extends Versionable {
    Collection<Role> getRoles();
    Collection<User> getUsers();
    void addRole(Role role);
    void addRoles(Collection<Role> role);
    void removeRole(Role role);
    String getName();
    String getCN();
}
