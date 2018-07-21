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
 * Notified of task activity changes.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Shawn Minto
 * @since 2.0
 */
public interface ITaskActivityListener {

	/**
	 * @since 3.0
	 */
	public abstract void activityReset();

	/**
	 * Warning: This is called frequently (i.e. every 15s) Implementers are responsible for launching jobs for long
	 * running activity.
	 * 
	 * @since 3.0
	 */
	public abstract void elapsedTimeUpdated(ITask task, long newElapsedTime);

}
