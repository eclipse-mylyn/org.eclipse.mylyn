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

package org.eclipse.mylar.internal.tasklist.ui.views;

import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Mik Kersten
 */
public class TaskListFilteredTree extends FilteredTree {

	private static final int LABEL_MAX_SIZE = 30;

//	private static final int DELAY_REFRESH = 700;

	private static final String LABEL_FIND = " Find:";

	private static final String LABEL_NO_ACTIVE = "          <no active task>";
	
//	private static final String LABEL_NO_ACTIVE = "     <no active task>";
		
//	private Job refreshJob;
	
	private Hyperlink activeTaskLabel;
	
	public TaskListFilteredTree(Composite parent, int treeStyle, PatternFilter filter) {
		super(parent, treeStyle, filter);
//		Field refreshField;
//		try {
//			refreshField = FilteredTree.class.getDeclaredField("refreshJob");
//			refreshField.setAccessible(true);
//			refreshJob = (Job)refreshField.get(this);
//		} catch (Exception e) {
//			MylarStatusHandler.fail(e, "Could not get refresh job", false);
//		}
	}
	
	@Override
    protected Composite createFilterControls(Composite parent){
		Composite container = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		container.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		container.setLayout(gridLayout); 
		
		Label label = new Label(container, SWT.LEFT);
		label.setText(LABEL_FIND);
				
		super.createFilterControls(container);
//		patternFilter.setSize(100, patternFilter.getSize().y);
		
		filterText.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.ESC) {
					setFilterText("");
				}
			}

			public void keyReleased(KeyEvent e) {
				// ignore
			}
		});

		activeTaskLabel = new Hyperlink(container, SWT.RIGHT);
		activeTaskLabel.setText(LABEL_NO_ACTIVE);
		ITask activeTask = MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTask();
		if (activeTask != null) {
			indicateActiveTask(activeTask);
		}
		activeTaskLabel.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				// ignore
			}

			public void mouseDown(MouseEvent e) {
				TaskListFilteredTree.super.filterText.setText("");
				if (TaskListView.getDefault().getDrilledIntoCategory() != null) {
					TaskListView.getDefault().goUpToRoot();
				}
//				TaskListFilteredTree.this.textChanged(0);
				TaskListFilteredTree.this.textChanged();
				TaskListView.getDefault().selectedAndFocusTask(
						MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTask()
				);
			}

			public void mouseUp(MouseEvent e) {
				// ignore
			}
		});
		
		return container;
	}

    protected void textChanged() {
    	super.textChanged();
//    	textChanged(DELAY_RE	`FRESH);
    	//refreshJob.schedule(200);
    }
    
//    public void textChanged(int delay) {
//    	if (refreshJob != null) {
//    		refreshJob.schedule(delay);
//    	} 
//    }

    public void indicateActiveTask(ITask task) {
    	String text = task.getDescription();
    	if (text.length() > LABEL_MAX_SIZE) {
    		text = text.substring(0, LABEL_MAX_SIZE);
    	}
    	activeTaskLabel.setText(text);
		activeTaskLabel.setUnderlined(true);
    }
    
    public void indicateNoActiveTask() {
    	activeTaskLabel.setText(LABEL_NO_ACTIVE);
		activeTaskLabel.setUnderlined(false);
    }
    
	public void setFilterText(String string) {
		if (filterText != null){
			filterText.setText(string);
			selectAll();		
		}
	}
}
