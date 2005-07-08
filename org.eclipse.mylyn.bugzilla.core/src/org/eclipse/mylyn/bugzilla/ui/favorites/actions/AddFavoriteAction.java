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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.core.search.BugzillaSearchResultView;
import org.eclipse.mylar.bugzilla.ui.FavoritesView;
import org.eclipse.mylar.bugzilla.ui.favorites.Favorite;


/**
 * Bookmark a returned Bugzilla Search result.
 */
public class AddFavoriteAction extends AbstractFavoritesAction {

	/** Selected objects */
	private List<Favorite> selected;
	
	/** The view where the Bugzilla search results are displayed */
	private BugzillaSearchResultView resultView;

	/**
	 * Constructor
	 * @param text The label for the context menu item
	 * @param resultView The view where the Bugzilla search results are displayed
	 */
	public AddFavoriteAction(String text, BugzillaSearchResultView resultView) {
		setText(text);
		setIcon("icons/elcl16/bug-favorite.gif");
		this.resultView = resultView;
		selected = null;
	}
	
	/**
	 * Bookmark the selected items
	 * @see org.eclipse.ui.actions.ActionDelegate#run(IAction)
	 */
	@Override
	public void run() 
	{
		FavoritesView.checkWindow();
		
		selected = new ArrayList<Favorite>();
		buildSelection();

		// go through each of the selected items and add its data to the favorites file
		for (int i = 0; i < selected.size(); i++) 
		{
			BugzillaPlugin.getDefault().getFavorites().add(selected.get(i));
		}
		FavoritesView.add();
		FavoritesView.updateActionEnablement();
	}

	/**
	 * Gets the entire selection and puts each bug report into a list. Only the
	 * relevent parts of each bug report are put into the list.
	 */
    @SuppressWarnings("unchecked")
	private void buildSelection() {
		ISelection s = resultView.getViewer().getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) s;
			for (Iterator<IMarker> it = selection.iterator(); it.hasNext();) {
				IMarker marker = it.next();

				try 
				{
					// try to get the attribute for the marker
					Integer attributeId = (Integer) marker.getAttribute(IBugzillaConstants.HIT_MARKER_ATTR_ID);
					// add the selected item to the list of items that are selected
					String description = (String) marker.getAttribute(IBugzillaConstants.HIT_MARKER_ATTR_DESC);
					String query = (String) marker.getAttribute(IBugzillaConstants.HIT_MARKER_ATTR_QUERY);
					
					// create a map to add attributes to so that the favorites file can sort
					HashMap<String, Object> attributes = new HashMap<String, Object>();
					attributes.put(IBugzillaConstants.HIT_MARKER_ATTR_ID, attributeId);
					attributes.put(IBugzillaConstants.HIT_MARKER_ATTR_PRIORITY, marker.getAttribute(IBugzillaConstants.HIT_MARKER_ATTR_PRIORITY));
					attributes.put(IBugzillaConstants.HIT_MARKER_ATTR_SEVERITY, marker.getAttribute(IBugzillaConstants.HIT_MARKER_ATTR_SEVERITY));
					attributes.put(IBugzillaConstants.HIT_MARKER_ATTR_STATE, marker.getAttribute(IBugzillaConstants.HIT_MARKER_ATTR_STATE));
					attributes.put(IBugzillaConstants.HIT_MARKER_ATTR_RESULT, marker.getAttribute(IBugzillaConstants.HIT_MARKER_ATTR_RESULT));
					
					Favorite favorite = new Favorite(BugzillaPlugin.getDefault().getServerName(), attributeId.intValue(), description, query, attributes);
					selected.add(favorite);
				}
				catch (CoreException ignored) 
				{
					// do nothing
				}
			}
		}
	}
	
}
