/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
	public abstract void activityChanged();

	/**
	 * @since 3.0
	 */
	public abstract void preTaskActivated(AbstractTask task);

	/**
	 * @since 3.0
	 */
	public abstract void preTaskDeactivated(AbstractTask task);

	public abstract void taskActivated(AbstractTask task);

	public abstract void taskDeactivated(AbstractTask task);

	public abstract void taskListRead();

}
