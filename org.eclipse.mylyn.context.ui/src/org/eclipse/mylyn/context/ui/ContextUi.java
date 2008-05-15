/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.ui;

import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 3.0
 */
public final class ContextUi {

	public static String ID_CONTEXT_PAGE = "org.eclipse.mylyn.context.ui.editor.context";

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
}
