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
package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskOutlineNode;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * An outline page for a <code>BugEditor</code>.
 */
public class RepositoryTaskOutlinePage extends ContentOutlinePage {

	private RepositoryTaskOutlineNode topTreeNode;

	protected final ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if ((part instanceof AbstractRepositoryTaskEditor) && (selection instanceof IStructuredSelection)) {
				if (((IStructuredSelection) selection).getFirstElement() instanceof IRepositoryTaskSelection) {
					if (((IStructuredSelection) getSelection()).getFirstElement() instanceof IRepositoryTaskSelection) {
						IRepositoryTaskSelection brs1 = (IRepositoryTaskSelection) ((IStructuredSelection) getSelection())
								.getFirstElement();
						IRepositoryTaskSelection brs2 = ((IRepositoryTaskSelection) ((IStructuredSelection) selection)
								.getFirstElement());
						if (ContentOutlineTools.getHandle(brs1).compareTo(ContentOutlineTools.getHandle(brs2)) == 0) {
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
	 * Creates a new <code>RepositoryTaskOutlinePage</code>.
	 *
	 * @param topTreeNode
	 *            The top data node of the tree for this view.
	 * @param editor
	 *            The editor this outline page is for.
	 */
	public RepositoryTaskOutlinePage(RepositoryTaskOutlineNode topTreeNode) {
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
				if (element instanceof RepositoryTaskOutlineNode) {
					RepositoryTaskOutlineNode node = (RepositoryTaskOutlineNode) element;

					if (RepositoryTaskOutlineNode.LABEL_COMMENTS.equals(node.getContents())||
						RepositoryTaskOutlineNode.LABEL_NEW_COMMENT.equals(node.getContents())) {
						return TasksUiImages.getImage(TasksUiImages.COMMENT);
					} if (RepositoryTaskOutlineNode.LABEL_DESCRIPTION.equals(node.getContents())) {
						return TasksUiImages.getImage(TasksUiImages.TASK_NOTES);
					} else if (node.getComment() != null) {
						return TasksUiImages.getImage(TasksUiImages.PERSON);
					} else {
						return TasksUiImages.getImage(TasksUiImages.TASK_REPOSITORY);
					}
				} else {
					return super.getImage(element);
				}
			}

			@Override
			public String getText(Object element) {
				if (element instanceof RepositoryTaskOutlineNode) {
					RepositoryTaskOutlineNode node = (RepositoryTaskOutlineNode) element;
					TaskComment comment = node.getComment();
					if (comment == null) {
						return node.getName();
					}
					int n = comment.getNumber();
//					if (n == 0) {
//						return comment.getAuthorName() + " (" + node.getName() + ")";
//					}
					return n + ": " + comment.getAuthorName() + " (" + node.getName() + ")";
				}
				return super.getText(element);
			}
		});
		try {
			viewer.setInput(topTreeNode);
			viewer.setComparer(new RepositoryTaskOutlineComparer());
			viewer.expandAll();
		} catch (Exception e) {
			StatusManager.fail(e, "could not create bugzilla outline", true);
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
	protected static class BugTaskOutlineContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof RepositoryTaskOutlineNode) {
				Object[] children = ((RepositoryTaskOutlineNode) parentElement).getChildren();
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
			if (element instanceof RepositoryTaskOutlineNode) {
				return ((RepositoryTaskOutlineNode) element).getChildren().length > 0;
			}
			return false;
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof RepositoryTaskOutlineNode) {
				Object[] children = ((RepositoryTaskOutlineNode) inputElement).getChildren();
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
