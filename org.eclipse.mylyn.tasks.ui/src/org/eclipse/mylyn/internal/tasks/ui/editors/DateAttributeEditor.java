/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.commons.workbench.forms.DatePicker;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 * @author Robert Elves
 */
public class DateAttributeEditor extends AbstractAttributeEditor {

	private DatePicker datePicker;

	private boolean showTime;

	private boolean showDateRelative;

	private Text text;

	public DateAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.SINGLE));
		setShowDateRelative(false);
	}

	@Override
	public void createControl(Composite composite, FormToolkit toolkit) {
		if (isReadOnly()) {
			text = new Text(composite, SWT.FLAT | SWT.READ_ONLY);
			text.setFont(EditorUtil.TEXT_FONT);
			toolkit.adapt(text, false, false);
			text.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
			text.setText(getTextValue());
			text.setToolTipText(getDescription());
			setControl(text);
		} else {
			datePicker = new DatePicker(composite, SWT.FLAT, getTextValue(), showTime, 0);
			datePicker.setFont(EditorUtil.TEXT_FONT);
			datePicker.setToolTipText(getDescription());
			if (!showTime) {
				datePicker.setDateFormat(EditorUtil.getDateFormat());
			} else {
				datePicker.setDateFormat(EditorUtil.getDateTimeFormat());
			}
			updateDatePicker();
			datePicker.addPickerSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Calendar cal = datePicker.getDate();
					if (cal != null) {
						if (!showTime) {
							TaskActivityUtil.snapStartOfDay(cal);
						}
						Date value = cal.getTime();
						if (!value.equals(getValue())) {
							setValue(value);
						}
					} else {
						if (getValue() != null) {
							setValue(null);
						}
						datePicker.setDate(null);
					}
				}

			});

			GridDataFactory.fillDefaults().hint(120, SWT.DEFAULT).grab(false, false).applyTo(datePicker);
			datePicker.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
			toolkit.adapt(datePicker, false, false);

			setControl(datePicker);
		}
	}

	private void updateDatePicker() {
		if (getValue() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(getValue());
			datePicker.setDate(cal);
		}
	}

	@Override
	protected void decorateIncoming(Color color) {
		if (datePicker != null) {
			datePicker.setBackground(color);
		}
	}

	public boolean getShowTime() {
		return showTime;
	}

	private String getTextValue() {
		Date date = getValue();
		if (date != null) {
			if (getShowTime()) {
				return EditorUtil.formatDateTime(date);
			} else if (getShowDateRelative()) {
				return DateUtil.getRelative(date.getTime());
			} else {
				return EditorUtil.formatDate(date);
			}
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	public Date getValue() {
		return getAttributeMapper().getDateValue(getTaskAttribute());
	}

	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
	}

	public void setValue(Date date) {
		getAttributeMapper().setDateValue(getTaskAttribute(), date);
		attributeChanged();
	}

	@Override
	public void refresh() {
		if (text != null && !text.isDisposed()) {
			text.setText(getTextValue());
		}
		if (datePicker != null && !datePicker.isDisposed()) {
			updateDatePicker();
		}
	}

	@Override
	public boolean shouldAutoRefresh() {
		return true;
	}

	public boolean getShowDateRelative() {
		return showDateRelative;
	}

	public void setShowDateRelative(boolean showDateRelative) {
		this.showDateRelative = showDateRelative;
	}

}
