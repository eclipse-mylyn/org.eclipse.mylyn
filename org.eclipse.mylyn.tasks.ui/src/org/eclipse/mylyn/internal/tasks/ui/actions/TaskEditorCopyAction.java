/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;

/**
 * TODO: delete?
 * 
 * @author Mik Kersten
 */
public class TaskEditorCopyAction extends Action {

	public TaskEditorCopyAction() {
		setText("TaskInfoEditor.copy.text"); //$NON-NLS-1$
	}

	@Override
	public void run() {
		// if (editorPart instanceof TaskInfoEditor)
		// ((TaskInfoEditor)editorPart).getCurrentText().copy();
	}

}
