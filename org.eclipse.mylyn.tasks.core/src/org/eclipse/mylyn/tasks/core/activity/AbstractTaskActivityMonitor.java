/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
