/*******************************************************************************
 * Copyright (c) 2010, 2013 Eike Stepper and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Eike Stepper - initial API and implementation
 *     Tasktop Technologies - improvements
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.builds.core.IParameterDefinition;
import org.eclipse.mylyn.builds.internal.core.BooleanParameterDefinition;
import org.eclipse.mylyn.builds.internal.core.BuildParameterDefinition;
import org.eclipse.mylyn.builds.internal.core.BuildPlan;
import org.eclipse.mylyn.builds.internal.core.ChoiceParameterDefinition;
import org.eclipse.mylyn.builds.internal.core.FileParameterDefinition;
import org.eclipse.mylyn.builds.internal.core.PasswordParameterDefinition;
import org.eclipse.mylyn.builds.internal.core.StringParameterDefinition;
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

	private final Map<String, Control> controls = new HashMap<>();

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

		for (final IParameterDefinition definition : plan.getParameterDefinitions()) {
			String name = definition.getName();

			Label label = new Label(pane, SWT.NONE);
			label.setText(name);

			Control control = addParameter(pane, definition);
			if (control instanceof Text) {
				GridDataFactory.fillDefaults()
				.hint(convertVerticalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.applyTo(control);
			}
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

	private Control addParameter(Composite pane, IParameterDefinition definition) {
		if (definition instanceof ChoiceParameterDefinition def) {
			Combo control = new Combo(pane, SWT.SINGLE | SWT.BORDER);
			for (String option : def.getOptions()) {
				control.add(option);
			}
			if (def.getDefaultValue() != null) {
				int i = control.indexOf(def.getDefaultValue());
				if (i != -1) {
					control.select(i);
				}
			}
			return control;
		} else if (definition instanceof BooleanParameterDefinition def) {
			Button control = new Button(pane, SWT.CHECK | SWT.BORDER);
			control.setSelection(def.isDefaultValue());
			return control;
		} else if (definition instanceof StringParameterDefinition def) {
			Text control = new Text(pane, SWT.BORDER);
			control.setText(toValue(def.getDefaultValue()));
			return control;
		} else if (definition instanceof PasswordParameterDefinition def) {
			Text control = new Text(pane, SWT.BORDER);
			control.setEchoChar('*');
			control.setText(toValue(def.getDefaultValue()));
			return control;
		} else if (definition instanceof BuildParameterDefinition def) {
			Text control = new Text(pane, SWT.BORDER);
			control.setText(toValue(def.getBuildPlanId()));
			return control;
		} else if (definition instanceof FileParameterDefinition) {
			Text control = new Text(pane, SWT.BORDER);
			return control;
		}

		throw new IllegalArgumentException("Unexpected definition type: " + definition.getClass().getName());
	}

	private String toValue(String defaultValue) {
		return defaultValue != null ? defaultValue : ""; //$NON-NLS-1$
	}

	@Override
	protected void okPressed() {
		parameters = new HashMap<>();
		for (final IParameterDefinition definition : plan.getParameterDefinitions()) {
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
		if (control instanceof Combo combo) {
			return combo.getText();
		}

		if (control instanceof Button button) {
			if (button.getSelection()) {
				return "on";
			}

			return null;
		}

		if (control instanceof Text text) {
			return text.getText();
		}

		throw new IllegalArgumentException("Unexpected control type: " + control.getClass().getName());
	}

}
