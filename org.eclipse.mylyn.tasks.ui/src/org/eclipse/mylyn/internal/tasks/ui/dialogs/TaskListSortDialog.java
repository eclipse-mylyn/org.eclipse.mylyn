/*******************************************************************************
 * Copyright (c) 2004, 2008 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Frank Becker
 */
public class TaskListSortDialog extends TaskCompareDialog {

	public TaskListSortDialog(IShellProvider parentShell, TaskListView taskListView) {
		super(parentShell, taskListView.getSorter().getComparator());
		setTitle(TaskListView.LABEL_VIEW + Messages.TaskListSortDialog_Sorting);
	}

	@Override
	protected void createDialogStartArea(Composite parent) {
		Label sortByLabel = new Label(parent, SWT.NULL);
		sortByLabel.setText(Messages.TaskListSortDialog_Sort_order);
		GridData data = new GridData();
		data.horizontalSpan = 3;
		sortByLabel.setLayoutData(data);
		Dialog.applyDialogFont(parent);
	}

}
