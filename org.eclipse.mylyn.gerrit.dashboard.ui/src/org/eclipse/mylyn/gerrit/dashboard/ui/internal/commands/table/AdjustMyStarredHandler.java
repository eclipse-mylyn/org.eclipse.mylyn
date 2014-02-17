/*******************************************************************************
 * Copyright (c) 2013 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 	This class implements the implementation to adjust the selection of the My starred handler.
 * in the review table view
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the selection of My starred handler
 ******************************************************************************/
package org.eclipse.mylyn.gerrit.dashboard.ui.internal.commands.table;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.gerrit.dashboard.core.GerritTask;
import org.eclipse.mylyn.gerrit.dashboard.ui.GerritUi;
import org.eclipse.mylyn.gerrit.dashboard.ui.views.GerritTableView;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 *
 */
public class AdjustMyStarredHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@SuppressWarnings("restriction")
	@Override
	public Object execute(ExecutionEvent aEvent) throws ExecutionException {
		//GerritPlugin.Ftracer.traceInfo("AdjustMyStarred  " ); //$NON-NLS-1$
		GerritTask item = null;
		TableViewer viewer = GerritTableView.getTableViewer();
		ISelection tableSelection = viewer.getSelection();
		if (tableSelection.isEmpty()) {
			GerritUi.Ftracer.traceInfo("Selected table selection is EMPTY " ); 
			
		} else {
			if (tableSelection instanceof IStructuredSelection ) {
				Object obj = ((IStructuredSelection) tableSelection).getFirstElement();
				if (obj instanceof  GerritTask) {
					item = (GerritTask) obj;
					//GerritPlugin.Ftracer.traceInfo("Selected table OBJECT selection ID: "  + item.getId() ); 				
					if (Boolean.valueOf(item.getAttribute(GerritTask.IS_STARRED))) {
						item.setAttribute(GerritTask.IS_STARRED, Boolean.toString(false));
					} else if (!Boolean.valueOf(item.getAttribute(GerritTask.IS_STARRED))) {
						item.setAttribute(GerritTask.IS_STARRED, Boolean.toString(true));
					}
					viewer.update(item, null);
				}
			}
		}
		return item;
	}

}
