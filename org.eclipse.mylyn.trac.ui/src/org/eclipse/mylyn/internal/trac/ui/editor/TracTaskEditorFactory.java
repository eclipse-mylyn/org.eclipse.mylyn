/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.trac.ui.editor;

import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.TracTask;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.ITaskEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * @author Steffen Pingel
 */
public class TracTaskEditorFactory implements ITaskEditorFactory {

	public boolean canCreateEditorFor(AbstractTask task) {
		if (task instanceof TracTask) {
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					TracCorePlugin.REPOSITORY_KIND, ((TracTask) task).getRepositoryUrl());
			return TracRepositoryConnector.hasRichEditor(repository);
		}
		return task instanceof TracTask;
	}

	public boolean canCreateEditorFor(IEditorInput input) {
		if (input instanceof RepositoryTaskEditorInput) {
			RepositoryTaskEditorInput existingInput = (RepositoryTaskEditorInput) input;
			return existingInput.getTaskData() != null
					&& TracCorePlugin.REPOSITORY_KIND.equals(existingInput.getRepository().getKind());
		} 
//		else if (input instanceof NewTaskEditorInput) {
//			NewTaskEditorInput newInput = (NewTaskEditorInput) input;
//			return newInput.getTaskData() != null
//					&& TracCorePlugin.REPOSITORY_KIND.equals(newInput.getRepository().getKind());
//		}
		return false;
	}

	public IEditorPart createEditor(TaskEditor parentEditor, IEditorInput editorInput) {

		if (editorInput instanceof RepositoryTaskEditorInput) {
			RepositoryTaskEditorInput taskInput = (RepositoryTaskEditorInput) editorInput;
			if (taskInput.getTaskData().isNew()) {
				return new NewTracTaskEditor(parentEditor);
			} else {
				return new TracTaskEditor(parentEditor);
			}
		} else if (editorInput instanceof TaskEditorInput) {
			return new TracTaskEditor(parentEditor);
		}
		return null;
	}

	public IEditorInput createEditorInput(AbstractTask task) {
		TracTask tracTask = (TracTask) task;
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(TracCorePlugin.REPOSITORY_KIND,
				tracTask.getRepositoryUrl());
		try {
			return new RepositoryTaskEditorInput(repository, tracTask.getHandleIdentifier(), tracTask.getTaskUrl(), tracTask.getTaskId());
		} catch (Exception e) {
			StatusManager.fail(e, "Could not create Trac editor input", true);
		}
		return null;
	}

	public String getTitle() {
		return "Trac";
	}

	public boolean providesOutline() {
		return true;
	}
}
