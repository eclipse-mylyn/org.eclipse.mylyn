/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.editor;

import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.mylyn.monitor.ui.AbstractEditorTracker;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

public class BreakpointEditorTracker extends AbstractEditorTracker {
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
	protected void editorOpened(IEditorPart part) {
		if (part instanceof JavaEditor) {
			IAnnotationModel model = ((JavaEditor) part).getViewer().getAnnotationModel();
			if (model != null) {
				model.addAnnotationModelListener(new BreakpointListener(part, model));
			}
		}
	}

	@Override
	protected void editorClosed(IEditorPart part) {
		// ignore
	}

	@Override
	protected void editorBroughtToTop(IEditorPart part) {
		// ignore
	}
}