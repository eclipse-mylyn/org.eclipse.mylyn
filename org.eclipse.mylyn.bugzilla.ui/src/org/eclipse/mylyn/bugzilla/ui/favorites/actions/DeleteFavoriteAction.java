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

import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.FavoritesView;

/**
 * Action of removing a bookmark
 */
public class DeleteFavoriteAction extends AbstractFavoritesAction 
{
	/** The instance of the favorites view */
	private FavoritesView view;
	
	/** True if all of the bookmarks are to be deleted */
	private boolean deleteAll;
	
	/**
	 * Constructor
	 * @param favoritesView The favorites view being used
	 * @param deleteAllFavorites <code>true</code> if all of the favorites should be deleted, else <code>false</code>
	 */
	public DeleteFavoriteAction(FavoritesView favoritesView, boolean deleteAllFavorites) 
	{
		deleteAll = deleteAllFavorites;
		
		// set the appropriate icons and tool tips for the action depending
		// on whether it will delete all items or not
		if (deleteAll) 
		{
			setToolTipText("Remove All Favorites");
			setText("Remove All");
			setImageDescriptor(BugzillaImages.REMOVE_ALL);
		}
		else 
		{
			setToolTipText( "Remove Selected Favorites" );
			setText( "Remove" );
			setImageDescriptor(BugzillaImages.REMOVE);
		}
		
		view = favoritesView;
	}
	
	/**
	 * Delete the appropriate favorites 
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
	public void run() 
	{
		FavoritesView.checkWindow();
		
		// call the appropriate delete function
		if (deleteAll)
			view.deleteAllFavorites();
		else
			view.deleteSelectedFavorites();
		FavoritesView.updateActionEnablement();
	}
}
