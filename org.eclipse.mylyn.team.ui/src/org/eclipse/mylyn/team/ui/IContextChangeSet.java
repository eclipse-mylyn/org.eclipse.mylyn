/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.team.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * Mixin interface used for custom change set support.
 * 
 * @author Mik Kersten
 * @since 3.0
 */
public interface IContextChangeSet {

	public abstract ITask getTask();

	public abstract void updateLabel();

	public abstract void restoreResources(IResource[] resources) throws CoreException;

	public abstract String getComment(boolean checkTaskRepository);

}
