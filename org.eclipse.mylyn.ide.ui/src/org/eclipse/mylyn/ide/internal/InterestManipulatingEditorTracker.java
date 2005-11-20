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
import org.eclipse.ui.IEditorInput;
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
	public void editorClosed(IEditorPart part) {
		IEditorInput input = part.getEditorInput();
		Object adapter = input.getAdapter(IResource.class);
		if (adapter instanceof IResource) {
			IResource resource = (IResource)adapter;
			IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(resource);
			
			IMylarElement element = MylarPlugin.getContextManager().getElement(bridge.getHandleIdentifier(resource));
			if (element != null) {
				MylarPlugin.getContextManager().manipulateInterestForNode(
						element, false, false, SOURCE_ID);
			}
		}
	}

}
