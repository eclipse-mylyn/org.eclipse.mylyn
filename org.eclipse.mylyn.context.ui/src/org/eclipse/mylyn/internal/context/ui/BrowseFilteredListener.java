/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.context.ui.InterestFilter;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Mik Kersten
 */
public class BrowseFilteredListener implements MouseListener, KeyListener {

	private StructuredViewer viewer;

	public BrowseFilteredListener(StructuredViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * @param treeViewer
	 *            cannot be null
	 * @param targetSelection
	 *            cannot be null
	 */
	public void unfilterSelection(TreeViewer treeViewer, IStructuredSelection targetSelection) {
		InterestFilter filter = getInterestFilter(treeViewer);
		Object targetObject = targetSelection.getFirstElement();
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
	}

	public void mouseDown(MouseEvent event) {
		// ignore
	}

	public void mouseDoubleClick(MouseEvent e) {
		// ignore
	}
	
	public void mouseUp(MouseEvent event) {
		final InterestFilter filter = getInterestFilter(viewer);
		if (filter == null || !(viewer instanceof TreeViewer)) {
			return;
		}

		TreeViewer treeViewer = (TreeViewer) viewer;
		Object selectedObject = null;
		Object clickedObject = getClickedItem(event);
		if (clickedObject != null) {
			selectedObject = clickedObject;
		} else {
			selectedObject = treeViewer.getTree();
		}

		if (isUnfilterEvent(event)) {
			if (treeViewer instanceof CommonViewer) {
				CommonViewer commonViewer = (CommonViewer) treeViewer;
				commonViewer.setSelection(new StructuredSelection(selectedObject), true);
			}

			unfilter(filter, treeViewer, selectedObject);
		} else {
			if (event.button == 1) {
				if ((event.stateMask & SWT.MOD1) != 0) {
					viewer.setSelection(new StructuredSelection(selectedObject));
					viewer.refresh(selectedObject);
				} else {
					final Object unfiltered = filter.getTemporarilyUnfiltered();
					if (unfiltered != null) {
						filter.resetTemporarilyUnfiltered();
						// NOTE: need to set selection otherwise it will be missed
						viewer.setSelection(new StructuredSelection(selectedObject));
						
						// TODO: using asyncExec so that viewer has chance to open
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

							public void run() {
								viewer.refresh(unfiltered);	
							}
						});
					}
				}
			}
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
}
