/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskComment;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public class RepositoryTaskOutlinePage extends ContentOutlinePage {

	private final RepositoryTaskOutlineNode topTreeNode;

	private final TaskRepository repository;

	protected final ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (selection instanceof IStructuredSelection) {
				if (((IStructuredSelection) selection).getFirstElement() instanceof IRepositoryTaskSelection) {
					if (((IStructuredSelection) getSelection()).getFirstElement() instanceof IRepositoryTaskSelection) {
						IRepositoryTaskSelection brs1 = (IRepositoryTaskSelection) ((IStructuredSelection) getSelection()).getFirstElement();
						IRepositoryTaskSelection brs2 = ((IRepositoryTaskSelection) ((IStructuredSelection) selection).getFirstElement());
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
		repository = TasksUi.getRepositoryManager().getRepository(topTreeNode.getConnectorKind(),
				topTreeNode.getRepositoryUrl());
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

					if (RepositoryTaskOutlineNode.LABEL_COMMENTS.equals(node.getContents())
							|| RepositoryTaskOutlineNode.LABEL_NEW_COMMENT.equals(node.getContents())) {
						return CommonImages.getImage(TasksUiImages.COMMENT);
					}
					if (RepositoryTaskOutlineNode.LABEL_DESCRIPTION.equals(node.getContents())) {
						return CommonImages.getImage(TasksUiImages.TASK_NOTES);
					} else if (node.getComment() != null) {
						if (repository != null && node.getComment().getAuthor().equals(repository.getUserName())) {
							return CommonImages.getImage(CommonImages.PERSON_ME);
						} else {
							return CommonImages.getImage(CommonImages.PERSON);
						}
					} else {
						return CommonImages.getImage(TasksUiImages.TASK);
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

					String name = comment.getAuthorName();
					if (name != null) {
						String id = comment.getAuthor();
						if (id != null) {
							name += " <" + id + ">";
						}
					} else {
						name = comment.getAuthor();
					}

					return n + ": " + name + " (" + node.getName() + ")";
				}
				return super.getText(element);
			}
		});
		try {
			viewer.setInput(topTreeNode);
			viewer.setComparer(new RepositoryTaskOutlineComparer());
			viewer.expandAll();
		} catch (Exception e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not create bugzilla outline",
					e));
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
