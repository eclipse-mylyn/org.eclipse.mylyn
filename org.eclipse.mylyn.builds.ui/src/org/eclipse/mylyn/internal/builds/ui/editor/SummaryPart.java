/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.internal.core.BuildPackage;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class SummaryPart extends AbstractBuildEditorPart {

	public SummaryPart() {
		setPartName("Summary");
	}

	@Override
	protected Control createContent(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		Label label;
		Text text;

		label = createLabel(composite, toolkit, "Name: ");
		GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
		text = createTextReadOnly(composite, toolkit, "");
		bind(text, IBuild.class, BuildPackage.Literals.BUILD_ELEMENT__NAME);

		label = createLabel(composite, toolkit, "Built completed on: ");
		GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
		text = createTextReadOnly(composite, toolkit, "");
		bind(text, IBuild.class, BuildPackage.Literals.BUILD__TIMESTAMP);

		label = createLabel(composite, toolkit, "Status: ");
		GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
		text = createTextReadOnly(composite, toolkit, "");
		System.err.println(getInput(IBuild.class).getStatus());
		bind(text, IBuild.class, BuildPackage.Literals.BUILD__STATUS);

		return composite;
	}

	@Override
	protected boolean shouldExpandOnCreate() {
		return true;
	}

}
