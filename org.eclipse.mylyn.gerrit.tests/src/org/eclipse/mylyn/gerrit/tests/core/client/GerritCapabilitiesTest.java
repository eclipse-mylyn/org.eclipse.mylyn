/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core.client;

import junit.framework.TestCase;

import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritCapabilities;
import org.osgi.framework.Version;

public class GerritCapabilitiesTest extends TestCase {

	public void testIsSupported() {
		Version version = GerritFixture.current().getGerritVersion();
		GerritCapabilities capabilities = new GerritCapabilities(version);
		assertTrue(capabilities.isSupported());
	}

}
