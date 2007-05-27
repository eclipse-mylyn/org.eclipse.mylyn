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

package org.eclipse.mylar.internal.context.ui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylar.context.ui.InterestFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.navigator.CommonViewer;

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
		InterestFilter filter = getInterestFilter(viewer);

		if (event.keyCode == SWT.ARROW_RIGHT) {
			if (filter == null || !(viewer instanceof TreeViewer)) {
				return;
			}

			final TreeViewer treeViewer = (TreeViewer) viewer;
			ISelection selection = treeViewer.getSelection();
			if (selection instanceof IStructuredSelection) {
				Object targetObject = ((IStructuredSelection) selection).getFirstElement();
				unfilter(filter, treeViewer, targetObject);
			}
		}
		// final InterestFilter filter = getFilter(viewer);
		// if (filter == null || !(viewer instanceof TreeViewer)) {
		// return;
		// }
		//
		// if (isUnfilterEvent(event)) {
		// final TreeViewer treeViewer = (TreeViewer) viewer;
		// ISelection selection = treeViewer.getSelection();
		// if (selection instanceof IStructuredSelection) {
		// Object targetObject = ((IStructuredSelection)
		// selection).getFirstElement();
		// unfilter(filter, treeViewer, targetObject);
		// }
		// } else if (event.keyCode != SWT.ARROW_DOWN && event.keyCode !=
		// SWT.ARROW_UP) {
		// if (filter.resetTemporarilyUnfiltered()) {
		// viewer.refresh(false);
		// }
		// }
	}

	public void mouseDown(MouseEvent event) {
		final InterestFilter filter = getInterestFilter(viewer);
		if (filter == null || !(viewer instanceof TreeViewer)) {
			return;
		}

		if (isUnfilterEvent(event)) {
			final TreeViewer treeViewer = (TreeViewer) viewer;
			ISelection selection = treeViewer.getSelection();
			if (selection instanceof IStructuredSelection) {
				Object selectedObject = null;
				Object clickedObject = getClickedItem(event);
				if (clickedObject != null) {
					selectedObject = clickedObject;
					// selectedObject = ((IStructuredSelection)selection).getFirstElement();
				} else {
					selectedObject = treeViewer.getTree();
				}

				if (treeViewer instanceof CommonViewer) {
					CommonViewer commonViewer = (CommonViewer) treeViewer;
					commonViewer.setSelection(new StructuredSelection(selectedObject), true);
				}

				unfilter(filter, treeViewer, selectedObject);
			}
		} else {
			filter.resetTemporarilyUnfiltered();
			// if (filter.resetTemporarilyUnfiltered()) {
			// viewer.refresh(false);
			// }
		}
	}

	private Object getClickedItem(MouseEvent event) {
		if (event.getSource() instanceof Table) {
			TableItem item = ((Table) event.getSource()).getItem(new Point(event.x, event.y));
			if (item != null) {
				return item.getData();
			} else {
				return null;
			}
		} else if (event.getSource() instanceof Tree) {
			TreeItem item = ((Tree) event.getSource()).getItem(new Point(event.x, event.y));
			if (item != null) {
				return item.getData();
			} else {
				return null;
			}
		}
		return null;
	}

	public static boolean isUnfilterEvent(MouseEvent event) {
		return (event.stateMask & SWT.ALT) != 0;
	}

	private InterestFilter getInterestFilter(StructuredViewer structuredViewer) {
		ViewerFilter[] filters = structuredViewer.getFilters();
		for (int i = 0; i < filters.length; i++) {
			if (filters[i] instanceof InterestFilter)
				return (InterestFilter) filters[i];
		}
		return null;
	}

	public void mouseUp(MouseEvent e) {
		// ignore
	}

	public void mouseDoubleClick(MouseEvent e) {
	}

}
