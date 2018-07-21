/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Delegates progress reporting to all attached monitors.
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author Steffen Pingel
 * @since 3.2
 */
public interface IDelegatingProgressMonitor extends IProgressMonitor {

	/**
	 * Registers to <code>monitor</code> to receive all progress events. If the operation is already in progress
	 * <code>monitor</code> will receive all queued events.
	 * 
	 * @since 3.2
	 * @see #detach(IProgressMonitor)
	 */
	public void attach(IProgressMonitor monitor);

	/**
	 * Unregisters <code>monitor</code>. No progress events will be sent to <code>monitor</code>.
	 * 
	 * @since 3.2
	 * @see #attach(IProgressMonitor)
	 */
	public void detach(IProgressMonitor monitor);

	/**
	 * Sets a user object.
	 * 
	 * @see #getData()
	 * @since 3.5
	 */
	public void setData(Object data);

	/***
	 * Returns a user object.
	 * 
	 * @see #setData(Object)
	 * @since 3.5
	 */
	public Object getData();

}
