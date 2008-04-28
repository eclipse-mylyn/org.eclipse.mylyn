/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.monitor.core.DateUtil;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.DatePicker;
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

	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

	private DatePicker datePicker;

	public DateAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
	}

	@Override
	public void createControl(Composite composite, FormToolkit toolkit) {
		if (isReadOnly()) {
			Text text = new Text(composite, SWT.FLAT | SWT.READ_ONLY);
			text.setFont(TEXT_FONT);
			toolkit.adapt(text, true, true);
			text.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
			text.setText(DateUtil.getFormattedDate(getValue(), DATE_FORMAT));
			setControl(text);
		} else {
			Composite dateWithClearComposite = toolkit.createComposite(composite);
			GridLayout layout = new GridLayout(2, false);
			layout.marginWidth = 1;
			dateWithClearComposite.setLayout(layout);

			String value = "";
			Date date = getValue();
			if (date != null) {
				SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
				value = f.format(date);
			}

			datePicker = new DatePicker(dateWithClearComposite, /* SWT.NONE */SWT.BORDER, value);
			datePicker.setEnabled(!isReadOnly());
			datePicker.setFont(TEXT_FONT);
			datePicker.setDatePattern("yyyy-MM-dd");
			datePicker.addPickerSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Calendar cal = datePicker.getDate();
					if (cal != null) {
						// TODO goes dirty even if user presses cancel
						setValue(cal.getTime());
					} else {
						setValue(null);
						datePicker.setDate(null);
					}
				}
			});

			ImageHyperlink clearDeadlineDate = toolkit.createImageHyperlink(dateWithClearComposite, SWT.NONE);
			clearDeadlineDate.setImage(TasksUiImages.getImage(TasksUiImages.REMOVE));
			clearDeadlineDate.setToolTipText("Clear");
			clearDeadlineDate.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					setValue(null);
					datePicker.setDate(null);
				}

			});

			setControl(dateWithClearComposite);
		}
	}

	public Date getValue() {
		return getAttributeMapper().getDateValue(getTaskAttribute());
	}

	public void setValue(Date date) {
		getAttributeMapper().setDateValue(getTaskAttribute(), date);
		attributeChanged();
	}

	@Override
	public void decorate(Color color) {
		if (datePicker != null) {
			datePicker.setBackground(color);
		} else {
			super.decorate(color);
		}
	}

}
