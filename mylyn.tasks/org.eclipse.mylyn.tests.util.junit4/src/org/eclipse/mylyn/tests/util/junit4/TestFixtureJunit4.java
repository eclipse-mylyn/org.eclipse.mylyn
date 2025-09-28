/*******************************************************************************
 * Copyright (c) 2025 George Lindholm
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.tests.util.junit4;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tests.util.TestFixture;

import junit.framework.TestCase;
import junit.framework.TestSuite;

@SuppressWarnings({ "nls" })
public abstract class TestFixtureJunit4 extends TestFixture {
	public TestFixtureJunit4(String connectorKind, String repositoryUrl) {
		super(connectorKind, repositoryUrl);
	}

	private final class Activation extends TestCase {

		private final boolean activate;

		private Activation(String name, boolean activate) {
			super(name);
			this.activate = activate;
		}

		@Override
		protected void runTest() throws Throwable {
			if (activate) {
				activate();
			} else {
				getDefault().activate();
			}
		}

	}

	private TestSuite suite;

	@Override
	protected abstract TestFixtureJunit4 activate();

	@Override
	protected abstract TestFixtureJunit4 getDefault();


	public TestSuite createSuite(TestSuite parentSuite) {
		suite = new TestSuite("Testing on " + getInfo());
		parentSuite.addTest(suite);
		suite.addTest(new Activation("repository: " + getRepositoryUrl() + " [@" + getSimpleInfo() + "]", true));
		return suite;
	}

	public void done() {
		Assert.isNotNull(suite, "Invoke createSuite() first");
		suite.addTest(new Activation("done", false));
		suite = null;
	}

	public void add(Class<? extends TestCase> clazz) {
		Assert.isNotNull(suite, "Invoke createSuite() first");
		suite.addTestSuite(clazz);
	}

}
