/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Feb 17, 2005
 */
package org.eclipse.mylyn.internal.context.ui.views;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Mik Kersten
 */
public class ContextNodeOpenListener implements IOpenListener, IDoubleClickListener, MouseListener {

	private final Viewer viewer;

	public ContextNodeOpenListener(Viewer viewer) {
		this.viewer = viewer;
	}

	public void open(OpenEvent event) {
		StructuredSelection selection = (StructuredSelection) viewer.getSelection();
		Object object = selection.getFirstElement();
		IInteractionElement node = null;
		if (object instanceof IInteractionElement) {
			node = (IInteractionElement) object;
		} else if (!(object instanceof IInteractionRelation)) {
			AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(object);
			String handle = bridge.getHandleIdentifier(object);
			node = ContextCore.getContextManager().getElement(handle);
		}
		if (node != null) {
			ContextUiPlugin.getDefault().getUiBridge(node.getContentType()).open(node);
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
