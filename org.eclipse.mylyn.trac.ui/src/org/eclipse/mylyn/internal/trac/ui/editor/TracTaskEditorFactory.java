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
import org.eclipse.mylar.internal.tasks.ui.editors.ExistingBugEditorInput;
import org.eclipse.mylar.internal.tasks.ui.editors.MylarTaskEditor;
import org.eclipse.mylar.internal.trac.core.TracCorePlugin;
import org.eclipse.mylar.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylar.internal.trac.core.TracTask;
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
			TracRepositoryConnector connector = (TracRepositoryConnector) TasksUiPlugin.getRepositoryManager()
					.getRepositoryConnector(TracCorePlugin.REPOSITORY_KIND);
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					TracCorePlugin.REPOSITORY_KIND, ((TracTask) task).getRepositoryUrl());
			return connector.hasRichEditor(repository);
		}
		return task instanceof TracTask;
	}

	public boolean canCreateEditorFor(IEditorInput input) {
		if (input instanceof ExistingBugEditorInput) {
			ExistingBugEditorInput existingInput = (ExistingBugEditorInput) input;
			return existingInput.getRepositoryTaskData() != null
					&& TracCorePlugin.REPOSITORY_KIND.equals(existingInput.getRepository().getKind());
		}
		return false;
	}

	public IEditorPart createEditor(MylarTaskEditor parentEditor) {
		return new TracTaskEditor(parentEditor);
	}

	public IEditorInput createEditorInput(ITask task) {
		TracTask tracTask = (TracTask) task;
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(TracCorePlugin.REPOSITORY_KIND,
				tracTask.getRepositoryUrl());
		try {
			return new TracTaskEditorInput(repository, tracTask);
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Could not create Trac editor input", true);
		}
		return null;
	}

	public String getTitle() {
		return "Trac";
	}

	public void notifyEditorActivationChange(IEditorPart editor) {
		// TODO Auto-generated method stub

	}

	public boolean providesOutline() {
		return true;
	}

}
