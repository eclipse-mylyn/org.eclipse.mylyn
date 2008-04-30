/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;

/**
 * @author Mik Kersten
 * @deprecated
 */
@Deprecated
public class FilterArchiveContainerAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.filter.archive";

	private static final String LABEL = "Filter Archives";

	private final TaskListView view;

	public FilterArchiveContainerAction(TaskListView view) {
		this.view = view;
		setText(LABEL);
		setToolTipText(LABEL);
		setId(ID);
		setImageDescriptor(CommonImages.FILTER_ARCHIVE);
		setChecked(TasksUiPlugin.getDefault().getPreferenceStore().contains(
				TasksUiPreferenceConstants.FILTER_ARCHIVE_MODE));
	}

	@Override
	public void run() {
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.FILTER_ARCHIVE_MODE,
				isChecked());
		if (isChecked()) {
			view.addFilter(view.getArchiveFilter());
		} else {
			view.removeFilter(view.getArchiveFilter());
		}
		this.view.refresh();
	}
}
