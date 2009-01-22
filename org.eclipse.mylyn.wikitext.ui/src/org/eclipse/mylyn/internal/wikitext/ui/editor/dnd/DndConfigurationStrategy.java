/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor.dnd;

import org.eclipse.jface.util.DelegatingDragAdapter;
import org.eclipse.jface.util.DelegatingDropAdapter;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.mylyn.internal.wikitext.ui.editor.operations.CommandManager;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author David Green
 */
public class DndConfigurationStrategy {

	private DelegatingDragAdapter dragAdapter;

	private DragSource dragSource;

	private DelegatingDropAdapter dropAdapter;

	private DropTarget dropTarget;

	public void configure(CommandManager commandManager, Control control, ISelectionProvider provider) {

		dragAdapter = new DelegatingDragAdapter();
		dragSource = new DragSource(control, DND.DROP_COPY | DND.DROP_MOVE);
		dragAdapter.addDragSourceListener(new OutlineDragSourceListener(provider));
		dragSource.addDragListener(dragAdapter);
		dragSource.setTransfer(dragAdapter.getTransfers());

		dropAdapter = new DelegatingDropAdapter();
		dropTarget = new DropTarget(control, DND.DROP_COPY | DND.DROP_MOVE);
		dropAdapter.addDropTargetListener(new OutlineDropTargetListener(commandManager));
		dropTarget.addDropListener(dropAdapter);
		dropTarget.setTransfer(dropAdapter.getTransfers());
	}

	public void dispose() {
		if (dragSource != null) {
			dragSource.dispose();
			dragSource = null;
		}
		if (dropTarget != null) {
			dropTarget.dispose();
			dropTarget = null;
		}
	}
}
