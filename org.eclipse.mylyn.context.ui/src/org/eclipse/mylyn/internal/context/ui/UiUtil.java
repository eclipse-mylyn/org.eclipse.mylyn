/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 * 
 * API-3.0: remove
 */
public class UiUtil {

	public static void initializeViewerSelection(IViewPart viewPart) {
		ISelectionProvider selectionProvider = viewPart.getSite().getSelectionProvider();
		if (selectionProvider != null) {
			ISelection selection = selectionProvider.getSelection();
			try {
				if (selection != null) {
					selectionProvider.setSelection(selection);
				} else {
					selectionProvider.setSelection(StructuredSelection.EMPTY);
				}
			} catch (UnsupportedOperationException e) {
				// ignore if the selection does not support setting a selection, see bug 217634
			}
		}
	}

	public static void displayInterestManipulationFailure() {
		MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Mylyn Interest Manipulation",
				"Not a valid landmark, select an element within this resource instead.");
	}
}
