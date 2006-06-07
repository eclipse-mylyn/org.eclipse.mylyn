/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.ui.editor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Proxy;
import java.security.GeneralSecurityException;

import javax.security.auth.login.LoginException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.core.AbstractReportFactory.UnrecognizedBugzillaError;
import org.eclipse.mylar.internal.bugzilla.ui.WebBrowserDialog;
import org.eclipse.mylar.internal.tasklist.OfflineTaskManager;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * The <code>IEditorInput</code> implementation for
 * <code>ExistingBugEditor</code>.
 * 
 * @author Mik Kersten (some hardening of initial prototype)
 */
public class ExistingBugEditorInput extends AbstractBugEditorInput {

	private TaskRepository repository;

	protected int bugId;

	protected RepositoryTaskData bug;

	public ExistingBugEditorInput(TaskRepository repository, RepositoryTaskData bug) {
		this.bug = bug;
		this.bugId = bug.getId();
		this.repository = repository;
	}

	/**
	 * Creates a new <code>ExistingBugEditorInput</code>. An exception is
	 * thrown if the bug could not be obtained from the server.
	 * 
	 * @param bugId
	 *            The id of the bug for this editor input.
	 * @throws IOException
	 * @throws GeneralSecurityException 
	 */
	public ExistingBugEditorInput(TaskRepository repository, int bugId) throws IOException, GeneralSecurityException {
		this.bugId = bugId;
		this.repository = repository;
		// get the bug from the server if it exists
		bug = BugzillaRepositoryUtil.getBug(repository.getUrl(), repository.getUserName(), repository.getPassword(), proxySettings, repository.getCharacterEncoding(), bugId);
//		repository = MylarTaskListPlugin.getRepositoryManager().getRepository(BugzillaPlugin.REPOSITORY_KIND,
//				repositoryUrl);
	}

	public ExistingBugEditorInput(TaskRepository repository, int bugId, boolean offline) throws IOException, GeneralSecurityException {
		this.bugId = bugId;
		this.repository = repository;
		if (!offline) {
			try {
				bug = BugzillaRepositoryUtil.getBug(repository.getUrl(), repository.getUserName(), repository.getPassword(), proxySettings, repository.getCharacterEncoding(), bugId);
			} catch (IOException e) {
				bug = getCurrentBug(repository, proxySettings, bugId);
				// IWorkbench workbench = PlatformUI.getWorkbench();
				// workbench.getDisplay().asyncExec(new Runnable() {
				// public void run() {
				// MessageDialog.openInformation(
				// Display.getDefault().getActiveShell(),
				// "Mylar Bugzilla Client",
				// "Unable to download bug report, using offline copy.");
				//
				// }
				// });
			}
		} else {			
			bug = getCurrentBug(repository, proxySettings, bugId);
		}
	}

	// TODO: move?
	private RepositoryTaskData getCurrentBug(final TaskRepository repository, Proxy proxySettings, final int id)
			throws IOException, GeneralSecurityException {
		RepositoryTaskData result = null;
		// Look among the offline reports for a bug with the given id.
		OfflineTaskManager reportsFile = MylarTaskListPlugin.getDefault().getOfflineReportsFile();
		if (reportsFile != null) {
			int offlineId = reportsFile.find(repository.getUrl(), id);
			// If an offline bug was found, return it if possible.
			if (offlineId != -1) {
				RepositoryTaskData bug = reportsFile.elements().get(offlineId);
				if (bug instanceof RepositoryTaskData) {
					result = (RepositoryTaskData) bug;
				}
			}
		} 
		// TODO: Use downloadTaskData on repositoryConnector?
		// If a suitable offline report was not found, get it from the server
		if(result == null) {
		try {
			result = BugzillaRepositoryUtil.getBug(repository.getUrl(), repository.getUserName(), repository.getPassword(), proxySettings, repository.getCharacterEncoding(), id);
		} catch (final LoginException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Report Download Failed",
							"Ensure proper repository configuration of " + repository.getUrl() + " in "
									+ TaskRepositoriesView.NAME + ".");
				}
			});
		} catch (final UnrecognizedBugzillaError e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					WebBrowserDialog.openAcceptAgreement(null, "Report Download Failed", "Unrecognized response from "
							+ repository.getUrl(), e.getMessage());
				}
			});
		} catch (final Exception e) {
			if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (e instanceof FileNotFoundException) {
							MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Report Download Failed",
									"Resource not found: " + e.getMessage());
							
						} else {
							MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Report Download Failed",
									"Report "+id+" did not download correctly from " + repository.getUrl());
							
						}
					}
				});
			}
		}
		} 
		return result;
	}

	public String getName() {
		return bug.getLabel();
	}

	/**
	 * @return The id of the bug for this editor input.
	 */
	public int getBugId() {
		return bugId;
	}

	@Override
	public RepositoryTaskData getBug() {
		return bug;
	}

	/**
	 * @return <code>true</code> if the argument is a bug report editor input
	 *         on the same bug id.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof ExistingBugEditorInput) {
			ExistingBugEditorInput input = (ExistingBugEditorInput) o;
			return getBugId() == input.getBugId();
		}
		return false;
	}

	public TaskRepository getRepository() {
		return repository;
	}

}
