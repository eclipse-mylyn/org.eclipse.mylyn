/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class SynchronizeJob extends Job {

	private boolean changedTasksSynchronization = true;

	private boolean fullSynchronization = false;

	public SynchronizeJob(String name) {
		super(name);
	}

	public boolean isChangedTasksSynchronization() {
		return changedTasksSynchronization;
	}

	public boolean isFullSynchronization() {
		return fullSynchronization;
	}

	public void setChangedTasksSynchronization(boolean synchronizeChangedTasks) {
		this.changedTasksSynchronization = synchronizeChangedTasks;
	}

	public void setFullSynchronization(boolean fullSynchronization) {
		this.fullSynchronization = fullSynchronization;
	}

}
