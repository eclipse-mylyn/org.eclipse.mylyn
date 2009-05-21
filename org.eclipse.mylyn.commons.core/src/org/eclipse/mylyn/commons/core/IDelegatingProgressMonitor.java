/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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

}
