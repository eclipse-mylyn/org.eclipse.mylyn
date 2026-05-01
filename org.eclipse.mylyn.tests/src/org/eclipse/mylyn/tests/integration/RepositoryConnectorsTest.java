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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Collection;
import java.util.stream.Collectors;

import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * Test harness for iterating over all connectors and performing a test.
 *
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class RepositoryConnectorsTest {

	@TestFactory
	public Collection<DynamicTest> testConnectorKind() {
		return TasksUi.getRepositoryManager()
				.getRepositoryConnectors()
				.stream()
				.map(connector -> dynamicTest(connector.getClass().getName(),
						() -> assertNotNull(connector.getConnectorKind(),
								"Expected non-null value for " + connector.getClass())))
				.collect(Collectors.toList());
	}

}