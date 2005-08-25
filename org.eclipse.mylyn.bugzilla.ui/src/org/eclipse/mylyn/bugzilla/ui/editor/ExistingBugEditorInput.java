/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.ui.editor;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.BugzillaRepository;


/**
 * The <code>IEditorInput</code> implementation for <code>ExistingBugEditor</code>.
 */
public class ExistingBugEditorInput extends AbstractBugEditorInput
{
	protected int bugId;

	protected BugReport bug;
	
	/**
	 * Creates a new <code>ExistingBugEditorInput</code>.
	 * @param bug The bug for this editor input.
	 */
	public ExistingBugEditorInput(BugReport bug) {
		this.bug = bug;
		this.bugId = bug.getId();
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
	public ExistingBugEditorInput(int bugId) throws LoginException, IOException {
		this.bugId = bugId;
		
		// get the bug from the server if it exists
		bug = BugzillaRepository.getInstance().getBug(bugId);
	}
	
	public ExistingBugEditorInput(int bugId, boolean offline) throws LoginException, IOException {
		this.bugId = bugId;
		
		if(!offline){
			bug = BugzillaRepository.getInstance().getBug(bugId);
		} else {
			bug = BugzillaRepository.getInstance().getCurrentBug(bugId);
		}
	}

	/*
	 * @see IEditorInput#getName()
	 */
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
}
