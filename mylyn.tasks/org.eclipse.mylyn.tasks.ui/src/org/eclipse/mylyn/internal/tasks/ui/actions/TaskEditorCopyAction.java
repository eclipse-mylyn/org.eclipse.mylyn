/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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
