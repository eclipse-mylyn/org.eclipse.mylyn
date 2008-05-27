/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.internal.bugzilla.ui.editor.BugzillaTaskEditor;
import org.eclipse.mylyn.internal.bugzilla.ui.editor.NewBugzillaTaskEditor;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.RepositoryTaskEditorInput;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.EditorPart;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaTaskEditorFactory extends AbstractTaskEditorFactory {

	private static final String TITLE = "Bugzilla";

	@Override
	public EditorPart createEditor(TaskEditor parentEditor, IEditorInput editorInput) {
		AbstractRepositoryTaskEditor editor = null;
		if (editorInput instanceof RepositoryTaskEditorInput) {
			RepositoryTaskEditorInput taskInput = (RepositoryTaskEditorInput) editorInput;
			if (taskInput.getTaskData().isNew()) {
				editor = new NewBugzillaTaskEditor(parentEditor);
			} else {
				editor = new BugzillaTaskEditor(parentEditor);
			}
		} else if (editorInput instanceof TaskEditorInput) {
			editor = new BugzillaTaskEditor(parentEditor);
		}
		return editor;
	}

	@Override
	public IEditorInput createEditorInput(ITask task) {
		if (task instanceof BugzillaTask) {
			BugzillaTask bugzillaTask = (BugzillaTask) task;
			final TaskRepository repository = TasksUi.getRepositoryManager().getRepository(
					BugzillaCorePlugin.CONNECTOR_KIND, bugzillaTask.getRepositoryUrl());
			BugzillaTaskEditorInput input = new BugzillaTaskEditorInput(repository, bugzillaTask, true);
			return input;
		}
		return null;
	}

	@Override
	public String getTitle() {
		return TITLE;
	}

	@Override
	public boolean canCreateEditorFor(ITask task) {
		return task instanceof BugzillaTask;
	}

	@Override
	public boolean providesOutline() {
		return true;
	}

	@Override
	public boolean canCreateEditorFor(IEditorInput input) {
		if (input instanceof RepositoryTaskEditorInput) {
			return BugzillaCorePlugin.CONNECTOR_KIND.equals(((RepositoryTaskEditorInput) input).getRepository()
					.getConnectorKind());
		}
		return false;
	}
}
