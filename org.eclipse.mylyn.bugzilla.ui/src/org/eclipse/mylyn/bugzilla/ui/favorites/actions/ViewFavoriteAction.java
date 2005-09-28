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
package org.eclipse.mylar.bugzilla.ui.favorites.actions;

import java.util.List;

import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.BugzillaOpenStructure;
import org.eclipse.mylar.bugzilla.ui.FavoritesView;
import org.eclipse.mylar.bugzilla.ui.ViewBugzillaAction;


/**
 * View a bug from the favorites menu
 */
public class ViewFavoriteAction extends AbstractFavoritesAction 
{
	
	/** The view to get the result to launch a viewer on */
	private FavoritesView view;
	
	/**
	 * Constructor
	 * @param resultsView The view to launch a viewer on
	 */
	public ViewFavoriteAction( FavoritesView resultsView ) 
	{
		setToolTipText( "View Selected Favorites" );
		setText( "View Selected" );
		setImageDescriptor(BugzillaImages.OPEN);
		view = resultsView;
	}
	
	/**
	 * View the selected bugs in the editor window
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
	public void run() 
	{
		FavoritesView.checkWindow();
		List<BugzillaOpenStructure> selectedBugs = view.getBugIdsOfSelected();
		
		// if there are some selected bugs view the bugs in the editor window
		if (!selectedBugs.isEmpty()) 
		{
			ViewBugzillaAction viewBugs = new ViewBugzillaAction("Display bugs in editor", selectedBugs);
			viewBugs.schedule();
		}
	}
}
