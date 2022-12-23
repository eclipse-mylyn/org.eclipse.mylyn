/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Description:
 * 	This class implements the implementation of the Dashboard-Gerrit UI Access controls reviews handler.
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

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */

public class AccessControlHandler extends AbstractHandler {

	private final String ACCESS_CONTROL_DOCUMENTATION = "Documentation/access-control.html"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent aEvent) throws ExecutionException {
		GerritUi.Ftracer.traceInfo("Search the documentation AccessControlHandler  "); //$NON-NLS-1$

		GerritServerUtility.getInstance().openWebBrowser(ACCESS_CONTROL_DOCUMENTATION);

		return null;
	}

}
