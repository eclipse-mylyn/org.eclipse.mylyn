/*******************************************************************************
 * Copyright (c) 2013, 2015 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Description:
 * 	This class implements the implementation of the Dashboard-Gerrit UI REST API 	reviews handler.
 *
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the plug-in handler
 ******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.ui.internal.commands.documentation;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.gerrit.dashboard.ui.GerritUi;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.GerritServerUtility;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.UIUtils;
import org.eclipse.mylyn.gerrit.dashboard.ui.views.GerritTableView;
import org.eclipse.osgi.util.NLS;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */

public class RestAPIHandler extends AbstractHandler {

	private final String REST_API_DOCUMENTATION = "Documentation/rest-api.html"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		GerritTableView view = GerritTableView.getActiveView(true);

		if (view.isGerritVersionBefore_2_5()) {
			String msg = NLS.bind(Messages.RestAPIHandler_selectedServer, view.getlastGerritServerVersion().toString());
			String reason = Messages.RestAPIHandler_serverTooOld;
			GerritUi.Ftracer.traceInfo(msg);
			UIUtils.showErrorDialog(msg, reason);
			return null;
		}

		GerritServerUtility.getInstance().openWebBrowser(REST_API_DOCUMENTATION);

		return null;
	}

}
