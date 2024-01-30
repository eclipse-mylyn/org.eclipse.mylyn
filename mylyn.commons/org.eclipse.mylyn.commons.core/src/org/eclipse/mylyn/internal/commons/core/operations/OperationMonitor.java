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

	@Override
	public synchronized void addFlag(OperationFlag flag) {
		if (root != null) {
			root.addFlag(flag);
		} else if (flags == null) {
			flags = EnumSet.of(flag);
		} else {
			flags.add(flag);
		}
	}

	@Override
	public void beginTask(String name, int totalWork) {
		monitor.beginTask(name, totalWork);
	}

	@Override
	public void clearBlocked() {
		monitor.clearBlocked();
	}

	@Override
	public void done() {
		monitor.done();
	}

	@Override
	public boolean equals(Object obj) {
		return monitor.equals(obj);
	}

	@Override
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

	@Override
	public void internalWorked(double work) {
		monitor.internalWorked(work);
	}

	@Override
	public boolean isCanceled() {
		return monitor.isCanceled();
	}

	@Override
	public IOperationMonitor newChild(int totalWork) {
		return new OperationMonitor(root == null ? this : root, monitor.newChild(totalWork));
	}

	@Override
	public IOperationMonitor newChild(int totalWork, int suppressFlags) {
		return new OperationMonitor(root == null ? this : root, monitor.newChild(totalWork, suppressFlags));
	}

	@Override
	public synchronized void removeFlag(OperationFlag flag) {
		if (root != null) {
			root.removeFlag(flag);
		} else if (flags != null) {
			flags.remove(flag);
		}
	}

	@Override
	public void setBlocked(IStatus reason) {
		monitor.setBlocked(reason);
	}

	@Override
	public void setCanceled(boolean b) {
		monitor.setCanceled(b);
	}

	@Override
	public void setTaskName(String name) {
		monitor.setTaskName(name);
	}

	@Override
	public IOperationMonitor setWorkRemaining(int workRemaining) {
		monitor.setWorkRemaining(workRemaining);
		return this;
	}

	@Override
	public void subTask(String name) {
		monitor.subTask(name);
	}

	@Override
	public String toString() {
		return monitor.toString();
	}

	@Override
	public void worked(int work) {
		monitor.worked(work);
	}

}