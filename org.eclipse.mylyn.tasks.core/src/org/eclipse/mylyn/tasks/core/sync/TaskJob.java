/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core.sync;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.core.DelegatingProgressMonitor;
import org.eclipse.mylyn.commons.core.IDelegatingProgressMonitor;
import org.eclipse.mylyn.commons.net.Policy;

/**
 * @author Steffen Pingel
 * @since 3.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class TaskJob extends Job {
	/**
	 * @since 3.3
	 */
	protected final IDelegatingProgressMonitor monitor;

	/**
	 * @since 3.0
	 */
	public TaskJob(String name) {
		super(name);
		this.monitor = new DelegatingProgressMonitor();
		this.monitor.setData(this);
	}

	/**
	 * @since 3.0
	 */
	public abstract IStatus getStatus();

	/**
	 * @since 3.3
	 */
	public IDelegatingProgressMonitor getMonitor() {
		return monitor;
	}

	/**
	 * @since 3.9
	 */
	protected IProgressMonitor subMonitorFor(IProgressMonitor monitor, int ticks) {
		if (!isUser()) {
			return Policy.backgroundMonitorFor(monitor);
		}
		return Policy.subMonitorFor(monitor, ticks);
	}

}
