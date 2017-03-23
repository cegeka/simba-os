package org.simbasecurity.core.service.manager;

import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The EntityFilterService is used by the manager services to filter entity lists before returning them to the
 * manager user interface. This allows hiding certain entities from certain users.
 * <p/>
 * When no {@link EntityFilter}'s are configured in the Spring context, no filtering to the entity collections is applied.
 * Multiple {@link EntityFilter} predicates are combined using the <code>and</code> operator.
 *
 * @see UserManagerService
 * @see PolicyManagerService
 * @see RoleManagerService
 * @see EntityFilter
 *
 * @since 3.0.0
 */
@Service
public class EntityFilterService {

    private List<EntityFilter> filters;

    private Predicate<Role> rolePredicate;
    private Predicate<Policy> policyPredicate;
    private Predicate<User> userPredicate;

    @Autowired
    public EntityFilterService(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<List<EntityFilter>> filters) {
        this.filters = filters.orElseGet(ArrayList::new);
    }

    @PostConstruct
    public void initializePredicates() {
        rolePredicate = filters.stream().map(EntityFilter::rolePredicate).reduce(Predicate::and).orElse(r -> true);
        policyPredicate = filters.stream().map(EntityFilter::policyPredicate).reduce(Predicate::and).orElse(p -> true);
        userPredicate = filters.stream().map(EntityFilter::userPredicate).reduce(Predicate::and).orElse(u -> true);
    }

    public Collection<Role> filterRoles(Collection<Role> input) {
        return input.stream()
                    .filter(rolePredicate)
                    .collect(new SameTypeCollectionSupplier<>(input.getClass()), Collection::add, Collection::addAll);
    }

    public Collection<Policy> filterPolicies(Collection<Policy> input) {
        return input.stream()
                    .filter(policyPredicate)
                    .collect(new SameTypeCollectionSupplier<>(input.getClass()), Collection::add, Collection::addAll);
    }

    public Collection<User> filterUsers(Collection<User> input) {
        return input.stream()
                    .filter(userPredicate)
                    .collect(new SameTypeCollectionSupplier<>(input.getClass()), Collection::add, Collection::addAll);
    }

    private static class SameTypeCollectionSupplier<T> implements Supplier<Collection<T>> {
        private final Class<? extends Collection> aClass;

        private SameTypeCollectionSupplier(Class<? extends Collection> aClass) {
            this.aClass = aClass;
        }

        @Override
        public Collection<T> get() {
            try {
                return aClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                return new ArrayList<>();
            }
        }
    }
}
