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
/*
 * Created on Apr 6, 2005
 */
package org.eclipse.mylar.internal.java.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaOutlinePage;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.context.ui.AbstractContextUiBridge;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.java.JavaStructureBridge;
import org.eclipse.mylar.resources.MylarResourcesPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Mik Kersten
 */
public class JavaUiBridge extends AbstractContextUiBridge {

	private Field javaOutlineField = null;

	public JavaUiBridge() {
		try {
			javaOutlineField = JavaOutlinePage.class.getDeclaredField("fOutlineViewer");
			javaOutlineField.setAccessible(true);
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "could not get install Mylar on Outline viewer", true);
		}
	}

	@Override
	public void open(IMylarElement node) {
		// get the element and open it in an editor
		IJavaElement javaElement = JavaCore.create(node.getHandleIdentifier());
		if (javaElement == null || !javaElement.exists())
			return;
		try {
			IEditorPart part = JavaUI.openInEditor(javaElement);
			JavaUI.revealInEditor(part, javaElement);
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "Could not open editor for: " + node, true);
		}
	}

//	private boolean explorerLinked = PreferenceConstants.getPreferenceStore().getBoolean(
//	PreferenceConstants.LINK_PACKAGES_TO_EDITOR);
	
//	public void setContextCapturePaused(boolean paused) {
//		PackageExplorerPart explorer = PackageExplorerPart.getFromActivePerspective();
//		if (paused) {
//			explorerLinked = PreferenceConstants.getPreferenceStore().getBoolean(
//					PreferenceConstants.LINK_PACKAGES_TO_EDITOR);
//			if (explorerLinked) { // causes delayed selection
//				if (explorer != null)
//					explorer.setLinkingEnabled(false);
//			}
//		} else {
//			if (explorer != null)
//				explorer.setLinkingEnabled(true);
//			PreferenceConstants.getPreferenceStore().setValue(PreferenceConstants.LINK_PACKAGES_TO_EDITOR,
//					explorerLinked);
//			if (explorer != null) {
//				explorer.setLinkingEnabled(explorerLinked);
//			}
//		}
//	}

	@Override
	public void restoreEditor(IMylarElement document) {
		IResource resource = MylarResourcesPlugin.getDefault().getResourceForElement(document, false);
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage != null && resource instanceof IFile && resource.exists()) {
			try {
				IDE.openEditor(activePage, (IFile) resource, false);
			} catch (PartInitException e) {
				MylarStatusHandler.fail(e, "failed to open editor for: " + resource, false);
			}
		}
	}

	@Override
	public void close(IMylarElement node) {
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page != null) {
				IEditorReference[] references = page.getEditorReferences();
				for (int i = 0; i < references.length; i++) {
					IEditorPart part = references[i].getEditor(false);
					if (part != null && part instanceof JavaEditor) {
						JavaEditor editor = (JavaEditor) part;
						Object adapter = editor.getEditorInput().getAdapter(IJavaElement.class);
						if (adapter instanceof IJavaElement
								&& node.getHandleIdentifier().equals(((IJavaElement) adapter).getHandleIdentifier())) {
							editor.close(true);
						}
					}
				}
			}
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "Could not auto close editor.", false);
		}
	}

	@Override
	public boolean acceptsEditor(IEditorPart editorPart) {
		return editorPart instanceof JavaEditor;
	}

	@Override
	public IMylarElement getElement(IEditorInput input) {
		Object adapter = input.getAdapter(IJavaElement.class);
		if (adapter instanceof IJavaElement) {
			IJavaElement javaElement = (IJavaElement)adapter;
			String handle = ContextCorePlugin.getDefault().getStructureBridge(javaElement).getHandleIdentifier(javaElement);
			return ContextCorePlugin.getContextManager().getElement(handle);
		} else {
			return null;
		}
	}
	
	@Override
	public List<TreeViewer> getContentOutlineViewers(IEditorPart editorPart) {
		if (editorPart == null || javaOutlineField == null)
			return null;
		List<TreeViewer> viewers = new ArrayList<TreeViewer>();
		Object out = editorPart.getAdapter(IContentOutlinePage.class);
		if (out instanceof JavaOutlinePage) {
			JavaOutlinePage page = (JavaOutlinePage) out;
			if (page != null && page.getControl() != null) {
				try {
					viewers.add((TreeViewer) javaOutlineField.get(page));
				} catch (Exception e) {
					MylarStatusHandler.log(e, "could not get outline viewer");
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
					if (element instanceof IJavaElement)
						return element;
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
