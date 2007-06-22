/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.java.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.mylyn.internal.java.ui.editor.ActiveFoldingListener;
import org.eclipse.mylyn.monitor.ui.AbstractEditorTracker;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 */
public class ActiveFoldingEditorTracker extends AbstractEditorTracker {

	protected Map<JavaEditor, ActiveFoldingListener> editorListenerMap = new HashMap<JavaEditor, ActiveFoldingListener>();

	@Override
	public void editorOpened(IEditorPart part) {
		if (part instanceof JavaEditor)
			registerEditor((JavaEditor) part);
	}

	@Override
	public void editorClosed(IEditorPart part) {
		if (part instanceof JavaEditor)
			unregisterEditor((JavaEditor) part);
	}

	public void registerEditor(final JavaEditor editor) {
		if (editorListenerMap.containsKey(editor)) {
			return;
		} else {
			ActiveFoldingListener listener = new ActiveFoldingListener(editor);
			editorListenerMap.put(editor, listener);
		}
	}

	public void unregisterEditor(JavaEditor editor) {
		ActiveFoldingListener listener = editorListenerMap.get(editor);
		if (listener != null) {
			listener.dispose();
		} 
		editorListenerMap.remove(editor);
	}

	/**
	 * For testing.
	 */
	public Map<JavaEditor, ActiveFoldingListener> getEditorListenerMap() {
		return editorListenerMap;
	}

	@Override
	protected void editorBroughtToTop(IEditorPart part) {
		// ignore
	}

}
