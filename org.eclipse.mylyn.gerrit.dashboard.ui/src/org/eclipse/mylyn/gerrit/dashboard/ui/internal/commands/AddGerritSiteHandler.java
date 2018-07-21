/*******************************************************************************
 * Copyright (c) 2013, 2015 Ericsson AB and others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Description:
 *
 * This class implements the "Add ..." a new Gerrit
 * project locations.
 *
 * Contributors:
 *   Jacques Bouthillier - Created for Mylyn Review Dashboard-Gerrit project
 *
 ******************************************************************************/
package org.eclipse.mylyn.gerrit.dashboard.ui.internal.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.GerritServerUtility;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.UIUtils;
import org.eclipse.mylyn.gerrit.dashboard.ui.views.GerritTableView;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */
public class AddGerritSiteHandler extends AbstractHandler {

	// ------------------------------------------------------------------------
	// Variables
	// ------------------------------------------------------------------------

	private GerritServerUtility fServerUtil = null;

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * Method execute.
	 *
	 * @param aEvent
	 *            ExecutionEvent
	 * @return Object
	 * @see org.eclipse.core.commands.IHandler#execute(ExecutionEvent)
	 */
	public Object execute(final ExecutionEvent aEvent) {

		String menuItemText = ""; //$NON-NLS-1$
		fServerUtil = GerritServerUtility.getInstance();
		Object obj = aEvent.getTrigger();
		GerritTableView reviewTableView = GerritTableView.getActiveView(true);

		if (obj instanceof Event) {
			Event ev = (Event) obj;
			Widget objWidget = ev.widget;
			if (objWidget instanceof MenuItem) {
				MenuItem menuItem = (MenuItem) objWidget;
				menuItemText = menuItem.getText();
				String stURL = fServerUtil.getMenuSelectionURL(menuItemText);
				// Open the review table first;
				reviewTableView.openView();

				//Verify if we selected the "Add.." button or a pre=defined Gerrit
				if (stURL != null) {
					if (stURL.equals(fServerUtil.getLastSavedGerritServer())) {

						//Initiate the request for the list of reviews with a default query
						reviewTableView.processCommands(GerritQuery.MY_WATCHED_CHANGES);

						return Status.OK_STATUS; //For now , do not process the dialogue
					} else {
						//Store the new Gerrit server into a file
						fServerUtil.saveLastGerritServer(stURL);

						//Initiate the request for the list of reviews with a default query
						reviewTableView.processCommands(GerritQuery.MY_WATCHED_CHANGES);

						return Status.OK_STATUS; //For now , do not process the dialogue
					}
				}
			}
		}

		//Open the Dialogue to enter a new Gerrit URL
//		Object dialogObj = openDialogue ();
//		return dialogObj;

		//JB Dec 19, 2013
		//The previous line are blocked for now until we can add
		//a new Gerrit Server from the "Add Gerrit Repository.." button
		UIUtils.showErrorDialog(Messages.AddGerritSiteHandler_defineNewServer,
				Messages.AddGerritSiteHandler_buttonNotReady);
		return obj;
	}
}
