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

import java.lang.reflect.Field;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Mik Kersten
 */
public class TaskListFilteredTree extends FilteredTree {

	private static final String LABEL_FIND = " Find: ";

	private Job refreshJobHack;
	
	public TaskListFilteredTree(Composite parent, int treeStyle, PatternFilter filter) {
		super(parent, treeStyle, filter);
		Field refreshField;
		try {
			refreshField = FilteredTree.class.getDeclaredField("refreshJob");
			refreshField.setAccessible(true);
			refreshJobHack = (Job)refreshField.get(this);
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Could not get refresh job", false);
		}
	}
	
	@Override
    protected Composite createFilterControls(Composite parent){
		Composite container = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		container.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		container.setLayout(gridLayout); 
		
		Label label = new Label(container, SWT.LEFT);
		label.setText(LABEL_FIND); 
				
		super.createFilterControls(container);
		return container;
	}

    protected void textChanged() {
    	if (refreshJobHack != null) {
    		refreshJobHack.schedule(500);
    	} 
    	//refreshJob.schedule(200);
    	
//    	try {
//			Thread.sleep(300);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	super.textChanged();
    }
}
