/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.views;

import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Mik Kersten
 */
public class TaskListFilteredTree extends AbstractMylarFilteredTree {

	public TaskListFilteredTree(Composite parent, int treeStyle, PatternFilter filter) {
		super(parent, treeStyle, filter);
	}

	private static final String LABEL_NO_ACTIVE = "<no active task>";
	
	private Hyperlink activeTaskLabel;
	
	protected Composite createStatusComposite(Composite container) {
		activeTaskLabel = new Hyperlink(container, SWT.LEFT);
		activeTaskLabel.setText(LABEL_NO_ACTIVE);
		ITask activeTask = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask();
		if (activeTask != null) {
			indicateActiveTask(activeTask);
		}
		
		activeTaskLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				TaskListFilteredTree.super.filterText.setText("");
				if (TaskListView.getFromActivePerspective().getDrilledIntoCategory() != null) {
					TaskListView.getFromActivePerspective().goUpToRoot();
				}
				TaskListFilteredTree.this.textChanged();
				TaskListView.getFromActivePerspective().selectedAndFocusTask(
						TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask()
				);
			}

		});
		return activeTaskLabel;
	}

    public void indicateActiveTask(ITask task) {
    	String text = task.getDescription();
    	activeTaskLabel.setText(text);
		activeTaskLabel.setUnderlined(true);
		activeTaskLabel.setToolTipText(task.getDescription());
		filterComposite.layout();
    }
    
    public void indicateNoActiveTask() {
    	activeTaskLabel.setText(LABEL_NO_ACTIVE);
		activeTaskLabel.setUnderlined(false);
		activeTaskLabel.setToolTipText("");
		filterComposite.layout();
    }
    
	public void setFilterText(String string) {
		if (filterText != null){
			filterText.setText(string);
			selectAll();		
		}
	}
}
