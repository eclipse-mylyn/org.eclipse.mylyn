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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * a UI context bridge for WikiText.
 * 
 * @author David Green
 */
public class WikiTextContextUiBridge extends AbstractContextUiBridge {

	public WikiTextContextUiBridge() {
	}

	@Override
	public boolean acceptsEditor(IEditorPart editorPart) {
		return editorPart.getAdapter(OutlineItem.class) != null;
	}

	@Override
	public List<TreeViewer> getContentOutlineViewers(IEditorPart editorPart) {
		List<TreeViewer> viewers = new ArrayList<TreeViewer>();
		if (acceptsEditor(editorPart)) {
			try {
				IContentOutlinePage outlinePage = (IContentOutlinePage) editorPart.getAdapter(IContentOutlinePage.class);
				if (outlinePage != null) {
					Method method = ContentOutlinePage.class.getDeclaredMethod("getTreeViewer", new Class[] {}); //$NON-NLS-1$
					method.setAccessible(true);
					viewers.add((TreeViewer) method.invoke(outlinePage, new Object[] {}));
				}
			} catch (Exception e) {
				// ignore
			}
		}
		return viewers;
	}

	@Override
	public String getContentType() {
		return WikiTextContextStructureBridge.CONTENT_TYPE;
	}

	@Override
	public Object getObjectForTextSelection(TextSelection selection, IEditorPart editor) {
		OutlineItem outline = (OutlineItem) editor.getAdapter(OutlineItem.class);
		if (outline != null && selection != null) {
			return outline.findNearestMatchingOffset(selection.getOffset());
		}
		return outline;
	}

	@Override
	public IInteractionElement getElement(IEditorInput input) {
		Object adapter = input.getAdapter(OutlineItem.class);
		if (adapter != null) {
			String handle = ContextCore.getStructureBridge(adapter).getHandleIdentifier(adapter);
			return ContextCore.getContextManager().getElement(handle);
		}
		return null;
	}

	@Override
	public void open(IInteractionElement element) {
		AbstractContextStructureBridge structureBridge = ContextCore.getStructureBridge(WikiTextContextStructureBridge.CONTENT_TYPE);
		if (structureBridge instanceof WikiTextContextStructureBridge) {
			Object object = structureBridge.getObjectForHandle(element.getHandleIdentifier());
			OutlineItem item = null;
			if (object instanceof OutlineItem) {
				item = (OutlineItem) object;
				object = ((WikiTextContextStructureBridge) structureBridge).getFile(item);
			}
			if (object instanceof IFile) {
				FileEditorInput editorInput = new FileEditorInput((IFile) object);
				try {
					IEditorPart editor = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow()
							.getActivePage()
							.openEditor(editorInput, "org.eclipse.mylyn.wikitext.ui.editor.markupEditor"); //$NON-NLS-1$
					if (item != null && editor instanceof IShowInTarget) {
						((IShowInTarget) editor).show(new ShowInContext(editorInput, new StructuredSelection(item)));
					}
				} catch (PartInitException e) {
					WikiTextTasksUiPlugin.getDefault().log(e);
				}
			}
		}

	}

	@Override
	public void close(IInteractionElement element) {
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page != null) {
				List<IEditorReference> editors = new ArrayList<IEditorReference>(4);
				for (IEditorReference reference : page.getEditorReferences()) {
					try {
						if (reference.getEditorInput() instanceof IFileEditorInput) {
							IFileEditorInput input = (IFileEditorInput) reference.getEditorInput();
							if (input.getFile().getFullPath().toString().equals(element.getHandleIdentifier())) {
								editors.add(reference);
							}
						}
					} catch (PartInitException e) {
						// ignore
					}
				}
				if (!editors.isEmpty()) {
					page.closeEditors(editors.toArray(new IEditorReference[editors.size()]), true);
				}
			}
		} catch (Throwable t) {
			WikiTextTasksUiPlugin.getDefault().log(t);
		}
	}

}
