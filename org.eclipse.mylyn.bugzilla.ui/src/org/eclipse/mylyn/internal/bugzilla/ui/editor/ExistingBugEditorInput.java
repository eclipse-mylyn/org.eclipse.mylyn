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

import javax.security.auth.login.LoginException;

import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.TaskRepository;

/**
 * The <code>IEditorInput</code> implementation for
 * <code>ExistingBugEditor</code>.
 * 
 * @author Mik Kersten (some hardening of initial prototype)
 */
public class ExistingBugEditorInput extends AbstractBugEditorInput {

	private TaskRepository repository;

	protected int bugId;

	protected BugReport bug;

	/**
	 * Creates a new <code>ExistingBugEditorInput</code>.
	 * 
	 * @param bug
	 *            The bug for this editor input.
	 */
	public ExistingBugEditorInput(BugReport bug) {
		this.bug = bug;
		this.bugId = bug.getId();
		repository = MylarTaskListPlugin.getRepositoryManager().getRepository(BugzillaPlugin.REPOSITORY_KIND,
				bug.getRepository());
	}

	/**
	 * Creates a new <code>ExistingBugEditorInput</code>. An exception is
	 * thrown if the bug could not be obtained from the server.
	 * 
	 * @param bugId
	 *            The id of the bug for this editor input.
	 * @throws LoginException
	 * @throws IOException
	 */
	public ExistingBugEditorInput(String repositoryUrl, int bugId) throws LoginException, IOException {
		this.bugId = bugId;
		// get the bug from the server if it exists
		bug = BugzillaRepositoryUtil.getBug(repositoryUrl, bugId);
		repository = MylarTaskListPlugin.getRepositoryManager().getRepository(BugzillaPlugin.REPOSITORY_KIND,
				repositoryUrl);
	}

	public ExistingBugEditorInput(String repositoryUrl, int bugId, boolean offline) throws LoginException, IOException {
		this.bugId = bugId;
		repository = MylarTaskListPlugin.getRepositoryManager().getRepository(BugzillaPlugin.REPOSITORY_KIND,
				repositoryUrl);
		if (!offline) {
			try {
				bug = BugzillaRepositoryUtil.getBug(repositoryUrl, bugId);
			} catch (IOException e) {
				bug = BugzillaRepositoryUtil.getCurrentBug(repositoryUrl, bugId);
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
			bug = BugzillaRepositoryUtil.getCurrentBug(repositoryUrl, bugId);
		}
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
	public BugReport getBug() {
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
