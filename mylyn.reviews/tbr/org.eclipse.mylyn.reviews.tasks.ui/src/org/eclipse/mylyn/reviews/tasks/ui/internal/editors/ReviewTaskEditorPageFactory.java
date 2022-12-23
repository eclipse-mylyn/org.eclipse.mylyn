/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.ui.internal.editors;

import org.eclipse.mylyn.reviews.tasks.ui.internal.Images;
import org.eclipse.mylyn.reviews.tasks.ui.internal.Messages;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * @author Kilian Matt
 */
public class ReviewTaskEditorPageFactory extends AbstractTaskEditorPageFactory {

	@Override
	public boolean canCreatePageFor(TaskEditorInput input) {
		AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(input.getTask().getConnectorKind());
		return connector != null && connector.isUserManaged();
	}

	@Override
	public IFormPage createPage(TaskEditor parentEditor) {
		return new ReviewTaskEditorPage(parentEditor);
	}

	@Override
	public Image getPageImage() {
		return Images.SMALL_ICON.createImage();
	}

	@Override
	public String getPageText() {
		return Messages.ReviewTaskEditorPageFactory_PageTitle;
	}

	@Override
	public int getPriority() {
		return PRIORITY_ADDITIONS;
	}

	@Override
	public String[] getConflictingIds(TaskEditorInput input) {
		return new String[0];
	}
}
