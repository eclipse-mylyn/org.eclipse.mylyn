/*******************************************************************************
 * Copyright (c) 2008, 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
