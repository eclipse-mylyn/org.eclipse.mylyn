/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * An outline page for a {@link TaskEditor}.
 * 
 * @author Steffen Pingel
 */
public class TaskEditorOutlinePage extends ContentOutlinePage {

	private TaskEditorOutlineModel model;

	private TreeViewer viewer;

	public TaskEditorOutlinePage() {
	}

	public void setInput(TaskRepository taskRepository, TaskEditorOutlineNode rootNode) {
		if (rootNode != null) {
			this.model = new TaskEditorOutlineModel(rootNode);
		} else {
			this.model = null;
		}
		if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
			viewer.setInput(this.model);
			viewer.expandAll();
			viewer.refresh(true);
		}
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		viewer = getTreeViewer();
		viewer.setContentProvider(new TaskEditorOutlineContentProvider());
		viewer.setLabelProvider(new TaskEditorOutlineNodeLabelProvider());
		viewer.setInput(model);
		viewer.expandAll();
	}

}
