/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core.activity;

import java.util.List;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityManager;

/**
 * @author Steffen Pingel
 * @since 3.7
 */
public abstract class AbstractTaskActivityMonitor {

	/**
	 * @since 3.7
	 */
	public abstract List<ITask> getActivationHistory();

	/**
	 * @since 3.7
	 */
	public abstract boolean isEnabled();

	/**
	 * @since 3.7
	 */
	public abstract void loadActivityTime();

	/**
	 * @since 3.7
	 */
	public abstract void reloadActivityTime();

	/**
	 * @since 3.7
	 */
	public abstract void start(ITaskActivityManager taskActivityManager);

	/**
	 * @since 3.7
	 */
	public abstract void stop();

}
