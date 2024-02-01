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

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Mik Kersten
 */
public class BrowseFilteredListener implements MouseListener, KeyListener {

	private final StructuredViewer viewer;

	private boolean wasExternalClick = false;

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
		if (filter != null && targetObject != null) {
			filter.addTemporarilyUnfiltered(targetObject);
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
			filter.addTemporarilyUnfiltered(targetObject);
			if (targetObject instanceof Tree) {
				treeViewer.refresh();
			} else {
				treeViewer.refresh(targetObject, true);
				treeViewer.expandToLevel(targetObject, 1);
			}
		}
	}

	public void setWasExternalClick(boolean wasExternalClick) {
		this.wasExternalClick = wasExternalClick;
	}

	@Override
	public void keyPressed(KeyEvent event) {
		// ignore
	}

	@Override
	public void keyReleased(KeyEvent event) {
		InterestFilter filter = getInterestFilter(viewer);

		if (event.keyCode == SWT.ARROW_RIGHT) {
			if (filter == null || !(viewer instanceof final TreeViewer treeViewer)) {
				return;
			}

			ISelection selection = treeViewer.getSelection();
			if (selection instanceof IStructuredSelection) {
				Object targetObject = ((IStructuredSelection) selection).getFirstElement();
				unfilter(filter, treeViewer, targetObject);
			}
		}
	}

	@Override
	public void mouseDown(MouseEvent event) {
		// ignore
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// ignore
	}

	@Override
	public void mouseUp(MouseEvent event) {
		final InterestFilter filter = getInterestFilter(viewer);
		if (filter == null || !(viewer instanceof TreeViewer treeViewer)) {
			return;
		}

		Object selectedObject = null;
		Object clickedObject = getClickedItem(event);
		if (clickedObject != null) {
			selectedObject = clickedObject;
		} else {
			selectedObject = treeViewer.getTree();
		}

		if (isUnfilterEvent(event)) {
			if (treeViewer instanceof CommonViewer commonViewer) {
				commonViewer.setSelection(new StructuredSelection(selectedObject), true);
			}
			unfilter(filter, treeViewer, selectedObject);
		} else if (event.button == 1) {
			if ((event.stateMask & SWT.MOD1) != 0 || wasExternalClick) {
				viewer.refresh(selectedObject);
				wasExternalClick = false;
			} else {
				final Object unfiltered = filter.getLastTemporarilyUnfiltered();
				if (unfiltered != null) {
					// NOTE: delaying refresh to ensure double click is handled, see bug 208702
					resetUnfiltered();
				}
			}
		}
	}

	public void resetUnfiltered() {
		new UIJob("") { //$NON-NLS-1$
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				final InterestFilter filter = getInterestFilter(viewer);
				filter.resetTemporarilyUnfiltered();
				viewer.refresh();
				return Status.OK_STATUS;
			}
		}.schedule(Display.getDefault().getDoubleClickTime() + 50);
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
		for (ViewerFilter filter : filters) {
			if (filter instanceof InterestFilter) {
				return (InterestFilter) filter;
			}
		}
		return null;
	}

	public boolean isUnfiltered(Object object) {
		InterestFilter interestFilter = getInterestFilter(viewer);
		if (interestFilter != null) {
			return interestFilter.isTemporarilyUnfiltered(object);
		}
		return false;
	}
}
