package org.simbasecurity.dwclient.gateway;

import java.net.SocketException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.thrift.TException;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService.Client;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentials;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentialsFactory;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaPrincipal;
import org.simbasecurity.dwclient.exception.InternalServerErrorDWSimbaException;
import org.simbasecurity.dwclient.exception.SimbaUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.sun.jersey.spi.container.ContainerRequest;

public class SimbaGateway {

	public static final String SIMBA_AUTHENTICATION_SERVICE = "authenticationService";

	public static final String SESSION_AUTHENTICATE_CHAIN = "sessionChain";
	public static final String LOGIN_AUTHENTICATE_CHAIN = "loginAuthChain";
	public static final String LOGOUT_CHAIN = "logoutChain";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String simbaWebURL;

	private THttpClient tHttpClient;
	private Client authenticationFilterService;

	private SimbaServiceFactory simbaServiceFactory;

	private SimbaCredentialsFactory simbaCredentialsFactory;

	@Inject
	public SimbaGateway(@Named("simbaWebURL") String simbaWebURL, SimbaServiceFactory simbaServiceFactory, SimbaCredentialsFactory simbaCredentialsFactory) {
		this.simbaWebURL = simbaWebURL;
		this.simbaServiceFactory = simbaServiceFactory;
		this.simbaCredentialsFactory = simbaCredentialsFactory;
	}

	public Optional<String> login(String appUser, String appPassword) throws SimbaUnavailableException {
		RequestData requestData = simbaCredentialsFactory.createForLogin(appUser, appPassword).asRequestData();
		return loginInSimba(requestData);
	}

	public boolean checkIfPasswordMatches(String username, String password) {
		try {
			Optional<String> login = login(username, password);
			return login.isPresent();
		} catch (SimbaUnavailableException e) {
			throw new InternalServerErrorDWSimbaException();
		}

	}

	/**
	 * @param request
	 * @return Optional&lt;String&gt; that contains the SSOToken as a String on successful login, and null when unsuccessful
	 * @throws SimbaUnavailableException
	 */
	public Optional<String> login(ContainerRequest request) throws SimbaUnavailableException {
		RequestData requestData = simbaCredentialsFactory.create(request, false).asRequestData();
		return loginInSimba(requestData);
	}

	/**
	 * 
	 * @param request
	 * @return true on successful logout
	 * @throws SimbaUnavailableException
	 */
	public void logout(ContainerRequest request) throws SimbaUnavailableException {
		SimbaCredentials credentials = simbaCredentialsFactory.create(request, true);
		processRequestInSimba(credentials.asRequestData(), LOGOUT_CHAIN);
	}

	/**
	 * On successful authentication returns a present SimbaPrincipal
	 * On failed authentication returns either an absent SimbaPrincipal, or throws a SimbaUnavailableException
	 * 
	 * @param credentials
	 * @return an absent SimbaPrincipal when the ActionDescriptor does not contain DO_FILTER_AND_SET_PRINCIPAL (means authentication failed)
	 *         a present SimbaPrincipal when the ActionDescriptor does contain DO_FILTER_AND_SET_PRINCIPAL (means authentication was successful)
	 * @throws SimbaUnavailableException
	 */
	public Optional<SimbaPrincipal> authenticate(SimbaCredentials credentials) throws SimbaUnavailableException {
		SimbaPrincipal principal = null;
		ActionDescriptor actionDescriptor = processRequestInSimba(credentials.asRequestData(), SESSION_AUTHENTICATE_CHAIN);
		if (isValidActionDescriptor(actionDescriptor) && actionDescriptor.getActionTypes().contains(ActionType.DO_FILTER_AND_SET_PRINCIPAL)) {
			String token = actionDescriptor.getSsoToken() != null ? actionDescriptor.getSsoToken().getToken() : null;
			principal = new SimbaPrincipal(actionDescriptor.getPrincipal(), token);
		}

		return Optional.fromNullable(principal);
	}

	/**
	 * @return always creates a new client
	 * @throws SimbaUnavailableException
	 */
	AuthenticationFilterService.Client createAuthenticationService() throws SimbaUnavailableException {
		try {
			tHttpClient = getTHttpClient();
			authenticationFilterService = simbaServiceFactory.createJSONAuthenticationFilterService(tHttpClient);
		} catch (TTransportException | RuntimeException e) {
			if (tHttpClient != null) {
				tHttpClient.close();
			}
			logger.error("Simba is down?", e);
			throw new SimbaUnavailableException(e);
		}

		return authenticationFilterService;
	}

	public boolean isSimbaAlive() {
		THttpClient tHttpClient = null;
		try {
			tHttpClient = getTHttpClient();
			tHttpClient.flush();
		} catch (TTransportException e) {
			if (e.getCause() != null && e.getCause().getClass().isAssignableFrom(SocketException.class)) {
				if (tHttpClient != null) {
					tHttpClient.close();
				}
				return false;
			}
		} finally {
			if (tHttpClient != null) {
				tHttpClient.close();
			}
		}
		return true;
	}

	private Optional<String> loginInSimba(RequestData requestData) throws SimbaUnavailableException {
		ActionDescriptor actionDescriptor = processRequestInSimba(requestData, LOGIN_AUTHENTICATE_CHAIN);
		if (isValidActionDescriptor(actionDescriptor) && actionDescriptor.getActionTypes().contains(ActionType.MAKE_COOKIE)) {
			return Optional.fromNullable(actionDescriptor.getSsoToken().getToken());
		}
		return Optional.absent();
	}

	private boolean isValidActionDescriptor(ActionDescriptor actionDescriptor) {
		return actionDescriptor != null && actionDescriptor.getActionTypes() != null;
	}

	private ActionDescriptor processRequestInSimba(RequestData requestData, String chain) throws SimbaUnavailableException {
		try {
			logger.debug("processRequest in simba with requestdata: {}", requestData);
			ActionDescriptor actionDescriptor = createAuthenticationService().processRequest(requestData, chain);
			return actionDescriptor;
		} catch (TException | RuntimeException e) {
			logger.error("Simba is down?", e);
			throw new SimbaUnavailableException(e);
		} finally {
			if (tHttpClient != null) {
				tHttpClient.close();
			}
		}
	}

	private THttpClient getTHttpClient() throws TTransportException {
		return simbaServiceFactory.createTHttpClient(simbaWebURL + "/" + SIMBA_AUTHENTICATION_SERVICE);
	}

}
