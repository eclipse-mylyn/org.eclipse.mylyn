/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.core;

import org.eclipse.core.runtime.IProgressMonitorWithBlocking;

/**
 * @author Steffen Pingel
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
