/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.core;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.mylyn.versions.tests.support.MockRepositoryProvider;
import org.eclipse.mylyn.versions.tests.support.MockScmConnector;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class ScmCoreTest {

	@Test
	public void testMockConnectorPresent() {
		List<ScmConnector> connectors = ScmCore.getAllRegisteredConnectors();
		for (ScmConnector connector : connectors) {
			if (MockRepositoryProvider.ID.equals(connector.getProviderId())) {
				assertEquals(MockScmConnector.class, connector.getClass());
				return;
			}
		}
		Assert.fail("Expected MockScmConnector in " + connectors.toString());
	}

}
