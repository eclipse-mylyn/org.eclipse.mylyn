/*******************************************************************************
 * Copyright (c) 2004, 2008 Jingwen Ou and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jingwen Ou - initial API and implementation
 *     Tasktop Technologies - enhancements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Jingwen Ou
 * @author Steffen Pingel
 */
public class TaskRepositoryPropertyTester extends PropertyTester {

	private static final String PROPERTY_CONNECTOR_KIND = "connectorKind"; //$NON-NLS-1$

	private static final String PROPERTY_USER_MANAGED = "userManaged"; //$NON-NLS-1$

	private boolean equals(boolean value, Object expectedValue) {
		return new Boolean(value).equals(expectedValue);
	}

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof TaskRepository) {
			TaskRepository repository = (TaskRepository) receiver;
			if (PROPERTY_CONNECTOR_KIND.equals(property)) {
				return repository.getConnectorKind().equals(expectedValue);
			} else if (PROPERTY_USER_MANAGED.equals(property)) {
				AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
						repository.getConnectorKind());
				return equals(connector != null && connector.isUserManaged(), expectedValue);
			}
		}

		return false;
	}

}
