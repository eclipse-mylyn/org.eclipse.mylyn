/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaOutlinePage;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Mik Kersten
 */
public class JavaUiBridge extends AbstractContextUiBridge {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.java.ui";

	private Field javaOutlineField = null;

	public JavaUiBridge() {
		try {
			javaOutlineField = JavaOutlinePage.class.getDeclaredField("fOutlineViewer");
			javaOutlineField.setAccessible(true);
		} catch (Exception e) {
			StatusHandler.fail(new Status(IStatus.ERROR, JavaUiBridgePlugin.PLUGIN_ID, "Could not get outline viewer",
					e));
		}
	}

	@Override
	public void open(IInteractionElement node) {
		IJavaElement javaElement = JavaCore.create(node.getHandleIdentifier());
		if (javaElement == null || !javaElement.exists()) {
			return;
		}
		try {
			IEditorPart part = JavaUI.openInEditor(javaElement);
			JavaUI.revealInEditor(part, javaElement);
		} catch (Throwable t) {
			StatusHandler.fail(new Status(IStatus.ERROR, JavaUiBridgePlugin.PLUGIN_ID, "Could not open editor for: "
					+ node, t));
		}
	}

	@Override
	public void close(IInteractionElement node) {
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page != null) {
				List<IEditorReference> toClose = new ArrayList<IEditorReference>(4);
				for (IEditorReference reference : page.getEditorReferences()) {
					try {
						IJavaElement input = (IJavaElement) reference.getEditorInput().getAdapter(IJavaElement.class);
						if (input != null && node.getHandleIdentifier().equals(input.getHandleIdentifier())) {
							toClose.add(reference);
						}
					} catch (PartInitException e) {
						// ignore
					}
				}
				if (toClose.size() > 0) {
					page.closeEditors(toClose.toArray(new IEditorReference[toClose.size()]), true);
				}
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.PLUGIN_ID, "Could not auto close editor", t));
		}
	}

	@Override
	public boolean acceptsEditor(IEditorPart editorPart) {
		return editorPart instanceof JavaEditor;
	}

	@Override
	public IInteractionElement getElement(IEditorInput input) {
		Object adapter = input.getAdapter(IJavaElement.class);
		if (adapter instanceof IJavaElement) {
			IJavaElement javaElement = (IJavaElement) adapter;
			String handle = ContextCore.getStructureBridge(javaElement).getHandleIdentifier(
					javaElement);
			return ContextCore.getContextManager().getElement(handle);
		} else {
			return null;
		}
	}

	@Override
	public List<TreeViewer> getContentOutlineViewers(IEditorPart editorPart) {
		if (editorPart == null || javaOutlineField == null) {
			return null;
		}
		List<TreeViewer> viewers = new ArrayList<TreeViewer>();
		Object out = editorPart.getAdapter(IContentOutlinePage.class);
		if (out instanceof JavaOutlinePage) {
			JavaOutlinePage page = (JavaOutlinePage) out;
			if (page.getControl() != null) {
				try {
					viewers.add((TreeViewer) javaOutlineField.get(page));
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.PLUGIN_ID,
							"Could not get outline viewer", e));
				}
			}
		}
		return viewers;
	}

	@Override
	public Object getObjectForTextSelection(TextSelection selection, IEditorPart editor) {
		if (editor instanceof JavaEditor) {
			TextSelection textSelection = selection;
			try {
				if (selection != null) {
					return SelectionConverter.resolveEnclosingElement((JavaEditor) editor, textSelection);
				} else {
					Object element = ((JavaEditor) editor).getEditorInput().getAdapter(IJavaElement.class);
					if (element instanceof IJavaElement) {
						return element;
					}
				}
			} catch (JavaModelException e) {
				// ignore
			}
		}
		return null;
	}

	@Override
	public String getContentType() {
		return JavaStructureBridge.CONTENT_TYPE;
	}

}
