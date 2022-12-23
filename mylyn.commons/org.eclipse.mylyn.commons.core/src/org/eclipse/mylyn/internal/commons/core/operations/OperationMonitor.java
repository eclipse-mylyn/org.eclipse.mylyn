/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.core.operations;

import java.util.EnumSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;

public class OperationMonitor implements IOperationMonitor {

	private EnumSet<OperationFlag> flags;

	private final SubMonitor monitor;

	private final IOperationMonitor root;

	public OperationMonitor(IOperationMonitor root, IProgressMonitor monitor) {
		this.root = root;
		this.monitor = SubMonitor.convert(monitor);
	}

	public OperationMonitor(IOperationMonitor root, IProgressMonitor monitor, String taskName, int work) {
		this.root = root;
		this.monitor = SubMonitor.convert(monitor, taskName, work);
	}

	public synchronized void addFlag(OperationFlag flag) {
		if (root != null) {
			root.addFlag(flag);
		} else if (flags == null) {
			flags = EnumSet.of(flag);
		} else {
			flags.add(flag);
		}
	}

	public void beginTask(String name, int totalWork) {
		monitor.beginTask(name, totalWork);
	}

	public void clearBlocked() {
		monitor.clearBlocked();
	}

	public void done() {
		monitor.done();
	}

	@Override
	public boolean equals(Object obj) {
		return monitor.equals(obj);
	}

	public synchronized boolean hasFlag(OperationFlag flag) {
		if (root != null) {
			return root.hasFlag(flag);
		} else if (flags != null) {
			return flags.contains(flag);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return monitor.hashCode();
	}

	public void internalWorked(double work) {
		monitor.internalWorked(work);
	}

	public boolean isCanceled() {
		return monitor.isCanceled();
	}

	public IOperationMonitor newChild(int totalWork) {
		return new OperationMonitor((root == null) ? this : root, monitor.newChild(totalWork));
	}

	public IOperationMonitor newChild(int totalWork, int suppressFlags) {
		return new OperationMonitor((root == null) ? this : root, monitor.newChild(totalWork, suppressFlags));
	}

	public synchronized void removeFlag(OperationFlag flag) {
		if (root != null) {
			root.removeFlag(flag);
		} else if (flags != null) {
			flags.remove(flag);
		}
	}

	public void setBlocked(IStatus reason) {
		monitor.setBlocked(reason);
	}

	public void setCanceled(boolean b) {
		monitor.setCanceled(b);
	}

	public void setTaskName(String name) {
		monitor.setTaskName(name);
	}

	public IOperationMonitor setWorkRemaining(int workRemaining) {
		monitor.setWorkRemaining(workRemaining);
		return this;
	}

	public void subTask(String name) {
		monitor.subTask(name);
	}

	@Override
	public String toString() {
		return monitor.toString();
	}

	public void worked(int work) {
		monitor.worked(work);
	}

}