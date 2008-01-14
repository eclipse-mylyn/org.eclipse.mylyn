/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
// TODO EDITOR add API for section toolbars
public abstract class AbstractTaskEditorPart extends AbstractFormPart {

	// XXX why is this required?
	protected static final Font TEXT_FONT = JFaceResources.getDefaultFont();

	private RepositoryTaskData taskData;

	private Control control;

	private TaskRepository taskRepository;

	private AbstractRepositoryConnector connector;

	private final AbstractTaskEditorPage taskEditorPage;
	
	public AbstractTaskEditorPart(AbstractTaskEditorPage taskEditorPage) {
		this.taskEditorPage = taskEditorPage;
	}

	public abstract void createControl(Composite parent, FormToolkit toolkit);

	public Control getControl() {
		return control;
	}

	public AbstractRepositoryConnector getConnector() {
		return connector;
	}
	
	public RepositoryTaskData getTaskData() {
		return taskData;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}
	
	// TODO EDITOR review if this is required
	public TaskEditor getTaskEditor() {
		return getTaskEditorPage().getParentEditor();
	}
	
	public AbstractTaskEditorPage getTaskEditorPage() {
		return taskEditorPage;
	}
	
	public void setControl(Control control) {
		this.control = control;
	}

	public void setInput(AbstractRepositoryConnector connector, TaskRepository taskRepository, RepositoryTaskData taskData) {
		this.connector = connector;
		this.taskRepository = taskRepository;
		this.taskData = taskData;
	}

	protected void fillToolBar(ToolBarManager toolBarManager) {
	}

}
