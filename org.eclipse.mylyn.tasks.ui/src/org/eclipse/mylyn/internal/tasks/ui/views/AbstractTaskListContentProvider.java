/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * @author Eugene Kuleshov
 * @author Mik Kersten
 */
public abstract class AbstractTaskListContentProvider implements ITreeContentProvider {

	protected TaskListView taskListView;

	public AbstractTaskListContentProvider(TaskListView view) {
		this.taskListView = view;
	}
}
