/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
