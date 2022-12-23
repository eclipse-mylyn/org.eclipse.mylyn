/*******************************************************************************
 * Copyright (c) 2004, 2016 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.dialogs;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListSorter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListSorter.GroupBy;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class TaskListSortDialog extends TaskCompareDialog {

	private Combo modeCombo;

	private final TaskListView taskListView;

	public TaskListSortDialog(IShellProvider parentShell, TaskListView taskListView) {
		super(parentShell, taskListView.getSorter().getTaskComparator());
		this.taskListView = taskListView;
		setTitle(Messages.TaskListSortDialog_Title);
	}

	@Override
	protected Control createContentArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(GridLayoutFactory.fillDefaults().create());

		Group groupByComposite = new Group(container, SWT.NONE);
		groupByComposite.setLayout(new GridLayout(2, false));
		groupByComposite.setText(Messages.TaskListSortDialog_Queries_and_Categories);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(groupByComposite);

		Label numberLabel = new Label(groupByComposite, SWT.NULL);
		numberLabel.setText(Messages.TaskListSortDialog_Grouped_by);
		modeCombo = new Combo(groupByComposite, SWT.READ_ONLY);
		modeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GroupBy[] values = TaskListSorter.GroupBy.values();
		for (GroupBy groupBy : values) {
			modeCombo.add(groupBy.getLabel());
		}
		modeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				markDirty();
			}
		});
		modeCombo.select(taskListView.getSorter().getGroupBy().ordinal());

		Control child = super.createContentArea(container);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(child);

		return container;
	}

	@Override
	protected void okPressed() {
		if (isDirty()) {
			int selectionIndex = modeCombo.getSelectionIndex();
			if (selectionIndex != -1) {
				taskListView.getSorter().setGroupBy(TaskListSorter.GroupBy.values()[selectionIndex]);
			}
		}
		super.okPressed();
	}

}
