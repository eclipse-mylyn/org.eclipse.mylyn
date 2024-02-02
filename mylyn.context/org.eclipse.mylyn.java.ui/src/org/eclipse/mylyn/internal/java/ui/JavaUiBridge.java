/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Remy Chi Jian Suen - Bug 256071 Reduce/remove reflection usage in Java bridge
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Mik Kersten
 */
public class JavaUiBridge extends AbstractContextUiBridge {

	@Override
	public void open(IInteractionElement node) {
		IJavaElement javaElement = JavaCore.create(node.getHandleIdentifier());
		if (javaElement == null || !javaElement.exists()) {
			return;
		}

		try {
			IEditorPart part = JavaUI.openInEditor(javaElement);
			JavaUI.revealInEditor(part, javaElement);
		} catch (PartInitException | JavaModelException e) {
			StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN,
					NLS.bind("Failed to open editor for: {0}", node.getHandleIdentifier()), e)); //$NON-NLS-1$
		}
	}

	@Override
	public void close(IInteractionElement node) {
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page != null) {
				List<IEditorReference> toClose = new ArrayList<>(4);
				for (IEditorReference reference : page.getEditorReferences()) {
					try {
						IJavaElement input = reference.getEditorInput().getAdapter(IJavaElement.class);
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
			StatusHandler
					.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN, "Could not auto close editor", t)); //$NON-NLS-1$
		}
	}

	@Override
	public boolean acceptsEditor(IEditorPart editorPart) {
		return editorPart instanceof JavaEditor;
	}

	@Override
	public IInteractionElement getElement(IEditorInput input) {
		Object adapter = input.getAdapter(IJavaElement.class);
		if (adapter instanceof IJavaElement javaElement) {
			String handle = ContextCore.getStructureBridge(javaElement).getHandleIdentifier(javaElement);
			return ContextCore.getContextManager().getElement(handle);
		} else {
			return null;
		}
	}

	@Override
	public List<TreeViewer> getContentOutlineViewers(IEditorPart editorPart) {
		if (editorPart == null) {
			return null;
		}
		List<TreeViewer> viewers = new ArrayList<>();
		Object out = editorPart.getAdapter(IContentOutlinePage.class);
		if (out instanceof Page page) {
			if (page.getControl() != null) {
				IWorkbenchSite site = page.getSite();
				if (site != null) {
					ISelectionProvider provider = site.getSelectionProvider();
					if (provider instanceof TreeViewer) {
						viewers.add((TreeViewer) provider);
					}
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
