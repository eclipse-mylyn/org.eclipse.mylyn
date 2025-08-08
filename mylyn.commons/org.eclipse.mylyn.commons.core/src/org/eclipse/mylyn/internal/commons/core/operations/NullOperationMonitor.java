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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;

public class NullOperationMonitor extends NullProgressMonitor implements IOperationMonitor {

	private EnumSet<OperationFlag> flags;

	@Override
	public synchronized void addFlag(OperationFlag flag) {
		if (flags == null) {
			flags = EnumSet.of(flag);
		} else {
			flags.add(flag);
		}
	}

	@Override
	public void clearBlocked() {
		// ignore
	}

	@Override
	public synchronized boolean hasFlag(OperationFlag flag) {
		if (flags != null) {
			return flags.contains(flag);
		}
		return false;
	}

	@Override
	public IOperationMonitor newChild(int totalWork) {
		return this;
	}

	@Override
	public IOperationMonitor newChild(int totalWork, int suppressFlags) {
		return this;
	}

	@Override
	public synchronized void removeFlag(OperationFlag flag) {
		if (flags != null) {
			flags.remove(flag);
		}
	}

	@Override
	public void setBlocked(IStatus reason) {
		// ignore
	}

	@Override
	public IOperationMonitor setWorkRemaining(int workRemaining) {
		return this;
	}

}