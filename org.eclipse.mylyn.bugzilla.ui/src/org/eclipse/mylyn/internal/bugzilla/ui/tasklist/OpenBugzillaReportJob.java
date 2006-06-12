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

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;
import org.eclipse.mylar.internal.tasklist.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylar.internal.tasklist.ui.editors.ExistingBugEditorInput;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class OpenBugzillaReportJob extends Job { 

	private int id;

	private String serverUrl;

	private IWorkbenchPage page;

	public OpenBugzillaReportJob(String serverUrl, int id, IWorkbenchPage page) {
		 super("Opening Bugzilla report: " + id);
		this.id = id;
		this.serverUrl = serverUrl;
		this.page = page;
	}

	public IStatus run(IProgressMonitor monitor) {

		// @Override
		// public IStatus runInUIThread(IProgressMonitor monitor) {
		try {
			monitor.beginTask("Opening Bugzilla Report", 10);
			Integer bugId = id;

			try {				
				// try to open a new editor on the bug
				TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
						BugzillaPlugin.REPOSITORY_KIND, serverUrl);			
				
				RepositoryTaskData data = BugzillaRepositoryUtil.getBug(repository.getUrl(), repository.getUserName(), repository.getPassword(), MylarTaskListPlugin.getDefault().getProxySettings(), repository.getCharacterEncoding(), bugId.intValue());
				final ExistingBugEditorInput editorInput = new ExistingBugEditorInput(repository, data);
				// final ExistingBugEditorInput editorInput = new
				// ExistingBugEditorInput(repository, bugId.intValue());

				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					public void run() {
						if (editorInput.getRepositoryTaskData() == null) {
							MessageDialog.openError(null, "Server Setting Error", "Incorrect server set for the bug.");
						} else {
							try {
								AbstractRepositoryTaskEditor abe = (AbstractRepositoryTaskEditor) page.openEditor(editorInput,
										BugzillaUiPlugin.EXISTING_BUG_EDITOR_ID);
								abe.selectDescription();
								// if (commentNumber == 0) {
								// abe.selectDescription();
								// } else if (commentNumber == 1) {
								// abe.select(commentNumber);
								// } else {
								// abe.select(commentNumber - 1);
								// }
							} catch (PartInitException e) {
								BugzillaPlugin.log(e);
							}
						}
					}
				});
			} catch (LoginException e) {
				MessageDialog
						.openError(
								null,
								"Login Error",
								"Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease check your settings in the bugzilla preferences. ");
				BugzillaPlugin.log(e);

			} catch (IOException e) {
				MylarStatusHandler.fail(e, "Error opening Bugzilla report", true);
			}
			monitor.done();
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Unable to open Bug report: " + id, true);
		}
		 return new Status(IStatus.OK, BugzillaUiPlugin.PLUGIN_ID, IStatus.OK, "", null);
	}
}
