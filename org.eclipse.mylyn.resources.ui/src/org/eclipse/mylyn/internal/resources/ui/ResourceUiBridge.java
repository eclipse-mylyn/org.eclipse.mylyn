/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.resources.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author Mik Kersten
 */
public class ResourceUiBridge extends AbstractContextUiBridge {

	@Override
	public void open(IInteractionElement element) {
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(
				element.getContentType());
		if (bridge == null) {
			return;
		} else {
			IResource resource = (IResource) bridge.getObjectForHandle(element.getHandleIdentifier());
			if (resource instanceof IFile && resource.exists()) {
				internalOpenEditor((IFile) resource, true);
			}
		}
	}

	private void internalOpenEditor(IFile file, boolean activate) {
		try {
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorDescriptor editorDescriptor = IDE.getDefaultEditor(file);
			if (editorDescriptor != null && editorDescriptor.isInternal() && !editorDescriptor.isOpenInPlace()
					&& !isContextIgnoring(editorDescriptor)) {
				IDE.openEditor(activePage, file, activate);
			}
		} catch (PartInitException e) {
			StatusHandler.fail(e, "failed to open editor for: " + file, false);
		}
	}

	private boolean isContextIgnoring(IEditorDescriptor editorDescriptor) {
		// TODO: could find a better mechanism than tagging the ID
		if (editorDescriptor.getId() != null && editorDescriptor.getId().endsWith(".contextIgnoring")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void close(IInteractionElement element) {
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(
				element.getContentType());
		Object object = bridge.getObjectForHandle(element.getHandleIdentifier());
		if (object instanceof IFile) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page != null) {
				List<IEditorReference> toClose = new ArrayList<IEditorReference>(0);
				for (IEditorReference reference : page.getEditorReferences()) {
					try {
						IResource input = (IResource) reference.getEditorInput().getAdapter(IResource.class);
						if (input instanceof IFile && ((IFile) input).equals(object))  {
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
		}
	}

	@Override
	public boolean acceptsEditor(IEditorPart editorPart) {
		return false;
	}

	@Override
	public List<TreeViewer> getContentOutlineViewers(IEditorPart editor) {
		return Collections.emptyList();
	}

	@Override
	public Object getObjectForTextSelection(TextSelection selection, IEditorPart editor) {
		return null;
	}

	@Override
	public IInteractionElement getElement(IEditorInput input) {
		Object adapter = input.getAdapter(IResource.class);
		if (adapter instanceof IFile) {
			IFile javaElement = (IFile) adapter;
			String handle = ContextCorePlugin.getDefault().getStructureBridge(javaElement).getHandleIdentifier(
					javaElement);
			return ContextCorePlugin.getContextManager().getElement(handle);
		} else {
			return null;
		}
	}

	@Override
	public String getContentType() {
		return ResourceStructureBridge.CONTENT_TYPE;
	}
}
