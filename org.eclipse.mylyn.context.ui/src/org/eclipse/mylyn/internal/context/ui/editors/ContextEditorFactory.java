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

package org.eclipse.mylyn.internal.context.ui.editors;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.editors.ITaskEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 */
public class ContextEditorFactory implements ITaskEditorFactory {

	private static final String LABEL = "Context";

	public boolean canCreateEditorFor(AbstractTask task) {
		return task != null && ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier());
	}

	/**
	 * Works for any kind of task
	 */
	public boolean canCreateEditorFor(IEditorInput input) {
		if (input instanceof RepositoryTaskEditorInput) {
			RepositoryTaskEditorInput repositoryTaskEditorInput = (RepositoryTaskEditorInput)input;
			return repositoryTaskEditorInput.getRepositoryTask() != null;
		} else {
			return input instanceof TaskEditorInput;
		}
	}

	public IEditorPart createEditor(TaskEditor parentEditor, IEditorInput editorInput) {
		ContextEditorFormPage formPage = new ContextEditorFormPage(parentEditor, "org.eclipse.mylyn.context.ui.editor.context", LABEL);
		return formPage;
	}

	public IEditorInput createEditorInput(AbstractTask task) {
		return new ContextEditorInput(task);
	}

	public String getTitle() {
		return LABEL;
	}

	public boolean providesOutline() {
		return false;
	}
	
}
