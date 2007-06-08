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
/*
 * Created on 20-Jan-2005
 */
package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;

/**
 * TODO: delete?
 */
public class TaskEditorCopyAction extends Action {

	public TaskEditorCopyAction() {
		setText("TaskInfoEditor.copy.text");
	}

	@Override
	public void run() {
		// if (editorPart instanceof TaskInfoEditor)
		// ((TaskInfoEditor)editorPart).getCurrentText().copy();
	}

}
