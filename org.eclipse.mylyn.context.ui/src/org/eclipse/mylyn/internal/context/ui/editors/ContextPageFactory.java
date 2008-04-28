/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.editors;

import org.eclipse.mylyn.context.ui.ContextUi;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.FormPage;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class ContextPageFactory extends AbstractTaskEditorPageFactory {

	private static final String LABEL = "Context";

	@Override
	public boolean canCreatePageFor(TaskEditorInput input) {
		return true;
	}

	@Override
	public FormPage createPage(TaskEditor parentEditor) {
		return new ContextEditorFormPage(parentEditor, ContextUi.ID_CONTEXT_PAGE, LABEL);
	}

	@Override
	public int getPriority() {
		return PRIORITY_CONTEXT;
	}

	@Override
	public Image getPageImage() {
		return TasksUiImages.getImage(TasksUiImages.TASK_ACTIVE_CENTERED);
	}

	@Override
	public String getPageText() {
		return LABEL;
	}

}
