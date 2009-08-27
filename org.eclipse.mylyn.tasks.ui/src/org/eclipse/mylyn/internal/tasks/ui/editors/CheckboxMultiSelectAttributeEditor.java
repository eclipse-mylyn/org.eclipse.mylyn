/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.provisional.commons.ui.CheckBoxTreeDialog;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Shawn Minto
 */
public class CheckboxMultiSelectAttributeEditor extends AbstractAttributeEditor {

	private Text valueText;

	public CheckboxMultiSelectAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.MULTIPLE));
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 1;
		composite.setLayout(layout);

		valueText = toolkit.createText(composite, "", SWT.FLAT); //$NON-NLS-1$
		valueText.setFont(EditorUtil.TEXT_FONT);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		valueText.setLayoutData(gd);
		valueText.setEditable(false);
		Button changeValueButton = toolkit.createButton(composite, Messages.CheckboxMultiSelectAttributeEditor_Edit,
				SWT.FLAT);
		gd = new GridData();
		changeValueButton.setLayoutData(gd);
		changeValueButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {

				List<String> values = getValues();

				Map<String, String> validValues = getAttributeMapper().getOptions(getTaskAttribute());

				Shell shell = WorkbenchUtil.getShell();

				CheckBoxTreeDialog selectionDialog = new CheckBoxTreeDialog(shell, values, validValues, NLS.bind(
						Messages.CheckboxMultiSelectAttributeEditor_Select_X, getLabel()));
				int responseCode = selectionDialog.open();

				List<String> newValues = selectionDialog.getSelectedValues();
				if (responseCode == Window.OK && values != null) {
					setValues(newValues);
					attributeChanged();
					updateText();
				} else {
					return;
				}

			}

		});
		toolkit.adapt(valueText, false, false);
		updateText();
		setControl(composite);
	}

	private void updateText() {
		if (valueText != null && !valueText.isDisposed()) {
			StringBuilder valueString = new StringBuilder();
			List<String> values = getValuesLabels();
			Collections.sort(values);
			for (int i = 0; i < values.size(); i++) {
				valueString.append(values.get(i));
				if (i != values.size() - 1) {
					valueString.append(", "); //$NON-NLS-1$
				}
			}
			valueText.setText(valueString.toString());
		}
	}

	public List<String> getValues() {
		return getAttributeMapper().getValues(getTaskAttribute());
	}

	public List<String> getValuesLabels() {
		return getAttributeMapper().getValueLabels(getTaskAttribute());
	}

	public void setValues(List<String> newValues) {
		getAttributeMapper().setValues(getTaskAttribute(), newValues);
		attributeChanged();
	}

	@Override
	protected void decorateIncoming(Color color) {
		if (valueText != null && !valueText.isDisposed()) {
			valueText.setBackground(color);
		}
	}

}
