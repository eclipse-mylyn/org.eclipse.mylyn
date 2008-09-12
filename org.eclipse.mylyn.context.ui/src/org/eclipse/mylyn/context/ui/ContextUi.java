/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.ui;

import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.ui.ColorMap;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.IContextUiPreferenceContstants;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 3.0
 */
public final class ContextUi {

	/**
	 * @return the corresponding adapter if found, or an adapter with no behavior otherwise (so null is never returned)
	 */
	public static AbstractContextUiBridge getUiBridge(String contentType) {
		return ContextUiPlugin.getDefault().getUiBridge(contentType);
	}

	/**
	 * TODO: cache this to improve performance?
	 */
	public static AbstractContextUiBridge getUiBridgeForEditor(IEditorPart editorPart) {
		return ContextUiPlugin.getDefault().getUiBridgeForEditor(editorPart);
	}

	public static Color getForeground(IInteractionElement node) {
		if (node == null) {
			return null;
		}
		if (node.getInterest().isPredicted() || node.getInterest().isPropagated()) {
			return ColorMap.GRAY_MEDIUM;
		} else if (node.getInterest().isLandmark()) {
			return ColorMap.LANDMARK;
		} else if (node.getInterest().isInteresting()) {
			return null;
		}
		return ColorMap.GRAY_MEDIUM;
	}

	public static boolean isEditorAutoCloseEnabled() {
		return ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
				IContextUiPreferenceContstants.AUTO_MANAGE_EDITOR_CLOSE);
	}

	public static boolean isEditorAutoManageEnabled() {
		return ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
				IContextUiPreferenceContstants.AUTO_MANAGE_EDITORS);
	}
}
