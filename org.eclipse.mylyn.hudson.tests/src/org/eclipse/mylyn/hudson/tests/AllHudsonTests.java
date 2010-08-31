/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.hudson.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.hudson.tests.client.HudsonClientTest;
import org.eclipse.mylyn.hudson.tests.core.HudsonServerBehaviourTest;
import org.eclipse.mylyn.hudson.tests.support.HudsonFixture;

/**
 * @author Steffen Pingel
 */
public class AllHudsonTests {

	public static Test suite() {
		return suite(false);
	}

	public static Test suite(boolean defaultOnly) {
		TestSuite suite = new TestSuite("Tests for org.eclipse.mylyn.hudson.tests");
		suite.addTestSuite(HudsonServerBehaviourTest.class);
		// network tests
		for (HudsonFixture fixture : HudsonFixture.ALL) {
			fixture.createSuite(suite);
			fixture.add(HudsonClientTest.class);
			fixture.done();
		}
		return suite;
	}

}
