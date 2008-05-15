/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Apr 6, 2005
 */
package org.eclipse.mylyn.internal.ide.ant;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ant.internal.ui.editor.AntEditor;
import org.eclipse.ant.internal.ui.editor.outline.AntEditorContentOutlinePage;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Mik Kersten
 */
public class AntUiBridge extends AbstractContextUiBridge {

	/**
	 * @see
	 * 	org.eclipse.mylyn.context.ui.AbstractContextUiBridge#open(org.eclipse.mylyn.context.core.IInteractionElement)
	 */
	@Override
	public void open(IInteractionElement node) {
		// get the handle of the node
		String handle = node.getHandleIdentifier();

		int first = handle.indexOf(";");
		String filename = "";
		if (first == -1) {
			filename = handle;
		} else {
			filename = handle.substring(0, first);
		}

		try {
			// get the file
			IPath path = new Path(filename);
			IFile f = (IFile) ((Workspace) ResourcesPlugin.getWorkspace()).newResource(path, IResource.FILE);

			// open the xml file looking at the proper line
			IEditorPart editor = openInEditor(f, true);

			// if the editor is null, we had a problem and should return
			if (editor == null) {
				StatusHandler.log(new Status(IStatus.ERROR, AntUiBridgePlugin.ID_PLUGIN,
						"Unable to open editor for file: " + filename));
				return;
			}

			// get the contents and create a new document so that we can get
			// the offsets to highlight
			// String content = XmlNodeHelper.getContents(f.getContents());

			// IDocument d = new Document(content);

			// if(first != -1){
			// int start = Integer.parseInt(handle.substring(first + 1));
			//
			// // get the offsets for the element
			// int startOffset = d.getLineOffset(start);
			// int length = 0;
			//                
			// // set the selection if the selection provider is not null
			// ISelectionProvider selectionProvider =
			// editor.getEditorSite().getSelectionProvider();
			// if(selectionProvider != null)
			// selectionProvider.setSelection(new TextSelection(startOffset,
			// length));
			// }

		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, AntUiBridgePlugin.ID_PLUGIN, "Could not open XML editor", e));
		}
	}

	/**
	 * Open a file in the appropriate editor
	 * 
	 * @param file
	 * 		The IFile to open
	 * @param activate
	 * 		Whether to activate the editor or not
	 * @return The IEditorPart that the file opened in
	 * @throws PartInitException
	 */
	private IEditorPart openInEditor(IFile file, boolean activate) throws PartInitException {
		if (file != null) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page != null && file.exists()) {
				try {
					IEditorPart editorPart = IDE.openEditor(page, file, activate);
					return editorPart;
				} catch (Exception e) {
					// ignore this
				}
			}
		}
		return null;
	}

	@Override
	public void close(IInteractionElement node) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			List<IEditorReference> toClose = new ArrayList<IEditorReference>();
			for (IEditorReference reference : page.getEditorReferences()) {
				try {
					if (reference.getEditorInput() instanceof IFileEditorInput) {
						IFileEditorInput input = (IFileEditorInput) reference.getEditorInput();
						if (input.getFile().getFullPath().toString().equals(node.getHandleIdentifier())) {
							toClose.add(reference);
						}
					}
				} catch (PartInitException e) {
					// ignore
				}
			}
			if (toClose.size() > 0) {
				page.closeEditors(toClose.toArray(new IEditorReference[toClose.size()]), true);
			}
		}
	}

	@Override
	public boolean acceptsEditor(IEditorPart editorPart) {
		return editorPart instanceof AntEditor;
	}

	/**
	 * HACK: use reflection to get the TreeViewer
	 */
	@Override
	public List<TreeViewer> getContentOutlineViewers(IEditorPart editor) {
		List<TreeViewer> viewers = new ArrayList<TreeViewer>();
		if (editor instanceof AntEditor) {
			try {
				AntEditor ae = (AntEditor) editor;
				AntEditorContentOutlinePage outline = (AntEditorContentOutlinePage) ae.getAdapter(IContentOutlinePage.class);
				Class<?> clazz = ContentOutlinePage.class;
				Method method = clazz.getDeclaredMethod("getTreeViewer", new Class[] {});
				method.setAccessible(true);
				viewers.add((TreeViewer) method.invoke(outline, new Object[] {}));
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.WARNING, AntUiBridgePlugin.ID_PLUGIN, "Unable to get outline", e));
			}
		}
		return viewers;
	}

	@Override
	public Object getObjectForTextSelection(TextSelection selection, IEditorPart editor) {
		return null;
	}

	@Override
	public IInteractionElement getElement(IEditorInput input) {
		return null;
	}

	@Override
	public String getContentType() {
		return AntStructureBridge.CONTENT_TYPE;
	}
}
