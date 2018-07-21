/*******************************************************************************
 * Copyright (c) 2008, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

/**
 * @author Steffen Pingel
 * @author Sam Davis
 * @since 3.0
 */
public abstract class TaskDataModelListener {

	/**
	 * @since 3.0
	 */
	public abstract void attributeChanged(TaskDataModelEvent event);

	/**
	 * @since 3.6
	 */
	public void modelRefreshed() {
	}
}
