/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.commons.core;

import org.eclipse.core.runtime.IStatus;

/**
 * @author Mik Kersten
 * @since 3.0
 */
public interface IStatusHandler {

	/**
	 * Display/log internal failure
	 * 
	 * @param status
	 *            IStatus representing failure
	 * @param inform
	 *            inform user via dialog, if no only status is logged
	 * @since 3.0
	 */
	public abstract void fail(IStatus status, boolean informUser);

	/**
	 * Display funtional error to user
	 * 
	 * @param title
	 *            Title of dialog to display
	 * @param status
	 *            IStatus to display
	 * @since 3.0
	 */
	public abstract void displayStatus(final String title, final IStatus status);
}
