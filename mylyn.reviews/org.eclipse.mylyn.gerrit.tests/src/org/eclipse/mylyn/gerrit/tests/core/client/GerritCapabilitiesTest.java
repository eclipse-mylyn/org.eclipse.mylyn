/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core.client;

import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritCapabilities;
import org.osgi.framework.Version;

import junit.framework.TestCase;

public class GerritCapabilitiesTest extends TestCase {

	public void testIsSupported() {
		Version version = GerritFixture.current().getGerritVersion();
		GerritCapabilities capabilities = new GerritCapabilities(version);
		assertTrue(capabilities.isSupported());
	}

}
