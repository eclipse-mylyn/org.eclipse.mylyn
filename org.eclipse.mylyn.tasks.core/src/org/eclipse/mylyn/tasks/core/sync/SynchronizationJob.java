/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
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

	private boolean fetchSubtasks = true;

	/**
	 * @since 3.0
	 */
	public SynchronizationJob(String name) {
		super(name);
		this.monitor = new DelegatingProgressMonitor();
		this.monitor.setData(this);
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

	/**
	 * Specify whether subtasks should be fetched as part of task synchronization. Defaults to true.
	 * 
	 * @since 3.12
	 */
	public void setFetchSubtasks(boolean fetchSubtasks) {
		this.fetchSubtasks = fetchSubtasks;
	}

	/**
	 * @return whether subtasks should be fetched as part of task synchronization
	 * @since 3.12
	 */
	public boolean getFetchSubtasks() {
		return fetchSubtasks;
	}
}
