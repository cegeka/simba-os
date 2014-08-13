package org.simbasecurity.dwclient.dropwizard.provider;

import org.simbasecurity.dwclient.dropwizard.credentials.SimbaPrincipal;

public interface DomainUserProvider<P extends AuthenticatedPrincipal> {

	P lookUp(SimbaPrincipal principal);

}
