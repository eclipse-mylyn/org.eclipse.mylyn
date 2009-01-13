/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * An outline page for a {@link TaskEditor}.
 * 
 * @author Steffen Pingel
 */
public class TaskEditorOutlinePage extends ContentOutlinePage {

	private static class TaskEditorOutlineContentProvider implements ITreeContentProvider {

		public void dispose() {
			// ignore
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof TaskEditorOutlineNode) {
				Object[] children = ((TaskEditorOutlineNode) parentElement).getChildren();
				return children;
			}
			return new Object[0];
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof TaskEditorOutlineModel) {
				return new Object[] { ((TaskEditorOutlineModel) inputElement).getRoot() };
			}
			return new Object[0];
		}

		public Object getParent(Object element) {
			if (element instanceof TaskEditorOutlineNode) {
				return ((TaskEditorOutlineNode) element).getParent();
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof TaskEditorOutlineNode) {
				return ((TaskEditorOutlineNode) element).getChildren().length > 0;
			}
			return false;
		}

		public void inputChanged(Viewer viewerChanged, Object oldInput, Object newInput) {
			// ignore
		}

	}

	private static class TaskEditorOutlineModel {

		private final TaskEditorOutlineNode root;

		public TaskEditorOutlineModel(TaskEditorOutlineNode root) {
			this.root = root;
		}

		public TaskEditorOutlineNode getRoot() {
			return root;
		}

	}

	private TaskEditorOutlineModel model;

	private TaskRepository taskRepository;

	private TreeViewer viewer;

	public TaskEditorOutlinePage() {
	}

	public void setInput(TaskRepository taskRepository, TaskEditorOutlineNode rootNode) {
		this.taskRepository = taskRepository;
		if (rootNode != null) {
			this.model = new TaskEditorOutlineModel(rootNode);
		} else {
			this.model = null;
		}
		if (viewer != null) {
			viewer.setInput(this.model);
			viewer.refresh(true);
		}
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		viewer = getTreeViewer();
		viewer.setContentProvider(new TaskEditorOutlineContentProvider());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public Image getImage(Object element) {
				if (element instanceof TaskEditorOutlineNode) {
					TaskEditorOutlineNode node = (TaskEditorOutlineNode) element;
					if (TaskEditorOutlineNode.LABEL_COMMENTS.equals(node.getLabel())
							|| TaskEditorOutlineNode.LABEL_NEW_COMMENT.equals(node.getLabel())) {
						return CommonImages.getImage(TasksUiImages.COMMENT);
					}
					if (TaskEditorOutlineNode.LABEL_DESCRIPTION.equals(node.getLabel())) {
						return CommonImages.getImage(TasksUiImages.TASK_NOTES);
					} else if (node.getTaskComment() != null) {
						IRepositoryPerson author = node.getTaskComment().getAuthor();
						if (taskRepository != null && author != null
								&& author.getPersonId().equals(taskRepository.getUserName())) {
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
				if (element instanceof TaskEditorOutlineNode) {
					TaskEditorOutlineNode node = (TaskEditorOutlineNode) element;
					return node.getLabel();
				}
				return super.getText(element);
			}
		});
		viewer.setInput(model);
		viewer.expandAll();
	}

}
