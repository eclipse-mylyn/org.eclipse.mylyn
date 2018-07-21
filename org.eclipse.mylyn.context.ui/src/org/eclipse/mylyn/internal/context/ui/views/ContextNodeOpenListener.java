/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.views;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.context.ui.ContextUi;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Mik Kersten
 */
public class ContextNodeOpenListener implements IOpenListener, IDoubleClickListener, MouseListener {

	private final Viewer viewer;

	private IInteractionContext context;

	public ContextNodeOpenListener(Viewer viewer) {
		this.viewer = viewer;
	}

	public ContextNodeOpenListener(CommonViewer viewer, IInteractionContext context) {
		this.viewer = viewer;
		this.context = context;
	}

	public void open(OpenEvent event) {
		StructuredSelection selection = (StructuredSelection) viewer.getSelection();
		Object object = selection.getFirstElement();
		IInteractionElement node = null;
		if (object instanceof IInteractionElement) {
			node = (IInteractionElement) object;
		} else if (!(object instanceof IInteractionRelation)) {
			AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(object);
			String handle = bridge.getHandleIdentifier(object);
			if (context == null) {
				node = ContextCore.getContextManager().getElement(handle);
			} else {
				node = context.get(handle);
			}
		}
		if (node != null) {
			ContextUi.getUiBridge(node.getContentType()).open(node);
		}
	}

	public void doubleClick(DoubleClickEvent event) {
		open(null);
	}

	public void mouseDoubleClick(MouseEvent event) {
		setSelection(event);
	}

	public void mouseDown(MouseEvent event) {
		setSelection(event);
	}

	private void setSelection(MouseEvent event) {
		try {
			Object selection = ((Tree) event.getSource()).getSelection()[0].getData();
			viewer.setSelection(new StructuredSelection(selection));
			open(null);
		} catch (Exception e) {
			// ignore
		}
	}

	public void mouseUp(MouseEvent e) {
		// ignore
	}
}
