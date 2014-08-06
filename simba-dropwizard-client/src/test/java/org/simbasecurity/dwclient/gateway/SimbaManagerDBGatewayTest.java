package org.simbasecurity.dwclient.gateway;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.simbasecurity.core.domain.Language;
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.dwclient.domain.user.SimbaUser;
import org.simbasecurity.dwclient.domain.user.SimbaUserRepository;
import org.simbasecurity.dwclient.gateway.SimbaManagerDBGateway;
import org.simbasecurity.dwclient.test.rule.MockitoRule;

public class SimbaManagerDBGatewayTest {

	@Rule
	public MockitoRule mockitoRule = MockitoRule.create();

	@Mock
	private SimbaUserRepository simbaUserRepositoryMock;

	private SimbaManagerDBGateway gateway;

	@Before
	public void setUp() {
		gateway = new SimbaManagerDBGateway(simbaUserRepositoryMock);
	}

	@Test
	public void createUserWithEmailAddress_CreatesUserDirectlyInSimbaDBWithGivenPasswordAndDefaultLanguageAndStatus() throws Exception {
		String emailAddress = "mail";
		String password = "pwd";
		gateway.createUserWithEmailAddress(emailAddress, password);

		ArgumentCaptor<SimbaUser> simbaUserCaptor = ArgumentCaptor.forClass(SimbaUser.class);
		verify(simbaUserRepositoryMock).save(simbaUserCaptor.capture());
		SimbaUser simbaUser = simbaUserCaptor.getValue();
		assertThat(simbaUser.getUserName()).isEqualTo(emailAddress);
		assertThat(simbaUser.getLanguage()).isEqualTo(Language.en_US);
		assertThat(simbaUser.getStatus()).isEqualTo(Status.ACTIVE);
		assertThat(simbaUser.getPassword()).isNotNull();
	}

	@Test
	public void isSimbaManagerAlive_AlwaysReturnsTrue_BecauseHibernateBundleWillFailForUs() throws Exception {
		assertThat(gateway.isSimbaManagerAlive()).isTrue();
	}

}
