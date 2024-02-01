/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core;

/**
 * @author Rob Elves
 * @since 3.0
 */
public interface ITaskActivationListener {

	/**
	 * @since 3.0
	 */
	void preTaskActivated(ITask task);

	/**
	 * @since 3.0
	 */
	void preTaskDeactivated(ITask task);

	/**
	 * @since 3.0
	 */
	void taskActivated(ITask task);

	/**
	 * @since 3.0
	 */
	void taskDeactivated(ITask task);

}