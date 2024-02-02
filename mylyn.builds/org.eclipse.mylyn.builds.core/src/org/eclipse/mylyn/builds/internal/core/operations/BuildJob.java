/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.internal.core.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.internal.core.util.BuildsConstants;
import org.eclipse.mylyn.commons.core.DelegatingProgressMonitor;
import org.eclipse.mylyn.commons.core.IDelegatingProgressMonitor;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor.OperationFlag;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;

/**
 * @author Steffen Pingel
 */
public abstract class BuildJob extends Job {

	IStatus status;

	protected final IDelegatingProgressMonitor monitor;

	public BuildJob(String name) {
		super(name);
		monitor = new DelegatingProgressMonitor();
	}

	public IBuildElement getElement() {
		return null;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IBuildElement.class) {
			IBuildElement element = getElement();
			if (element != null) {
				return element;
			}
		}
		return super.getAdapter(adapter);
	}

	@Override
	public boolean belongsTo(Object family) {
		return family == BuildsConstants.JOB_FAMILY;
	}

	public IStatus getStatus() {
		return status;
	}

	protected void setStatus(IStatus status) {
		this.status = status;
	}

	@Override
	public IStatus run(IProgressMonitor jobMonitor) {
		try {
			monitor.setCanceled(false);
			monitor.attach(jobMonitor);
			try {
				IOperationMonitor progress = OperationUtil.convert(monitor);
				if (!isUser()) {
					progress.addFlag(OperationFlag.BACKGROUND);
				}
				return doExecute(progress);
			} catch (OperationCanceledException e) {
				return Status.CANCEL_STATUS;
			} finally {
				monitor.done();
			}
		} finally {
			monitor.detach(jobMonitor);
		}
	}

	protected abstract IStatus doExecute(IOperationMonitor progress);

	public IDelegatingProgressMonitor getMonitor() {
		return monitor;
	}

}
