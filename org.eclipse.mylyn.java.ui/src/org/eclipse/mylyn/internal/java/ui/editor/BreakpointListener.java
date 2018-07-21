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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.java.ui.JavaUiBridgePlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

public class BreakpointListener implements IAnnotationModelListener, IAnnotationModelListenerExtension {
	private final IEditorPart editor;

	private final IAnnotationModel model;

	public BreakpointListener(IEditorPart editor, IAnnotationModel model) {
		this.editor = editor;
		this.model = model;
	}

	public void modelChanged(AnnotationModelEvent event) {
		if (!ContextCore.getContextManager().isContextActive()) {
			return;
		}
		IInteractionContext context = ContextCore.getContextManager().getActiveContext();
		if (context == null) {
			return;
		}
		for (Annotation a : event.getAddedAnnotations()) {
			if (a.getType().equals("org.eclipse.debug.core.breakpoint")) { //$NON-NLS-1$
				try {
					ITypeRoot root = EditorUtility.getEditorInputJavaElement(editor, true);
					Position position = model.getPosition(a);
					IJavaElement element = root.getElementAt(position.offset);

					final AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(element);
					final String handleIdentifier = bridge.getHandleIdentifier(element);
					if (handleIdentifier != null) {
						PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
							public void run() {
								ContextCore.getContextManager().processInteractionEvent(
										new InteractionEvent(InteractionEvent.Kind.SELECTION, bridge.getContentType(),
												handleIdentifier, editor.getSite().getId()));
							}
						});
					}
				} catch (JavaModelException e) {
					StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN, e.getMessage(), e));
				}
			}
		}
	}

	public void modelChanged(IAnnotationModel model) {
		// ignore
	}
}