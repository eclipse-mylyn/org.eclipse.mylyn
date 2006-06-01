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

package org.eclipse.mylar.internal.tasklist;

import java.util.List;

import org.eclipse.mylar.internal.tasklist.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasklist.ui.editors.MylarTaskEditor;
import org.eclipse.mylar.internal.tasklist.ui.editors.TaskEditorInput;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.AbstractTaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskListChangeListener;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.ui.PlatformUI;

public class RepositoryEditorManager implements ITaskListChangeListener {

	public void containerAdded(AbstractTaskContainer container) {
		// ignore
	}

	public void containerDeleted(AbstractTaskContainer container) {
		// ignore
	}

	public void containerInfoChanged(AbstractTaskContainer container) {
		// ignore
	}

	public void localInfoChanged(ITask task) {
		// ignore
	}

	public void repositoryInfoChanged(ITask task) {

		if (task instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
			if (repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING) {

				List<MylarTaskEditor> editors = TaskUiUtil.getActiveRepositoryTaskEditors();
				for (final MylarTaskEditor editor : editors) {
					final TaskEditorInput input = (TaskEditorInput) editor.getEditorInput();
					if (input.getTask().getHandleIdentifier().equals(repositoryTask.getHandleIdentifier())) {

						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								TaskUiUtil.closeEditorInActivePage(input.getTask());
								TaskUiUtil.refreshAndOpenTaskListElement(input.getTask());
								// If the following is used, incoming status will remain
								// TaskUiUtil.openEditor(input.getTask(),
								// false);
							}
						});

					}
				}

			}
		}

	}

	public void taskAdded(ITask task) {
		// ignore
	}

	public void taskDeleted(ITask task) {
		// ignore
	}

	public void taskMoved(ITask task, AbstractTaskContainer fromContainer, AbstractTaskContainer toContainer) {
		// ignore
	}

}
