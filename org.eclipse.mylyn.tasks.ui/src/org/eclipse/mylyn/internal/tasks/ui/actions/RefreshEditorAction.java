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

package org.eclipse.mylar.internal.tasks.ui.actions;

import java.util.Iterator;

import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylar.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Rob Elves
 */
public class RefreshEditorAction extends BaseSelectionListenerAction {

	private static final String LABEL = "Refresh";

	public static final String ID = "org.eclipse.mylar.tasklist.actions.refresh.editor";

	public RefreshEditorAction() {
		super(LABEL);
		setToolTipText(LABEL);
		setId(ID);
		setImageDescriptor(TaskListImages.REFRESH);
		//setAccelerator(SWT.MOD1 + 'r');
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		if (super.getStructuredSelection() != null) {
			for (Iterator iter = super.getStructuredSelection().iterator(); iter.hasNext();) {
				runWithSelection(iter.next());
			}
		}
	}

	private void runWithSelection(Object selectedObject) {
		if(selectedObject instanceof TaskEditor) {
			TaskEditor editor = (TaskEditor)selectedObject;
			editor.refreshEditorContents();
		} else if(selectedObject instanceof AbstractRepositoryTaskEditor) {
			((AbstractRepositoryTaskEditor)selectedObject).refreshEditor();
		}
	}
}
