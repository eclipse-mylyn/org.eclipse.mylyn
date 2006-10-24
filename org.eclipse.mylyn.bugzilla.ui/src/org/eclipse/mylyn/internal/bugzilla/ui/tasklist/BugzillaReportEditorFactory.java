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

package org.eclipse.mylar.internal.bugzilla.ui.tasklist;

import javax.security.auth.login.LoginException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylar.internal.bugzilla.ui.editor.BugzillaTaskEditor;
import org.eclipse.mylar.internal.bugzilla.ui.editor.NewBugzillaTaskEditor;
import org.eclipse.mylar.internal.tasks.ui.ITaskEditorFactory;
import org.eclipse.mylar.internal.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylar.internal.tasks.ui.editors.ExistingBugEditorInput;
import org.eclipse.mylar.internal.tasks.ui.editors.MylarTaskEditor;
import org.eclipse.mylar.internal.tasks.ui.editors.NewBugEditorInput;
import org.eclipse.mylar.internal.tasks.ui.editors.TaskEditorInput;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaReportEditorFactory implements ITaskEditorFactory {

	private static final String REPOSITORY_INFO = "Bugzilla";

	public void notifyEditorActivationChange(IEditorPart editor) {
		// ignore
	}

	public EditorPart createEditor(MylarTaskEditor parentEditor, IEditorInput editorInput) {
		AbstractRepositoryTaskEditor editor = null;
		if (editorInput instanceof ExistingBugEditorInput || editorInput instanceof TaskEditorInput) {
			editor = new BugzillaTaskEditor(parentEditor);
		} else if (editorInput instanceof NewBugEditorInput) {
			editor = new NewBugzillaTaskEditor(parentEditor);
		} 
		return editor;
	}

	public IEditorInput createEditorInput(ITask task) {
		if (task instanceof BugzillaTask) {
			BugzillaTask bugzillaTask = (BugzillaTask) task;
			final TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					BugzillaCorePlugin.REPOSITORY_KIND, bugzillaTask.getRepositoryUrl());
			try {
				BugzillaTaskEditorInput input = new BugzillaTaskEditorInput(repository, bugzillaTask, true);
				// input.setOfflineBug(bugzillaTask.getTaskData());
				return input;
			} catch (final LoginException e) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Report Download Failed",
								"Ensure proper repository configuration in " + TaskRepositoriesView.NAME + ".\n"
										+ "Repository set to: " + repository.getUrl() + ", username: "
										+ repository.getUserName());
					}
				});
			} catch (Exception e) {
				MylarStatusHandler.fail(e, "Could not create Bugzilla editor input", true);
			}
		}
		return null;
	}

	public String getTitle() {
		return REPOSITORY_INFO;
	}

	public boolean canCreateEditorFor(ITask task) {
		return task instanceof BugzillaTask;
	}

	public boolean providesOutline() {
		return true;
	}

	public boolean canCreateEditorFor(IEditorInput input) {
		if (input instanceof ExistingBugEditorInput) {
			return BugzillaCorePlugin.REPOSITORY_KIND
					.equals(((ExistingBugEditorInput) input).getRepository().getKind());
		} else if (input instanceof NewBugEditorInput) {
			return BugzillaCorePlugin.REPOSITORY_KIND.equals(((NewBugEditorInput) input).getRepository().getKind());
		}
		return false;
	}
}
