/*******************************************************************************
 * Copyright (c) 2009, 2024 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util;

import org.eclipse.core.runtime.Assert;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Steffen Pingel
 * @author Thomas Ehrnhoefer
 */
@SuppressWarnings("nls")
public abstract class RepositoryTestFixture extends AbstractTestFixture {

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
				((RepositoryTestFixture) getDefault()).activate();
			}
		}

	}

	private TestSuite suite;

	public RepositoryTestFixture(String connectorKind, String repositoryUrl) {
		super(connectorKind, repositoryUrl);
		useCertificateAuthentication = repositoryUrl.contains("/secure/");
	}

	public void add(Class<? extends TestCase> clazz) {
		Assert.isNotNull(suite, "Invoke createSuite() first");
		suite.addTestSuite(clazz);
	}

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

	protected abstract RepositoryTestFixture activate();

}
