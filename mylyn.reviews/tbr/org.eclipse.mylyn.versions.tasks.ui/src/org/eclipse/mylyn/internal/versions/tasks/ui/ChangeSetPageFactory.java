/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.versions.tasks.ui;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 *
 * @author Kilian Matt
 *
 */
public class ChangeSetPageFactory extends AbstractTaskEditorPageFactory {

	public ChangeSetPageFactory() {
	}

	@Override
	public boolean canCreatePageFor(TaskEditorInput input) {
		AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(input.getTask().getConnectorKind());
		return connector != null && connector.isUserManaged();
	}

	@Override
	public IFormPage createPage(TaskEditor parentEditor) {
		return new ChangeSetPage(parentEditor);
	}

	@Override
	public Image getPageImage() {
		return null;
	}

	@Override
	public String getPageText() {
		return "Changeset";
	}

}
