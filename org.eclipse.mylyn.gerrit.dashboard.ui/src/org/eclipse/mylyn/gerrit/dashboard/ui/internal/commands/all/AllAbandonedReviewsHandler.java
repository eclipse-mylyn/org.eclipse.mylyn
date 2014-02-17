/*******************************************************************************
 * Copyright (c) 2013 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 	This class implements the implementation of the Dashboard-Gerrit UI Abandoned reviews handler.
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the plug-in handler
 ******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.ui.internal.commands.all;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.gerrit.dashboard.ui.GerritUi;
import org.eclipse.mylyn.gerrit.dashboard.ui.views.GerritTableView;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 *
 */

public class AllAbandonedReviewsHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent aEvent) throws ExecutionException {
		GerritUi.Ftracer.traceInfo("Search the Gerrit reviews for All Abandonned  " ); //$NON-NLS-1$

		GerritTableView reviewTableView = GerritTableView
				.getActiveView();

		// see http://gerrit-documentation.googlecode.com/svn/Documentation/2.5.2/user-search.html
		//for All > AbandonedOpen--> status:abandoned
		GerritUi.Ftracer.traceInfo("Execute command :   "  + GerritQuery.QUERY_ALL_ABANDONED_CHANGES ); //$NON-NLS-1$
		reviewTableView.processCommands(GerritQuery.QUERY_ALL_ABANDONED_CHANGES);

		return null;	
	}

}
