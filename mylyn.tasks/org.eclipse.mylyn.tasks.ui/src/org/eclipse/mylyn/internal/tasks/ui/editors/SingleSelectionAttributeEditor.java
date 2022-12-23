/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class SingleSelectionAttributeEditor extends AbstractAttributeEditor {

	private CCombo combo;

	private boolean ignoreNotification;

	private Text text;

	private String[] values;

	public SingleSelectionAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
	}

	@Override
	public void createControl(final Composite parent, FormToolkit toolkit) {
		if (isReadOnly()) {
			text = new Text(parent, SWT.FLAT | SWT.READ_ONLY);
			text.setFont(EditorUtil.TEXT_FONT);
			toolkit.adapt(text, false, false);
			text.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
			text.setToolTipText(getDescription());
			setControl(text);
		} else {
			combo = new CCombo(parent, SWT.FLAT | SWT.READ_ONLY);
			combo.setVisibleItemCount(10);
			toolkit.adapt(combo, false, false);
			combo.setFont(EditorUtil.TEXT_FONT);
			combo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
			combo.setToolTipText(getDescription());
			combo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					if (!ignoreNotification) {
						int index = combo.getSelectionIndex();
						if (index > -1) {
							Assert.isNotNull(values);
							Assert.isLegal(index >= 0 && index <= values.length - 1);
							setValue(values[index]);
						}
					}
				}
			});
			EditorUtil.addScrollListener(combo);
			setControl(combo);
		}
		refresh();
	}

	public String getValue() {
		return getAttributeMapper().getValue(getTaskAttribute());
	}

	public String getValueLabel() {
		return getAttributeMapper().getValueLabel(getTaskAttribute());
	}

	@Override
	public void refresh() {
		try {
			ignoreNotification = true;
			if (text != null && !text.isDisposed()) {
				String label = getValueLabel();
				if ("".equals(label)) { //$NON-NLS-1$
					// if set to the empty string the label will use 64px on GTK 
					text.setText(" "); //$NON-NLS-1$
				} else {
					text.setText(label);
				}
			} else if (combo != null && !combo.isDisposed()) {
				combo.removeAll();
				Map<String, String> labelByValue = getAttributeMapper().getOptions(getTaskAttribute());
				if (labelByValue != null) {
					values = labelByValue.keySet().toArray(new String[0]);
					for (String value : values) {
						combo.add(labelByValue.get(value));
					}
				}
				select(getValue(), getValueLabel());
				combo.redraw();
			}
		} finally {
			ignoreNotification = false;
		}
	}

	@Override
	public boolean shouldAutoRefresh() {
		return true;
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

	void selectDefaultValue() {
		if (combo.getSelectionIndex() == -1 && values.length > 0) {
			combo.select(0);
			setValue(values[0]);
		}
	}

	public void setValue(String value) {
		String oldValue = getAttributeMapper().getValue(getTaskAttribute());
		if (!oldValue.equals(value)) {
			getAttributeMapper().setValue(getTaskAttribute(), value);
			attributeChanged();
		}
	}
}
