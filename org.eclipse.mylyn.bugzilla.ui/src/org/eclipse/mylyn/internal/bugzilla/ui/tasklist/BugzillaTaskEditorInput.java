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
/*
 * Created on 1-Feb-2005
 */
package org.eclipse.mylar.internal.bugzilla.ui.tasklist;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.internal.bugzilla.ui.editor.ExistingBugEditorInput;
import org.eclipse.mylar.provisional.bugzilla.core.BugzillaReport;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Eric Booth
 * @author Mik Kersten
 */
public class BugzillaTaskEditorInput extends ExistingBugEditorInput {

	private String bugTitle;

	private BugzillaReport offlineBug;

	private BugzillaTask bugTask;

	private boolean offline;

	public BugzillaTaskEditorInput(TaskRepository repository, BugzillaTask bugTask, boolean offline) throws IOException, GeneralSecurityException {
		super(repository, AbstractRepositoryTask.getTaskIdAsInt(bugTask.getHandleIdentifier()), offline);
		this.bugTask = bugTask;
		offlineBug = bugTask.getBugReport();
		bugId = AbstractRepositoryTask.getTaskIdAsInt(bugTask.getHandleIdentifier());
		bugTitle = "";
		this.offline = offline;
	}

	protected void setBugTitle(String str) {
		// 03-20-03 Allows editor to store title (once it is known)
		bugTitle = str;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return bugTask.getDescription();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return bugTitle;
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public int getBugId() {
		return bugId;
	}

	/**
	 * Returns the online server bug for this input
	 * 
	 * @see BugzillaRepositoryUtil
	 * @see BugReport
	 */
	// public BugReport getServerBug() {
	// return serverBug;
	// }
	/**
	 * Returns the offline bug for this input's Bugzilla task
	 */
	public BugzillaReport getOfflineBug() {
		if (offline || bugTask.getSyncState() == RepositoryTaskSyncState.OUTGOING
				|| bugTask.getSyncState() == RepositoryTaskSyncState.CONFLICT)
			return offlineBug;
		else
			return super.getBug();
	}

	public void setOfflineBug(BugzillaReport offlineBug) {
		this.offlineBug = offlineBug;
	}

	/**
	 * Gets the bug page input stream
	 */
	// public InputStream getInputStream() throws IOException {
	// try {
	// return url.openStream();
	// }
	// catch (Exception e) {
	// throw new IOException(e.getMessage());
	// }
	//
	// }
	/**
	 * Returns true if the argument is a bug report editor input on the same bug
	 * id.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof BugzillaTaskEditorInput) {
			BugzillaTaskEditorInput input = (BugzillaTaskEditorInput) o;
			return getBugId() == input.getBugId();
		}
		return false;
	}

	/**
	 * @return Returns the <code>BugzillaTask</code>
	 */
	public BugzillaTask getBugTask() {
		return bugTask;
	}
}
