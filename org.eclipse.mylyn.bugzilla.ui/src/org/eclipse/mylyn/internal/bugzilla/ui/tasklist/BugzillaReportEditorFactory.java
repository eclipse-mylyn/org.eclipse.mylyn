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

import org.eclipse.mylar.internal.bugzilla.ui.editor.ExistingBugEditor;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.ITaskEditorFactory;
import org.eclipse.mylar.internal.tasklist.ui.editors.MylarTaskEditor;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
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

//			boolean offline = bugzillaTask.getSyncState() == RepositoryTaskSyncState.OUTGOING
//					|| bugzillaTask.getSyncState() == RepositoryTaskSyncState.CONFLICT;

			try {
				BugzillaTaskEditorInput input = new BugzillaTaskEditorInput(bugzillaTask, true);
				input.setOfflineBug(bugzillaTask.getBugReport()); 
				return input;
//				GetBugzillaReportJob getBugzillaReportJob = new GetBugzillaReportJob(bugzillaTask);
//				getBugzillaReportJob.schedule();
//				return getBugzillaReportJob.getEditorInput();
				// BugzillaTaskEditorInput input = new
				// BugzillaTaskEditorInput(bugTask, offline);
				// try {
				//				
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
