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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.tasklist.ui.ITaskListElement;
import org.eclipse.mylar.tasklist.ui.TaskListImages;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;

public class CopyDescriptionAction extends Action {

	private static final String DESCRIPTION_PREFIX = "Bugzilla Bug ";

	public static final String ID = "org.eclipse.mylar.tasklist.actions.copy";
	
	private TaskListView view;
	
	public CopyDescriptionAction(TaskListView view) {
		this.view = view;
		setText("Copy Description");
		setToolTipText("Copy Description");
		setId(ID);
		setImageDescriptor(TaskListImages.COPY);
		setAccelerator(SWT.MOD1 + 'c');
	}

	@Override
	public void run() {
		 ISelection selection = this.view.getViewer().getSelection();
	    Object obj = ((IStructuredSelection)selection).getFirstElement();
	    if (obj instanceof ITaskListElement) {
	    	ITaskListElement element = (ITaskListElement)obj;
	    	String description = DESCRIPTION_PREFIX + element.getDescription();
	    	
	    	// HACK: this should be done using proper copying
	    	StyledText styledText = new StyledText(view.getDummyComposite(), SWT.NULL);
	    	styledText.setText(description);
	    	styledText.selectAll();
	    	styledText.copy();
	    	styledText.dispose();
	    }
	}
}
