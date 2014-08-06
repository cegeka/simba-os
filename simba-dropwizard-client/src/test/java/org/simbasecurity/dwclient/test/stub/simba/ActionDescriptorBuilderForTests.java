package org.simbasecurity.dwclient.test.stub.simba;

import java.util.Map;
import java.util.Set;

import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.SSOToken;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ActionDescriptorBuilderForTests {

	private Set<ActionType> actionTypes;
	private Map<String, String> parameterMap;
	private SSOToken ssoToken;
	private String redirectURL;
	private String principal;

	public ActionDescriptorBuilderForTests() {
	}

	public ActionDescriptor build() {
		return new ActionDescriptor(actionTypes, parameterMap, ssoToken, redirectURL, principal);
	}

	public ActionDescriptorBuilderForTests withActionTypes(ActionType... actionTypes) {
		this.actionTypes = Sets.newHashSet(actionTypes);
		return this;
	}

	public ActionDescriptorBuilderForTests withParameterMap(Map<String, String> parameterMap) {
		this.parameterMap = parameterMap;
		return this;
	}

	public ActionDescriptorBuilderForTests addParameter(String key, String value) {
		if (parameterMap == null) {
			parameterMap = Maps.newHashMap();
		}
		this.parameterMap.put(key, value);
		return this;
	}

	public ActionDescriptorBuilderForTests withSsoToken(SSOToken ssoToken) {
		this.ssoToken = ssoToken;
		return this;
	}

	public ActionDescriptorBuilderForTests withRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
		return this;
	}

	public ActionDescriptorBuilderForTests withPrincipal(String principal) {
		this.principal = principal;
		return this;
	}

}
