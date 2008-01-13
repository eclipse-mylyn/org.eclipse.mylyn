/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class SingleSelectionAttributeEditor extends AbstractAttributeEditor {

	private String[] values;

	private CCombo combo;

	public SingleSelectionAttributeEditor(AbstractAttributeEditorManager manager, RepositoryTaskAttribute taskAttribute) {
		super(manager, taskAttribute);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		combo = new CCombo(parent, SWT.FLAT | SWT.READ_ONLY);
		toolkit.adapt(combo, true, true);
		combo.setFont(TEXT_FONT);
		combo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

		Map<String, String> labelByValue = getAttributeMapper().getOptions(getTaskAttribute());
		if (labelByValue != null) {
			values = labelByValue.keySet().toArray(new String[0]);
			for (String value : values) {
				combo.add(labelByValue.get(value));
			}
		}

		select(getValue(), getValueLabel());

		if (values != null) {
			combo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					int index = combo.getSelectionIndex();
					if (index > -1) {
						Assert.isNotNull(values);
						Assert.isLegal(index >= 0 && index <= values.length - 1);
						setValue(values[index]);
					}
				}
			});
		}

		decorate(combo);
		setControl(combo);
	}

	public String getValue() {
		return getAttributeMapper().getValue(getTaskAttribute());
	}

	public String getValueLabel() {
		return getAttributeMapper().getValueLabel(getTaskAttribute());
	}

	private void select(String value, String label) {
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				if (values[i].equals(value)) {
					combo.select(i);
					break;
				}
			}
		} else {
			combo.setText(label);
		}
	}

	public void setValue(String value) {
		getAttributeMapper().setValue(getTaskAttribute(), value);
		attributeChanged();
	}

}
