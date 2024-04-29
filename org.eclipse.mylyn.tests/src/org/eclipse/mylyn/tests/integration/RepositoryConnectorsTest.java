/*******************************************************************************
 * Copyright (c) 2004, 2008 Eugene Kuleshov and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Eugene Kuleshov - initial API and implementation
 *     Tasktop Technologies - improvements
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tests.integration;

import junit.extensions.ActiveTestSuite;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * Test harness for iterating over all connectors and performing a test.
 * 
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class RepositoryConnectorsTest extends TestCase {

	private final AbstractRepositoryConnector connector;

	public RepositoryConnectorsTest(AbstractRepositoryConnector connector, String name) {
		super(name);
		this.connector = connector;
	}

	public void testConnectorKind() {
		assertNotNull("Expected non-null value for " + connector.getClass(), connector.getConnectorKind());
	}

	public static TestSuite suite() {
		TestSuite suite = new ActiveTestSuite(RepositoryConnectorsTest.class.getName());
		for (AbstractRepositoryConnector repositoryConnector : TasksUi.getRepositoryManager()
				.getRepositoryConnectors()) {
			suite.addTest(new RepositoryConnectorsTest(repositoryConnector, "testConnectorKind"));
		}
		return suite;
	}

}
