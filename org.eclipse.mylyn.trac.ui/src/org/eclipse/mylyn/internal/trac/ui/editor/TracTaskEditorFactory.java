/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.internal.trac.ui.editor;

import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.ITaskEditorFactory;
import org.eclipse.mylar.internal.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.mylar.internal.tasks.ui.editors.MylarTaskEditor;
import org.eclipse.mylar.internal.tasks.ui.editors.NewTaskEditorInput;
import org.eclipse.mylar.internal.tasks.ui.editors.TaskEditorInput;
import org.eclipse.mylar.internal.trac.core.TracCorePlugin;
import org.eclipse.mylar.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylar.internal.trac.core.TracTask;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * @author Steffen Pingel
 */
public class TracTaskEditorFactory implements ITaskEditorFactory {

	public boolean canCreateEditorFor(ITask task) {
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
			return existingInput.getRepositoryTaskData() != null
					&& TracCorePlugin.REPOSITORY_KIND.equals(existingInput.getRepository().getKind());
		} else if (input instanceof NewTaskEditorInput) {
			NewTaskEditorInput newInput = (NewTaskEditorInput) input;
			return newInput.getRepositoryTaskData() != null
					&& TracCorePlugin.REPOSITORY_KIND.equals(newInput.getRepository().getKind());
		}
		return false;
	}

	public IEditorPart createEditor(MylarTaskEditor parentEditor, IEditorInput editorInput) {
		if (editorInput instanceof RepositoryTaskEditorInput  || editorInput instanceof TaskEditorInput) {
			return new TracTaskEditor(parentEditor);
		} else if (editorInput instanceof NewTaskEditorInput) {
			return new NewTracTaskEditor(parentEditor);
		} 
		return null;
	}

	public IEditorInput createEditorInput(ITask task) {
		TracTask tracTask = (TracTask) task;
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(TracCorePlugin.REPOSITORY_KIND,
				tracTask.getRepositoryUrl());
		try {
			return new RepositoryTaskEditorInput(repository, tracTask.getTaskData(), AbstractRepositoryTask.getTaskId(tracTask.getHandleIdentifier()));
//			return new RepositoryTaskEditorInput(repository, tracTask);
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Could not create Trac editor input", true);
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
