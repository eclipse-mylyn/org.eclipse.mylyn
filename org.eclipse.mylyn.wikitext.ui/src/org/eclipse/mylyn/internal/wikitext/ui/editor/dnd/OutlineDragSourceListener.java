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

package org.eclipse.mylyn.internal.wikitext.ui.editor.dnd;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;

/**
 * 
 * @author David Green
 */
public class OutlineDragSourceListener implements TransferDragSourceListener {
	private final ISelectionProvider selectionProvider;

	public OutlineDragSourceListener(ISelectionProvider selectionProvider) {
		this.selectionProvider = selectionProvider;
	}

	public Transfer getTransfer() {
		return LocalSelectionTransfer.getTransfer();
	}

	public void dragFinished(DragSourceEvent event) {
		LocalSelectionTransfer.getTransfer().setSelection(null);
	}

	public void dragSetData(DragSourceEvent event) {
	}

	public void dragStart(DragSourceEvent event) {
		LocalSelectionTransfer.getTransfer().setSelection(selectionProvider.getSelection());
	}
}
