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

import java.io.IOException;
import java.net.Proxy;
import java.security.GeneralSecurityException;

import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.OfflineReportsFile;
import org.eclipse.mylar.provisional.bugzilla.core.BugzillaReport;
import org.eclipse.mylar.provisional.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * The <code>IEditorInput</code> implementation for
 * <code>ExistingBugEditor</code>.
 * 
 * @author Mik Kersten (some hardening of initial prototype)
 */
public class ExistingBugEditorInput extends AbstractBugEditorInput {

	private TaskRepository repository;

	protected int bugId;

	protected BugzillaReport bug;

	public ExistingBugEditorInput(TaskRepository repository, BugzillaReport bug) {
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

	// TODO: move
	private BugzillaReport getCurrentBug(TaskRepository repository, Proxy proxySettings, int id)
			throws IOException, GeneralSecurityException {
		// Look among the offline reports for a bug with the given id.
		OfflineReportsFile reportsFile = BugzillaUiPlugin.getDefault().getOfflineReportsFile();
		if (reportsFile != null) {
			int offlineId = reportsFile.find(repository.getUrl(), id);
	
			// If an offline bug was found, return it if possible.
			if (offlineId != -1) {
				IBugzillaBug bug = reportsFile.elements().get(offlineId);
				if (bug instanceof BugzillaReport) {
					return (BugzillaReport) bug;
				}
			}
		} 
		// If a suitable offline report was not found, get it from the server
		return BugzillaRepositoryUtil.getBug(repository.getUrl(), repository.getUserName(), repository.getPassword(), proxySettings, repository.getCharacterEncoding(), id);
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
	public BugzillaReport getBug() {
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
