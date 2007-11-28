/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten
 */
public class UiUtil {

	public static void displayInterestManipulationFailure() {
		MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Mylyn Interest Manipulation",
				"Not a valid landmark, select an element within this resource instead.");
	}

	public static Color getBackgroundForElement(IInteractionElement node) {
		return getBackgroundForElement(node, false);
	}

	@SuppressWarnings("deprecation")
	public static Color getBackgroundForElement(IInteractionElement node, boolean resolveContextColor) {
		if (node == null) {
			return null;
		} else if (!resolveContextColor && (node.getInterest().isPropagated() || node.getInterest().isPredicted())) {
			return null;
		} else if (!node.getInterest().isInteresting()) {
//		} else if (node.getInterest().getEncodedValue() <= InteractionContextManager.getScalingFactors()
//				.getInteresting()) {
			return null;
		}

		boolean isMultiple = false;
		String contextId = ContextCorePlugin.getContextManager().getDominantContextHandleForElement(node);

		if (contextId != null) {
			Highlighter highlighter = ContextUiPlugin.getDefault().getHighlighterForContextId(contextId);
			if (highlighter == null) {
				return null;
			} else if (ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
					ContextUiPrefContstants.INTERSECTION_MODE)) {
				if (isMultiple) {
					return null;
				} else {
					return null;
				}
			} else {
				return highlighter.getHighlight(node, false);
			}
		} else {
			return ColorMap.BACKGROUND_COLOR;
		}
	}
}

