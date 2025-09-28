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

package org.eclipse.mylyn.tests.util.junit5;

import org.eclipse.mylyn.tests.util.TestFixture;

/**
 * Placeholder for now.
 */
public class TestFixtureJunit5 extends TestFixture {

	public TestFixtureJunit5(String connectorKind, String repositoryUrl) {
		super(connectorKind, repositoryUrl);
	}

	@Override
	protected TestFixtureJunit5 activate() {
		return null;
	}

	@Override
	protected TestFixtureJunit5 getDefault() {
		return null;
	}

}
