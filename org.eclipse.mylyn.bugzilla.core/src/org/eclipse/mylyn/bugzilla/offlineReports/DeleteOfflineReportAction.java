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
package org.eclipse.mylar.bugzilla.offlineReports;

import org.eclipse.mylar.bugzilla.ui.OfflineView;

/**
 * Action of removing a bookmark
 */
public class DeleteOfflineReportAction extends AbstractOfflineReportsAction 
{
	/** The instance of the offlineReports view */
	private OfflineView view;
	
	/** True if all of the bookmarks are to be deleted */
	private boolean deleteAll;
	
	/**
	 * Constructor
	 * @param offlineReportsView The offlineReports view being used
	 * @param deleteAllOfflineReports <code>true</code> if all of the offlineReports should be deleted, else <code>false</code>
	 */
	public DeleteOfflineReportAction(OfflineView offlineReportsView, boolean deleteAllOfflineReports) 
	{
		deleteAll = deleteAllOfflineReports;
		
		// set the appropriate icons and tool tips for the action depending
		// on whether it will delete all items or not
		if (deleteAll) 
		{
			setToolTipText("Remove all offline reports");
			setText("Remove all");
			setIcon("Icons/remove-all.gif");
		}
		else 
		{
			setToolTipText( "Remove selected offline reports" );
			setText( "Remove" );
			setIcon( "Icons/remove.gif" );
		}
		
		view = offlineReportsView;
	}
	
	/**
	 * Delete the appropriate offline reports 
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
	public void run() 
	{
		OfflineView.checkWindow();
		
		// call the appropriate delete function
		if (deleteAll)
			view.deleteAllOfflineReports();
		else
			view.deleteSelectedOfflineReports();
		OfflineView.updateActionEnablement();
	}
}
