/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.internal.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class MultiSelectionAttributeEditor extends AbstractAttributeEditor {

	private String[] allValues;

	private List list;

	public MultiSelectionAttributeEditor(AbstractAttributeEditorManager manager, RepositoryTaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.MULTIPLE, ColumnSpan.SINGLE));
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		list = new List(parent, SWT.FLAT | SWT.MULTI | SWT.V_SCROLL);
		toolkit.adapt(list, true, true);
		list.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		list.setFont(TEXT_FONT);

		Map<String, String> labelByValue = getAttributeMapper().getOptions(getTaskAttribute());
		if (labelByValue != null) {
			allValues = labelByValue.keySet().toArray(new String[0]);
			for (String value : allValues) {
				list.add(labelByValue.get(value));
			}
		}

		select(getValues(), getValuesLabels());
		
		if (allValues != null) {
			list.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Assert.isNotNull(allValues);
					int[] indices = list.getSelectionIndices();
					String[] selectedValues = new String[indices.length];
					for (int i = 0; i < indices.length; i++) {
						int index = indices[i];
						Assert.isLegal(index >= 0 && index <= allValues.length - 1);
						selectedValues[i] = allValues[index];
					}
					setValues(selectedValues);
				}
			});
			list.showSelection();
		}

		decorate(list);
		setControl(list);
	}

	public String[] getValues() {
		return getAttributeMapper().getValues(getTaskAttribute());
	}

	public String[] getValuesLabels() {
		return getAttributeMapper().getValueLabels(getTaskAttribute());
	}

	private void select(String[] values, String[] labels) {
		if (values != null) {
			list.deselectAll();
			Set<String> selectedValues = new HashSet<String>(Arrays.asList(values));
			for (int i = 0; i < allValues.length; i++) {
				if (selectedValues.contains(allValues[i])) {
					list.select(i);
				}
			}
		} else {
			list.setItems(labels);
			list.setSelection(labels);
		}
	}

	public void setValues(String[] values) {
		getAttributeMapper().setValues(getTaskAttribute(), values);
		attributeChanged();
	}

}
