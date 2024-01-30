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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ProgressMonitorWrapper;

/**
 * Delegates to all attached monitors.
 * 
 * @author Steffen Pingel
 * @author Thomas Ehrnhoefer
 * @author Robert Elves
 * @since 3.2
 */
public class DelegatingProgressMonitor implements IDelegatingProgressMonitor {

	/**
	 * Returns the parent delegating progress monitor of <code>monitor</code>.
	 * 
	 * @param monitor
	 *            the child monitor
	 * @return the monitor; null, if none
	 * @since 3.5
	 */
	public static IDelegatingProgressMonitor getMonitorFrom(IProgressMonitor monitor) {
		if (monitor == null) {
			return null;
		} else if (monitor instanceof IDelegatingProgressMonitor) {
			return (IDelegatingProgressMonitor) monitor;
		} else if (monitor instanceof ProgressMonitorWrapper) {
			return getMonitorFrom(((ProgressMonitorWrapper) monitor).getWrappedProgressMonitor());
		}
		return null;
	}

	private boolean calledBeginTask;

	private boolean canceled;

	private Object data;

	private boolean done;

	private double internalWorked;

	private final List<IProgressMonitor> monitors;

	private String subTaskName;

	private String taskName;

	private int totalWork;

	private int worked;

	public DelegatingProgressMonitor() {
		monitors = new CopyOnWriteArrayList<>();
	}

	@Override
	public void attach(IProgressMonitor monitor) {
		Assert.isNotNull(monitor);
		if (calledBeginTask) {
			monitor.beginTask(taskName, totalWork);
		}
		if (taskName != null) {
			monitor.setTaskName(taskName);
		}
		if (subTaskName != null) {
			monitor.subTask(subTaskName);
		}
		if (worked > 0) {
			monitor.worked(worked);
		}
		if (internalWorked > 0) {
			monitor.internalWorked(internalWorked);
		}
		if (canceled) {
			monitor.setCanceled(canceled);
		}
		if (done) {
			monitor.done();
		}
		monitors.add(monitor);
	}

	@Override
	public void beginTask(String name, int totalWork) {
		if (!calledBeginTask) {
			taskName = name;
			this.totalWork = totalWork;
			calledBeginTask = true;
		}
		for (IProgressMonitor monitor : monitors) {
			monitor.beginTask(name, totalWork);
		}
	}

	@Override
	public void detach(IProgressMonitor monitor) {
		monitors.remove(monitor);
	}

	@Override
	public void done() {
		done = true;
		for (IProgressMonitor monitor : monitors) {
			monitor.done();
		}
	}

	/**
	 * @see IDelegatingProgressMonitor#getData()
	 * @since 3.5
	 */
	@Override
	public Object getData() {
		return data;
	}

	@Override
	public void internalWorked(double work) {
		internalWorked += work;
		for (IProgressMonitor monitor : monitors) {
			monitor.internalWorked(work);
		}
	}

	@Override
	public boolean isCanceled() {
		boolean canceled = false;
		for (IProgressMonitor monitor : monitors) {
			canceled |= monitor.isCanceled();
		}
		if (canceled) {
			setCanceled(canceled);
		}
		return canceled;
	}

	@Override
	public void setCanceled(boolean value) {
		canceled = value;
		for (IProgressMonitor monitor : monitors) {
			monitor.setCanceled(value);
		}
	}

	/**
	 * @see IDelegatingProgressMonitor#setData()
	 * @since 3.5
	 */
	@Override
	public void setData(Object o) {
		data = o;
	}

	@Override
	public void setTaskName(String name) {
		taskName = name;
		for (IProgressMonitor monitor : monitors) {
			monitor.setTaskName(name);
		}
	}

	@Override
	public void subTask(String name) {
		subTaskName = name;
		for (IProgressMonitor monitor : monitors) {
			monitor.subTask(name);
		}
	}

	@Override
	public void worked(int work) {
		worked += work;
		for (IProgressMonitor monitor : monitors) {
			monitor.worked(work);
		}
	}

}
