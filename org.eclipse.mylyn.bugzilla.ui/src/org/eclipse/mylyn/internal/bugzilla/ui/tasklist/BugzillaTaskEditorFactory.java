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

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.internal.bugzilla.ui.editor.BugzillaTaskEditor;
import org.eclipse.mylyn.internal.bugzilla.ui.editor.NewBugzillaTaskEditor;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskEditorInput;
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

	public IEditorInput createEditorInput(AbstractTask task) {
		if (task instanceof BugzillaTask) {
			BugzillaTask bugzillaTask = (BugzillaTask) task;
			final TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					BugzillaCorePlugin.REPOSITORY_KIND, bugzillaTask.getRepositoryUrl());
			BugzillaTaskEditorInput input = new BugzillaTaskEditorInput(repository, bugzillaTask, true);
			return input;
		}
		return null;
	}

	public String getTitle() {
		return TITLE;
	}

	public boolean canCreateEditorFor(AbstractTask task) {
		return task instanceof BugzillaTask;
	}

	public boolean providesOutline() {
		return true;
	}

	public boolean canCreateEditorFor(IEditorInput input) {
		if (input instanceof RepositoryTaskEditorInput) {
			return BugzillaCorePlugin.REPOSITORY_KIND.equals(((RepositoryTaskEditorInput) input).getRepository()
					.getConnectorKind());
		}
		return false;
	}
}
