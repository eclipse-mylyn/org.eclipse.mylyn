/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.support.MockRepositoryConnectorDescriptor.DynamicMockRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.junit.Test;

public class RepositoryConnectorContributorTest extends TestCase {

	@Test
	public void testConnectorContributed() {
		AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(DynamicMockRepositoryConnector.CONNECTOR_KIND);
		assertNotNull("Expected dynamically contributed mock connector", connector);
		assertEquals(DynamicMockRepositoryConnector.class, connector.getClass());
		assertEquals(DynamicMockRepositoryConnector.CONNECTOR_KIND, connector.getConnectorKind());
	}

}
