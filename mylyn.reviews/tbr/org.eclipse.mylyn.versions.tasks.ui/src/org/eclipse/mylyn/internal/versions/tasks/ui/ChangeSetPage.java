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

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskFormPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Kilian Matt
 */
public class ChangeSetPage extends TaskFormPage {

	public ChangeSetPage(TaskEditor editor) {
		super(editor, ChangeSetPage.class.getName(), "title");
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		Composite body = managedForm.getForm().getBody();
		body.setLayout(new TableWrapLayout());
		createPart(body, managedForm);
	}

	private void createPart(Composite parent, IManagedForm managedForm) {
		ChangesetPart part = new ChangesetPart();
		managedForm.addPart(part);
		part.initialize(this);
		Control control = part.createControl(parent, managedForm.getToolkit());
		control.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
	}

	public ITask getTask() {
		return ((TaskEditorInput) getEditorInput()).getTask();
	}
}
