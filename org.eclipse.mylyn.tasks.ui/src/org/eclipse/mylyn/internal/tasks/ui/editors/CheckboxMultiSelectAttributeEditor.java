/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Pawel Niewiadomski - fix for bug 287832
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;

/**
 * @author Shawn Minto
 */
public class CheckboxMultiSelectAttributeEditor extends AbstractAttributeEditor {

	private Text valueText;

	private Composite parent;

	public CheckboxMultiSelectAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.MULTIPLE));
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		this.parent = parent;

		Composite composite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 1;
		composite.setLayout(layout);

		valueText = toolkit.createText(composite, "", SWT.FLAT | SWT.WRAP); //$NON-NLS-1$
		valueText.setFont(EditorUtil.TEXT_FONT);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		valueText.setLayoutData(gd);
		valueText.setEditable(false);
		Button changeValueButton = toolkit.createButton(composite, Messages.CheckboxMultiSelectAttributeEditor_Edit,
				SWT.FLAT);
		gd = new GridData();
		changeValueButton.setLayoutData(gd);
		changeValueButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<String> values = getValues();
				Map<String, String> validValues = getAttributeMapper().getOptions(getTaskAttribute());

				CheckBoxTreeDialog selectionDialog = new CheckBoxTreeDialog(WorkbenchUtil.getShell(), values,
						validValues, NLS.bind(Messages.CheckboxMultiSelectAttributeEditor_Select_X, getLabel()));
				int responseCode = selectionDialog.open();

				if (responseCode == Window.OK) {
					Set<String> newValues = selectionDialog.getSelectedValues();
					if (!new HashSet<String>(values).equals(newValues)) {
						setValues(new ArrayList<String>(newValues));
						attributeChanged();
						updateText();
					}
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
			if (valueText != null && parent != null && parent.getParent() != null
					&& parent.getParent().getParent() != null) {
				Point size = valueText.getSize();
				// subtract 1 from size for border
				Point newSize = valueText.computeSize(size.x - 1, SWT.DEFAULT);
				if (!newSize.equals(valueText.getSize())) {
					reflow();
				}
			}
		}
	}

	/**
	 * Update scroll bars of the enclosing form.
	 * 
	 * @see Section#reflow()
	 */
	private void reflow() {
		Composite c = parent;
		while (c != null) {
			c.setRedraw(false);
			c = c.getParent();
			if (c instanceof SharedScrolledComposite || c instanceof Shell) {
				break;
			}
		}
		c = parent;
		while (c != null) {
			c.layout(true);
			c = c.getParent();
			if (c instanceof SharedScrolledComposite) {
				((SharedScrolledComposite) c).reflow(true);
				break;
			}
		}
		c = parent;
		while (c != null) {
			c.setRedraw(true);
			c = c.getParent();
			if (c instanceof SharedScrolledComposite || c instanceof Shell) {
				break;
			}
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
