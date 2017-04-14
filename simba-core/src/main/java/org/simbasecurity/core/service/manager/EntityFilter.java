package org.simbasecurity.core.service.manager;

import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.User;

import java.util.function.Predicate;

/**
 * This class allows the injection of custom filters in the {@link EntityFilterService}.
 * <p/>
 * If an EntityFilter should not require filtering on a specific entity, it can use:
 * <code>
 *     e -> true
 * </code>
 * as predicate to allow all entities to remain in the collection.
 *
 * @since 3.0.0
 */
public interface EntityFilter {

    /**
     * @return the predicate to use for filtering roles
     */
    Predicate<Role> rolePredicate();

    /**
     * @return the predicate to use for filtering policies
     */
    Predicate<Policy> policyPredicate();

    /**
     * @return the predicate to use for filtering users
     */
    Predicate<User> userPredicate();
}
