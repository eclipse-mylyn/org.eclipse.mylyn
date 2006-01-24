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
package org.eclipse.mylar.bugzilla.ui;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.ui.editor.AbstractBugEditor;
import org.eclipse.mylar.bugzilla.ui.editor.ExistingBugEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;

/**
 * Action performed when the bugs are supposed to be displayed in the editor
 * window from the favorites list
 */
public class ViewBugzillaAction extends UIJob {

	/** List of bugs to be displayed */
	private List<BugzillaOpenStructure> bugs;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The job name
	 * @param bugs
	 *            List of bugs to be displayed
	 */
	public ViewBugzillaAction(String name, List<BugzillaOpenStructure> bugs) {
		super(name);
		this.bugs = bugs;
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		IWorkbenchPage page = BugzillaPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

		// go through each bug and get its id
		for (Iterator<BugzillaOpenStructure> it = bugs.iterator(); it.hasNext();) {
			BugzillaOpenStructure bos = it.next();
			Integer bugId = bos.getBugId();
			Integer commentNumber = bos.getCommentNumber();

			try {
				// try to open a new editor on the bug
				ExistingBugEditorInput editorInput = new ExistingBugEditorInput(bos.getServer(), bugId.intValue());

				// if the bug could not be found, then tell the user that the
				// server settings are wrong
				if (editorInput.getBug() == null) {
					MessageDialog.openError(null, "Server Setting Error", "Incorrect server set for the bug.");
				} else {
					AbstractBugEditor abe = (AbstractBugEditor) page.openEditor(editorInput,
							IBugzillaConstants.EXISTING_BUG_EDITOR_ID);
					if (commentNumber == 0) {
						abe.selectDescription();
					} else if (commentNumber == 1) {
						abe.select(commentNumber);
					} else {
						abe.select(commentNumber - 1);
					}
				}
			} catch (LoginException e) {
				MessageDialog
						.openError(
								null,
								"Login Error",
								"Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease check your settings in the bugzilla preferences. ");
				BugzillaPlugin.log(e);
			} catch (PartInitException e) {
				BugzillaPlugin.log(e);
			} catch (IOException e) {
				BugzillaPlugin.getDefault().logAndShowExceptionDetailsDialog(e,
						"occurred while opening the bug report.", "Bugzilla Error");
			}
		}
		return new Status(IStatus.OK, IBugzillaConstants.PLUGIN_ID, IStatus.OK, "", null);
	}
}
