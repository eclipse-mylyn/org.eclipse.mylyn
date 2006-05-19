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

package org.eclipse.mylar.internal.ide;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.mylar.internal.ui.MylarUiPrefContstants;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.IMylarStructureBridge;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.ui.IMylarUiBridge;
import org.eclipse.mylar.provisional.ui.MylarUiPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class InterestManipulatingEditorTracker extends AbstractEditorTracker {

	public static final String SOURCE_ID = "org.eclipse.mylar.ide.editor.tracker.interest";

	@Override
	public void editorOpened(IEditorPart editorPartOpened) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editorReferences = page.getEditorReferences();
		for (int i = 0; i < editorReferences.length; i++) {
			IMylarElement element = null;
			Object adapter;
			IEditorPart editorToClose = editorReferences[i].getEditor(false);
			if (editorToClose != null) {
				adapter = editorToClose.getEditorInput().getAdapter(IResource.class);
				if (adapter instanceof IFile) {
					String handle = MylarPlugin.getDefault().getStructureBridge(adapter).getHandleIdentifier(adapter);
					element = MylarPlugin.getContextManager().getElement(handle);
				}
				if (element != null && !element.getInterest().isInteresting() && !isSameEditor(editorPartOpened, editorToClose)) {
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
		if (MylarUiPlugin.getPrefs().getBoolean(MylarUiPrefContstants.AUTO_MANAGE_EDITORS)
			&& !Workbench.getInstance().getPreferenceStore().getBoolean(IPreferenceConstants.REUSE_EDITORS_BOOLEAN)) {
			IMylarElement element = null;
			IMylarUiBridge uiBridge = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart);
			Object object = uiBridge.getObjectForTextSelection(null, editorPart);
			if (object != null) {
				IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(object);
				element = MylarPlugin.getContextManager().getElement(bridge.getHandleIdentifier(object));
			}
			if (element == null) { // TODO: probably should be refactored into
				// delegation
				Object adapter = editorPart.getEditorInput().getAdapter(IResource.class);
				if (adapter instanceof IResource) {
					IResource resource = (IResource) adapter;
					IMylarStructureBridge resourceBridge = MylarPlugin.getDefault().getStructureBridge(resource);
					element = MylarPlugin.getContextManager().getElement(resourceBridge.getHandleIdentifier(resource));
				}
			}
			if (element != null) {
				MylarPlugin.getContextManager().manipulateInterestForElement(element, false, false, SOURCE_ID);
			}
		}
	}

}
