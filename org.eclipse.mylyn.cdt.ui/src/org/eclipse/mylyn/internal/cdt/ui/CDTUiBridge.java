/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 		Red Hat Inc. - Initial implementation based on JavaUiBridge
 *******************************************************************************/

package org.eclipse.cdt.mylyn.internal.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.ui.actions.SelectionConverter;
import org.eclipse.cdt.internal.ui.editor.AbstractCModelOutlinePage;
import org.eclipse.cdt.internal.ui.editor.CContentOutlinePage;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.internal.ui.util.EditorUtility;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
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
 * @author Jeff Johnston
 */
public class CDTUiBridge extends AbstractContextUiBridge {

	private Field cOutlineField = null;

	public CDTUiBridge() {
		try {
			cOutlineField = AbstractCModelOutlinePage.class.getDeclaredField("fTreeViewer");
			cOutlineField.setAccessible(true);
		} catch (Exception e) {
			StatusHandler.fail(e, CDTUIBridgePlugin.getResourceString("MylynCDT.outlineViewerFailure"), true); // $NON-NLS-1$
		}
	}

	@Override
	public void open(IInteractionElement node) {
		try {
			ICElement cElement = CDTStructureBridge.getElementForHandle(node.getHandleIdentifier());
			if (cElement == null || !cElement.exists())
				return;
			IEditorPart part = EditorUtility.openInEditor(cElement);
		} catch (Throwable t) {
			StatusHandler.fail(t, CDTUIBridgePlugin.getFormattedString("MylynCDT.openEditorFailure", new String[]{node.toString()}), true); // $NON-NLS-1$
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
		} catch (Throwable t) {
			StatusHandler.fail(t, CDTUIBridgePlugin.getResourceString("MylynCDT.autoCloseEditorFailure"), false); // $NON-NLS-1$
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
			String handle = ContextCorePlugin.getDefault().getStructureBridge(cElement).getHandleIdentifier(
					cElement);
			return ContextCorePlugin.getContextManager().getElement(handle);
		} else {
			return null;
		}
	}

	@Override
	public List<TreeViewer> getContentOutlineViewers(IEditorPart editorPart) {
		if (editorPart == null || cOutlineField == null)
			return null;
		List<TreeViewer> viewers = new ArrayList<TreeViewer>();
		Object out = editorPart.getAdapter(IContentOutlinePage.class);
		if (out instanceof CContentOutlinePage) {
			CContentOutlinePage page = (CContentOutlinePage) out;
			if (page.getControl() != null) {
				try {
					viewers.add((TreeViewer) cOutlineField.get(page));
				} catch (Exception e) {
					StatusHandler.log(e, CDTUIBridgePlugin.getResourceString("MylynCDT.log.getOutlineViewerFailure")); // $NON-NLS-1$
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
				if (selection != null) {
					return SelectionConverter.getElementAtOffset(((CEditor) editor).getInputCElement(), textSelection);
				} else {
					Object element = ((CEditor) editor).getInputCElement();
					if (element instanceof ICElement)
						return element;
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

}
