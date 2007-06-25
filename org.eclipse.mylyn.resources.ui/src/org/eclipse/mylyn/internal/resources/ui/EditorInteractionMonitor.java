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

package org.eclipse.mylyn.internal.resources.ui;

import org.eclipse.compare.internal.CompareEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.internal.context.ui.ContextUiPrefContstants;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.ui.AbstractEditorTracker;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class EditorInteractionMonitor extends AbstractEditorTracker {

	public static final String SOURCE_ID = "org.eclipse.mylyn.ide.editor.tracker.interest";

	@Override
	protected void editorBroughtToTop(IEditorPart part) {
		Object object = part.getEditorInput().getAdapter(IResource.class);
		if (object instanceof IResource) {
			IResource resource = (IResource) object;
			AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(resource);
			InteractionEvent selectionEvent = new InteractionEvent(InteractionEvent.Kind.SELECTION,
					bridge.getContentType(), bridge.getHandleIdentifier(resource), part.getSite().getId());
			ContextCorePlugin.getContextManager().processInteractionEvent(selectionEvent);
		}
	}

	@Override
	public void editorOpened(IEditorPart editorPartOpened) {
		IWorkbenchPage page = editorPartOpened.getSite().getPage();
		IEditorReference[] editorReferences = page.getEditorReferences();
		for (int i = 0; i < editorReferences.length; i++) {
			IInteractionElement element = null;
			Object adapter;
			IEditorPart editorToClose = editorReferences[i].getEditor(false);
			if (editorToClose != null) {
				adapter = editorToClose.getEditorInput().getAdapter(IResource.class);
				if (adapter instanceof IFile) {
					String handle = ContextCorePlugin.getDefault().getStructureBridge(adapter).getHandleIdentifier(
							adapter);
					element = ContextCorePlugin.getContextManager().getElement(handle);
				}
				if (!ContextCorePlugin.getContextManager().isContextCapturePaused() && element != null
						&& !element.getInterest().isInteresting() && !isSameEditor(editorPartOpened, editorToClose)) {
					page.closeEditor(editorToClose, true);
				}
			}
		}
	}

	private boolean isSameEditor(IEditorPart editorPart1, IEditorPart editorPart2) {
		if (editorPart1 == null || editorPart2 == null) {
			return false;
		} else {
			return editorPart1.equals(editorPart2) && editorPart1.getEditorInput().equals(editorPart2.getEditorInput());
		}
	}

	@Override
	public void editorClosed(IEditorPart editorPart) {
		if (PlatformUI.getWorkbench().isClosing()) {
			return;
		} else if (ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
				ContextUiPrefContstants.AUTO_MANAGE_EDITORS)
				&& !Workbench.getInstance().getPreferenceStore().getBoolean(IPreferenceConstants.REUSE_EDITORS_BOOLEAN)
				&& !otherEditorsOpenForResource(editorPart)
				&& !(editorPart instanceof CompareEditor)
				&& !(editorPart instanceof IContextAwareEditor)) {
			IInteractionElement element = null;
			AbstractContextUiBridge uiBridge = ContextUiPlugin.getDefault().getUiBridgeForEditor(editorPart);
			Object object = uiBridge.getObjectForTextSelection(null, editorPart);
			if (object != null) {
				AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(object);
				element = ContextCorePlugin.getContextManager().getElement(bridge.getHandleIdentifier(object));
			}
			// TODO: probably should be refactored into delegation
			if (element == null) {
				Object adapter = editorPart.getEditorInput().getAdapter(IResource.class);
				if (adapter instanceof IResource) {
					IResource resource = (IResource) adapter;
					AbstractContextStructureBridge resourceBridge = ContextCorePlugin.getDefault().getStructureBridge(
							resource);
					element = ContextCorePlugin.getContextManager().getElement(
							resourceBridge.getHandleIdentifier(resource));
				}
			}
			if (element != null) {
				ContextCorePlugin.getContextManager().manipulateInterestForElement(element, false, false, SOURCE_ID);
			}
		}
	}

	private boolean otherEditorsOpenForResource(IEditorPart editorPart) {
		Object adapter = editorPart.getEditorInput().getAdapter(IResource.class);
		if (adapter instanceof IResource) {
			IResource resource = (IResource) adapter;
			IWorkbenchPage page = editorPart.getSite().getPage();
			IEditorReference[] editorReferences = page.getEditorReferences();
			for (int i = 0; i < editorReferences.length; i++) {
				Object otherAdapter;
				IEditorPart otherEditor = editorReferences[i].getEditor(false);
				if (otherEditor != null) {
					otherAdapter = otherEditor.getEditorInput().getAdapter(IResource.class);
					if (otherAdapter instanceof IResource && otherAdapter.equals(resource)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
