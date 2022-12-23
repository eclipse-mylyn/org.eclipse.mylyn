/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.Set;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.ui.part.ViewPart;

public abstract class AbstractTaskListView extends ViewPart {

	public abstract Set<AbstractTaskListFilter> getFilters();

	public abstract TaskListFilteredTree getFilteredTree();

	public abstract void refresh();

	public abstract boolean isFocusedMode();

	public abstract TreeViewer getViewer();

	public abstract boolean isScheduledPresentation();

	protected abstract void expandToActiveTasks();

	protected abstract void updateToolTip(boolean force);

	protected abstract boolean isAutoExpandMode();

}
