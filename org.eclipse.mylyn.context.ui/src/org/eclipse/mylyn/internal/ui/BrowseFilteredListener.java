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

package org.eclipse.mylar.internal.ui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylar.provisional.ui.InterestFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Mik Kersten
 */
public class BrowseFilteredListener implements MouseListener, KeyListener {

	private StructuredViewer viewer;

	public BrowseFilteredListener(StructuredViewer viewer) {
		this.viewer = viewer;
	}

	private void unfilter(final InterestFilter filter, final TreeViewer treeViewer, Object targetObject) {
		if (targetObject != null) {
			filter.setTemporarilyUnfiltered(targetObject);
			if (targetObject instanceof Tree) {
				treeViewer.refresh();
			} else { 
				treeViewer.refresh(targetObject, true);
				treeViewer.expandToLevel(targetObject, 1);
			}
		}
	}

	public void keyPressed(KeyEvent event) {
		// ignore
	}

	public void keyReleased(KeyEvent event) {
		final InterestFilter filter = getFilter(viewer);
		if (filter == null || !(viewer instanceof TreeViewer)) {
			return;
		}

		if (isUnfilterEvent(event)) {
			final TreeViewer treeViewer = (TreeViewer) viewer;
			ISelection selection = treeViewer.getSelection();
			if (selection instanceof IStructuredSelection) {
				Object targetObject = ((IStructuredSelection) selection).getFirstElement();
				unfilter(filter, treeViewer, targetObject);
			}
		} else if (event.keyCode != SWT.ARROW_DOWN && event.keyCode != SWT.ARROW_UP) {
			if (filter.resetTemporarilyUnfiltered()) {
				viewer.refresh(false);
			}
		}
	}

	public void mouseDown(MouseEvent event) {
		final InterestFilter filter = getFilter(viewer);
		if (filter == null || !(viewer instanceof TreeViewer)) {
			return;
		}

		if (isUnfilterEvent(event)) {
			final TreeViewer treeViewer = (TreeViewer) viewer;
			ISelection selection = treeViewer.getSelection();
			if (selection instanceof IStructuredSelection) {
				Object targetObject = null;
				if (getClickedItem(event) != null) {
					targetObject = ((IStructuredSelection) selection).getFirstElement();
				} else {// if (treeViewer.getTree().getTopItem() != null) {
					targetObject = treeViewer.getTree();
				} 
				unfilter(filter, treeViewer, targetObject);
			}
		} else {
			if (filter.resetTemporarilyUnfiltered()) {
				viewer.refresh(false);
			}
		}
	}

	private Object getClickedItem(MouseEvent event) {
		if (event.getSource() instanceof Table) {
			return ((Table) event.getSource()).getItem(new Point(event.x, event.y));
		} else if (event.getSource() instanceof Tree) {
			return ((Tree) event.getSource()).getItem(new Point(event.x, event.y));
		}
		return null;
	}

	private boolean isUnfilterEvent(MouseEvent event) {
		return (event.stateMask & SWT.ALT) != 0;
	}
	
	private boolean isUnfilterEvent(KeyEvent event) {
		return (event.keyCode == SWT.ARROW_RIGHT) && (event.stateMask == SWT.ALT);
	}
	
	private InterestFilter getFilter(StructuredViewer structuredViewer) {
		ViewerFilter[] filters = structuredViewer.getFilters();
		for (int i = 0; i < filters.length; i++) {
			if (filters[i] instanceof InterestFilter)
				return (InterestFilter) filters[i];
		}
		return null;
	}

	public void mouseUp(MouseEvent e) {
	}

	public void mouseDoubleClick(MouseEvent e) {
	}

}
