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

import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.internal.core.BuildPackage.Literals;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class TestResultPart extends AbstractBuildEditorPart {

	public TestResultPart() {
		setPartName("Test Result");
	}

	@Override
	protected Control createContent(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		if (getInput(IBuild.class).getTestResult() == null) {
			createLabel(composite, toolkit, "No test result generated.");
		} else {
			Label label = createLabel(composite, toolkit, "Failed:");
			GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
			Text text = createTextReadOnly(composite, toolkit, "");
			bind(text, IBuild.class, FeaturePath
					.fromList(Literals.BUILD__TEST_RESULT, Literals.TEST_RESULT__FAIL_COUNT));

			label = createLabel(composite, toolkit, "Passed:");
			GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
			text = createTextReadOnly(composite, toolkit, "");
			bind(text, IBuild.class, FeaturePath
					.fromList(Literals.BUILD__TEST_RESULT, Literals.TEST_RESULT__PASS_COUNT));
		}

		return composite;
	}

	@Override
	protected boolean shouldExpandOnCreate() {
		return true;
	}

}
