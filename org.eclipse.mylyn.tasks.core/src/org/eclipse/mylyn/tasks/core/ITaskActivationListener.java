/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
	public abstract void preTaskActivated(ITask task);

	/**
	 * @since 3.0
	 */
	public abstract void preTaskDeactivated(ITask task);

	/**
	 * @since 3.0
	 */
	public abstract void taskActivated(ITask task);

	/**
	 * @since 3.0
	 */
	public abstract void taskDeactivated(ITask task);

}