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

package org.eclipse.mylar.internal.context.ui.editors;

import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.ui.editors.ITaskEditorFactory;
import org.eclipse.mylar.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 */
public class ContextEdtiorFactory implements ITaskEditorFactory {

	private static final String LABEL = "Context";

	public boolean canCreateEditorFor(ITask task) {
		return ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier());
	}

	/**
	 * Works for any kind of task
	 */
	public boolean canCreateEditorFor(IEditorInput input) {
		return true;
	}

	public IEditorPart createEditor(TaskEditor parentEditor, IEditorInput editorInput) {
		ContextEditorFormPage formPage = new ContextEditorFormPage(parentEditor, "org.eclipse.mylar.context.ui.editor.context", LABEL);
		return formPage;
	}

	public IEditorInput createEditorInput(ITask task) {
		return new ContextEditorInput(task);
	}

	public String getTitle() {
		return LABEL;
	}

	public boolean providesOutline() {
		return false;
	}
	
}
