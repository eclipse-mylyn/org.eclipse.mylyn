/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.TracTask;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.BrowserFormPage;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * @author Steffen Pingel
 */
public class TracTaskEditorFactory extends AbstractTaskEditorFactory {

	private static final String TITLE = "Browser";

	@Override
	public boolean canCreateEditorFor(AbstractTask task) {
		return (task instanceof TracTask);
	}

	@Override
	public boolean canCreateEditorFor(IEditorInput input) {
		if (input instanceof RepositoryTaskEditorInput) {
			RepositoryTaskEditorInput taskInput = (RepositoryTaskEditorInput) input;
			return taskInput.getTaskData() != null
					&& TracCorePlugin.REPOSITORY_KIND.equals(taskInput.getRepository().getConnectorKind());
		} else if (input instanceof TaskEditorInput) {
			TaskEditorInput taskInput = (TaskEditorInput) input;
			return taskInput.getTask() instanceof TracTask;
		}

		return false;
	}

	@Override
	public IEditorPart createEditor(TaskEditor parentEditor, IEditorInput editorInput) {
		if (editorInput instanceof RepositoryTaskEditorInput) {
			RepositoryTaskEditorInput taskInput = (RepositoryTaskEditorInput) editorInput;
			if (taskInput.getTaskData().isNew()) {
				return new NewTracTaskEditor(parentEditor);
			} else {
				return new TracTaskEditor(parentEditor);
			}
		} else if (editorInput instanceof TaskEditorInput) {
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					TracCorePlugin.REPOSITORY_KIND, ((TaskEditorInput) editorInput).getTask().getRepositoryUrl());
			if (TracRepositoryConnector.hasRichEditor(repository)) {
				// the editor is actually initialized with a RepositoryTaskEditorInput, see bug 193430
				return new TracTaskEditor(parentEditor);
			} else {
				return new BrowserFormPage(parentEditor, TITLE);
			}
		}
		return null;
	}

	@Override
	public IEditorInput createEditorInput(AbstractTask task) {
		TracTask tracTask = (TracTask) task;
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(TracCorePlugin.REPOSITORY_KIND,
				tracTask.getRepositoryUrl());
		if (TracRepositoryConnector.hasRichEditor(repository)) {
			return new RepositoryTaskEditorInput(repository, tracTask.getTaskId(), tracTask.getUrl());
		} else {
			return new TaskEditorInput(repository, task) {
				@Override
				public ImageDescriptor getImageDescriptor() {
					return TasksUiImages.BROWSER_SMALL;
				}
			};
		}
	}

	@Override
	public String getTitle() {
		return "Trac";
	}

	@Override
	public boolean providesOutline() {
		return true;
	}
}
