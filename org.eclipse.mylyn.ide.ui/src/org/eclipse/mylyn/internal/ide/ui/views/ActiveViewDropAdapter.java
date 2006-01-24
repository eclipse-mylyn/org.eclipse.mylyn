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

package org.eclipse.mylar.internal.ide.ui.views;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;

/**
 * @author Mik Kersten
 */
public class ActiveViewDropAdapter extends ViewerDropAdapter {

	public static final String ID_MANIPULATION = "org.eclipse.mylar.ui.views.active.drop.landmark";

	public ActiveViewDropAdapter(Viewer viewer) {
		super(viewer);
		setFeedbackEnabled(false);
	}

	@Override
	public boolean performDrop(Object data) {
		if (data instanceof StructuredSelection) {
			Object firstElement = ((StructuredSelection) data).getFirstElement();
			IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(firstElement);
			String handle = bridge.getHandleIdentifier(firstElement);
			IMylarElement node = MylarPlugin.getContextManager().getElement(handle);
			if (node != null)
				MylarPlugin.getContextManager().manipulateInterestForNode(node, true, true, ID_MANIPULATION);
		}
		return false; // to ensure that the sender doesn't treat this as a
		// move
	}

	@Override
	public boolean validateDrop(Object targetObject, int operation, TransferData transferType) {
		return LocalSelectionTransfer.getInstance().isSupportedType(transferType);
	}
}