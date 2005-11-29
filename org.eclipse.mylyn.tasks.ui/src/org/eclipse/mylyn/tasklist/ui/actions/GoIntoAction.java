/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.tasklist.TasklistImages;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class GoIntoAction extends Action implements IViewActionDelegate {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.view.go.into";
//		
//	private DrillDownAdapter drillDownAdapter;
//	
	public GoIntoAction() {
		setId(ID);
		setText("Go Into Category");
		setToolTipText("Go Into Category");
		setImageDescriptor(TasklistImages.GO_INTO);
	}

	public void init(IViewPart view) {
		// TODO Auto-generated method stub
		
	}

	public void run() {
		if(TaskListView.getDefault() != null) {
			TaskListView.getDefault().goIntoCategory();
		}
	}

	public void run(IAction action) {
		run();
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
}
