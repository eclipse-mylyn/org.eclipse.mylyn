/*******************************************************************************
 * Copyright (c) 2010 Eike Stepper and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eike Stepper - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.mylyn.internal.builds.core.BooleanParameterDefinition;
import org.eclipse.mylyn.internal.builds.core.BuildPlan;
import org.eclipse.mylyn.internal.builds.core.ChoiceParameterDefinition;
import org.eclipse.mylyn.internal.builds.core.ParameterDefinition;
import org.eclipse.mylyn.internal.builds.core.PasswordParameterDefinition;
import org.eclipse.mylyn.internal.builds.core.StringParameterDefinition;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 * @author Steffen Pingel
 */
public class ParametersDialog extends TitleAreaDialog {

	private final Map<String, Control> controls = new HashMap<String, Control>();

	private final BuildPlan plan;

	private Map<String, String> parameters;

	public ParametersDialog(Shell parentShell, BuildPlan plan) {
		super(parentShell);
		this.plan = plan;
		setHelpAvailable(false);
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Run Build");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		setTitle(NLS.bind("Build Plan {0}", plan.getLabel()));

		Composite pane = new Composite(composite, SWT.NONE);
		pane.setLayoutData(new GridData(GridData.FILL_BOTH));
		pane.setLayout(new GridLayout(2, false));

		for (final ParameterDefinition definition : plan.getParameterDefinitions()) {
			String name = definition.getName();

			Label label = new Label(pane, SWT.NONE);
			label.setText(name);

			Control control = addParameter(pane, definition);
			control.addFocusListener(new FocusAdapter() {
				private boolean firstTime = true;

				@Override
				public void focusGained(FocusEvent e) {
					if (firstTime) {
						setMessage("Provide build parameters.");
						firstTime = false;
					} else {
						setMessage(definition.getDescription());
					}
				}
			});

			controls.put(name, control);
		}

		return composite;
	}

	private Control addParameter(Composite pane, ParameterDefinition definition) {
		if (definition instanceof ChoiceParameterDefinition) {
			ChoiceParameterDefinition def = (ChoiceParameterDefinition) definition;
			Combo control = new Combo(pane, SWT.SINGLE | SWT.BORDER);
			for (String option : def.getOptions()) {
				control.add(option);
			}

			control.select(0);
			return control;
		}

		if (definition instanceof BooleanParameterDefinition) {
			BooleanParameterDefinition def = (BooleanParameterDefinition) definition;
			Button control = new Button(pane, SWT.CHECK | SWT.BORDER);
			control.setSelection(def.isDefaultValue());
			return control;
		}

		if (definition instanceof StringParameterDefinition) {
			StringParameterDefinition def = (StringParameterDefinition) definition;
			Text control = new Text(pane, SWT.BORDER);
			control.setText(def.getDefaultValue());
			return control;
		}

		if (definition instanceof PasswordParameterDefinition) {
			PasswordParameterDefinition def = (PasswordParameterDefinition) definition;
			Text control = new Text(pane, SWT.BORDER);
			control.setEchoChar('*');
			control.setText(def.getDefaultValue());
			return control;
		}

		throw new IllegalArgumentException("Unexpected definition type: " + definition.getClass().getName());
	}

	@Override
	protected void okPressed() {
		parameters = new HashMap<String, String>();
		for (final ParameterDefinition definition : plan.getParameterDefinitions()) {
			String name = definition.getName();
			Control control = controls.get(name);
			String parameter = getParameter(control);
			if (parameter != null) {
				parameters.put(name, parameter);
			}
		}

		super.okPressed();
	}

	private String getParameter(Control control) {
		if (control instanceof Combo) {
			Combo combo = (Combo) control;
			return combo.getText();
		}

		if (control instanceof Button) {
			Button button = (Button) control;
			if (button.getSelection()) {
				return "on";
			}

			return null;
		}

		if (control instanceof Text) {
			Text text = (Text) control;
			return text.getText();
		}

		throw new IllegalArgumentException("Unexpected control type: " + control.getClass().getName());
	}

}
