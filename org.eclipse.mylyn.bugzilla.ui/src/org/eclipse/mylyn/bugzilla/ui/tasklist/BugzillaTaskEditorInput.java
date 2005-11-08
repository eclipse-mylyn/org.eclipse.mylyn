/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
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
package org.eclipse.mylar.bugzilla.ui.tasklist;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.ui.editor.ExistingBugEditorInput;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask.BugReportSyncState;
import org.eclipse.ui.IPersistableElement;


/**
 * @author Eric Booth
 */
public class BugzillaTaskEditorInput extends ExistingBugEditorInput {

	private String bugTitle;

	private BugReport offlineBug;
	
	private BugzillaTask bugTask;
	
	private boolean offline;

	public BugzillaTaskEditorInput(BugzillaTask bugTask, boolean offline) throws LoginException, IOException {
        super(BugzillaTask.getBugId(bugTask.getHandle()), offline);
		this.bugTask = bugTask;
		offlineBug = bugTask.getBugReport();
		bugId = BugzillaTask.getBugId(bugTask.getHandle());
		bugTitle = "";
		this.offline = offline;
	}

	protected void setBugTitle(String str) {
		//		03-20-03 Allows editor to store title (once it is known)
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
		return bugTask.getDescription(true); 
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
	 * @see BugzillaRepository
	 * @see BugReport
	 */
//	public BugReport getServerBug() {
//		return serverBug;
//	}
	
	/**
	 * Returns the offline bug for this input's Bugzilla task
	 */
	public BugReport getOfflineBug() {
		if(offline || bugTask.getSyncState() == BugReportSyncState.OUTGOING || bugTask.getSyncState() == BugReportSyncState.CONFLICT)
			return offlineBug;
		else
			return super.getBug();
	}

	public void setOfflineBug(BugReport offlineBug){
		this.offlineBug = offlineBug;
	}
	
	/**
	 * Gets the bug page input stream
	 */
//	public InputStream getInputStream() throws IOException {
//		try {
//			return url.openStream();
//		}
//		catch (Exception e) {
//			throw new IOException(e.getMessage());
//		}
//
//	}

	/**
	 * Returns true if the argument is a bug report editor input on the same bug id.
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
