/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.mylyn.internal.context.ui.actions.AbstractInterestManipulationAction;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Mik Kersten
 */
@Deprecated
public class UiUtil {

	/**
	 * @deprecated Use {@link FocusedViewerManager#initializeViewerSelection(IWorkbenchPart)} instead
	 */
	@Deprecated
	public static void initializeViewerSelection(IWorkbenchPart part) {
		FocusedViewerManager.initializeViewerSelection(part);
	}

	/**
	 * @deprecated Use {@link AbstractInterestManipulationAction#displayInterestManipulationFailure()} instead
	 */
	@Deprecated
	public static void displayInterestManipulationFailure() {
		AbstractInterestManipulationAction.displayInterestManipulationFailure();
	}

}
