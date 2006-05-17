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
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.editor.ExistingBugEditor;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.ITaskEditorFactory;
import org.eclipse.mylar.internal.tasklist.ui.editors.MylarTaskEditor;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

/**
 * @author Mik Kersten
 */
public class BugzillaReportEditorFactory implements ITaskEditorFactory {

	private static final String REPOSITORY_INFO = "Bugzilla";

	public void notifyEditorActivationChange(IEditorPart editor) {
		// ignore
	}

	public EditorPart createEditor(MylarTaskEditor parentEditor) {
		ExistingBugEditor editor = new ExistingBugEditor();
		editor.setParentEditor(parentEditor);
		return editor;
	}

	public IEditorInput createEditorInput(ITask task) {
		if (task instanceof BugzillaTask) {
			BugzillaTask bugzillaTask = (BugzillaTask) task;
			try {
				TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(BugzillaPlugin.REPOSITORY_KIND, bugzillaTask.getRepositoryUrl());
				BugzillaTaskEditorInput input = new BugzillaTaskEditorInput(repository, bugzillaTask, true);
				input.setOfflineBug(bugzillaTask.getBugReport()); 
				return input;	
			} catch (final LoginException e) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Report Download Failed",
								"Ensure proper repository configuration in " + TaskRepositoriesView.NAME + ".");
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
}
