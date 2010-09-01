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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Markus Knittig
 * @author Steffen Pingel
 */
public class BuildDetailsPage extends BuildEditorPage {

	public static final String BUILD_EDITOR_PAGE_ID = "org.eclipse.mylyn.build.ui.editor.DetailsPage";

	private Form form;

	private List<AbstractBuildEditorPart> parts;

	private FormToolkit toolkit;

	public BuildDetailsPage(FormEditor editor, String title) {
		super(editor, BUILD_EDITOR_PAGE_ID, title);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		form = managedForm.getForm().getForm();
		toolkit = managedForm.getToolkit();

		Composite body = form.getBody();
		GridLayout layout = new GridLayout(2, true);
		layout.verticalSpacing = 0;
		body.setLayout(layout);

		for (AbstractBuildEditorPart part : parts) {
			part.initialize(this);
			getManagedForm().addPart(part);
			Control control = part.createControl(body, toolkit);
			part.setControl(control);
			int span = (part instanceof HeaderPart) ? 2 : 1;
			GridDataFactory.fillDefaults().grab(true, false).span(span, 1).applyTo(part.getControl());
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);

		parts = new ArrayList<AbstractBuildEditorPart>();
		parts.add(new HeaderPart());
		parts.add(new SummaryPart());
		parts.add(new TestResultPart());
	}

	@Override
	public void setFocus() {
		getManagedForm().getForm().getForm().getBody().setFocus();
	}

}
