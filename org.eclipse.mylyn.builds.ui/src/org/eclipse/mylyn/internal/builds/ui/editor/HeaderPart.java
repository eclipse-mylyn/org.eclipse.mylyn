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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
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
public class HeaderPart extends AbstractBuildEditorPart {

	private DataBindingContext dbc;

	@Override
	protected Control createContent(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout());

		Label label = createLabel(composite, toolkit, "Plan:");
		GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
		Text text = createTextReadOnly(composite, toolkit, "");
		bind(text, IBuildPlan.class, BuildPackage.Literals.BUILD_ELEMENT__NAME);

		label = createLabel(composite, toolkit, "Build:");
		GridDataFactory.defaultsFor(label).indent(12, 0).applyTo(label);
		text = createTextReadOnly(composite, toolkit, "");
		bind(text, IBuild.class, BuildPackage.Literals.BUILD__BUILD_NUMBER);

		((GridLayout) composite.getLayout()).numColumns = composite.getChildren().length;
		toolkit.paintBordersFor(composite);

		return composite;
	}

	@Override
	public Control createControl(Composite parent, FormToolkit toolkit) {
		return createContent(parent, toolkit);
	}

	@Override
	protected boolean shouldExpandOnCreate() {
		return false;
	}

}
