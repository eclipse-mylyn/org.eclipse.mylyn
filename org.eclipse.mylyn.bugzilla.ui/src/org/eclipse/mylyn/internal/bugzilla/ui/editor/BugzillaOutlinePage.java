/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.ui.editor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaTools;
import org.eclipse.mylar.internal.bugzilla.ui.IBugzillaReportSelection;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * An outline page for a <code>BugEditor</code>.
 */
public class BugzillaOutlinePage extends ContentOutlinePage {

	private BugzillaOutlineNode topTreeNode;

	protected final ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if ((part instanceof AbstractBugEditor) && (selection instanceof IStructuredSelection)) {
				if (((IStructuredSelection) selection).getFirstElement() instanceof IBugzillaReportSelection) {
					if (((IStructuredSelection) getSelection()).getFirstElement() instanceof IBugzillaReportSelection) {
						IBugzillaReportSelection brs1 = (IBugzillaReportSelection) ((IStructuredSelection) getSelection())
								.getFirstElement();
						IBugzillaReportSelection brs2 = ((IBugzillaReportSelection) ((IStructuredSelection) selection)
								.getFirstElement());
						if (BugzillaTools.getHandle(brs1).compareTo(BugzillaTools.getHandle(brs2)) == 0) {
							// don't need to make a selection for the same
							// element
							return;
						}
					}
					getTreeViewer().setSelection(selection, true);
				}
			}
		}
	};

	private TreeViewer viewer;

	/**
	 * Creates a new <code>BugzillaOutlinePage</code>.
	 * 
	 * @param topTreeNode
	 *            The top data node of the tree for this view.
	 * @param editor
	 *            The editor this outline page is for.
	 */
	public BugzillaOutlinePage(BugzillaOutlineNode topTreeNode) {
		super();
		this.topTreeNode = topTreeNode;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		viewer = getTreeViewer();
		viewer.setContentProvider(new BugTaskOutlineContentProvider());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public Image getImage(Object element) {
				if (element instanceof BugzillaOutlineNode) {
					BugzillaOutlineNode node = (BugzillaOutlineNode) element;
					if (node.getComment() != null) {
						return node.getImage();
					} else {
						return BugzillaImages.getImage(BugzillaImages.BUG);
					}
				} else {
					return super.getImage(element);
				}
			}

			@Override
			public String getText(Object element) {
				if (element instanceof BugzillaOutlineNode) {
					BugzillaOutlineNode node = (BugzillaOutlineNode) element;
					if (node.getComment() != null) {
						return node.getComment().getAuthorName() + " (" + node.getName() + ")";
					} else {
						return node.getName();
					}
				}
				return super.getText(element);
			}
		});
		try {
			viewer.setInput(topTreeNode);
			viewer.setComparer(new BugzillaOutlineComparer());
			viewer.expandAll();
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "could not create bugzilla outline", true);
		}
		getSite().getPage().addSelectionListener(selectionListener);
	}

	@Override
	public void dispose() {
		super.dispose();
		getSite().getPage().removeSelectionListener(selectionListener);
	}

	public TreeViewer getOutlineTreeViewer() {
		return viewer;
	}

	/**
	 * A content provider for the tree for this view.
	 * 
	 * @see ITreeContentProvider
	 */
	protected class BugTaskOutlineContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof BugzillaOutlineNode) {
				Object[] children = ((BugzillaOutlineNode) parentElement).getChildren();
				if (children.length > 0) {
					return children;
				}
			}
			return new Object[0];
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof BugzillaOutlineNode) {
				return ((BugzillaOutlineNode) element).getChildren().length > 0;
			}
			return false;
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof BugzillaOutlineNode) {
				Object[] children = ((BugzillaOutlineNode) inputElement).getChildren();
				if (children.length > 0) {
					return children;
				}
			}
			return new Object[0];
		}

		public void dispose() {
			// don't care when we are disposed
		}

		public void inputChanged(Viewer viewerChanged, Object oldInput, Object newInput) {
			// don't care when the input changes
		}
	}

}
