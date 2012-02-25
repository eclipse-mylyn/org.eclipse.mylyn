/*******************************************************************************
 * Copyright (c) 2004, 2008 Eugene Kuleshov and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *     Tasktop Technologies - improvements
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
		for (AbstractRepositoryConnector repositoryConnector : TasksUi.getRepositoryManager().getRepositoryConnectors()) {
			suite.addTest(new RepositoryConnectorsTest(repositoryConnector, "testConnectorKind"));
		}
		return suite;
	}

}
