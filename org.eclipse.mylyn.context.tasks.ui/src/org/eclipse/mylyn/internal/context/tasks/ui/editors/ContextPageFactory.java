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

package org.eclipse.mylyn.internal.context.tasks.ui.editors;

import org.eclipse.mylyn.commons.ui.CommonImages;
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

	public static String ID_CONTEXT_PAGE_FACTORY = "org.eclipse.mylyn.context.ui.editor.context"; //$NON-NLS-1$

	@Override
	public boolean canCreatePageFor(TaskEditorInput input) {
		return true;
	}

	@Override
	public IFormPage createPage(TaskEditor parentEditor) {
		return new ContextEditorFormPage(parentEditor, ID_CONTEXT_PAGE_FACTORY, Messages.ContextPageFactory_Context);
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
		return Messages.ContextPageFactory_Context;
	}

}
