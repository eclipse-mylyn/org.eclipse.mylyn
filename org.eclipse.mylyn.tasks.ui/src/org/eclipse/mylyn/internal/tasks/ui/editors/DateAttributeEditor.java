/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.DatePicker;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * @author Steffen Pingel
 * @author Robert Elves
 */
public class DateAttributeEditor extends AbstractAttributeEditor {

	private DatePicker datePicker;

	private boolean showTime;

	public DateAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
	}

	@Override
	public void createControl(Composite composite, FormToolkit toolkit) {
		if (isReadOnly()) {
			Text text = new Text(composite, SWT.FLAT | SWT.READ_ONLY);
			text.setFont(EditorUtil.TEXT_FONT);
			toolkit.adapt(text, false, false);
			text.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
			text.setText(getTextValue());
			setControl(text);
		} else {
			Composite dateWithClearComposite = toolkit.createComposite(composite);
			GridLayout layout = new GridLayout(2, false);
			layout.marginHeight = 2;
			layout.marginWidth = 1;
			layout.verticalSpacing = 2;
			layout.horizontalSpacing = 2;
			dateWithClearComposite.setLayout(layout);
			datePicker = new DatePicker(dateWithClearComposite, SWT.FLAT, getTextValue(), false, 0);
			datePicker.setFont(EditorUtil.TEXT_FONT);
			datePicker.setDateFormat(EditorUtil.getDateFormat());
			if (getValue() != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(getValue());
				datePicker.setDate(cal);
			}
			datePicker.addPickerSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Calendar cal = datePicker.getDate();
					if (cal != null) {
						if (!showTime) {
							TaskActivityUtil.snapStartOfDay(cal);
						}
						setValue(cal.getTime());
					} else {
						setValue(null);
						datePicker.setDate(null);
					}
				}
			});

			GridDataFactory.fillDefaults().grab(true, false).applyTo(datePicker);
			datePicker.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			toolkit.adapt(datePicker, false, false);

			ImageHyperlink clearDeadlineDate = toolkit.createImageHyperlink(dateWithClearComposite, SWT.NONE);
			clearDeadlineDate.setImage(CommonImages.getImage(CommonImages.REMOVE));
			clearDeadlineDate.setToolTipText("Clear");
			clearDeadlineDate.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					setValue(null);
					datePicker.setDate(null);
				}

			});

			toolkit.paintBordersFor(dateWithClearComposite);
			setControl(dateWithClearComposite);
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
			} else {
				return EditorUtil.formatDate(date);
			}
		} else {
			return "";
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

}
