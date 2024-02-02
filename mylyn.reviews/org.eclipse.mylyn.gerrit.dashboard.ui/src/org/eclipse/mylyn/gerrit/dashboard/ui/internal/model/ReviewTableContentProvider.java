/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Description:
 * 	This class implements the content provider of the review table view.
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the table view content provider.
 ******************************************************************************/
package org.eclipse.mylyn.gerrit.dashboard.ui.internal.model;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.gerrit.dashboard.core.GerritTask;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */

/*
 * The content provider class is responsible for providing objects to the
 * view. It can wrap existing objects in adapters or simply return objects
 * as-is. These objects may be sensitive to the current input of the view,
 * or ignore it and always show the same content (like Task List, for
 * example).
 */

public class ReviewTableContentProvider implements IStructuredContentProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer aViewer, Object aOldInput, Object aNewInput) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object aInputElement) {
		//GerritPlugin.Ftracer.traceInfo("getElements() content provider Object: " + aInputElement);
		if (aInputElement instanceof GerritTask[] itemList) {
			return itemList;
		}
		//This null will generate an error if we reach this point, may be we should log an error and
		// initiate an empty structure to return
		//return null;
		return new GerritTask[] {};
	}

}
