/*******************************************************************************
 * Copyright (c) 2013, 2015 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the plug-in handler
 ******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.ui.internal.commands.projects;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.gerrit.dashboard.ui.views.GerritTableView;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;

/**
 * This class implements the implementation of the Dashboard-Gerrit UI project list reviews handler.
 * 
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */

public class ProjectListHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent aEvent) throws ExecutionException {
		GerritTableView reviewTableView = GerritTableView.getActiveView(true);

		// see http://gerrit-documentation.googlecode.com/svn/Documentation/2.5.2/user-search.html
		//for Project list
		reviewTableView.processCommands(GerritQuery.OPEN_CHANGES_BY_PROJECT);

		return null;

	}

}
