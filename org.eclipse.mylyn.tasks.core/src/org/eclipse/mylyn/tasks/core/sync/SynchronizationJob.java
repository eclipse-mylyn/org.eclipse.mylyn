/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.sync;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.core.DelegatingProgressMonitor;
import org.eclipse.mylyn.commons.core.IDelegatingProgressMonitor;

/**
 * @author Steffen Pingel
 * @since 3.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class SynchronizationJob extends Job {

//	private boolean changedTasksSynchronization = true;

	private boolean fullSynchronization = false;

	/**
	 * @since 3.3
	 */
	protected final IDelegatingProgressMonitor monitor;

	/**
	 * @since 3.0
	 */
	public SynchronizationJob(String name) {
		super(name);
		this.monitor = new DelegatingProgressMonitor();
	}

//	public boolean isChangedTasksSynchronization() {
//		return changedTasksSynchronization;
//	}

	/**
	 * @since 3.0
	 */
	public boolean isFullSynchronization() {
		return fullSynchronization;
	}

//	public void setChangedTasksSynchronization(boolean synchronizeChangedTasks) {
//		this.changedTasksSynchronization = synchronizeChangedTasks;
//	}

	/**
	 * @since 3.0
	 */
	public void setFullSynchronization(boolean fullSynchronization) {
		this.fullSynchronization = fullSynchronization;
	}

	/**
	 * @since 3.3
	 */
	public IDelegatingProgressMonitor getMonitor() {
		return monitor;
	}
}
