/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskFormPage;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author Markus Knittig
 * @author Steffen Pingel
 */
public class BuildEditorPage extends TaskFormPage {

	public static final String BUILD_EDITOR_PAGE_ID = "org.eclipse.mylyn.build.ui.editor.DetailsPage";

	public BuildEditorPage(FormEditor editor, String title) {
		super(editor, BUILD_EDITOR_PAGE_ID, title);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		GridLayoutFactory.swtDefaults().applyTo(managedForm.getForm().getBody());

		managedForm.getToolkit().createText(managedForm.getForm().getBody(), "Not implemented.");
	}

	@Override
	public void setFocus() {
		getManagedForm().getForm().getForm().getBody().setFocus();
	}

}
