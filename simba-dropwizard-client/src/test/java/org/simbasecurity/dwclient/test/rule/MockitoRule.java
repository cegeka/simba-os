package org.simbasecurity.dwclient.test.rule;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class MockitoRule implements MethodRule {

	public static MockitoRule create() {
		return new MockitoRule();
	}

	@Override
	public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				MockitoAnnotations.initMocks(target);

				try {
					base.evaluate();

				} catch (Throwable t) {
					throw t;

				} finally {
					Mockito.validateMockitoUsage();
				}
			}
		};
	}
}