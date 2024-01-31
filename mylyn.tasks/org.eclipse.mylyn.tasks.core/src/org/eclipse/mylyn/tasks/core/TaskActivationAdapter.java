/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
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
public class TaskActivationAdapter implements ITaskActivationListener {

	/**
	 * @since 3.16
	 */
	public boolean canDeactivateTask(ITask task) {
		return true;
	}

	@Override
	public void preTaskActivated(ITask task) {
	}

	@Override
	public void preTaskDeactivated(ITask task) {
	}

	@Override
	public void taskActivated(ITask task) {
	}

	@Override
	public void taskDeactivated(ITask task) {
	}

}