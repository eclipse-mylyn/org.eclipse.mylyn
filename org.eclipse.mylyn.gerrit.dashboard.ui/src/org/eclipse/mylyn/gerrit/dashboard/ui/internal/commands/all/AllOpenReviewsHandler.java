/*******************************************************************************
 * Copyright (c) 2013 Ericsson AB and others.
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 
 * This class implements the "Add ..." a new Gerrit 
 * project locations.
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial implementation of the handler
 *   
 ******************************************************************************/
package org.eclipse.mylyn.gerrit.dashboard.ui.internal.commands.all;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.mylyn.gerrit.dashboard.ui.GerritUi;
import org.eclipse.mylyn.gerrit.dashboard.ui.views.GerritTableView;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */

public class AllOpenReviewsHandler extends AbstractHandler {

	/**
	 * Method execute.
	 * 
	 * @param aEvent
	 *            ExecutionEvent
	 * @return Object
	 * @see org.eclipse.core.commands.IHandler#execute(ExecutionEvent)
	 */
	public Object execute(final ExecutionEvent aEvent) {

		GerritUi.Ftracer.traceInfo("Search the Gerrit reviews for All Open  "); //$NON-NLS-1$

		GerritTableView reviewTableView = GerritTableView.getActiveView();

		// see http://gerrit-documentation.googlecode.com/svn/Documentation/2.5.2/user-search.html
		//for All > Open--> status:open (or is:open)
		GerritUi.Ftracer.traceInfo("Execute command :   " + GerritQuery.ALL_OPEN_CHANGES); //$NON-NLS-1$
		reviewTableView.processCommands(GerritQuery.ALL_OPEN_CHANGES);

		return null;

	}

}
