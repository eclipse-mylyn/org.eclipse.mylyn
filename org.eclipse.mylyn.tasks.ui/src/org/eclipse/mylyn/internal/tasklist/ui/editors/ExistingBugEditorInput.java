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
package org.eclipse.mylar.internal.tasklist.ui.editors;

import java.io.IOException;
import java.net.Proxy;
import java.security.GeneralSecurityException;

import org.eclipse.mylar.internal.tasklist.OfflineTaskManager;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * The <code>IEditorInput</code> implementation for
 * <code>ExistingBugEditor</code>.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public class ExistingBugEditorInput extends AbstractBugEditorInput {

	private TaskRepository repository;

	protected int bugId;

	protected AbstractRepositoryTask repositoryTask = null;

	protected RepositoryTaskData repositoryTaskData;

	// Called for new bug reports
	public ExistingBugEditorInput(TaskRepository repository, RepositoryTaskData bug) {
		this.repositoryTaskData = bug;
		this.bugId = bug.getId();
		this.repository = repository;
	}

	public ExistingBugEditorInput(TaskRepository repository, int bugId) throws IOException, GeneralSecurityException {
		this.bugId = bugId;
		this.repository = repository;
		this.repositoryTaskData = getOfflineTaskData(repository, proxySettings, bugId);

		String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), bugId);
		ITask task = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handle);
		if (task != null && task instanceof AbstractRepositoryTask) {
			this.repositoryTask = (AbstractRepositoryTask) task;
		}
	}

	public AbstractRepositoryTask getRepositoryTask() {
		return repositoryTask;
	}

	@Override
	public RepositoryTaskData getRepositoryTaskData() {
		return repositoryTaskData;
	}

	// TODO: move?
	private RepositoryTaskData getOfflineTaskData(final TaskRepository repository, Proxy proxySettings, final int id)
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

		// If a suitable offline report was not found, get it from the server
		// if(result == null) {
		// try {
		// result = BugzillaRepositoryUtil.getBug(repository.getUrl(),
		// repository.getUserName(), repository.getPassword(), proxySettings,
		// repository.getCharacterEncoding(), id);
		// } catch (final LoginException e) {
		// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
		// public void run() {
		// MessageDialog.openError(Display.getDefault().getActiveShell(),
		// "Report Download Failed",
		// "Ensure proper repository configuration of " + repository.getUrl() +
		// " in "
		// + TaskRepositoriesView.NAME + ".");
		// }
		// });
		// } catch (final UnrecognizedBugzillaError e) {
		// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
		// public void run() {
		// WebBrowserDialog.openAcceptAgreement(null, "Report Download Failed",
		// "Unrecognized response from "
		// + repository.getUrl(), e.getMessage());
		// }
		// });
		// } catch (final Exception e) {
		// if (PlatformUI.getWorkbench() != null &&
		// !PlatformUI.getWorkbench().isClosing()) {
		// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
		// public void run() {
		// if (e instanceof FileNotFoundException) {
		// MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
		// "Report Download Failed",
		// "Resource not found: " + e.getMessage());
		//							
		// } else {
		// MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
		// "Report Download Failed",
		// "Report "+id+" did not download correctly from " +
		// repository.getUrl());
		//							
		// }
		// }
		// });
		// }
		// }
		// }
		return result;
	}

	public String getName() {
		return repositoryTaskData.getLabel();
	}

	/**
	 * @return The id of the bug for this editor input.
	 */
	public int getBugId() {
		return bugId;
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
