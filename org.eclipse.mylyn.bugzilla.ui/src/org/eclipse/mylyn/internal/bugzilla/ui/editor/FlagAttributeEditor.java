/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Frank Becker
 */
public class FlagAttributeEditor extends AbstractAttributeEditor {

	private String[] values;

	private CCombo combo;

	private Text requesteeText;

	public FlagAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		if (taskAttribute.getAttribute("state") != null) { //$NON-NLS-1$
			setReadOnly(taskAttribute.getAttribute("state").getMetaData().isReadOnly()); //$NON-NLS-1$
		}
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 1;
		composite.setLayout(layout);
		if (isReadOnly()) {
			Text text = new Text(composite, SWT.FLAT | SWT.READ_ONLY);
			text.setFont(EditorUtil.TEXT_FONT);
			toolkit.adapt(text, false, false);
			text.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
			text.setText(getValueLabel());
		} else {
			combo = new CCombo(composite, SWT.FLAT | SWT.READ_ONLY);
			toolkit.adapt(combo, false, false);
			combo.setFont(EditorUtil.TEXT_FONT);
			combo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

			Map<String, String> labelByValue = getAttributeMapper().getAssoctiatedAttribute(getTaskAttribute())
					.getOptions();
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
							if (requesteeText != null) {
								requesteeText.setEnabled(values[index].equals("?")); //$NON-NLS-1$
							}
						}
					}
				});
			}
			TaskAttribute requestee = getTaskAttribute().getAttribute("requestee"); //$NON-NLS-1$
			if (requestee != null && !requestee.getMetaData().isReadOnly()) {
				requesteeText = toolkit.createText(composite, requestee.getValue());
				requesteeText.setEnabled("?".equals(getValueLabel())); //$NON-NLS-1$
				GridData requesteeData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
				requesteeData.widthHint = 78;
				requesteeText.setLayoutData(requesteeData);
				requesteeText.addFocusListener(new FocusListener() {

					public void focusGained(FocusEvent e) {
					}

					public void focusLost(FocusEvent e) {
						setRequestee(requesteeText.getText());
					}
				});
			}
		}
		toolkit.paintBordersFor(composite);
		setControl(composite);
	}

	public String getValue() {
//		return getAttributeMapper().getValue(getTaskAttribute());
		return getAttributeMapper().getValue(getAttributeMapper().getAssoctiatedAttribute(getTaskAttribute()));
	}

	public String getValueLabel() {
//		return getAttributeMapper().getValueLabel(getTaskAttribute());
		return getAttributeMapper().getValueLabel(getAttributeMapper().getAssoctiatedAttribute(getTaskAttribute()));
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

	public void setRequestee(String value) {
		TaskAttribute requestee = getTaskAttribute().getAttribute("requestee"); //$NON-NLS-1$
		if (requestee != null) {
			getAttributeMapper().setValue(getTaskAttribute().getAttribute("requestee"), value); //$NON-NLS-1$
			attributeChanged();
		}
	}

	public void setValue(String value) {
		getAttributeMapper().setValue(getAttributeMapper().getAssoctiatedAttribute(getTaskAttribute()), value);
		attributeChanged();
	}

	@Override
	public String getLabel() {
		String label = getAttributeMapper().getLabel(getAttributeMapper().getAssoctiatedAttribute(getTaskAttribute()));
		if (label != null) {
			label.replace("&", "&&"); //$NON-NLS-1$//$NON-NLS-2$
		} else {
			label = ""; //$NON-NLS-1$
		}

		TaskAttribute setter = getTaskAttribute().getAttribute("setter"); //$NON-NLS-1$
		if (setter != null) {
			String setterValue = setter.getValue();
			if (setterValue != null && !setterValue.equals("")) { //$NON-NLS-1$
				if (setterValue.indexOf("@") != 0) { //$NON-NLS-1$
					setterValue = setterValue.substring(0, setterValue.indexOf("@")); //$NON-NLS-1$
				}
				label = setterValue + ": " + label; //$NON-NLS-1$
			}
		}
		return label;
	}
}
