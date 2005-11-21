/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.ide.internal;

import org.eclipse.core.resources.IResource;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ide.AbstractEditorTracker;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 */
public class InterestManipulatingEditorTracker extends AbstractEditorTracker {

	public static final String SOURCE_ID = "org.eclipse.mylar.ide.editor.tracker.interest";

	@Override
	public void editorOpened(IEditorPart part) {

	}

	@Override
	public void editorClosed(IEditorPart editorPart) {
		if (MylarTasklistPlugin.getPrefs().getBoolean(MylarTasklistPlugin.AUTO_MANAGE_EDITORS)) {
			IMylarElement element = null;
			IMylarUiBridge uiBridge = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart);
			Object object = uiBridge.getObjectForTextSelection(null, editorPart);
			if (object != null) {
				IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(object);
				element = MylarPlugin.getContextManager().getElement(bridge.getHandleIdentifier(object));
			}
			if (element == null) { // TODO: probably should be refactored into delegation
				Object adapter = editorPart.getEditorInput().getAdapter(IResource.class);
				if (adapter instanceof IResource) {
					IResource resource = (IResource)adapter;
					IMylarStructureBridge resourceBridge = MylarPlugin.getDefault().getStructureBridge(resource);
					element = MylarPlugin.getContextManager().getElement(resourceBridge.getHandleIdentifier(resource));
				} 
			}
			if (element != null) {
				MylarPlugin.getContextManager().manipulateInterestForNode(element, false, false, SOURCE_ID);
			}
		}
	}

}
