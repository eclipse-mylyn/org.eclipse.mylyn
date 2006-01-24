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

package org.eclipse.mylar.internal.bugzilla.ui.actions;

import org.eclipse.mylar.internal.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.internal.bugzilla.ui.OfflineView;

/**
 * Action of removing a bookmark
 */
public class DeleteOfflineReportAction extends AbstractOfflineReportsAction {
	/** The instance of the offlineReports view */
	private OfflineView view;

	/** True if all of the bookmarks are to be deleted */
	private boolean deleteAll;

	/**
	 * Constructor
	 * 
	 * @param offlineReportsView
	 *            The offlineReports view being used
	 * @param deleteAllOfflineReports
	 *            <code>true</code> if all of the offlineReports should be
	 *            deleted, else <code>false</code>
	 */
	public DeleteOfflineReportAction(OfflineView offlineReportsView, boolean deleteAllOfflineReports) {
		deleteAll = deleteAllOfflineReports;

		// set the appropriate icons and tool tips for the action depending
		// on whether it will delete all items or not
		if (deleteAll) {
			setToolTipText("Remove All Offline Reports");
			setText("Remove all");
			setImageDescriptor(BugzillaImages.REMOVE_ALL);
		} else {
			setToolTipText("Remove Selected Offline Reports");
			setText("Remove");
			setImageDescriptor(BugzillaImages.REMOVE);
		}

		view = offlineReportsView;
	}

	/**
	 * Delete the appropriate offline reports
	 * 
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
	public void run() {
		OfflineView.checkWindow();

		// call the appropriate delete function
		if (deleteAll)
			view.deleteAllOfflineReports();
		else
			view.deleteSelectedOfflineReports();
		OfflineView.updateActionEnablement();
	}
}
