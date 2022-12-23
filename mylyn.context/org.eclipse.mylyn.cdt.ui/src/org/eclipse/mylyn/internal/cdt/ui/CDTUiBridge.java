/*******************************************************************************
 * Copyright (c) 2004, 2013 Mylyn project committers and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * 		Red Hat Inc. - Initial implementation based on JavaUiBridge
 *******************************************************************************/

package org.eclipse.mylyn.internal.cdt.ui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.ui.actions.SelectionConverter;
import org.eclipse.cdt.internal.ui.editor.AbstractCModelOutlinePage;
import org.eclipse.cdt.internal.ui.editor.CContentOutlinePage;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.internal.ui.util.EditorUtility;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.TextSelection;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 * @author Shawn Minto
 */
public class CDTUiBridge extends AbstractContextUiBridge {

	private Field cOutlineField = null;

	public CDTUiBridge() {
		try {
			cOutlineField = AbstractCModelOutlinePage.class.getDeclaredField("fTreeViewer"); //$NON-NLS-1$
			cOutlineField.setAccessible(true);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, CDTUIBridgePlugin.ID_PLUGIN,
					"Unexpected error while focusing outline viewer", e)); //$NON-NLS-1$
		}
	}

	@Override
	public void open(IInteractionElement node) {
		try {
			ICElement cElement = CDTStructureBridge.getElementForHandle(node.getHandleIdentifier());
			if (cElement == null || !cElement.exists()) {
				return;
			}
			EditorUtility.openInEditor(cElement);
		} catch (CModelException t) {
			StatusHandler.log(new Status(IStatus.ERROR, CDTUIBridgePlugin.ID_PLUGIN, NLS.bind(
					"Failed to open editor for: {0}", node.getHandleIdentifier()), t)); //$NON-NLS-1$
		} catch (PartInitException t) {
			StatusHandler.log(new Status(IStatus.ERROR, CDTUIBridgePlugin.ID_PLUGIN, NLS.bind(
					"Failed to open editor for: {0}", node.getHandleIdentifier()), t)); //$NON-NLS-1$
		}
	}

	@Override
	public void close(IInteractionElement node) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			List<IEditorReference> toClose = new ArrayList<IEditorReference>(4);
			for (IEditorReference reference : page.getEditorReferences()) {
				try {
					ICElement input = (ICElement) reference.getEditorInput().getAdapter(ICElement.class);
					if (input != null
							&& node.getHandleIdentifier().equals(CDTStructureBridge.getHandleForElement(input))) {
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

	@Override
	public boolean acceptsEditor(IEditorPart editorPart) {
		return editorPart instanceof CEditor;
	}

	@Override
	public IInteractionElement getElement(IEditorInput input) {
		Object adapter = input.getAdapter(ICElement.class);
		if (adapter instanceof ICElement) {
			ICElement cElement = (ICElement) adapter;
			String handle = ContextCore.getStructureBridge(cElement).getHandleIdentifier(cElement);
			return ContextCore.getContextManager().getElement(handle);
		} else {
			return null;
		}
	}

	@Override
	public List<TreeViewer> getContentOutlineViewers(IEditorPart editorPart) {
		if (editorPart == null || cOutlineField == null) {
			return null;
		}
		List<TreeViewer> viewers = new ArrayList<TreeViewer>();
		Object out = editorPart.getAdapter(IContentOutlinePage.class);
		if (out instanceof CContentOutlinePage) {
			CContentOutlinePage page = (CContentOutlinePage) out;
			if (page.getControl() != null) {
				try {
					viewers.add((TreeViewer) cOutlineField.get(page));
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, CDTUIBridgePlugin.ID_PLUGIN,
							"Could not get Outline viewer.", e)); //$NON-NLS-1$
				}
			}
		}
		return viewers;
	}

	@Override
	public Object getObjectForTextSelection(TextSelection selection, IEditorPart editor) {
		if (editor instanceof CEditor) {
			TextSelection textSelection = selection;
			try {
				ICElement element = getInputCElement((CEditor) editor);
				if (element != null) {
					if (selection != null) {
						return SelectionConverter.getElementAtOffset(element, textSelection);
					} else {
						return element;
					}
				}
			} catch (CModelException e) {
				// ignore
			}
		}
		return null;
	}

	@Override
	public String getContentType() {
		return CDTStructureBridge.CONTENT_TYPE;
	}

	/**
	 * The return type of CEditor.getInputCElement was changed from ICElement to IWorkingCopy, breaking binary
	 * compatibility, so we have to call it using reflection.
	 */
	public static ICElement getInputCElement(CEditor editor) {
		try {
			Method getInputCElementMethod = editor.getClass().getMethod("getInputCElement"); //$NON-NLS-1$
			Object result = getInputCElementMethod.invoke(editor);
			return (ICElement) result;
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			}
			throw new RuntimeException(e.getCause());
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, CDTUIBridgePlugin.ID_PLUGIN, e.getMessage(), e));
		}
		return null;
	}

}
