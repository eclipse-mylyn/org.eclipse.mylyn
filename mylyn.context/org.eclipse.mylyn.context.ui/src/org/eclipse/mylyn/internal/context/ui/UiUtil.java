/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
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
