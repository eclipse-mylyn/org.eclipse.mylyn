/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTaskListContentProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTaskListPresentation;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListContentProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;

/**
 * @author Mik Kersten
 */
public class CategorizedPresentation extends AbstractTaskListPresentation {

	public static final String ID = "org.eclipse.mylyn.tasks.ui.categorized"; //$NON-NLS-1$

	public CategorizedPresentation() {
		super(ID);
	}

	@Override
	protected AbstractTaskListContentProvider createContentProvider(TaskListView taskListView) {
		return new TaskListContentProvider(taskListView);
	}

}
