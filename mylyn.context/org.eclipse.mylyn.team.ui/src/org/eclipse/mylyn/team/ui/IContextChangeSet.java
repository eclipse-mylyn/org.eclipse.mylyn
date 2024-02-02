/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
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

	ITask getTask();

	void updateLabel();

	void restoreResources(IResource[] resources) throws CoreException;

	String getComment(boolean checkTaskRepository);

}
