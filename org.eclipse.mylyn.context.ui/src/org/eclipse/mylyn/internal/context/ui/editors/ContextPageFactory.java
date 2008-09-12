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

package org.eclipse.mylyn.internal.context.ui.editors;

import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.IFormPage;

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
	public IFormPage createPage(TaskEditor parentEditor) {
		return new ContextEditorFormPage(parentEditor, ContextUiPlugin.ID_CONTEXT_PAGE, LABEL);
	}

	@Override
	public int getPriority() {
		return PRIORITY_CONTEXT;
	}

	@Override
	public Image getPageImage() {
		return CommonImages.getImage(TasksUiImages.CONTEXT_ACTIVE_CENTERED);
	}

	@Override
	public String getPageText() {
		return LABEL;
	}

}
