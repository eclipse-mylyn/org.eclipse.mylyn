/*******************************************************************************
 * Copyright (c) 2004, 2009 Mylyn project committers and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *******************************************************************************/
package org.eclipse.mylyn.internal.cdt.ui.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.mylyn.monitor.ui.AbstractEditorTracker;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 */
public class ActiveFoldingEditorTracker extends AbstractEditorTracker {

	protected Map<CEditor, ActiveFoldingListener> editorListenerMap = new HashMap<>();

	@Override
	public void editorOpened(IEditorPart part) {
		if (part instanceof CEditor) {
			registerEditor((CEditor) part);
		}
	}

	@Override
	public void editorClosed(IEditorPart part) {
		if (part instanceof CEditor) {
			unregisterEditor((CEditor) part);
		}
	}

	public void registerEditor(final CEditor editor) {
		if (editorListenerMap.containsKey(editor)) {
			return;
		} else {
			ActiveFoldingListener listener = new ActiveFoldingListener(editor);
			editorListenerMap.put(editor, listener);
		}
	}

	public void unregisterEditor(CEditor editor) {
		ActiveFoldingListener listener = editorListenerMap.get(editor);
		if (listener != null) {
			listener.dispose();
		}
		editorListenerMap.remove(editor);
	}

	/**
	 * For testing.
	 */
	public Map<CEditor, ActiveFoldingListener> getEditorListenerMap() {
		return editorListenerMap;
	}

	@Override
	protected void editorBroughtToTop(IEditorPart part) {
		// ignore
	}

}
