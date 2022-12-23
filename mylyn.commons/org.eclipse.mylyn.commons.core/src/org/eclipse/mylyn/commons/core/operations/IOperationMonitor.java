/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core.operations;

import org.eclipse.core.runtime.IProgressMonitorWithBlocking;

/**
 * @author Steffen Pingel
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 3.7
 * @see OperationUtil
 */
public interface IOperationMonitor extends IProgressMonitorWithBlocking {

	public enum OperationFlag {
		BACKGROUND
	};

	public abstract void addFlag(OperationFlag flag);

	public abstract boolean hasFlag(OperationFlag flag);

	public abstract IOperationMonitor newChild(int totalWork);

	public abstract IOperationMonitor newChild(int totalWork, int suppressFlags);

	public void removeFlag(OperationFlag flag);

	public abstract IOperationMonitor setWorkRemaining(int workRemaining);

}
