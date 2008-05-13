/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Steffen Pingel
 */
public class TaskPropertyTester extends PropertyTester {

	private static final String PROPERTY_CONNECTOR_KIND = "connectorKind";

	private static final String PROPERTY_IS_LOCAL = "isLocal";

	private static final String PROPERTY_IS_COMPLETED = "isCompleted";

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof ITask) {
			ITask task = (ITask) receiver;
			if (PROPERTY_CONNECTOR_KIND.equals(property)) {
				return task.getConnectorKind().equals(expectedValue);
			} else if (PROPERTY_IS_LOCAL.equals(property)) {
				return (task instanceof AbstractTask)
						&& new Boolean(((AbstractTask) task).isLocal()).equals(expectedValue);
			} else if (PROPERTY_IS_COMPLETED.equals(property)) {
				return new Boolean(task.isCompleted()).equals(expectedValue);
			}
		}
		return false;
	}
}
