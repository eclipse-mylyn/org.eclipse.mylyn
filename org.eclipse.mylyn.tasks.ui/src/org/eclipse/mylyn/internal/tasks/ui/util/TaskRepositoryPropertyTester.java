/*******************************************************************************
 * Copyright (c) 2004, 2008 Jingwen Ou and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jingwen Ou - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Jingwen Ou
 */
public class TaskRepositoryPropertyTester extends PropertyTester {

	private static final String PROPERTY_CONNECTOR_KIND = "connectorKind"; //$NON-NLS-1$

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof TaskRepository) {
			TaskRepository taskRepository = (TaskRepository) receiver;
			if (PROPERTY_CONNECTOR_KIND.equals(property)) {
				return taskRepository.getConnectorKind().equals(expectedValue);
			}
		}

		return false;
	}

}
