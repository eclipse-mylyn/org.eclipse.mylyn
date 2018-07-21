/*******************************************************************************
 * Copyright (c) 2013, 2015 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Description:
 * 	This class implements the implementation of the Dashboard-Gerrit UI my drafts reviews handler.
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the plug-in handler
 ******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.ui.internal.commands.my;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.gerrit.dashboard.ui.views.GerritTableView;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */

public class MyDraftsReviewsHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent aEvent) throws ExecutionException {
		GerritTableView reviewTableView = GerritTableView.getActiveView(true);

		// see http://gerrit-documentation.googlecode.com/svn/Documentation/2.5.2/user-search.html
		//for MY > Drafts --> is:draft
		reviewTableView.processCommands(GerritQuery.QUERY_MY_DRAFTS_CHANGES);

		return null;
	}

}
