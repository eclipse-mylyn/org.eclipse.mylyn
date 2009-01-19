/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.tasks.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.internal.wikitext.ui.editor.IFoldingStructure;
import org.eclipse.mylyn.monitor.ui.AbstractEditorTracker;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * 
 * based on implementation of ActiveFoldingEditorTracker in org.eclipse.mylyn.java.ui
 * 
 * @author David Green
 */
public class ActiveFoldingEditorTracker extends AbstractEditorTracker {

	private final Map<IEditorPart, ActiveFoldingListener> partToListener = new HashMap<IEditorPart, ActiveFoldingListener>();

	public ActiveFoldingEditorTracker(IWorkbench workbench) {
		install(workbench);
	}

	@Override
	public void install(IWorkbench workbench) {
		super.install(workbench);
		for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
			for (IWorkbenchPage page : window.getPages()) {
				for (IEditorReference reference : page.getEditorReferences()) {
					IEditorPart editorPart = reference.getEditor(false);
					if (editorPart != null) {
						editorOpened(editorPart);
					}
				}
			}
		}
	}

	@Override
	protected void editorBroughtToTop(IEditorPart part) {
		// nothing to do
	}

	@Override
	protected void editorClosed(IEditorPart part) {
		ActiveFoldingListener listener = partToListener.remove(part);
		if (listener != null) {
			listener.dispose();
		}
	}

	@Override
	protected void editorOpened(IEditorPart part) {
		IFoldingStructure foldingStructure = (IFoldingStructure) part.getAdapter(IFoldingStructure.class);
		if (foldingStructure == null) {
			return;
		}
		if (partToListener.containsKey(part)) {
			return;
		}
		partToListener.put(part, new ActiveFoldingListener(part, foldingStructure));
	}

}
