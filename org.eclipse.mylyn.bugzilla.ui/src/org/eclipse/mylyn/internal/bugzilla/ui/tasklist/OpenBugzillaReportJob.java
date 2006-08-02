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
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaServerFacade;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.tasks.ui.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasks.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasks.ui.editors.AbstractBugEditorInput;
import org.eclipse.mylar.internal.tasks.ui.editors.ExistingBugEditorInput;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
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
				TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
						BugzillaPlugin.REPOSITORY_KIND, serverUrl);

				if (repository == null) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(null, "Repository Not Found",
									"Could not find repository configuration for " + serverUrl
											+ ". \nPlease set up repository via " + TaskRepositoriesView.NAME + ".");
							String bugUrl = BugzillaServerFacade.getBugUrlWithoutLogin(serverUrl, id);
							TaskUiUtil.openUrl(bugUrl);
						}

					});
					return Status.OK_STATUS;
				}

				RepositoryTaskData data = BugzillaServerFacade.getBug(repository.getUrl(), repository.getUserName(),
						repository.getPassword(), TasksUiPlugin.getDefault().getProxySettings(), repository
								.getCharacterEncoding(), bugId.intValue());
				final AbstractBugEditorInput editorInput = new ExistingBugEditorInput(repository, data);

				// final ExistingBugEditorInput editorInput = new
				// ExistingBugEditorInput(repository, bugId.intValue());

				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					public void run() {
						if (editorInput.getRepositoryTaskData() == null) {
							MessageDialog.openError(null, "Server Setting Error", "Incorrect server set for the bug.");
						} else {
							try {
								page.openEditor(editorInput, TaskListPreferenceConstants.TASK_EDITOR_ID);

								// AbstractRepositoryTaskEditor abe =
								// (AbstractRepositoryTaskEditor)
								// page.openEditor(editorInput,
								// TaskListPreferenceConstants.TASK_EDITOR_ID);//BugzillaUiPlugin.EXISTING_BUG_EDITOR_ID
								// abe.selectDescription();

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
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					public void run() {
						MessageDialog
								.openError(
										null,
										"Login Error",
										"Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease check your settings in the bugzilla preferences. ");
					}
				});
			} catch (IOException e) {
				MylarStatusHandler.fail(e, "Error opening Bugzilla report", true);
			} finally {
				monitor.done();
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Unable to open Bug report: " + id, true);
		}
		return new Status(IStatus.OK, BugzillaUiPlugin.PLUGIN_ID, IStatus.OK, "", null);
	}
}
